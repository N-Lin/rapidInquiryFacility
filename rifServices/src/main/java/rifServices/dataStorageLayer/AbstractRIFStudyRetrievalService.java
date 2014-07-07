package rifServices.dataStorageLayer;

import rifServices.businessConceptLayer.*;
import rifServices.system.*;
import rifServices.util.FieldValidationUtility;


import rifServices.util.RIFLogger;

import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Main implementation of the RIF middle ware.  
 * <p>
 * The main roles of this class are to support:
 * <ul>
 * <li>
 * use final parameters for all API methods to help minimise concurrency issues.
 * </li>
 * <li>
 * defensively copy method parameters to minimise concurrency and security issues
 * </li>
 * <li>
 * detect any empty method parameters
 * <li>
 * <li>
 * detect any security violations (eg: injection attacks)
 * </li>
 * </ul>
 *<p>
 *Most methods in the class perform the same sequence of steps:
 *<ol>
 *<li><b>Defensively copy parameter values<b>.  This step ensures that parameter values passed by the
 *client will not change any of their values while the method executes.  Defensive copying helps
 *minimise problems of multiple threads altering parameter values that are passed to the methods.  The
 *technique also helps reduce the likelihood of certain types of security attacks.
 *</li>
 *<li>
 *Ensure none of the required parameter values are empty.  By empty, we mean they are either null, or in the case
 *of Strings, null or an empty string.  This step helps minimise the effort the service has to spend recovering from
 *null pointer exceptions.
 *</li>
 *<li>
 *Scan every parameter value for malicious field values.  Sometimes this just means scanning a String parameter value
 *for text that could be used as part of a malicous code attack.  In this step, the <code>checkSecurityViolations(..)</code>
 *methods for business objects are called.  Note that the <code>checkSecurityViolations</code> method of a business
 *object will scan each text field for malicious field values, and recursively call the same method in any child
 *business objects it may possess.
 *</li>
 *<li>
 *Obtain a connection object from the {@link rifServices.dataStorageLayer.SLQConnectionManager}.  
 *</li>
 *<li>
 *Delegate to a manager class, using a method with the same name.  Pass the connection object as part of the call. 
 *</li>
 *<li>
 *Return the connection object to the connection pool.
 *<li>
 *
 *<h2>Security</h2>
 *The RIF software contains many different types of checks which are designed to detect or prevent malicious code
 *attacks.  Defensive copying prevents an attacker from trying to change parameter values sometime when the method
 *has not finished executing.  The use of PreparedStatement objects, and trigger checks that are part of the database
 *should eliminate the prospect of a malicious attack.  However, calls to the <code>checkSecurityViolations(..)</code>
 *methods are intended to identify an attack anyway.  We feel that we should not merely prevent malicious attacks but
 *log any attempts to do so.  If the method throws a {@link rifServices.businessConceptLayer.RIFServiceSecurityException},
 *then this class will log it and continue to pass it to client application.
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

abstract class AbstractRIFStudyRetrievalService 
	extends AbstractRIFService 
	implements RIFStudyResultRetrievalAPI {

	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================
	
	// ==========================================
	// Section Construction
	// ==========================================

	/**
	 * Instantiates a new production rif job submission service.
	 */
	public AbstractRIFStudyRetrievalService() {
		
	}

	
	// ==========================================
	// Section Accessors and Mutators
	// ==========================================
	

	
	public String getGeometry(
		final User _user,
		final Geography _geography,
		final GeoLevelSelect _geoLevelSelect,
		final GeoLevelToMap _geoLevelToMap,
		final ArrayList<MapArea> _mapAreas) throws RIFServiceException {

		//Part I: Defensively copy parameters
		User user = User.createCopy(_user);

		SQLConnectionManager sqlConnectionManager
			= rifServiceResources.getSqlConnectionManager();
		if (sqlConnectionManager.isUserBlocked(user) == true) {
			return null;
		}
		Geography geography = Geography.createCopy(_geography);
		GeoLevelSelect geoLevelSelect 
			= GeoLevelSelect.createCopy(_geoLevelSelect);
		GeoLevelToMap geoLevelToMap
			= GeoLevelToMap.createCopy(_geoLevelToMap);
		ArrayList<MapArea> mapAreas
			= MapArea.createCopy(_mapAreas);
		
		String result = "";
		try {
			
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getGeometry",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getGeometry",
				"geography",
				geography);
			fieldValidationUtility.checkNullMethodParameter(
				"getGeometry",
				"geoLevelSelect",
				geoLevelSelect);		
			fieldValidationUtility.checkNullMethodParameter(
				"getGeometry",
				"geoLevelToMap",
				geoLevelToMap);						
			fieldValidationUtility.checkNullMethodParameter(
				"getGeometry",
				"mapAreas",
				mapAreas);
				
			//Part III: Check for security violations
			validateUser(user);
			geography.checkSecurityViolations();
			geoLevelSelect.checkSecurityViolations();
			geoLevelToMap.checkSecurityViolations();
			
			for (MapArea mapArea : mapAreas) {
				mapArea.checkSecurityViolations();
			}

			//Part IV: Perform operation		
			//@TODO
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getGeometry",
				rifServiceException);	
		}
		
		return result;
	}
	
	
	//Calls to obtain geographical extents of areas
	public ArrayList<MapAreaAttributeValue> getMapAreaAttributeValues(
		final User _user,
		final Geography _geography,
		final GeoLevelSelect _geoLevelSelect,
		final String geoLevelAttribute)
		throws RIFServiceException {
			
		//Part I: Defensively copy parameters
		User user = User.createCopy(_user);
		SQLConnectionManager sqlConnectionManager
			= rifServiceResources.getSqlConnectionManager();		
		if (sqlConnectionManager.isUserBlocked(user) == true) {
			return null;
		}
		Geography geography = Geography.createCopy(_geography);
		GeoLevelSelect geoLevelSelect
			= GeoLevelSelect.createCopy(_geoLevelSelect);
		
		ArrayList<MapAreaAttributeValue> results
			= new ArrayList<MapAreaAttributeValue>();
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getMapAreaAttributeValues",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getMapAreaAttributeValues",
				"geography",
				geography);	
			fieldValidationUtility.checkNullMethodParameter(
				"getMapAreaAttributeValues",
				"getLevelSelect",
				geoLevelSelect);	
			fieldValidationUtility.checkNullMethodParameter(
				"getMapAreaAttributeValues",
				"geoLevelAttribute",
				geoLevelAttribute);	
		
			//Part III: Check for security violations
			validateUser(user);
			geography.checkSecurityViolations();
			geoLevelSelect.checkSecurityViolations();
			fieldValidationUtility.checkMaliciousMethodParameter(			
				"getMapAreaAttributeValues",
				"geoLevelAttribute",
				geoLevelAttribute);	

			RIFLogger rifLogger = new RIFLogger();				
			String auditTrailMessage
				= RIFServiceMessages.getMessage("logging.getMapAreaAttributeValues",
					user.getUserID(),
					user.getIPAddress(),
					geoLevelAttribute);
			rifLogger.info(
				AbstractRIFStudyRetrievalService.class,
				auditTrailMessage);
		
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();
			results
				= sqlResultsQueryManager.getMapAreaAttributeValues(
					connection,
					user,
					geography,
					geoLevelSelect,
					geoLevelAttribute);
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getMapAreaAttributeValues",
				rifServiceException);			
		}
		
		return results;
	}
	
	public ArrayList<GeoLevelAttributeSource> getGeoLevelAttributeSources(
		final User _user,
		final Geography _geography,
		final GeoLevelSelect _geoLevelSelect,
		final DiseaseMappingStudy _diseaseMappingStudy) 
		throws RIFServiceException {
		
		//Part I: Defensively copy parameters
		User user = User.createCopy(_user);
		SQLConnectionManager sqlConnectionManager
			= rifServiceResources.getSqlConnectionManager();
		if (sqlConnectionManager.isUserBlocked(user) == true) {
			return null;
		}
		Geography geography = Geography.createCopy(_geography);
		GeoLevelSelect geoLevelSelect
			= GeoLevelSelect.createCopy(_geoLevelSelect);
		DiseaseMappingStudy diseaseMappingStudy
			= DiseaseMappingStudy.createCopy(_diseaseMappingStudy);
		
		ArrayList<GeoLevelAttributeSource> results 
			= new ArrayList<GeoLevelAttributeSource>();
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelAttributeSources",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelAttributeSources",
				"geography",
				geography);	
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelAttributeSources",
				"geoLevelSelect",
				geoLevelSelect);	
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelAttributeSources",
				"diseaseMappingStudy",
				diseaseMappingStudy);	
			
			//Part III: Check for security violations
			validateUser(user);
			geography.checkSecurityViolations();
			geoLevelSelect.checkSecurityViolations();
			diseaseMappingStudy.checkSecurityViolations();
						
			RIFLogger rifLogger = new RIFLogger();				
			String auditTrailMessage
				= RIFServiceMessages.getMessage("logging.getMapAreaAttributeValues",
					user.getUserID(),
					user.getIPAddress(),
					diseaseMappingStudy.getDisplayName());
			rifLogger.info(
				AbstractRIFStudyRetrievalService.class,
				auditTrailMessage);
		
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);

			results
				= sqlResultsQueryManager.getGeoLevelAttributeSources(
					connection,
					user,
					geography,
					geoLevelSelect,
					diseaseMappingStudy); 
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getGeoLevelAttributeSources",
				rifServiceException);			
		}
		return results;
	}
	
	
	public ArrayList<GeoLevelAttributeTheme> getGeoLevelAttributeThemes(
		final User _user,
		final Geography _geography,
		final GeoLevelSelect _geoLevelSelect)
		throws RIFServiceException {
		
		//Part I: Defensively copy parameters
		User user = User.createCopy(_user);
		SQLConnectionManager sqlConnectionManager
			= rifServiceResources.getSqlConnectionManager();
		if (sqlConnectionManager.isUserBlocked(user) == true) {
			return null;
		}
		Geography geography = Geography.createCopy(_geography);
		GeoLevelSelect geoLevelSelect
			= GeoLevelSelect.createCopy(_geoLevelSelect);
		
		ArrayList<GeoLevelAttributeTheme> results 
			= new ArrayList<GeoLevelAttributeTheme>();
		
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getMapAreaAttributeValues",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getMapAreaAttributeValues",
				"geography",
				geography);	
			fieldValidationUtility.checkNullMethodParameter(
				"getMapAreaAttributeValues",
				"getLevelSelect",
				geoLevelSelect);	

			
			//Part III: Check for security violations
			validateUser(user);
			geography.checkSecurityViolations();
			geoLevelSelect.checkSecurityViolations();
						
			RIFLogger rifLogger = new RIFLogger();				
			String auditTrailMessage
				= RIFServiceMessages.getMessage("logging.getGeoLevelAttributeThemes",
					user.getUserID(),
					user.getIPAddress(),
					geography.getDisplayName(),
					geoLevelSelect.getDisplayName());
			rifLogger.info(
				AbstractRIFStudyRetrievalService.class,
				auditTrailMessage);
		
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();			
			results
				= sqlResultsQueryManager.getGeoLevelAttributeThemes(
					connection,
					user,
					geography,
					geoLevelSelect);
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getGeoLevelAttributeThemes",
				rifServiceException);						
		}
				
		return results;
	}

	public String[] getAllAttributesForGeoLevelAttributeTheme(
		final User _user,
		final Geography _geography,
		final GeoLevelSelect _geoLevelSelect,
		final DiseaseMappingStudy _diseaseMappingStudy,
		final GeoLevelAttributeSource _geoLevelAttributeSource,
		final GeoLevelAttributeTheme _geoLevelAttributeTheme)
		throws RIFServiceException {
			
		//Part I: Defensively copy parameters
		User user = User.createCopy(_user);
		SQLConnectionManager sqlConnectionManager
			= rifServiceResources.getSqlConnectionManager();
		if (sqlConnectionManager.isUserBlocked(user) == true) {
			return null;
		}
		Geography geography = Geography.createCopy(_geography);
		GeoLevelSelect geoLevelSelect
			= GeoLevelSelect.createCopy(_geoLevelSelect);
		DiseaseMappingStudy diseaseMappingStudy
			= DiseaseMappingStudy.createCopy(_diseaseMappingStudy);
		GeoLevelAttributeSource geoLevelAttributeSource
			= GeoLevelAttributeSource.createCopy(_geoLevelAttributeSource);
		GeoLevelAttributeTheme geoLevelAttributeTheme
			= GeoLevelAttributeTheme.createCopy(_geoLevelAttributeTheme);
			String[] results = new String[0];
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getAllAttributesForGeoLevelAttributeTheme",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getAllAttributesForGeoLevelAttributeTheme",
				"geography",
				geography);	
			fieldValidationUtility.checkNullMethodParameter(
				"getAllAttributesForGeoLevelAttributeTheme",
				"geoLevelSelect",
				geoLevelSelect);	
			fieldValidationUtility.checkNullMethodParameter(
				"getAllAttributesForGeoLevelAttributeTheme",
				"diseaseMappingStudy",
				diseaseMappingStudy);	
			fieldValidationUtility.checkNullMethodParameter(
				"getAllAttributesForGeoLevelAttributeTheme",
				"geoLevelAttributeSource",
				geoLevelAttributeSource);	
			fieldValidationUtility.checkNullMethodParameter(
				"getAllAttributesForGeoLevelAttributeTheme",
				"geoLevelAttributeTheme",
				geoLevelAttributeTheme);	
				
			RIFLogger rifLogger = new RIFLogger();				
			String auditTrailMessage
				= RIFServiceMessages.getMessage("logging.getAllAttributesForGeoLevelAttributeTheme",
					user.getUserID(),
					user.getIPAddress(),
					geography.getDisplayName(),
					geoLevelSelect.getDisplayName());
			rifLogger.info(
				AbstractRIFStudyRetrievalService.class,
				auditTrailMessage);
			
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();			
			results
				= sqlResultsQueryManager.getAllAttributesForGeoLevelAttributeTheme(
					connection,
					user,
					geography,
					geoLevelSelect,
					diseaseMappingStudy,
					geoLevelAttributeSource,
					geoLevelAttributeTheme);
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getGeoLevelAttributeThemes",
				rifServiceException);						
		}
					
		return results;
	}

	
	public String[] getNumericAttributesForGeoLevelAttributeTheme(
		final User _user,
		final Geography _geography,
		final GeoLevelSelect _geoLevelSelect,
		final DiseaseMappingStudy diseaseMappingStudy,
		final GeoLevelAttributeSource _geoLevelAttributeSource,
		final GeoLevelAttributeTheme _geoLevelAttributeTheme)
		throws RIFServiceException {
				
		//Part I: Defensively copy parameters
		User user = User.createCopy(_user);
		SQLConnectionManager sqlConnectionManager
			= rifServiceResources.getSqlConnectionManager();
		if (sqlConnectionManager.isUserBlocked(user) == true) {
			return null;
		}
		Geography geography = Geography.createCopy(_geography);
		GeoLevelSelect geoLevelSelect
			= GeoLevelSelect.createCopy(_geoLevelSelect);
		GeoLevelAttributeSource geoLevelAttributeSource
			= GeoLevelAttributeSource.createCopy(_geoLevelAttributeSource);
		GeoLevelAttributeTheme geoLevelAttributeTheme
			= GeoLevelAttributeTheme.createCopy(_geoLevelAttributeTheme);
		String[] results = new String[0];
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getNumericAttributesForGeoLevelAttributeTheme",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getNumericAttributesForGeoLevelAttributeTheme",
				"geography",
				geography);	
			fieldValidationUtility.checkNullMethodParameter(
				"getNumericAttributesForGeoLevelAttributeTheme",
				"geoLevelSelect",
				geoLevelSelect);	
			fieldValidationUtility.checkNullMethodParameter(
				"getNumericAttributesForGeoLevelAttributeTheme",
				"diseaseMappingStudy",
				diseaseMappingStudy);
			fieldValidationUtility.checkNullMethodParameter(
				"getNumericAttributesForGeoLevelAttributeTheme",
				"geoLevelAttributeSource",
				geoLevelAttributeSource);			
			fieldValidationUtility.checkNullMethodParameter(
				"getNumericAttributesForGeoLevelAttributeTheme",
				"geoLevelAttributeTheme",
				geoLevelAttributeTheme);	
					
			RIFLogger rifLogger = new RIFLogger();				
			String auditTrailMessage
				= RIFServiceMessages.getMessage("logging.getNumericAttributesForGeoLevelAttributeTheme",
					user.getUserID(),
					user.getIPAddress(),
					diseaseMappingStudy.getDisplayName(),
					geoLevelAttributeSource.getDisplayName(),
					geoLevelAttributeTheme.getDisplayName());
			rifLogger.info(
				AbstractRIFStudyRetrievalService.class,
				auditTrailMessage);
			
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();				
			results
				= sqlResultsQueryManager.getAllAttributesForGeoLevelAttributeTheme(
					connection,
					user,
					geography,
					geoLevelSelect,
					diseaseMappingStudy,
					geoLevelAttributeSource,
					geoLevelAttributeTheme);
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getNumericAttributesForGeoLevelAttributeTheme",
				rifServiceException);						
		}
						
		return results;
	}
	
	public RIFResultTable getCalculatedResultsByBlock(
		User _user,
		DiseaseMappingStudy _diseaseMappingStudy,
		String[] calculatedResultColumnFieldNames,
		Integer startRowIndex,
		Integer endRowIndex)
		throws RIFServiceException {

		
		RIFResultTable results = new RIFResultTable();
	
		//Part I: Defensively copy parameters
		User user = User.createCopy(_user);
		SQLConnectionManager sqlConnectionManager
			= rifServiceResources.getSqlConnectionManager();
		if (sqlConnectionManager.isUserBlocked(user) == true) {
			return results;
		}
		DiseaseMappingStudy diseaseMappingStudy
			= DiseaseMappingStudy.createCopy(_diseaseMappingStudy);
					
		try {
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();			
			//Part II: Check for empty parameter values
			fieldValidationUtility.checkNullMethodParameter(
				"getCalculatedResultsByBlock",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getCalculatedResultsByBlock",
				"diseaseMappingStudy",
				diseaseMappingStudy);	
			fieldValidationUtility.checkNullMethodParameter(
				"getCalculatedResultsByBlock",
				"calculatedResultColumnFieldNames",
				calculatedResultColumnFieldNames);	
			fieldValidationUtility.checkNullMethodParameter(
				"getCalculatedResultsByBlock",
				"endRowIndex",
				endRowIndex);	
			fieldValidationUtility.checkNullMethodParameter(
				"getCalculatedResultsByBlock",
				"startRowIndex",
				startRowIndex);	
			fieldValidationUtility.checkNullMethodParameter(
				"getCalculatedResultsByBlock",
				"endRowIndex",
				endRowIndex);	
			
			for (String calculatedResultColumnFieldName : calculatedResultColumnFieldNames) {
				fieldValidationUtility.checkMaliciousMethodParameter(
					"getCalculatedResultsByBlock", 
					"calculatedResultColumnFieldNames", 
					calculatedResultColumnFieldName);
			}
			
			//Part III: Check for security violations
			validateUser(user);
			diseaseMappingStudy.checkSecurityViolations();
				
			RIFLogger rifLogger = new RIFLogger();			
			String auditTrailMessage
				= RIFServiceMessages.getMessage(
					"logging.getCalculatedResultsByBlock",
					user.getUserID(),
					user.getIPAddress(),
					diseaseMappingStudy.getDisplayName(),
					String.valueOf(startRowIndex),
					String.valueOf(endRowIndex));
			rifLogger.info(
				AbstractRIFStudyRetrievalService.class,
				auditTrailMessage);
	
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();
			results
				= sqlResultsQueryManager.getCalculatedResultsByBlock(
					connection,
					user,
					diseaseMappingStudy,
					calculatedResultColumnFieldNames,
					startRowIndex,
					endRowIndex);
			
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getCalculatedResultsByBlock",
				rifServiceException);	
		}	
		
		return results;
	}
	
	public RIFResultTable getExtractByBlock(
		User _user,
		DiseaseMappingStudy _diseaseMappingStudy,
		String[] calculatedResultColumnFieldNames,
		Integer startRowIndex,
		Integer endRowIndex)
		throws RIFServiceException {

			
		RIFResultTable results = new RIFResultTable();
		
		//Part I: Defensively copy parameters
		User user = User.createCopy(_user);
		SQLConnectionManager sqlConnectionManager
			= rifServiceResources.getSqlConnectionManager();
		if (sqlConnectionManager.isUserBlocked(user) == true) {
			return results;
		}
		DiseaseMappingStudy diseaseMappingStudy
			= DiseaseMappingStudy.createCopy(_diseaseMappingStudy);
					
		try {
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();			
			//Part II: Check for empty parameter values
			fieldValidationUtility.checkNullMethodParameter(
				"getExtractByBlock",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getExtractByBlock",
				"diseaseMappingStudy",
				diseaseMappingStudy);	
			fieldValidationUtility.checkNullMethodParameter(
				"getExtractByBlock",
				"startRowIndex",
				startRowIndex);	
			fieldValidationUtility.checkNullMethodParameter(
				"getExtractByBlock",
				"endRowIndex",
				endRowIndex);	

			//Part III: Check for security violations
			validateUser(user);
			diseaseMappingStudy.checkSecurityViolations();
					
			RIFLogger rifLogger = new RIFLogger();			
			String auditTrailMessage
				= RIFServiceMessages.getMessage(
					"logging.getExtractByBlock",
					user.getUserID(),
					user.getIPAddress(),
					diseaseMappingStudy.getDisplayName(),
					String.valueOf(startRowIndex),
					String.valueOf(endRowIndex));
			rifLogger.info(
				AbstractRIFStudyRetrievalService.class,
				auditTrailMessage);
		
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();
			results
				= sqlResultsQueryManager.getExtractByBlock(
					connection,
					user,
					diseaseMappingStudy,
					calculatedResultColumnFieldNames,
					startRowIndex,
					endRowIndex);
		}
		catch(RIFServiceException rifServiceException) {	
			logException(
				user,
				"getExtractByBlock",
				rifServiceException);	
		}	
			
		return results;
	}
			
	public RIFResultTable getResultsStratifiedByGenderAndAgeGroup(
		User _user,
		Geography _geography,
		GeoLevelSelect _geoLevelSelect,
		DiseaseMappingStudy _diseaseMappingStudy,
		GeoLevelAttributeTheme _geoLevelAttributeTheme,
		String geoLevelAttribute,
		ArrayList<MapArea> _mapAreas,
		Integer year)
		throws RIFServiceException {
		
		
		RIFResultTable results = new RIFResultTable();
		
		//Part I: Defensively copy parameters
		User user = User.createCopy(_user);
		SQLConnectionManager sqlConnectionManager
			= rifServiceResources.getSqlConnectionManager();
		if (sqlConnectionManager.isUserBlocked(user) == true) {
			return results;
		}
		Geography geography = Geography.createCopy(_geography);
		GeoLevelSelect geoLevelSelect = GeoLevelSelect.createCopy(_geoLevelSelect);
		DiseaseMappingStudy diseaseMappingStudy
			= DiseaseMappingStudy.createCopy(_diseaseMappingStudy);
		GeoLevelAttributeTheme geoLevelAttributeTheme
			= GeoLevelAttributeTheme.createCopy(_geoLevelAttributeTheme);
		ArrayList<MapArea> mapAreas = MapArea.createCopy(_mapAreas);
		
		try {
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();			
			//Part II: Check for empty parameter values
			fieldValidationUtility.checkNullMethodParameter(
				"getResultsStratifiedByGenderAndAgeGroup",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getResultsStratifiedByGenderAndAgeGroup",
				"geography",
				geography);	
			fieldValidationUtility.checkNullMethodParameter(
				"getResultsStratifiedByGenderAndAgeGroup",
				"geoLevelSelect",
				geoLevelSelect);	
			fieldValidationUtility.checkNullMethodParameter(
				"getResultsStratifiedByGenderAndAgeGroup",
				"diseaseMappingStudy",
				diseaseMappingStudy);
			fieldValidationUtility.checkNullMethodParameter(
				"getResultsStratifiedByGenderAndAgeGroup",
				"geoLevelAttributeTheme",
				geoLevelAttributeTheme);		
			fieldValidationUtility.checkNullMethodParameter(
				"getResultsStratifiedByGenderAndAgeGroup",
				"geoLevelAttribute",
				geoLevelAttribute);	
			
			
			//Part III: Check for security violations
			validateUser(user);
			geography.checkSecurityViolations();
			geoLevelSelect.checkSecurityViolations();		
			diseaseMappingStudy.checkSecurityViolations();
			fieldValidationUtility.checkMaliciousMethodParameter(
				"getResultsStratifiedByGenderAndAgeGroup", 
				"geoLevelAttribute", 
				geoLevelAttribute);		
			if (mapAreas != null) {
				for (MapArea mapArea : mapAreas) {
					mapArea.checkSecurityViolations();
				}
			}
			
			RIFLogger rifLogger = new RIFLogger();			
			String auditTrailMessage
				= RIFServiceMessages.getMessage(
					"logging.getResultsStratifiedByGenderAndAgeGroup",
					user.getUserID(),
					user.getIPAddress(),
					geography.getDisplayName(),
					geoLevelSelect.getDisplayName(),
					diseaseMappingStudy.getDisplayName());
			rifLogger.info(
				AbstractRIFStudyRetrievalService.class,
				auditTrailMessage);
		
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();
			results
				= sqlResultsQueryManager.getResultsStratifiedByGenderAndAgeGroup(
					connection,
					user,
					geography,
					geoLevelSelect,
					diseaseMappingStudy,
					geoLevelAttributeTheme,
					geoLevelAttribute,
					mapAreas,
					year);
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getResultsStratifiedByGenderAndAgeGroup",
				rifServiceException);	
			
		}
		
		return results;
	}

	
	
	public Rectangle2D.Double getGeoLevelBoundsForArea(
		final User _user,
		final Geography _geography,
		final GeoLevelSelect _geoLevelSelect,
		final MapArea _mapArea)
		throws RIFServiceException {
		
		//Part I: Defensively copy parameters
		User user = User.createCopy(_user);
		SQLConnectionManager sqlConnectionManager
			= rifServiceResources.getSqlConnectionManager();
		if (sqlConnectionManager.isUserBlocked(user) == true) {
			return null;
		}
		Geography geography = Geography.createCopy(_geography);
		GeoLevelSelect geoLevelSelect = GeoLevelSelect.createCopy(_geoLevelSelect);
		MapArea mapArea = MapArea.createCopy(_mapArea);
				
		Rectangle2D.Double result = new Rectangle2D.Double(0,0,0,0);
		try {			
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelBoundsForArea",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelBoundsForArea",
				"geography",
				geography);
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelBoundsForArea",
				"geoLevelSelect",
				geoLevelSelect);
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelBoundsForArea",
				"mapArea",
				mapArea);
			
		
			//Part III: Check for security violations
			validateUser(user);
			geography.checkSecurityViolations();
			geoLevelSelect.checkSecurityViolations();
			mapArea.checkSecurityViolations();
			
			RIFLogger rifLogger = new RIFLogger();				
			String auditTrailMessage
				= RIFServiceMessages.getMessage("logging.getGeoLevelBoundsForArea",
					user.getUserID(),
					user.getIPAddress(),
					mapArea.getDisplayName());
			rifLogger.info(
				AbstractRIFStudyRetrievalService.class,
				auditTrailMessage);
		
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();		
			result
				= sqlResultsQueryManager.getGeoLevelBoundsForArea(
					connection,
					user,
					geography,
					geoLevelSelect,
					mapArea);
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getGeoLevelBoundsForArea",
				rifServiceException);	
		}		
		
		return result;
	}
		
	public Rectangle2D.Double getGeoLevelFullExtentForStudy(
		final User _user,
		final Geography _geography,
		final GeoLevelSelect _geoLevelSelect,
		final DiseaseMappingStudy _diseaseMappingStudy)
		throws RIFServiceException {

		//Part I: Defensively copy parameters
		User user = User.createCopy(_user);
		SQLConnectionManager sqlConnectionManager
			= rifServiceResources.getSqlConnectionManager();
		if (sqlConnectionManager.isUserBlocked(user) == true) {
			return null;
		}
		Geography geography = Geography.createCopy(_geography);
		GeoLevelSelect geoLevelSelect = GeoLevelSelect.createCopy(_geoLevelSelect);
		DiseaseMappingStudy diseaseMappingStudy
			=  DiseaseMappingStudy.createCopy(_diseaseMappingStudy);
		
		
		Rectangle2D.Double result = new Rectangle2D.Double(0,0,0,0);
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelFullExtentForStudy",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelFullExtentForStudy",
				"geography",
				geography);	
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelFullExtentForStudy",
				"geoLevelSelect",
				geoLevelSelect);				
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelFullExtentForStudy",
				"diseaseMappingStudy",
				diseaseMappingStudy);				

			
			//Part III: Check for security violations
			validateUser(user);
			geography.checkSecurityViolations();
			geoLevelSelect.checkSecurityViolations();
			diseaseMappingStudy.checkSecurityViolations();
			
			RIFLogger rifLogger = new RIFLogger();				
			String auditTrailMessage
				= RIFServiceMessages.getMessage("logging.getGeoLevelFullExtentForStudy",
					user.getUserID(),
					user.getIPAddress(),
					diseaseMappingStudy.getDisplayName());
			rifLogger.info(
				AbstractRIFStudyRetrievalService.class,
				auditTrailMessage);
			
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();		
			result
				= sqlResultsQueryManager.getGeoLevelFullExtentForStudy(
					connection,
					user,
					geography,
					geoLevelSelect,
					diseaseMappingStudy);
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getGeoLevelFullExtentForStudy",
				rifServiceException);			
		}
		return result;
	}
	
	public Rectangle2D.Double getGeoLevelFullExtent(
		final User _user,
		final Geography _geography,
		final GeoLevelSelect _geoLevelSelect)
		throws RIFServiceException {

		//Part I: Defensively copy parameters
		User user = User.createCopy(_user);
		SQLConnectionManager sqlConnectionManager
			= rifServiceResources.getSqlConnectionManager();
		if (sqlConnectionManager.isUserBlocked(user) == true) {
			return null;
		}
		Geography geography = Geography.createCopy(_geography);
		GeoLevelSelect geoLevelSelect = GeoLevelSelect.createCopy(_geoLevelSelect);
			
		Rectangle2D.Double result = new Rectangle2D.Double(0,0,0,0);
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelFullExtentForStudy",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelFullExtentForStudy",
				"geography",
				geography);	
			fieldValidationUtility.checkNullMethodParameter(
				"getGeoLevelFullExtentForStudy",
				"geoLevelSelect",
				geoLevelSelect);

				
			//Part III: Check for security violations
			validateUser(user);
			geography.checkSecurityViolations();
			geoLevelSelect.checkSecurityViolations();
				
			RIFLogger rifLogger = new RIFLogger();				
			String auditTrailMessage
				= RIFServiceMessages.getMessage("logging.getGeoLevelFullExtent",
					user.getUserID(),
					user.getIPAddress(),
					geoLevelSelect.getDisplayName());
			rifLogger.info(
				AbstractRIFStudyRetrievalService.class,
				auditTrailMessage);
			
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();			
			result
				= sqlResultsQueryManager.getGeoLevelFullExtent(
					connection,
					user,
					geography,
					geoLevelSelect);
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getGeoLevelFullExtent",
				rifServiceException);			
		}
		return result;
	}
	
	public String getTiles(
		final User _user,
		final Geography _geography,
		final GeoLevelSelect _geoLevelSelect,
		final Integer zoomFactor,
		final String tileIdentifier)
		throws RIFServiceException {

		//Part I: Defensively copy parameters
		User user = User.createCopy(_user);
		SQLConnectionManager sqlConnectionManager
			= rifServiceResources.getSqlConnectionManager();
		if (sqlConnectionManager.isUserBlocked(user) == true) {
			return null;
		}
		Geography geography
			= Geography.createCopy(_geography);
		GeoLevelSelect geoLevelSelect 
			= GeoLevelSelect.createCopy(_geoLevelSelect);
				
		String result = "";
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getTiles",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getTiles",
				"geography",
				geography);	
			fieldValidationUtility.checkNullMethodParameter(
				"getTiles",
				"getLevelSelect",
				geoLevelSelect);	
			fieldValidationUtility.checkNullMethodParameter(
				"getTiles",
				"zoomFactor",
				zoomFactor);	
			fieldValidationUtility.checkNullMethodParameter(
				"getTiles",
				"tileIdentifier",
				tileIdentifier);	
			
			//Part III: Check for security violations
			validateUser(user);
			geoLevelSelect.checkSecurityViolations();
			fieldValidationUtility.checkMaliciousMethodParameter(			
				"getTiles",
				"tileIdentifier",
				tileIdentifier);	

			RIFLogger rifLogger = new RIFLogger();				
			String auditTrailMessage
				= RIFServiceMessages.getMessage("logging.getTiles",
					user.getUserID(),
					user.getIPAddress(),
					tileIdentifier);
			rifLogger.info(
				AbstractRIFStudyRetrievalService.class,
				auditTrailMessage);
			
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();
			result
				= sqlResultsQueryManager.getTiles(
					connection,
					user,
					geography,
					geoLevelSelect,
					zoomFactor,
					tileIdentifier);
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getTiles",
				rifServiceException);			
		}
		return result;
		
	}	

	/**
	 * returns data stratified by age group
	 * @param user
	 * @param geography
	 * @param geoLevelSelect
	 * @param geoLevelSource
	 * @param geoLevelAttribute
	 * @return
	 * @throws RIFServiceException
	 */
	public RIFResultTable getPyramidData(
		final User _user,
		final Geography _geography,
		final GeoLevelSelect _geoLevelSelect,
		final GeoLevelAttributeSource _geoLevelSource,
		final String geoLevelAttribute) 
		throws RIFServiceException {
		
		//Part I: Defensively copy parameters
		User user = User.createCopy(_user);
		SQLConnectionManager sqlConnectionManager
			= rifServiceResources.getSqlConnectionManager();
		if (sqlConnectionManager.isUserBlocked(user) == true) {
			return null;
		}
		Geography geography
			= Geography.createCopy(_geography);
		GeoLevelSelect geoLevelSelect 
			= GeoLevelSelect.createCopy(_geoLevelSelect);
		GeoLevelAttributeSource geoLevelSource
			= GeoLevelAttributeSource.createCopy(_geoLevelSource);

		RIFResultTable results = new RIFResultTable();
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidData",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidData",
				"geography",
				geography);	
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidData",
				"geoLevelSelect",
				geoLevelSelect);	
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidData",
				"geoLevelSource",
				geoLevelSource);	

			//Part III: Check for security violations
			validateUser(user);
			geography.checkSecurityViolations();
			geoLevelSelect.checkSecurityViolations();
			geoLevelSource.checkSecurityViolations();
			fieldValidationUtility.checkMaliciousMethodParameter(
				"getPyramidData", 
				"geoLevelAttribute", 
				geoLevelAttribute);

			RIFLogger rifLogger = new RIFLogger();				
			String auditTrailMessage
				= RIFServiceMessages.getMessage("logging.getPyramidData",
					user.getUserID(),
					user.getIPAddress(),
					geography.getDisplayName(),
					geoLevelSelect.getDisplayName(),
					geoLevelSource.getDisplayName());
			rifLogger.info(
				AbstractRIFStudyRetrievalService.class,
				auditTrailMessage);
			
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();			
			
			results
				= sqlResultsQueryManager.getPyramidData(
					connection,
					user,
					geography,
					geoLevelSelect,
					geoLevelSource,
					geoLevelAttribute);
				
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getPyramidData",
				rifServiceException);			
		}
		
		return results;
	}
	
	public RIFResultTable getPyramidDataByYear(
		final User user,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelAttributeSource geoLevelSource,
		final String geoLevelAttribute,
		final Integer year) 
		throws RIFServiceException {
		
		RIFResultTable results = new RIFResultTable();
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidDataByYear",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidDataByYear",
				"geography",
				geography);	
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidDataByYear",
				"geoLevelSelect",
				geoLevelSelect);	
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidDataByYear",
				"geoLevelSource",
				geoLevelSource);
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidDataByYear",
				"geoLevelAttribute",
				geoLevelAttribute);
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidDataByYear",
				"year",
				year);

			//Part III: Check for security violations
			validateUser(user);
			geography.checkSecurityViolations();
			geoLevelSelect.checkSecurityViolations();
			geoLevelSource.checkSecurityViolations();
			fieldValidationUtility.checkMaliciousMethodParameter(
				"getPyramidDataByYear", 
				"geoLevelAttribute", 
				geoLevelAttribute);			
			
			RIFLogger rifLogger = new RIFLogger();				
			String auditTrailMessage
				= RIFServiceMessages.getMessage("logging.getPyramidDataByYear",
					user.getUserID(),
					user.getIPAddress(),
					geography.getDisplayName(),
					geoLevelSelect.getDisplayName(),
					geoLevelSource.getDisplayName());
			rifLogger.info(
				AbstractRIFStudyRetrievalService.class,
				auditTrailMessage);
			
			SQLConnectionManager sqlConnectionManager
				= rifServiceResources.getSqlConnectionManager();
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();			
			
			results
				= sqlResultsQueryManager.getPyramidDataByYear(
					connection,
					user,
					geography,
					geoLevelSelect,
					geoLevelSource,
					geoLevelAttribute,
					year);
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getPyramidDataByYear",
				rifServiceException);
		}

		return results;		
	}
		
	/**
	 * returns a table with these fields:
	 * eg:
	 * agegroup     | sex    popcount
	 * @param user
	 * @param geography
	 * @param geoLevelSelect
	 * @param geoLevelSource
	 * @param geoLevelAttribute
	 * @param mapAreas
	 * @return
	 * @throws RIFServiceException
	 */
	public RIFResultTable getPyramidDataByMapAreas(
		final User user,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final GeoLevelAttributeSource geoLevelSource,
		final String geoLevelAttribute,
		final ArrayList<MapArea> mapAreas) 
		throws RIFServiceException {
		
				
		RIFResultTable results = new RIFResultTable();
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidDataByMapAreas",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidDataByMapAreas",
				"geography",
				geography);	
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidDataByMapAreas",
				"geoLevelSelect",
				geoLevelSelect);	
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidDataByMapAreas",
				"geoLevelSource",
				geoLevelSource);
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidDataByMapAreas",
				"geoLevelAttribute",
				geoLevelAttribute);
			fieldValidationUtility.checkNullMethodParameter(
				"getPyramidDataByMapAreas",
				"mapAreas",
				mapAreas);

			//Part III: Check for security violations
			validateUser(user);
			geography.checkSecurityViolations();
			geoLevelSelect.checkSecurityViolations();
			geoLevelSource.checkSecurityViolations();
			fieldValidationUtility.checkMaliciousMethodParameter(
				"getPyramidDataByMapAreas", 
				"geoLevelAttribute", 
				geoLevelAttribute);			
			for (MapArea mapArea : mapAreas) {
				mapArea.checkSecurityViolations();
			}

			SQLConnectionManager sqlConnectionManager
				= rifServiceResources.getSqlConnectionManager();
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();			

			results
				= sqlResultsQueryManager.getPyramidDataByMapAreas(
					connection,
					user,
					geography,
					geoLevelSelect,
					geoLevelSource,
					geoLevelAttribute,
					mapAreas);
			
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getPyramidDataByMapAreas",
				rifServiceException);
		}

		return results;			
	}
	
	public String[] getResultFieldsStratifiedByAgeGroup(
		final User user,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final DiseaseMappingStudy diseaseMappingStudy,
		final GeoLevelAttributeTheme geoLevelAttributeTheme,
		final GeoLevelAttributeSource geoLevelAttributeSource)
		throws RIFServiceException {
				
		String[] results = new String[0];
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getResultFieldsStratifiedByAgeGroup",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getResultFieldsStratifiedByAgeGroup",
				"geography",
				geography);	
			fieldValidationUtility.checkNullMethodParameter(
				"getResultFieldsStratifiedByAgeGroup",
				"geoLevelSelect",
				geoLevelSelect);	
			fieldValidationUtility.checkNullMethodParameter(
				"getResultFieldsStratifiedByAgeGroup",
				"diseaseMappingStudy",
				diseaseMappingStudy);	
			fieldValidationUtility.checkNullMethodParameter(
				"getResultFieldsStratifiedByAgeGroup",
				"geoLevelAttributeTheme",
				geoLevelAttributeTheme);	
			fieldValidationUtility.checkNullMethodParameter(
				"getResultFieldsStratifiedByAgeGroup",
				"geoLevelAttributeSource",
				geoLevelAttributeSource);			
			
			//Part III: Check for security violations
			validateUser(user);
			geography.checkSecurityViolations();
			geoLevelSelect.checkSecurityViolations();
			diseaseMappingStudy.checkSecurityViolations();
			geoLevelAttributeSource.checkSecurityViolations();
			geoLevelAttributeTheme.checkSecurityViolations();
			geoLevelAttributeSource.checkSecurityViolations();
			
			SQLConnectionManager sqlConnectionManager
				= rifServiceResources.getSqlConnectionManager();
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();			

			results
				= sqlResultsQueryManager.getResultFieldsStratifiedByAgeGroup(
					connection,
					user,
					geography,
					geoLevelSelect,
					diseaseMappingStudy,
					geoLevelAttributeTheme,
					geoLevelAttributeSource);
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getResultFieldsStratifiedByAgeGroup",
				rifServiceException);
		}

		return results;		
	}
	

	/**
	 * returns field with the following headers:
	 * GID, SMR, CL, CU
	 * @param user
	 * @param diseaseMappingStudy
	 * @return
	 */
	public RIFResultTable getSMRValues(
		final User user,
		final DiseaseMappingStudy diseaseMappingStudy)
		throws RIFServiceException {

		
		RIFResultTable results = new RIFResultTable();
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getSMRValues",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getSMRValues",
				"diseaseMappingStudy",
				diseaseMappingStudy);	
			
			//Part III: Check for security violations
			validateUser(user);
			diseaseMappingStudy.checkSecurityViolations();
			
			SQLConnectionManager sqlConnectionManager
				= rifServiceResources.getSqlConnectionManager();
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();			

			results
				= sqlResultsQueryManager.getSMRValues(
					connection,
					user,
					diseaseMappingStudy);

		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getSMRValues",
				rifServiceException);
		}

		return results;		
	}


	/**
	 * obtains RR (unsmoothed - adjusted) and its confidence intervals for the
	 * study area
	 * @param user
	 * @param diseaseMappingStudy
	 * @return
	 * @throws RIFServiceException
	 */
	public RIFResultTable getRRValues(
		final User user,
		final DiseaseMappingStudy diseaseMappingStudy)
		throws RIFServiceException {

		
		RIFResultTable results = new RIFResultTable();
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getRRValues",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getRRValues",
				"diseaseMappingStudy",
				diseaseMappingStudy);	
			
			//Part III: Check for security violations
			validateUser(user);
			diseaseMappingStudy.checkSecurityViolations();
			
			SQLConnectionManager sqlConnectionManager
				= rifServiceResources.getSqlConnectionManager();
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();			

			results
				= sqlResultsQueryManager.getRRValues(
					connection,
					user,
					diseaseMappingStudy);

		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getRRValues",
				rifServiceException);
		}

		return results;		
	}
	
	/**
	 * returns a table with the following columns
	 * GID  |  RR_unadj  | CL  | CU
	 * @param user
	 * @param diseaseMappingStudy
	 * @return
	 * @throws RIFServiceException
	 */
	public RIFResultTable getRRUnadjustedValues(
		final User user,
		final DiseaseMappingStudy diseaseMappingStudy)
		throws RIFServiceException {
				
		RIFResultTable results = new RIFResultTable();
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getRRUnadjustedValues",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getRRUnadjustedValues",
				"diseaseMappingStudy",
				diseaseMappingStudy);	
			
			//Part III: Check for security violations
			validateUser(user);
			diseaseMappingStudy.checkSecurityViolations();
			
			SQLConnectionManager sqlConnectionManager
				= rifServiceResources.getSqlConnectionManager();
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();			

			results
				= sqlResultsQueryManager.getRRUnadjustedValues(
					connection,
					user,
					diseaseMappingStudy);

		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getRRUnadjustedValues",
				rifServiceException);
		}

		return results;		
	}
	
	
	/**
	 * Returns a table with the following fields:
	 *    Total denominator in study
	 *    Observed in study
		- Number of areas in study
		- Average observed in study
		- Total expected adj
		- Average expected adj
		- Relative Risk adj
		- Total expected unadj
		- Average expected unadj
		- Relative Risk unadj
	 * 
	 * @param user
	 * @param diseaseMappingStudy
	 * @return
	 * @throws RIFServiceException
	 */
	public RIFResultTable getResultStudyGeneralInfo(
		final User user,
		final DiseaseMappingStudy diseaseMappingStudy)
		throws RIFServiceException {
		
		RIFResultTable results = new RIFResultTable();
		try {
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getResultStudyGeneralInfo",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getResultStudyGeneralInfo",
				"diseaseMappingStudy",
				diseaseMappingStudy);	
	
			//Part III: Check for security violations
			validateUser(user);
			diseaseMappingStudy.checkSecurityViolations();
	
			SQLConnectionManager sqlConnectionManager
				= rifServiceResources.getSqlConnectionManager();
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();			

			results
				= sqlResultsQueryManager.getResultStudyGeneralInfo(
					connection,
					user,
					diseaseMappingStudy);

		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getResultStudyGeneralInfo",
				rifServiceException);
		}

		return results;		
		
	}
	
	public ArrayList<AgeGroup> getResultAgeGroups(
		final User user,
		final Geography geography,
		final GeoLevelSelect geoLevelSelect,
		final DiseaseMappingStudy diseaseMappingStudy,
		final GeoLevelAttributeTheme geoLevelAttributeTheme,
		final GeoLevelAttributeSource geoLevelAttributeSource,
		final String geoLevalAttribute)
		throws RIFServiceException {
		
		ArrayList<AgeGroup> results = new ArrayList<AgeGroup>();
		try {
			
			//Part II: Check for empty parameter values
			FieldValidationUtility fieldValidationUtility
				= new FieldValidationUtility();
			fieldValidationUtility.checkNullMethodParameter(
				"getResultAgeGroups",
				"user",
				user);
			fieldValidationUtility.checkNullMethodParameter(
				"getResultAgeGroups",
				"geography",
				geography);
			fieldValidationUtility.checkNullMethodParameter(
				"getResultAgeGroups",
				"geoLevelSelect",
				geoLevelSelect);
			fieldValidationUtility.checkNullMethodParameter(
				"getResultAgeGroups",
				"diseaseMappingStudy",
				diseaseMappingStudy);
			fieldValidationUtility.checkNullMethodParameter(
				"getResultAgeGroups",
				"geoLevelAttributeTheme",
				geoLevelAttributeTheme);
			fieldValidationUtility.checkNullMethodParameter(
				"getResultAgeGroups",
				"geoLevelAttributeSource",
				geoLevelAttributeSource);
				
			//Part III: Check for security violations
			validateUser(user);
			geography.checkSecurityViolations();
			geoLevelSelect.checkSecurityViolations();
			diseaseMappingStudy.checkSecurityViolations();
			geoLevelAttributeTheme.checkSecurityViolations();
			geoLevelAttributeSource.checkSecurityViolations();
			fieldValidationUtility.checkMaliciousMethodParameter(
				"getResultAgeGroups", 
				"geoLevalAttribute", 
				geoLevalAttribute);
			
			SQLConnectionManager sqlConnectionManager
				= rifServiceResources.getSqlConnectionManager();
			Connection connection
				= sqlConnectionManager.getWriteConnection(user);
			SQLResultsQueryManager sqlResultsQueryManager
				= rifServiceResources.getSqlResultsQueryManager();			
			results
				= sqlResultsQueryManager.getResultAgeGroups(
					connection,
					user,
					geography,
					geoLevelSelect,
					diseaseMappingStudy,
					geoLevelAttributeTheme,
					geoLevelAttributeSource,
					geoLevalAttribute);
		}
		catch(RIFServiceException rifServiceException) {
			logException(
				user,
				"getResultAgeGroups",
				rifServiceException);			
		}
		
		return results;
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