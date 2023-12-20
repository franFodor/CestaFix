ALTER TABLE citydep
RENAME TO citydept;

ALTER TABLE citydept
RENAME COLUMN citydep_id TO citydept_id;

ALTER TABLE citydept
RENAME COLUMN citydep_name TO citydept_name;

ALTER TABLE citydepcategory
RENAME TO citydeptcategory;

ALTER TABLE citydeptcategory
RENAME COLUMN citydep_id TO citydept_id;

ALTER TABLE users
RENAME COLUMN citydep_id TO citydept_id;
