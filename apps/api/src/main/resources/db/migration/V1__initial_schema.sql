-- Enable UUID extension in PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 2. Projects Table
CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    language VARCHAR(50) NOT NULL,
    source_code TEXT NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT chk_projects_language CHECK (language IN ('JAVA'))
);

-- 3. Execution Sessions Table
CREATE TABLE execution_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    error_message TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT chk_sessions_status CHECK (status IN ('PENDING', 'RUNNING', 'SUCCESS', 'FAILED'))
);

-- 4. Execution Steps Table
CREATE TABLE execution_steps (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id UUID NOT NULL REFERENCES execution_sessions(id) ON DELETE CASCADE,
    step_number INT NOT NULL,
    line_number INT NOT NULL,
    method_name VARCHAR(255) NOT NULL,
    class_name VARCHAR(255) NOT NULL,
    stdout TEXT,
    stderr TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 5. Variable Snapshots Table
CREATE TABLE variable_snapshots (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    step_id UUID NOT NULL REFERENCES execution_steps(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    value TEXT NOT NULL,
    type VARCHAR(255) NOT NULL,
    scope VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT chk_snapshots_scope CHECK (scope IN ('LOCAL', 'HEAP'))
);

-- Indexes for foreign keys to optimize query joins
CREATE INDEX idx_projects_user_id ON projects(user_id);
CREATE INDEX idx_execution_sessions_project_id ON execution_sessions(project_id);
CREATE INDEX idx_execution_steps_session_id ON execution_steps(session_id);
CREATE INDEX idx_variable_snapshots_step_id ON variable_snapshots(step_id);
