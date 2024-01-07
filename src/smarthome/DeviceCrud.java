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
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.JXDatePicker;

public class DeviceCrud implements TableCrudOperations, ExportCSV{

	private Statement stmt = null;
	private ResultSet rs = null;
	private static QueryTableModel TableModel = new QueryTableModel();
	private static final String TABLE_NAME = "device";
	private static final String TABLE_FILE_NAME = "device.csv";
	
	private JPanel panel = new JPanel();

    private JLabel deviceIDLabel = new JLabel("Device ID:");
    private JLabel deviceInfoIDLabel = new JLabel("Device Info ID:");
    private JLabel deviceLocationLabel = new JLabel("Location:");
    
    private JLabel deviceParametersLabel = new JLabel("Device Parameters:");
    private JLabel deviceIsActiveLabel = new JLabel("Device Active:");
    private JLabel deviceActiveFromLabel = new JLabel("Device Active From:");
    private JLabel deviceActiveToLabel = new JLabel("Device Active To:");
    
    private JTextField deviceLocationTF = new JTextField(10);
    private JTextField deviceParametersTF = new JTextField(10);

    private JXDatePicker activeFromDatePicker = new JXDatePicker();
    private JXDatePicker activeToDatePicker = new JXDatePicker();
    
    NumberFormat intFormat = NumberFormat.getIntegerInstance();
    NumberFormatter integerFormatter = new NumberFormatter(intFormat);
    NumberFormat intFormat2 = NumberFormat.getIntegerInstance();
    NumberFormatter integer2Formatter = new NumberFormatter(intFormat2);
    JFormattedTextField deviceIDTF, deviceInfoIDTF, deviceIsActiveTF;

    public DeviceCrud(Statement stmt) {
    	this.stmt = stmt;
    	
    	// IDs
    	integerFormatter.setValueClass(Integer.class);
    	integerFormatter.setAllowsInvalid(false);
    	integerFormatter.setMinimum(1); 
        deviceIDTF = new JFormattedTextField(integerFormatter);
        deviceInfoIDTF = new JFormattedTextField(integerFormatter);
        // Boolean
        integer2Formatter.setValueClass(Integer.class);
    	integer2Formatter.setAllowsInvalid(false);
        integer2Formatter.setMinimum(0); 
        integer2Formatter.setMaximum(1); 
        deviceIsActiveTF = new JFormattedTextField(integer2Formatter);
        // Date
        activeFromDatePicker.setDate(new Date()); 
        activeFromDatePicker.setFormats(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        activeToDatePicker.setDate(new Date()); 
        activeToDatePicker.setFormats(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        
        // Add to Panel
        panel.add(deviceIDLabel);	 		panel.add(deviceIDTF);
        panel.add(deviceInfoIDLabel); 		panel.add(deviceInfoIDTF);
        panel.add(deviceLocationLabel);		panel.add(deviceLocationTF);
        panel.add(deviceParametersLabel);	panel.add(deviceParametersTF);
        panel.add(deviceIsActiveLabel);		panel.add(deviceIsActiveTF);
        panel.add(deviceActiveFromLabel);	panel.add(activeFromDatePicker);
        panel.add(deviceActiveToLabel);		panel.add(activeToDatePicker);
        
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
	        String activeFromDate = format.format(activeFromDatePicker.getDate());
	        String activeToDate = format.format(activeToDatePicker.getDate());
	        
			String insertStmt ="INSERT INTO "+TABLE_NAME+ " VALUES ('"+
					deviceIDTF.getText()			+"','"+
					deviceInfoIDTF.getText()		+"','"+
					deviceLocationTF.getText()		+"','"+
					deviceParametersTF.getText()	+"','"+
					deviceIsActiveTF.getText()		+"','"+
					activeFromDate					+"','"+
					activeToDate 					+"');";
					
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
			String deleteStmt ="DELETE FROM "+ TABLE_NAME + " WHERE device_id = "+deviceIDTF.getText()+";"; 
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
	        String activeFromDate = format.format(activeFromDatePicker.getDate());
	        String activeToDate = format.format(activeToDatePicker.getDate());
	        
	        
			String updateTemp ="UPDATE "+ TABLE_NAME +
					" SET device_info_id = '"	+	deviceInfoIDTF.getText()+
					"', device_location = '"+	deviceLocationTF.getText()+
					"', device_parameters = '"+	deviceParametersTF.getText()+
					"', is_active = '"		+	deviceIsActiveTF.getText()+
					"', active_from = '"	+	activeFromDate+
					"', active_to = '"		+ 	activeToDate +
					"' WHERE device_id = "	+	deviceIDTF.getText()+";";
			
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
		deviceLocationTF.setText("");
		deviceParametersTF.setText("");
		deviceIsActiveTF.setText("0");
    	deviceIDTF.setText("1");
    	deviceInfoIDTF.setText("1");
	}
}
