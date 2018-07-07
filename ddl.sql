create database spider;
use spider;
create table film(
	id int primary key auto_increment;
	title varchar(40),
    poster varchar(140),
    star double,
    rating varchar(140),
    quote varchar(140)
);
