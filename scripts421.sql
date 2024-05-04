ALTER TABLE student ADD CONSTRAINT age_constraint CHECK (age >= 16)

ALTER TABLE student ALTER COLUMN name SET NOT NULL

ALTER TABLE student add CONSTRAINT name_unique UNIQUE (name)

ALTER TABLE faculty ADD CONSTRAINT name_color_unique UNIQUE (color, name)

ALTER TABLE student ALTER COLUMN age SET DEFAULT (20)