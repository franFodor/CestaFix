CREATE TABLE Citydept (
    citydept_id SERIAL PRIMARY KEY,
    citydept_name VARCHAR(150) NOT NULL
);

CREATE TABLE Category (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL
);

CREATE TABLE Problems (
    problem_id SERIAL PRIMARY KEY,
    longitude DOUBLE PRECISION NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    status VARCHAR(50) NOT NULL,
    category_id INT NOT NULL,
    FOREIGN KEY(category_id) REFERENCES Category(category_id)
);

CREATE TABLE CitydeptCategory (
    citydept_id INT,
    category_id INT,
    PRIMARY KEY(citydept_id, category_id),
    FOREIGN KEY(citydept_id) REFERENCES Citydept(citydept_id),
    FOREIGN KEY(category_id) REFERENCES Category(category_id)
);

CREATE TABLE Users (
    user_id SERIAL PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL DEFAULT 'USER',
    citydept_id INT,
    FOREIGN KEY(citydept_id) REFERENCES Citydept(citydept_id),
    CONSTRAINT chkRoleCitydep CHECK((role = 'STAFF' AND citydept_id IS NOT NULL)
                                    OR (role <> 'STAFF' AND citydept_id IS NULL))
);

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE Reports (
    report_id SERIAL PRIMARY KEY,
    user_id INT,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    address VARCHAR(255),
    photo BYTEA,
    report_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    business_id UUID NOT NULL UNIQUE DEFAULT uuid_generate_v4(),
    problem_id INT,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY(problem_id) REFERENCES Problems(problem_id)
);