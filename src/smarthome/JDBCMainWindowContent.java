package smarthome;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import java.sql.*;

@SuppressWarnings("serial")
public class JDBCMainWindowContent extends JInternalFrame implements ActionListener, ExportCSV {
	// DB Connectivity Attributes
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	
	private Container content;
	private static QueryTableModel TableModel = new QueryTableModel();

	// Table ComboBox
	private JComboBox<String> tableSelectionComboBox;
	private JLabel tableSelectionLabel;
	private String[] tableNames = { "device_type", "device_info", "device", "device_msg_log",
			"device_energy_log" };

	// Table Details Panel
	private JTable TableofDBContents = new JTable(TableModel);
	private JScrollPane dbTableContentsPanel;
	private Border lineBorder;

	// Table CRUD operations
	private TableCrudOperations currentCrudOperations;
	private JPanel crudFormPanel;
	private JPanel crudButtonPanel;
	private JButton insertButton, deleteButton, updateButton, exportButton, clearButton;

	// Table Export Data
	private JPanel exportButtonPanel;
	private JButton deviceTypeCount  			= new JButton("Types & Count");
	private JButton deviceLocationStatus  		= new JButton("Location & Status");
	private JButton deviceRanges  				= new JButton("Ranges");
	private JButton deviceSecurities  			= new JButton("Security Types");
	private JButton devicePower  				= new JButton("Active & Standby Power");
	private JButton deviceUsagePattern  		= new JButton("Usage Pattern");
	private JButton deviceAvgEnergyConsumptionByMonth	= new JButton("Monthly Avg Energy Consumption");
	private JButton deviceTopConsumptionByMonth			= new JButton("Monthly Highest Energy Consumption");
	
	public JDBCMainWindowContent(String aTitle) {
		// setting up the GUI
		super(aTitle, false, false, false, false);
		setEnabled(true);

		initiate_db_conn();

		// Set the layout for the main content pane
		content = getContentPane();
		content.setLayout(new BorderLayout());
		content.setBackground(Color.lightGray);

		// Table Selection panel
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		tableSelectionComboBox = new JComboBox<>(tableNames);
		tableSelectionComboBox.addActionListener(this);
		tableSelectionLabel = new JLabel("Select Table:");
		tableSelectionLabel.setLabelFor(tableSelectionComboBox);

		tableSelectionComboBox.addActionListener(e -> {
			String selectedTable = (String) tableSelectionComboBox.getSelectedItem();
			updateCrudPanel(selectedTable);
		});

		topPanel.add(tableSelectionLabel);
		topPanel.add(Box.createRigidArea(new Dimension(5, 0))); // Add some space
		topPanel.add(tableSelectionComboBox);
		

		// Table CRUD
		
		// Buttons
		crudButtonPanel = new JPanel();
		crudButtonPanel.setLayout(new GridLayout(3,2,5,5));

		insertButton = new JButton("Insert"); crudButtonPanel.add(insertButton);
		deleteButton = new JButton("Delete"); crudButtonPanel.add(deleteButton);
		updateButton = new JButton("Update"); crudButtonPanel.add(updateButton);
		exportButton = new JButton("Export"); crudButtonPanel.add(exportButton);
		clearButton = new JButton("Clear"); crudButtonPanel.add(clearButton);

		// Form
		crudFormPanel = new JPanel(new GridBagLayout());
		JScrollPane formScrollPane = new JScrollPane(
				crudFormPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Create the main JSplitPane to divide the window
        JSplitPane crudSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        										  formScrollPane,
        										  crudButtonPanel);
        crudSplitPane.setBorder(BorderFactory.createTitledBorder(lineBorder, "Table CRUD"));
        crudSplitPane.setResizeWeight(1.0); // Adjust as needed


		// Table Export
		exportButtonPanel=new JPanel();
		exportButtonPanel.setLayout(new GridLayout(4,2, 5, 5));
		exportButtonPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Export Data"));
		exportButtonPanel.add(deviceTypeCount);
		exportButtonPanel.add(deviceLocationStatus);
		exportButtonPanel.add(deviceRanges);
		exportButtonPanel.add(deviceSecurities);
		exportButtonPanel.add(devicePower);
		exportButtonPanel.add(deviceUsagePattern);
		exportButtonPanel.add(deviceAvgEnergyConsumptionByMonth);
		exportButtonPanel.add(deviceTopConsumptionByMonth);
		
		// Table Details panel
		TableofDBContents.setPreferredScrollableViewportSize(new Dimension(900, 300));
		customizeTableHeader();
		dbTableContentsPanel = new JScrollPane(TableofDBContents, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		dbTableContentsPanel.setBackground(Color.lightGray);
		dbTableContentsPanel.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(lineBorder, "Table Content"),
						BorderFactory.createEmptyBorder(5, 5, 5, 5))); // Add padding within the border

	
		 // Create the main JSplitPane to divide the window
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        										  crudSplitPane,
                                                  dbTableContentsPanel);
        mainSplitPane.setResizeWeight(0.3); // Adjust as needed

        // Create a secondary JSplitPane to include the Export panel on the bottom left
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        										  dbTableContentsPanel,
                                                  exportButtonPanel);
        rightSplitPane.setResizeWeight(0.8); // Adjust to give more space to either the CRUD or export panel

        // Add the leftSplitPane to the mainSplitPane
        mainSplitPane.setRightComponent(rightSplitPane);

        // CRUD
		insertButton.addActionListener(this);
		deleteButton.addActionListener(this);
		updateButton.addActionListener(this);
		exportButton.addActionListener(this);
		clearButton.addActionListener(this);
		// Export
		deviceTypeCount.addActionListener(this);
		deviceLocationStatus.addActionListener(this);
		deviceRanges.addActionListener(this);
		deviceSecurities.addActionListener(this);
		devicePower.addActionListener(this);
		deviceUsagePattern.addActionListener(this);
		deviceAvgEnergyConsumptionByMonth.addActionListener(this);
		deviceTopConsumptionByMonth.addActionListener(this);
		
		// Add Panels to content pane
		content.add(topPanel, BorderLayout.NORTH);
        content.add(mainSplitPane, BorderLayout.CENTER);

		// Set the initial state
		setSize(982, 645);
		setVisible(true);

		updateCrudPanel((String) tableSelectionComboBox.getSelectedItem());

		// Refresh table model
		TableModel.refreshFromDB(stmt, (String) tableSelectionComboBox.getSelectedItem());
	}

	private void customizeTableHeader() {
		JTableHeader header = TableofDBContents.getTableHeader();
		// Increase the font size, set bold style and center
		Font headerFont = new Font("Segoe UI", Font.BOLD, 12);
		header.setFont(headerFont);
		((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
	}

	public void initiate_db_conn() {
		try {
			String url = "jdbc:mysql://localhost:3306/SmartHomeEnergyMonitoring"; 	// DB Name
			con = DriverManager.getConnection(url, "root", "root"); 				// DB Connection
			stmt = con.createStatement();
		} catch (Exception e) {
			System.out.println("Error: Failed to connect to database\n" + e.getMessage());
		}
	}

	private void updateCrudPanel(String selectedTable) {
							
		crudFormPanel.removeAll();								// Clear the existing crudPanel's contents

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5); 


		switch (selectedTable) {
		case "device_type":
			currentCrudOperations = new DeviceTypeCrud(stmt);
			break;
		case "device_info":
			currentCrudOperations = new DeviceInfoCrud(stmt);
			break;
		case "device":
			currentCrudOperations = new DeviceCrud(stmt);
			break;
		case "device_msg_log":
			currentCrudOperations = new DeviceMsgLogCrud(stmt);
			break;
		case "device_energy_log":
			currentCrudOperations = new DeviceEnergyConsumedLogCrud(stmt);
			break;
		default:
			currentCrudOperations = null;
			break;
		}

		if (currentCrudOperations != null) {
			JPanel newCrudForm = currentCrudOperations.getCrudPanel();
			if(newCrudForm != null) {
				Component[] formComponents = newCrudForm.getComponents();
				for (Component c : formComponents) {
					crudFormPanel.add(c, gbc);
				}
			}
		}

		crudFormPanel.revalidate();
		crudFormPanel.repaint();
	}

	public void actionPerformed(ActionEvent e) {
		Object target = e.getSource();
		String selectedTable = (String) tableSelectionComboBox.getSelectedItem();
		
		if (target == tableSelectionComboBox) {
			updateCrudPanel(selectedTable); 

		} else if (target == insertButton) {
			currentCrudOperations.insert();
			
		} else if (target == deleteButton) {
			currentCrudOperations.delete();
			
		} else if (target == updateButton) {
			currentCrudOperations.update();
			
		} else if (target == exportButton) {
			currentCrudOperations.export();
			
		} else if (target == clearButton) {
			currentCrudOperations.clear();
			
		} else if (target == deviceTypeCount) {
			try
			{ 			
				String cmd = " SELECT device_type_category, COUNT(*) AS count "
						+ "FROM device_type "
						+ "GROUP BY device_type_category;";
				
				rs = stmt.executeQuery(cmd);
				writeToFile(rs, "deviceType_Count.csv");
			}
			catch (SQLException sqle){
				System.err.println("Error with SELECT:\n"+sqle.toString());
			}
			
		} else if (target == deviceLocationStatus) {
			try
			{ 			
				String cmd = " SELECT device_name, device_location, is_active "
						+ "FROM device "
						+ "JOIN device_info ON device.device_info_id = device_info.device_info_id;";
				
				rs = stmt.executeQuery(cmd);
				writeToFile(rs, "deviceLocation_Status.csv");
			}
			catch (SQLException sqle){
				System.err.println("Error with SELECT:\n"+sqle.toString());
			}
			
		} else if (target == deviceRanges) {
			try
			{ 			
				String cmd = " SELECT di.device_name, di.device_range, di.device_communication, dt.device_type_category, dt.device_type_name "
						+ "FROM device_info di "
						+ "JOIN device_type dt ON di.device_type_id = dt.device_type_id;";
				
				rs = stmt.executeQuery(cmd);
				writeToFile(rs, "deviceRanges.csv");
			}
			catch (SQLException sqle){
				System.err.println("Error with SELECT:\n"+sqle.toString());
			}
			
		} else if (target == deviceSecurities) {
			try
			{ 			
				String cmd = "SELECT device_security, COUNT(*) AS device_count "
						+ "FROM device_info "
						+ "GROUP BY device_security;";
				
				rs = stmt.executeQuery(cmd);
				writeToFile(rs, "deviceSecurities.csv");
			}
			catch (SQLException sqle){
				System.err.println("Error with SELECT:\n"+sqle.toString());
			}
			
		} else if (target == devicePower) {
			try
			{ 			
				String cmd = " SELECT device_name, device_activePower, device_standbyPower "
						+ "FROM device_info;";
				
				rs = stmt.executeQuery(cmd);
				writeToFile(rs, "devicePower.csv");
			}
			catch (SQLException sqle){
				System.err.println("Error with SELECT:\n"+sqle.toString());
			}
			
		} else if (target == deviceUsagePattern) {
			try
			{ 			
				String cmd = "SELECT CONCAT(device_info.device_name, ' (', device.device_location, ')') AS device_name_with_location, "
						+ " device.active_from, device.active_to "
						+ " FROM device "
						+ " JOIN device_info ON device.device_info_id = device_info.device_info_id "
						+ "WHERE device.is_active = TRUE;";
				
				rs = stmt.executeQuery(cmd);
				writeToFile(rs, "deviceUsagePattern.csv");
			}
			catch (SQLException sqle){
				System.err.println("Error with SELECT:\n"+sqle.toString());
			}
			
		} else if (target == deviceAvgEnergyConsumptionByMonth) {
			try
			{ 			
				String cmd = "SELECT "
					    + "CONCAT(device_info.device_name, ' (', device.device_location, ')') AS device_name_with_location, "
					    + "YEAR(device_energy_log.device_log_date) AS year, "
					    + "MONTH(device_energy_log.device_log_date) AS month, "
					    + "AVG(device_energy_log.energy_consumed) AS average_energy_consumed "
					    + "FROM "
					    + "device_energy_log "
					    + "JOIN device ON device_energy_log.device_id = device.device_id "
					    + "JOIN device_info ON device.device_info_id = device_info.device_info_id "
					    + "GROUP BY "
					    + "device_info.device_name, "
					    + "device.device_location, "
					    + "YEAR(device_energy_log.device_log_date), "
					    + "MONTH(device_energy_log.device_log_date) "
					    + "ORDER BY "
					    + "device_info.device_name, "
					    + "device.device_location, "
					    + "year, "
					    + "month;";
				
				rs = stmt.executeQuery(cmd);
				writeToFile(rs, "deviceAvgEnergyByMonth.csv");
			}
			catch (SQLException sqle){
				System.err.println("Error with SELECT:\n"+sqle.toString());
			}
			
		} else if (target == deviceTopConsumptionByMonth) {
			try
			{ 	
				String cmd = 
						"SELECT YEAR(device_energy_log.device_log_date) AS year, "
						+ "	MONTH(device_energy_log.device_log_date) AS month, "
						+ "	CONCAT(device_info.device_name,'(', device.device_location, ')') AS device_with_location, "
						+ "	MAX(device_energy_log.energy_consumed) AS energy_consumed "
						+ "FROM "
						+ "	device_energy_log "
						+ "	JOIN device ON device_energy_log.device_id = device.device_id "
						+ "	JOIN device_info ON device.device_info_id = device_info.device_info_id "
						+ "GROUP BY "
						+ "	device_info.device_name, "
						+ " device.device_location, "
						+ "	YEAR(device_energy_log.device_log_date), "
						+ "	MONTH(device_energy_log.device_log_date);";
				
				rs = stmt.executeQuery(cmd);
				writeToFile(rs, "deviceTopDailyEnergyByMonth.csv");
			}
			catch (SQLException sqle){
				System.err.println("Error with SELECT:\n"+sqle.toString());
			}
		}
		
		TableModel.refreshFromDB(stmt, selectedTable);
	}
}