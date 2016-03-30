CREATE TABLE "player" (
  "id" character varying(255) NOT NULL,
  "password" character varying(255) NOT NULL,
  "country" character varying(255) NOT NULL,
  "elo" integer NOT NULL,
  "email" character varying(255) NOT NULL
);
CREATE TABLE "server" (
  "id" character varying(255) NOT NULL,
  "port" integer NOT NULL,
  "playernumber" integer NULL DEFAULT '0',
  "created" timestamp NOT NULL,
  "updated" timestamp NOT NULL,
  "completed" smallint NOT NULL DEFAULT '0'
);

CREATE TABLE "partie" (
  "id" serial NOT NULL,
  "player" character varying(255) NOT NULL,
  "classement" smallint NOT NULL DEFAULT '0',
  "perdu" smallint NOT NULL,
  "gagnee" smallint NOT NULL,
  "elo" integer NOT NULL,
  "date" character varying(255) NULL
);
CREATE TABLE "server_player" (
  "id" serial NOT NULL,
  "player" character varying(255) NOT NULL,
  "server" character varying(255) NOT NULL
);



ALTER TABLE "player"
ADD CONSTRAINT "player_id" PRIMARY KEY ("id");

ALTER TABLE "server"
ADD CONSTRAINT "server_id" PRIMARY KEY ("id");

ALTER TABLE "partie"
ADD CONSTRAINT "partie_id" PRIMARY KEY ("id");

ALTER TABLE "server_player"
ADD FOREIGN KEY ("player") REFERENCES "player" ("id") ON DELETE CASCADE ON UPDATE CASCADE

ALTER TABLE "server_player"
ADD FOREIGN KEY ("server") REFERENCES "server" ("id")

ALTER TABLE "partie"
ADD FOREIGN KEY ("player") REFERENCES "player" ("id")



