-- V3: Add CANCELLED status to execution session status constraint
ALTER TABLE execution_sessions DROP CONSTRAINT chk_sessions_status;
ALTER TABLE execution_sessions ADD CONSTRAINT chk_sessions_status CHECK (status IN ('PENDING', 'RUNNING', 'SUCCESS', 'FAILED', 'CANCELLED'));
