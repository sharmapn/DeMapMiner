package sampleCodes;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ReadInputFileData extends JPanel {
    private final JTable table;

    public ReadInputFileData(String fileName) {
        super(new BorderLayout(3, 3));
        this.table = new JTable(new MyModel());
        this.table.setPreferredScrollableViewportSize(new Dimension(1000, 600));
        this.table.setFillsViewportHeight(true);
        JPanel ButtonOpen = new JPanel(new FlowLayout(FlowLayout.CENTER));
        add(ButtonOpen, BorderLayout.SOUTH);
        // Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        // Add the scroll pane to this panel.
        add(scrollPane, BorderLayout.CENTER);
        // add a nice border
        setBorder(new EmptyBorder(5, 5, 5, 5));
        CSVFile Rd = new CSVFile();
        MyModel NewModel = new MyModel();
        this.table.setModel(NewModel);
        File DataFile = new File(fileName);
        ArrayList<String[]> Rs2 = Rd.ReadCSVfile(DataFile);
        NewModel.AddCSVData(Rs2);
        System.out.println("Rows: " + NewModel.getRowCount());
        System.out.println("Cols: " + NewModel.getColumnCount());
    }

    // Method for reading CSV file
    public class CSVFile {
        private final ArrayList<String[]> Rs = new ArrayList<String[]>();
        private String[] OneRow;

        public ArrayList<String[]> ReadCSVfile(File DataFile) {
            try {
                BufferedReader brd = new BufferedReader(new FileReader(DataFile));
                while (brd.ready()) {
                    String st = brd.readLine();
                    OneRow = st.split(",|\\s|;");
                    Rs.add(OneRow);
                    System.out.println(Arrays.toString(OneRow));
                } // end of while
            } // end of try
            catch (Exception e) {
                String errmsg = e.getMessage();
                System.out.println("File not found:" + errmsg);
            } // end of Catch
            return Rs;
        }// end of ReadFile method
    }// end of CSVFile class

    private static void createAndShowGUI() {
        // Create and set up the window.
        JFrame frame = new JFrame("T1Data");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Create and set up the content pane.
        ReadInputFileData newContentPane = new ReadInputFileData("c:\\scripts\\data.txt");
        frame.setContentPane(newContentPane);
        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    class MyModel extends AbstractTableModel {
        private final String[] columnNames = { "1", "2", "3", "4" };
        private ArrayList<String[]> Data = new ArrayList<String[]>();

        public void AddCSVData(ArrayList<String[]> DataIn) {
            this.Data = DataIn;
            this.fireTableDataChanged();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;// length;
        }

        @Override
        public int getRowCount() {
            return Data.size();
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            return Data.get(row)[col];
        }
    }

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
//                createAndShowGUI();
            }
        });
        
        JFrame frame = new JFrame("Tabbed Pane Sample");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        String titles[] = { "A", "B", "C", "D", "E", "F" };
        for (int i = 0, n = titles.length; i < n; i++) {
        	JButton button = new JButton(titles[i]);
        //	tabbedPane.addTab(titles[i], new ImageIcon("yourFile.gif"), button, titles[i]);
        	if (i==0){
        		 // Create and set up the window.
              
                ReadInputFileData newContentPane = new ReadInputFileData("c:\\scripts\\data.txt");
                tabbedPane.addTab("File", newContentPane);
           //     frame.setContentPane(newContentPane);
                // Display the window.
           //     frame.pack();
           //     frame.setVisible(true);
        	}
        	else if (i==2){
               ReadInputFileData newContentPane = new ReadInputFileData("c:\\scripts\\input-BaseIdeasMoreParam.txt");
               tabbedPane.addTab("File", newContentPane);         
        	}
        	
        }
        //tabbedPane.addTab(label, new ImageIcon("yourFile.gif"), button, titles[i]);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setVisible(true);
        
        
    }
}
