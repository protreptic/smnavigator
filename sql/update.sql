/*
    Токен представляет собой строку из 32 символов
    сгенерированную по алгоритму MD5
*/
drop domain "sm_token";
create domain "sm_token" as nvarchar(32);

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
    update "RefUser" a set a."sessionToken" = "sm_generateToken"(), a."sessionExpiration" = dateadd(hour, 1, now()) where a."wi_login" = "login" and a."wi_password" = "password"; 
	
    select a."sessionToken", a."sessionExpiration" from "RefUser" a where a."wi_login" = "login" and a."wi_password" = "password";
end;
comment on procedure "sm_authenticate" is '
    Процедура принимает в запросе имя пользователя и пароль,
    и генерирует сессионный токен для клинета при каждом запросе
    вне зависимости от того просрочен он или нет.';

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

        call sa_set_http_header('@HttpStatus', '200');
    endif;
    
    call sa_set_http_header('Server', null);
    call sa_set_http_header('Expires', null);    
    //call sa_set_http_header('Content-Type', null);     
    
    call sa_set_http_header('AuthStatus', "tokenStatus");
    call sa_set_http_header('AuthStatusDescription', "tokenStatusDescription");    

    return "tokenStatus";
end;
comment on procedure "sm_validateToken" is '';

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

/*
    
*/
create or replace procedure "sm_checkUpdates" ("token" sm_token, "packageName" nvarchar(255)) 
    result ("artifactId" nvarchar(255), "artifactName" nvarchar(255), "package" nvarchar(255), "description" nvarchar(255), "versionCode" integer, "versionName" nvarchar(255))
begin
    if ("sm_validateToken" ("token") >= 0) then
        select 
			a."artifact_id", a."artifact_name", a."package", a."description", a."version_code", a."version_name"
        from 
            "sm_release" a
        where
            a."package" = "packageName"
        order by 
            a."version_code" desc;
    endif; 
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

    if ("sm_validateToken" ("token") >= 0) then
		if ((select a."artifact_id" from "sm_release" a where a."artifact_id" = "artifactId") is not null) then
			select a."artifact_name" into "appName" from sm_release a where a."artifact_id" = "artifactId"; 
        	select a."version_name" into "versionName" from sm_release a where a."artifact_id" = "artifactId";     
			select a."hash" into "checksum" from sm_release a where a."artifact_id" = "artifactId";    
			
        	call sa_set_http_header('Content-Type', 'application/vnd.android.package-archive');     
        	call sa_set_http_header('Content-Disposition', 'attachment; filename="' || string("appName", '-', "versionName", '.apk') || '"');
			call sa_set_http_header('Checksum', "checksum");        

        	select a."data" from "sm_release" a where a."artifact_id" = "artifactId";
		else 
			call sa_set_http_header('@HttpStatus', '404');
    		call sa_set_http_header('Server', null);
   	 		call sa_set_http_header('Expires', null);    
    		call sa_set_http_header('Content-Type', null); 
		endif;
    endif;
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
            c."Email",
            c."Phone",
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
        
*/
create or replace procedure "sm_getBranch" ("token" sm_token) 
    result ("id" integer, "name" nvarchar(255))
begin
    if ("sm_validateToken" ("token") >= 0) then
        select
            d."Id",
            d."Descr"
        from     
            "RefUser" a
            join "RefUsersEmployee" b 
                on a."Id" = b."ParentExt"
            join "RefEmployee" c 
                on b."Employee" = c."Id"
            join "RefBranch" d 
                on c."Branch" = d."Id"
        where
            a."sessionToken" = "token"
    endif;
end;

/*
        
*/
drop service "sm_getBranch";
create service "sm_getBranch" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_getBranch" (:token);

/*
        
*/
create or replace procedure "sm_getDepartment" ("token" sm_token) 
    result ("id" integer, "name" nvarchar(255))
begin
    if ("sm_validateToken" ("token") >= 0) then
        select
            d."Id",
            d."Descr"
        from     
            "RefUser" a
            join "RefUsersEmployee" b 
                on a."Id" = b."ParentExt"
            join "RefEmployee" c 
                on b."Employee" = c."Id"
            join "RefDepartment" d 
                on c."Department" = d."Id"
        where
            a."sessionToken" = "token"
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
    result ("id" integer, "name" nvarchar(255), "project" nvarchar(255), "email" nvarchar(255), "tel" nvarchar(255), "branch" integer, "department" integer)
begin
    declare "department_id" integer;

    if ("sm_validateToken" ("token") >= 0) then    
        select d."Id" into "department_id"
        from     
            "RefUser" a
            join "RefUsersEmployee" b 
                on a."Id" = b."ParentExt"
            join "RefEmployee" c 
                on b."Employee" = c."Id"
            join "RefDepartment" d 
                on c."Department" = d."Id"
        where a."sessionToken" = "token";
        
        select 
            a."Id",
            a."Descr",
            c."Descr",
            c."Email",
            c."Phone",
            d."Id",
            e."Id"
        from     
            "RefUser" a
            join "RefUsersEmployee" b 
                on a."Id" = b."ParentExt"
            join "RefEmployee" c 
                on b."Employee" = c."Id"
            join "RefDepartment" d 
                on c."Department" = d."Id"
            join "RefBranch" e
                on c."Branch" = e."Id"
        where
            c."Department" = "department_id";
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