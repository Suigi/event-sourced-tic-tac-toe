terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
    }
  }
  backend "gcs" {
    bucket = "es-tic-tac-toe-opentofu-state"
    prefix = "tofu/state/wif"
  }
}

provider "google" {
  project = var.google_project_id
}

# Create the Workload Identity Pool
resource "google_iam_workload_identity_pool" "github_pool" {
  project                   = var.google_project_id
  workload_identity_pool_id = "github"
  display_name              = "GitHub Actions Identity Pool"
}

# Create the OIDC Provider with attribute condition
resource "google_iam_workload_identity_pool_provider" "github_provider" {
  project                            = var.google_project_id
  workload_identity_pool_id          = google_iam_workload_identity_pool.github_pool.workload_identity_pool_id
  workload_identity_pool_provider_id = "github-provider"
  display_name                       = "GitHub OIDC Provider"
  description                        = "Accepts tokens from GitHub Actions"

  oidc {
    issuer_uri = "https://token.actions.githubusercontent.com"
  }

  attribute_mapping = {
    "google.subject"       = "assertion.sub"
    "attribute.actor"      = "assertion.actor"
    "attribute.repository" = "assertion.repository"
    "attribute.ref"        = "assertion.ref"
  }

  attribute_condition = "attribute.repository=='${var.github_owner}/${var.github_repo}' && attribute.ref=='refs/heads/main' && attribute.actor=='${var.github_actor}'"
}

# Create the Service Account for pushing to Artifact Registry
resource "google_service_account" "github_deployer" {
  account_id   = "github-deployer"
  display_name = "GitHub Deployer"
  project      = var.google_project_id
}

# Allow Workload Identity Pool to impersonate the service account
resource "google_service_account_iam_binding" "wif_impersonation" {
  service_account_id = google_service_account.github_deployer.name
  role               = "roles/iam.workloadIdentityUser"

  members = [
    "principalSet://iam.googleapis.com/projects/${var.google_project_number}/locations/global/workloadIdentityPools/${google_iam_workload_identity_pool.github_pool.workload_identity_pool_id}/attribute.subject/*"
  ]
}

# Optional: Grant Artifact Registry write access to the service account
resource "google_project_iam_member" "artifact_pusher" {
  project = var.google_project_id
  role    = "roles/artifactregistry.writer"
  member  = "serviceAccount:${google_service_account.github_deployer.email}"
}
