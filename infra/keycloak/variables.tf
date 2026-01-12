variable "keycloak_url" {
  type        = string
  description = "The URL of the Keycloak server."
}

variable "keycloak_admin_user" {
  type        = string
  description = "The admin username for Keycloak."
}

variable "keycloak_admin_password" {
  type        = string
  description = "The admin password for Keycloak."
  sensitive   = true
}

variable "api_client_secret" {
  type        = string
  description = "Client secret for the church-cms-api."
  default     = "0D707122-4A11-4889-869B-4752CAEDECCD"
  sensitive   = true
}

variable "ui_client_secret" {
  type        = string
  description = "Client secret for the church-cms-ui."
  default     = "27074815-E8EF-469A-A183-DE11B802F90B"
  sensitive   = true
}

variable "default_user_password" {
  type        = string
  description = "Password for the default user."
  default     = "12345"
  sensitive   = true
}

variable "default_user_organization_id" {
  type        = string
  description = "Organization ID for the default user."
  default     = "9987eb71-8050-4e44-8f4e-179564bfccca"
}
