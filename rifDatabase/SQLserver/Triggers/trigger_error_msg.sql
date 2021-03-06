/****** trigger for error messeges ******/
SELECT TOP 1000 [ERROR_CODE]
      ,[TAG]
      ,[TABLE_NAME]
      ,[CAUSE]
      ,[ACTION]
      ,[MESSAGE]
      ,[ROWID]
  FROM [RIF40].[dbo].[RIF40_ERROR_MESSAGES]



  --------------------------------
  -- create trigger code 
  --------------------------------
  create trigger tr_error_msg_checks
  on  [dbo].[RIF40_ERROR_MESSAGES]
  for insert , update 
  as
  begin
	 DECLARE @tablelist nvarchar(MAX) =
    (
    SELECT 
		[TABLE_NAME] + ', '
        FROM inserted
        WHERE OBJECT_ID([TABLE_NAME], 'U') IS NULL
        FOR XML PATH('')
    );

	IF @tablelist IS NOT NULL
	BEGIN
		RAISERROR('These table/s do not exist: %s', 16, 1, @tablelist) with log;
	END;
  
 end 