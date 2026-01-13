-- Clean up old tables (Order matters! Drop dependents first)
DROP TABLE IF EXISTS instructor_preferences CASCADE;
DROP TABLE IF EXISTS student_preferences CASCADE;
DROP TABLE IF EXISTS student_courses CASCADE;
DROP TABLE IF EXISTS grades CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS packs CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS instructors CASCADE;
DROP TABLE IF EXISTS app_users CASCADE;

-- 1. Base User Table (Inheritance Root)
CREATE TABLE app_users (
                           id BIGSERIAL PRIMARY KEY,
                           email VARCHAR(255) NOT NULL UNIQUE,
                           name VARCHAR(255) NOT NULL,
                           password VARCHAR(255) NOT NULL,
                           role VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN', 'INSTRUCTOR', 'STUDENT', 'GUEST'))
);

-- 2. Subclass Tables
CREATE TABLE instructors (
                             id BIGINT PRIMARY KEY REFERENCES app_users(id) ON DELETE CASCADE
);

CREATE TABLE students (
                          id BIGINT PRIMARY KEY REFERENCES app_users(id) ON DELETE CASCADE,
                          code VARCHAR(50) NOT NULL UNIQUE,
                          year INTEGER NOT NULL
);

-- 3. Domain Entities
CREATE TABLE packs (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       semester VARCHAR(50) NOT NULL,
                       year INTEGER NOT NULL
);

CREATE TABLE courses (
                         id BIGSERIAL PRIMARY KEY,
                         abbr VARCHAR(20) NOT NULL,
                         code VARCHAR(50) NOT NULL UNIQUE,
                         name VARCHAR(255) NOT NULL,
                         type VARCHAR(20) NOT NULL CHECK (type IN ('COMPULSORY', 'OPTIONAL')),
                         description TEXT,
                         group_count INTEGER,
                         instructor_id BIGINT REFERENCES instructors(id),
                         pack_id BIGINT REFERENCES packs(id)
);

CREATE TABLE student_courses (
                                 student_id BIGINT NOT NULL REFERENCES students(id),
                                 course_id BIGINT NOT NULL REFERENCES courses(id),
                                 PRIMARY KEY (student_id, course_id)
);

CREATE TABLE grades (
                        id BIGSERIAL PRIMARY KEY,
                        value DOUBLE PRECISION NOT NULL,
                        student_id BIGINT NOT NULL REFERENCES students(id),
                        course_id BIGINT NOT NULL REFERENCES courses(id)
);

CREATE TABLE student_preferences (
                                     id BIGSERIAL PRIMARY KEY,
                                     preference_rank INTEGER NOT NULL,
                                     pack_name VARCHAR(255) NOT NULL,
                                     version BIGINT,
                                     student_id BIGINT REFERENCES students(id),
                                     course_id BIGINT REFERENCES courses(id),
                                     UNIQUE (student_id, course_id)
);

CREATE TABLE instructor_preferences (
                                        id BIGSERIAL PRIMARY KEY,
                                        weight DOUBLE PRECISION NOT NULL,
                                        optional_course_id BIGINT NOT NULL REFERENCES courses(id),
                                        compulsory_course_id BIGINT NOT NULL REFERENCES courses(id)
);