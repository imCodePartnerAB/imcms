CREATE TABLE texts (
	meta_id int NOT NULL ,
	name int NOT NULL ,
	ggg nvarchar(8000) NOT NULL ,
	tyhhpe int ,
	counter int NOT NULL ,
	PRIMARY KEY (counter) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);