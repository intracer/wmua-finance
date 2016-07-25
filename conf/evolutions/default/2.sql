# --- !Ups

ALTER TABLE account add COLUMN  type VARCHAR(64);

INSERT INTO account (name, type) VALUES ('2202', 'bank');
INSERT INTO account (name, type) VALUES ('2200', 'bank');
INSERT INTO account (name, type) VALUES ('2201', 'bank');
INSERT INTO account (name, type) VALUES ('2203', 'bank');
INSERT INTO account (name, type) VALUES ('nana', 'cash');
INSERT INTO account (name, type) VALUES ('***REMOVED***', 'cash');
INSERT INTO account (name, type) VALUES ('sasha', 'cash');
INSERT INTO account (name, type) VALUES ('ann', 'cash');
