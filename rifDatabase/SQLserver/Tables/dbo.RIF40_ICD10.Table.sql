USE [RIF40]
GO
/****** Object:  Table [dbo].[RIF40_ICD10]    Script Date: 19/09/2014 12:07:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RIF40_ICD10](
	[ICD10_1CHAR] [varchar](20) NULL,
	[ICD10_3CHAR] [varchar](3) NULL,
	[ICD10_4CHAR] [varchar](4) NOT NULL,
	[TEXT_1CHAR] [varchar](250) NULL,
	[TEXT_3CHAR] [varchar](250) NULL,
	[TEXT_4CHAR] [varchar](250) NULL,
 CONSTRAINT [RIF40_ICD10_PK] PRIMARY KEY CLUSTERED 
(
	[ICD10_4CHAR] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_ICD10.ICD10_1CHAR' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_ICD10', @level2type=N'COLUMN',@level2name=N'ICD10_1CHAR'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_ICD10.ICD10_3CHAR' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_ICD10', @level2type=N'COLUMN',@level2name=N'ICD10_3CHAR'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_ICD10.ICD10_4CHAR' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_ICD10', @level2type=N'COLUMN',@level2name=N'ICD10_4CHAR'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_ICD10.TEXT_1CHAR' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_ICD10', @level2type=N'COLUMN',@level2name=N'TEXT_1CHAR'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_ICD10.TEXT_3CHAR' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_ICD10', @level2type=N'COLUMN',@level2name=N'TEXT_3CHAR'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_ICD10.TEXT_4CHAR' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_ICD10', @level2type=N'COLUMN',@level2name=N'TEXT_4CHAR'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_ICD10' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_ICD10'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_ICD10.RIF40_ICD10_PK' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_ICD10', @level2type=N'CONSTRAINT',@level2name=N'RIF40_ICD10_PK'
GO
