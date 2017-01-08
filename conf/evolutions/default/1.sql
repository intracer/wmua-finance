# --- !Ups
CREATE TABLE account (
  id   SERIAL NOT NULL PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE category (
  id   SERIAL NOT NULL PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE project (
  id   SERIAL NOT NULL PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE grant_list (
  id   SERIAL NOT NULL PRIMARY KEY,
  name VARCHAR(255),
  url  VARCHAR(1024)
);

CREATE TABLE operation (
  id         SERIAL         NOT NULL PRIMARY KEY,
  op_date    VARCHAR(20)    NOT NULL,
  amount     DECIMAL(20, 2),
  account_id INT,
  cat_id     INT,
  proj_id    INT,
  grant_id   INT,
  descr      TEXT,
  FOREIGN KEY (account_id) REFERENCES account (id),
  FOREIGN KEY (cat_id) REFERENCES category (id),
  FOREIGN KEY (proj_id) REFERENCES project (id),
  FOREIGN KEY (grant_id) REFERENCES grant_list (id)
);


ALTER TABLE operation add COLUMN grant_row VARCHAR(20);

ALTER TABLE operation
  ADD CONSTRAINT operation_project_id_id_fk
FOREIGN KEY (proj_id) REFERENCES project (id);


