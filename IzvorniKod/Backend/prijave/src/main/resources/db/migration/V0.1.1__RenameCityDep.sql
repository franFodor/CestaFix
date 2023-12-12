ALTER TABLE citydep
RENAME TO citydept;

ALTER TABLE citydep
RENAME COLUMN citydep_id TO citydept_id;

ALTER TABLE citydep
RENAME COLUMN citydep_name TO citydept_name;