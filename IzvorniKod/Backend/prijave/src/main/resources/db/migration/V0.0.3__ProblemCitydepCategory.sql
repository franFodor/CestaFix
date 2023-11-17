CREATE TABLE Citydep (
    citydep_id SERIAL PRIMARY KEY,
    citydep_name VARCHAR(150) NOT NULL
);

CREATE TABLE Category (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL
);

CREATE TABLE Problems (
    problem_id SERIAL PRIMARY KEY,
    longitude FLOAT NOT NULL,
    latitude FLOAT NOT NULL,
    status VARCHAR(50) NOT NULL,
    category_id INT,
    FOREIGN KEY(category_id) REFERENCES Category(category_id)
);

CREATE TABLE CitydepCategory (
    citydep_id INT,
    category_id INT,
    PRIMARY KEY(citydep_id, category_id),
    FOREIGN KEY(citydep_id) REFERENCES Citydep(citydep_id),
    FOREIGN KEY(category_id) REFERENCES Category(category_id)
);
