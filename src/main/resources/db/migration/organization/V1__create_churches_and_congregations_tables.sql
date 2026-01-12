-- Create organization schema
CREATE SCHEMA IF NOT EXISTS organization;

-- Create churches table
CREATE TABLE organization.churches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    host_name VARCHAR(200) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create congregations table
CREATE TABLE organization.congregations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    church_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    is_main BOOLEAN NOT NULL DEFAULT false,
    line1 VARCHAR(200),
    line2 VARCHAR(200),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT fk_congregations_church FOREIGN KEY (church_id) REFERENCES organization.churches(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_churches_host_name ON organization.churches(host_name);
CREATE INDEX idx_churches_name ON organization.churches(name);
CREATE INDEX idx_congregations_church_id ON organization.congregations(church_id);
CREATE INDEX idx_congregations_is_main ON organization.congregations(is_main);
CREATE INDEX idx_churches_created_at ON organization.churches(created_at);
CREATE INDEX idx_congregations_created_at ON organization.congregations(created_at);
