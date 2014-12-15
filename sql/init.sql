drop schema "sm_navigator" if exists;
create schema "sm_navigator";

/*
	
*/
create table "sm_navigator"."branch" (
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
create table "sm_navigator"."manager" (
	id integer primary key,
	name varchar(255)
);

/*
	
*/
create table "sm_navigator"."store" (
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
create table "sm_navigator"."store_statistics" (
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
create table "sm_navigator"."psr" (
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
create table "sm_navigator"."psr_route" (
	id integer primary key,
	visit_date date,
	psr integer,
	store integer
);

/*
	
*/
create table "sm_navigator"."sync_info" ();

/*
	
*/
create table "sm_navigator"."georegion" (
	id integer,
	latitude double,
	longitude double		
);
