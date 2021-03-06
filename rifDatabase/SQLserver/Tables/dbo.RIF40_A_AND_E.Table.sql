USE [RIF40]
GO
/****** Object:  Table [dbo].[RIF40_A_AND_E]    Script Date: 19/09/2014 12:07:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RIF40_A_AND_E](
	[A_AND_E_3CHAR] [varchar](4) NOT NULL,
	[TEXT_3CHAR] [varchar](200) NULL,
 CONSTRAINT [RIF40_A_AND_E_PK] PRIMARY KEY CLUSTERED 
(
	[A_AND_E_3CHAR] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_A_AND_E.A_AND_E_3CHAR' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_A_AND_E', @level2type=N'COLUMN',@level2name=N'A_AND_E_3CHAR'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_A_AND_E.TEXT_3CHAR' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_A_AND_E', @level2type=N'COLUMN',@level2name=N'TEXT_3CHAR'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_A_AND_E' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_A_AND_E'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_A_AND_E.RIF40_A_AND_E_PK' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_A_AND_E', @level2type=N'CONSTRAINT',@level2name=N'RIF40_A_AND_E_PK'
GO
