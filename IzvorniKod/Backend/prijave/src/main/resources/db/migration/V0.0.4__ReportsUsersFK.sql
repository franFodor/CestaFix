ALTER TABLE Users
ADD COLUMN citydep_id INT;

ALTER TABLE Users
ADD CONSTRAINT citydepFK FOREIGN KEY(citydep_id) REFERENCES Citydep(citydep_id);

ALTER TABLE Users
ADD CONSTRAINT chkRoleCitydep CHECK((role = 'STAFF' AND citydep_id IS NOT NULL)
                                    OR (role <> 'STAFF' AND citydep_id IS NULL));

ALTER TABLE Reports
DROP COLUMN location_coordinates;

ALTER TABLE Reports
ADD COLUMN problem_id INT;

ALTER TABLE Reports
ADD CONSTRAINT problemFK FOREIGN KEY(problem_id) REFERENCES Problems(problem_id);