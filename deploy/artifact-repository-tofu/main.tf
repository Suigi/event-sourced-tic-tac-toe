terraform {
  required_providers {
    google = {
      source  = "hashicorp/google-beta"
      version = "~> 6.5.0"
    }
  }

  required_version = ">= 1.0"
}

provider "google" {
  region  = var.google_region
  project = var.google_project_id
  zone    = var.google_zone
}

resource "google_artifact_registry_repository" "repository" {
  location      = var.google_region
  repository_id = "tic-tac-toe"
  description   = "tic-tac-toe docker repository"
  format        = "DOCKER"

  docker_config {
    immutable_tags = false
  }

}

data "google_iam_workload_identity_pool" "github" {
  workload_identity_pool_id = "github"
}

resource "google_artifact_registry_repository_iam_binding" "binding" {
  project    = google_artifact_registry_repository.repository.project
  location   = google_artifact_registry_repository.repository.location
  repository = google_artifact_registry_repository.repository.name
  role       = "roles/artifactregistry.writer"
  members = [
    local.oidc_principal,
  ]
}

locals {
  oidc_principal = "principalSet://iam.googleapis.com/${data.google_iam_workload_identity_pool.github.name}/attribute.repository/Suigi/tic-tac-toe"
}

