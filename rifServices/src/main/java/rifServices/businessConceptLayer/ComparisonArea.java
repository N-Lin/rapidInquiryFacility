
package rifServices.businessConceptLayer;

import rifServices.system.*;

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


public final class ComparisonArea 
	extends AbstractGeographicalArea {


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
     * Instantiates a new comparison area.
     */
	private ComparisonArea() {

    }

	/**
	 * New instance.
	 *
	 * @return the comparison area
	 */
	static public ComparisonArea newInstance() {
		
		ComparisonArea comparisonArea = new ComparisonArea();
		return comparisonArea;
	}
	
	/**
	 * Creates the copy.
	 *
	 * @param originalComparisonArea the original comparison area
	 * @return the comparison area
	 */
	static public ComparisonArea createCopy(final ComparisonArea originalComparisonArea) {
		
		if (originalComparisonArea == null) {
			return null;
		}
				
		ComparisonArea cloneComparisonArea
			= new ComparisonArea();
		cloneComparisonArea.setIdentifier(originalComparisonArea.getIdentifier());
		ArrayList<MapArea> cloneMapAreas 
			= MapArea.createCopy(originalComparisonArea.getMapAreas());
		cloneComparisonArea.setMapAreas(cloneMapAreas);

		GeoLevelView cloneGeoLevelView
			= GeoLevelView.createCopy(originalComparisonArea.getGeoLevelView());		
		cloneComparisonArea.setGeoLevelView(cloneGeoLevelView);;
		GeoLevelArea cloneGeoLevelArea
			= GeoLevelArea.createCopy(originalComparisonArea.getGeoLevelArea());		
		cloneComparisonArea.setGeoLevelArea(cloneGeoLevelArea);;
		GeoLevelSelect cloneGeoLevelSelect
			= GeoLevelSelect.createCopy(originalComparisonArea.getGeoLevelSelect());		
		cloneComparisonArea.setGeoLevelSelect(cloneGeoLevelSelect);;
		GeoLevelToMap cloneGeoLevelToMap
			= GeoLevelToMap.createCopy(originalComparisonArea.getGeoLevelToMap());		
		cloneComparisonArea.setGeoLevelToMap(cloneGeoLevelToMap);;

		return cloneComparisonArea;
	}	
	
// ==========================================
// Section Accessors and Mutators
// ==========================================
	
	
	public void identifyDifferences(
		final ComparisonArea anotherComparisonArea,
		final ArrayList<String> differences) {
		
		super.identifyDifferences(
			anotherComparisonArea, 
			differences);
	}
	
    /**
     * Checks for identical contents.
     *
     * @param otherComparisonArea the other comparison area
     * @return true, if successful
     */
	public boolean hasIdenticalContents(
		final ComparisonArea otherComparisonArea) {
    	
    	if (otherComparisonArea == null) {
    		return false;
    	}
    	
    	return super.hasIdenticalContents(otherComparisonArea);
    }
// ==========================================
// Section Errors and Validation
// ==========================================
	

	/* (non-Javadoc)
	 * @see rifServices.businessConceptLayer.AbstractGeographicalArea#checkSecurityViolations()
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
		
		//do security checks on String area identifiers
		ArrayList<String> errorMessages = new ArrayList<String>();
		super.checkErrors(errorMessages);
		countErrors(RIFServiceError.INVALID_COMPARISON_AREA, errorMessages);
	}
	
// ==========================================
// Section Interfaces
// ==========================================

// ==========================================
// Section Override
// ==========================================
	
	@Override
	public String getRecordType() {
		
		String recordTypeLabel
			= RIFServiceMessages.getMessage("comparisonArea.label");
		return recordTypeLabel;
	}
	
}
