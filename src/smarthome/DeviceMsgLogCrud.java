package smarthome;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.JXDatePicker;

public class DeviceMsgLogCrud implements TableCrudOperations, ExportCSV{
	private Statement stmt = null;
	private ResultSet rs = null;
	private static QueryTableModel TableModel = new QueryTableModel();
	private static final String TABLE_NAME = "device_msg_log";
	private static final String TABLE_FILE_NAME = "deviceMsgLog.csv";
	
	private JPanel panel = new JPanel();

    private JLabel deviceMsgIDLabel = new JLabel("Device Message ID:");
    private JLabel deviceIDLabel = new JLabel("Device ID:");
    private JLabel deviceMsgTypeLabel = new JLabel("Message Type:");
    private JLabel deviceMsgLabel = new JLabel("Message:");
    private JLabel deviceIsReadLabel = new JLabel("Message Read:");
    private JLabel createdOnLabel = new JLabel("Message Date:");
    
    private JTextField deviceMsgTF = new JTextField(10);
    private String[] msgTypes = { "Warning", "Alert", "Maintenance", "Event", "Command", "Schedule", "Status Update"};
	private JComboBox<String> deviceMsgTypeCB = new JComboBox<>(msgTypes);
	
	
    private JXDatePicker createdOnDatePicker = new JXDatePicker();
    
    NumberFormat intFormat = NumberFormat.getIntegerInstance();
    NumberFormatter integerFormatter = new NumberFormatter(intFormat);
    NumberFormat intFormat2 = NumberFormat.getIntegerInstance();
    NumberFormatter integer2Formatter = new NumberFormatter(intFormat2);
    JFormattedTextField deviceMsgIDTF, deviceIDTF, deviceIsReadTF;

    public DeviceMsgLogCrud(Statement stmt) {
    	this.stmt = stmt;
    	
    	// IDs
    	integerFormatter.setValueClass(Integer.class);
    	integerFormatter.setAllowsInvalid(false);
    	integerFormatter.setMinimum(1); 
        deviceMsgIDTF = new JFormattedTextField(integerFormatter);
        deviceIDTF = new JFormattedTextField(integerFormatter);
        // Boolean
        integer2Formatter.setValueClass(Integer.class);
    	integer2Formatter.setAllowsInvalid(false);
        integer2Formatter.setMinimum(0); 
        integer2Formatter.setMaximum(1); 
        deviceIsReadTF = new JFormattedTextField(integer2Formatter);
        // Date
        createdOnDatePicker.setDate(new Date()); 
        createdOnDatePicker.setFormats(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        
        // Add to Panel
        panel.add(deviceMsgIDLabel);	 	panel.add(deviceMsgIDTF);
        panel.add(deviceMsgTypeLabel);		panel.add(deviceMsgTypeCB);
        panel.add(deviceIDLabel); 			panel.add(deviceIDTF);
        panel.add(deviceMsgLabel);			panel.add(deviceMsgTF);        
        panel.add(createdOnLabel);			panel.add(createdOnDatePicker);
        panel.add(deviceIsReadLabel);		panel.add(deviceIsReadTF);
        
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
	        String createdOnDate = format.format(createdOnDatePicker.getDate());
	        
			String insertStmt ="INSERT INTO "+TABLE_NAME+ " VALUES ('"+
					deviceMsgIDTF.getText()						+"','"+
					(String) deviceMsgTypeCB.getSelectedItem()	+"','"+
					deviceIDTF.getText()						+"','"+
					deviceMsgTF.getText()						+"','"+
					createdOnDate								+"','"+
					deviceIsReadTF.getText()					+"');";
					
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
			String deleteStmt ="DELETE FROM "+ TABLE_NAME + " WHERE device_msg_id = "+deviceMsgIDTF.getText()+";"; 
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
	        String createdOnDate = format.format(createdOnDatePicker.getDate());
	        
			String updateTemp ="UPDATE "+ TABLE_NAME +
					" SET  device_msg_type = '"	+		(String) deviceMsgTypeCB.getSelectedItem()+
					"', device_id = '"			+		deviceIDTF.getText()+
					"', device_msg = '" 		+		deviceMsgTF.getText()+
					"', createdOn = '"			+		createdOnDate+
					"', is_read = '"			+		deviceIsReadTF.getText()+
					"' WHERE device_msg_id = "	+		deviceMsgIDTF.getText()+";";
			
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
		deviceMsgIDTF.setText("1");
    	deviceIDTF.setText("1");
		deviceMsgTF.setText("");
		deviceIsReadTF.setText("0");
	}
}
