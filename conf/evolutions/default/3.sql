# --- !Ups

CREATE TABLE "grant_item" (
  "id"         SERIAL NOT NULL PRIMARY KEY,
  "grant_id"   INT    NOT NULL,
  "number"     VARCHAR(20),
  "description" TEXT,
  "total_cost" DECIMAL(20, 2),
  "notes"      TEXT,
  FOREIGN KEY ("grant_id") REFERENCES "grant_list" ("id"),
  CONSTRAINT "uc_number" UNIQUE ("grant_id", "number")
);

ALTER TABLE "operation" add COLUMN "grant_item" int;

ALTER TABLE "operation"
  ADD CONSTRAINT "operation_grant_item_id_id_fk"
FOREIGN KEY ("grant_item") REFERENCES "grant_item" ("id");



