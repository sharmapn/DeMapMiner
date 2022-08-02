package ManualAnalysis.StatesAndReasons;

import javax.swing.table.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
  
public class testTable extends JFrame {
   public testTable() {
      super("TableModel Demonstration");
   
      // create our own custom TableModel
      WineTableModel wineModel = new WineTableModel();
      JTable table = new JTable(wineModel);
  
      // since we're using values of floats and boolean here, we need
      // to set the cell renderer for every column.
      for (int i =0; i<wineModel.getColumnCount();i++) {
         table.setDefaultRenderer(table.getColumnClass(i), new WineCellRenderer());
      }
        
      // add rows to our TableModel, each row is represented as a Wine object
      wineModel.addWine(new Wine("Chateau Meyney, St. Estephe", "1994", 18.75f, true));
      wineModel.addWine(new Wine("Chateau Montrose, St. Estephe", "1975", 54.25f, true));
      wineModel.addWine(new Wine("Chateau Gloria, St. Julien", "1993", 22.99f, false));
      wineModel.addWine(new Wine("Chateau Beychevelle, St. Julien", "1970", 61.63f, false));
      wineModel.addWine(new Wine("Chateau La Tour de Mons, Margeaux", "1975", 57.03f, true));
      wineModel.addWine(new Wine("Chateau Brane-Cantenac, Margeaux", "1978", 49.92f, false));
  
      // create the scroll pane and add the table to it.
      JScrollPane scrollPane = new JScrollPane(table);
  
      // add the scroll pane to this window.
      getContentPane().add(scrollPane, BorderLayout.CENTER);
  
      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            System.exit(0);
         }
      });
   }
  
   public static void main(String[] args) {
	  testTable main = new testTable();
      main.pack();
      main.setVisible(true);
   }
}
  
// a simple object that holds data about a particular wine
class Wine {
   private String  name;
   private String  vintage;
   private float   price;
   private boolean inStock;
  
   public Wine(String name, String vintage, float price, boolean inStock) {
      this.name = name;
      this.vintage = vintage;
      this.price = price;
      this.inStock = inStock;
   }
  
   public String getName()     { return name; }
   public String getVintage()  { return vintage; }
   public float  getPrice()    { return price; }
   public boolean getInStock() { return inStock; }
  
   public String toString() {
      return "[" + name + ", " + vintage + ", " + price + ", " + inStock + "]"; }
}
  
class WineTableModel extends AbstractTableModel {
   // holds the strings to be displayed in the column headers of our table
   final String[] columnNames = {"Name", "Vintage", "Price", "In stock?"};
  
   // holds the data types for all our columns
   final Class[] columnClasses = {String.class, String.class, Float.class, Boolean.class};
  
   // holds our data
   final Vector data = new Vector();
   
   // adds a row
   public void addWine(Wine w) {
      data.addElement(w);
      fireTableRowsInserted(data.size()-1, data.size()-1);
   }
  
   public int getColumnCount() {
      return columnNames.length;
   }
          
   public int getRowCount() {
      return data.size();
   }
  
   public String getColumnName(int col) {
      return columnNames[col];
   } 
  
   public Object getValueAt(int row, int col) {
      Wine wine = (Wine) data.elementAt(row);
      if (col == 0)      return wine.getName();
      else if (col == 1) return wine.getVintage();
      else if (col == 2) return new Float(wine.getPrice());
      else if (col == 3) return new Boolean(wine.getInStock());
      else return null;
   }
  
   public Object getValueAtRow(int row) {
      Wine wine = (Wine) data.elementAt(row);
      return wine;
   }
  
   public boolean isCellEditable(int row, int col) {
      return false;
   }
}
  
class WineCellRenderer extends DefaultTableCellRenderer {
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,boolean hasFocus, int row, int column)
   {
      WineTableModel wtm = (WineTableModel) table.getModel();
      Wine wine = (Wine) wtm.getValueAtRow(row);
  
      if (column == 3 && wine.getInStock() == false) {
         setBackground(Color.red);
      }
      else {
         setBackground(Color.white);
      }
  
      return super.getTableCellRendererComponent(table, value, isSelected,hasFocus, row, column);
   }
}
