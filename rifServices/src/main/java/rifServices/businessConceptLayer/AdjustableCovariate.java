package rifServices.businessConceptLayer;

import rifServices.system.RIFServiceError;
import rifServices.system.RIFServiceException;
import rifServices.system.RIFServiceMessages;
import rifServices.system.RIFServiceSecurityException;

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



public final class AdjustableCovariate 
	extends AbstractCovariate {

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
     * Instantiates a new adjustable covariate.
     */
	private AdjustableCovariate() {

    }

	/**
	 * Instantiates a new adjustable covariate.
	 *
	 * @param name the name
	 * @param minimumValue the minimum value
	 * @param maximumValue the maximum value
	 */
	private AdjustableCovariate(
		final String name,
		final String minimumValue,
		final String maximumValue) {
		
		super(name, minimumValue, maximumValue);
	}
	
	/**
	 * New instance.
	 *
	 * @return the adjustable covariate
	 */
	public static AdjustableCovariate newInstance() {
		
		AdjustableCovariate adjustableCovariate = new AdjustableCovariate();
		return adjustableCovariate;
	}
	
	/**
	 * New instance.
	 *
	 * @param name the name
	 * @param minimumValue the minimum value
	 * @param maximumValue the maximum value
	 * @param covariateType the covariate type
	 * @return the adjustable covariate
	 */
	public static AdjustableCovariate newInstance(
		final String name,
		final String minimumValue,
		final String maximumValue,
		final CovariateType covariateType) {
		
		AdjustableCovariate adjustableCovariate
			= new AdjustableCovariate(
				name, 
				minimumValue, 
				maximumValue);
		adjustableCovariate.setCovariateType(covariateType);
		return adjustableCovariate;		
	}

	/**
	 * Creates the copy.
	 *
	 * @param adjustableCovariate the adjustable covariate
	 * @return the adjustable covariate
	 */
	public static AdjustableCovariate createCopy(
		final AdjustableCovariate adjustableCovariate) {
		
		if (adjustableCovariate == null) {
			return null;
		}

		AdjustableCovariate cloneAdjustableCovariate
            = new AdjustableCovariate();
        cloneAdjustableCovariate.setName(adjustableCovariate.getName());
        cloneAdjustableCovariate.setMinimumValue(adjustableCovariate.getMinimumValue());
        cloneAdjustableCovariate.setMaximumValue(adjustableCovariate.getMaximumValue());
        cloneAdjustableCovariate.setCovariateType(adjustableCovariate.getCovariateType());
		return cloneAdjustableCovariate;
	}	
	
// ==========================================
// Section Accessors and Mutators
// ==========================================

	
	public void identifyDifferences(
		final AdjustableCovariate anotherAdjustableCovariate,
		final ArrayList<String> differences) {
		
		super.identifyDifferences(
			anotherAdjustableCovariate, 
			differences);
	}
		
	/**
	 * Checks for identical contents.
	 *
	 * @param otherAdjustableCovariate the other adjustable covariate
	 * @return true, if successful
	 */
	public boolean hasIdenticalContents(
			
		final AdjustableCovariate otherAdjustableCovariate) {
		if (otherAdjustableCovariate == null) {
			return false;
		}
		
		return super.hasIdenticalContents(otherAdjustableCovariate);
	}
	
// ==========================================
// Section Errors and Validation
// ==========================================
	
	/* (non-Javadoc)
	 * @see rifServices.businessConceptLayer.AbstractCovariate#checkSecurityViolations()
	 */
	@Override
	public void checkSecurityViolations() 
		throws RIFServiceSecurityException {
		
		super.checkSecurityViolations();		
	}
	
	/* (non-Javadoc)
	 * @see rifServices.businessConceptLayer.AbstractRIFConcept#checkErrors()
	 */
	public void checkErrors() 
		throws RIFServiceException {		
		
        ArrayList<String> errorMessages = new ArrayList<String>();     
		super.checkErrors(errorMessages);    
		countErrors(RIFServiceError.INVALID_ADJUSTABLE_COVARIATE, errorMessages);
	}
    	
// ==========================================
// Section Interfaces
// ==========================================
	
// ==========================================
// Section Override
// ==========================================
	
	/* (non-Javadoc)
	 * @see rifServices.businessConceptLayer.AbstractCovariate#getRecordType()
	 */
	@Override
	public String getRecordType() {
		
		String recordNameLabel
			= RIFServiceMessages.getMessage("adjustableCovariate.label");
		return recordNameLabel;
	}	
}
