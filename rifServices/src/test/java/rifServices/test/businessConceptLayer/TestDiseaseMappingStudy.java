package rifServices.test.businessConceptLayer;


import rifServices.SampleTestObjectGenerator;

import rifServices.businessConceptLayer.*;
import rifServices.system.RIFServiceException;
import rifServices.system.RIFServiceSecurityException;
import rifServices.system.RIFServiceError;
import rifServices.test.AbstractRIFTestCase;
import static org.junit.Assert.*;

import org.junit.Test;


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

public class TestDiseaseMappingStudy extends AbstractRIFTestCase {

	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================
	/** The master disease mapping study. */
	private DiseaseMappingStudy masterDiseaseMappingStudy;
	
	// ==========================================
	// Section Construction
	// ==========================================

	/**
	 * Instantiates a new test disease mapping study.
	 */
	public TestDiseaseMappingStudy() {
		SampleTestObjectGenerator generator
			= new SampleTestObjectGenerator();
		masterDiseaseMappingStudy 
			= generator.createSampleDiseaseMappingStudy();
	}
	
	// ==========================================
	// Section Accessors and Mutators
	// ==========================================

	/**
	 * Accept valid disease mapping study.
	 */
	@Test
	/**
	 * Accept a valid disease mapping study with typical fields
	 */
	public void acceptValidDiseaseMappingStudy() {
		try {
			DiseaseMappingStudy diseaseMappingStudy
				= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
			diseaseMappingStudy.checkErrors();
		}
		catch(RIFServiceException rifServiceException) {
			fail();
		}
	}
	
	/**
	 * Reject blank name.
	 */
	@Test
	/**
	 * A disease mapping study is invalid if it has a blank name
	 */
	public void rejectBlankName() {
		try {
			DiseaseMappingStudy diseaseMappingStudy
				= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
			diseaseMappingStudy.setName(null);
			diseaseMappingStudy.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException, 
				RIFServiceError.INVALID_DISEASE_MAPPING_STUDY, 
				1);			
		}
		
		try {
			DiseaseMappingStudy diseaseMappingStudy
				= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
			diseaseMappingStudy.setName("");
			diseaseMappingStudy.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException, 
				RIFServiceError.INVALID_DISEASE_MAPPING_STUDY, 
				1);			
		}
	}
	
	/**
	 * Reject blank description.
	 */
	@Test
	/**
	 * A disease mapping study is invalid if it has a blank description
	 */
	public void rejectBlankDescription() {
		try {
			DiseaseMappingStudy diseaseMappingStudy
				= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
			diseaseMappingStudy.setDescription(null);
			diseaseMappingStudy.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException, 
				RIFServiceError.INVALID_DISEASE_MAPPING_STUDY, 
				1);			
		}
		
		try {
			DiseaseMappingStudy diseaseMappingStudy
				= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
			diseaseMappingStudy.setDescription("");
			diseaseMappingStudy.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException, 
				RIFServiceError.INVALID_DISEASE_MAPPING_STUDY, 
				1);			
		}		
	}
	
	/**
	 * Reject empty comparison area.
	 */
	@Test
	/**
	 * A disease mapping study is invalid if no comparison area is specified.
	 */
	public void rejectEmptyComparisonArea() {
		try {
			DiseaseMappingStudy diseaseMappingStudy
				= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
			diseaseMappingStudy.setComparisonArea(null);
			diseaseMappingStudy.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException, 
				RIFServiceError.INVALID_DISEASE_MAPPING_STUDY, 
				1);			
		}		
	}
	
	/**
	 * Reject empty investigation list.
	 */
	@Test
	/**
	 * A disease mapping study is invalid if no investigations are specified
	 */
	public void rejectEmptyInvestigationList() {
		
		try {
			DiseaseMappingStudy diseaseMappingStudy
				= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
			diseaseMappingStudy.setInvestigations(null);
			diseaseMappingStudy.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException, 
				RIFServiceError.INVALID_DISEASE_MAPPING_STUDY, 
				1);			
		}		
		
		try {
			DiseaseMappingStudy diseaseMappingStudy
				= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
			diseaseMappingStudy.clearInvestigations();
			diseaseMappingStudy.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException, 
				RIFServiceError.INVALID_DISEASE_MAPPING_STUDY, 
				1);			
		}
	}
	
	/**
	 * Reject invalid comparison area.
	 */
	@Test
	/**
	 * A disease mapping study is invalid if its comparison area is invalid
	 */
	public void rejectInvalidComparisonArea() {
		//Intentionally creating a comparison area that has an invalid
		//GeoLevelArea.  This should be picked up when disease mapping study
		//validates
		GeoLevelSelect geoLevelSelect = GeoLevelSelect.newInstance("LEVEL2");
		
		//injecting error
		GeoLevelArea invalidGeoLevelArea = GeoLevelArea.newInstance(null);

		GeoLevelView geoLevelView = GeoLevelView.newInstance("LEVEL3");
		GeoLevelToMap geoLevelToMap = GeoLevelToMap.newInstance("LEVEL3");
		
		MapArea mapArea1 = MapArea.newInstance("111", "Brent");
		MapArea mapArea2 = MapArea.newInstance("222", "Barnet");
				
		ComparisonArea invalidComparisonArea
			= ComparisonArea.newInstance();
		invalidComparisonArea.addMapArea(mapArea1);
		invalidComparisonArea.addMapArea(mapArea2);
		invalidComparisonArea.setGeoLevelSelect(geoLevelSelect);
		invalidComparisonArea.setGeoLevelArea(invalidGeoLevelArea);
		invalidComparisonArea.setGeoLevelView(geoLevelView);
		invalidComparisonArea.setGeoLevelToMap(geoLevelToMap);

		try {		
			DiseaseMappingStudy diseaseMappingStudy
				= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
			diseaseMappingStudy.setComparisonArea(invalidComparisonArea);
			diseaseMappingStudy.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_DISEASE_MAPPING_STUDY,
				1);
		}		
	}
	
	/**
	 * Reject invalid investigation.
	 */
	@Test
	/**
	 * A disease mapping study is invalid if it has an invalid investigation
	 */	
	public void rejectInvalidInvestigation() {
		SampleTestObjectGenerator generator 
			= new SampleTestObjectGenerator();
		Investigation invalidInvestigation
			= generator.createSampleInvestigation("This is an invalid investigation");
		HealthCode invalidHealthCode 
			= HealthCode.newInstance("", null, "", true);
		invalidInvestigation.addHealthCode(invalidHealthCode);
		
		try {
			
			DiseaseMappingStudy diseaseMappingStudy
				= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
			diseaseMappingStudy.addInvestigation(invalidInvestigation);
			diseaseMappingStudy.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_DISEASE_MAPPING_STUDY,
				3);
		}		
	}
	
	/**
	 * Test security violations.
	 */
	@Test
	public void testSecurityViolations() {
		DiseaseMappingStudy maliciousDiseaseMappingStudy
			= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
		maliciousDiseaseMappingStudy.setIdentifier(getTestMaliciousValue());
		try {
			maliciousDiseaseMappingStudy.checkSecurityViolations();
			fail();
		}
		catch(RIFServiceSecurityException rifServiceSecurityException) {
			//pass
		}
		
		
		maliciousDiseaseMappingStudy
			= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
		maliciousDiseaseMappingStudy.setName(getTestMaliciousValue());
		try {
			maliciousDiseaseMappingStudy.checkSecurityViolations();
			fail();
		}
		catch(RIFServiceSecurityException rifServiceSecurityException) {
			//pass
		}
		
		
		maliciousDiseaseMappingStudy
			= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
		maliciousDiseaseMappingStudy.setDescription(getTestMaliciousValue());
		try {
			maliciousDiseaseMappingStudy.checkSecurityViolations();
			fail();
		}
		catch(RIFServiceSecurityException rifServiceSecurityException) {
			//pass
		}

		
		maliciousDiseaseMappingStudy
			= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
		maliciousDiseaseMappingStudy.setKnownIssues(getTestMaliciousValue());
		try {
			maliciousDiseaseMappingStudy.checkSecurityViolations();
			fail();
		}
		catch(RIFServiceSecurityException rifServiceSecurityException) {
			//pass
		}
		
		maliciousDiseaseMappingStudy
			= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
		Geography maliciousGeography
			= Geography.newInstance(getTestMaliciousValue(), "not much description");
		maliciousDiseaseMappingStudy.setGeography(maliciousGeography);		
		try {
			maliciousDiseaseMappingStudy.checkSecurityViolations();
			fail();
		}
		catch(RIFServiceSecurityException rifServiceSecurityException) {
			//pass
		}

		maliciousDiseaseMappingStudy
			= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
		ComparisonArea maliciousComparisonArea
			= maliciousDiseaseMappingStudy.getComparisonArea();
		maliciousComparisonArea.setIdentifier(getTestMaliciousValue());
		try {
			maliciousDiseaseMappingStudy.checkSecurityViolations();
			fail();
		}
		catch(RIFServiceSecurityException rifServiceSecurityException) {
			//pass
		}
		
		maliciousDiseaseMappingStudy
			= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
		DiseaseMappingStudyArea diseaseMappingStudyArea
			= maliciousDiseaseMappingStudy.getDiseaseMappingStudyArea();
		MapArea maliciousMapArea = MapArea.newInstance("454", getTestMaliciousValue());
		diseaseMappingStudyArea.addMapArea(maliciousMapArea);
		try {
			maliciousDiseaseMappingStudy.checkSecurityViolations();
			fail();
		}
		catch(RIFServiceSecurityException rifServiceSecurityException) {
			//pass
		}
		
		maliciousDiseaseMappingStudy
			= DiseaseMappingStudy.createCopy(masterDiseaseMappingStudy);
		Investigation maliciousInvestigation
			= Investigation.newInstance();
		maliciousInvestigation.setTitle(getTestMaliciousValue());
		maliciousDiseaseMappingStudy.addInvestigation(maliciousInvestigation);
		try {
			maliciousDiseaseMappingStudy.checkSecurityViolations();
			fail();
		}
		catch(RIFServiceSecurityException rifServiceSecurityException) {
			//pass
		}		
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