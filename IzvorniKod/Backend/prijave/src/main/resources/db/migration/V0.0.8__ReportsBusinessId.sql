CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE Reports
ADD COLUMN business_id UUID NOT NULL DEFAULT uuid_generate_v4();
