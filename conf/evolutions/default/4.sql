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

INSERT INTO test.user (fullname, wiki_account, email, created_at, deleted_at, password, roles, lang)
VALUES ('Admin', null, 'admin', '2016-12-24 12:46:23', null, null, 'admin', 'en');

INSERT INTO test.user (fullname, wiki_account, email, created_at, deleted_at, password, roles, lang)
VALUES ('Viewer', null, 'viewer', '2016-12-24 12:46:23', null, null, 'organizer', 'en');