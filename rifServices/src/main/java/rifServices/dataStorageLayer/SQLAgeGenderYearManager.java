package rifServices.dataStorageLayer;

import rifServices.businessConceptLayer.AgeGroup;
import rifServices.businessConceptLayer.AgeBand;
import rifServices.businessConceptLayer.Geography;
import rifServices.businessConceptLayer.RIFJobSubmissionAPI;
import rifServices.businessConceptLayer.NumeratorDenominatorPair;
import rifServices.businessConceptLayer.Sex;
import rifServices.businessConceptLayer.YearRange;
import rifServices.system.RIFServiceError;
import rifServices.system.RIFServiceException;
import rifServices.system.RIFServiceMessages;
import rifServices.util.RIFLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



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

public class SQLAgeGenderYearManager 
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
	 * Instantiates a new SQL age gender year manager.
	 *
	 * @param sqlRIFContextManager the sql rif context manager
	 */
	public SQLAgeGenderYearManager(
		final SQLRIFContextManager sqlRIFContextManager) {

		this.sqlRIFContextManager = sqlRIFContextManager;
	}

	// ==========================================
	// Section Accessors and Mutators
	// ==========================================

	/**
	 * Gets the age groups.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param ndPair the nd pair
	 * @param sortingOrder the sorting order
	 * @return the age groups
	 * @throws RIFServiceException the RIF service exception
	 */
	public ArrayList<AgeGroup> getAgeGroups(
		final Connection connection,
		final Geography geography,
		final NumeratorDenominatorPair ndPair,
		final RIFJobSubmissionAPI.AgeGroupSortingOption sortingOrder)
		throws RIFServiceException {
				
		//Validate parameters
		validateCommonMethodParameters(
			connection,
			geography,
			ndPair);
		
		//Create SQL queries
		Integer ageGroupID = null;
		SQLSelectQueryFormatter getAgeIDQuery = new SQLSelectQueryFormatter();
		getAgeIDQuery.addSelectField("age_group_id");
		getAgeIDQuery.addFromTable("rif40_tables");
		getAgeIDQuery.addWhereParameter("table_name");
		getAgeIDQuery.addWhereParameter("isnumerator");
		
		ArrayList<AgeGroup> results = new ArrayList<AgeGroup>();
		
		PreparedStatement getAgeIDStatement = null;
		ResultSet getAgeIDResultSet = null;		
		try {
			getAgeIDStatement
				= connection.prepareStatement(getAgeIDQuery.generateQuery());
			getAgeIDStatement.setString(1, ndPair.getNumeratorTableName());
			//set isnumerator flag to 'true'
			getAgeIDStatement.setInt(2, 1);
			getAgeIDResultSet = getAgeIDStatement.executeQuery();
			if (getAgeIDResultSet.next() == false) {
				//ERROR: No entry available
				String errorMessage
					= RIFServiceMessages.getMessage(
						"sqlAgeGenderYearManager.error.noAgeGroupIDForNumeratorTable",
						ndPair.getNumeratorTableDescription());
				RIFServiceException rifServiceException
					= new RIFServiceException(
						RIFServiceError.NO_AGE_GROUP_ID_FOR_NUMERATOR, 
						errorMessage);
				throw rifServiceException;
			}
			else {
				ageGroupID = getAgeIDResultSet.getInt(1);
			}
		}
		catch(SQLException sqlException) {
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlAgeGenderYearManager.error.unableToGetAgeGroupID",
					ndPair.getNumeratorTableDescription());

			RIFLogger rifLogger = new RIFLogger();
			rifLogger.error(
				SQLAgeGenderYearManager.class, 
				errorMessage, 
				sqlException);
			
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.DB_UNABLE_GET_AGE_GROUP_ID, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources			
			SQLQueryUtility.close(getAgeIDStatement);
			SQLQueryUtility.close(getAgeIDResultSet);			
		}

		if (ageGroupID == null) {
			return results;
		}
		
		//Step II: Obtain the list of age groups that are appropriate
		//for the age group ID associated with the numerator table
		//The age group id helps group together age groups based on different
		//needs.  "1" may represent the standard breakdown of age ranges.
		//"2" may represent age ranges that are broken down every 4 years
		//After obtaining the list of age groups having the correct age group id
		//sort them by low_age
		SQLSelectQueryFormatter formatter = new SQLSelectQueryFormatter();
		formatter.addSelectField("age_group_id");
		formatter.addSelectField("low_age");
		formatter.addSelectField("high_age");
		formatter.addSelectField("fieldname");
		formatter.addFromTable("rif40_age_groups");
		formatter.addWhereParameter("age_group_id");
		
		if ((sortingOrder == null) ||
			(sortingOrder == RIFJobSubmissionAPI.AgeGroupSortingOption.ASCENDING_LOWER_LIMIT)) {
			formatter.addOrderByCondition(
				"low_age", 
				SQLSelectQueryFormatter.SortOrder.ASCENDING);			
		}
		else if (sortingOrder == RIFJobSubmissionAPI.AgeGroupSortingOption.DESCENDING_LOWER_LIMIT) {
			formatter.addOrderByCondition(
				"low_age",
				SQLSelectQueryFormatter.SortOrder.DESCENDING);
		}
		else if (sortingOrder == RIFJobSubmissionAPI.AgeGroupSortingOption.ASCENDING_UPPER_LIMIT) {
			formatter.addOrderByCondition(
				"high_age",
				SQLSelectQueryFormatter.SortOrder.ASCENDING);		
		}
		else {
			//it must be descending lower limit.		
			formatter.addOrderByCondition(
				"high_age",
				SQLSelectQueryFormatter.SortOrder.DESCENDING);		
			assert sortingOrder == RIFJobSubmissionAPI.AgeGroupSortingOption.DESCENDING_UPPER_LIMIT;			
		}
		
		//Parameterise and execute query
		PreparedStatement statement = null;
		ResultSet dbResultSet = null;
		try {
			statement 
				= connection.prepareStatement(formatter.generateQuery());
			statement.setInt(1, ageGroupID);
			dbResultSet = statement.executeQuery();

			while (dbResultSet.next()) {
				AgeGroup ageGroup = AgeGroup.newInstance();
				ageGroup.setIdentifier(String.valueOf(dbResultSet.getInt(1)));
				ageGroup.setLowerLimit(String.valueOf(dbResultSet.getInt(2)));
				ageGroup.setUpperLimit(String.valueOf(dbResultSet.getInt(3)));
				ageGroup.setName(dbResultSet.getString(4));
				results.add(ageGroup);
			}
		}
		catch(SQLException sqlException) {
			String errorMessage
				= RIFServiceMessages.getMessage("ageGroup.error.unableToGetAgeGroups");

			RIFLogger rifLogger = new RIFLogger();
			rifLogger.error(
				SQLAgeGenderYearManager.class, 
				errorMessage, 
				sqlException);
						
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.UNABLE_GET_AGE_GROUPS,
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources			
			SQLQueryUtility.close(statement);
			SQLQueryUtility.close(dbResultSet);
		}
		return results;		
	}

	
	
	
	
	/**
	 * Gets the genders.
	 *
	 * @return the genders
	 * @throws RIFServiceException the RIF service exception
	 */
	public ArrayList<Sex> getGenders()
		throws RIFServiceException {
		
		//Perform operation
		ArrayList<Sex> results = new ArrayList<Sex>();
		results.add(Sex.MALES);
		results.add(Sex.FEMALES);
		results.add(Sex.BOTH);
		
		return results;		
	}
	
	/**
	 * Gets the year range.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param ndPair the nd pair
	 * @return the year range
	 * @throws RIFServiceException the RIF service exception
	 */
	public YearRange getYearRange(
		final Connection connection,
		final Geography geography,
		final NumeratorDenominatorPair ndPair) 
		throws RIFServiceException {
		
		//Validate parameters
		validateCommonMethodParameters(
			connection,
			geography,
			ndPair);
		
		//Create SQL query		
		SQLSelectQueryFormatter query = new SQLSelectQueryFormatter();
		query.addSelectField("year_start");
		query.addSelectField("year_stop");
		query.addFromTable("rif40_tables");
		query.addWhereParameter("table_name");
		
		YearRange result = null;
		PreparedStatement getYearRangeStatement = null;
		ResultSet getYearRangeResultSet = null;
		try {
			getYearRangeStatement = connection.prepareStatement(query.generateQuery());
			getYearRangeStatement.setString(1, ndPair.getNumeratorTableName());
			getYearRangeResultSet = getYearRangeStatement.executeQuery();
			
			//there should be exactly one result
			if (getYearRangeResultSet.next() == false) {
				//no entry found in the rif40 tables
				String errorMessage
					= RIFServiceMessages.getMessage(
						"sqlAgeGenderYearManager.error.noStartEndYearForNumeratorTable",
						ndPair.getNumeratorTableDescription());
				RIFServiceException rifServiceException
					= new RIFServiceException(
						RIFServiceError.NO_START_END_YEAR_FOR_NUMERATOR, 
						errorMessage);
				throw rifServiceException;
			}
			
			int yearStartValue = getYearRangeResultSet.getInt(1);
			int yearEndValue = getYearRangeResultSet.getInt(2);
			result 
				= YearRange.newInstance(
						String.valueOf(yearStartValue), 
						String.valueOf(yearEndValue));
		}
		catch(SQLException sqlException) {			
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlAgeGenderYearManager.error.unableToGetStartEndYear",
					ndPair.getDisplayName());
			
			RIFLogger rifLogger = new RIFLogger();
			rifLogger.error(
				SQLAgeGenderYearManager.class, 
				errorMessage, 
				sqlException);
						
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.DB_UNABLE_GET_START_END_YEAR, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources
			SQLQueryUtility.close(getYearRangeStatement);
			SQLQueryUtility.close(getYearRangeResultSet);			
		}
		return result;				
	}
	

	
	// ==========================================
	// Section Errors and Validation
	// ==========================================
	/**
	 * Validate common method parameters.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param ndPair the nd pair
	 * @throws RIFServiceException the RIF service exception
	 */
	private void validateCommonMethodParameters(
		final Connection connection,
		final Geography geography,
		final NumeratorDenominatorPair ndPair) 
		throws RIFServiceException {

		if (geography != null) {
			geography.checkErrors();
		
			sqlRIFContextManager.checkNonExistentGeography(
				connection, 
				geography);	
		}
		if (ndPair != null) {
			ndPair.checkErrors();
			sqlRIFContextManager.checkNonExistentNDPair(
				connection, 
				geography,
				ndPair);			
		}
	}

	public void checkNonExistentAgeGroups(
			Connection connection,
			ArrayList<AgeBand> ageBands) 
			throws RIFServiceException {
			
			for (AgeBand ageBand : ageBands) {
				AgeGroup lowerAgeGroup = ageBand.getLowerLimitAgeGroup();
				checkNonExistentAgeGroup(
					connection, 
					lowerAgeGroup);
				AgeGroup upperAgeGroup = ageBand.getUpperLimitAgeGroup();
				checkNonExistentAgeGroup(
					connection, 
					upperAgeGroup);
				
			}
			
			
		}
		
		private void checkNonExistentAgeGroup(
			Connection connection,
			AgeGroup ageGroup) 
			throws RIFServiceException {
			
			Integer id = Integer.valueOf(ageGroup.getIdentifier());
			
			SQLRecordExistsQueryFormatter ageGroupExistsQueryFormatter
				= new SQLRecordExistsQueryFormatter();
			ageGroupExistsQueryFormatter.setFromTable("rif40_age_groups");
			ageGroupExistsQueryFormatter.setLookupKeyFieldName("age_group_id");
			PreparedStatement statement = null;
			ResultSet resultSet = null;
			try {
				statement 
					= connection.prepareStatement(ageGroupExistsQueryFormatter.generateQuery());
				statement.setInt(1, id);
				
				resultSet = statement.executeQuery();		
				if (resultSet.next() == false) {

					//ERROR: no such age group exists
					String recordType
						= ageGroup.getRecordType();
					String errorMessage
						= RIFServiceMessages.getMessage(
							"general.validation.nonExistentRecord",
							recordType,
							ageGroup.getDisplayName());

					RIFServiceException rifServiceException
						= new RIFServiceException(
							RIFServiceError.NON_EXISTENT_AGE_GROUP, 
							errorMessage);
					throw rifServiceException;
				}		
			}
			catch(SQLException sqlException) {
				sqlException.printStackTrace(System.out);
				String errorMessage
					= RIFServiceMessages.getMessage(
						"general.validation.unableCheckNonExistentRecord",
						ageGroup.getRecordType(),
						ageGroup.getDisplayName());

				RIFLogger rifLogger = new RIFLogger();
				rifLogger.error(
					SQLAgeGenderYearManager.class, 
					errorMessage, 
					sqlException);										
					
				RIFServiceException rifServiceException
					= new RIFServiceException(
						RIFServiceError.DB_UNABLE_CHECK_NONEXISTENT_RECORD, 
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