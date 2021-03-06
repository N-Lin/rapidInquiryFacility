USE [RIF40]
GO
/****** Object:  Table [dbo].[T_RIF40_PROJECTS]    Script Date: 19/09/2014 12:07:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[T_RIF40_PROJECTS](
	[PROJECT] [varchar](30) NOT NULL,
	[DESCRIPTION] [varchar](250) NOT NULL,
	[DATE_STARTED] [datetime2](0) NOT NULL,
	[DATE_ENDED] [datetime2](0) NULL,
 CONSTRAINT [T_RIF40_PROJECTS_PK] PRIMARY KEY CLUSTERED 
(
	[PROJECT] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
ALTER TABLE [dbo].[T_RIF40_PROJECTS] ADD  DEFAULT (sysdatetime()) FOR [DATE_STARTED]
GO
ALTER TABLE [dbo].[T_RIF40_PROJECTS]  WITH NOCHECK ADD  CONSTRAINT [T_RIF40_PROJECTS_DATE_CK] CHECK  (([DATE_ENDED] IS NULL OR [DATE_ENDED]>=[DATE_STARTED]))
GO
ALTER TABLE [dbo].[T_RIF40_PROJECTS] CHECK CONSTRAINT [T_RIF40_PROJECTS_DATE_CK]
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_PROJECTS.PROJECT' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_PROJECTS', @level2type=N'COLUMN',@level2name=N'PROJECT'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_PROJECTS.DESCRIPTION' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_PROJECTS', @level2type=N'COLUMN',@level2name=N'DESCRIPTION'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_PROJECTS.DATE_STARTED' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_PROJECTS', @level2type=N'COLUMN',@level2name=N'DATE_STARTED'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_PROJECTS.DATE_ENDED' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_PROJECTS', @level2type=N'COLUMN',@level2name=N'DATE_ENDED'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_PROJECTS' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_PROJECTS'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_PROJECTS.T_RIF40_PROJECTS_PK' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_PROJECTS', @level2type=N'CONSTRAINT',@level2name=N'T_RIF40_PROJECTS_PK'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_PROJECTS.T_RIF40_PROJECTS_DATE_CK' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_PROJECTS', @level2type=N'CONSTRAINT',@level2name=N'T_RIF40_PROJECTS_DATE_CK'
GO
