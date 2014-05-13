package rifServices.test.businessConceptLayer;

import java.util.ArrayList;


import rifServices.businessConceptLayer.MapArea;
import rifServices.system.RIFServiceException;
import rifServices.system.RIFServiceSecurityException;
import rifServices.system.RIFServiceError;
import rifServices.test.AbstractRIFTestCase;
import rifServices.util.DisplayableItemSorter;
import rifServices.businessConceptLayer.DisplayableListItem;
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

public class TestMapArea extends AbstractRIFTestCase {

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
	 * Instantiates a new test map area.
	 */
	public TestMapArea() {

	}

	/**
	 * Accept valid map area.
	 */
	@Test
	/**
	 * Accept a valid map area with typical values.
	 */
	public void acceptValidMapArea() {
		MapArea mapArea = MapArea.newInstance("123", "Brent");
		try {
			mapArea.checkErrors();
		}
		catch(RIFServiceException rifServiceException) {
			fail();
		}
	}
	
	/**
	 * A map area is invalid if it has a blank identifier.
	 */
	@Test	
	public void rejectBlankIdentifier() {
		try {
			MapArea mapArea = MapArea.newInstance(null, "Brent");
			mapArea.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(rifServiceException, RIFServiceError.INVALID_MAP_AREA, 1);
		}

		try {
			MapArea mapArea = MapArea.newInstance("", "Brent");
			mapArea.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(rifServiceException, RIFServiceError.INVALID_MAP_AREA, 1);
		}
	}
	
	/**
	 * A map area is invalid if it has a blank label.
	 */
	
	/*
	public void rejectBlankLabel() {
		try {
			MapArea mapArea = MapArea.newInstance("123", null);
			mapArea.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(rifServiceException, RIFServiceError.INVALID_MAP_AREA, 1);
		}
		
		try {
			MapArea mapArea = MapArea.newInstance("123", null);
			mapArea.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(rifServiceException, RIFServiceError.INVALID_MAP_AREA, 1);
		}		
	}
	*/

	/**
	 * A map area is invalid if it has multiple field errors.
	 */
	
	/**
	 * @TODO: KLG: Note: eventually we need to uncomment
	 * this test case when we know all of the labels in the
	 * test data set are guaranteed to be filled in
	 */
	/*
	public void invalidMapAreaMultipleErrorsE1() {
		MapArea mapArea = MapArea.newInstance(null, "");
		try {
			mapArea.checkErrors();
			fail();
		}
		catch(RIFServiceException rifServiceException) {
			checkErrorType(rifServiceException, RIFServiceError.INVALID_MAP_AREA, 2);
		}		
	}
	*/
	
	/**
	 * A utility method should be able to identify map areas common to two
	 * collections of map areas.
	 */
	@Test
	public void identifyMapAreasInListAListB() {
		//List Destination: {24, 25, 26, 27, 28, 40}
		ArrayList<MapArea> destinationList = new ArrayList<MapArea>();
		destinationList.add(MapArea.newInstance("24", "24"));
		destinationList.add(MapArea.newInstance("25", "25"));
		destinationList.add(MapArea.newInstance("26", "26"));
		destinationList.add(MapArea.newInstance("27", "27"));
		destinationList.add(MapArea.newInstance("28", "28"));
		destinationList.add(MapArea.newInstance("40", "40"));

		//List Source: {21, 25, 27, 40, 92}
		ArrayList<MapArea> sourceList = new ArrayList<MapArea>();
		sourceList.add(MapArea.newInstance("21", "21"));
		sourceList.add(MapArea.newInstance("25", "25"));
		sourceList.add(MapArea.newInstance("27", "27"));
		sourceList.add(MapArea.newInstance("40", "40"));
		sourceList.add(MapArea.newInstance("92", "92"));
		
		//duplicates found in Source: {25, 27, 40}
		ArrayList<MapArea> duplicates
			= MapArea.identifyListOfDuplicates(sourceList, destinationList);
		
		ArrayList<DisplayableListItem> sortedResults = sortItems(duplicates);		
		assertEquals(3, duplicates.size());
		assertEquals("25", sortedResults.get(0).getIdentifier());
		assertEquals("27", sortedResults.get(1).getIdentifier());
		assertEquals("40", sortedResults.get(2).getIdentifier());
		
	}
	
	/**
	 * Identify map areas in list a not list b.
	 */
	@Test
	/**
	 * A utility should identify map areas found in one list of 
	 * map areas but not another.
	 */
	public void identifyMapAreasInListANotListB() {
		//List Destination: {24, 25, 26, 27, 28, 40}
		//List Source: {21, 25, 27, 40, 92}

		//List Destination: {24, 25, 26, 27, 28, 40}
		ArrayList<MapArea> destinationList = new ArrayList<MapArea>();
		destinationList.add(MapArea.newInstance("24", "24"));
		destinationList.add(MapArea.newInstance("25", "25"));
		destinationList.add(MapArea.newInstance("26", "26"));
		destinationList.add(MapArea.newInstance("27", "27"));
		destinationList.add(MapArea.newInstance("28", "28"));
		destinationList.add(MapArea.newInstance("40", "40"));

		//List Source: {21, 25, 27, 40, 92}
		ArrayList<MapArea> sourceList = new ArrayList<MapArea>();
		sourceList.add(MapArea.newInstance("21", "21"));
		sourceList.add(MapArea.newInstance("25", "25"));
		sourceList.add(MapArea.newInstance("27", "27"));
		sourceList.add(MapArea.newInstance("40", "40"));
		sourceList.add(MapArea.newInstance("92", "92"));
		
		//non-duplicates in B: {21, 92}
		ArrayList<MapArea> nonDuplicates
			= MapArea.identifyListOfUniqueAreas(sourceList, destinationList);
		assertNotNull(nonDuplicates);
		
		ArrayList<DisplayableListItem> sortedResults
			= sortItems(nonDuplicates);	
		
		assertEquals(2, sortedResults.size());
		assertEquals("21", sortedResults.get(0).getIdentifier());
		assertEquals("92", sortedResults.get(1).getIdentifier());
	}
	
	/**
	 * Sort items.
	 *
	 * @param mapAreas the map areas
	 * @return the array list
	 */
	private ArrayList<DisplayableListItem> sortItems(ArrayList<MapArea> mapAreas) {
		DisplayableItemSorter sorter = new DisplayableItemSorter();

		for (MapArea mapArea : mapAreas) {
			sorter.addDisplayableListItem(mapArea);
		}
		
		return sorter.sortList();
	}
	
	/**
	 * Test security violations.
	 */
	@Test
	public void testSecurityViolations() {
		MapArea maliciousMapArea 
			= MapArea.newInstance(getTestMaliciousValue(), "Brent");
		try {
			maliciousMapArea.checkSecurityViolations();
			fail();
		}
		catch(RIFServiceSecurityException rifServiceSecurityException) {
			//pass
		}
		
		maliciousMapArea 
			= MapArea.newInstance("123", getTestMaliciousValue());
		try {
			maliciousMapArea.checkSecurityViolations();
			fail();
		}
		catch(RIFServiceSecurityException rifServiceSecurityException) {
			//pass
		}
		
		
		
	}
	
	// ==========================================
	// Section Accessors and Mutators
	// ==========================================

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