
-- Note: mariadb driver is case sensitive, keep case in sync with java code

CREATE DATABASE IF NOT EXISTS schooldb;

CREATE SEQUENCE IF NOT EXISTS schooldb.subject_ids_seq;

CREATE SEQUENCE IF NOT EXISTS schooldb.student_ids_seq;

CREATE SEQUENCE IF NOT EXISTS schooldb.topic_ids_seq;

CREATE TABLE IF NOT EXISTS schooldb.subject (
  id decimal(12,0),
  name varchar(30) NOT NULL,
  introduction BLOB NOT NULL,
  difficulty varchar(10) NOT NULL,
  PRIMARY KEY (id) 
    -- not supported in this version: default (next value for subject_ids_seq)
);

CREATE TABLE IF NOT EXISTS schooldb.student (
  id decimal(12,0),
  name varchar(50) NOT NULL,
  PRIMARY KEY (id) 
    -- not supported in this version: default (next value for student_ids_seq)
);

CREATE TABLE IF NOT EXISTS schooldb.topic (
  id decimal(12,0),
  name varchar(50) NOT NULL,
  subject_id decimal(12,0),
  PRIMARY KEY (id) 
    -- not supported in this version: default (next value for topic_ids_seq)
  ,
  CONSTRAINT topic_fk_01 FOREIGN KEY (subject_id) REFERENCES subject (id)
);

CREATE TABLE IF NOT EXISTS schooldb.subject_student (
  subject_id decimal(12,0),
  student_id decimal(12,0),
  PRIMARY KEY (subject_id, student_id),
  CONSTRAINT subject_student_subject_fk FOREIGN KEY (subject_id) REFERENCES subject (id),
  CONSTRAINT subject_student_student_fk FOREIGN KEY (student_id) REFERENCES student (id)
);

