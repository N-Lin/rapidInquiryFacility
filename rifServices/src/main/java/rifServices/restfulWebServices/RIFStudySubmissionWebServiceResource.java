package rifServices.restfulWebServices;



import rifServices.system.RIFServiceMessages;
import rifServices.businessConceptLayer.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.servlet.http.*;
import javax.ws.rs.QueryParam;

import java.text.Collator;
import java.util.ArrayList;



/**
 * This class advertises API methods found in 
 * {@link rifServices.businessConceptLayer.RIFJobSubmissionAPI}
 * as a web service.  
 * 
 * Two issues have dominated the design of this class:
 * <ul>
 * <li>
 * the slight mismatch between URL parameter values and corresponding instances of Java
 * objects
 * </li>
 * <li>
 * the level of granularity in the conversations we would expect the web service to have
 * with the client
 * </li>
 * <li>
 * The efficiency with which 
 * </ul>
 * 
 * <p>
 * 
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

@Path("/")
public class RIFStudySubmissionWebServiceResource 
	extends AbstractRIFWebServiceResource {

	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================
	
	// ==========================================
	// Section Construction
	// ==========================================

	public RIFStudySubmissionWebServiceResource() {
		super();

	}

	// ==========================================
	// Section Accessors and Mutators
	// ==========================================

	
	@GET
	@Produces({"application/json"})	
	@Path("/login")
	public Response login(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("password") String password) {

		return super.login(
			servletRequest,
			userID, 
			password);
	}
	
	@GET
	@Produces({"application/json"})	
	@Path("/isLoggedIn")
	public Response login(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID) {

		return super.isLoggedIn(
			servletRequest,
			userID);
	}
	
	@GET
	@Produces({"application/json"})	
	@Path("/logout")
	public Response logout(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID) {

		return super.logout(
			servletRequest,
			userID);
	}
	
	
	
	@GET
	@Produces({"application/json"})	
	@Path("/getGeographies")
	public Response getGeographies(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID) {

		return super.getGeographies(
			servletRequest,
			userID);
	}
	
	@GET
	@Produces({"application/json"})	
	@Path("/getGeoLevelSelectValues")
	public Response getGeographicalLevelSelectValues(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName) {
	
		return super.getGeographicalLevelSelectValues(
			servletRequest,
			userID, 
			geographyName);
	}	
	
	
	@GET
	@Produces({"application/json"})	
	@Path("/getDefaultGeoLevelSelectValue")
	public Response getDefaultGeoLevelSelectValue(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName) {

		return super.getDefaultGeoLevelSelectValue(
			servletRequest,
			userID,
			geographyName);
	}	
	
	@GET
	@Produces({"application/json"})	
	@Path("/getGeoLevelAreaValues")
	public Response getGeoLevelAreaValues(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,
		@QueryParam("geoLevelSelectName") String geoLevelSelectName) {
	
		return super.getGeoLevelAreaValues(
			servletRequest,
			userID, 
			geographyName, 
			geoLevelSelectName);
	}	
	
	@GET
	@Produces({"application/json"})	
	@Path("/getGeoLevelViews")
	public Response getGeoLevelViewValues(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,
		@QueryParam("geoLevelSelectName") String geoLevelSelectName) {
	
		return super.getGeoLevelViewValues(
			servletRequest,
			userID, 
			geographyName, 
			geoLevelSelectName);
	}
		
	@GET
	@Produces({"application/json"})	
	@Path("/getNumerator")
	public Response getNumerator(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,		
		@QueryParam("healthThemeDescription") String healthThemeDescription) {
	
	
		return super.getNumerator(
			servletRequest,
			userID,
			geographyName,
			healthThemeDescription);
	}	
	
	@GET
	@Produces({"application/json"})	
	@Path("/getDenominator")
	public Response getDenominator(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,		
		@QueryParam("healthThemeDescription") String healthThemeDescription) {

		return super.getDenominator(
			servletRequest,
			userID,
			geographyName,
			healthThemeDescription);
	}
	
	@GET
	@Produces({"application/json"})	
	@Path("/getYearRange")
	public Response getYearRange(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,	
		@QueryParam("numeratorTableName") String numeratorTableName) {

		return super.getYearRange(
			servletRequest,
			userID, 
			geographyName, 
			numeratorTableName);		
	}
	
	/**
	 * STUB
	 * @param userID
	 * @return
	 */
	@GET
	@Produces({"application/json"})	
	@Path("/getAvailableRIFOutputOptions")
	public Response getAvailableRIFOutputOptions(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID) {
				
		String result = "";
		
		
		try {
			//Convert URL parameters to RIF service API parameters			
			User user = createUser(servletRequest, userID);
			
			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();	
			ArrayList<RIFOutputOption> rifOutputOptions
				= studySubmissionService.getAvailableRIFOutputOptions(user);
			
			//Convert results to support JSON
			//@TODO
			
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);			
		}
		
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();
		
		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);
		
	}
	
	/**
	 * STUB 
	 * @param userID
	 * @return
	 */
	
	@GET
	@Produces({"application/json"})	
	@Path("/getAvailableCalculationMethods")
	public Response getAvailableCalculationMethods(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID) {
				
		String result = "";
		
		
		try {
			//Convert URL parameters to RIF service API parameters			
			User user = createUser(servletRequest, userID);

			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();	
			ArrayList<CalculationMethod> calculationMethods
				= studySubmissionService.getAvailableCalculationMethods(user);
			
			//Convert results to support JSON
			ArrayList<CalculationMethodProxy> calculationMethodProxies
				= new ArrayList<CalculationMethodProxy>();
			for (CalculationMethod calculationMethod : calculationMethods) {
				CalculationMethodProxy calculationMethodProxy
					= new CalculationMethodProxy();
				calculationMethodProxy.setCodeRoutineName(calculationMethod.getCodeRoutineName());
				calculationMethodProxy.setDescription(calculationMethod.getDescription());
				calculationMethodProxy.setPrior(calculationMethod.getPrior().getName());
				ArrayList<Parameter> parameters = calculationMethod.getParameters();
				ArrayList<ParameterProxy> parameterProxies 
					= new ArrayList<ParameterProxy>();
				for (Parameter parameter : parameters) {
					ParameterProxy parameterProxy 
						= new ParameterProxy();
					parameterProxy.setName(parameter.getName());
					parameterProxy.setValue(parameter.getValue());
					parameterProxies.add(parameterProxy);
				}
				calculationMethodProxy.setParameterProxies(parameterProxies);				
				calculationMethodProxies.add(calculationMethodProxy);
			}
			result 
				= serialiseArrayResult(
					servletRequest,
					calculationMethodProxies);			
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			exception.printStackTrace(System.out);
			result 
				= serialiseException(
					servletRequest,
					exception);			
		}
		
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();
	
		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);
	}
		
	@GET
	@Produces({"application/json"})	
	@Path("/getDiseaseMappingStudies")
	public Response getDiseaseMappingStudies(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID) {
				
		String result = "";
		
		
		try {
			//Convert URL parameters to RIF service API parameters
			User user = createUser(servletRequest, userID);

			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();	
			ArrayList<DiseaseMappingStudy> diseaseMappingStudies
				= studySubmissionService.getDiseaseMappingStudies(user);
			
			//Convert results to support JSON
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);			
		}
		
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();
	
		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);		
	}

	
	@GET
	@Produces({"application/json"})	
	@Path("/getProjects")
	public Response getProjects(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID) {
				
		String result = "";
		
		
		try {
			//Convert URL parameters to RIF service API parameters
			User user = createUser(servletRequest, userID);

			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();	
			ArrayList<Project> projects
				= studySubmissionService.getProjects(user);			

			//Convert results to support JSON
			ArrayList<ProjectProxy> projectProxies 
				= new ArrayList<ProjectProxy>();
			for (Project project : projects) {
				ProjectProxy projectProxy
					= new ProjectProxy();
				projectProxy.setName(project.getName());
				projectProxies.add(projectProxy);
			}		
			result 
				= serialiseArrayResult(
					servletRequest,
					projectProxies);
		}
		catch(Exception exception) {
			exception.printStackTrace(System.out);
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);			
		}
		
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();
	
		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);		
	}
	
	@GET
	@Produces({"application/json"})	
	@Path("/getProjectDescription")
	public Response getProjectDescription(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("projectName") String projectName) {
				
		String result = "";

		try {
			//Convert URL parameters to RIF service API parameters
			User user = createUser(servletRequest, userID);

			
			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();
			ArrayList<Project> projects
				= studySubmissionService.getProjects(user);			

			//Convert results to support JSON
			Collator collator = RIFServiceMessages.getCollator();
			Project selectedProject = null;
			for (Project project : projects) {
				if (collator.equals(projectName, project.getName())) {
					selectedProject = project;
					break;
				}
			}
			if (selectedProject == null) {
				result
					= RIFServiceMessages.getMessage(
						"webService.getProjectDescription.error.projectNotFound",
						projectName);
			}
			else {
				result 
					= serialiseStringResult(
						selectedProject.getDescription());
			}
			
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);			
		}
				
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();
	
		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);		
	}
	@GET
	@Produces({"application/json"})	
	@Path("/getGeoLevelToMapValues")
	public Response getGeoLevelToMapValues(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,
		@QueryParam("geoLevelSelectName") String geoLevelSelectName) {
				
		String result = "";
		
		
		try {
			//Convert URL parameters to RIF service API parameters
			User user = createUser(servletRequest, userID);
			Geography geography = Geography.newInstance(geographyName, "");
			GeoLevelSelect geoLevelSelect
				= GeoLevelSelect.newInstance(geoLevelSelectName);

			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();		
			ArrayList<GeoLevelToMap> geoLevelToMaps
				= studySubmissionService.getGeoLevelToMapValues(
					user, 
					geography, 
					geoLevelSelect);

			//Convert results to support JSON			
			GeoLevelToMapsProxy geoLevelToMapsProxy 
				= new GeoLevelToMapsProxy();
			ArrayList<String> geoLevelToMapsNames = new ArrayList<String>();
			
			for (GeoLevelToMap geoLevelToMap : geoLevelToMaps) {
				geoLevelToMapsNames.add(geoLevelToMap.getName());
			}
			geoLevelToMapsProxy.setNames(geoLevelToMapsNames.toArray(new String[0]));			
			result 
				= serialiseSingleItemAsArrayResult(
					servletRequest,
					geoLevelToMapsProxy);
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);			
		}

		
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();

		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);		
		
	}

	
	@GET
	@Produces({"application/json"})	
	@Path("/getMapAreas")
	public Response getGeoLevelToMapAreas(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,
		@QueryParam("geoLevelSelectName") String geoLevelSelectName,
		@QueryParam("geoLevelAreaName") String geoLevelAreaName,
		@QueryParam("geoLevelToMapName") String geoLevelToMapName) {
				
		String result = "";
		
		
		try {
			//Convert URL parameters to RIF service API parameters
			User user = createUser(servletRequest, userID);
			Geography geography = Geography.newInstance(geographyName, "");
			GeoLevelSelect geoLevelSelect
				= GeoLevelSelect.newInstance(geoLevelSelectName);
			GeoLevelArea geoLevelArea
				= GeoLevelArea.newInstance(geoLevelAreaName);
			GeoLevelToMap geoLevelToMap
				= GeoLevelToMap.newInstance(geoLevelToMapName);			
			geoLevelToMap.checkErrors();
			
			
			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();	
			ArrayList<MapArea> mapAreas
				= studySubmissionService.getMapAreas(
					user, 
					geography, 
					geoLevelSelect,
					geoLevelArea,
					geoLevelToMap);
			
			//Convert results to support JSON			
			/*
			ArrayList<MapAreaProxy> mapAreaProxies 
				= new ArrayList<MapAreaProxy>();
			for (MapArea mapArea : mapAreas) {
				MapAreaProxy mapAreaProxy
					= new MapAreaProxy();
				mapAreaProxy.setIdentifier(mapArea.getIdentifier());
				mapAreaProxy.setLabel(mapArea.getLabel());
				mapAreaProxies.add(mapAreaProxy);
			}
			*/
			
			//use a specialised serialiser to produce a useful way of 
			//rendering map areas
			
			MapAreaJSONGenerator mapAreaJSONGenerator
				= new MapAreaJSONGenerator();
			result
				= mapAreaJSONGenerator.writeJSONMapAreas(mapAreas);
			
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);			
		}
		
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();

		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);		
	}
		
	@GET
	@Produces({"application/json"})	
	@Path("/getHealthThemes")
	public Response getHealthThemes(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName) {
				
		String result = "";
		
		
		try {
			//Convert URL parameters to RIF service API parameters
			User user = createUser(servletRequest, userID);
			Geography geography = Geography.newInstance(geographyName, "");

			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();				

			ArrayList<HealthTheme> healthThemes
				= studySubmissionService.getHealthThemes(
					user, 
					geography);
			
			//Convert results to support JSON						
			ArrayList<HealthThemeProxy> healthThemeProxies 
				= new ArrayList<HealthThemeProxy>();
			for (HealthTheme healthTheme : healthThemes) {
				HealthThemeProxy healthThemeProxy
					= new HealthThemeProxy();
				healthThemeProxy.setName(healthTheme.getName());
				healthThemeProxy.setDescription(healthTheme.getDescription());
				healthThemeProxies.add(healthThemeProxy);
			}			
			result 
				= serialiseArrayResult(
					servletRequest,
					healthThemeProxies);
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);
		}
		
		
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();

		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);		
	}
	
	@GET
	@Produces({"application/json"})	
	@Path("/getSexes")
	public Response getSexes(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID) {
				
		String result = "";
				
		try {
			//Convert URL parameters to RIF service API parameters
			User user = createUser(servletRequest, userID);

			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();			
			ArrayList<Sex> sexes
				= studySubmissionService.getSexes(
					user);
			
			//We should be guaranteed that at least one pair will be returned.
			//All the numerators returned should have the same denominator
			//Therefore, we should be able to pick the first ndPair and extract
			//the denominator.
			SexesProxy sexesProxy = new SexesProxy();
			ArrayList<String> sexNames = new ArrayList<String>();
			
			for (Sex sex : sexes) {
				sexNames.add(sex.getName());
			}

			//Convert results to support JSON						
			sexesProxy.setNames(sexNames.toArray(new String[0]));
			result 
				= serialiseSingleItemAsArrayResult(
					servletRequest,
					sexesProxy);
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);
		}
				
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();

		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);		
	}
	
	
	@GET
	@Produces({"application/json"})	
	@Path("/getCovariates")
	public Response getCovariates(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,
		@QueryParam("geoLevelToMapName") String geoLevelToMapName) {
						
		String result = "";
				
		try {
			//Convert URL parameters to RIF service API parameters
			User user = createUser(servletRequest, userID);
			Geography geography
				= Geography.newInstance(geographyName, "");
			GeoLevelToMap geoLevelToMap
				= GeoLevelToMap.newInstance(geoLevelToMapName);
			System.out.println("RIFStudySubmissionWebService - getCovariates -"+geoLevelToMapName+"==");
			
			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();
			ArrayList<AbstractCovariate> covariates
				= studySubmissionService.getCovariates(
					user, 
					geography, 
					geoLevelToMap);

			//Convert results to support JSON						
			ArrayList<CovariateProxy> covariateProxies
				= new ArrayList<CovariateProxy>();
			for (AbstractCovariate covariate : covariates) {
				CovariateProxy covariateProxy
					= new CovariateProxy();
				if (covariate instanceof AdjustableCovariate) {
					covariateProxy.setCovariateType("adjustable");
				}
				else {
					covariateProxy.setCovariateType("exposure");					
				}
				covariateProxy.setName(covariate.getName());
				covariateProxy.setMinimumValue(covariate.getMinimumValue());
				covariateProxy.setMaximumValue(covariate.getMaximumValue());
				covariateProxies.add(covariateProxy);
			}
			
			result 
				= serialiseArrayResult(
					servletRequest,
					covariateProxies);
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);			
		}
		
		
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();

		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);		
		
	}
		
	@GET
	@Produces({"application/json"})	
	@Path("/getAgeGroups")
	public Response getAgeGroups(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,	
		@QueryParam("numeratorTableName") String numeratorTableName) {

		String result = "";
		
		try {
			//Convert URL parameters to RIF service API parameters
			User user = createUser(servletRequest, userID);
			Geography geography = Geography.newInstance(geographyName, "xxx");

			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();			
			NumeratorDenominatorPair ndPair
				= studySubmissionService.getNumeratorDenominatorPairFromNumeratorTable(
					user, 
					geography, 
					numeratorTableName);
			ArrayList<AgeGroup> ageGroups
				= studySubmissionService.getAgeGroups(
					user, 
					geography, 
					ndPair, 
					AgeGroupSortingOption.ASCENDING_LOWER_LIMIT);
			
			//Convert results to support JSON	
			AgeGroupJSONGenerator ageGroupJSONGenerator
				= new AgeGroupJSONGenerator();
			result
				= ageGroupJSONGenerator.writeJSONMapAreas(ageGroups);
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);
		}

		
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();

		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);		
	}

	@GET
	@Produces({"application/json"})	
	@Path("/getHealthCodeTaxonomies")
	public Response getHealthCodeTaxonomies(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID) {
	
		String result = "";

		try {
			//Convert URL parameters to RIF service API parameters
			User user = createUser(servletRequest, userID);

			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();			
			ArrayList<HealthCodeTaxonomy> healthCodeTaxonomies
				= studySubmissionService.getHealthCodeTaxonomies(user);

			//Convert results to support JSON						
			ArrayList<HealthCodeTaxonomyProxy> healthCodeTaxonomyProxies
				 = new ArrayList<HealthCodeTaxonomyProxy>();
			for (HealthCodeTaxonomy healthCodeTaxonomy : healthCodeTaxonomies) {
				HealthCodeTaxonomyProxy healthCodeTaxonomyProxy
					= new HealthCodeTaxonomyProxy();
				healthCodeTaxonomyProxy.setName(healthCodeTaxonomy.getName());
				healthCodeTaxonomyProxy.setDescription(healthCodeTaxonomy.getDescription());
				healthCodeTaxonomyProxy.setNameSpace(healthCodeTaxonomy.getNameSpace());
				healthCodeTaxonomyProxy.setVersion(healthCodeTaxonomy.getVersion());
				healthCodeTaxonomyProxies.add(healthCodeTaxonomyProxy);
			}
			result 
				= serialiseArrayResult(
					servletRequest,
					healthCodeTaxonomyProxies);
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);			
		}
		
		
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();

		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);		
	}
		
	@GET
	@Produces({"application/json"})	
	@Path("/getTopLevelCodes")
	public Response getTopLevelCodes(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("healthCodeTaxonomyNameSpace") String healthCodeTaxonomyNameSpace) {
			
		String result = "";
		
		try {
			//Convert URL parameters to RIF service API parameters
			User user = createUser(servletRequest, userID);

			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();			
			HealthCodeTaxonomy healthCodeTaxonomy
				= studySubmissionService.getHealthCodeTaxonomyFromNameSpace(
					user, 
					healthCodeTaxonomyNameSpace);
			ArrayList<HealthCode> healthCodes
				= studySubmissionService.getTopLevelHealthCodes(user, healthCodeTaxonomy);

			//Convert results to support JSON						
			ArrayList<HealthCodeProxy> healthCodeProxies
				= new ArrayList<HealthCodeProxy>();
			for (HealthCode healthCode : healthCodes) {
				HealthCodeProxy healthCodeProxy = new HealthCodeProxy();
				healthCodeProxy.setCode(healthCode.getCode());
				healthCodeProxy.setDescription(healthCode.getDescription());
				healthCodeProxy.setNameSpace(healthCode.getNameSpace());
				healthCodeProxy.setIsTopLevelTerm(String.valueOf(healthCode.isTopLevelTerm()));
				healthCodeProxy.setNumberOfSubTerms(String.valueOf(healthCode.getNumberOfSubTerms()));
				healthCodeProxies.add(healthCodeProxy);
			}
			result 
				= serialiseArrayResult(
					servletRequest,
					healthCodeProxies);					
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);			
		}
		
		
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();

		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);		
	}

	
	/*
	@GET
	@Produces({"application/json"})	
	@Path("/getHealthCodesForSearchText")
	public Response getHealthCodesForSearchText(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("healthCodeTaxonomyNameSpace") String healthCodeTaxonomyNameSpace,
		@QueryParam("searchText") String searchText,
		@DefaultValue("false") @QueryParam("isContextSensitive") boolean isContextSensitive) {
	
		String result = "";
		try {			
			//Convert URL parameters to RIF service API parameters
			User user = User.newInstance(userID, "xxx");

			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();			
			HealthCodeTaxonomy healthCodeTaxonomy
				= studySubmissionService.getHealthCodeTaxonomyFromNameSpace(
					user, 
					healthCodeTaxonomyNameSpace);
			
			ArrayList<HealthCode> healthCodes
				= studySubmissionService.getHealthCodesMatchingSearchText(
					user, 
					healthCodeTaxonomy, 
					searchText,
					isContextSensitive);

			//Convert results to support JSON						
			ArrayList<HealthCodeProxy> healthCodeProxies
				= new ArrayList<HealthCodeProxy>();
			for (HealthCode healthCode : healthCodes) {
				HealthCodeProxy healthCodeProxy
					= new HealthCodeProxy();
				healthCodeProxy.setCode(healthCode.getCode());
				healthCodeProxy.setDescription(healthCode.getDescription());
				healthCodeProxy.setNameSpace(healthCode.getNameSpace());
				healthCodeProxy.setIsTopLevelTerm(String.valueOf(healthCode.isTopLevelTerm()));
				healthCodeProxy.setNumberOfSubTerms(String.valueOf(healthCode.getNumberOfSubTerms()));
				healthCodeProxies.add(healthCodeProxy);
			}
			result 
				= serialiseArrayResult(
					servletRequest,
					healthCodeProxies);					
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);			
		}
		
		return generateAppropriateContentTypeResponse(
			servletRequest,
			result);
	}
*/
	
	
	@GET
	@Produces({"application/json"})	
	@Path("/getParentHealthCode")
	public Response getParentHealthCode(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("childHealthCode") String healthCode,
		@QueryParam("childHealthCodeNameSpace") String healthCodeNameSpace) {
	
		String result = "";
		try {			
			//Convert URL parameters to RIF service API parameters
			User user = createUser(servletRequest, userID);

			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();			
			HealthCode childHealthCode
				= studySubmissionService.getHealthCode(
					user, 
					healthCode, 
					healthCodeNameSpace);
			HealthCode parentHealthCode
				= studySubmissionService.getParentHealthCode(user, childHealthCode);			

			//Convert results to support JSON						
			HealthCodeProxy healthCodeProxy
				= new HealthCodeProxy();
			if (parentHealthCode != null) {
				healthCodeProxy.setCode(parentHealthCode.getCode());
				healthCodeProxy.setDescription(parentHealthCode.getDescription());
				healthCodeProxy.setNameSpace(parentHealthCode.getNameSpace());
				healthCodeProxy.setIsTopLevelTerm(String.valueOf(parentHealthCode.isTopLevelTerm()));
				healthCodeProxy.setNumberOfSubTerms(String.valueOf(parentHealthCode.getNumberOfSubTerms()));
				result 
					= serialiseSingleItemAsArrayResult(
						servletRequest,
						healthCodeProxy);
			}
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);			
		}
		
		
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();

		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);		
	}
	

	@GET
	@Produces({"application/json"})	
	@Path("/getImmediateChildHealthCodes")
	public Response getImmediateChildHealthCodes(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("healthCode") String healthCode,
		@QueryParam("healthCodeNameSpace") String healthCodeNameSpace) {
	
		String result = "";
		try {			
			//Convert URL parameters to RIF service API parameters
			User user = createUser(servletRequest, userID);

			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();			
			HealthCode parentHealthCode
				= studySubmissionService.getHealthCode(
					user, 
					healthCode, 
					healthCodeNameSpace);
			ArrayList<HealthCode> healthCodes
				= studySubmissionService.getImmediateChildHealthCodes(user, parentHealthCode);
			
			//Convert results to support JSON						
			ArrayList<HealthCodeProxy> healthCodeProxies
				= new ArrayList<HealthCodeProxy>();
			for (HealthCode currentHealthCode : healthCodes) {
				HealthCodeProxy healthCodeProxy
					= new HealthCodeProxy();
				healthCodeProxy.setCode(currentHealthCode.getCode());
				healthCodeProxy.setDescription(currentHealthCode.getDescription());
				healthCodeProxy.setNameSpace(currentHealthCode.getNameSpace());
				healthCodeProxy.setIsTopLevelTerm(String.valueOf(currentHealthCode.isTopLevelTerm()));
				healthCodeProxy.setNumberOfSubTerms(String.valueOf(currentHealthCode.getNumberOfSubTerms()));
				healthCodeProxies.add(healthCodeProxy);
			}
			result 
				= serialiseArrayResult(
					servletRequest,
					healthCodeProxies);					
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);			
		}
		
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();

		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);		
	}

	@GET
	@Produces({"application/json"})	
	@Path("/getHealthCodesMatchingSearchText")
	public Response getHealthCodesMatchingSearchText(
		@Context HttpServletRequest servletRequest,
		@QueryParam("userID") String userID,
		@QueryParam("nameSpace") String nameSpace,		
		@QueryParam("searchText") String searchText,
		@QueryParam("isCaseSensitive") boolean isCaseSensitive) {
		
		String result = "";
		try {			
			//Convert URL parameters to RIF service API parameters
			User user = createUser(servletRequest, userID);

			//Call service API
			RIFStudySubmissionAPI studySubmissionService
				= getRIFStudySubmissionService();
			
			System.out.println("zzzAbout to call get codes for matching text with nameSpace=="+nameSpace+"==searchText=="+searchText+"==isContextSensitive=="+isCaseSensitive+"==");
			HealthCodeTaxonomy healthCodeTaxonomy
				= studySubmissionService.getHealthCodeTaxonomyFromNameSpace(
					user, 
					nameSpace);	
			ArrayList<HealthCode> matchingHealthCodes
				= studySubmissionService.getHealthCodesMatchingSearchText(
					user, 
					healthCodeTaxonomy, 
					searchText,
					isCaseSensitive);
			
			//Convert results to support JSON						
			ArrayList<HealthCodeProxy> healthCodeProxies
				= new ArrayList<HealthCodeProxy>();
			for (HealthCode matchingHealthCode : matchingHealthCodes) {
				HealthCodeProxy healthCodeProxy
					= new HealthCodeProxy();
				healthCodeProxy.setCode(matchingHealthCode.getCode());
				healthCodeProxy.setDescription(matchingHealthCode.getDescription());
				healthCodeProxy.setNameSpace(matchingHealthCode.getNameSpace());
				healthCodeProxy.setIsTopLevelTerm(String.valueOf(matchingHealthCode.isTopLevelTerm()));
				healthCodeProxy.setNumberOfSubTerms(String.valueOf(matchingHealthCode.getNumberOfSubTerms()));
				healthCodeProxies.add(healthCodeProxy);
			}
			
			result 
				= serialiseArrayResult(
					servletRequest,
					healthCodeProxies);
		}
		catch(Exception exception) {
			//Convert exceptions to support JSON
			result 
				= serialiseException(
					servletRequest,
					exception);			
		}

		
		WebServiceResponseGenerator webServiceResponseGenerator
			= getWebServiceResponseGenerator();

		return webServiceResponseGenerator.generateWebServiceResponse(
			servletRequest,
			result);		
	}
	

	@GET
	@Produces({"application/json"})	
	@Path("/getMapAreasForBoundaryRectangle")
	public Response getMapAreasForBoundaryRectangle(
		@Context HttpServletRequest servletRequest,	
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,
		@QueryParam("geoLevelSelectName") String geoLevelSelectName,
		@QueryParam("tileIdentifier") String tileIdentifier,
		@QueryParam("zoomFactor") Integer zoomFactor,		
		@QueryParam("yMax") String yMax,
		@QueryParam("xMax") String xMax,
		@QueryParam("yMin") String yMin,
		@QueryParam("xMin") String xMin) {
					
		return super.getMapAreasForBoundaryRectangle(
			servletRequest, 
			userID, 
			geographyName, 
			geoLevelSelectName, 
			yMax, 
			xMax, 
			yMin, 
			xMin);
		
	}	
	
	@GET
	@Produces({"application/json"})	
	@Path("/getTiles")
	public Response getTiles(
		@Context HttpServletRequest servletRequest,	
		@QueryParam("userID") String userID,
		@QueryParam("geographyName") String geographyName,
		@QueryParam("geoLevelSelectName") String geoLevelSelectName,
		@QueryParam("tileIdentifier") String tileIdentifier,
		@QueryParam("zoomFactor") Integer zoomFactor,		
		@QueryParam("yMax") String yMax,
		@QueryParam("xMax") String xMax,
		@QueryParam("yMin") String yMin,
		@QueryParam("xMin") String xMin) {
					
		return super.getTiles(
			servletRequest, 
			userID, 
			geographyName, 
			geoLevelSelectName, 
			tileIdentifier, 
			zoomFactor, 
			yMax, 
			xMax, 
			yMin, 
			xMin);		
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
