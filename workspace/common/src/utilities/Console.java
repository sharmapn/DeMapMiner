package utilities;

import java.io.*;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;

public class Console implements Runnable
{
     JTextArea displayPane;
     BufferedReader reader;
     
     public Console(){
    	 
     }

     private Console(JTextArea displayPane, PipedOutputStream pos)
     {
          this.displayPane = displayPane;

          try
          {
               PipedInputStream pis = new PipedInputStream( pos );
               reader = new BufferedReader( new InputStreamReader(pis) );
          }
          catch(IOException e) {}
     }

     public void run()
     {
          String line = null;
         
          try
          {
               while ((line = reader.readLine()) != null)
               {
                    displayPane.replaceSelection( line + "\n" );
                    displayPane.setCaretPosition( displayPane.getDocument().getLength() );
               }

               System.err.println("im here");
          }
          catch (IOException ioe)
          {
        	  /*   
        	   * JOptionPane.showMessageDialog(null,
                    "Error redirecting output : "+ioe.getMessage());
              */
          }
          
     }

     public static void redirectOutput(JTextArea displayPane)
     {
          Console.redirectOut(displayPane);
          Console.redirectErr(displayPane);
     }

     public static void redirectOut(JTextArea displayPane)
     {
          PipedOutputStream pos = new PipedOutputStream();
          System.setOut( new PrintStream(pos, true) );

          Console console = new Console(displayPane, pos);
          new Thread(console).start();
     }

     public static void redirectErr(JTextArea displayPane)
     {
          PipedOutputStream pos = new PipedOutputStream();
          System.setErr( new PrintStream(pos, true) );

          Console console = new Console(displayPane, pos);
          new Thread(console).start();
     }

     public static String showInGUI(){
    	 JTextArea textArea = new JTextArea(50, 50);
 		JScrollPane scrollPane = new JScrollPane(textArea);

 		JTextField inputSentence = new JTextField(50);
 		String getValue = inputSentence.getText();

 		JFrame frame = new JFrame("Redirect Output");
 		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

 		frame.getContentPane().add(inputSentence);
 		frame.getContentPane().add(new JTextField("Text field 2", 8));
 		JTextField t = new JTextField("Text field 3", 8);
 		t.setHorizontalAlignment(JTextField.RIGHT);
 		frame.getContentPane().add(t);

 		frame.setBackground(Color.YELLOW);
 		// JTextArea textArea = new JTextArea(50,50);
 		// JScrollPane scrollPane = new JScrollPane(textArea);

 		// JFrame frame = new JFrame("Redirect Output");
 		// frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

 		frame.add(textArea); // scrollPane
 		// frame.getContentPane().add(t);
 		frame.setSize(300, 300);
 		frame.pack();
 		frame.setVisible(true);

 		Console.redirectOutput(textArea);
 		final int i = 0;

 		//System.out.println(parameter);

 		Panel p = new Panel();
 		p.setBackground(Color.YELLOW);
 		Panel p1 = new Panel();
 		p1.setBackground(Color.YELLOW);
 		Label jFirstName = new Label("Enter Sentence/Phrase");
 		TextField lFirstName = new TextField(50);
 		
 		//left frame
 		Panel p2 = new Panel();
 		p2.setBackground(Color.RED);
 		TextField test = new TextField(50);
 		//p2.add(test);
 		
 		/*
 		JButton display;                       // a button to raise an event
 		JTextArea tf ;                         // a text area that contains some text
 		String str = "Java is simple to practice.\nBut is ocean.\nRequires lot of practice\n to become a good programmer.\nWhenever free, open the Java API and browse the methods in each class.";
 	    display = new JButton("Display");
 		tf = new JTextArea(str, 10, 10);     // text area of size 10 rows and 30 columns 		 
 //	    display.addActionListener(this); 	   
 	    p2.add(tf);
 	    p2.add(display);
 		*/
 	    
 		p.setLayout(new GridLayout(3, 1));
 		p.add(jFirstName);
 		p.add(lFirstName);
 		
 		Button Submit = new Button("Submit");
 		p.add(Submit);
 		
 		Button Clear = new Button("Clear");
 		p.add(Clear);
 		
 		p1.add(p);
 		frame.add(p1, BorderLayout.NORTH);
 		//frame.add(p2, BorderLayout.WEST);
 		
 		
 		
 	// add the listener to the jbutton to handle the "pressed" event
 		Submit.addActionListener(new ActionListener()
 	    {
 	      public void actionPerformed(ActionEvent e)
 	      {
 	        // display/center the jdialog when the button is pressed
// 	        JDialog d = new JDialog(frame, "Hello", true);
// 	        d.setLocationRelativeTo(frame);
// 	        d.setVisible(true);
 	    	  
 	      }
 	    });
 		
 	// adds event handler for button Clear
        Clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // clears the text area
                try {
                    textArea.getDocument().remove(0,textArea.getDocument().getLength());
                    System.out.println("Text area cleared");
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });
 		
 		return lFirstName.toString();
 		
     }
     
     public static void main(String[] args)
     {          
    	 showInGUI();          
     }
}
