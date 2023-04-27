CREATE TABLE IF NOT EXISTS schooldb.subject (
  id decimal(12,0),
  introduction BLOB NOT NULL,
  difficulty varchar(10) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS schooldb.student (
  id decimal(12,0),
  name varchar(50) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS schooldb.topic (
  id decimal(12,0),
  name varchar(50) NOT NULL,
  subject_id decimal(12,0),
  PRIMARY KEY (id),
  CONSTRAINT topic_fk_01 FOREIGN KEY (subject_id) REFERENCES subject (id)
);

CREATE TABLE IF NOT EXISTS schooldb.subject_student (
  subject_id decimal(12,0),
  student_id decimal(12,0),
  PRIMARY KEY (subject_id, student_id),
  CONSTRAINT subject_student_subject_fk FOREIGN KEY (subject_id) REFERENCES subject (id),
  CONSTRAINT subject_student_student_fk FOREIGN KEY (student_id) REFERENCES student (id)
);

--

INSERT IGNORE INTO schooldb.subject (id, name, introduction, difficulty)
  VALUES
  (1, 'Math', 'Once upon a time...', 'MODERATE'),
  (2, 'Geography', 'This is a long...', 'EASY');

INSERT IGNORE INTO schooldb.student (id, name)
  VALUES
  (1, 'Thomas'),
  (2, 'Mario');

INSERT IGNORE INTO schooldb.topic (id, name, subject_id)
  VALUES
  (1, 'Arithmetic', 1),
  (2, 'Geometry', 1),
  (3, 'Asia', 2),
  (4, 'Affrica', 2);

INSERT IGNORE INTO schooldb.subject_student (subject_id, student_id)
  VALUES
  (1, 1),
  (1, 2),
  (2, 1),
  (2, 2);

