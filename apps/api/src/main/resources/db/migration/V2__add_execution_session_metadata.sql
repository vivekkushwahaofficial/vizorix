-- V2: Add execution session metadata fields for analytics and performance tracking
ALTER TABLE execution_sessions
    ADD COLUMN compile_time_ms    BIGINT,
    ADD COLUMN execution_time_ms  BIGINT,
    ADD COLUMN total_steps        INT,
    ADD COLUMN total_variables    INT,
    ADD COLUMN total_console_lines INT,
    ADD COLUMN max_stack_depth    INT;
