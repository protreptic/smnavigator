drop schema "SM_NAVIGATOR" if exists;
drop schema "sm_navigator" if exists;
create schema "sm_navigator";

/*
	
*/
create table "sm_navigator"."manager" (
	id integer primary key,
	name varchar(255),
	email varchar(255),
	tel varchar(255),
	branch integer,
	department integer
);

/*
	
*/
create table "sm_navigator"."branch" (
	id integer primary key,
	name varchar(255)
);

/*
	
*/
create table "sm_navigator"."customer" (
	id integer primary key,
	name varchar(255)
);

/*
	
*/
create table "sm_navigator"."department" (
	id integer primary key,
	name varchar(255)
);

/*
	
*/
create table "sm_navigator"."psr" (
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

/*
	
*/
create table "sm_navigator"."route" (
	id integer primary key,
	visit_date timestamp,
	psr integer,
	store integer
);

/*
	
*/
create table "sm_navigator"."store" (
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

/*
	
*/
create table "sm_navigator"."measure" (
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

/*
	
*/
create table "sm_navigator"."georegion" (
	id integer,
	latitude double,
	longitude double		
);
