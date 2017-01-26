ALTER TABLE operation add COLUMN log_date timestamp not null;

update operation
set log_date = NOW();

ALTER TABLE operation add COLUMN user_id int not null default 1;

ALTER TABLE operation
  ADD CONSTRAINT operation_user_id_id_fk
FOREIGN KEY (user_id) REFERENCES user(id);