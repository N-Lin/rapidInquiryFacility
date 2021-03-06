package rifDataLoaderTool.businessConceptLayer;

import rifDataLoaderTool.system.RIFDataLoaderToolError;
import rifDataLoaderTool.system.RIFDataLoaderToolMessages;
import rifServices.system.RIFServiceMessages;
import rifServices.system.RIFServiceSecurityException;
import rifServices.system.RIFServiceException;
import rifServices.util.FieldValidationUtility;

import java.util.ArrayList;

/**
 * A rule that is used to generate SQL code that can search and replace values. Apart from
 * name and description fields, the three main important fields are:
 * <ul>
 * <li><b>searchValue</b>: which can just be a phrase or some complex regular expression </li>
 * <li><b>replaceValue</b>: the value to be used to replace an existing flawed value</li>
 * <li><b>isRegularExpressionSearch</b>: determines whether the search value should be
 * treated as part of a simple search and replace statement or if it should be phrased as
 * a search that involves regular expressions.  
 * </li> 
 * </ul>
 *
 *<p>
 * This flag helps inform the SQL code generation classes about how to write the rule.  For example,
 * a statement could be: 
 * </p>
 * <p>
 * <pre>
 * CASE
 *    WHEN sex='M' THEN 0
 *    ...
 * END
 * </pre>
 * 
 * <p>
 * or
 *<p>
 *<pre>
 * CASE
 *    WHEN sex ~ '^[mM] THEN 0
 *    ...
 * END
 *
 * <hr>
 * Copyright 2014 Imperial College London, developed by the Small Area
 * Health Statistics Unit. 
 *
 * <pre> 
 * This file is part of the Rapid Inquiry Facility (RIF) project.
 * RIF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * RIF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RIF.  If not, see <http://www.gnu.org/licenses/>.
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

public final class CleaningRule 
	extends AbstractRIFDataLoaderToolConcept {

	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================
		
	private String name;
	private String description;
	private String searchValue;
	private String replaceValue;
	
	private boolean isRegularExpressionSearch;
	
	// ==========================================
	// Section Construction
	// ==========================================

	private CleaningRule() {
		name = "";
		description = "";
		searchValue = "";
		replaceValue = "";
	}
	
	private CleaningRule(
		final String name,
		final String description,
		final String searchValue,
		final String replaceValue,
		final boolean isRegularExpressionSearch) {

		this.name = name;
		this.description = description;
		this.searchValue = searchValue;
		this.replaceValue = replaceValue;
		this.isRegularExpressionSearch = isRegularExpressionSearch;
	}

	public static CleaningRule newInstance() {
		CleaningRule cleaningRule
			= new CleaningRule();		
		return cleaningRule;
	}
	
	public static CleaningRule newInstance(
		final String name,
		final String description,
		final String searchValue,
		final String replaceValue,
		final boolean isRegularExpressionSearch) {
		
		CleaningRule cleaningRule
			= new CleaningRule(
				name,
				description,
				searchValue,
				replaceValue,
				isRegularExpressionSearch);
		return cleaningRule;
	}

	public static CleaningRule createCopy(
		final CleaningRule originalCleaningRule) {
		
		CleaningRule cloneCleaningRule
			= new CleaningRule();
		cloneCleaningRule.setName(originalCleaningRule.getName());
		cloneCleaningRule.setDescription(originalCleaningRule.getDescription());
		cloneCleaningRule.setRegularExpressionSearch(originalCleaningRule.isRegularExpressionSearch());
		cloneCleaningRule.setSearchValue(originalCleaningRule.getSearchValue());
		cloneCleaningRule.setReplaceValue(originalCleaningRule.getReplaceValue());
			
		return cloneCleaningRule;
	}

	// ==========================================
	// Section Accessors and Mutators
	// ==========================================
		
	public String getName() {
		return name;
	}

	public void setName(
		final String name) {

		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(
		final String description) {

		this.description = description;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(
		final String searchValue) {

		this.searchValue = searchValue;
	}

	public String getReplaceValue() {
		return replaceValue;
	}

	public void setReplaceValue(
		final String replaceValue) {

		this.replaceValue = replaceValue;
	}

	public boolean isRegularExpressionSearch() {
		return isRegularExpressionSearch;
	}

	public void setRegularExpressionSearch(
		final boolean isRegularExpressionSearch) {

		this.isRegularExpressionSearch = isRegularExpressionSearch;
	}
		
	// ==========================================
	// Section Errors and Validation
	// ==========================================

	public void checkSecurityViolations() 
		throws RIFServiceSecurityException {

		FieldValidationUtility fieldValidationUtility
			= new FieldValidationUtility();
		String recordType = getRecordType();

		if (name != null) {
			String nameField
				= RIFDataLoaderToolMessages.getMessage("cleaningRule.name.label");		
			fieldValidationUtility.checkMaliciousCode(
				recordType,
				nameField,
				name);
		}

		if (description != null) {
			String descriptionFieldName
				= RIFDataLoaderToolMessages.getMessage("cleaningRule.description.label");		
			fieldValidationUtility.checkMaliciousCode(
				recordType,
				descriptionFieldName,
				description);
		}
		
		if (searchValue != null) {
			String searchValueFieldName
				= RIFDataLoaderToolMessages.getMessage("cleaningRule.searchValue.label");		
			fieldValidationUtility.checkMaliciousCode(
				recordType,
				searchValueFieldName,
				searchValue);
		}
				
		if (replaceValue != null) {
			String replaceValueFieldName
				= RIFDataLoaderToolMessages.getMessage("cleaningRule.replaceValue.label");		
			fieldValidationUtility.checkMaliciousCode(
				recordType,
				replaceValueFieldName,
				replaceValue);
		}

	}	
	
	public void checkErrors() throws RIFServiceException {
		String recordType
			= getRecordType();
		
		String nameField
			= RIFDataLoaderToolMessages.getMessage("cleaningRule.name.label");
		String descriptionFieldName
			= RIFDataLoaderToolMessages.getMessage("cleaningRule.description.label");
		String searchFieldName
			= RIFDataLoaderToolMessages.getMessage("cleaningRule.searchValue.label");
		String replaceFieldName
			= RIFDataLoaderToolMessages.getMessage("cleaningRule.replaceValue.label");

		ArrayList<String> errorMessages = new ArrayList<String>();
		FieldValidationUtility fieldValidationUtility
			= new FieldValidationUtility();
		
		if (fieldValidationUtility.isEmpty(name)) {
			String errorMessage
				= RIFServiceMessages.getMessage(
					"general.validation.emptyRequiredRecordField", 
					recordType,
					name);
			errorMessages.add(errorMessage);			
		}

		
		if (fieldValidationUtility.isEmpty(searchValue)) {
			String errorMessage
				= RIFServiceMessages.getMessage(
					"general.validation.emptyRequiredRecordField", 
					recordType,
					searchValue);
			errorMessages.add(errorMessage);			
		}
				
		if (fieldValidationUtility.isEmpty(replaceValue)) {
			String errorMessage
				= RIFServiceMessages.getMessage(
					"general.validation.emptyRequiredRecordField", 
					recordType,
					replaceValue);
			errorMessages.add(errorMessage);			
		}
		
		countErrors(
			RIFDataLoaderToolError.INVALID_CLEANING_RULE, 
			errorMessages);		
		
	}
	
	public String getRecordType() {
		String recordType
			= RIFDataLoaderToolMessages.getMessage("cleaningRule.label");
		return recordType;
	}
	
	// ==========================================
	// Section Interfaces
	// ==========================================

	//Interface: Display Name
	public String getDisplayName() {
		return name;
	}
	
	// ==========================================
	// Section Override
	// ==========================================

}


