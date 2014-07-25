package rifServices.restfulWebServices;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import rifServices.dataStorageLayer.ProductionRIFStudyServiceBundle;
import rifServices.system.RIFServiceException;
import rifServices.system.RIFServiceMessages;
import rifServices.system.RIFServiceStartupOptions;
import rifServices.businessConceptLayer.GeoLevelArea;
import rifServices.businessConceptLayer.GeoLevelSelect;
import rifServices.businessConceptLayer.GeoLevelView;
import rifServices.businessConceptLayer.Geography;
import rifServices.businessConceptLayer.HealthTheme;
import rifServices.businessConceptLayer.NumeratorDenominatorPair;
import rifServices.businessConceptLayer.RIFStudySubmissionAPI;
import rifServices.businessConceptLayer.RIFStudyResultRetrievalAPI;
import rifServices.businessConceptLayer.User;
import rifServices.businessConceptLayer.YearRange;

/**
 * This is a web service class that is analoguous to  
 * to {@link rifServices.dataStorageLayer.AbstractRIFService}. Its purpose is
 * to wrap API methods that are common to both {@link rifServices.businessConceptLayer.RIFStudySubmissionAPI}
 * and {@link rifServices.businessConceptLayer.RIFStudyResultRetrievalAPI}.
 * 
 * <hr>
 * The Rapid Inquiry Facility (RIF) is an automated tool devised by SAHSU 
 * that rapidly addresses epidemiological and public health questions using 
 * routinely collected health and population data and generates standardised 
 * rates and relative risks for any given health outcome, for specified age 
 * and year ranges, for any given geographical area.
 *
 * Copyright 2014 Imperial College London, developed by the Small Area
 * Health Statistics Unit. The work of the Small Area Health Statistics Unit 
 * is funded by the Public Health England as part of the MRC-PHE Centre for 
 * Environment and Health. Funding for this project has also been received 
 * from the United States Centers for Disease Control and Prevention.  
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

abstract class AbstractRIFWebServiceResource {

	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================
	private ProductionRIFStudyServiceBundle rifStudyServiceBundle;
	private SimpleDateFormat sd;
	private Date startTime;
	
	// ==========================================
	// Section Construction
	// ==========================================

	public AbstractRIFWebServiceResource() {
		startTime = new Date();
		sd = new SimpleDateFormat("HH:mm:ss:SSS");

		RIFServiceStartupOptions rifServiceStartupOptions
			= new RIFServiceStartupOptions();
		StringBuilder webApplicationFolderPath
			= new StringBuilder();
		webApplicationFolderPath.append("C:");
		webApplicationFolderPath.append(File.separator);
		webApplicationFolderPath.append("Program Files");
		webApplicationFolderPath.append(File.separator);
		webApplicationFolderPath.append("Apache Software Foundation");
		webApplicationFolderPath.append(File.separator);
		webApplicationFolderPath.append("Tomcat 8.0");
		webApplicationFolderPath.append(File.separator);
		webApplicationFolderPath.append("webapps");
		webApplicationFolderPath.append(File.separator);
		webApplicationFolderPath.append("rifServices");
		webApplicationFolderPath.append(File.separator);
		webApplicationFolderPath.append("WEB-INF");
		webApplicationFolderPath.append(File.separator);
		webApplicationFolderPath.append("classes");
		rifServiceStartupOptions.setWebApplicationFilePath(webApplicationFolderPath.toString());

		rifStudyServiceBundle
			= ProductionRIFStudyServiceBundle.getRIFServiceBundle();

		try {
			rifStudyServiceBundle.initialise(rifServiceStartupOptions);
			rifStudyServiceBundle.login("kgarwood", new String("a").toCharArray());
		}
		catch(RIFServiceException exception) {
			exception.printStackTrace(System.out);
		}
	}

	// ==========================================
	// Section Accessors and Mutators
	// ==========================================
	protected RIFStudySubmissionAPI getRIFStudySubmissionService() {
		return rifStudyServiceBundle.getRIFStudySubmissionService();
	}
	
	protected RIFStudyResultRetrievalAPI getRIFStudyResultRetrievalService() {
		return rifStudyServiceBundle.getRIFStudyRetrievalService();
	}
	
	
	@GET
	@Produces({"application/json"})	
	@Path("/getGeographies")
	public String getGeographies(
		@QueryParam("userID") String userID) {
			
		String result = "";
		
		ArrayList<GeographyProxy> geographyProxies 
			= new ArrayList<GeographyProxy>();
	
		try {
			User user = User.newInstance(userID, "xxx");
			RIFStudySubmissionAPI studySubmissionService
				= rifStudyServiceBundle.getRIFStudySubmissionService();
			ArrayList<Geography> geographies
				= studySubmissionService.getGeographies(user);
			for (Geography geography : geographies) {
				GeographyProxy geographyProxy
					= new GeographyProxy();
				geographyProxy.setName(geography.getName());
				geographyProxies.add(geographyProxy);
			}
		
			result = serialiseResult(geographyProxies);
		}
		catch(Exception exception) {
			exception.printStackTrace(System.out);
			result = serialiseException(exception);			
		}
	
		return result;
	
	}

	@GET
	@Produces({"application/json"})	
	@Path("/getGeoLevelSelectValues")
	public String getGeographicalLevelSelectValues(
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName) {
			
		String result = "";
	
	
		GeoLevelSelectsProxy geoLevelSelectProxy
			= new GeoLevelSelectsProxy();
	
		try {
			User user = User.newInstance(userID, "xxxxxxxx");
			Geography geography = Geography.newInstance(geographyName, "xxx");
			RIFStudySubmissionAPI studySubmissionService
				= rifStudyServiceBundle.getRIFStudySubmissionService();			
			ArrayList<GeoLevelSelect> geoLevelSelects
				= studySubmissionService.getGeographicalLevelSelectValues(
					user, 
					geography);
			ArrayList<String> geoLevelSelectNames = new ArrayList<String>();			
			for (GeoLevelSelect geoLevelSelect : geoLevelSelects) {
				geoLevelSelectNames.add(geoLevelSelect.getName());
			}
		
			geoLevelSelectProxy.setNames(geoLevelSelectNames.toArray(new String[0]));
			result = serialiseResult(geoLevelSelectProxy);
		}
		catch(Exception exception) {
			result = serialiseException(exception);			
		}
	
		return result;
	
	}
		

	@GET
	@Produces({"application/json"})	
	@Path("/getDefaultGeoLevelSelectValue")
	public String getDefaultGeoLevelSelectValue(
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName) {
			
		String result = "";
	
	
		GeoLevelSelectsProxy geoLevelSelectProxy
			= new GeoLevelSelectsProxy();
	
		try {
			User user = User.newInstance(userID, "xxxxxxxx");
			Geography geography = Geography.newInstance(geographyName, "xxx");
			RIFStudySubmissionAPI studySubmissionService
				= rifStudyServiceBundle.getRIFStudySubmissionService();

			GeoLevelSelect defaultGeoLevelSelect
				= studySubmissionService.getDefaultGeoLevelSelectValue(
					user, 
					geography);
			String[] geoLevelSelectValues = new String[1];
			geoLevelSelectValues[0] = defaultGeoLevelSelect.getName();
			geoLevelSelectProxy.setNames(geoLevelSelectValues);
			result = serialiseResult(geoLevelSelectProxy);
		}
		catch(Exception exception) {
			result = serialiseException(exception);			
		}
	
		return result;
	
	}

	
	@GET
	@Produces({"application/json"})	
	@Path("/getGeoLevelAreaValues")
	public String getGeoLevelAreaValues(
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,
		@QueryParam("geoLevelSelectName") String geoLevelSelectName) {
				
		String result = "";
		
		GeoLevelAreasProxy geoLevelAreasProxy = new GeoLevelAreasProxy();
		
		try {
			User user = User.newInstance(userID, "xxx");
			Geography geography = Geography.newInstance(geographyName, "xxx");
			GeoLevelSelect geoLevelSelect
				= GeoLevelSelect.newInstance(geoLevelSelectName);
			RIFStudySubmissionAPI studySubmissionService
				= rifStudyServiceBundle.getRIFStudySubmissionService();			
			ArrayList<GeoLevelArea> areas
				= studySubmissionService.getGeoLevelAreaValues(
					user, 
					geography, 
					geoLevelSelect);
			
			ArrayList<String> geoLevelAreaNames = new ArrayList<String>();
			for (GeoLevelArea area : areas) {
				geoLevelAreaNames.add(area.getName());
			}
			geoLevelAreasProxy.setNames(geoLevelAreaNames.toArray(new String[0]));
			result = serialiseResult(geoLevelAreasProxy);
		}
		catch(Exception exception) {
			result = serialiseException(exception);			
		}
		
		return result;
		
	}
	
	
	
	@GET
	@Produces({"application/json"})	
	@Path("/getGeoLevelViews")
	public String getGeoLevelViewValues(
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,
		@QueryParam("geoLevelSelectName") String geoLevelSelectName) {
				
		String result = "";
				
		GeoLevelViewsProxy geoLevelViewsProxy = new GeoLevelViewsProxy();
		
		try {
			User user = User.newInstance(userID, "xxxx");
			Geography geography = Geography.newInstance(geographyName, "");
			GeoLevelSelect geoLevelSelect
				= GeoLevelSelect.newInstance(geoLevelSelectName);
			RIFStudySubmissionAPI studySubmissionService
				= rifStudyServiceBundle.getRIFStudySubmissionService();	
			ArrayList<GeoLevelView> geoLevelViews
				= studySubmissionService.getGeoLevelViewValues(
					user, 
					geography, 
					geoLevelSelect);
			
			ArrayList<String> geoLevelViewNames = new ArrayList<String>();
			for (GeoLevelView geoLevelView : geoLevelViews) {
				geoLevelViewNames.add(geoLevelView.getName());
			}
			geoLevelViewsProxy.setNames(geoLevelViewNames.toArray(new String[0]));
			
			result = serialiseResult(geoLevelViewsProxy);
		}
		catch(Exception exception) {
			result = serialiseException(exception);			
		}
		
		return result;
		
	}	
	
	/**
	 * retrieves the numerator associated with a given health theme.
	 * @param userID
	 * @param geographyName
	 * @param healthThemeDescription
	 * @return
	 */
	@GET
	@Produces({"application/json"})	
	@Path("/getNumerator")
	public String getNumerator(
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,		
		@QueryParam("healthThemeDescription") String healthThemeDescription) {
				
		String result = "";
		
		
		ArrayList<NumeratorDenominatorPairProxy> ndPairProxies 
			= new ArrayList<NumeratorDenominatorPairProxy>();
				
		try {
			User user 
				= User.newInstance(userID, "xxx");
			Geography geography 
				= Geography.newInstance(geographyName, "");
			HealthTheme healthTheme 
				= HealthTheme.newInstance("xxx", healthThemeDescription);
			RIFStudySubmissionAPI studySubmissionService
				= rifStudyServiceBundle.getRIFStudySubmissionService();				
			ArrayList<NumeratorDenominatorPair> ndPairs
				= studySubmissionService.getNumeratorDenominatorPairs(
					user, 
					geography, 
					healthTheme);
			for (NumeratorDenominatorPair ndPair : ndPairs) {
				NumeratorDenominatorPairProxy ndPairProxy
					= new NumeratorDenominatorPairProxy();
				ndPairProxy.setNumeratorTableName(ndPair.getNumeratorTableName());
				ndPairProxy.setNumeratorTableDescription(ndPair.getNumeratorTableDescription());
				ndPairProxy.setDenominatorTableName(ndPair.getDenominatorTableName());
				ndPairProxy.setDenominatorTableDescription(ndPair.getDenominatorTableDescription());
				
				ndPairProxies.add(ndPairProxy);
			}
			
			result = serialiseResult(ndPairProxies);
		}
		catch(Exception exception) {
			result = serialiseException(exception);			
		}
		
		return result;
		
	}

	@GET
	@Produces({"application/json"})	
	@Path("/denominator")
	public String getDenominator(
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,		
		@QueryParam("healthThemeDescription") String healthThemeDescription) {
				
		String result = "";
				
		try {
			User user 	
				= User.newInstance(userID, "xxx");
			Geography geography 
				= Geography.newInstance(geographyName, "");
			HealthTheme healthTheme 
				= HealthTheme.newInstance("xxx", healthThemeDescription);
			RIFStudySubmissionAPI studySubmissionService
				= rifStudyServiceBundle.getRIFStudySubmissionService();				
			ArrayList<NumeratorDenominatorPair> ndPairs
				= studySubmissionService.getNumeratorDenominatorPairs(
					user, 
					geography, 
					healthTheme);
			
			//We should be guaranteed that at least one pair will be returned.
			//All the numerators returned should have the same denominator
			//Therefore, we should be able to pick the first ndPair and extract
			//the denominator.
			NumeratorDenominatorPair firstResult
				= ndPairs.get(0);
			NumeratorDenominatorPairProxy ndPairProxy
				= new NumeratorDenominatorPairProxy();
			ndPairProxy.setNumeratorTableName(firstResult.getNumeratorTableName());
			ndPairProxy.setNumeratorTableDescription(firstResult.getNumeratorTableDescription());
			ndPairProxy.setDenominatorTableName(firstResult.getDenominatorTableName());
			ndPairProxy.setDenominatorTableDescription(firstResult.getDenominatorTableDescription());
							
			result = serialiseResult(firstResult);
		}
		catch(Exception exception) {
			result = serialiseException(exception);			
		}
		
		return result;
		
	}
	
	
	@GET
	@Produces({"application/json"})	
	@Path("/yearRange")
	public String getYearRange(
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,	
		@QueryParam("numeratorTableName") String numeratorTableName) {
			
		String result = "";
		
		try {
			User user = User.newInstance(userID, "xxx");
			Geography geography = Geography.newInstance(geographyName, "xxx");
			RIFStudySubmissionAPI studySubmissionService
				= rifStudyServiceBundle.getRIFStudySubmissionService();			
			NumeratorDenominatorPair ndPair
				= studySubmissionService.getNumeratorDenominatorPairFromNumeratorTable(
					user, 
					geography, 
					numeratorTableName);
			
			YearRange yearRange
				= studySubmissionService.getYearRange(user, geography, ndPair);
			YearRangeProxy yearRangeProxy = new YearRangeProxy();
			yearRangeProxy.setLowerBound(yearRange.getLowerBound());
			yearRangeProxy.setUpperBound(yearRange.getUpperBound());
			result = serialiseResult(yearRangeProxy);
		}
		catch(Exception exception) {
			result = serialiseException(exception);			
		}
		
		return result;
	}
	


	/**
	 * takes advantage of the Jackson project library to serialise objects
	 * for the JSON format.
	 * @param objectToWrite
	 * @return
	 * @throws Exception
	 */
	protected String serialiseResult(
		Object objectToWrite) 
			throws Exception {

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(out, objectToWrite);
		final byte[] data = out.toByteArray();
		return(new String(data));
	}
	
	// ==========================================
	// Section Errors and Validation
	// ==========================================

	protected String serialiseException(
		Exception exceptionThrownByRIFService) {
		
		String result = "";
		try {			
			RIFServiceExceptionProxy rifServiceExceptionProxy
				= new RIFServiceExceptionProxy();
			if (exceptionThrownByRIFService instanceof RIFServiceException) {
				RIFServiceException rifServiceException
					= (RIFServiceException) exceptionThrownByRIFService;
				ArrayList<String> errorMessages
					= rifServiceException.getErrorMessages();
				rifServiceExceptionProxy.setErrorMessages(errorMessages.toArray(new String[0]));
			}
			else {
				/*
				 * We should never encounter this.  However, if we do, 
				 * then we should just indicate that an unexpected error has occurred.
				 * We may assume that the root cause of the error has been logged within
				 * the implementation of the service.
				 */
				String[] errorMessages = new String[1];
				String timeStamp = sd.format(new Date());
				errorMessages[0]
					= RIFServiceMessages.getMessage(
						"webServices.error.unexpectedError",
						timeStamp);
			
				rifServiceExceptionProxy.setErrorMessages(errorMessages);
			}
			result = serialiseResult(rifServiceExceptionProxy);
		}
		catch(Exception exception) {
			String timeStamp = sd.format(new Date());
			result 
				= RIFServiceMessages.getMessage(
					"webServices.error.unableToProvideError",
					timeStamp);			
		}
		
		return result;
	}
	
	/**
	 * Used as a crude way to find how long individual service operations are taking to 
	 * complete.
	 * @param header
	 */
	protected void printTime(String header) {
		Date date = new Date();
		StringBuilder buffer = new StringBuilder();
		buffer.append(header);
		buffer.append(":");
		buffer.append(sd.format(date));
		buffer.append("(");
		long elapsed = date.getTime() - startTime.getTime();
		buffer.append(elapsed);
		buffer.append(" milliseconds since start time");
		System.out.println(buffer.toString());		
	}
	

	// ==========================================
	// Section Interfaces
	// ==========================================

	// ==========================================
	// Section Override
	// ==========================================
}