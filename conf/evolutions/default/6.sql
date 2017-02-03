# --- !Ups

ALTER TABLE "operation" add COLUMN "parent_rev_id" INT;

ALTER TABLE "operation" add COLUMN "latest" BOOLEAN;
