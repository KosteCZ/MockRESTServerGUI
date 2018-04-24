package cz.koscak.jan.ws.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class Main {

	private static final int TABLE_X = 10;
	private static final int TABLE_Y = 180;
	private static final int TABLE_WIDTH = 965;

	private static final String URL_WS_DB_LOCALHOST = "http://localhost:11011/MockRestServer/db";
	private static final String URL_WS_DB_PID = "http://tlv-a128.dixons.co.uk:11011/MockRestServer/db";
	private static final String URL_WS_DB_PIQ = "http://tlv-a129.dixons.co.uk:11011/MockRestServer/db";
	private static final String URL_WS_DB_CUSTOM = URL_WS_DB_LOCALHOST;
	
	private static final String[] URLS_NAMES = new String[] { "Custom =>", "localhost", "PID", "PIQ" };
	private static final String[] URLS_VALUES = new String[] { /*null*/ URL_WS_DB_LOCALHOST, URL_WS_DB_LOCALHOST, URL_WS_DB_PID, URL_WS_DB_PIQ };
	
	private static final String SQL_SELECT_START = "SELECT id, date, ws_name, ws_result, text FROM message WHERE ";
	private static final String SQL_SELECT_END = " LIMIT 30;";
	private static final String SQL_SELECT_COUNT_START = "SELECT count(id) FROM message WHERE ";
	private static final String SQL_SELECT_COUNT_END = ";";

	public static void main(String[] args) {

		JFrame frame = new JFrame("Mock REST Server - Monitoring Tool");
		
		createGUIElements(frame);

		//frame.setSize(1000, 727);
		frame.setSize(990, 717);
		frame.setLayout(null);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	private static void createGUIElements(final JFrame frame) {
		
		JLabel urlLabel = new JLabel("URL: ");

		urlLabel.setBounds(10, 10 + 3, 80, 15); // x axis, y axis, width, height

		frame.add(urlLabel);

		final JTextField urlField = new JTextField(20);

		urlField.setText(URL_WS_DB_CUSTOM);

		urlField.setBounds(300, 10, 675, 20); // x axis, y axis, width, height

		frame.add(urlField);
		
		
		
		String[] urlStrings = URLS_NAMES;

		final JComboBox<String> urlList = new JComboBox<String>(urlStrings);
		urlList.setSelectedIndex(0);
		
		urlList.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				int selectedURL = urlList.getSelectedIndex();
				if (selectedURL == 0) {
					urlField.setEditable(true);
				} else {
					urlField.setEditable(false);
				}
				urlField.setText(URLS_VALUES[selectedURL]);
				
			}
			
		});
		
		urlList.setBounds(70, 10, 220, 20); // x axis, y axis, width, height

		frame.add(urlList);
		
		
		
		JLabel resultsLabel = new JLabel("Results: ");

		resultsLabel.setBounds(10, 150 + 3, 80, 15); // x axis, y axis, width, height

		frame.add(resultsLabel);

		final JTextField resultsField = new JTextField(20);

		resultsField.setText("0");

		resultsField.setBounds(70, 150, 560 + 345, 20); // x axis, y axis, width, height

		resultsField.setEditable(false);
		
		frame.add(resultsField);

		
		
		JLabel sqlLabel = new JLabel("SQL: ");

		sqlLabel.setBounds(10, 40 + 3, 80, 15); // x axis, y axis, width, height

		frame.add(sqlLabel);

		JLabel sqlLabel2 = new JLabel("WHERE: ");

		sqlLabel2.setBounds(10, 70 + 3, 80, 15); // x axis, y axis, width, height

		frame.add(sqlLabel2);
		
		

		final JTextField sqlField = new JTextField(20);

		sqlField.setText(SQL_SELECT_START + "<WHERE>" + SQL_SELECT_END);

		sqlField.setBounds(70, 40, 560 + 345, 20); // x axis, y axis, width, height

		sqlField.setEditable(false);
		
		frame.add(sqlField);

		final JTextField sqlField2 = new JTextField(20);

		Date date = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd" /* HH-mm-ss"*/);
		
		final String today = sdf.format(date);
		
		sqlField2.setText("date >= datetime('" + today + " 00:00:00')"
				+ " AND date <= datetime('" + today + " 23:59:59') AND ws_name!='testdb' AND ws_result LIKE '2%'");

		sqlField2.setBounds(70, 70, 560 + 345, 20); // x axis, y axis, width, height

		frame.add(sqlField2);

		final JTable table = createTable(frame);

		table.setBounds(TABLE_X, TABLE_Y + 20, TABLE_WIDTH, 480); // x axis, y
																	// axis,
																	// width,
																	// height

		JButton button = new JButton("Execute"); // creating instance of JButton
		button.setBounds(70, 100, 100, 40); // x axis, y axis, width, height
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				
				CredentialsItem credentialsItem = new CredentialsItem(urlField.getText(), null, null);
				
				try {
					
					String sqlWherePart = sqlField2.getText();
					
					if ("".equalsIgnoreCase(sqlWherePart)) {
						sqlWherePart = "1=1";
					}

					String sqlCount = SQL_SELECT_COUNT_START + sqlWherePart + SQL_SELECT_COUNT_END;
					String responseCount = RESTClient.useHttpClientPOST(credentialsItem, sqlCount);
					//System.out.println("Response: " + responseCount);
					
					resultsField.setText(responseCount.replaceAll("\\]|\\[", ""));
					
					String sql = SQL_SELECT_START + sqlWherePart + SQL_SELECT_END;
					String response = RESTClient.useHttpClientPOST(credentialsItem, sql);
					//System.out.println("Response: " + response);

					updateTable(table, response);
					
				} catch (Exception e) {
					System.out.println("ERROR: " + e.getMessage());
					e.printStackTrace();
				}

			}

		});

		frame.add(button);

		frame.add(table);
		
		
		
		JButton button2 = new JButton("Help"); // creating instance of JButton
		button2.setBounds(875, 100, 100, 40); // x axis, y axis, width, height
		button2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				
				StringBuilder sbHelp = new StringBuilder();
				sbHelp.append("Today: " + "date >= datetime('" + today + " 00:00:00') AND date <= datetime('" + today + " 23:59:59')" + "\n");
				sbHelp.append("WS_Type: " + "ws_name == 'forward'" + "\n");
				sbHelp.append("WS_Type: " + "ws_name != 'testdb'" + "\n");
				sbHelp.append("WS_Result: " + "ws_result == '200'" + "\n");
				sbHelp.append("WS_Result: " + "ws_result != '400'" + "\n");
				sbHelp.append("WS_Result: " + "ws_result LIKE '2%'" + "\n");
				sbHelp.append("WS_Result: " + "ws_result NOT LIKE '4%'" + "\n");
				sbHelp.append("Ordering: " + "ORDER BY date" + "\n");
				sbHelp.append("Ordering: " + "ORDER BY date DESC" /*+ "\n"*/);
				
				JTextArea textarea= new JTextArea(sbHelp.toString());
				textarea.setEditable(true);
				
				JTable table2 = new JTable();
				
				DefaultTableModel tableModel2 = new DefaultTableModel(0, 0);

				String[] header2 = new String[] { "Action", "SQL" };
				tableModel2.setColumnIdentifiers(header2);
				
				String[] arrayHelp = sbHelp.toString().split("\\n");
				
				// add row dynamically into the tab
				for (String elementHelp : arrayHelp) {
					Object[] arrayHelp2 = elementHelp.toString().split("\\: ");
					tableModel2.addRow(arrayHelp2);
				}

				table2.setModel(tableModel2);
				
				TableColumnModel colModel = table2.getColumnModel();
				colModel.getColumn(0).setPreferredWidth(50);
				colModel.getColumn(0).setWidth(50);
				colModel.getColumn(1).setPreferredWidth(700);
				colModel.getColumn(1).setWidth(700);
				
				table2.setMinimumSize(new Dimension(900, 50));
				
				JDialog dialog = new JDialog(frame, "SQL HELP", true);
				dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				dialog.setMinimumSize(new Dimension(900, 50));
				//dialog.add(dialogPanel);
				dialog.add(table2);
				dialog.validate();
				dialog.pack();
				dialog.setLocationRelativeTo(null);
			    //dialog.setPreferredSize(new Dimension(400, 400));
				dialog.setVisible(true);
				
				//JOptionPane.showConfirmDialog(null, table2 /*textarea*/, "SQL Help", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
				
			}
			
		});
		
		frame.add(button2);

	}

	private static JTable createTable(JFrame frame) {

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

	private static void updateTable(JTable table, String string) {

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
		colModel.getColumn(1).setPreferredWidth(25);
		colModel.getColumn(1).setWidth(25);
		colModel.getColumn(2).setPreferredWidth(135);
		colModel.getColumn(2).setWidth(135);
		colModel.getColumn(3).setPreferredWidth(75);
		colModel.getColumn(3).setWidth(75);
		colModel.getColumn(4).setPreferredWidth(75);
		colModel.getColumn(4).setWidth(75);
		colModel.getColumn(5).setPreferredWidth(720);
		colModel.getColumn(5).setWidth(720);

	}

}