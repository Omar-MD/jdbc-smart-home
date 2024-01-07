package smarthome;

//import javax.swing.*;
import javax.swing.table.*;
//import java.awt.*;
import java.sql.*;
//import java.io.*;
import java.util.*;

@SuppressWarnings("serial")
class QueryTableModel extends AbstractTableModel
{
	Vector<String[]> modelData; //will hold String[] objects
	int colCount;
	String[] headers=new String[0] ;
	Connection con;
	Statement stmt = null;
	String[] record;
	ResultSet rs = null;

	public QueryTableModel(){
		modelData = new Vector<String[]>();
	}//end constructor QueryTableModel

	public String getColumnName(int i){
		return headers[i];
	}	
	public int getColumnCount(){
		return colCount;
	}
	
	public int getRowCount(){
		return modelData.size();
	}
	
	public Object getValueAt(int row, int col){
		return ((String[])modelData.elementAt(row))[col];
	}

	public void refreshFromDB( Statement stmt1, String tableName)
	{
		modelData = new Vector<String[]>();
		stmt = stmt1;
		try{
			//Execute the query and store the result set and its metadata
			rs = stmt.executeQuery("SELECT * FROM " + tableName);
			ResultSetMetaData meta = rs.getMetaData();
		
			colCount = meta.getColumnCount(); 
			headers = new String[colCount];
	
			for(int h = 0; h<colCount; h++)
			{
				headers[h] = meta.getColumnName(h+1);
			}
			
			while(rs.next())
			{
				record = new String[colCount];
				for(int i = 0; i < colCount; i++)
				{
					record[i] = rs.getString(i+1);
				}//end for loop
				modelData.addElement(record);
			}
			fireTableChanged(null); //Tell the listeners a new table has arrived.
		}
		catch(Exception e) {
					System.out.println("Error with refreshFromDB Method\n"+e.toString());
		}
	}
}