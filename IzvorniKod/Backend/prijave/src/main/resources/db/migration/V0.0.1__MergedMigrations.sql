CREATE TABLE Category (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL
);

CREATE TABLE CityDept (
    city_dept_id SERIAL PRIMARY KEY,
    city_dept_name VARCHAR(150) NOT NULL,
    category_id INT NOT NULL,
    FOREIGN KEY(category_id) REFERENCES Category(category_id)
);

CREATE TABLE Users (
    user_id SERIAL PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL DEFAULT 'USER',
    city_dept_id INT,
    FOREIGN KEY(city_dept_id) REFERENCES CityDept(city_dept_id),
    CONSTRAINT chkRoleCityDept CHECK((role = 'STAFF' AND city_dept_id IS NOT NULL)
                                        OR (role <> 'STAFF' AND city_dept_id IS NULL))
);

CREATE TABLE Problems (
    problem_id SERIAL PRIMARY KEY,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    status VARCHAR(50) NOT NULL,
    category_id INT NOT NULL,
    FOREIGN KEY(category_id) REFERENCES Category(category_id)
);

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE Reports (
    report_id SERIAL PRIMARY KEY,
    business_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    user_id INT,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    address VARCHAR(255),
    report_time TIMESTAMP WITH TIME ZONE DEFAULT (CURRENT_TIMESTAMP + INTERVAL '1 hour'),
    status VARCHAR(50) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    problem_id INT,
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY(problem_id) REFERENCES Problems(problem_id)
);

CREATE TABLE Photos (
    photo_id SERIAL PRIMARY KEY,
    photo_data BYTEA NOT NULL,
    report_id INT,
    FOREIGN KEY(report_id) REFERENCES Reports(report_id)
);