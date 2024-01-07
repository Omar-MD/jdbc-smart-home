package smarthome;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jdesktop.swingx.JXDatePicker;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

public class DeviceInfoCrud implements TableCrudOperations, ExportCSV{
	
	private Statement stmt = null;
	private ResultSet rs = null;
	private static QueryTableModel TableModel = new QueryTableModel();
	private static final String TABLE_NAME = "device_info";
	private static final String TABLE_FILE_NAME = "deviceInfo.csv";
	
	private JPanel panel = new JPanel();

    private JLabel deviceInfoIDLabel = new JLabel("Device Info ID:");
    private JLabel deviceTypeIDLabel = new JLabel("Device Type ID:");
    private JLabel deviceNameLabel = new JLabel("Name:");
    private JLabel deviceDescriptionLabel = new JLabel("Description:");
  
    private JLabel deviceRangeLabel = new JLabel("Range:");
    private JLabel deviceCommunicationLabel = new JLabel("Communication:");
    private JLabel deviceDataRateLabel = new JLabel("Data Rate:");
    private JLabel deviceSecurityLabel = new JLabel("Security:");
    private JLabel deviceSVLabel = new JLabel("Software Version:");
    private JLabel deviceSCLabel = new JLabel("Storage Capacity:");
    private JLabel deviceEnergyRatingLabel = new JLabel("Device Energy Rating:");
    private JLabel devicePowerActiveLabel = new JLabel("Power Active (KWatt):");
    private JLabel devicePowerStandbyLabel = new JLabel("Power Standby (KWatt):");
    private JLabel deviceRegisteredOnLabel = new JLabel("Registered On date:");
    
    private JTextField deviceNameTF = new JTextField(10);
    private JTextField deviceDescriptionTF = new JTextField(10);
    private JTextField deviceCommunicationTF = new JTextField(10);
    private JTextField deviceDataRateTF = new JTextField(10);
    private JTextField deviceSecurityTF = new JTextField(10);
    private JTextField deviceSVTF = new JTextField(10);
    private JTextField deviceSCTF = new JTextField(10);
    private JTextField deviceEnergyRatingTF = new JTextField(10);

    private JXDatePicker datePicker = new JXDatePicker();
    NumberFormat intFormat = NumberFormat.getIntegerInstance();
    NumberFormatter integerFormatter = new NumberFormatter(intFormat);
    
    NumberFormat floatFormat = NumberFormat.getNumberInstance();
    NumberFormatter floatFormatter = new NumberFormatter(floatFormat);
    JFormattedTextField deviceInfoIDTF, deviceTypeIDTF, deviceRangeTF, devicePowerActiveTF, devicePowerStandbyTF;

    public DeviceInfoCrud(Statement stmt) {
    	this.stmt = stmt;
    	
    	// IDs & Range
    	integerFormatter.setValueClass(Integer.class);
    	integerFormatter.setAllowsInvalid(false);
    	integerFormatter.setMinimum(1); 
        deviceInfoIDTF = new JFormattedTextField(integerFormatter);
        deviceTypeIDTF = new JFormattedTextField(integerFormatter);
        deviceRangeTF = new JFormattedTextField(integerFormatter);
        // Power
        floatFormatter.setValueClass(Float.class);
        floatFormatter.setAllowsInvalid(false); 
        floatFormatter.setMinimum(0.0f);
        devicePowerActiveTF = new JFormattedTextField(floatFormatter);
        devicePowerActiveTF.setColumns(10); devicePowerActiveTF.setFormatterFactory(new DefaultFormatterFactory(floatFormatter));
        devicePowerStandbyTF = new JFormattedTextField(floatFormatter);
        devicePowerStandbyTF.setColumns(10); devicePowerStandbyTF.setFormatterFactory(new DefaultFormatterFactory(floatFormatter));
        // Date
        datePicker.setDate(new Date()); 
        datePicker.setFormats(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        
        // Add to Panel
        panel.add(deviceInfoIDLabel);	 		panel.add(deviceInfoIDTF);
        panel.add(deviceTypeIDLabel); 			panel.add(deviceTypeIDTF);
        panel.add(deviceNameLabel);				panel.add(deviceNameTF);
        panel.add(deviceDescriptionLabel);		panel.add(deviceDescriptionTF);
        panel.add(deviceCommunicationLabel);	panel.add(deviceCommunicationTF);
        panel.add(deviceRangeLabel);			panel.add(deviceRangeTF);
        panel.add(deviceDataRateLabel);			panel.add(deviceDataRateTF);
        panel.add(deviceSecurityLabel);			panel.add(deviceSecurityTF);
        panel.add(deviceSVLabel);				panel.add(deviceSVTF);
        panel.add(deviceSCLabel);				panel.add(deviceSCTF);
        panel.add(deviceEnergyRatingLabel);		panel.add(deviceEnergyRatingTF);
        panel.add(devicePowerActiveLabel);		panel.add(devicePowerActiveTF);
        panel.add(devicePowerStandbyLabel);		panel.add(devicePowerStandbyTF);
        panel.add(deviceRegisteredOnLabel);		panel.add(datePicker);
        
        clear();
    }

    @Override
    public JPanel getCrudPanel() {
        return panel;
    }
    
	@Override
	public void insert() {
		
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String registeredDate = format.format(datePicker.getDate());
	        
			String insertStmt ="INSERT INTO "+TABLE_NAME+ " VALUES ('"+
					deviceInfoIDTF.getText()+"','"+
					deviceTypeIDTF.getText()+"','"+
					deviceNameTF.getText()+"','"+
					deviceDescriptionTF.getText()+"','"+
					
					deviceRangeTF.getText()+"','"+
					deviceCommunicationTF.getText()+"','"+
					deviceDataRateTF.getText()+"','"+
					
					deviceSecurityTF.getText()+"','"+
					deviceSVTF.getText()+"','"+
					deviceSCTF.getText()+"','"+
					deviceEnergyRatingTF.getText()+"','"+
					
					devicePowerActiveTF.getText()+"','"+
					devicePowerStandbyTF.getText()+"','"+
					registeredDate +"');";
					
			
			stmt.executeUpdate(insertStmt);
		
		}
		catch (SQLException sqle)
		{
			System.err.println("Error with INSERT:\n"+sqle.toString());
		}
		finally
		{
			TableModel.refreshFromDB(stmt, TABLE_NAME);
		}
		
	}
	
	@Override
	public void delete() {
		try
		{
			String deleteStmt ="DELETE FROM "+ TABLE_NAME + " WHERE device_info_id = "+deviceInfoIDTF.getText()+";"; 
			stmt.executeUpdate(deleteStmt);
		
		}
		catch (SQLException sqle)
		{
			System.err.println("Error with DELETE:\n"+sqle.toString());
		}
		finally
		{
			TableModel.refreshFromDB(stmt, TABLE_NAME);
		}
		
	}
	
	@Override
	public void update() {
		try
		{ 	
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String registeredDate = format.format(datePicker.getDate());
	        
			String updateTemp ="UPDATE "+ TABLE_NAME +
					" SET device_type_id = '"	+	deviceTypeIDTF.getText()+
					"', device_name = '"		+	deviceNameTF.getText()+
					"', device_description = '"	+	deviceDescriptionTF.getText()+
					"', device_range = '"		+	deviceRangeTF.getText()+
					"', device_communication = '"+	deviceCommunicationTF.getText()+
					"', device_dataRate = '"	+	deviceDataRateTF.getText()+
					"', device_security = '"	+	deviceSecurityTF.getText()+
					"', device_softwareVersion = '"+deviceSVTF.getText()+
					"', device_storageCapacity = '"+deviceSCTF.getText()+
					"', device_energyRating = '"+	deviceEnergyRatingTF.getText()+
					"', device_activePower = '"	+	devicePowerActiveTF.getText()+
					"', device_standbyPower = '"+	devicePowerStandbyTF.getText()+
					"', device_registeredOn = '"+ 	registeredDate +
					"' WHERE device_info_id = "+deviceInfoIDTF.getText()+";";
			
			stmt.executeUpdate(updateTemp);
		}
		catch (SQLException sqle){
			System.err.println("Error with UPDATE:\n"+sqle.toString());
		}
		finally{
			TableModel.refreshFromDB(stmt, TABLE_NAME);
		}
		
	}


	@Override
	public void export() {
		try
		{ 			
			rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);
			writeToFile(rs, TABLE_FILE_NAME);
		}
		catch (SQLException sqle){
			System.err.println("Error with SELECT:\n"+sqle.toString());
		}
		finally{
			TableModel.refreshFromDB(stmt, TABLE_NAME);
		}
	}

	@Override
	public void clear() {
		deviceNameTF.setText("");
		deviceDescriptionTF.setText("");
		deviceCommunicationTF.setText("");
		deviceDataRateTF.setText("");
		deviceSecurityTF.setText("");
		deviceSVTF.setText("");
		deviceSCTF.setText("");
		deviceEnergyRatingTF.setText("");
		
    	deviceInfoIDTF.setText("1");
    	deviceTypeIDTF.setText("1");
    	deviceRangeTF.setText("1");
    	devicePowerActiveTF.setText("0.0");
    	devicePowerStandbyTF.setText("0.0");
	}
}
