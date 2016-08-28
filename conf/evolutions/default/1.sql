# Users schema

# --- !Ups

CREATE TABLE User (
  id varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  client varchar(255) NOT NULL,
  clientId varchar(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX clientIdPair ON User (client, clientId);

# --- !Downs

DROP TABLE User;