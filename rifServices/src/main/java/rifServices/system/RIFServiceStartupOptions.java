package rifServices.system;

import rifServices.util.FieldValidationUtility;

import java.io.File;
import java.util.ArrayList;


/**
 * Class that holds configuration settings for rif services.  These will appear
 * in <code>RIFServiceStartupProperties.properties</code>, a properties file
 * containing a list of name-value pairs
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

public final class RIFServiceStartupOptions {

	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================
	/** The database driver. */
	private String databaseDriver;
	
	/** The host. */
	private String host;
	
	/** The port. */
	private String port;
	
	/** The database name. */
	private String databaseName;
	
	/** The server side cache directory. */
	private File serverSideCacheDirectory;
		
	// ==========================================
	// Section Construction
	// ==========================================

	/**
	 * Instantiates a new RIF service startup options.
	 */
	public RIFServiceStartupOptions() {
		
		databaseDriver = "jdbc:postgresql";
		host = "localhost";
		port = "5432";
		databaseName = "sahsuland";
		serverSideCacheDirectory
			= new File("C:\\rif_stuff\\working_directory");
		
	}

	// ==========================================
	// Section Accessors and Mutators
	// ==========================================

	/**
	 * Gets the database driver.
	 *
	 * @return the database driver
	 */
	public String getDatabaseDriver() {
		
		return databaseDriver;
	}

	/**
	 * Sets the database driver.
	 *
	 * @param databaseDriver the new database driver
	 */
	public void setDatabaseDriver(
		final String databaseDriver) {

		this.databaseDriver = databaseDriver;
	}

	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public String getHost() {

		return host;
	}

	/**
	 * Sets the host.
	 *
	 * @param host the new host
	 */
	public void setHost(
		final String host) {

		this.host = host;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public String getPort() {

		return port;
	}

	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(
		final String port) {

		this.port = port;
	}

	/**
	 * Gets the database name.
	 *
	 * @return the database name
	 */
	public String getDatabaseName() {

		return databaseName;
	}

	/**
	 * Sets the database name.
	 *
	 * @param databaseName the new database name
	 */
	public void setDatabaseName(
		final String databaseName) {

		this.databaseName = databaseName;
	}
	
	/**
	 * Gets the server side cache directory.
	 *
	 * @return the server side cache directory
	 */
	public File getServerSideCacheDirectory() {

		return serverSideCacheDirectory;
	}
	
	/**
	 * Sets the server side cache directory.
	 *
	 * @param serverSideCacheDirectory the new server side cache directory
	 */
	public void setServerSideCacheDirectory(
		final File serverSideCacheDirectory) {

		this.serverSideCacheDirectory = serverSideCacheDirectory;
	}
	
	/**
	 * Gets the record type.
	 *
	 * @return the record type
	 */
	private String getRecordType() {
		
		String recordType
			= RIFServiceMessages.getMessage("rifServiceStartupOptions.label");
		return recordType;
	}
	
	// ==========================================
	// Section Errors and Validation
	// ==========================================
	
	/**
	 * Check security violations.
	 *
	 * @throws RIFServiceSecurityException the RIF service security exception
	 */
	public void checkSecurityViolations() 
		throws RIFServiceSecurityException {

		String recordType = getRecordType();

		FieldValidationUtility fieldValidationUtility
			= new FieldValidationUtility();

		if (databaseDriver != null) {
			String databaseDriverLabel
				= RIFServiceMessages.getMessage("rifServiceStartupOptions.databaseDriver.label");		
			fieldValidationUtility.checkMaliciousCode(
				recordType,
				databaseDriverLabel,
				databaseDriver);
		}

		if (host != null) {
			String hostLabel
				= RIFServiceMessages.getMessage("rifServiceStartupOptions.host.label");		
			fieldValidationUtility.checkMaliciousCode(
				recordType,
				hostLabel,
				host);
		}
		
		if (port != null) {
			String portLabel
				= RIFServiceMessages.getMessage("rifServiceStartupOptions.port.label");		
			fieldValidationUtility.checkMaliciousCode(
				recordType,
				portLabel,
				port);
		}
	
		if (databaseName != null) {
			String databaseNameLabel
				= RIFServiceMessages.getMessage("rifServiceStartupOptions.databaseName.label");		
			fieldValidationUtility.checkMaliciousCode(
				recordType,
				databaseNameLabel,
				databaseName);
		}		
	}
	
	/**
	 * Check errors.
	 *
	 * @throws RIFServiceException the RIF service exception
	 */
	public void checkErrors() 
		throws RIFServiceException {

		FieldValidationUtility fieldValidationUtility
			= new FieldValidationUtility();

		String recordType = getRecordType();
		ArrayList<String> errorMessages = new ArrayList<String>();
		
		if (fieldValidationUtility.isEmpty(databaseDriver)) {
			String databaseDriverLabel
				= RIFServiceMessages.getMessage("rifServiceStartupOptions.databaseDriver.label");
			String errorMessage
				= RIFServiceMessages.getMessage(
					"general.validation.emptyRequiredRecordField", 
					recordType,
					databaseDriverLabel);
			errorMessages.add(errorMessage);			
		}
		
		if (fieldValidationUtility.isEmpty(host)) {
			String hostLabel
				= RIFServiceMessages.getMessage("rifServiceStartupOptions.host.label");
			String errorMessage
				= RIFServiceMessages.getMessage(
					"general.validation.emptyRequiredRecordField", 
					recordType,
					hostLabel);
			errorMessages.add(errorMessage);
		}

		if (fieldValidationUtility.isEmpty(port)) {
			String portLabel
				= RIFServiceMessages.getMessage("rifServiceStartupOptions.port.label");
			String errorMessage
				= RIFServiceMessages.getMessage(
					"general.validation.emptyRequiredRecordField", 
					recordType,
					portLabel);
			errorMessages.add(errorMessage);
		}
		
		if (fieldValidationUtility.isEmpty(databaseName)) {
			String databaseNameLabel
				= RIFServiceMessages.getMessage("rifServiceStartupOptions.databaseName.label");
			String errorMessage
				= RIFServiceMessages.getMessage(
					"general.validation.emptyRequiredRecordField", 
					recordType,
					databaseNameLabel);
			errorMessages.add(errorMessage);
		}
		
		if (errorMessages.isEmpty() == false) {
			RIFServiceException rifServiceException
				= new RIFServiceException(
					RIFServiceError.INVALID_STARTUP_OPTIONS, 
					errorMessages);
			throw rifServiceException;
		}
	}
	
	// ==========================================
	// Section Interfaces
	// ==========================================

	// ==========================================
	// Section Override
	// ==========================================
}