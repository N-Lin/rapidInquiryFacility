/****** trigger for [dbo].[T_RIF40_INV_COVARIATES]

Check - USERNAME exists.
Check - USERNAME is Kerberos USER on INSERT.
Check - UPDATE not allowed.
Check - DELETE only allowed on own records.

Check - study_geolevel_name. NOT FK YET in this table should it be ?
Check - Covariates  : Oracle code - IF c1%NOTFOUND THEN need clarification 
a) MIN and MAX.  
b) Limits 
c) Check access to covariate table, <covariate name> column exists
d) Check score.

 ******/


 insert into [dbo].[T_RIF40_INV_COVARIATES]
 (INV_ID, STUDY_ID, COVARIATE_NAME, USERNAME, GEOGRAPHY, STUDY_GEOLEVEL_NAME, MIN, MAX)
 values 
 (1,534,'IMD04EDUCAT_QUI23','PETERH@PRIVATE.NET','EW01','SOA2001',1,1),
 (1,534,'IMD04EDUCAT_QUI24','PETERH@PRIVATE.NET','EW01','SOA2001',.1,1)



 SELECT * FROM [T_RIF40_INV_COVARIATES] where COVARIATE_NAME='IMD04EDUCAT_QUIN3'


 -------------------------
 -- create trigger code 
 --------------------------
 create alter trigger tr_inv_covariate
 on [dbo].[T_RIF40_INV_COVARIATES]
 for insert, update 
 as
 begin 
 -- if min< expected 
	 DECLARE @min nvarchar(MAX) =
		(
		SELECT 
        cast(ic.[min] as varchar(20))+ ' '
		FROM inserted ic 
		where EXISTS (SELECT 1 FROM [dbo].rif40_covariates c 
              WHERE ic.[geography]=c.[GEOGRAPHY] and 
			  c.geolevel_name=ic.study_geolevel_name and
			  --c.covariate_name=ic.covariate_name and -- can comment it out to test 
			  Ic.[min]<c.[min])
        FOR XML PATH('')
		 );

	IF @min IS NOT NULL
		BEGIN
			RAISERROR('MIN value is less than expected: %s', 16, 1, @min) with log;
		END;
-- if max> expected : NOT WORKING . may be should uncomment covariate name
 DECLARE @max nvarchar(MAX) =
		(
		SELECT 
        cast(ic.[max] as varchar(20))+ ' '
		FROM inserted ic 
		where EXISTS (SELECT 1 FROM [dbo].rif40_covariates c 
              WHERE ic.[geography]=c.[GEOGRAPHY] and 
			  c.geolevel_name=ic.study_geolevel_name and
			 -- c.covariate_name=ic.covariate_name and -- can comment it out to test 
			  ic.[max]>c.[max])
        FOR XML PATH('')
		 );

	IF @max IS NOT NULL
		BEGIN
			RAISERROR('MAX value is greater than expected: %s', 16, 1, @max) with log; 
		END;

--Remove when supported
-- Type 2
 DECLARE @TYPE nvarchar(MAX) =
		(
		SELECT 
		concat(ic.COVARIATE_NAME,',', ic.study_geolevel_name)+ '  '
		FROM inserted  ic 
		where EXISTS (SELECT 1 FROM [dbo].rif40_covariates c 
              WHERE ic.[geography]=c.[GEOGRAPHY] and 
			  c.geolevel_name=ic.study_geolevel_name and
			 c.covariate_name=ic.covariate_name and -- can comment it out to test 
			  c.type=2)
        FOR XML PATH('')
		 );

	IF @type IS NOT NULL
		BEGIN
			RAISERROR('Type 2 not supported for : %s', 16, 1, @type) with log;
		END;

-- MAX is not an integer
DECLARE @max2 NVARCHAR(MAX)= 

( 
 SELECT cast(ic.max as varchar(20)), ':' ,covariate_name + '  '
 FROM   inserted  ic
 WHERE  round(ic.max,0)<>ic.max and  EXISTS (SELECT 1 FROM [dbo].rif40_covariates c 
              WHERE ic.[geography]=c.[GEOGRAPHY] and 
			  c.geolevel_name=ic.study_geolevel_name and
			 --c.covariate_name=ic.covariate_name and -- can comment it out to test 
			  c.type=1)
 FOR XML PATH('') 
); 
IF @max2 IS NOT NULL 
	BEGIN 
		raiserror('RIF40_COVARIATES type = 1  and MAX is not an integer: %s',16,1,@max2); 
	END;


-- Min is not an integer
DECLARE @min2 NVARCHAR(MAX)= 
( 
 SELECT cast(ic.min as varchar(20)), ':' ,covariate_name + '  '
 FROM   inserted  ic
 WHERE  round(ic.min,0)<>ic.min and  EXISTS (SELECT 1 FROM [dbo].rif40_covariates c 
              WHERE ic.[geography]=c.[GEOGRAPHY] and 
			  c.geolevel_name=ic.study_geolevel_name and
			 --c.covariate_name=ic.covariate_name and -- can comment it out to test 
			  c.type=1)
 FOR XML PATH('') 
); 
IF @min2 IS NOT NULL 
	BEGIN 
		raiserror('RIF40_COVARIATES type = 1,MIN is not an integer: %s',16,1,@min2); 
	END;

--Type = 1 and min <0
DECLARE @min3 NVARCHAR(MAX)= 
( 
 SELECT cast(ic.min as varchar(20)), ':' ,covariate_name + '  '
 FROM   inserted  ic
 WHERE  EXISTS (SELECT 1 FROM [dbo].rif40_covariates c 
              WHERE ic.[geography]=c.[GEOGRAPHY] and 
			  c.geolevel_name=ic.study_geolevel_name and
			 --c.covariate_name=ic.covariate_name and -- can comment it out to test 
			  c.type=1 and ic.min<0)
 FOR XML PATH('') 
); 
IF @min3 IS NOT NULL 
	BEGIN 
		raiserror('RIF40_COVARIATES type = 1,MIN less than 0: %s',16,1,@min3); 
	END;
-- d) Check score: NEED CLARIFICATION 

end


-