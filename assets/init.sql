drop schema smnavigator if exists;
create schema smnavigator;

set schema smnavigator;

create table manager (
	id integer primary key,
	name varchar(255),
	email varchar(255),
	tel varchar(255),
	branch integer,
	department integer
);

create table branch (
	id integer primary key,
	name varchar(255)
);

create table customer (
	id integer primary key,
	name varchar(255)
);

create table department (
	id integer primary key,
	name varchar(255)
);

create table psr (
	id integer primary key,
	name varchar (255),
	project varchar (255),
	email varchar (255),
	tel varchar (255),
	branch varchar (255),
	department varchar (255),
	latitude double,
	longitude double,
);

create table route (
	id integer primary key,
	visit_date timestamp,
	psr integer,
	store integer
);

create table store (
	id integer primary key,
	name varchar (255),
	customer integer,
	address varchar (255),
	tel varchar (255),
	channel varchar (255),
	coverage_type varchar (255),
	latitude double,
	longitude double
);

create table measure (
	id integer primary key,
	visit_frequency integer,
	last_visit timestamp,
	next_visit timestamp,
	turnover_previous_month float,
	turnover_current_month float,
	total_distribution integer,
	golden_distribution integer,
	golden_status varchar (255)
);

create table target (
	id integer primary key auto_increment,
	store integer,
	name varchar (255),
	target float,
	fact float,
	index float
);

create table georegion (
	id integer,
	latitude double,
	longitude double		
);
