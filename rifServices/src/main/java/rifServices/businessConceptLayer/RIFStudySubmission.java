
package rifServices.businessConceptLayer;

import rifServices.businessConceptLayer.AbstractStudy;

import rifServices.businessConceptLayer.DiseaseMappingStudy;
import rifServices.businessConceptLayer.CalculationMethod;
import rifServices.businessConceptLayer.RIFOutputOption;
import rifServices.system.RIFServiceException;
import rifServices.system.RIFServiceSecurityException;
import rifServices.system.RIFServiceMessages;
import rifServices.system.RIFServiceError;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;


/**
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


public final class RIFStudySubmission 
	extends AbstractRIFConcept {
	
// ==========================================
// Section Constants
// ==========================================

// ==========================================
// Section Properties
// ==========================================

	/** The job submission time. */
	private Date jobSubmissionTime;
	
	/** The user. */
	private User user;
	
	/** The project. */
	private Project project;
	
	/** The study. */
	private AbstractStudy study;
	
	/** The calculation methods. */
	private ArrayList<CalculationMethod> calculationMethods;
	
	/** The rif output options. */
	private ArrayList<RIFOutputOption> rifOutputOptions;
	   
// ==========================================
// Section Construction
// ==========================================
    /**
     * Instantiates a new RIF job submission.
 	*/
	private RIFStudySubmission() {
    	jobSubmissionTime = new Date();
    	user = User.newInstance("", "");
    	project = Project.newInstance();
    	study = DiseaseMappingStudy.newInstance();
		calculationMethods = new ArrayList<CalculationMethod>();
		rifOutputOptions = new ArrayList<RIFOutputOption>();
    }

    /**
     * New instance.
     *
     * @return the RIF job submission
     */
    public static RIFStudySubmission newInstance() {
    	RIFStudySubmission rifJobSubmission = new RIFStudySubmission();	
    	return rifJobSubmission;
    }
    
    /**
     * Creates the copy.
     *
     * @param originalRIFJobSubmission the original rif job submission
     * @return the RIF job submission
     */
    public static RIFStudySubmission createCopy(
    	final RIFStudySubmission originalRIFStudySubmission) {
   
    	if (originalRIFStudySubmission == null) {
    		return null;
    	}
    	
    	RIFStudySubmission cloneRIFStudySubmission = new RIFStudySubmission();

    	User originalUser = originalRIFStudySubmission.getUser();
    	User cloneUser = User.createCopy(originalUser);
    	cloneRIFStudySubmission.setUser(cloneUser);
    	
    	Date originalJobSubmissionTime
    		= originalRIFStudySubmission.getJobSubmissionTime();
    	if (originalJobSubmissionTime != null) {
    		Date cloneJobSubmissionTime
    			= new Date(originalJobSubmissionTime.getTime());
    		cloneRIFStudySubmission.setJobSubmissionTime(cloneJobSubmissionTime);
    	}
    	
    	Project originalProject = originalRIFStudySubmission.getProject();
    	cloneRIFStudySubmission.setProject(Project.createCopy(originalProject)); 	
    	
    	DiseaseMappingStudy originalDiseaseMappingStudy
    		= (DiseaseMappingStudy) originalRIFStudySubmission.getStudy();
    	DiseaseMappingStudy cloneDiseaseMappingStudy
    		= DiseaseMappingStudy.createCopy(originalDiseaseMappingStudy);
    	cloneRIFStudySubmission.setStudy(cloneDiseaseMappingStudy);
    	
    	ArrayList<CalculationMethod> originalCalculationMethods
    		= originalRIFStudySubmission.getCalculationMethods(); 	
    	ArrayList<CalculationMethod> clonedCalculationMethods
    		= CalculationMethod.createCopy(originalCalculationMethods);
    	
    	cloneRIFStudySubmission.setCalculationMethods(clonedCalculationMethods);
   
    	ArrayList<RIFOutputOption> originalRIFOutputOptions
    		= originalRIFStudySubmission.getRIFOutputOptions();
    	for (RIFOutputOption originalRIFOutputOption : originalRIFOutputOptions) {
    		cloneRIFStudySubmission.addRIFOutputOption(originalRIFOutputOption);
    	}
    	
    	return cloneRIFStudySubmission;

    }
    
// ==========================================
// Section Accessors and Mutators
// ==========================================
    /**
     * Adds the calculation method.
     *
     * @param calculationMethod the calculation method
     */
    public void addCalculationMethod(
    	final CalculationMethod calculationMethod) {

    	calculationMethods.add(calculationMethod);
	}
	
	/**
	 * Gets the calculation methods.
	 *
	 * @return the calculation methods
	 */
	public ArrayList<CalculationMethod> getCalculationMethods() {
		
		return calculationMethods;
	}
	
	/**
	 * Adds the rif output option.
	 *
	 * @param rifOutputOption the rif output option
	 */
	public void addRIFOutputOption(
		final RIFOutputOption rifOutputOption) {
		
		rifOutputOptions.add(rifOutputOption);
	}
	
	/**
	 * Gets the RIF output options.
	 *
	 * @return the RIF output options
	 */
	public ArrayList<RIFOutputOption> getRIFOutputOptions() {
		
		return rifOutputOptions;
	}
	
	/**
	 * Sets the calculation methods.
	 *
	 * @param calculationMethods the new calculation methods
	 */
	public void setCalculationMethods(
		final ArrayList<CalculationMethod> calculationMethods) {

		this.calculationMethods = calculationMethods;
	}
	
	/**
	 * Sets the project.
	 *
	 * @param project the new project
	 */
	public void setProject(
		final Project project) {

		this.project = project;
	}
	
	/**
	 * Gets the project.
	 *
	 * @return the project
	 */
	public Project getProject() {
		
		return project;
	}
	
	/**
	 * Gets the study.
	 *
	 * @return the study
	 */
	public AbstractStudy getStudy() {
		
		return study;
	}
	
	/**
	 * Sets the study.
	 *
	 * @param study the new study
	 */
	public void setStudy(
		final AbstractStudy study) {

		this.study = study;
	}
	
	/**
	 * Clear calculation methods.
	 */
	public void clearCalculationMethods() {
		
		calculationMethods.clear();
	}
	
	/**
	 * Clear rif output options.
	 */
	public void clearRIFOutputOptions() {
		
		rifOutputOptions.clear();
	}
	
	/**
	 * Sets the RIF output options.
	 *
	 * @param rifOutputOptions the new RIF output options
	 */
	public void setRIFOutputOptions(
		final ArrayList<RIFOutputOption> rifOutputOptions) {

		this.rifOutputOptions = rifOutputOptions;
	}
	
	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public User getUser() {
		
		return user;
	}
	
	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(
		final User user) {

		this.user = user;
	}
	
	/**
	 * Gets the job submission time.
	 *
	 * @return the job submission time
	 */
	public Date getJobSubmissionTime() {
		
		return jobSubmissionTime;
	}
	
	/**
	 * Sets the job submission time.
	 *
	 * @param jobSubmissionTime the new job submission time
	 */
	public void setJobSubmissionTime(
		final Date jobSubmissionTime) {
		
		this.jobSubmissionTime = jobSubmissionTime;
	}
	
	/**
	 * Checks for identical contents.
	 *
	 * @param otherRIFJobSubmission the other rif job submission
	 * @return true, if successful
	 */
	public boolean hasIdenticalContents(
		final RIFStudySubmission otherRIFJobSubmission) {

		if (otherRIFJobSubmission == null) {
			return false;
		}
		
		ArrayList<CalculationMethod> otherCalculationMethods
			= otherRIFJobSubmission.getCalculationMethods();
		ArrayList<RIFOutputOption> otherRIFOutputOptions
			= otherRIFJobSubmission.getRIFOutputOptions();
		
		//@TODO KLG - casting makes this code a bit vulnerable
		
		Project otherProject = otherRIFJobSubmission.getProject();
		if (project == null) {
			if (otherProject != null) {
				return false;
			}
		}
		else {
			if (project.hasIdenticalContents(otherProject) == false) {
				return false;
			}			
		}		
		
		DiseaseMappingStudy diseaseMappingStudy
			= (DiseaseMappingStudy) study;
		DiseaseMappingStudy otherDiseaseMappingStudy
			= (DiseaseMappingStudy) otherRIFJobSubmission.getStudy();
		if (diseaseMappingStudy == null) {
			if (otherDiseaseMappingStudy != null) {
				return false;
			}
		}
		else {
			if (diseaseMappingStudy.hasIdenticalContents(otherDiseaseMappingStudy) == false) {
				return false;
			}
		}
		
		if (CalculationMethod.hasIdenticalContents(
			calculationMethods, 
			otherCalculationMethods) == false) {
			
			return false;
		}
		if (RIFOutputOption.hasIdenticalContents(
			rifOutputOptions, 
			otherRIFOutputOptions) == false) {
			
			return false;
		}
		
		return super.hasIdenticalContents(otherRIFJobSubmission);
	}
// ==========================================
// Section Errors and Validation
// ==========================================
	/* (non-Javadoc)
	 * @see rifServices.businessConceptLayer.AbstractRIFConcept#checkSecurityViolations()
	 */
	public void checkSecurityViolations() 
		throws RIFServiceSecurityException {		

		super.checkSecurityViolations();
		
		user.checkSecurityViolations();	
		project.checkSecurityViolations();
		
		//For now we only have disease mapping studies
		//In future we will have risk analysis studies
		DiseaseMappingStudy diseaseMappingStudy
			= (DiseaseMappingStudy) study;
		diseaseMappingStudy.checkSecurityViolations();
		
		for (CalculationMethod calculationMethod : calculationMethods) {
			calculationMethod.checkSecurityViolations();
		}
	}
	
	/* (non-Javadoc)
	 * @see rifServices.businessConceptLayer.AbstractRIFConcept#checkErrors()
	 */
	public void checkErrors() 
		throws RIFServiceException {	
		
		ArrayList<String> errorMessages = new ArrayList<String>();
		
		String recordType = getRecordType();
		
		if (user == null) {
			String userFieldName
				= RIFServiceMessages.getMessage("user.label");
			String errorMessage
				= RIFServiceMessages.getMessage(
					"general.validation.emptyRequiredRecordField", 
					recordType,
					userFieldName);
			errorMessages.add(errorMessage);			
		}
		else {		
			try {
				user.checkErrors();
			}
			catch(RIFServiceException rifServiceException) {
				errorMessages.addAll(rifServiceException.getErrorMessages());			
			}
		}
		if (study == null) {
			String studyFieldName
				= RIFServiceMessages.getMessage("diseaseMappingStudy.label");
			String errorMessage
				= RIFServiceMessages.getMessage(
					"general.validation.emptyRequiredRecordField", 
					recordType,
					studyFieldName);
			errorMessages.add(errorMessage);			
		}
		else {
			try {
				DiseaseMappingStudy diseaseMappingStudy
					= (DiseaseMappingStudy) study;
				diseaseMappingStudy.checkErrors();
			}
			catch(RIFServiceException rifServiceException) {
				errorMessages.addAll(rifServiceException.getErrorMessages());
			}
		}

		HashSet<String> uniqueCalculationMethodNames = new HashSet<String>();
		
		for (CalculationMethod calculationMethod : calculationMethods) {
			try {
				String displayName = calculationMethod.getDisplayName();
				if (uniqueCalculationMethodNames.contains(displayName) == true) {
					String errorMessage
						= RIFServiceMessages.getMessage("rifJobSubmission.error.duplicateCalculationMethod", 
							displayName);
					errorMessages.add(errorMessage);
				}
				else {
					uniqueCalculationMethodNames.add(displayName);
				}
				calculationMethod.checkErrors();				
			}
			catch(RIFServiceException rifServiceException) {
				errorMessages.addAll(rifServiceException.getErrorMessages());				
			}
		}

		if (rifOutputOptions == null) {
			String rifOutputOptionsFieldName
				= RIFServiceMessages.getMessage("rifOutputOption.plural.label");
			String errorMessage
				= RIFServiceMessages.getMessage(
					"general.validation.emptyRequiredRecordField",
					recordType,
					rifOutputOptionsFieldName);
			errorMessages.add(errorMessage);			
		}
		else if (rifOutputOptions.isEmpty()) {
			String errorMessage
				= RIFServiceMessages.getMessage(
					"rifJobSubmission.error.noRIFOutputOptionsSpecified");
			errorMessages.add(errorMessage);			
		}
		else {
			ArrayList<RIFOutputOption> uniqueRIFOutputOptions 
				= new ArrayList<RIFOutputOption>();
		
			for (RIFOutputOption rifOutputOption : rifOutputOptions) {
				if (uniqueRIFOutputOptions.contains(rifOutputOption) == true) {
					String errorMessage
						= RIFServiceMessages.getMessage(
							"rifJobSubmission.error.duplicateOutputOption", 
							rifOutputOption.getName());
					errorMessages.add(errorMessage);
				}
				else {
					uniqueRIFOutputOptions.add(rifOutputOption);
				}
			}	
		}
		
		countErrors(RIFServiceError.INVALID_RIF_JOB_SUBMISSION, errorMessages);
	}
	
// ==========================================
// Section Interfaces
// ==========================================

	/* (non-Javadoc)
	 * @see rifServices.businessConceptLayer.AbstractRIFConcept#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return study.getDisplayName();
	}
	
	/* (non-Javadoc)
	 * @see rifServices.businessConceptLayer.AbstractRIFConcept#getRecordType()
	 */
	@Override
	public String getRecordType() {
		String recordType
			= RIFServiceMessages.getMessage("rifJobSubmission.label");
		return recordType;
	}
	
// ==========================================
// Section Override
// ==========================================

}