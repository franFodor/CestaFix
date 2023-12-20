-- Update Users table
ALTER TABLE Users
ADD COLUMN role VARCHAR(255) NOT NULL DEFAULT 'USER';

-- Update existing user roles
UPDATE Users
SET role = CASE WHEN is_admin THEN 'ADMIN' ELSE 'USER' END;

-- Remove the is_admin column
ALTER TABLE Users
DROP COLUMN is_admin;