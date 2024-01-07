package smarthome;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.JXDatePicker;

public class DeviceEnergyConsumedLogCrud implements TableCrudOperations, ExportCSV{
	private Statement stmt = null;
	private ResultSet rs = null;
	private static QueryTableModel TableModel = new QueryTableModel();
	private static final String TABLE_NAME = "device_energy_log";
	private static final String TABLE_FILE_NAME = "deviceEnergyLog.csv";
	
	private JPanel panel = new JPanel();

    private JLabel deviceLogIDLabel = new JLabel("Energy Log ID:");
    private JLabel energyLogDateLabel = new JLabel("Log Date:");
    private JLabel deviceIDLabel = new JLabel("Device ID:");
    private JLabel hoursActiveLabel = new JLabel("Hours Active:");
    private JLabel hoursInactiveLabel = new JLabel("Hours Inactive:");
    private JLabel energyConsumedLabel = new JLabel("Energy Consumed(KWh):");
	
    
    private JXDatePicker energyLogDatePicker = new JXDatePicker();
    
    NumberFormat intFormat = NumberFormat.getIntegerInstance();
    NumberFormatter integerFormatter = new NumberFormatter(intFormat);  
    NumberFormat floatFormat = NumberFormat.getNumberInstance();
    NumberFormatter floatFormatter = new NumberFormatter(floatFormat);
    
    JFormattedTextField deviceLogIDTF, deviceIDTF, hoursActiveTF, hoursInactiveTF, energyConsumedTF;

    public DeviceEnergyConsumedLogCrud(Statement stmt) {
    	this.stmt = stmt;
    	
    	// IDs
    	integerFormatter.setValueClass(Integer.class);
    	integerFormatter.setAllowsInvalid(false);
    	integerFormatter.setMinimum(1); 
        deviceLogIDTF = new JFormattedTextField(integerFormatter);
        deviceIDTF = new JFormattedTextField(integerFormatter);
        // Hours & Energy
        floatFormatter.setValueClass(Float.class);
        floatFormatter.setAllowsInvalid(false); 
        floatFormatter.setMinimum(0.0f);
        hoursActiveTF = new JFormattedTextField(floatFormatter);
        hoursActiveTF.setColumns(10); hoursActiveTF.setFormatterFactory(new DefaultFormatterFactory(floatFormatter)); 
        hoursInactiveTF = new JFormattedTextField(floatFormatter);
        hoursInactiveTF.setColumns(10); hoursInactiveTF.setFormatterFactory(new DefaultFormatterFactory(floatFormatter)); 
        energyConsumedTF = new JFormattedTextField(floatFormatter);
        energyConsumedTF.setColumns(10); energyConsumedTF.setFormatterFactory(new DefaultFormatterFactory(floatFormatter)); 
        // Date
        energyLogDatePicker.setDate(new Date()); 
        energyLogDatePicker.setFormats(new SimpleDateFormat("yyyy-MM-dd"));
        
        // Add to Panel
        panel.add(deviceLogIDLabel);	 	panel.add(deviceLogIDTF);
        panel.add(energyLogDateLabel);		panel.add(energyLogDatePicker);
        panel.add(deviceIDLabel); 			panel.add(deviceIDTF);
        panel.add(hoursActiveLabel);		panel.add(hoursActiveTF);
        panel.add(hoursInactiveLabel);		panel.add(hoursInactiveTF);
        panel.add(energyConsumedLabel);		panel.add(energyConsumedTF);
        
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
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	        String energyLogDate = format.format(energyLogDatePicker.getDate());
	        
			String insertStmt ="INSERT INTO "+TABLE_NAME+ " VALUES ('"+
					deviceLogIDTF.getText()						+"','"+
					energyLogDate								+"','"+
					deviceIDTF.getText()						+"','"+
					hoursActiveTF.getText()						+"','"+
					hoursInactiveTF.getText()					+"','"+
					energyConsumedTF.getText()					+"');";
					
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
			String deleteStmt ="DELETE FROM "+ TABLE_NAME + " WHERE device_log_id = "+deviceLogIDTF.getText()+";"; 
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
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	        String energyLogDate = format.format(energyLogDatePicker.getDate());
	        
			String updateTemp ="UPDATE "+ TABLE_NAME +
					" SET  device_log_date = '"	+		energyLogDate+
					"', device_id = '"			+		deviceIDTF.getText()+
					"', hours_active = '" 		+		hoursActiveTF.getText()+
					"', hours_inactive = '"		+		hoursInactiveTF.getText()+
					"', energy_consumed = '"	+		energyConsumedTF.getText()+
					"' WHERE device_log_id = "	+		deviceLogIDTF.getText()+";";
			
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
		deviceLogIDTF.setText("1");
    	deviceIDTF.setText("1");
    	hoursActiveTF.setText("0.0");
    	hoursInactiveTF.setText("0.0");
    	energyConsumedTF.setText("0.0");
	}
}
