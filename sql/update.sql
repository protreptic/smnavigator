/*
    ����� ������������ ����� ������ �� 32 ��������
    ��������������� �� ��������� MD5
*/
drop domain "sm_token";
create domain "sm_token" as nvarchar(32);

/*
    
*/
drop domain "ShortString";
create domain "ShortString" as nvarchar(255);

/* ������� �������������� �������� �������� ���� �������� */
create or replace function "sm_explainCoverageType" ("coverageType" integer) returns "ShortString" 
begin
    declare "explanation" "ShortString";
    
    case "coverageType"
       when 0 then
          set "explanation" = '0'
       when 1 then
          set "explanation" = '1'
       when 2 then
          set "explanation" = '2'
       when 3 then
          set "explanation" = '3'
       when 4 then
          set "explanation" = '4'
       else
          set "explanation" = 'UNKNOWN'
    end case;     

    return "explanation";
end;
comment on procedure "sm_explainCoverageType" is '
    ������� �������������� �������� �������� ���� ��������';

/* ������� ���������� ����� ����� �� ��������� MD5 */
create or replace function "sm_generateToken" () returns sm_token 
begin
    return hash(cast(now() as sm_token), 'md5');
end;
comment on procedure "sm_generateToken" is '
    ������� ���������� ����� ����� �� ��������� MD5';

/*
    ��������� ��������� � ������� ��� ������������ � ������,
    � ���������� ���������� ����� ��� ������� ��� ������ �������
    ��� ����������� �� ���� ��������� �� ��� ���.
*/
create or replace procedure "sm_authenticate" ("login" nvarchar(255), "password" nvarchar(255))
    result ("token" sm_token, "expiration" datetime)
begin
    update "RefUser" a set a."sessionToken" = "sm_generateToken"(), a."sessionExpiration" = dateadd(hour, 5, now()) where a."wi_login" = "login" and a."wi_password" = "password"; 
	
    select a."sessionToken", a."sessionExpiration" from "RefUser" a where a."wi_login" = "login" and a."wi_password" = "password";
end;
comment on procedure "sm_authenticate" is '
    ��������� ��������� � ������� ��� ������������ � ������,
    � ���������� ���������� ����� ��� ������� ��� ������ �������
    ��� ����������� �� ���� ��������� �� ��� ���.';

/*
    ��������� ��������� ������
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
    
    call sa_set_http_header('Server', null);
    call sa_set_http_header('Expires', null);    
    //call sa_set_http_header('Content-Type', null);     
    
    call sa_set_http_header('AuthStatus', "tokenStatus");
    call sa_set_http_header('AuthStatusDescription', "tokenStatusDescription");    

    return "tokenStatus";
end;
comment on procedure "sm_validateToken" is '
    ��������� ��������� ������';

/*
    ������� ��� �������� ���������� ����������� ����������
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
    ������� ��� �������� ���������� ����������� ����������';

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
			call sa_set_http_header('Checksum', "checksum"); // ���� null, �� ��������� �� ����� ����������     

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
    ���������� ������ ������������ �������� ����������� ����� ���������� � �������
*/
create or replace procedure "sm_getBranch" ("token" sm_token) 
    result ("id" integer, "name" nvarchar(255))
begin
    if ("sm_validateToken" ("token") >= 0) then
        select
            b."Id",     // ������������� �������
            b."Descr"   // ������������ �������
        from     
            "sm_getManager" ("token") a
            join "RefBranch" b 
                on b."Id" = a."branch"
    endif;
end;
comment on procedure "sm_getBranch" is '
    ���������� ������ ������������ �������� ����������� ����� ���������� � �������';

/*
    ���������� ������ ������������ �������� ����������� ����� ���������� � �������        
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
    ���������� ������ ������������ �������� ����������� ����� ���������� � �������';

/*
        
*/
create or replace procedure "sm_getDepartment" ("token" sm_token) 
    result ("id" integer, "name" nvarchar(255))
begin
    if ("sm_validateToken" ("token") >= 0) then
        select
            b."Id",     // ������������� ������������
            b."Descr"   // ������������ ������������
        from     
            "sm_getManager" ("token") a
            join "RefDepartment" b 
                on b."Id" = a."department"
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
        select 
            d."Id", d."Descr", b."Descr", b."Email", b."Phone", b."Branch", b."Department"
        from     
            "sm_getDepartment" ("token") a
             join "RefEmployee" b 
                on b."Department" = a."id"
             join "RefUsersEmployee" c 
                on c."Employee" = b."Id"
             join "RefUser" d 
                on d."Id" = c."ParentExt"
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
    result ("id" integer, "visitDate" nvarchar(255), "psr" integer, "store" integer)
begin
    if ("sm_validateToken" ("token") >= 0) then    
        select 
            b."Id", b."StartDate", a."id", b."Outlet" 
        from "sm_getPsr" ("token") a 
            join "TaskVisitJournal" b 
                on b."Author" = a."id" 
        where datediff(day, b."StartDate", now()) = 0;
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
        "id" integer, "name" nvarchar(255), "customer" nvarchar(255), 
        "address" nvarchar(255), "tel" nvarchar(255), "channel" nvarchar(255), 
        "coverageType" nvarchar(255), "latitude" double, "longitude" double)
begin
    if ("sm_validateToken" ("token") >= 0) then    
        select distinct
            b."Id", b."Descr", c."Descr", 
            b."Address", e."Phone", d."Descr",
            "sm_explainCoverageType" (c."CoverageType"),
            b."LocationLat", b."LocationLon"
        from
            "sm_getRoute" ("token") a 
            join "RefOutlet" b 
                on b."Id" = a."store"
            join "RefCustomer" c
                on c."Id" = b."ParentExt"
            join "RefStoreChannel" d
                on d."Id" = b."Channel"
            left join "RefContact" e
                on e."Id" = c."Contact" 
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

/*
        
*/
create or replace procedure "sm_getMeasure" ("token" sm_token) 
    result (
        "id" integer, "lastVisit" nvarchar(255), "nextVisit" nvarchar(255), 
        "turnoverPreviousMonth" double, "turnoverCurrentMonth" double,
        "totalDistribution" double, "goldenDistribution" double, "goldenStatus" nvarchar(255), "frequencyOfVisits" integer)
begin
    if ("sm_validateToken" ("token") >= 0) then    
        select
            b."id", // ������������� �����
            null,   // ���� ���������� ������
            null,   // ���� ���������� ������
            0,  // ������������ �������� ������
            0,  // ������������ ����������� ������
            0,  // ���
            0,  // ���
            c."Descr",  // "�������" ������ �����
            0           // ������� ���������
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
drop service "sm_getMeasure";
create service "sm_getMeasure" 
    type 'json'
    authorization off
    secure off
    user dba
    methods 'GET,POST'
    as call "sm_getMeasure" (:token);