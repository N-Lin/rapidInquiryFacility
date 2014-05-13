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

public final class HealthTheme 
	extends AbstractRIFContextOption {

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
	 * Instantiates a new health theme.
	 */
	private HealthTheme() {
		
		super();
	}

	/**
	 * New instance.
	 *
	 * @return the health theme
	 */
	public static HealthTheme newInstance() {
		
		HealthTheme geoLevelArea = new HealthTheme();
		return geoLevelArea;
	}
	
	/**
	 * New instance.
	 *
	 * @param name the name
	 * @return the health theme
	 */
	public static HealthTheme newInstance(
		final String name) {
			
		HealthTheme geoLevelArea = new HealthTheme();
		geoLevelArea.setName(name);
		geoLevelArea.setDescription("");
			
		return geoLevelArea;
	}	

	
	/**
	 * New instance.
	 *
	 * @param name the name
	 * @param description the description
	 * @return the health theme
	 */
	public static HealthTheme newInstance(
		final String name,
		final String description) {
		
		HealthTheme geoLevelArea = new HealthTheme();
		geoLevelArea.setName(name);
		geoLevelArea.setDescription(description);
		
		return geoLevelArea;
	}	
	
	/**
	 * Creates the copy.
	 *
	 * @param originalGeoLevel the original geo level
	 * @return the health theme
	 */
	public static HealthTheme createCopy(
		final HealthTheme originalGeoLevel) {

		if (originalGeoLevel == null) {
			return null;
		}
		
		HealthTheme cloneGeoLevelArea = new HealthTheme();		
		cloneGeoLevelArea.setName(originalGeoLevel.getName());
		cloneGeoLevelArea.setDescription(originalGeoLevel.getDescription());
		
		return cloneGeoLevelArea;
	}
	
	// ==========================================
	// Section Accessors and Mutators
	// ==========================================

	/**
	 * Checks for identical contents.
	 *
	 * @param otherHealthTheme the other health theme
	 * @return true, if successful
	 */
	public boolean hasIdenticalContents(
		final HealthTheme otherHealthTheme) {
		
		return super.hasIdenticalContents(otherHealthTheme);		
	}
	
	// ==========================================
	// Section Errors and Validation
	// ==========================================

	/* (non-Javadoc)
	 * @see rifServices.businessConceptLayer.AbstractRIFContextOption#checkSecurityViolations()
	 */
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
		super.checkErrors(RIFServiceError.INVALID_HEALTH_THEME, errorMessages);
		countErrors(RIFServiceError.INVALID_HEALTH_THEME, errorMessages);
	}
	
	// ==========================================
	// Section Interfaces
	// ==========================================

	// ==========================================
	// Section Override
	// ==========================================
	
	/* (non-Javadoc)
	 * @see rifServices.businessConceptLayer.AbstractRIFConcept#getRecordType()
	 */
	@Override
	public String getRecordType() {
		
		String recordType
			= RIFServiceMessages.getMessage("healthTheme.label");
		return recordType;
	}
	
	/* (non-Javadoc)
	 * @see rifServices.businessConceptLayer.AbstractRIFContextOption#getDisplayName()
	 */
	@Override
	public String getDisplayName() {	
		
		return getDescription();
	}	
	
	/* (non-Javadoc)
	 * @see rifServices.businessConceptLayer.AbstractRIFConcept#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		
		return getName();
	}
	
}