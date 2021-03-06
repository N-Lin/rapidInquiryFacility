
package rifServices.fileFormats;

import rifGenericLibrary.presentationLayer.HTMLUtility;
import rifServices.businessConceptLayer.RIFOutputOption;
import rifServices.system.RIFServiceMessages;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;
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


final class RIFOutputOptionContentHandler 
	extends AbstractRIFConceptContentHandler {

// ==========================================
// Section Constants
// ==========================================

// ==========================================
// Section Properties
// ==========================================
	/** The current rif output options. */
	private ArrayList<RIFOutputOption> currentRIFOutputOptions;
    
// ==========================================
// Section Construction
// ==========================================
    /**
     * Instantiates a new RIF output option content handler.
     */
	public RIFOutputOptionContentHandler() {
		
    	setPluralRecordName("rif_output_options");
    	setSingularRecordName("rif_output_option");
    	currentRIFOutputOptions = new ArrayList<RIFOutputOption>();
    }

// ==========================================
// Section Accessors and Mutators
// ==========================================
    
    /**
     * Gets the RIF output options.
     *
     * @return the RIF output options
     */
	public ArrayList<RIFOutputOption> getRIFOutputOptions() {
		
    	return currentRIFOutputOptions;
    }
	
    /**
     * Write html.
     *
     * @param headerLevel the header level
     * @param rifOutputOptions the rif output options
     * @param isFragmentWithinLargerReport the is fragment within larger report
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void writeHTML(
    	final int headerLevel,
    	final ArrayList<RIFOutputOption> rifOutputOptions,
    	final boolean isFragmentWithinLargerReport) 
    	throws IOException {

    	HTMLUtility htmlUtility = getHTMLUtility();
    	
    	if (isFragmentWithinLargerReport == false) {
    		htmlUtility.beginDocument();
    	}
    	
    	String title
    		= RIFServiceMessages.getMessage("rifOutputOption.plural.label");
    	htmlUtility.writeHeader(headerLevel, title);
    		
    	htmlUtility.beginBulletedList();
		for (RIFOutputOption rifOutputOption : rifOutputOptions) {
			htmlUtility.writeBulletedItem(rifOutputOption.getName());
		}
    	htmlUtility.endBulletedList();
    	
    	if (isFragmentWithinLargerReport == false) {
    		htmlUtility.endDocument();
    	} 	 	
    }
    
    /**
     * Write xml.
     *
     * @param rifOutputOptions the rif output options
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void writeXML(
    	final ArrayList<RIFOutputOption> rifOutputOptions) 
    	throws IOException {
    	
		String recordName = getPluralRecordName();
		XMLUtility xmlUtility = getXMLUtility();
		
		xmlUtility.writeRecordStartTag(recordName);	
		for (RIFOutputOption rifOutputOption : rifOutputOptions) {
			xmlUtility.writeField(
				recordName, 
				getPluralRecordName(), 
				rifOutputOption.getName());			
		}
		
		xmlUtility.writeRecordEndTag(recordName);	
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

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
    @Override
    public void startElement(
		final String nameSpaceURI,
		final String localName,
		final String qualifiedName,
		final Attributes attributes) 
		throws SAXException {

		if (equalsFieldName(qualifiedName, getPluralRecordName()) == true) {
			activate();
		}		
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(
		final String nameSpaceURI,
		final String localName,
		final String qualifiedName) 
		throws SAXException {
				
		if (isPluralRecordName(qualifiedName)) {
			deactivate();
		}
		else if (isSingularRecordName(qualifiedName)) {
			RIFOutputOption rifOutputOption
				= RIFOutputOption.getOptionFromPhrase(getCurrentFieldValue());
			currentRIFOutputOptions.add(rifOutputOption);
		}
		else {
			assert false;
		}
	}
}
