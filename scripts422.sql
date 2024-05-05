CREATE TABLE car (
	id serial PRIMARY KEY,
	brand varchar(50) NOT null,
	model varchar(50) NOT null,
	price NUMERIC(10, 2) CHECK (price > 0)
)

CREATE TABLE person (
	id serial PRIMARY KEY,
	name varchar(100) NOT null,
	age SMALLINT CHECK (age > 0),
	driver_license boolean,
	car_id int REFERENCES car(id)
)

ALTER TABLE car ADD CONSTRAINT brand_model_unique UNIQUE (brand, model)