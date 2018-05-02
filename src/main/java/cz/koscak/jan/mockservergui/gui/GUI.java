package cz.koscak.jan.mockservergui.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import cz.koscak.jan.mockservergui.ws.rest.CredentialsItem;
import cz.koscak.jan.mockservergui.ws.rest.RESTClient;

public class GUI {
	
	private static final String URL_WS_DB_LOCALHOST = "http://localhost:11011/MockRestServer/db";
	private static final String URL_WS_DB_PID = "http://tlv-a128.dixons.co.uk:11011/MockRestServer/db";
	private static final String URL_WS_DB_PIQ = "http://tlv-a129.dixons.co.uk:11011/MockRestServer/db";
	private static final String URL_WS_DB_CUSTOM = URL_WS_DB_LOCALHOST;
	
	private static final String[] URLS_NAMES = new String[] { "Custom =>", "localhost", "PID", "PIQ" };
	private static final String[] URLS_VALUES = new String[] { /*null*/ URL_WS_DB_LOCALHOST, URL_WS_DB_LOCALHOST, URL_WS_DB_PID, URL_WS_DB_PIQ };
	
	private static final String SELECT_ALL = "SELECT id, date, ws_name, ws_result, text";
	private static final String SELECT_WS_TYPE_COUNT = "SELECT '', '', ws_name, '', count(id)";
	private static final String SELECT_WS_TYPE_WS_RESULT_COUNT = "SELECT '', '', ws_name, ws_result, count(id)";
	private static final String SELECT_CUSTOM = SELECT_ALL;

	private static final String[] SELECT_NAMES = new String[] { "Custom =>", "ALL", "WS_NAME count", "WS_NAME + WS_RESULT count" };
	private static final String[] SELECT_VALUES = new String[] { SELECT_CUSTOM, SELECT_ALL, SELECT_WS_TYPE_COUNT, SELECT_WS_TYPE_WS_RESULT_COUNT };

	//private static final String SQL_SELECT_START = "SELECT id, date, ws_name, ws_result, text FROM message WHERE ";
	private static final String SQL_SELECT_END = " LIMIT 30;";
	private static final String SQL_SELECT_COUNT_START = "SELECT count(id) FROM message WHERE ";
	private static final String SQL_SELECT_COUNT_END = ";";
	private static final String SQL_SELECT_FROM = " FROM message WHERE ";
	private static final String SQL_SELECT_WHERE = " FROM message WHERE <WHERE>" + SQL_SELECT_END;
	private static final String SQL_SELECT_GROUP_BY_WS_NAME = " GROUP BY ws_name";
	private static final String SQL_SELECT_GROUP_BY_WS_NAME_WS_RESULT = " GROUP BY ws_name, ws_result";

	public static void createGUIElements(final JFrame frame) {
		
		final JTextField urlField = createURLElements(frame);
		
		final JTextField resultsField = createResultElements(frame);

		final JTextField sqlWhereField = createSQLWhereField(frame);

		final JTextField selectField = createSelectElements(frame, sqlWhereField);	
		
		createTable(frame, urlField, resultsField, selectField, sqlWhereField);
		
		createHelpButton(frame);

	}

	private static String getTodayDate() {
		
		Date date = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd" /* HH-mm-ss"*/);
		
		return sdf.format(date);
		
	}

	private static String getTodayDateSQLString() {
		
		final String today = getTodayDate();
		
		return "date >= datetime('" + today + " 00:00:00')"
		+ " AND date <= datetime('" + today + " 23:59:59')";
		
	}

	private static JTextField createSQLWhereField(final JFrame frame) {
		final JTextField sqlWhereField = new JTextField(20);

		sqlWhereField.setText(getTodayDateSQLString()); // AND ws_name!='testdb' AND ws_result LIKE '2%'");

		sqlWhereField.setBounds(70, 70, 560 + 345, 20); // x axis, y axis, width, height

		frame.add(sqlWhereField);
		return sqlWhereField;
	}

	private static void createTable(final JFrame frame, final JTextField urlField, final JTextField resultsField, final JTextField selectField,
			final JTextField sqlWhereField) {
		
		final JTable table = Table.createTable(frame);

		table.setBounds(Table.TABLE_X, Table.TABLE_Y + 20, Table.TABLE_WIDTH, 480); // x axis, y
																	// axis,
																	// width,
																	// height

		JButton button = new JButton("Execute"); // creating instance of JButton
		button.setBounds(70, 100, 100, 40); // x axis, y axis, width, height
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				
				CredentialsItem credentialsItem = new CredentialsItem(urlField.getText(), null, null);
				
				try {
					
					String sqlWherePart = sqlWhereField.getText();
					
					if ("".equalsIgnoreCase(sqlWherePart)) {
						sqlWherePart = "1=1";
					}
					
					String sqlWherePartForCount = sqlWherePart.split("((?i) GROUP BY )")[0];
					
					String sqlCount = SQL_SELECT_COUNT_START + sqlWherePartForCount + SQL_SELECT_COUNT_END;
					String responseCount = RESTClient.useHttpClientPOST(credentialsItem, sqlCount);
					//System.out.println("Response: " + responseCount);
					
					resultsField.setText(responseCount.replaceAll("\\]|\\[", ""));
					
					String sql = selectField.getText() + SQL_SELECT_FROM + sqlWherePart + SQL_SELECT_END;
					
					String response = RESTClient.useHttpClientPOST(credentialsItem, sql);
					//System.out.println("Response: " + response);

					Table.updateTable(table, response);
					
				} catch (Exception e) {
					System.out.println("ERROR: " + e.getMessage());
					e.printStackTrace();
				}

			}

		});

		frame.add(button);

		frame.add(table);
		
	}

	private static JTextField createURLElements(final JFrame frame) {
		
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
		
		return urlField;
		
	}
	
	private static JTextField createSelectElements(final JFrame frame, final JTextField sqlWhereField) {
		
		JLabel sqlLabel = new JLabel("SQL: ");

		sqlLabel.setBounds(10, 40 + 3, 80, 15); // x axis, y axis, width, height

		frame.add(sqlLabel);
		
		
		
		final JTextField selectField = new JTextField(20);

		selectField.setText(SELECT_ALL);

		//selectField.setBounds(300, 40, 675, 20); // x axis, y axis, width, height

		selectField.setBounds(300, 40, 400, 20); // x axis, y axis, width, height

		frame.add(selectField);
		
		
		
		JLabel sqlWhereLabel = new JLabel(SQL_SELECT_WHERE);

		sqlWhereLabel.setBounds(710, 40 + 3, 265, 15); // x axis, y axis, width, height

		frame.add(sqlWhereLabel);
		
		
		
		String[] selectStrings = SELECT_NAMES;

		final JComboBox<String> selectList = new JComboBox<String>(selectStrings);
		
		selectList.setSelectedIndex(1);
		
		selectField.setEditable(false);
		
		selectList.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				int selectedSelect = selectList.getSelectedIndex();
				if (selectedSelect == 0) {
					selectField.setEditable(true);
				} else {
					selectField.setEditable(false);
				}
				selectField.setText(SELECT_VALUES[selectedSelect]);
				
				String sqlWhereToday = getTodayDateSQLString();
				
				if (selectedSelect == 1) {
					sqlWhereField.setText(sqlWhereToday);
				}
				if (selectedSelect == 2) {
					sqlWhereField.setText(sqlWhereToday + SQL_SELECT_GROUP_BY_WS_NAME);
				}
				if (selectedSelect == 3) {
					sqlWhereField.setText(sqlWhereToday + SQL_SELECT_GROUP_BY_WS_NAME_WS_RESULT);
				}
			}
			
		});
		
		selectList.setBounds(70, 40, 220, 20); // x axis, y axis, width, height

		frame.add(selectList);
		
		
		
		JLabel sqlLabel2 = new JLabel("WHERE: ");

		sqlLabel2.setBounds(10, 70 + 3, 80, 15); // x axis, y axis, width, height

		frame.add(sqlLabel2);
		
		
		
/*		final JTextField sqlField = new JTextField(20);

		sqlField.setText(SQL_SELECT_START + "<WHERE>" + SQL_SELECT_END);

//		sqlField.setBounds(70, 40, 560 + 345, 20); // x axis, y axis, width, height

		sqlField.setBounds(300, 40, 675, 20); // x axis, y axis, width, height

		sqlField.setEditable(false);
		
		frame.add(sqlField);*/

		
		
		return selectField;

	}

	private static JTextField createResultElements(final JFrame frame) {
		
		JLabel resultsLabel = new JLabel("Results: ");

		resultsLabel.setBounds(10, 150 + 3, 80, 15); // x axis, y axis, width, height

		frame.add(resultsLabel);

		final JTextField resultsField = new JTextField(20);

		resultsField.setText("0");

		resultsField.setBounds(70, 150, 560 + 345, 20); // x axis, y axis, width, height

		resultsField.setEditable(false);
		
		frame.add(resultsField);
		return resultsField;
		
	}

	private static void createHelpButton(final JFrame frame) {
		
		JButton button2 = new JButton("Help"); // creating instance of JButton
		button2.setBounds(875, 100, 100, 40); // x axis, y axis, width, height
		button2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				
				String today = getTodayDateSQLString();
				
				StringBuilder sbHelp = new StringBuilder();
				sbHelp.append("Today: " + today + "\n");
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

}
