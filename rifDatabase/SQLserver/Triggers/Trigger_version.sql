/****** trigger for version 

Check - single column, populate schema_amended. Prevent DELETE or INSERT 
NEED CLARIFICATION : if there are more than 1 row then no insertion or deletion ?
cant we do it with permissions ?

******/

SELECT [VERSION]
      ,[SCHEMA_CREATED]
      ,[SCHEMA_AMENDED]
      ,[CVS_REVISION]
      ,[ROWID]
  FROM [RIF40].[dbo].[RIF40_VERSION]


--------------------------------------
-- create trigger code ---------------
--------------------------------------

create alter  trigger [tr_version]
on [dbo].[RIF40_VERSION]
for insert , update ,delete 
As
Begin

-- Determine the type of transaction 
Declare  @xtype varchar(5)

	IF EXISTS (SELECT * FROM DELETED)
	BEGIN
		SET @XTYPE = 'D'
	END
	IF EXISTS (SELECT * FROM INSERTED)
		BEGIN
			IF (@XTYPE = 'D')
		BEGIN
			SET @XTYPE = 'U'
		END
	ELSE
		BEGIN
			SET @XTYPE = 'I'
		END
--When Transaction is an insert 
	IF (@XTYPE = 'I')
		BEGIN
		  if (SELECT COUNT(*) total FROM rif40_version) > 0 
		  begin 
			Raiserror ('Error: RIF40_VERSION INSERT disallowed', 16,1 );
			rollback tran
		  end 
		END
--When Transaction is a delete  
    IF (@XTYPE = 'D')
		begin
			raiserror( 'Error: RIF40_VERSION DELETE disallowed',16,1)
			rollback tran
		end 
   End 
 end 



  
  /*****
 --------------------------------------------------------------------------
 ---------------testing something -----------------------------------------
 --------------------------------------------------------------------------
  create alter  trigger tr_test 
  on [dbo].test
  for insert , update ,delete 
  as
  begin
  
	Declare  @xtype varchar(5)

		IF EXISTS (SELECT * FROM DELETED)
		BEGIN
			SET @XTYPE = 'D'
		END
		IF EXISTS (SELECT * FROM INSERTED)
			BEGIN
				IF (@XTYPE = 'D')
			BEGIN
			  SET @XTYPE = 'U'
			END
		ELSE
			 BEGIN
			  SET @XTYPE = 'I'
			 END
		END

  -- Transaction is a insert 
	IF (@XTYPE = 'I')
		BEGIN
		 declare @count int 
		 set @count= (select count(*) from inserted) 
		 print 'Inserting :' + ' ' + cast(@count as varchar(20)) +' Rows'
		END
	 --  Transaction is a Update 
	IF (@XTYPE = 'U')
		BEGIN
		 print 'Updating not allowed'
		 rollback tran 
		END
	IF (@XTYPE = 'D')
		begin
		print 'DELETION allowed' 
		end 
 --print 'Test'
 end 

 --------------------------------------------------------------------------
 ---------------testing something -----------------------------------------
 --------------------------------------------------------------------------
 create table test (id int identity, name varchar(50))

 insert into test(name) values('rimpie'), ('Ontie'), ('Munta')

 update test set name='rim' where name = 'rimpie'

 delete test where name = 'rimpie'

 select * from test 

 

 if (select count(*) from test) >0
 begin 
 declare @cnt int =(select count(*) from test)
 print 'records in table' +' ' + cast(@cnt as varchar(10)) 
 end 
 else 
 print 'No records in Table TEST'


 *****/