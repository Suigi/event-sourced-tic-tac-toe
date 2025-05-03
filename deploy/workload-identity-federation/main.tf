terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 6.0"
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
