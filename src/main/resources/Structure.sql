--Drop DATABASE IF exists "MyCompany";
CREATE DATABASE "MyCompany"
    WITH OWNER = "postgres"
    ENCODING = 'UTF8';

CREATE TABLE "User" (
        Username VARCHAR(255) PRIMARY KEY NOT NULL,
        Password VARCHAR(255) NOT NULL
);

INSERT INTO "User" (Username, Password) VALUES ('AAA', '123');
INSERT INTO "User" (Username, Password) VALUES ('BBB', '456');
INSERT INTO "User" (Username, Password) VALUES ('CCC', '789');


select * from "User";