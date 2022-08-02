package relationExtraction;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

public class relationsViewerOLD extends JPanel {
	
	public static final String APP_TITLE = "Relations Viewer",SUBJECT = "Subject",VERB = "Verb",OBJECT = "Object",SENTENCE = "Sentence";	
	//index[0] must be "larger than"
	private static final String[] optionsForPopulation = {"Population larger than", "Population less than"}, optionsForMatch = {"Partial match", "Exact match"}; //index[0] must be "partial match" 
	private static List<relations> relationList = new ArrayList<relations>();
	private static Database db;
	private static relationsTableModel tableModel;	
	private static JTextField subjectInput, verbInput, objectInput, sentenceInput;	
	private static JButton addButton,searchButton;	
	private static JComboBox<String> populationSearchOptions, matchSearchOptions;
	
	public relationsViewerOLD() {
//		super(APP_TITLE);
		
//$$		db = new Database(relationList);
//$$		db.readAll();
		
		setLayout(new BorderLayout(4, 4));
		//add(makeInputFieldsPanel(), BorderLayout.NORTH);
		
//		super(APP_TITLE);
		
		
		JButton button2 = new JButton("Retrieve Data"); 		button2.setBounds(20,30,50,30); 		add(button2);
		
		String[] tableNames = new String[] {"relations","extractedrelations_clausie_reasontriples"};
		JComboBox<String> tableNamesList = new JComboBox<>(tableNames);
		db = new Database(relationList);
		db.setTableName("relations");
		//add to the parent container (e.g. a JFrame):
		JLabel lblTable = new JLabel("Select Table");		lblTable.setBounds(80, 27, 84, 14);		// Label Search
		
		setLayout(new BorderLayout(4, 4));
  		//j.add(makeInputFieldsPanel(), BorderLayout.NORTH);
  		JPanel inputFieldsPanel = new JPanel();
		inputFieldsPanel.setLayout(new FlowLayout());
		inputFieldsPanel.add(lblTable);		inputFieldsPanel.add(tableNamesList);	//inputFieldsPanel.add(makeControlPanel());	
		
		JPanel contolPanel = new JPanel();
		contolPanel.setLayout(new BoxLayout(contolPanel, BoxLayout.X_AXIS));		
		addButton = new JButton("Add"); 			contolPanel.add(addButton);		
		searchButton = new JButton("Search"); 		contolPanel.add(searchButton);		
		//contolPanel.add(makeSearchOptionBox());
				
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));
		optionsPanel.setBorder(new TitledBorder("Search options"));
		
		populationSearchOptions = new JComboBox<String>(optionsForPopulation);
		populationSearchOptions.setSelectedIndex(0);		optionsPanel.add(populationSearchOptions);
		matchSearchOptions = new JComboBox<String>(optionsForMatch);
		matchSearchOptions.setSelectedIndex(0);		optionsPanel.add(matchSearchOptions);
		contolPanel.add(optionsPanel);
		
		inputFieldsPanel.add(contolPanel);
		
		JLabel cityL = new JLabel(SUBJECT + ":"); 	   	inputFieldsPanel.add(cityL);	  subjectInput = new JTextField(10);  	inputFieldsPanel.add(subjectInput);	    	    		
	    JLabel contL = new JLabel(VERB + ":"); 	    	inputFieldsPanel.add(contL);       verbInput = new JTextField(10); 	   	inputFieldsPanel.add(verbInput);	    
	    JLabel populationL = new JLabel(OBJECT + ":"); 	inputFieldsPanel.add(populationL); objectInput = new JTextField(10); 	inputFieldsPanel.add(objectInput);
		
		    //get the selected item:
		
  		tableNamesList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {          
				//String selectedsTo = (String) tableNamesList.getSelectedItem();
				String dataTableName = (String) tableNamesList.getSelectedItem();
				db.setTableName(dataTableName);
			}
		});
		
		// add the listener to the jbutton to handle the "pressed" event
	    button2.addActionListener(new ActionListener()	    {
	      public void actionPerformed(ActionEvent e)	      {
	        // display/center the jdialog when the button is pressed
//	        JDialog d = new JDialog(j, "Hello", true);
//	        d.setLocationRelativeTo(j);	        d.setVisible(true);
	    	
	  		
	  		db.readAll();		  			    	    		
		    
	//	    JLabel sentenceL = new JLabel(SENTENCE + ":");	    inputFieldsPanel.add(sentenceL);
	//	    sentenceInput = new JTextField(10);//	    inputFieldsPanel.add(sentenceInput);
		    		    
	  		tableModel = new relationsTableModel(relationList);	  		
	  		add(makeResultTable(tableModel), BorderLayout.CENTER);	  		
//	  		addListners();  
	      }
	    });
	    
	    searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					db.search(subjectInput.getText(),verbInput.getText(),
							//parsePopulationInput(),
							objectInput.getText(),(matchSearchOptions.getSelectedIndex() == 0),(populationSearchOptions.getSelectedIndex() == 0));
					tableModel.fireTableDataChanged();
				} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
				}
			}
		});
	    add(inputFieldsPanel);
//		pack();
		setVisible(true);
		
		tableModel = new relationsTableModel(relationList);
		add(makeResultTable(tableModel), BorderLayout.CENTER);		
	}
	/*
	private static JPanel makeInputFieldsPanel() {
		
		
			
		try {
			
		}
	    catch (Exception e)	    {
			System.err.println("Got an exception! ");			System.err.println(e.getMessage() + " mid ");
			System.out.println(StackTraceToString(e)  );	
		}	    
	    return inputFieldsPanel;
	}
	*/
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
	
//	private static JPanel makeControlPanel() {
//		
//		return contolPanel;
//	}
	
//	private static JPanel makeSearchOptionBox() {
//		JPanel optionsPanel = new JPanel();
//		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));
//		optionsPanel.setBorder(new TitledBorder("Search options"));
//		
//		populationSearchOptions = new JComboBox<String>(optionsForPopulation);
//		populationSearchOptions.setSelectedIndex(0);
//		optionsPanel.add(populationSearchOptions);
//
//		matchSearchOptions = new JComboBox<String>(optionsForMatch);
//		matchSearchOptions.setSelectedIndex(0);
//		optionsPanel.add(matchSearchOptions);
//		
//		return optionsPanel;		
//	}
	
	private static JScrollPane makeResultTable(AbstractTableModel model) {
		JTable resultTable = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(resultTable);
		return scrollPane;
	}
	
	private static void addListners() {
//		addButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				try {
//					db.add(createCity());
//					tableModel.fireTableDataChanged();
//				} catch (Exception e) {
//					System.out.println(e);
//					e.printStackTrace();
//				}
//			}
//		});
		
		
	}

//	private relations createCity() {
//		String city = cityNameInput.getText();
//		String continent = continentInput.getText();
//		int population = parsePopulationInput();
//		if (city.length() != 0 && continent.length() != 0) {
//			return new relations(subject,relation, verb);
//		} else {
//			return null;
//		}
//	}
	
//	private int parsePopulationInput() {
//		String input = populationInput.getText();
//		if(input.length() == 0) {
//			return 0;
//		} else {
//			return Integer.parseInt(input);
//		}
//	}
}
