package rifDataLoaderTool.dataStorageLayer.postgresql;

import java.util.ArrayList;

import rifDataLoaderTool.businessConceptLayer.ConvertStepQueryGeneratorAPI;
import rifDataLoaderTool.businessConceptLayer.TableConversionConfiguration;
import rifDataLoaderTool.businessConceptLayer.TableFieldCleaningConfiguration;
import rifDataLoaderTool.businessConceptLayer.TableFieldConversionConfiguration;
import rifDataLoaderTool.system.RIFTemporaryTablePrefixes;
import rifGenericLibrary.dataStorageLayer.SQLGeneralQueryFormatter;

/**
 * Contains methods that generate Postgres-specific SQL code that supports
 * the conversion step.
 *
 * <hr>
 * Copyright 2014 Imperial College London, developed by the Small Area
 * Health Statistics Unit. 
 *
 * <pre> 
 * This file is part of the Rapid Inquiry Facility (RIF) project.
 * RIF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * RIF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RIF.  If not, see <http://www.gnu.org/licenses/>.
 * </pre>
 *
 * <hr>
 * Kevin Garwood
 * @author kgarwood
 */

/*
 * Code Road Map:
 * --------------
 * Code is organised into the following sections.  Wherever possible, 
 * methods are classified based on an order of precedence described in 
 * parentheses (..).  For example, if you're trying to find a method 
 * 'getName(...)' that is both an interface method and an accessor 
 * method, the order tells you it should appear under interface.
 * 
 * Order of 
 * Precedence     Section
 * ==========     ======
 * (1)            Section Constants
 * (2)            Section Properties
 * (3)            Section Construction
 * (7)            Section Accessors and Mutators
 * (6)            Section Errors and Validation
 * (5)            Section Interfaces
 * (4)            Section Override
 *
 */

public final class PostgresConvertStepQueryGenerator 
	implements ConvertStepQueryGeneratorAPI {

	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================

	// ==========================================
	// Section Construction
	// ==========================================

	public PostgresConvertStepQueryGenerator() {

	}

	// ==========================================
	// Section Accessors and Mutators
	// ==========================================

	
	
	public String generateConvertTableQuery(
		final TableConversionConfiguration tableConversionConfiguration) {

		
		/**
		 * CREATE TABLE convert_my_table2001 AS 
		 * SELECT
		 *    data_source_id,
		 *    row_number,
		 *    pst AS postal_code,
		 *    convert_age_sex(birth_date, event_date, sex) AS age_sex_group,
		 *    year AS year,
		 *    other_field1,
		 *    other_field2
		 * FROM
		 *    cln_cast_my_table2001 AS cleanedTableName;
		 * 
		 * 
		 */
		
		
		
		String coreTableName 
			= tableConversionConfiguration.getCoreTableName();
		String cleanedTableName
			= RIFTemporaryTablePrefixes.CLEAN_CASTING.getTableName(coreTableName);
		String convertedTableName
			= RIFTemporaryTablePrefixes.CONVERT.getTableName(coreTableName);
		
		SQLGeneralQueryFormatter queryFormatter = new SQLGeneralQueryFormatter();
		
		queryFormatter.addQueryPhrase(0, "CREATE TABLE ");
		queryFormatter.addQueryPhrase(convertedTableName);
		queryFormatter.addQueryPhrase(" AS");
		queryFormatter.padAndFinishLine();
		queryFormatter.addQueryPhrase(1, "SELECT");
		queryFormatter.padAndFinishLine();
		
		queryFormatter.addQueryLine(2, "data_source_id,");
		queryFormatter.addQueryLine(2, "row_number,");
				
		ArrayList<TableFieldConversionConfiguration> conversionFieldConfigurations
			= tableConversionConfiguration.getRequiredFieldConfigurations();
		for (TableFieldConversionConfiguration convertionFieldConfiguration : conversionFieldConfigurations) {
			
			addConvertQueryFragment(
				queryFormatter,
				2,
				tableConversionConfiguration,
				convertionFieldConfiguration);
		}
		//Now add on the extra fields
		
		ArrayList<TableFieldCleaningConfiguration> extraFields
			= tableConversionConfiguration.getExtraFields();
		for (TableFieldCleaningConfiguration extraField : extraFields) {
			queryFormatter.addQueryPhrase(",");
			queryFormatter.finishLine();			
			queryFormatter.addQueryPhrase(extraField.getCleanedTableFieldName());
		}
		
		queryFormatter.addQueryPhrase(0, "FROM");
		queryFormatter.padAndFinishLine();
		queryFormatter.addQueryPhrase(1, cleanedTableName);
				
		return queryFormatter.generateQuery();
	}
	
	private void addConvertQueryFragment(
		final SQLGeneralQueryFormatter queryFormatter,
		final int baseIndentationLevel,
		final TableConversionConfiguration tableConversionConfiguration,
		final TableFieldConversionConfiguration fieldConversionConfiguration) {
	
		/**
		 * cleanedTableName.postal_code AS postal_code,
		 * convert_age_sex(age, sex)
		 * 
		 * 
		 * 
		 * 
		 * 
		 */
		
		String conversionFunctionName
			= tableConversionConfiguration.getConversionFunctionName(
				fieldConversionConfiguration);
		ArrayList<TableFieldCleaningConfiguration> fieldConfigurations
			= tableConversionConfiguration.getCleaningConfigurations(fieldConversionConfiguration);

		if (conversionFunctionName == null) {
			//there is no function
			queryFormatter.addQueryPhrase(
				baseIndentationLevel,
				fieldConfigurations.get(0).getCleanedTableFieldName());
			queryFormatter.addQueryPhrase(" AS ");
			queryFormatter.addQueryPhrase(
				fieldConversionConfiguration.getConversionTableFieldName());
		}
		else {
			//there is a function
			queryFormatter.addQueryPhrase(
				baseIndentationLevel, 
				conversionFunctionName);
			queryFormatter.addQueryPhrase("(");
			queryFormatter.addQueryPhrase(
				concatenateFunctionParameters(
					fieldConfigurations));				
			queryFormatter.addQueryPhrase(") AS ");
			queryFormatter.addQueryPhrase(
				fieldConversionConfiguration.getConversionTableFieldName());
			queryFormatter.padAndFinishLine();
		}
	}

	private String concatenateFunctionParameters(
		final ArrayList<TableFieldCleaningConfiguration> fieldConfigurations) {
		
		StringBuilder buffer = new StringBuilder();

		int numberOfFieldConfigurations
			= fieldConfigurations.size();

		if (numberOfFieldConfigurations == 0) {
			buffer.append(fieldConfigurations.get(0).getCleanedTableFieldName());
		}
		else {
			for (int i = 0; i < numberOfFieldConfigurations; i++) {
				if (i != 0) {
					buffer.append(",");
				}
				buffer.append(fieldConfigurations.get(i).getCleanedTableFieldName());
			}
		}
		
		return buffer.toString();
	}
	
	
	// ==========================================
	// Section Errors and Validation
	// ==========================================

	// ==========================================
	// Section Interfaces
	// ==========================================

	// ==========================================
	// Section Override
	// ==========================================

}


