USE [RIF40]
GO
/****** Object:  Table [dbo].[T_RIF40_RESULTS]    Script Date: 19/09/2014 12:07:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[T_RIF40_RESULTS](
	[USERNAME] [varchar](90) NOT NULL,
	[STUDY_ID] [numeric](8, 0) NOT NULL,
	[INV_ID] [numeric](8, 0) NOT NULL,
	[BAND_ID] [numeric](8, 0) NOT NULL,
	[GENDERS] [numeric](1, 0) NOT NULL,
	[ADJUSTED] [numeric](1, 0) NOT NULL,
	[DIRECT_STANDARDISATION] [numeric](1, 0) NOT NULL,
	[OBSERVED] [numeric](38, 6) NULL,
	[EXPECTED] [numeric](38, 6) NULL,
	[LOWER95] [numeric](38, 6) NULL,
	[UPPER95] [numeric](38, 6) NULL,
	[RELATIVE_RISK] [numeric](38, 6) NULL,
	[SMOOTHED_RELATIVE_RISK] [numeric](38, 6) NULL,
	[POSTERIOR_PROBABILITY] [numeric](38, 6) NULL,
	[POSTERIOR_PROBABILITY_LOWER95] [numeric](38, 6) NULL,
	[POSTERIOR_PROBABILITY_UPPER95] [numeric](38, 6) NULL,
	[RESIDUAL_RELATIVE_RISK] [numeric](38, 6) NULL,
	[RESIDUAL_RR_LOWER95] [numeric](38, 6) NULL,
	[RESIDUAL_RR_UPPER95] [numeric](38, 6) NULL,
	[SMOOTHED_SMR] [numeric](38, 6) NULL,
	[SMOOTHED_SMR_LOWER95] [numeric](38, 6) NULL,
	[SMOOTHED_SMR_UPPER95] [numeric](38, 6) NULL,
 CONSTRAINT [T_RIF40_RESULTS_PK] PRIMARY KEY CLUSTERED 
(
	[STUDY_ID] ASC,
	[BAND_ID] ASC,
	[INV_ID] ASC,
	[GENDERS] ASC,
	[ADJUSTED] ASC,
	[DIRECT_STANDARDISATION] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
ALTER TABLE [dbo].[T_RIF40_RESULTS] ADD  DEFAULT (user_name()) FOR [USERNAME]
GO
ALTER TABLE [dbo].[T_RIF40_RESULTS]  WITH NOCHECK ADD  CONSTRAINT [T_RIF40_RESULTS_STUDY_ID_FK] FOREIGN KEY([STUDY_ID], [INV_ID])
REFERENCES [dbo].[T_RIF40_INVESTIGATIONS] ([STUDY_ID], [INV_ID])
GO
ALTER TABLE [dbo].[T_RIF40_RESULTS] CHECK CONSTRAINT [T_RIF40_RESULTS_STUDY_ID_FK]
GO
ALTER TABLE [dbo].[T_RIF40_RESULTS]  WITH CHECK ADD  CONSTRAINT [T_RIF40_RES_ADJUSTED_CK] CHECK  (([ADJUSTED]>=(0) AND [ADJUSTED]<=(1)))
GO
ALTER TABLE [dbo].[T_RIF40_RESULTS] CHECK CONSTRAINT [T_RIF40_RES_ADJUSTED_CK]
GO
ALTER TABLE [dbo].[T_RIF40_RESULTS]  WITH CHECK ADD  CONSTRAINT [T_RIF40_RES_DIR_STAND_CK] CHECK  (([DIRECT_STANDARDISATION]>=(0) AND [DIRECT_STANDARDISATION]<=(1)))
GO
ALTER TABLE [dbo].[T_RIF40_RESULTS] CHECK CONSTRAINT [T_RIF40_RES_DIR_STAND_CK]
GO
ALTER TABLE [dbo].[T_RIF40_RESULTS]  WITH CHECK ADD  CONSTRAINT [T_RIF40_RESULTS_GENDERS_CK] CHECK  (([GENDERS]>=(1) AND [GENDERS]<=(3)))
GO
ALTER TABLE [dbo].[T_RIF40_RESULTS] CHECK CONSTRAINT [T_RIF40_RESULTS_GENDERS_CK]
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.USERNAME' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'USERNAME'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.STUDY_ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'STUDY_ID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.INV_ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'INV_ID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.BAND_ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'BAND_ID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.GENDERS' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'GENDERS'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.ADJUSTED' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'ADJUSTED'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.DIRECT_STANDARDISATION' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'DIRECT_STANDARDISATION'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.OBSERVED' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'OBSERVED'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.EXPECTED' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'EXPECTED'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.LOWER95' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'LOWER95'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.UPPER95' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'UPPER95'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.RELATIVE_RISK' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'RELATIVE_RISK'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.SMOOTHED_RELATIVE_RISK' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'SMOOTHED_RELATIVE_RISK'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.POSTERIOR_PROBABILITY' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'POSTERIOR_PROBABILITY'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.POSTERIOR_PROBABILITY_LOWER95' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'POSTERIOR_PROBABILITY_LOWER95'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.POSTERIOR_PROBABILITY_UPPER95' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'POSTERIOR_PROBABILITY_UPPER95'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.RESIDUAL_RELATIVE_RISK' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'RESIDUAL_RELATIVE_RISK'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.RESIDUAL_RR_LOWER95' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'RESIDUAL_RR_LOWER95'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.RESIDUAL_RR_UPPER95' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'RESIDUAL_RR_UPPER95'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.SMOOTHED_SMR' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'SMOOTHED_SMR'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.SMOOTHED_SMR_LOWER95' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'SMOOTHED_SMR_LOWER95'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.SMOOTHED_SMR_UPPER95' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'COLUMN',@level2name=N'SMOOTHED_SMR_UPPER95'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.T_RIF40_RESULTS_PK' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'CONSTRAINT',@level2name=N'T_RIF40_RESULTS_PK'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.T_RIF40_RES_ADJUSTED_CK' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'CONSTRAINT',@level2name=N'T_RIF40_RES_ADJUSTED_CK'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.T_RIF40_RES_DIR_STAND_CK' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'CONSTRAINT',@level2name=N'T_RIF40_RES_DIR_STAND_CK'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.T_RIF40_RESULTS.T_RIF40_RESULTS_GENDERS_CK' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'T_RIF40_RESULTS', @level2type=N'CONSTRAINT',@level2name=N'T_RIF40_RESULTS_GENDERS_CK'
GO
