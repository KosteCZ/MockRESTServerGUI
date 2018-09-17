package cz.koscak.jan.mockservergui.gui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class Table {

	public static final int TABLE_X = 10;
	public static final int TABLE_Y = 180;
	public static final int TABLE_WIDTH = 965;

	public static JTable createTable(JFrame frame) {

		JTable table = new JTable();

		table.setShowVerticalLines(true);
		table.setCellSelectionEnabled(true);
		table.setColumnSelectionAllowed(true);
		table.setBorder(new LineBorder(null));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setIntercellSpacing(new Dimension(2, 0));
		
		updateTable(table, "");
		
		JTableHeader headers = table.getTableHeader();

		headers.setBounds(TABLE_X, TABLE_Y, TABLE_WIDTH, 20);
		frame.add(headers);

		return table;

	}

	public static void updateTable(JTable table, String string) {

		List<Object[]> listOfArrays = new ArrayList<Object[]>();
		
		try {
			
			if (string != null && !"".equals(string) && !"[]".equals(string) && !"[[]]".equals(string)) {
			
				String trimmedString = string.substring(2, string.length() - 2);
		
				//System.out.println("1: " + trimmedString);
		
				String[] arrayOfStrings = trimmedString.split("\\]\\, \\[");
		
				//System.out.println("2: Array length: " + arrayOfStrings.length);
		
				for (int i = 0; i < arrayOfStrings.length; i++) {
					
					//System.out.println("2: " + arrayOfStrings[i]);
					String[] originalArray = arrayOfStrings[i].split("\\, ", 5);
					String[] updatedArray = new String[originalArray.length + 1];
					updatedArray[0] = "" + (i+1) + ".";
					
					for (int j = 0; j < originalArray.length; j++) {
						
						if ((j == (originalArray.length-1)) && (originalArray[j] == null || "null".equalsIgnoreCase(originalArray[j]))) {
							originalArray[j] = "";
						}
						
						updatedArray[j+1] = originalArray[j];
						
					}
					
					listOfArrays.add((Object[]) updatedArray);
					
				}
				
			}
		
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
		DefaultTableModel tableModel = new DefaultTableModel(0, 0);

		String[] header = new String[] { "#", "ID", "Date", "WS_Name", "WS_Result", "Text" };
		tableModel.setColumnIdentifiers(header);
		table.setModel(tableModel);
		
		// add row dynamically into the table
		for (Object[] array : listOfArrays) {
			tableModel.addRow(array);
		}

		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(30);
		colModel.getColumn(0).setWidth(30);
		colModel.getColumn(1).setPreferredWidth(40);
		colModel.getColumn(1).setWidth(40);
		colModel.getColumn(2).setPreferredWidth(135);
		colModel.getColumn(2).setWidth(135);
		colModel.getColumn(3).setPreferredWidth(75);
		colModel.getColumn(3).setWidth(75);
		colModel.getColumn(4).setPreferredWidth(75);
		colModel.getColumn(4).setWidth(75);
		colModel.getColumn(5).setPreferredWidth(705);
		colModel.getColumn(5).setWidth(705);

	}

}
