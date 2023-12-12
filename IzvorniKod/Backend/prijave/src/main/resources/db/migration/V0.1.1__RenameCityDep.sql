ALTER TABLE citydep
RENAME TO citydept;

ALTER TABLE citydept
RENAME COLUMN citydep_id TO citydept_id;

ALTER TABLE citydept
RENAME COLUMN citydep_name TO citydept_name;