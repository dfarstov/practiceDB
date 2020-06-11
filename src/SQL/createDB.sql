use practice;

CREATE TABLE regions
(
	id_region INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE 
);

INSERT INTO regions(name) VALUES("Сев. Америка");
INSERT INTO regions(name) VALUES("Европа"); 
INSERT INTO regions(name) VALUES("Азия");

CREATE TABLE countries
(
	id_country INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    id_region INT NOT NULL,
    FOREIGN KEY (id_region) REFERENCES regions (id_region)
);

INSERT INTO countries(name, id_region) VALUES("США", 1);
INSERT INTO countries(name, id_region) VALUES("Франция", 2);
INSERT INTO countries(name, id_region) VALUES("Дания", 2);

CREATE TABLE products_statistics
(
	id_staistic INT PRIMARY KEY AUTO_INCREMENT,
    id_country INT NOT NULL,
    oil INT NOT NULL,
    cheese INT NOT NULL,
    FOREIGN KEY (id_country) REFERENCES countries (id_country)
);

INSERT INTO products_statistics(id_country, oil, cheese) VALUES(1, 641, 632);
INSERT INTO products_statistics(id_country, oil, cheese) VALUES(2, 150, 123);
INSERT INTO products_statistics(id_country, oil, cheese) VALUES(3, 166, 84);