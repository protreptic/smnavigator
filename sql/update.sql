/*
    Токен представляет собой строку из 32 символов
    сгенерированную по алгоритму MD5
*/
drop domain "sm_token";
create domain "sm_token" as nvarchar(32);

/*
    
*/
drop domain "ShortString";
create domain "ShortString" as nvarchar(255);

/* Функция интерпретирует числовое значение типа покрытия */
create or replace function "sm_explainCoverageType" ("coverageType" integer) returns "ShortString" 
begin
    declare "explanation" "ShortString";
    
    case "coverageType"
       when 0 then
          set "explanation" = 'UNKNOWN'
       when 1 then
          set "explanation" = 'HFS'
       when 2 then
          set "explanation" = 'WHS'
       when 3 then
          set "explanation" = 'KBD'
       when 4 then
          set "explanation" = 'NA'
       else
          set "explanation" = 'UNKNOWN'
    end case;     

    return "explanation";
end;
comment on procedure "sm_explainCoverageType" is '
    Функция интерпретирует числовое значение типа покрытия';

/* Функция генерирует новый токен по алгоритму MD5 */
create or replace function "sm_generateToken" () returns sm_token 
begin
    return hash(cast(now() as sm_token), 'md5');
end;
comment on procedure "sm_generateToken" is '
    Функция генерирует новый токен по алгоритму MD5';

/*
    Процедура принимает в запросе имя пользователя и пароль,
    и генерирует сессионный токен для клинета при каждом запросе
    вне зависимости от того просрочен он или нет.
*/
create or replace procedure "sm_authenticate" ("login" nvarchar(255), "password" nvarchar(255))
    result ("token" sm_token, "expiration" datetime)
begin
    update "RefUser" a set a."sessionToken" = "sm_generateToken"(), a."sessionExpiration" = dateadd(hour, 5, now()) where a."wi_login" = "login" and a."wi_password" = "password"; 
	
    select a."sessionToken", a."sessionExpiration" from "RefUser" a where a."wi_login" = "login" and a."wi_password" = "password";
end;
comment on procedure "sm_authenticate" is '
    Процедура принимает в запросе имя пользователя и пароль,
    и генерирует сессионный токен для клинета при каждом запросе
    вне зависимости от того просрочен он или нет.';

create or replace procedure "sm_auth2" ("login" nvarchar(255), "password" nvarchar(255))
    result ("result" text)
begin
    if (("login" is null) or ("login" = '') or ("password" is null) or ("password" = '')) then       
        call sa_set_http_header('@HttpStatus', '403');
    elseif ((select top 1 count(a."wi_login") from "RefUser" a where a."wi_login" = "login") = 0) then     
        call sa_set_http_header('@HttpStatus', '403');
    elseif ((select top 1 count(a."wi_login") from "RefUser" a where a."wi_login" = "login" and a."wi_password" = "password") = 0) then
        call sa_set_http_header('@HttpStatus', '403');
    else
        update 
            "RefUser" a 
        set 
            a."sessionToken" = "sm_generateToken"(), 
            a."sessionExpiration" = dateadd(hour, 5, now()) 
        where 
            a."wi_login" = "login" 
                and 
            a."wi_password" = "password"; 

        select 
            string('{"sessionToken":"', a."sessionToken", '","sessionExpiration":"',a."sessionExpiration",'"}')            
        from 
            "RefUser" a 
        where 
            a."wi_login" = "login" 
                and 
            a."wi_password" = "password";               
    endif;  
end;

drop service "sm_auth2";
create service "sm_auth2" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_auth2" (:login,:password); 

/*
    Процедура валидации токена
*/
create or replace function "sm_validateToken" ("token" sm_token) returns integer 
begin
    declare "tokenStatus" integer;
    declare "tokenStatusDescription" nvarchar(255);      

    if (("token" is null) or ("token" = '') or (length("token") != 32)) then       
        set "tokenStatus" = -101;
        set "tokenStatusDescription" = 'TOKEN_REQUIRED';      

        call sa_set_http_header('@HttpStatus', '401');
    elseif ((select a."sessionToken" from "RefUser" a where a."sessionToken" = "token") is null) then
        set "tokenStatus" = -103;
        set "tokenStatusDescription" = 'TOKEN_REJECTED';        

        call sa_set_http_header('@HttpStatus', '403');
	elseif (datediff(hour, now(), (select a."sessionExpiration" from "RefUser" a where a."sessionToken" = "token")) <= 0) then
		set "tokenStatus" = -104;
        set "tokenStatusDescription" = 'TOKEN_EXPIRED';        

        call sa_set_http_header('@HttpStatus', '403');
    else
        set "tokenStatus" = 100;
        set "tokenStatusDescription" = 'TOKEN_ACCEPTED';        
    endif;  
    
    call sa_set_http_header('AuthStatus', "tokenStatus");
    call sa_set_http_header('AuthStatusDescription', "tokenStatusDescription");    

    return "tokenStatus";
end;
comment on procedure "sm_validateToken" is '
    Процедура валидации токена';

/*
    Таблица для хранения артифактов репозитория обновления
*/
drop table if exists "sm_release";
create table "sm_release" (
    "artifact_id" nvarchar(255) primary key default cast(newid() as nvarchar(255)),
    "artifact_name" nvarchar(255) not null,
    "package" nvarchar(255) not null,
    "description" nvarchar(255) not null,
    "version_code" integer not null, 
    "version_name" nvarchar(255) not null,
    "hash" nvarchar(255),
    "data" long binary
);
comment on table "sm_release" is '
    Таблица для хранения артифактов репозитория обновления';

/*
    
*/
create or replace trigger "sm_rehash" after update of "data" on "sm_release" REFERENCING new as new_name 
for each row
begin
	update "sm_release" set "hash" = hash(new_name."data",'md5') where "artifact_id" = new_name."artifact_id";
end;
comment on trigger "sm_rehash" is '';

insert into "sm_release" ("artifact_name","package","description","version_code","version_name") values ('smnavigator','ru.magnat.smnavigator','',6,'1.0.0-alpha.5');
insert into "sm_release" ("artifact_name","package","description","version_code","version_name") values ('smnavigator','ru.magnat.smnavigator','',7,'1.0.0-alpha.6');
insert into "sm_release" ("artifact_name","package","description","version_code","version_name") values ('smnavigator','ru.magnat.smnavigator','',8,'1.0.0-alpha.7');
insert into "sm_release" ("artifact_name","package","description","version_code","version_name") values ('smnavigator','ru.magnat.smnavigator','',9,'1.0.0-alpha.8');
insert into "sm_release" ("artifact_name","package","description","version_code","version_name") values ('smnavigator','ru.magnat.smnavigator','',10,'1.0.0-alpha.9');
insert into "sm_release" ("artifact_name","package","description","version_code","version_name") values ('sfs','ru.magnat.sfs','',108,'1.108');
insert into "sm_release" ("artifact_name","package","description","version_code","version_name") values ('smnavigator','ru.magnat.smnavigator','',11,'1.0.0-alpha.10');
insert into "sm_release" ("artifact_name","package","description","version_code","version_name") values ('smnavigator','ru.magnat.smnavigator','',12,'1.0.0-alpha.11');
insert into "sm_release" ("artifact_name","package","description","version_code","version_name") values ('smnavigator','ru.magnat.smnavigator','',13,'1.0.0-beta.1');
insert into "sm_release" ("artifact_name","package","description","version_code","version_name") values ('smnavigator','ru.magnat.smnavigator','',14,'1.0.0-beta.2');
insert into "sm_release" ("artifact_name","package","description","version_code","version_name") values ('smnavigator','ru.magnat.smnavigator','',19,'1.0.0-beta.7');
insert into "sm_release" ("artifact_name","package","description","version_code","version_name") values ('smnavigator','ru.magnat.smnavigator','',20,'1.0.0-beta.8');
insert into "sm_release" ("artifact_name","package","description","version_code","version_name") values ('smnavigator','ru.magnat.smnavigator','',21,'1.0.0-beta.9');

/*
    
*/
create or replace procedure "sm_checkUpdates" ("token" sm_token, "packageName" nvarchar(255)) 
    result ("artifactId" nvarchar(255), "artifactName" nvarchar(255), "packageName" nvarchar(255), "description" nvarchar(255), "versionCode" integer, "versionName" nvarchar(255))
begin
    //if ("sm_validateToken" ("token") >= 0) then
        select 
			a."artifact_id", a."artifact_name", a."package", a."description", a."version_code", a."version_name"
        from 
            "sm_release" a
        where
            a."package" = "packageName"
        order by 
            a."version_code" desc;
    //endif; 
end;
comment on procedure "sm_checkUpdates" is '';

/*
    
*/
create or replace procedure "sm_downloadUpdate" ("token" sm_token, "artifactId" nvarchar(255)) 
    result ("data" long binary)
begin
    declare "appName" nvarchar(255); 
    declare "versionName" nvarchar(255);    
	declare "checksum" nvarchar(32); 
    declare "contentLength" integer; 

    //if ("sm_validateToken" ("token") >= 0) then
		//if ((select a."artifact_id" from "sm_release" a where a."artifact_id" = "artifactId") is not null) then
			select a."artifact_name" into "appName" from sm_release a where a."artifact_id" = "artifactId"; 
        	select a."version_name" into "versionName" from sm_release a where a."artifact_id" = "artifactId";     
			select a."hash" into "checksum" from sm_release a where a."artifact_id" = "artifactId";    
            select length(a."data") into "contentLength" from sm_release a where a."artifact_id" = "artifactId";   			

        	call sa_set_http_header('Content-Type', 'application/vnd.android.package-archive');     
        	call sa_set_http_header('Content-Disposition', 'attachment; filename="' || string("appName", '-', "versionName", '.apk') || '"');
			call sa_set_http_header('Checksum', "checksum"); // если null, то заголовок не будет установлен     
            call sa_set_http_header('Filename', string("appName", '-', "versionName", '.apk'));            
            call sa_set_http_header('Content-Length', contentLength);            

        	select a."data" from "sm_release" a where a."artifact_id" = "artifactId";
		//else 
		//	call sa_set_http_header('@HttpStatus', '404');
    	//	call sa_set_http_header('Server', null);
   	 	//	call sa_set_http_header('Expires', null);    
    	//	call sa_set_http_header('Content-Type', null); 
		//endif;
    //endif;
end;
comment on procedure "sm_downloadUpdate" is '';

/*
    
*/
drop service "sm_auth";
create service "sm_auth" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_authenticate" (:login,:password); 

/*
    
*/
drop service "sm_checkUpdates";
create service "sm_checkUpdates" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_checkUpdates" (:token,:packageName); 

/*
    
*/
drop service "sm_downloadUpdate";
create service "sm_downloadUpdate" 
    type 'raw'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_downloadUpdate" (:token,:artifactid);

/*
        
*/
create or replace procedure "sm_getManager" ("token" sm_token) 
    result ("id" integer, "name" varchar(255), "email" nvarchar(255), "tel" nvarchar(255), "branch" integer, "department" integer)
begin
    if ("sm_validateToken" ("token") >= 0) then
        select
            a."Id",
            a."Descr",
            ifnull(c."Email",'Нет'),
            ifnull(c."Phone",'Нет'),
            c."Branch",
            c."Department"
        from     
            "RefUser" a
            join "RefUsersEmployee" b 
                on a."Id" = b."ParentExt"
            join "RefEmployee" c 
                on b."Employee" = c."Id"
        where
            a."sessionToken" = "token"
    endif;
end;

/*
        
*/
drop service "sm_getManager";
create service "sm_getManager" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_getManager" (:token);

/*
    Возвращает филиал пользователя которому принадлежит токен переданный в запросе
*/
create or replace procedure "sm_getBranch" ("token" sm_token) 
    result ("id" integer, "name" nvarchar(255), "location" integer)
begin
    if ("sm_validateToken" ("token") >= 0) then
        select
            b."Id",     // идентификатор филиала
            b."Descr",   // наименование филиала
            b."Id"
        from     
            "sm_getManager" ("token") a
            join "RefBranch" b 
                on b."Id" = a."branch"
        order by
            b."Descr" asc;
    endif;
end;
comment on procedure "sm_getBranch" is '
    Возвращает филиал пользователя которому принадлежит токен переданный в запросе';

/*
    Возвращает филиал пользователя которому принадлежит токен переданный в запросе        
*/
drop service "sm_getBranch";
create service "sm_getBranch" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_getBranch" (:token);
comment on service "sm_getBranch" is '
    Возвращает филиал пользователя которому принадлежит токен переданный в запросе';

create or replace procedure "sm_getLocation" ("token" sm_token) 
    result ("id" integer, "latitude" double, "longitude" double)
begin
    if ("sm_validateToken" ("token") >= 0) then
        select
            b."Id",     // идентификатор филиала
            48.7193900,   // наименование филиала
            44.5018400
        from     
            "sm_getManager" ("token") a
            join "RefBranch" b 
                on b."Id" = a."branch"
        order by
            b."Descr" asc;
    endif;
end;
comment on procedure "sm_getLocation" is '';

drop service "sm_getLocation";
create service "sm_getLocation" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_getLocation" (:token);
comment on service "sm_getLocation" is '';

/*
        
*/
create or replace procedure "sm_getDepartment" ("token" sm_token) 
    result ("id" integer, "name" nvarchar(255))
begin
    if ("sm_validateToken" ("token") >= 0) then
        select
            b."Id",     // идентификатор департамента
            b."Descr"   // наименование департамента
        from     
            "sm_getManager" ("token") a
            join "RefDepartment" b 
                on b."Id" = a."department"
        order by
            b."Descr" asc;
    endif;
end;

/*
        
*/
drop service "sm_getDepartment";
create service "sm_getDepartment" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_getDepartment" (:token);

/*
        
*/
create or replace procedure "sm_getPsr" ("token" sm_token) 
    result ("id" integer, "name" nvarchar(255), "project" nvarchar(255), "email" nvarchar(255), "tel" nvarchar(255), "branch" integer, "department" integer, "latitude" double, "longitude" double)
begin
    declare "department_id" integer;

    if ("sm_validateToken" ("token") >= 0) then    
        select distinct
            d."Id", d."Descr", b."Descr", b."Email", 
            b."Phone", b."Branch", b."Department",
            ifnull(d."LastLatitude",0,d."LastLatitude"), ifnull(d."LastLongitude",0,d."LastLongitude")
        from     
            "sm_getDepartment" ("token") a
             join "RefEmployee" b 
                on b."Department" = a."id"
             join "RefUsersEmployee" c 
                on c."Employee" = b."Id"
             join "RefUser" d 
                on d."Id" = c."ParentExt"
        where
            d."IsMark" = 0
            and d."TestUser" = 0
            and d."IsMark" = 0
        order by
            d."Descr" asc;
    endif;
end;

/*
        
*/
drop service "sm_getPsr";
create service "sm_getPsr" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_getPsr" (:token);

/*
        
*/
create or replace procedure "sm_getRoute" ("token" sm_token) 
    result ("id" integer, "visitDate" date, "psr" integer, "store" integer)
begin
    if ("sm_validateToken" ("token") >= 0) then    
        select 
            b."Id", cast(b."StartDate" as date), a."id", b."Outlet" 
        from "sm_getPsr" ("token") a 
            join "TaskVisitJournal" b 
                on b."Author" = a."id" 
        where datediff(month, b."StartDate", now()) = 0;
    endif;
end;

/*
        
*/
drop service "sm_getRoute";
create service "sm_getRoute" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_getRoute" (:token);

/*
        
*/
create or replace procedure "sm_getStore" ("token" sm_token) 
    result (
        "id" integer, "name" nvarchar(255), "customer" integer, 
        "address" nvarchar(255), "tel" nvarchar(255), "storeProperty" integer, "channel" nvarchar(255), 
        "coverageType" nvarchar(255), "latitude" double, "longitude" double)
begin
    if ("sm_validateToken" ("token") >= 0) then    
        select distinct
            b."Id", b."Descr", c."Id", 
            b."Address", ifnull(e."Phone",'Нет'), b."Id", ifnull(d."Descr",'',d."Descr"),
            "sm_explainCoverageType" (c."CoverageType"),
            ifnull(b."LocationLat",0,b."LocationLat"), ifnull(b."LocationLon",0,b."LocationLon")
        from
            "sm_getRoute" ("token") a 
            join "RefOutlet" b 
                on b."Id" = a."store"
            join "RefCustomer" c
                on c."Id" = b."ParentExt"
            left join "RefStoreChannel" d
                on d."Id" = b."Channel"
            left join "RefContact" e
                on e."Id" = c."Contact"
        where
            b."IsMark" = 0
        order by
            b."Descr" asc; 
    endif;
end;

/*
        
*/
drop service "sm_getStore";
create service "sm_getStore" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_getStore" (:token);

create or replace procedure "sm_getStoreProperty" ("token" sm_token) 
    result (
        "id" integer, 
        "goldenStatus" nvarchar(255))
begin
    if ("sm_validateToken" ("token") >= 0) then    
        select
            a."id",
            isnull(c."Descr", 'Статус недоступен')
        from
            "sm_getStore" ("token") a 
            join "RefOutlet" b 
                on b."Id" = a."id"
            left join "RefStoreType" c
                on c."Id" = b.StoreType; 
    endif;
end;

/*
    
*/
drop service "sm_getStoreProperty";
create service "sm_getStoreProperty" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_getStoreProperty" (:token);


/*
        
*/
create or replace procedure "sm_getCustomer" ("token" sm_token) 
    result ("id" integer, "name" nvarchar(255))
begin
    if ("sm_validateToken" ("token") >= 0) then    
        select distinct
            c."Id", c."Descr"
        from "sm_getStore" ("token") a
            join "RefOutlet" b
                on b."Id" = a."id" 
            join "RefCustomer" c
                on c."Id" = b."ParentExt"
        where
            c."IsMark" = 0
        order by
            c."Descr" asc; 
    endif;
end;

/*
        
*/
drop service "sm_getCustomer";
create service "sm_getCustomer" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_getCustomer" (:token);

// Возвращает товарооборот точки за прошлый месяц по ее идентификатору
create or replace function "sm_getTurnoverPreviousMonth" ("store_id" integer) returns numeric(10,2)
begin
    declare "turnover" numeric(10,2);    

    select sum(a."Shipments") into "turnover" from "RegSales" a where a."Outlet" = "store_id" and datediff(month, dateadd(month, -1, now()), now()) = 1;

    return isnull("turnover",0);
end;

// Возвращает товарооборот точки за текущий месяц по ее идентификатору
create or replace function "sm_getTurnoverCurrentMonth" ("store_id" integer) returns numeric(10,2)
begin
    declare "turnover" numeric(10,2);    

    select sum(a."Shipments") into "turnover" from "RegSales" a where a."Outlet" = "store_id" and datediff(month, now(), now()) = 0;

    return isnull("turnover",0);
end;

// Возвращает товарооборот точки за период по ее идентификатору
create or replace function "sm_getTurnover" ("store_id" integer, "period" date) returns numeric(10,2)
begin
    declare "turnover" numeric(10,2);    

    select sum(a."Shipments") into "turnover" from "RegSales" a where a."Outlet" = "store_id" and datediff(month, "period", now()) = 0;

    return isnull("turnover",0);
end;

// Возвращает ОПД точки по ее идентификатору
create or replace function "sm_getTotalDistribution" ("store_id" integer) returns integer
begin
    declare "distribution" integer;    
    
    select count(distinct a."Csku") into "distribution" from "RegSales" a where a."Outlet" = "store_id" and datediff(month, now(), now()) < 3;

    return "distribution";
end;

// Возвращает ЗПД точки по ее идентификатору
create or replace function "sm_getGoldenDistribution" ("store_id" integer) returns integer
begin
    declare "distribution" integer;    
    
    select count(distinct a."Csku") into "distribution" from "RegSales" a where a."Outlet" = "store_id" and a."Abc" = 1 and datediff(month, now(), now()) < 3;

    return "distribution";
end;

// Возвращает частоту посещений точки по ее идентификатору
create or replace function "sm_getFrequencyOfVisits" ("store_id" integer) returns integer
begin
    declare "frequency" integer;    
    
    set "frequency" = 0;   

    return "frequency";
end;

// Возвращает дату последнего визита в точку по ее идентификатору
create or replace function "sm_getLastVisitDate" ("store_id" integer) returns date
begin
    declare "lastVisit" date;    
    
    select top 1 a."StartDate" into "lastVisit" from "TaskVisitJournal" a where a."Outlet" = "store_id" and datediff(day, a."StartDate", now()) >= 0 order by a."StartDate" desc; 

    return "lastVisit";
end;

// Возвращает дату следующего визита в точку по ее идентификатору
create or replace function "sm_getNextVisitDate" ("store_id" integer) returns date
begin
    declare "nextVisit" date;    
    
    select top 1 a."StartDate" into "nextVisit" from "TaskVisitJournal" a where a."Outlet" = "store_id" and datediff(day, a."StartDate", now()) < 0 order by a."StartDate" asc;

    return "nextVisit";
end;
    
create or replace procedure "sm_getMeasure" ("token" sm_token) 
    result (
        "id" integer, "lastVisit" datetime, "nextVisit" datetime, 
        "turnoverPreviousMonth" numeric(10,2), "turnoverCurrentMonth" numeric(10,2),
        "totalDistribution" integer, "goldenDistribution" integer, 
        "goldenStatus" nvarchar(255), "frequencyOfVisits" integer)
begin
    //if ("sm_validateToken" ("token") >= 0) then    
        select
            b."id", // идентификатор точки
            "sm_getLastVisitDate" (b."Id"), // дата последнего визита
            "sm_getNextVisitDate" (b."Id"), // дата следующего визита
            "sm_getTurnoverPreviousMonth" (b."Id"),  // товарооборот предыдущего месяца
            "sm_getTurnoverCurrentMonth" (b."Id"),  // товарооборот текущего месяца
            "sm_getTotalDistribution" (b."Id"),  // ОПД
            "sm_getGoldenDistribution" (b."Id"),  // ЗПД
            c."Descr", // "золотой" статус точки
            "sm_getFrequencyOfVisits" (b."Id") // частота посещения
        from
            "sm_getStore" ("token") a 
            join "RefOutlet" b 
                on b."Id" = a."id"
            left join "RefStoreType" c
                on c."Id" = b.StoreType; 
    //endif;
end;

/*
    
*/
drop service "sm_getMeasure";
create service "sm_getMeasure" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_getMeasure" (:token);

create or replace procedure "sm_getTarget" ("token" sm_token) 
    result (
        "store" integer, 
        "name" nvarchar(255), 
        "target" numeric(10,2), 
        "fact" numeric(10,2),
        "index" integer)
begin
    if ("sm_validateToken" ("token") >= 0) then    
        select
            a."id",
            b."Kpi",
            b."Target",
            b."Fact",
            cast(b."KpiIndex" as integer)
        from
            "sm_getStore" ("token") a 
            join "RegOutletTarget" b 
                on b."Outlet" = a."id"; 
    endif;
end;

/*
    
*/
drop service "sm_getTarget";
create service "sm_getTarget" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_getTarget" (:token);

drop table if exists #georegion ;
create table #georegion (
    "branch_id" integer,
    "latitude" double,
    "longitude" double
);

drop table if exists "sm_georegion";
create table "sm_georegion" (
    "id" integer primary key default autoincrement,
    "branch_id" integer,
    "branch_name" nvarchar(255) not null,
    "latitude" double not null,
    "longitude" double not null
);

load into table #georegion from '/home/petr_bu/geodata.csv' 
    format text 
    delimited by ';' 
    encoding 'utf-8';

insert into "sm_georegion" ("branch_name", "latitude", "longitude") select "branch_id", "latitude", "longitude" from #georegion;

create or replace procedure "sm_getGeoregion" ("token" sm_token) 
    result (
        "branchId" integer, 
        "latitude" double, 
        "longitude" double)
begin
    if ("sm_validateToken" ("token") >= 0) then    
        select
            a."id",
            c."latitude",
            c."longitude"
        from
            "sm_getBranch" ("token") a 
            join "RefBranch" b 
                on b."Id" = a."id"
            join "sm_georegion" c
                on b."Id" = c."branch_id";
    endif;
end;

/*
    
*/
drop service "sm_getGeoregion";
create service "sm_getGeoregion" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_getGeoregion" (:token);