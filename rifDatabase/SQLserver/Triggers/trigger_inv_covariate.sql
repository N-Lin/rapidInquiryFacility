/****** trigger for [dbo].[T_RIF40_INV_COVARIATES]

Check - USERNAME exists.
Check - USERNAME is Kerberos USER on INSERT.
Check - UPDATE not allowed.
Check - DELETE only allowed on own records.

Check - study_geolevel_name. NOT FK YET in this table should it be ?
Check - Covariates  : Oracle code -IF c1%NOTFOUND THEN need clarification 
a) MIN and MAX.  
b) Limits 
c) Check access to covariate table, <covariate name> column exists
d) Check score.

 ******/


 insert into [dbo].[T_RIF40_INV_COVARIATES]
 (INV_ID, STUDY_ID, COVARIATE_NAME, USERNAME, GEOGRAPHY, STUDY_GEOLEVEL_NAME, MIN, MAX)
 values 
 (1,534,'IMD04EDUCAT_QUIN990','PETERH@PRIVATE.NET','EW01','SOA2001',-2,99),
 (1,534,'IMD04EDUCAT_QUIN970','PETERH@PRIVATE.NET','EW01','SOA2001',-1,66)



 SELECT * FROM [T_RIF40_INV_COVARIATES] where COVARIATE_NAME='IMD04EDUCAT_QUIN3'


 -------------------------
 -- create trigger code 
 --------------------------
 create alter trigger tr_inv_covariate
 on [dbo].[T_RIF40_INV_COVARIATES]
 for insert, update 
 as
 begin 
 ----------------------------------------
 -- check covariates : if min< expected 
 ----------------------------------------
	 DECLARE @min nvarchar(MAX) =
		(
		SELECT 
        cast(ic.[min] as varchar(20))+ ' '
		FROM inserted ic 
		where EXISTS (SELECT 1 FROM [dbo].rif40_covariates c 
              WHERE ic.[geography]=c.[GEOGRAPHY] and 
			  c.geolevel_name=ic.study_geolevel_name and
			  c.covariate_name=ic.covariate_name and -- can comment it out to test 
			  ic.[min]<c.[min])
        FOR XML PATH('')
		 );

	IF @min IS NOT NULL
		BEGIN
			RAISERROR('MIn value is less than expected: %s', 16, 1, @min) with log;
		END;
-----------------------------------------
-- check covariates --if max> expected 
-----------------------------------------
 DECLARE @max nvarchar(MAX) =
		(
		SELECT 
        cast(ic.[max] as varchar(20))+ ' '
		FROM inserted ic 
		where EXISTS (SELECT 1 FROM [dbo].rif40_covariates c 
              WHERE ic.[geography]=c.[GEOGRAPHY] and 
			  c.geolevel_name=ic.study_geolevel_name and
			  c.covariate_name=ic.covariate_name and -- can comment it out to test 
			  ic.[max]>c.[max])
        FOR XML PATH('')
		 );

	IF @max IS NOT NULL
		BEGIN
			RAISERROR('MAX value is greater than expected: %s', 16, 1, @max) with log;
		END;


-------------------------------
----Remove when supported
--------------------------------
 DECLARE @type2 nvarchar(MAX) =
		(
		SELECT 
        [STUDY_ID]+ ' '
		FROM inserted ic 
		where EXISTS (SELECT 1 FROM [dbo].rif40_covariates c 
              WHERE ic.[geography]=c.[GEOGRAPHY] and 
			  c.geolevel_name=ic.study_geolevel_name and
			  c.covariate_name=ic.covariate_name and -- can comment it out to test 
			  c.type =2)
        FOR XML PATH('')
		 );

	IF @type2 IS NOT NULL
		BEGIN
			RAISERROR('T_RIF40_INV_COVARIATES study: %s type = 2 (continuous variable) is not currently supported for geolevel_name given', 16, 1,@type2 ) with log;
		END;


 DECLARE @type1 nvarchar(MAX) =
		(
		SELECT 
        [STUDY_ID]+ ' '
		FROM inserted ic 
		where EXISTS (SELECT 1 FROM [dbo].rif40_covariates c 
              WHERE ic.[geography]=c.[GEOGRAPHY] and 
			  c.geolevel_name=ic.study_geolevel_name and
			  c.covariate_name=ic.covariate_name and -- can comment it out to test 
			  c.type =1 and 
			  ic.MAX <> round(ic.MAX,0))
        FOR XML PATH('')
		 );

	IF @type2 IS NOT NULL
		BEGIN
			RAISERROR('T_RIF40_INV_COVARIATES study: %s type = 1 (integer score) and max is not an integer', 16, 1,@type1 ) with log;
		END;

 DECLARE @type1b nvarchar(MAX) =
		(
		SELECT 
        [STUDY_ID]+ ' '
		FROM inserted ic 
		where EXISTS (SELECT 1 FROM [dbo].rif40_covariates c 
              WHERE ic.[geography]=c.[GEOGRAPHY] and 
			  c.geolevel_name=ic.study_geolevel_name and
			  c.covariate_name=ic.covariate_name and -- can comment it out to test 
			  c.type =1 and 
			  ic.MIN<> round(ic.MIN,0))
        FOR XML PATH('')
		 );

	IF @type2 IS NOT NULL
		BEGIN
			RAISERROR('T_RIF40_INV_COVARIATES study: %s type = 1 (integer score) and min is not an integer', 16, 1,@type1b ) with log;
		END;

 DECLARE @type1_min nvarchar(MAX) =
		(
		SELECT 
        [STUDY_ID]+ ' '
		FROM inserted ic 
		where EXISTS (SELECT 1 FROM [dbo].rif40_covariates c 
              WHERE ic.[geography]=c.[GEOGRAPHY] and 
			  c.geolevel_name=ic.study_geolevel_name and
			  c.covariate_name=ic.covariate_name and -- can comment it out to test 
			  c.type =1 and 
			  ic.MIN<0)
        FOR XML PATH('')
		 );

	IF @type2 IS NOT NULL
		BEGIN
			RAISERROR('T_RIF40_INV_COVARIATES study: %s type = 1 (integer score) and min <0', 16, 1,@type1_min ) with log;
		END;
-------------------------------
--Check - study_geolevel_name.
-------------------------------

DECLARE @study_geolevel_nm nvarchar(MAX) =
		(
		SELECT 
        [STUDY_GEOLEVEL_NAME]+ ' '
		FROM inserted ic 
		where EXISTS (SELECT 1 FROM [dbo].[T_RIF40_GEOLEVELS] c 
              WHERE ic.[geography]=c.[GEOGRAPHY] and 
			  c.geolevel_name=ic.study_geolevel_name and
			  ic.STUDY_GEOLEVEL_NAME is not Null 
					)
        FOR XML PATH('')
		 );

	IF @study_geolevel_nm IS NOT NULL
		BEGIN
			RAISERROR('Study geolevel name not found in rif40_geolevels: %s', 16, 1, @study_geolevel_nm) with log;
		END;

end





--------------------------------------------------------------------------
--------------------------tests-------------------------------------------
--------------------------------------------------------------------------
----------------convert this into EXISTS----------------------------------
select a.max 
from [dbo].[T_RIF40_INV_COVARIATES] a,  rif40_covariates r
where 
	a.[geography]=r.[geography] and 
	r.geolevel_name=a.study_geolevel_name and 
	--r.covariate_name=a.covariate_name and 
	a.max>r.max
--------------------------------------------------------------------------
select ic.[min]   
from [dbo].[T_RIF40_INV_COVARIATES] ic
where EXISTS (SELECT 1 FROM [dbo].rif40_covariates c 
              WHERE ic.[geography]=c.[GEOGRAPHY] and 
			  c.geolevel_name=ic.study_geolevel_name 
			  AND c.[min]<ic.[min])
------------------------end of tests--------------------------------------