drop schema "SM_NAVIGATOR" if exists;
create schema "SM_NAVIGATOR";

/*
	
*/
create table "SM_NAVIGATOR"."branch" (
	id integer primary key,
	name varchar(255)
);

/*
	
*/
create table "SM_NAVIGATOR"."department" (
	id integer primary key,
	name varchar(255)
);

/*
	
*/
create table "SM_NAVIGATOR"."manager" (
	id integer primary key,
	name varchar(255),
	email varchar(255),
	tel varchar(255),
	branch integer,
	department integer
);

/*
	
*/
create table "SM_NAVIGATOR"."psr" (
	id integer primary key,
	name varchar (255),
	project varchar (255),
	tel varchar (255),
	branch varchar (255),
	department varchar (255),
	latitude double,
	longitude double,
);

/*
	
*/
create table "SM_NAVIGATOR"."store" (
	id integer primary key,
	name varchar (255),
	customer varchar (255),
	address varchar (255),
	tel varchar (255),
	channel varchar (255),
	coverage_type varchar (255),
	golden_status varchar (255),
	psr integer,
	visit_frequency varchar (255),
	latitude double,
	longitude double,
	store_statistics integer
);

/*
	
*/
create table "SM_NAVIGATOR"."store_statistics" (
	id integer primary key,
	last_visit date,
	next_visit date,
	turnover_previous_month float,
	turnover_current_month float,
	total_distribution float,
	golden_distribution float
);

/*
	
*/
create table "SM_NAVIGATOR"."psr_route" (
	id integer primary key,
	visit_date date,
	psr integer,
	store integer
);

/*
	
*/
create table "SM_NAVIGATOR"."sync_info" ();

/*
	
*/
create table "SM_NAVIGATOR"."georegion" (
	id integer,
	latitude double,
	longitude double		
);
