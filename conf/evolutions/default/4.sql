CREATE TABLE user (
  id SERIAL not null PRIMARY KEY,
  fullname varchar(255) not null,
  wiki_account VARCHAR(256),
  email varchar(255) UNIQUE not null,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP,
  deleted_at timestamp null,
  password varchar(255),
  roles varchar(255),
  lang char(10)
);