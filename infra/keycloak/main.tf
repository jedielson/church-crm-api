terraform {
  required_providers {
    keycloak = {
      source  = "mrparkers/keycloak"
      version = "4.3.0"
    }
  }
}

provider "keycloak" {
  client_id                = "admin-cli"
  url                      = var.keycloak_url
  username                 = var.keycloak_admin_user
  password                 = var.keycloak_admin_password
  tls_insecure_skip_verify = true
}

# Corresponds to BuildChurchCmsRealm()
resource "keycloak_realm" "main" {
  realm        = "church-cms"
  ssl_required = "none"
  enabled      = true
}

# Corresponds to BuildChurchRealmUserProfile()
resource "keycloak_realm_user_profile" "user_profile" {
  realm_id = keycloak_realm.main.id

  attribute {
    name         = "username"
    display_name = "$${username}"
    permissions {
      edit = ["admin", "user"]
      view = ["admin", "user"]
    }
    validator {
      name = "length"
      config = {
        "min" = "3"
        "max" = "255"
      }
    }
  }

  attribute {
    name         = "email"
    display_name = "$${email}"
    permissions {
      edit = ["admin", "user"]
      view = ["admin", "user"]
    }
    required_for_roles = ["user"]
    validator {
      name = "email"
    }
    validator {
      name = "length"
      config = {
        "max" = "255"
      }
    }
  }

  attribute {
    name         = "firstName"
    display_name = "$${firstName}"
    permissions {
      edit = ["admin", "user"]
      view = ["admin", "user"]
    }
    required_for_roles = ["user"]
    validator {
      name = "length"
      config = {
        "max" = "255"
      }
    }
    validator {
      name = "person-name-prohibited-characters"
    }
  }

  attribute {
    name         = "lastName"
    display_name = "$${lastName}"
    permissions {
      edit = ["admin", "user"]
      view = ["admin", "user"]
    }
    required_for_roles = ["user"]
    validator {
      name = "length"
      config = {
        "max" = "255"
      }
    }
    validator {
      name = "person-name-prohibited-characters"
    }
  }

  attribute {
    name         = "organization-id"
    display_name = "$${organization-id}"
    permissions {
      edit = ["admin", "user"]
      view = ["admin", "user"]
    }
    # required_for_roles = ["user"]
  }

  group {
    name                = "user-metadata"
    display_header      = "User metadata"
    display_description = "Attributes, which refer to user metadata"
  }
  depends_on = [keycloak_realm.main]
}

# Corresponds to CreateApiClient()
resource "keycloak_openid_client" "api" {
  realm_id                     = keycloak_realm.main.id
  client_id                    = "church-cms-api"
  name                         = "Church Cms Api"
  enabled                      = true
  access_type                  = "CONFIDENTIAL"
  client_secret                = var.api_client_secret
  service_accounts_enabled     = true
  direct_access_grants_enabled = true

}

resource "keycloak_openid_audience_protocol_mapper" "audience_mapper" {
  realm_id                 = keycloak_realm.main.id
  client_id                = keycloak_openid_client.api.id
  name                     = "custom-audience-mapper"
  included_client_audience = keycloak_openid_client.api.client_id
  add_to_access_token      = true
  add_to_id_token          = false
}

# Corresponds to CreateUiClient()
resource "keycloak_openid_client" "ui" {
  realm_id                     = keycloak_realm.main.id
  client_id                    = "church-cms-ui"
  name                         = "Church Cms Ui"
  enabled                      = true
  access_type                  = "CONFIDENTIAL"
  client_secret                = var.ui_client_secret
  standard_flow_enabled        = true
  direct_access_grants_enabled = true
  service_accounts_enabled     = true
  root_url                     = "http://localhost:3000/"
  valid_redirect_uris = [
    "http://localhost:3000/*"
  ]
}

resource "keycloak_openid_user_attribute_protocol_mapper" "organization_mapper" {
  realm_id            = keycloak_realm.main.id
  client_id           = keycloak_openid_client.ui.id
  name                = "organization-mapper"
  user_attribute      = "organization-id"
  claim_name          = "organization_id"
  add_to_access_token = true
  add_to_id_token     = true
  add_to_userinfo     = true
}

# Corresponds to SetChurchApiClientAsUserManagement()
data "keycloak_openid_client" "realm_management" {
  realm_id  = keycloak_realm.main.id
  client_id = "realm-management"
}

data "keycloak_role" "manage_users" {
  realm_id  = keycloak_realm.main.id
  client_id = data.keycloak_openid_client.realm_management.id
  name      = "manage-users"
}

resource "keycloak_openid_client_service_account_role" "admin_permission_create_user" {
  realm_id                = keycloak_realm.main.id
  service_account_user_id = keycloak_openid_client.api.service_account_user_id
  client_id               = data.keycloak_openid_client.realm_management.id
  role                    = data.keycloak_role.manage_users.name
}

# Corresponds to CreateDefaultUser()
resource "keycloak_user" "default" {
  realm_id       = keycloak_realm.main.id
  username       = "a@b.com"
  email          = "a@b.com"
  first_name     = "John"
  last_name      = "Doe"
  enabled        = true
  email_verified = true

  initial_password {
    value     = var.default_user_password
    temporary = false
  }

  attributes = {
    organization-id = var.default_user_organization_id
  }
}

# Client Roles
# ADMIN role in church-cms-api client
resource "keycloak_role" "api_admin" {
  realm_id    = keycloak_realm.main.id
  client_id   = keycloak_openid_client.api.id
  name        = "ADMIN"
  description = "Administrator role for API access"
}

# USER role in church-cms-ui client
resource "keycloak_role" "ui_user" {
  realm_id    = keycloak_realm.main.id
  client_id   = keycloak_openid_client.ui.id
  name        = "USER"
  description = "Standard user role for UI access"
}

# Groups
# USERS group with USER role from church-cms-ui
resource "keycloak_group" "users" {
  realm_id = keycloak_realm.main.id
  name     = "USERS"
}

resource "keycloak_group_roles" "users_roles" {
  realm_id = keycloak_realm.main.id
  group_id = keycloak_group.users.id

  role_ids = [
    keycloak_role.ui_user.id
  ]
}

# ADMINS group with both ADMIN (from api) and USER (from ui) roles
resource "keycloak_group" "admins" {
  realm_id = keycloak_realm.main.id
  name     = "ADMINS"
}

resource "keycloak_group_roles" "admins_roles" {
  realm_id = keycloak_realm.main.id
  group_id = keycloak_group.admins.id

  role_ids = [
    keycloak_role.api_admin.id,
    keycloak_role.ui_user.id
  ]
}

# Add user a@b.com to ADMINS group
resource "keycloak_user_groups" "default_user_groups" {
  realm_id = keycloak_realm.main.id
  user_id  = keycloak_user.default.id

  group_ids = [
    keycloak_group.admins.id
  ]
}
