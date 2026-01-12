output "realm_id" {
  value       = keycloak_realm.main.id
  description = "The ID of the created realm."
}

output "manage_users_role_id" {
  value       = data.keycloak_role.manage_users.id
  description = "The ID of the 'manage-users' role."
}
