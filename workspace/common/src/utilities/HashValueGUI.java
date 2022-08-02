package utilities;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class HashValueGUI {
	
	public static void main(String[] args) throws Exception { 

    	//CREATE WINDOW
       JFrame aWindow = new JFrame("Compute Hash");
        //aWindow.setBackground(Color.RED);
        //aWindow.getContentPane().setBackground(Color.RED);
        int windowWidth = 800;           // Window width in pixels
        int windowHeight = 800;          // Window height in pixels
        aWindow.setBounds(50, 100,       // Set position
             windowWidth, windowHeight);  // and size
        JPanel panel = new JPanel(new BorderLayout());
        //panel.setBackground(Color.GRAY);
        JPanel panel1 = new JPanel();
        JTextArea output = new JTextArea(5, 50);
        
       
        
        JButton btn1 = new JButton("Compute");
        JButton btn2 = new JButton("Clear");
       
        panel1.add(btn1);
        panel1.add(btn2);
   
        
        
        JPanel panel2 = new JPanel();
        JTextArea input = new JTextArea("", 100, 60);
        input.setFont(new Font("Arial",Font.PLAIN,11));
        JScrollPane scroll = new JScrollPane (input);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);             
        DefaultCaret caret = (DefaultCaret)input.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        panel2.add(scroll);
        panel1.add(output);
        Console.redirectOutput( output );
        
        
        btn1.addActionListener(new ActionListener() {
       	 
            public void actionPerformed(ActionEvent e)
            {              
            	String sentStr = input.getText();
				Integer code = sentStr.hashCode();
				output.setText(code.toString());
            }
        });  
        btn2.addActionListener(new ActionListener() {       	 
            public void actionPerformed(ActionEvent e)
            {
            	output.setText(null);
            }
        }); 
        
        
       
        aWindow.add(panel);
        //add(panel);
        panel.add(panel1,BorderLayout.NORTH);
        panel.add(panel2,BorderLayout.CENTER);
        aWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        aWindow.setVisible(true);        // Display the window 
    
    }
    
}
