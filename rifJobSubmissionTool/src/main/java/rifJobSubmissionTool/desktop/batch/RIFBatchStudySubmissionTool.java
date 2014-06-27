package rifJobSubmissionTool.desktop.batch;

import rifJobSubmissionTool.desktop.interactive.ErrorDialog;
import rifJobSubmissionTool.system.RIFSession;
import rifJobSubmissionTool.system.RIFJobSubmissionToolMessages;
import rifJobSubmissionTool.util.UserInterfaceFactory;
import rifServices.ProductionRIFJobSubmissionService;
import rifServices.businessConceptLayer.RIFJobSubmissionAPI;
import rifServices.businessConceptLayer.User;
import rifServices.system.RIFServiceException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;

import java.awt.GridBagConstraints;

/**
 *
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

public class RIFBatchStudySubmissionTool {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {		
			RIFJobSubmissionAPI service = new ProductionRIFJobSubmissionService();
			service.initialiseService();
			service.login("keving", new String("a").toCharArray());			
			

			String ipAddress = InetAddress.getLocalHost().getHostAddress();
			
			User testUser = User.newInstance("keving", ipAddress);
			RIFSession rifSession = new RIFSession(service, testUser);
						
			RIFBatchStudySubmissionTool rifBatchStudySubmissionTool
				= new RIFBatchStudySubmissionTool(rifSession);
			rifBatchStudySubmissionTool.show();
			service.logout(testUser);
			System.exit(0);
		}
		catch(UnknownHostException unknownHostException) {
			ErrorDialog.showError(null, unknownHostException.toString());			
		}
		catch(RIFServiceException rifServiceException) {
			ErrorDialog.showError(null, rifServiceException);
		}
	}

	
	
	
	// ==========================================
	// Section Constants
	// ==========================================

	// ==========================================
	// Section Properties
	// ==========================================
	
	//Data
	private RIFSession rifSession;
	
	//UI
	private UserInterfaceFactory userInterfaceFactory;
	private JDialog dialog;
	
	private JTextField selectedSourceDirectoryTextField;	
	private JButton browseSourceDirectoryButton;
	private JTextField selectedDestinationDirectoryTextField;	
	private JButton browseDestinationDirectoryButton;
		
	// ==========================================
	// Section Construction
	// ==========================================

	public RIFBatchStudySubmissionTool(RIFSession rifSession) {
		this.rifSession = rifSession;
		userInterfaceFactory = rifSession.getUIFactory();		
		
		String dialogTitle
			= RIFJobSubmissionToolMessages.getMessage("rifJobSubmissionTool.title");
		dialog = userInterfaceFactory.createDialog(dialogTitle);
	}

	private void buildUI() {
		JPanel panel = userInterfaceFactory.createPanel();
		GridBagConstraints panelGC 
			= userInterfaceFactory.createGridBagConstraints();
		
		//JLabel sourceDirectoryLabelText
		//	= RIFJobSubmissionToolMessages.getMessage("");
		
		//panel.add()
		
		
	}
	
	// ==========================================
	// Section Accessors and Mutators
	// ==========================================
	public void show() {
		dialog.setVisible(true);
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
