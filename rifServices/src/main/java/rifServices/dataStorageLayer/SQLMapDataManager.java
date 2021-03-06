package rifServices.dataStorageLayer;

import rifGenericLibrary.dataStorageLayer.SQLFunctionCallerQueryFormatter;
import rifGenericLibrary.dataStorageLayer.SQLGeneralQueryFormatter;
import rifGenericLibrary.dataStorageLayer.SQLRecordExistsQueryFormatter;
import rifGenericLibrary.dataStorageLayer.SQLSelectQueryFormatter;
import rifServices.businessConceptLayer.GeoLevelArea;
import rifServices.businessConceptLayer.GeoLevelSelect;
import rifServices.businessConceptLayer.GeoLevelView;
import rifServices.businessConceptLayer.GeoLevelToMap;
import rifServices.businessConceptLayer.Geography;
import rifServices.businessConceptLayer.MapArea;
import rifServices.businessConceptLayer.MapAreaSummaryData;
import rifServices.businessConceptLayer.BoundaryRectangle;
import rifServices.businessConceptLayer.User;
import rifServices.system.RIFServiceError;
import rifServices.system.RIFServiceException;
import rifServices.system.RIFServiceMessages;
import rifServices.system.RIFServiceStartupOptions;
import rifServices.util.FieldValidationUtility;
import rifServices.util.RIFLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;


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

final class SQLMapDataManager 
	extends AbstractSQLManager {

	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================
	/** The rif service startup options. */
	private RIFServiceStartupOptions rifServiceStartupOptions;
	
	/** The sql rif context manager. */
	private SQLRIFContextManager sqlRIFContextManager;
	
	/** The point from map identifier. */
	private HashMap<String, Point> pointFromMapIdentifier;
	
	// ==========================================
	// Section Construction
	// ==========================================

	/**
	 * Instantiates a new SQL map data manager.
	 *
	 * @param rifServiceStartupOptions the rif service startup options
	 * @param sqlRIFContextManager the sql rif context manager
	 */
	public SQLMapDataManager(
		final RIFServiceStartupOptions rifServiceStartupOptions,
		final SQLRIFContextManager sqlRIFContextManager) {

		super(rifServiceStartupOptions.getRIFDatabaseProperties());
		this.rifServiceStartupOptions = rifServiceStartupOptions;
		this.sqlRIFContextManager = sqlRIFContextManager;
		
		pointFromMapIdentifier = new HashMap<String, Point>();
	}

	// ==========================================
	// Section Accessors and Mutators///////
	// ==========================================?//

	/**
	 * Gets the map area summary information.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param geoLevelSelect the geo level select
	 * @param geoLevelArea the geo level area
	 * @param geoLevelToMap the geo level to map
	 * @param mapAreas the map areas
	 * @return the map area summary information
	 * @throws RIFServiceException the RIF service exception
	 */
	public MapAreaSummaryData getMapAreaSummaryInformation (
		final Connection connection,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelArea geoLevelArea,
		final GeoLevelToMap geoLevelToMap,
		final ArrayList<MapArea> mapAreas) throws RIFServiceException {

		validateCommonMethodParameters(
			connection,
			geography,
			geoLevelSelect,
			geoLevelArea,
			geoLevelToMap,
			mapAreas);	
		
		//TODO: Add in operations to compute total area and total population
		MapAreaSummaryData result
			= MapAreaSummaryData.newInstance();
		result.setTotalViewAreas(mapAreas.size());
		
		return result;
	}
		
	/**
	 * Gets the summary data for extent areas.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param geoLevelSelect the geo level select
	 * @param geoLevelArea the geo level area
	 * @param geoLevelToMap the geo level to map
	 * @return the summary data for extent areas
	 * @throws RIFServiceException the RIF service exception
	 */
	public MapAreaSummaryData getSummaryDataForExtentAreas (
		final Connection connection,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelArea geoLevelArea,
		final GeoLevelToMap geoLevelToMap) 
		throws RIFServiceException {

		//Validate parameters
		validateCommonMethodParameters(
			connection,
			geography,
			geoLevelSelect,
			geoLevelArea,
			geoLevelToMap,
			null);	
		
		//Step I: Obtain the lookup table associated with the resolution
		//of geoLevelSelect (which is also the one used by geoLevelArea)
		String geoLevelSelectTableName
			= getGeoLevelLookupTableName(
				connection,
				geography,
				geoLevelSelect.getName());

		//Step II: Obtain the lookup table associated with the resolution
		//of geoLevelToMap
		String geoLevelToMapTableName
			= getGeoLevelLookupTableName(
				connection,
				geography,
				geoLevelToMap.getName());
		
		//Step III: Obtain the hierarchy table associated with this geography
		String hierarchyTableName
			=  getGeographyHierarchyTableName(connection, geography); 
				
		//Step IV: Assemble the query needed to extract the map areas
		//of resolution geoLevelToMap but restricted to only those 
		//having the specified geoLevelArea
		
		
		//SQLCountQueryFormatter queryFormatter = new SQLCountQueryFormatter();
		//queryFormatter.setCountField(countField);

		/*
		StringBuilder countMapAreasForExtentQuery = new StringBuilder();
		countMapAreasForExtentQuery.append("SELECT ");
		countMapAreasForExtentQuery.append("COUNT(");		
		countMapAreasForExtentQuery.append(geoLevelToMapTableName);
		countMapAreasForExtentQuery.append(".");
		countMapAreasForExtentQuery.append(geoLevelToMap.getName());
		countMapAreasForExtentQuery.append(") ");		
		countMapAreasForExtentQuery.append("FROM ");
		countMapAreasForExtentQuery.append(geoLevelSelectTableName);		
		countMapAreasForExtentQuery.append(",");
		countMapAreasForExtentQuery.append(hierarchyTableName);		
		countMapAreasForExtentQuery.append(",");
		countMapAreasForExtentQuery.append(geoLevelToMapTableName);
		countMapAreasForExtentQuery.append(" WHERE ");
		countMapAreasForExtentQuery.append(geoLevelToMapTableName);
		countMapAreasForExtentQuery.append(".");
		countMapAreasForExtentQuery.append(geoLevelToMap.getName());
		countMapAreasForExtentQuery.append("=");
		countMapAreasForExtentQuery.append(hierarchyTableName);		
		countMapAreasForExtentQuery.append(".");
		countMapAreasForExtentQuery.append(geoLevelToMap.getName());
		countMapAreasForExtentQuery.append(" AND ");
		countMapAreasForExtentQuery.append(geoLevelSelectTableName);		
		countMapAreasForExtentQuery.append(".");
		countMapAreasForExtentQuery.append(geoLevelSelect.getName());
		countMapAreasForExtentQuery.append("=");
		countMapAreasForExtentQuery.append(hierarchyTableName);		
		countMapAreasForExtentQuery.append(".");
		countMapAreasForExtentQuery.append(geoLevelSelect.getName());
		countMapAreasForExtentQuery.append(" AND ");
		countMapAreasForExtentQuery.append(geoLevelSelectTableName);		
		countMapAreasForExtentQuery.append(".");
		countMapAreasForExtentQuery.append("name");
		countMapAreasForExtentQuery.append("=?");
		*/
				
		SQLGeneralQueryFormatter queryFormatter
			= new SQLGeneralQueryFormatter();
		configureQueryFormatterForDB(queryFormatter);
		queryFormatter.addPaddedQueryLine(0, "SELECT");
		queryFormatter.addQueryPhrase(1, "COUNT(");
		queryFormatter.addQueryPhrase(geoLevelToMapTableName);
		queryFormatter.addQueryPhrase(".");
		queryFormatter.addQueryPhrase(geoLevelToMap.getName());
		queryFormatter.addQueryPhrase(")");
		queryFormatter.padAndFinishLine();
		
		queryFormatter.addPaddedQueryLine(0, "FROM");
		queryFormatter.addQueryPhrase(1, geoLevelSelectTableName);
		queryFormatter.addQueryPhrase(geoLevelSelectTableName);
		queryFormatter.addQueryPhrase(",");
		queryFormatter.padAndFinishLine();		
		queryFormatter.addQueryPhrase(1, hierarchyTableName);		
		queryFormatter.addQueryPhrase(",");		
		queryFormatter.padAndFinishLine();		
		queryFormatter.addQueryPhrase(1, geoLevelToMapTableName);		
		queryFormatter.padAndFinishLine();		
		
		queryFormatter.addPaddedQueryLine(0, "WHERE");
		queryFormatter.addQueryPhrase(1, geoLevelToMapTableName);
		queryFormatter.addQueryPhrase(".");
		queryFormatter.addQueryPhrase(geoLevelToMap.getName());
		queryFormatter.addQueryPhrase("=");
		queryFormatter.addQueryPhrase(hierarchyTableName);		
		queryFormatter.addQueryPhrase(".");
		queryFormatter.addQueryPhrase(geoLevelToMap.getName());
		queryFormatter.addQueryPhrase(" AND");
		queryFormatter.padAndFinishLine();		
		
		queryFormatter.addQueryPhrase(1, geoLevelSelectTableName);		
		queryFormatter.addQueryPhrase(".");
		queryFormatter.addQueryPhrase(geoLevelSelect.getName());
		queryFormatter.addQueryPhrase("=");
		queryFormatter.addQueryPhrase(hierarchyTableName);		
		queryFormatter.addQueryPhrase(".");
		queryFormatter.addQueryPhrase(geoLevelSelect.getName());
		queryFormatter.addQueryPhrase(" AND");
		queryFormatter.padAndFinishLine();		

		queryFormatter.addQueryPhrase(1, geoLevelSelectTableName);		
		queryFormatter.addQueryPhrase(".");
		queryFormatter.addQueryPhrase("name");
		queryFormatter.addQueryPhrase("=?");
		queryFormatter.finishLine();
				
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Integer numberOfAreas = null;
		try {
			statement = connection.prepareStatement(
				queryFormatter.generateQuery());
			statement.setString(1, geoLevelArea.getName());
			resultSet = statement.executeQuery();			
			resultSet.next();			
			numberOfAreas = resultSet.getInt(1);
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version			
			logSQLException(sqlException);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToGetAreaCount",
					geography.getName(),
					geoLevelToMap.getName());
			
			RIFLogger rifLogger = RIFLogger.getLogger();
			rifLogger.error(
				SQLMapDataManager.class, 
				errorMessage, 
				sqlException);
			
			RIFServiceException rifServiceException
				= new RIFServiceException(RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources			
			SQLQueryUtility.close(statement);
			SQLQueryUtility.close(resultSet);			
		}
		
		MapAreaSummaryData result = MapAreaSummaryData.newInstance();
		result.setTotalViewAreas(numberOfAreas);
		
		return result;		
	}
	
	public ArrayList<MapArea> getMapAreasForBoundaryRectangle(
			final Connection connection,
			final Geography geography,
			final GeoLevelSelect geoLevelSelect,
			final BoundaryRectangle boundaryRectangle) 
			throws RIFServiceException {

		//Validate parameters
		validateCommonMethodParameters(
			connection,
			geography,
			geoLevelSelect,
			null,
			null,
			null);	
		
		boundaryRectangle.checkErrors();
		
		SQLFunctionCallerQueryFormatter queryFormatter = new SQLFunctionCallerQueryFormatter();
		configureQueryFormatterForDB(queryFormatter);
		queryFormatter.setSchema("rif40_xml_pkg");
		queryFormatter.setFunctionName("rif40_getMapAreasForBoundaryRectangle");
		queryFormatter.setNumberOfFunctionParameters(3);
				
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ArrayList<MapArea> results = new ArrayList<MapArea>();
		try {
			statement = connection.prepareStatement(queryFormatter.generateQuery());
			statement.setString(1, geography.getName());
			statement.setString(2, geoLevelSelect.getName());
			statement.setFloat(3, (float) boundaryRectangle.getYMax());
			statement.setFloat(4, (float) boundaryRectangle.getXMax());
			statement.setFloat(5, (float) boundaryRectangle.getYMin());
			statement.setFloat(6, (float) boundaryRectangle.getXMin());
			
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				String geographicalIdentifier = resultSet.getString(1);
				String identifier = resultSet.getString(2);
				String label = resultSet.getString(3);
				
				MapArea mapArea 
					= MapArea.newInstance(
						geographicalIdentifier,
						identifier,
						label);
				
				results.add(mapArea);
			}
			
			//@TODO: Ideally we'd like to put this first before we iterate
			//through all the results.  Normally I would use resultSet.last(),
			//followed by getRow() but this requires a scrollable result set.
			//For now we have this but perhaps in future we need a separate
			//method for indicating whether the count is too high
			checkNumberMapAreaResultsBelowThreshold(results.size());				
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version			
			logSQLException(sqlException);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToGetMapAreasForBoundaryRectangle",
					geography.getDisplayName(),
					geoLevelSelect.getDisplayName(),
					boundaryRectangle.getDisplayName());
			
			RIFLogger rifLogger = RIFLogger.getLogger();
			rifLogger.error(
				SQLMapDataManager.class, 
				errorMessage, 
				sqlException);
								
			RIFServiceException rifServiceException
				= new RIFServiceException(RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources			
			SQLQueryUtility.close(statement);
			SQLQueryUtility.close(resultSet);			
		}
		
		return results;		
	}
	
	/**
	 * Gets the map areas.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param geoLevelSelect the geo level select
	 * @param geoLevelArea the geo level area
	 * @param geoLevelToMap the geo level to map
	 * @return the map areas
	 * @throws RIFServiceException the RIF service exception
	 */
	public ArrayList<MapArea> getMapAreas(
		final Connection connection,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelArea geoLevelArea,
		final GeoLevelToMap geoLevelToMap) 
		throws RIFServiceException {
		
		//Validate parameters
		validateCommonMethodParameters(
			connection,
			geography,
			geoLevelSelect,
			geoLevelArea,
			geoLevelToMap,
			null);	

		//Step I: Obtain the lookup table associated with the resolution
		//of geoLevelSelect (which is also the one used by geoLevelArea)
		String geoLevelSelectTableName
			= getGeoLevelLookupTableName(
				connection,
				geography,
				geoLevelSelect.getName());

		//Step II: Obtain the lookup table associated with the resolution
		//of geoLevelToMap
		String geoLevelToMapTableName
			= getGeoLevelLookupTableName(
				connection,
				geography,
				geoLevelToMap.getName());
		
		//Step III: Obtain the hierarchy table associated with this geography
		String hierarchyTableName
			=  getGeographyHierarchyTableName(connection, geography); 
		
				
		//Step IV: Assemble the query needed to extract the map areas
		//of resolution geoLevelToMap but restricted to only those 
		//having the specified geoLevelArea
		
		SQLGeneralQueryFormatter extractMapAreasQuery
			= new SQLGeneralQueryFormatter();
		configureQueryFormatterForDB(extractMapAreasQuery);
			
		extractMapAreasQuery.addPaddedQueryLine(0, "SELECT DISTINCT");
		extractMapAreasQuery.addQueryPhrase(1, geoLevelToMapTableName);		
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase(geoLevelToMap.getName());
		extractMapAreasQuery.addQueryPhrase(",");
		extractMapAreasQuery.finishLine();
		
		extractMapAreasQuery.addQueryPhrase(1, geoLevelToMapTableName);
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase("name");
		extractMapAreasQuery.padAndFinishLine();
		extractMapAreasQuery.addPaddedQueryLine(0, "FROM");
		extractMapAreasQuery.addQueryPhrase(1, geoLevelSelectTableName);		
		extractMapAreasQuery.addQueryPhrase(",");
		extractMapAreasQuery.finishLine();		
		extractMapAreasQuery.addQueryPhrase(1, hierarchyTableName);		
		extractMapAreasQuery.addQueryPhrase(",");
		extractMapAreasQuery.finishLine();
		extractMapAreasQuery.addPaddedQueryLine(1, geoLevelToMapTableName);		

		extractMapAreasQuery.addPaddedQueryLine(0, "WHERE");
		
		extractMapAreasQuery.addQueryPhrase(1, geoLevelToMapTableName);		
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase(geoLevelToMap.getName());
		extractMapAreasQuery.addQueryPhrase("=");
		extractMapAreasQuery.addQueryPhrase(hierarchyTableName);		
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase(geoLevelToMap.getName());
		extractMapAreasQuery.addQueryPhrase(" AND");
		extractMapAreasQuery.padAndFinishLine();
		
		extractMapAreasQuery.addQueryPhrase(1, geoLevelSelectTableName);		
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase(geoLevelSelect.getName());
		extractMapAreasQuery.addQueryPhrase("=");
		extractMapAreasQuery.addQueryPhrase(hierarchyTableName);		
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase(geoLevelSelect.getName());
		extractMapAreasQuery.addQueryPhrase(" AND");
		extractMapAreasQuery.padAndFinishLine();
				
		extractMapAreasQuery.addQueryPhrase(1, geoLevelSelectTableName);		
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase("name");
		extractMapAreasQuery.addQueryPhrase("=?");
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ArrayList<MapArea> results = new ArrayList<MapArea>();
		try {
			statement = connection.prepareStatement(extractMapAreasQuery.generateQuery());
			statement.setString(1, geoLevelArea.getName());
			resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				MapArea mapArea = MapArea.newInstance();
				String identifier = resultSet.getString(1);
				if (identifier != null) {
					//KLG:  @TODO change this when db supports geographical identifier
					mapArea.setGeographicalIdentifier(identifier);
					mapArea.setIdentifier(identifier);					
				}

				String label = resultSet.getString(2);
				if (label != null) {
					mapArea.setLabel(label);
				}
				results.add(mapArea);
			}

			//@TODO: Ideally we'd like to put this first before we iterate
			//through all the results.  Normally I would use resultSet.last(),
			//followed by getRow() but this requires a scrollable result set.
			//For now we have this but perhaps in future we need a separate
			//method for indicating whether the count is too high
			checkNumberMapAreaResultsBelowThreshold(results.size());	
			
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version
			sqlException.printStackTrace(System.out);
			logSQLException(sqlException);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToGetMapAreas",
					geography.getName(),
					geoLevelToMap.getName());
			
			RIFLogger rifLogger = RIFLogger.getLogger();
			rifLogger.error(
				SQLMapDataManager.class, 
				errorMessage, 
				sqlException);
								
			RIFServiceException rifServiceException
				= new RIFServiceException(RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources			
			SQLQueryUtility.close(statement);
			SQLQueryUtility.close(resultSet);			
		}
		
		return results;		
	}
	
	

	/**
	 * Gets the map areas.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param geoLevelSelect the geo level select
	 * @param geoLevelArea the geo level area
	 * @param geoLevelToMap the geo level to map
	 * @param startIndex the start index
	 * @param endIndex the end index
	 * @return the map areas
	 * @throws RIFServiceException the RIF service exception
	 */
	public ArrayList<MapArea> getMapAreas(
		final Connection connection,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelArea geoLevelArea,
		final GeoLevelToMap geoLevelToMap,
		final Integer startIndex,
		final Integer endIndex) 
		throws RIFServiceException {

		//Validate parameters
		validateCommonMethodParameters(
			connection,
			geography,
			geoLevelSelect,
			geoLevelArea,
			geoLevelToMap,
			null);	
		
		FieldValidationUtility.hasDifferentNullity(startIndex, endIndex);
		
		if (startIndex > endIndex) {
			//start index cannot be greater than end index
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.mapAreaStartIndexMoreThanEndIndex",
					String.valueOf(startIndex),
					String.valueOf(endIndex));
			RIFServiceException rifServiceException
				 = new RIFServiceException(
					RIFServiceError.MAP_AREA_START_INDEX_MORE_THAN_END_INDEX, 
					errorMessage);
			throw rifServiceException;
		}
		
		//Step I: Obtain the lookup table associated with the resolution
		//of geoLevelSelect (which is also the one used by geoLevelArea)
		String geoLevelSelectTableName
			= getGeoLevelLookupTableName(
				connection,
				geography,
				geoLevelSelect.getName());

		//Step II: Obtain the lookup table associated with the resolution
		//of geoLevelToMap
		String geoLevelToMapTableName
			= getGeoLevelLookupTableName(
				connection,
				geography,
				geoLevelToMap.getName());
		
		//Step III: Obtain the hierarchy table associated with this geography
		String hierarchyTableName
			=  getGeographyHierarchyTableName(connection, geography); 
		
				
		//Step IV: Assemble the query needed to extract the map areas
		//of resolution geoLevelToMap but restricted to only those 
		//having the specified geoLevelArea
		
		SQLGeneralQueryFormatter extractMapAreasQuery = new SQLGeneralQueryFormatter();
		configureQueryFormatterForDB(extractMapAreasQuery);
		//extractMapAreasQuery.addQueryPhrase("WITH ordered_results AS (");
		//StringBuilder extractMapAreasQuery = new StringBuilder();

		extractMapAreasQuery.addQueryLine(0, "WITH ordered_results AS ( ");
		extractMapAreasQuery.addQueryLine(1, "SELECT row_number() ");
		extractMapAreasQuery.addQueryPhrase(2, "OVER(ORDER BY ");
		extractMapAreasQuery.addQueryPhrase(geoLevelToMapTableName);
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase(geoLevelToMap.getName());
		extractMapAreasQuery.addQueryPhrase(" ASC,");
		extractMapAreasQuery.finishLine();
		
		extractMapAreasQuery.addQueryPhrase(2, geoLevelToMapTableName);
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase("name");
		extractMapAreasQuery.addQueryPhrase(" ASC) AS row,");	
		extractMapAreasQuery.finishLine();

		extractMapAreasQuery.addQueryPhrase(2, geoLevelToMapTableName);
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase(geoLevelToMap.getName());
		extractMapAreasQuery.addQueryPhrase(" AS identifier,");
		extractMapAreasQuery.finishLine();
				
		extractMapAreasQuery.addQueryPhrase(2, geoLevelToMapTableName);
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase("name");
		extractMapAreasQuery.padAndFinishLine();
		
		extractMapAreasQuery.addQueryPhrase(1, "FROM");
		extractMapAreasQuery.padAndFinishLine();
		extractMapAreasQuery.addQueryPhrase(2, geoLevelSelectTableName);		
		extractMapAreasQuery.addQueryPhrase(",");
		extractMapAreasQuery.finishLine();
		
		extractMapAreasQuery.addQueryPhrase(2, hierarchyTableName);		
		extractMapAreasQuery.addQueryPhrase(",");
		extractMapAreasQuery.finishLine();
		
		extractMapAreasQuery.addQueryPhrase(2, geoLevelToMapTableName);
		extractMapAreasQuery.finishLine();
		
		extractMapAreasQuery.addQueryPhrase(1, "WHERE");
		extractMapAreasQuery.padAndFinishLine();

		extractMapAreasQuery.addQueryPhrase(2, geoLevelToMapTableName);
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase(geoLevelToMap.getName());
		extractMapAreasQuery.addQueryPhrase("=");
		extractMapAreasQuery.addQueryPhrase(hierarchyTableName);		
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase(geoLevelToMap.getName());
		extractMapAreasQuery.addQueryPhrase(" AND");
		extractMapAreasQuery.padAndFinishLine();
		
		extractMapAreasQuery.addQueryPhrase(2, geoLevelSelectTableName);		
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase(geoLevelSelect.getName());
		extractMapAreasQuery.addQueryPhrase("=");
		extractMapAreasQuery.addQueryPhrase(hierarchyTableName);		
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase(geoLevelSelect.getName());
		extractMapAreasQuery.addQueryPhrase(" AND");
		extractMapAreasQuery.padAndFinishLine();
		
		extractMapAreasQuery.addQueryPhrase(2, geoLevelSelectTableName);		
		extractMapAreasQuery.addQueryPhrase(".");
		extractMapAreasQuery.addQueryPhrase("name");
		extractMapAreasQuery.addQueryPhrase("=?");
		extractMapAreasQuery.addQueryPhrase(")");
		extractMapAreasQuery.padAndFinishLine();
		
		extractMapAreasQuery.addQueryPhrase(0, "SELECT DISTINCT");
		extractMapAreasQuery.padAndFinishLine();
		
		extractMapAreasQuery.addQueryLine(1, "identifier,");
		extractMapAreasQuery.addQueryPhrase(1, "name");
		extractMapAreasQuery.padAndFinishLine();

		extractMapAreasQuery.addQueryPhrase(0, "FROM");
		extractMapAreasQuery.padAndFinishLine();
		extractMapAreasQuery.addQueryPhrase(1, "ordered_results");
		extractMapAreasQuery.padAndFinishLine();

		extractMapAreasQuery.addQueryPhrase(0, "WHERE");
		extractMapAreasQuery.padAndFinishLine();

		extractMapAreasQuery.addQueryPhrase(1, "row >= ? AND");
		extractMapAreasQuery.padAndFinishLine();
		extractMapAreasQuery.addQueryPhrase(1, "row <= ?");
			
		logSQLQuery(
			"getMapAreas",
			extractMapAreasQuery,
			geoLevelArea.getName(),
			String.valueOf(startIndex),
			String.valueOf(endIndex));
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ArrayList<MapArea> results = new ArrayList<MapArea>();
		try {
			statement = connection.prepareStatement(extractMapAreasQuery.generateQuery());
			statement.setString(1, geoLevelArea.getName());
			statement.setInt(2, startIndex);
			statement.setInt(3, endIndex);
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				MapArea mapArea = MapArea.newInstance();
				String identifier = resultSet.getString(1);
				if (identifier != null) {
					//KLG: Later on, database should return these as separate things
					mapArea.setIdentifier(identifier);
					mapArea.setGeographicalIdentifier(identifier);
				}
				String label = resultSet.getString(2);
				if (label != null) {
					mapArea.setLabel(label);
				}
				results.add(mapArea);
			}	
			
			//@TODO: Ideally we'd like to put this first before we iterate
			//through all the results.  Normally I would use resultSet.last(),
			//followed by getRow() but this requires a scrollable result set.
			//For now we have this but perhaps in future we need a separate
			//method for indicating whether the count is too high
			checkNumberMapAreaResultsBelowThreshold(results.size());	
			
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version			
			logSQLException(sqlException);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToGetMapAreas",
					geography.getName(),
					geoLevelToMap.getName());
			
			RIFLogger rifLogger = RIFLogger.getLogger();
			rifLogger.error(
				SQLMapDataManager.class, 
				errorMessage, 
				sqlException);
								
			RIFServiceException rifServiceException
				= new RIFServiceException(RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources			
			SQLQueryUtility.close(statement);
			SQLQueryUtility.close(resultSet);			
		}
		
		return results;		
	}
	
	
	/**
	 * Gets the geo level lookup table name.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param resolutionLevel the resolution level
	 * @return the geo level lookup table name
	 * @throws RIFServiceException the RIF service exception
	 */
	private String getGeoLevelLookupTableName( 
		final Connection connection,
		final Geography geography,
		final String resolutionLevel) 
		throws RIFServiceException { 

		String geoLevelSelectTableName = null;
				
		SQLSelectQueryFormatter queryFormatter 
			= new SQLSelectQueryFormatter();
		configureQueryFormatterForDB(queryFormatter);
		queryFormatter.addSelectField("lookup_table");
		queryFormatter.addFromTable("rif40_geolevels");
		queryFormatter.addWhereParameter("geography");
		queryFormatter.addWhereParameter("geolevel_name");
		
		logSQLQuery(
			"getGeoLevelLookupTableName",
			queryFormatter,
			geography.getName(),
			resolutionLevel);
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.prepareStatement(queryFormatter.generateQuery());
			statement.setString(1, geography.getName());
			statement.setString(2, resolutionLevel);
			resultSet = statement.executeQuery();
	
			if (resultSet.next() == false) {
				//this method assumes that geoLevelSelect is valid
				//Therefore, it must be associated with a lookup table
				assert false;
			}			
			geoLevelSelectTableName
				= useAppropariateTableNameCase(resultSet.getString(1));
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version			
			logSQLException(sqlException);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToGetGeoLevelLookupTable",
					geography.getName(),
					resolutionLevel);
			
			RIFLogger rifLogger = RIFLogger.getLogger();
			rifLogger.error(
				SQLMapDataManager.class, 
				errorMessage, 
				sqlException);
									
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources			
			SQLQueryUtility.close(statement);
			SQLQueryUtility.close(resultSet);		
		}
		
		return geoLevelSelectTableName;
	}
	
	/**
	 * Gets the geography hierarchy table name.
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @return the geography hierarchy table name
	 * @throws RIFServiceException the RIF service exception
	 */
	private String getGeographyHierarchyTableName( 
		final Connection connection,
		final Geography geography) 
		throws RIFServiceException { 

		String geographyHierarchyTableName = null;
					
		SQLSelectQueryFormatter queryFormatter 
			= new SQLSelectQueryFormatter();
		configureQueryFormatterForDB(queryFormatter);
		queryFormatter.addSelectField("hierarchytable");
		queryFormatter.addFromTable("rif40_geographies");
		queryFormatter.addWhereParameter("geography");

		logSQLQuery(
				"getGeographyHierarchyTableName",
				queryFormatter,
				geography.getName());
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.prepareStatement(queryFormatter.generateQuery());
			statement.setString(1, geography.getName());
			resultSet = statement.executeQuery();
	
			if (resultSet.next() == false) {
				//this method assumes that geoLevelSelect is valid
				//Therefore, it must be associated with a lookup table
				assert false;
			}			
			geographyHierarchyTableName
				= useAppropariateTableNameCase(resultSet.getString(1));
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version			
			logSQLException(sqlException);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToGetHierarchyTable",
					geography.getName());

			RIFLogger rifLogger = RIFLogger.getLogger();
			rifLogger.error(
				SQLMapDataManager.class, 
				errorMessage, 
				sqlException);
											
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources			
			SQLQueryUtility.close(statement);
			SQLQueryUtility.close(resultSet);		
		}
			
		return geographyHierarchyTableName;
	}
	
	/**
	 * Gets the image.
	 *
	 * @param connection the connection
	 * @param geoLevelSelect the geo level select
	 * @param geoLevelArea the geo level area
	 * @param geoLevelView the geo level view
	 * @param mapAreas the map areas
	 * @return the image
	 * @throws RIFServiceException the RIF service exception
	 */
	public BufferedImage getImage(
		final Connection connection,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelArea geoLevelArea,
		final GeoLevelView geoLevelView,
		final ArrayList<MapArea> mapAreas) 
		throws RIFServiceException {
		
		BufferedImage result = null;
		try {
			File serverSideCacheDirectory
				= rifServiceStartupOptions.getServerSideCacheDirectory();
			StringBuilder imageFilePath
				= new StringBuilder();
			imageFilePath.append(serverSideCacheDirectory.getAbsolutePath());
			imageFilePath.append(File.separator);
			imageFilePath.append("mappamundilge.JPG");
			File imageFile = new File(imageFilePath.toString());			
			result = ImageIO.read(imageFile);
			for (MapArea mapArea : mapAreas) {
				drawMapArea(result, mapArea);				
			}
			//now colour dots	
		}
		catch(IOException ioException) {
			String errorMessage
				= RIFServiceMessages.getMessage("io.error.problemsReadingMapImageData");
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		return result;
	}
	
	/**
	 * Draw map area.
	 *
	 * @param bufferedImage the buffered image
	 * @param mapArea the map area
	 */
	private void drawMapArea(
		final BufferedImage bufferedImage,
		final MapArea mapArea) {
		
		Point pointForMapArea 
			= pointFromMapIdentifier.get(mapArea.getIdentifier());
		if (pointForMapArea == null) {			
			int imageWidth = bufferedImage.getWidth();
			int imageHeight = bufferedImage.getHeight();

			//assume that dimension of the map will always be at least 10
			//in each direction
			int randomX = (int) (Math.random() * (imageWidth - 10));
			int randomY = (int) (Math.random() * (imageHeight - 10));
			pointForMapArea = new Point(randomX, randomY);
		}
				
		Graphics2D graphics2D 
			= (Graphics2D) bufferedImage.getGraphics();
		graphics2D.setColor(Color.RED);
		graphics2D.fillOval(
			(int) pointForMapArea.getX(), 
			(int) pointForMapArea.getY(), 
			20, 
			20);
	}
	
	public String getGeometry(
		final Connection connection,
		final User user,
		final Geography geography,	
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelView geoLevelView,
		final ArrayList<MapArea> mapAreas) throws RIFServiceException {

		user.checkErrors();
		geography.checkErrors();
		geoLevelSelect.checkErrors();
		geoLevelView.checkErrors();

		if (mapAreas.size() == 0) {
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.noMapAreasSpecified");
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.NO_MAP_AREAS_SPECIFIED, 
					errorMessage);
			throw rifServiceException;
		}
		
		for (MapArea mapArea : mapAreas) {
			mapArea.checkErrors();
		}
		
		String geographyName = geography.getName();
		String geoLevelSelectName = geoLevelSelect.getName();
		
		sqlRIFContextManager.checkGeographyExists(
			connection, 
			geographyName);
		sqlRIFContextManager.checkGeoLevelSelectExists(
			connection,
			geographyName,
			geoLevelSelectName);
		sqlRIFContextManager.checkGeoLevelToMapOrViewValueExists(
			connection, 
			geographyName, 
			geoLevelSelectName, 
			geoLevelView.getName(), 
			false);
		
		checkAreasExist(
			connection,
			geography.getName(),
			geoLevelSelect.getName(),
			mapAreas);
				
		String result = "";
		
		SQLFunctionCallerQueryFormatter queryFormatter
			= new SQLFunctionCallerQueryFormatter();
		configureQueryFormatterForDB(queryFormatter);
		queryFormatter.setSchema("rif40_xml_pkg");
		queryFormatter.setFunctionName("rif40_get_geojson_as_js");
		queryFormatter.setNumberOfFunctionParameters(5);

		//@TODO: the DB query needs to be altered to support an array of 
		//map areas
		String[] mapAreaIdentifiers = MapArea.getMapAreaIdentifierList(mapAreas);

		logSQLQuery(
			"getGeometry",
			queryFormatter,
			geography.getName(),
			geoLevelView.getName(),
			geoLevelSelect.getName(),
			mapAreaIdentifiers[0],
			String.valueOf(false));				
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement 
				= connection.prepareStatement(queryFormatter.generateQuery());
			statement.setString(1, geography.getName());
			statement.setString(2, geoLevelView.getName());
			statement.setString(3, geoLevelSelect.getName());
			statement.setString(4, mapAreaIdentifiers[0]);
			statement.setBoolean(5, false);		
			resultSet = statement.executeQuery();
			resultSet.next();
			result = resultSet.getString(1);
		}
		catch(SQLException sqlException) {
			logSQLException(sqlException);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToGetGeometry",
					geography.getName(),
					geoLevelSelect.getName(),
					geoLevelView.getName());
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.DATABASE_QUERY_FAILED, 
					errorMessage);
			throw rifServiceException;
		}
		finally {
			//Cleanup database resources
			SQLQueryUtility.close(statement);
			SQLQueryUtility.close(resultSet);			
		}
		
		return result;
	}
	
	
	// ==========================================
	// Section Errors and Validation
	// ==========================================

	public void checkNumberMapAreaResultsBelowThreshold(
		final int numberOfMapAreasRetrieved) 
		throws RIFServiceException {

		int maximumMapAreasAllowedForSingleDisplay
			= rifServiceStartupOptions.getMaximumMapAreasAllowedForSingleDisplay();
		if (numberOfMapAreasRetrieved > maximumMapAreasAllowedForSingleDisplay) {
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.thresholdMapAreasPerDisplayExceeded",
					String.valueOf(numberOfMapAreasRetrieved),
					String.valueOf(maximumMapAreasAllowedForSingleDisplay));
			
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.THRESHOLD_MAP_AREAS_PER_DISPLAY_EXCEEDED, 
					errorMessage);
			throw rifServiceException;
		}
	}
	
	public void checkAreasExist(
		final Connection connection,
		final String geographyName,
		final String geoLevelName,
		final ArrayList<MapArea> mapAreas) 
		throws RIFServiceException {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try {
			
			//find the correct table corresponding to the geography
			//eg: SAHSULAND_GEOGRAPHY
			String geographyTableName
				= getGeographyTableName(
					connection, 
					geographyName);

			SQLRecordExistsQueryFormatter queryFormatter
				= new SQLRecordExistsQueryFormatter();
			configureQueryFormatterForDB(queryFormatter);
			queryFormatter.setFromTable(geographyTableName);
			queryFormatter.setLookupKeyFieldName(geoLevelName);
			
			logSQLQuery(
				"checkAreasExist -- example",
				queryFormatter,
				mapAreas.get(0).getIdentifier());
			
			statement = connection.prepareStatement(queryFormatter.generateQuery());
			ArrayList<String> errorMessages = new ArrayList<String>();
			for (MapArea mapArea : mapAreas) {
				statement.setString(1, mapArea.getIdentifier());
				resultSet = statement.executeQuery();
				
				if (resultSet.next() == false) {
					//no result, which means identifier was not found
					String errorMessage
						= RIFServiceMessages.getMessage(
							"sqlMapDataManager.error.nonExistentMapArea",
							mapArea.getIdentifier(),
							geographyName,
							geoLevelName);
					errorMessages.add(errorMessage);
				}
			}	
			
			if (errorMessages.size() > 0) {
				RIFServiceException rifServiceException
					= new RIFServiceException(
						RIFServiceError.NON_EXISTENT_MAP_AREA,
						errorMessages);
				throw rifServiceException;
			}
		}
		catch(SQLException sqlException) {
			//Record original exception, throw sanitised, human-readable version			
			logSQLException(sqlException);
			String errorMessage
				= RIFServiceMessages.getMessage(
					"sqlMapDataManager.error.unableToCheckMapAreasExists",
					geographyName,
					geoLevelName);
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

	/**
	 * Assumes that geography exists
	 * @param connection
	 * @param geography
	 * @return
	 * @throws SQLException
	 */
	private String getGeographyTableName(
		final Connection connection,
		final String geographyName) 
		throws SQLException,
		RIFServiceException {

		SQLSelectQueryFormatter queryFormatter
			= new SQLSelectQueryFormatter();
		configureQueryFormatterForDB(queryFormatter);
		queryFormatter.addSelectField("hierarchytable");
		queryFormatter.addFromTable("rif40_geographies");
		queryFormatter.addWhereParameter("geography");

		logSQLQuery(
			"getGeographyTableName",
			queryFormatter,
			geographyName);
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.prepareStatement(queryFormatter.generateQuery());
			statement.setString(1, geographyName);
			
			resultSet = statement.executeQuery();
			resultSet.next();
						
			return resultSet.getString(1);			
		}
		finally {
			SQLQueryUtility.close(statement);
			SQLQueryUtility.close(resultSet);
		}
		
		
	}
	
	/**
	 * This is a convenience method that tries to centralise
	 * much of the validation code that is used in the other methods
	 * From the point of view of the SQLMapDataManager, its caller,
	 * an implementation of a RIFJobSubmissionAPI service, has already
	 * checked whether the parameters are null.  Therefore, the optional
	 * checks for null in this method are merely meant to let the
	 * convenience routine validate parameters if they're relevant.
	 * For example, some
	 *
	 * @param connection the connection
	 * @param geography the geography
	 * @param geoLevelSelect the geo level select
	 * @param geoLevelArea the geo level area
	 * @param geoLevelToMap the geo level to map
	 * @param mapAreas the map areas
	 * @throws RIFServiceException the RIF service exception
	 */
	private void validateCommonMethodParameters(
		final Connection connection,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelArea geoLevelArea,
		final GeoLevelToMap geoLevelToMap,
		final ArrayList<MapArea> mapAreas) 
		throws RIFServiceException {
		
		//Validate parameters
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

		if (geoLevelArea != null) {
			geoLevelArea.checkErrors();	
			sqlRIFContextManager.checkGeoLevelAreaExists(
				connection,
				geography.getName(),
				geoLevelSelect.getName(),
				geoLevelArea.getName());
		}
		
		if (geoLevelToMap != null) {
			geoLevelToMap.checkErrors();			
			sqlRIFContextManager.checkGeoLevelToMapOrViewValueExists(
				connection, 
				geography.getName(), 
				geoLevelSelect.getName(), 
				geoLevelToMap.getName(),
				true);
		}
	}
	
	// ==========================================
	// Section Interfaces
	// ==========================================

	// ==========================================
	// Section Override
	// ==========================================
}
