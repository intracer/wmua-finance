# --- !Ups

CREATE TABLE "op_id" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "rev_id" INT,
  FOREIGN KEY ("rev_id") REFERENCES "operation" ("id")
);

ALTER TABLE "operation" add COLUMN "parent_rev_id" INT;

ALTER TABLE "operation" ADD COLUMN "op_id" INT NOT NULL;

ALTER TABLE "operation"
  ADD CONSTRAINT "operation_op_id_id_fk"
FOREIGN KEY ("op_id") REFERENCES "op_id" ("id");