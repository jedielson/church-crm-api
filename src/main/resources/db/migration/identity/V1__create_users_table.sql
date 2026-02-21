-- Create identity schema
CREATE SCHEMA IF NOT EXISTS identity;

-- Create users table
CREATE TABLE IF NOT EXISTS identity.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    church_id UUID NOT NULL,
    username VARCHAR(50) NOT NULL,
    fullname VARCHAR(200) NOT NULL,
    email VARCHAR(254) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_users_church_id ON identity.users(church_id);
CREATE INDEX IF NOT EXISTS idx_users_username ON identity.users(username, church_id);
CREATE INDEX IF NOT EXISTS idx_users_email ON identity.users(email, church_id);
CREATE INDEX IF NOT EXISTS idx_users_id ON identity.users(id);
CREATE INDEX IF NOT EXISTS idx_users_church_id ON identity.users(church_id);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON identity.users(created_at);
CREATE INDEX IF NOT EXISTS idx_users_updated_at ON identity.users(updated_at);

-- Business rule: Username and email must be unique within each church
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_unique_username_per_church ON identity.users(username, church_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_unique_email_per_church ON identity.users(email, church_id);