package callIELibraries;

import java.io.*;
import java.awt.event.*;
import javax.swing.*;

public class Console implements Runnable
{
     JTextArea displayPane;
     BufferedReader reader;
     
     Console(){
    	 
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

     public static void showInGUI(String parameter){
    	 JTextArea textArea = new JTextArea(50,50);
         //JScrollPane scrollPane = new JScrollPane(textArea);
   	 
   	  	JFrame frame = new JFrame("Redirect Output");
         frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
   	 
         frame.add( textArea );  //scrollPane
         //frame.getContentPane().add(t);
         frame.setSize(100, 100);
         frame.pack();
         frame.setVisible(true);

         Console.redirectOutput( textArea );
         final int i = 0;
         
         System.out.println( parameter );
     }
     
     public static void main(String[] args)
     {
          /*
    	  JTextArea textArea = new JTextArea();
          JScrollPane scrollPane = new JScrollPane(textArea);
          
          JTextField inputSentence = new JTextField(50);          
          String getValue = inputSentence.getText();

          JFrame frame = new JFrame("Redirect Output");
          frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
          
          frame.getContentPane().add(inputSentence );
          frame.getContentPane().add(new JTextField("Text field 2", 8));
          JTextField t = new JTextField("Text field 3", 8);
          t.setHorizontalAlignment(JTextField.RIGHT);
          frame.getContentPane().add(t);
          */
          
    	  JTextArea textArea = new JTextArea(50,50);
          //JScrollPane scrollPane = new JScrollPane(textArea);
    	 
    	  JFrame frame = new JFrame("Redirect Output");
          frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    	 
          frame.add( textArea );  //scrollPane
          //frame.getContentPane().add(t);
          frame.setSize(300, 300);
          frame.pack();
          frame.setVisible(true);

          Console.redirectOutput( textArea );
          final int i = 0;
          
          System.out.println( "test" );
          
       
          
//          Timer timer = new Timer(1000, new ActionListener()
//          {
//               public void actionPerformed(ActionEvent e)
//               {
//                    System.out.println( new java.util.Date().toString() );
//                    System.err.println( System.currentTimeMillis() );
//               }
//          });
//          timer.start();
          
     }
}
