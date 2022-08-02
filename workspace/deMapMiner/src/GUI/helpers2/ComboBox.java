package GUI.helpers2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 
public class ComboBox {
    
   private JFrame mainFrame;
   private JLabel headerLabel;
   private JLabel statusLabel;
   private JPanel controlPanel;

   public ComboBox(){
      prepareGUI();
   }

   public static void main(String[] args){
      ComboBox  swingControlDemo = new ComboBox();      
      swingControlDemo.showComboboxDemo();
   }

   private void prepareGUI(){
      mainFrame = new JFrame("Java Swing Examples");
      mainFrame.setSize(400,400);
      mainFrame.setLayout(new GridLayout(3, 1));
      mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      });    
      headerLabel = new JLabel("", JLabel.CENTER);        
      statusLabel = new JLabel("",JLabel.CENTER);    

      statusLabel.setSize(350,100);

      controlPanel = new JPanel();
      controlPanel.setLayout(new FlowLayout());

      mainFrame.add(headerLabel);
      mainFrame.add(controlPanel);
      mainFrame.add(statusLabel);
      mainFrame.setVisible(true);  
   }

   private void showComboboxDemo(){                                    
      headerLabel.setText("Control in action: JComboBox"); 

      final DefaultComboBoxModel fruitsName = new DefaultComboBoxModel();

      fruitsName.addElement("Apple");
      fruitsName.addElement("Grapes");
      fruitsName.addElement("Mango");
      fruitsName.addElement("Peer");

      final JComboBox fruitCombo = new JComboBox(fruitsName);    
      fruitCombo.setSelectedIndex(0);

      JScrollPane fruitListScrollPane = new JScrollPane(fruitCombo);    

      JButton showButton = new JButton("Show");

      showButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) { 
            String data = "";
            if (fruitCombo.getSelectedIndex() != -1) {                     
               data = "Fruits Selected: " 
                  + fruitCombo.getItemAt
                    (fruitCombo.getSelectedIndex());             
            }              
            statusLabel.setText(data);
         }
      }); 
      controlPanel.add(fruitListScrollPane);          
      controlPanel.add(showButton);    
      mainFrame.setVisible(true);             
   }
}