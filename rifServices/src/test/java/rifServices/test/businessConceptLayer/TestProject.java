package rifServices.test.businessConceptLayer;

import rifServices.system.RIFServiceError;

import rifServices.system.RIFServiceException;
import rifServices.system.RIFServiceSecurityException;
import rifServices.test.AbstractRIFTestCase;
import rifServices.businessConceptLayer.Project;
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

public class TestProject extends AbstractRIFTestCase {

	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================
	/** The master project. */
	private Project masterProject;
	
	// ==========================================
	// Section Construction
	// ==========================================

	/**
	 * Instantiates a new test project.
	 */
	public TestProject() {
		masterProject = Project.newInstance();
		masterProject.setName("project1");
		masterProject.setDescription("description1");
		masterProject.setStartDate("11-JUN-2005");
		masterProject.setEndDate("18-JUN-2005");		
	}

	// ==========================================
	// Section Accessors and Mutators
	// ==========================================

	/**
	 * Accept valid project.
	 */
	@Test
	public void acceptValidProject() {		
		Project project = Project.createCopy(masterProject);
		try {
			project.checkErrors();
		}
		catch(RIFServiceException rifServiceException) {
			fail();		
		}
	}
	
	/**
	 * Reject blank fields.
	 */
	@Test
	public void rejectBlankFields() {
		try {
			Project testProject = Project.createCopy(masterProject);
			testProject.setName(null);
			testProject.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_PROJECT,
				1);			
		}

		try {
			Project testProject = Project.createCopy(masterProject);
			testProject.setName("");
			testProject.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_PROJECT,
				1);			
		}
	
		try {
			Project testProject = Project.createCopy(masterProject);
			testProject.setDescription(null);
			testProject.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_PROJECT,
				1);			
		}	
		
		try {
			Project testProject = Project.createCopy(masterProject);
			testProject.setDescription("");
			testProject.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_PROJECT,
				1);			
		}			

		try {
			Project testProject = Project.createCopy(masterProject);
			testProject.setStartDate(null);
			testProject.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_PROJECT,
				1);			
		}			
		
		try {
			Project testProject = Project.createCopy(masterProject);
			testProject.setStartDate("");
			testProject.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_PROJECT,
				1);			
		}			
		
		try {
			Project testProject = Project.createCopy(masterProject);
			testProject.setEndDate(null);
			testProject.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_PROJECT,
				1);			
		}			
		
		try {
			Project testProject = Project.createCopy(masterProject);
			testProject.setEndDate("");
			testProject.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_PROJECT,
				1);			
		}			
		
	}
	
	/**
	 * Reject multiple field errors.
	 */
	@Test
	public void rejectMultipleFieldErrors() {
		try {
			Project testProject = Project.createCopy(masterProject);
			testProject.setName("");
			testProject.setDescription(null);
			testProject.setStartDate("15-XXX-2006");
			testProject.setEndDate("15-JAN-QTQ3");
			testProject.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(
				rifServiceException,
				RIFServiceError.INVALID_PROJECT,
				4);			
		}
	}
	
	/**
	 * Test security violations.
	 */
	@Test
	public void testSecurityViolations() {
		Project maliciousProject = Project.createCopy(masterProject);
		maliciousProject.setIdentifier(getTestMaliciousValue());
		try {
			maliciousProject.checkSecurityViolations();	
			fail();
		}
		catch(RIFServiceSecurityException rifServiceSecurityException) {
			//pass
		}
		
		maliciousProject = Project.createCopy(masterProject);
		maliciousProject.setName(getTestMaliciousValue());
		try {
			maliciousProject.checkSecurityViolations();	
			fail();
		}
		catch(RIFServiceSecurityException rifServiceSecurityException) {
			//pass
		}
		
		maliciousProject = Project.createCopy(masterProject);
		maliciousProject.setDescription(getTestMaliciousValue());
		try {
			maliciousProject.checkSecurityViolations();	
			fail();
		}
		catch(RIFServiceSecurityException rifServiceSecurityException) {
			//pass
		}
		
		maliciousProject = Project.createCopy(masterProject);
		maliciousProject.setStartDate(getTestMaliciousValue());
		try {
			maliciousProject.checkSecurityViolations();	
			fail();
		}
		catch(RIFServiceSecurityException rifServiceSecurityException) {
			//pass
		}

		maliciousProject = Project.createCopy(masterProject);
		maliciousProject.setEndDate(getTestMaliciousValue());
		try {
			maliciousProject.checkSecurityViolations();	
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