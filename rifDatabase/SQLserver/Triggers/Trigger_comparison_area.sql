/****** trigger  [dbo].[T_RIF40_COMPARISON_AREAS]


check are id ? need claification on this 

 ******/

 	SELECT COUNT(*) total
		  FROM t_rif40_results
		   having COUNT(*) =0


SELECT TOP 1000 [USERNAME]
      ,[STUDY_ID]
      ,[AREA_ID]
      ,[ROWID]
  FROM [RIF40].[dbo].[T_RIF40_COMPARISON_AREAS]

	