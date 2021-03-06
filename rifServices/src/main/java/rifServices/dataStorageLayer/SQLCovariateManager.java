package rifServices.dataStorageLayer;

import rifGenericLibrary.dataStorageLayer.SQLRecordExistsQueryFormatter;
import rifGenericLibrary.dataStorageLayer.SQLSelectQueryFormatter;
import rifServices.businessConceptLayer.AbstractCovariate;
import rifServices.businessConceptLayer.AdjustableCovariate;
import rifServices.businessConceptLayer.CovariateType;
import rifServices.businessConceptLayer.DiseaseMappingStudy;
import rifServices.businessConceptLayer.Geography;
import rifServices.businessConceptLayer.GeoLevelSelect;
import rifServices.businessConceptLayer.GeoLevelToMap;
import rifServices.businessConceptLayer.Investigation;
import rifServices.businessConceptLayer.User;
import rifServices.system.RIFServiceException;
import rifServices.system.RIFServiceError;
import rifServices.system.RIFServiceMessages;
import rifServices.system.RIFDatabaseProperties;
import rifServices.util.RIFLogger;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



/**
 *
 *
 * <hr>
 * The Rapid Inquiry Facility (RIF) is an automated tool devised by SAHSU 
 * that rapidly addresses epidemiological and public health questions using 
 * routinely collected health and population data and generates standardised 
 * rates and relative risks for any given health outcome, for specified age 
 * and year ranges, for any given geographical area.
 *
 * <p>
 * Copyright 2014 Imperial College London, developed by the Small Area
 * Health Statistics Unit. The work of the Small Area Health Statistics Unit 
 * is funded by the Public Health England as part of the MRC-PHE Centre for 
 * Environment and Health. Funding for this project has also been received 
 * from the United States Centers for Disease Control and Prevention.  
 * </p>
 *
 * <pre> 
 * This file is part of the Rapid Inquiry Facility (RIF) project.
 * RIF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RIF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RIF. If not, see <http://www.gnu.org/licenses/>; or write 
 * to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
 * Boston, MA 02110-1301 USA
 * </pre>
 *
 * <hr>
 * Kevin Garwood
 * @author kgarwood
 * @version
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

final class SQLCovariateManager 
	extends AbstractSQLManager {

	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================
	/** The sql rif context manager. */
	private SQLRIFContextManager sqlRIFContextManager;
	
	// ==========================================
	// Section Construction
	// ==========================================

	/**
	 * Instantiates a new SQL covariate manager.
	 *
	 * @param sqlRIFContextManager the sql rif context manager
	 */
	public SQLCovariateManager(
		final RIFDatabaseProperties rifDatabaseProperties,
		final SQLRIFContextManager sqlRIFContextManager) {
		
		super(rifDatabaseProperties);
		this.sqlRIFContextManager = sqlRIFContextManager;
	}

	// ==========================================
	// Section Accessors and Mutators
	// ==========================================
	
	/**
	 * Gets the covariates for investigation.
	 *
	 * @param connection the connection
	 * @param user the user
	 * @param diseaseMappingStudy the disease mapping study
	 * @param investigation the investigation
	 * @return the covariates for investigation
	 * @throws RIFServiceException the RIF service exception
	 */
	public ArrayList<AbstractCovariate> getCovariatesForInvestigation(
		final Connection connection,
		final User user,
		final DiseaseMappingStudy diseaseMappingStudy,
		final Investigation investigation) 
		throws RIFServiceException {
		
		SQLSelectQueryFormatter queryFormatter 
			= new SQLSelectQueryFormatter();
		configureQueryFormatterForDB(queryFormatter);		
		queryFormatter.addSelectField("covariate_name");
		queryFormatter.addSelectField("min");
		queryFormatter.addSelectField("max");
		queryFormatter.addFromTable("t_rif40_inv_covariates");
		queryFormatter.addWhereParameter("inv_id");
		queryFormatter.addWhereParameter("study_id");
				
		logSQLQuery(
			"getCovariatesForInvestigation",
			queryFormatter,
			investigation.getIdentifier(),
			diseaseMappingStudy.getIdentifier());
										
		ArrayList<AbstractCovariate> results 
			= new ArrayList<AbstractCovariate>();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement
				= connection.prepareStatement(queryFormatter.generateQuery());
			Integer investigationID
				= Integer.valueOf(investigation.getIdentifier());
			statement.setInt(1, investigationID);
			Integer studyID
				= Integer.valueOf(diseaseMappingStudy.getIdentifier());
			statement.setInt(2, studyID);
			resultSet
				= statement.executeQuery();
			while (resultSet.next()) {
				String name = resultSet.getString(1);
				Double minimumValue = resultSet.getDouble(2);
				Double maximumValue = resultSet.getDouble(3);
				
				//TODO: KLG find out where we can find out what type the variable is
				CovariateType covariateType = CovariateType.CONTINUOUS_VARIABLE;
				AdjustableCovariate adjustableCovariate
					= AdjustableCovariate.newInstance(
						name, 
						String.valueOf(minimumValue), 
						String.valueOf(maximumValue), 
						covariateType);	
				results.add(adjustableCovariate);
			}
			return results;
		}
		catch(SQLException exception) {
			logSQLException(exception);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"covariateManager.db.unableToGetCovariatesForInvestigation",
					diseaseMappingStudy.getDisplayName(),
					investigation.getDisplayName());
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.GET_COVARIATES_FOR_INVESTIGATION, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources			
			SQLQueryUtility.close(statement);
			SQLQueryUtility.close(resultSet);
		}	
	}
		
	/**
	 * Gets the covariates.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param geoLevelSelect the geo level select
	 * @param geoLevelToMap the geo level to map
	 * @return the covariates
	 * @throws RIFServiceException the RIF service exception
	 */
	public ArrayList<AbstractCovariate> getCovariates(
		final Connection connection,
		final Geography geography,
		final GeoLevelToMap geoLevelToMap)
		throws RIFServiceException {
				
		
		//Validate parameters
		validateCommonMethodParameters(
			connection,
			geography,
			null,
			geoLevelToMap);

		//Create SQL query		
		SQLSelectQueryFormatter queryFormatter = new SQLSelectQueryFormatter();
		configureQueryFormatterForDB(queryFormatter);		
		queryFormatter.addSelectField("covariate_name");
		queryFormatter.addSelectField("min");
		queryFormatter.addSelectField("max");
		queryFormatter.addSelectField("type");
		queryFormatter.addFromTable("rif40_covariates");
		queryFormatter.addWhereParameter("geography");
		queryFormatter.addWhereParameter("geolevel_name");
		queryFormatter.addOrderByCondition("covariate_name");
		
		logSQLQuery(
			"getCovariates",
			queryFormatter,
			geography.getName(),
			geoLevelToMap.getName());
		
		//Parameterise and execute query
		ArrayList<AbstractCovariate> results = new ArrayList<AbstractCovariate>();
		PreparedStatement statement = null;
		ResultSet dbResultSet = null;
				
		try {
			statement
				= connection.prepareStatement(queryFormatter.generateQuery());
			statement.setString(1, geography.getName());
			statement.setString(2, geoLevelToMap.getName());

			dbResultSet = statement.executeQuery();
			while (dbResultSet.next()) {				
				AdjustableCovariate adjustableCovariate
					= AdjustableCovariate.newInstance();
				adjustableCovariate.setName(dbResultSet.getString(1).toLowerCase());
				double minimumValue = dbResultSet.getDouble(2);
				double maximumValue = dbResultSet.getDouble(3);
				adjustableCovariate.setMinimumValue(String.valueOf(minimumValue));
				adjustableCovariate.setMaximumValue(String.valueOf(maximumValue));				
				if ((minimumValue == 0) && (maximumValue == 1)) {
					adjustableCovariate.setCovariateType(CovariateType.BINARY_INTEGER_SCORE);								
				}
				else if (((minimumValue == 1) && (maximumValue == 3)) || ((minimumValue == 1) && (maximumValue == 5)) ) {
					//KLG: TODO - fix this, the logic for identifying ntile is not strong enough
					adjustableCovariate.setCovariateType(CovariateType.NTILE_INTEGER_SCORE);		
				}
				else {
					//it must be continuous, which is currently not supported
					adjustableCovariate.setCovariateType(CovariateType.CONTINUOUS_VARIABLE);
				}				
				
				results.add(adjustableCovariate);
			}		
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version						
			logSQLException(sqlException);
			String errorMessage
				= RIFServiceMessages.getMessage("covariateManager.db.unableToGetCovariates");

			RIFLogger rifLogger = RIFLogger.getLogger();
			rifLogger.error(
				SQLCovariateManager.class, 
				errorMessage, 
				sqlException);
			
			RIFServiceException rifServiceException
				= new RIFServiceException(RIFServiceError.GET_COVARIATES, errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources			
			SQLQueryUtility.close(statement);
			SQLQueryUtility.close(dbResultSet);
		}		
		return results;
	}
	
	// ==========================================
	// Section Errors and Validation
	// ==========================================
	/**
	 * Validate common method parameters.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param geoLevelSelect the geo level select
	 * @param geoLevelToMap the geo level to map
	 * @throws RIFServiceException the RIF service exception
	 */
	private void validateCommonMethodParameters(
		final Connection connection,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelToMap geoLevelToMap) 
		throws RIFServiceException {

		if (geography != null) {
			geography.checkErrors();
			sqlRIFContextManager.checkGeographyExists(
				connection, 
				geography.getName());			
		}
		
		if (geoLevelSelect != null) {
			geoLevelSelect.checkErrors();
			sqlRIFContextManager.checkGeoLevelSelectExists(
				connection,
				geography.getName(), 
				geoLevelSelect.getName());			
		}
		
		if (geoLevelToMap != null) {
			
			geoLevelToMap.checkErrors();			
			if (geoLevelSelect == null) {				
				sqlRIFContextManager.checkGeoLevelToMapOrViewValueExists(
					connection,
					geography.getName(),
					geoLevelToMap.getName(),
					true);
			}
			else {
				sqlRIFContextManager.checkGeoLevelToMapOrViewValueExists(
					connection,
					geography.getName(),
					geoLevelSelect.getName(),
					geoLevelToMap.getName(),
					true);				
			}
			
		}	
	}
	
	
	public void checkNonExistentCovariates(
		final Connection connection,
		final ArrayList<AbstractCovariate> covariates)
		throws RIFServiceException {
				
		SQLRecordExistsQueryFormatter queryFormatter
			= new SQLRecordExistsQueryFormatter();
		configureQueryFormatterForDB(queryFormatter);		
		queryFormatter.setFromTable("rif40_covariates");
		queryFormatter.setLookupKeyFieldName("covariate_name");

		
		logSQLQuery(
			"checkNonExistentCovariates - example query",
			queryFormatter,
			covariates.get(0).getName());
				
		PreparedStatement statement = null;
		ResultSet resultSet = null;		
		AbstractCovariate currentCovariate = null;
		try {
			statement 
				= connection.prepareStatement(queryFormatter.generateQuery());
			
			for (AbstractCovariate covariate : covariates) {
				statement.setString(1, covariate.getName());
				resultSet = statement.executeQuery();
				if (resultSet.next() == false) {
					String recordType = covariate.getRecordType();
					String errorMessage
						= RIFServiceMessages.getMessage(
							"general.validation.nonExistentRecord",
							recordType,
							covariate.getDisplayName());

					RIFServiceException rifServiceException
						= new RIFServiceException(
							RIFServiceError.NON_EXISTENT_COVARIATE, 
							errorMessage);
					
					SQLQueryUtility.close(statement);
					SQLQueryUtility.close(resultSet);
					
					throw rifServiceException;				
				}
			}
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version						
			logSQLException(sqlException);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"general.validation.unableCheckNonExistentRecord",
					currentCovariate.getRecordType(),
					currentCovariate.getDisplayName());

			RIFLogger rifLogger = RIFLogger.getLogger();
			rifLogger.error(
				SQLCovariateManager.class, 
				errorMessage, 
				sqlException);										
			
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;			
		}
		finally {
			SQLQueryUtility.close(statement);
			SQLQueryUtility.close(resultSet);
		}
		
	}

	// ==========================================
	// Section Interfaces
	// ==========================================

	// ==========================================
	// Section Override
	// ==========================================
}
