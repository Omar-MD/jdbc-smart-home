package smarthome;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;


public class DeviceTypeCrud implements TableCrudOperations, ExportCSV {
	
	private Statement stmt = null;
	ResultSet rs = null;
	private static QueryTableModel TableModel = new QueryTableModel();
	private static final String TABLE_NAME = "device_type";
	private static final String TABLE_FILE_NAME = "deviceType.csv";
	
	private JPanel panel = new JPanel();

    private JLabel deviceTypeIDLabel = new JLabel("ID:");
    private JLabel deviceTypeCategoryLabel = new JLabel("Category:");
    private JLabel deviceTypeNameLabel = new JLabel("Name:");

    private JTextField deviceTypeCategoryTF = new JTextField(10);
    private JTextField deviceTypeNameTF = new JTextField(10);

    NumberFormat intFormat = NumberFormat.getIntegerInstance();
    NumberFormatter numberFormatter = new NumberFormatter(intFormat);
    JFormattedTextField deviceTypeIDTF;

    public DeviceTypeCrud(Statement stmt) {
    	this.stmt = stmt;
    	
    	// IDs
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setMinimum(1); 
        deviceTypeIDTF = new JFormattedTextField(numberFormatter);
        // Add to panel
        panel.add(deviceTypeIDLabel);		panel.add(deviceTypeIDTF);
        panel.add(deviceTypeCategoryLabel);	panel.add(deviceTypeCategoryTF);
        panel.add(deviceTypeNameLabel);		panel.add(deviceTypeNameTF);
        
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
			String insertStmt ="INSERT INTO "+TABLE_NAME+ " VALUES ('"+deviceTypeIDTF.getText()+"','"+
					deviceTypeCategoryTF.getText()+"','"+deviceTypeNameTF.getText()+"');";
			
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
			String deleteStmt ="DELETE FROM "+ TABLE_NAME + " WHERE device_type_id = "+deviceTypeIDTF.getText()+";"; 
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
			String updateTemp ="UPDATE "+ TABLE_NAME +" SET device_type_category = '"+deviceTypeCategoryTF.getText()+
								"', device_type_name = '"+deviceTypeNameTF.getText()+
								"' WHERE device_type_id = "+deviceTypeIDTF.getText()+";";
			
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
		deviceTypeIDTF.setText("1");
		deviceTypeCategoryTF.setText("");
		deviceTypeNameTF.setText("");
	}
}
