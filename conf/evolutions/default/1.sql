# --- First database schema

# --- !Ups

CREATE TABLE products (
  id            BIGSERIAL PRIMARY KEY,
  location      VARCHAR(255) NOT NULL,
  category      VARCHAR(255) NOT NULL,
  description   VARCHAR(255) NOT NULL,
  brand         VARCHAR(255) NOT NULL,
  unit_quantity VARCHAR(255) NOT NULL,
  ean           VARCHAR(255) NOT NULL,
  price         VARCHAR(255) NOT NULL
);


# --- !Downs

drop table if exists products;
