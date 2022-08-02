package GUI.helpers2;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class JavaTable {
  public static void main(String[] argv) throws Exception {
    DefaultTableModel model = new DefaultTableModel();
    JTable table = new JTable(model);

    model.addColumn("Pep");
    model.addColumn("Sentence");
    model.addColumn("KeywordMatching");
    model.addColumn("SemanticMatching");
    model.addColumn("Classification");
    
    JFrame f = new JFrame();
    f.setSize(300, 300);
    f.add(new JScrollPane(table));
    f.setVisible(true);
    
    int i=0;
    while (i<10){
    	
    	try {
    	    Thread.sleep(1000);                 //1000 milliseconds is one second.
    	} catch(InterruptedException ex) {
    	    Thread.currentThread().interrupt();
    	}
    	
    	
    	model.addRow(new Object[] { "v1", "v2" });
    	model.addRow(new Object[] { "v1" });
    	model.addRow(new Object[] { "v1", "v2", "v3" });
    	i++;
    }
    

  }
}