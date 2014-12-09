delete from sm_navigator.georegion;

insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors/2000373929.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors/2001585282.csv', null, 'charset=UTF-8 fieldSeparator=;');
	
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2001362249.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2001362250.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2001362251.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2001362252.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2001362253.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2001601535.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2001603041.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2001603042.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2001943499.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2001943500.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2001943501.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2001943502.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2001943503.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2002233847.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2002372660.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2002372661.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2002372662.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2002372663.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2002372664.csv', null, 'charset=UTF-8 fieldSeparator=;');
insert into sm_navigator.georegion (id, latitude, longitude) 
	select * from csvread('~/distributors_branches/2002372665.csv', null, 'charset=UTF-8 fieldSeparator=;');
	
select count(*) from sm_navigator.georegion;
