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
	name varchar(255),
	location integer
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
	visit_date date,
	psr integer,
	store integer
);

create table store (
	id integer primary key,
	name varchar (255),
	customer integer,
	address varchar (255),
	tel varchar (255),
	store_property integer,
	channel varchar (255),
	coverage_type varchar (255),
	latitude double,
	longitude double
);

create table store_property (
	id integer primary key,
	golden_status varchar (255)
);

create table measure (
	id integer primary key,
	visit_frequency integer,
	last_visit timestamp,
	next_visit timestamp,
);

create table target (
	id integer primary key auto_increment,
	store integer,
	name varchar (255),
	target float,
	fact float,
	index float
);

create table location (
	id integer,
	latitude double,
	longitude double
);

create table georegion (
	id integer primary key auto_increment,
	branch_id integer,
	latitude double,
	longitude double		
);

create table geocoordinate (
	id integer primary key auto_increment,
	track integer,
	latitude double,
	longitude double
);

create table track (
	id integer primary key auto_increment,
	track_date date,
	manager integer
);
