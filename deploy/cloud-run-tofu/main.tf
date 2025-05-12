terraform {
  required_providers {
    google = {
      source  = "hashicorp/google-beta"
      version = "~> 6.5.0"
    }
  }

  required_version = ">= 1.0"

  backend "gcs" {
    bucket = "es-tic-tac-toe-opentofu-state"
    prefix = "tofu/state/cloud-run"
  }
}

provider "google" {
  region  = var.google_region
  project = var.google_project_id
  zone    = var.google_zone
}

data "google_artifact_registry_repository" "repository" {
  location      = var.google_region
  repository_id = "tic-tac-toe"
}

resource "google_service_account" "tic-tac-toe" {
  account_id = "tic-tac-toe"
}

resource "google_service_account_iam_binding" "admin-account-iam" {
  service_account_id = google_service_account.tic-tac-toe.name
  role               = "roles/iam.serviceAccountUser"

  members = [
    local.oidc_principal,
  ]
}

resource "google_project_iam_member" "tic-tac-toe" {
  project = var.google_project_id
  role    = "roles/secretmanager.secretAccessor"
  member  = "serviceAccount:${google_service_account.tic-tac-toe.email}"
}

resource "google_cloud_run_v2_service" "default" {
  name     = "tic-tac-toe-service"
  location = var.google_region
  ingress  = "INGRESS_TRAFFIC_ALL"

  deletion_protection = false

  template {
    service_account = google_service_account.tic-tac-toe.email
    containers {
      image = "us-docker.pkg.dev/cloudrun/container/hello"

      startup_probe {
        initial_delay_seconds = 1
        timeout_seconds       = 1
        period_seconds        = 5
        failure_threshold     = 3
        tcp_socket {
          port = 8080
        }
      }
      liveness_probe {
        http_get {
          path = "/actuator/health"
        }
      }


      env {
        name = "POSTGRES_HOST"
        value_source {
          secret_key_ref {
            secret  = "supabase-hostname"
            version = 1
          }
        }
      }
      env {
        name = "POSTGRES_USERNAME"
        value_source {
          secret_key_ref {
            secret  = "supabase-username"
            version = 1
          }
        }
      }
      env {
        name = "POSTGRES_PASSWORD"
        value_source {
          secret_key_ref {
            secret  = "supabase-password"
            version = 1
          }
        }
      }
      env {
        name  = "POSTGRES_PORT"
        value = "5432"
      }
      env {
        name  = "POSTGRES_DB"
        value = "postgres"
      }
      env {
        name  = "SPRING.PROFILES.ACTIVE"
        value = "supabase"
      }
      env {
        name  = "JAVA_OPTS"
        value = "-XX:ReservedCodeCacheSize=32M -XX:MaxDirectMemorySize=32M"
      }

      resources {
        cpu_idle = true
        limits = {
          cpu    = "1"
          memory = "512Mi"
        }
      }
    }
  }

  depends_on = [
    google_project_iam_member.tic-tac-toe
  ]

  lifecycle {
    ignore_changes = [
      template[0].containers[0].image,
      template[0].labels,
      client,
      client_version
    ]
  }
}

/*
  Make service public available
*/
data "google_iam_policy" "no_auth" {
  binding {
    role = "roles/run.invoker"
    members = [
      "allUsers"
    ]
  }
  binding {
    role = "roles/run.developer"
    members = [
      local.oidc_principal
    ]
  }
}

resource "google_cloud_run_service_iam_policy" "no_auth" {
  location = google_cloud_run_v2_service.default.location
  project  = google_cloud_run_v2_service.default.project
  service  = google_cloud_run_v2_service.default.name

  policy_data = data.google_iam_policy.no_auth.policy_data
}

resource "google_cloud_run_domain_mapping" "suigi" {
  location = var.google_region
  name     = "tic-tac-toe.suigi.dev"

  metadata {
    namespace = var.google_project_id
  }

  spec {
    route_name = google_cloud_run_v2_service.default.name
  }
}

data "google_iam_workload_identity_pool" "github" {
  workload_identity_pool_id = "github"
}

locals {
  oidc_principal = "principalSet://iam.googleapis.com/${data.google_iam_workload_identity_pool.github.name}/attribute.repository/Suigi/event-sourced-tic-tac-toe"
}

output "api_url" {
  value = google_cloud_run_v2_service.default.uri
}
