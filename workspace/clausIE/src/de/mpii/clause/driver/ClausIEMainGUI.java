package de.mpii.clause.driver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

//import Ollie.JavaOllieWrapper;
//import Ollie.JavaOllieWrapperGUI;





import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.mpii.clausie.ClausIE;
import de.mpii.clausie.Clause;
import de.mpii.clausie.Proposition;


public class ClausIEMainGUI extends JPanel {
	ClausIE clausIE;
//	public ClausIEMain2(){
//		clausIE = new ClausIE();
//        clausIE.initParser();
//        clausIE.getOptions().print(System.out, "# ");
//	}
	
	//public ClausIEMainGUI() { 
    public static void main(String[] args)  throws IOException  {
        ClausIE clausIE = new ClausIE();
        clausIE.initParser();
        clausIE.getOptions().print(System.out, "# ");
        
        //CREATE WINDOW
      JFrame aWindow = new JFrame("ClauseIE");
        //aWindow.setBackground(Color.RED);
        //aWindow.getContentPane().setBackground(Color.RED);
        int windowWidth = 800;           // Window width in pixels
        int windowHeight = 800;          // Window height in pixels
        aWindow.setBounds(50, 100,       // Set position
             windowWidth, windowHeight);  // and size
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.GRAY);
        JPanel panel1 = new JPanel();
        JTextArea tt = new JTextArea(5, 50);
        
       
        
        JButton btn1 = new JButton("Compute");
        JButton btn2 = new JButton("Clear");
        JButton btn3 = new JButton("ReadFileCompute");
        panel1.add(btn1);
        panel1.add(btn2);
        panel1.add(btn3);
        panel1.add(tt);
        
        JPanel panel2 = new JPanel();
        JTextArea output = new JTextArea("", 100, 60);
        output.setFont(new Font("Arial",Font.PLAIN,10));
        JScrollPane scroll = new JScrollPane (output);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);             
        DefaultCaret caret = (DefaultCaret)output.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        panel2.add(scroll);
        
     // JavaOllieWrapper jw = new JavaOllieWrapper();
        
        Console.redirectOutput( output );
     
        System.out.print("clauseIE");
        btn1.addActionListener(new ActionListener() {
        	 
            public void actionPerformed(ActionEvent e)
            {
                //Execute when button is pressed
                //System.out.println("You clicked the button");
                
             // input sentence
                // String sentence =
                //"PEP 488 is now marked as accepted. ";
 //               String sentence = "Bell, a telecommunication company, which is based in Los Angeles, makes and distributes electronic, computer and building products.";
                // String sentence = "There is a ghost in the room";
                // sentence = "Bell sometimes makes products";
                // sentence = "By using its experise, Bell made great products in 1922 in Saarland.";
                // sentence = "Albert Einstein remained in Princeton.";
                // sentence = "Albert Einstein is smart.";
                
            	//String sentence = "Sorry for the delay, but I've finally updated PEP 389, the argparse PEP, based on all the feedback from python-dev.  ";
            	
            	String sentence = tt.getText();
            	
                System.out.println("Input sentence  s : " + sentence);

                // parse tree
                System.out.print("Parse time       : ");
                long start = System.currentTimeMillis();
                clausIE.parse(sentence);
                long end = System.currentTimeMillis();
                System.out.println((end - start) / 1000. + "s");
                System.out.print("Dependency parse : ");
                System.out.println(clausIE.getDepTree().pennString()
                        .replaceAll("\n", "\n                   ").trim());
                System.out.print("Semantic graph   : ");
                System.out.println(clausIE.getSemanticGraph().toFormattedString()
                        .replaceAll("\n", "\n                   ").trim());

                // clause detection
                System.out.print("ClausIE time     : ");
                start = System.currentTimeMillis();
                clausIE.detectClauses();
                clausIE.generatePropositions();
                end = System.currentTimeMillis();
                System.out.println((end - start) / 1000. + "s");
                System.out.print("Clauses          : ");
                String sep = "";
                for (Clause clause : clausIE.getClauses()) {
                    System.out.println(sep + clause.toString(clausIE.getOptions()));
                    sep = "                   ";
                }

                // generate propositions
                System.out.print("Propositions     : ");
                sep = "";
                
                
                
                for (Proposition prop : clausIE.getPropositions()) {
                    System.out.println(sep + prop.toString());
                    sep = "          CurrentSentenceString         ";
                    
                    
                    
                }
                
             
//                try {
//					jw.computOllie(sentence);
//				} catch (MalformedURLException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
            }
        });  
        btn2.addActionListener(new ActionListener() {       	 
            public void actionPerformed(ActionEvent e)
            {
            	output.setText(null);
            }
        }); 
        
        btn3.addActionListener(new ActionListener() {         	 
            public void actionPerformed(ActionEvent e)
            {
            	//readfilecontents into array
            	ReadFileLinesIntoArray rf = new ReadFileLinesIntoArray();
            	String[] array = null;
				try {
					array = rf.readFromFile("C:\\DeMapMiner\\inputLabels2.txt");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	//for loop 
            	for (String sen: array){
            		String sections[] = sen.split(",");
					String v_idea = 		   sections[0];					
					String CurrSentence = sections[1];
					CurrSentence = CurrSentence.replace("'", "");
            		compute(CurrSentence);
            	}
            	//send sentence            	
            }
        }); 
       
       aWindow.add(panel);
       // add(panel);
        panel.add(panel1,BorderLayout.WEST);
        panel.add(panel2,BorderLayout.EAST);
        aWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        aWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        aWindow.setVisible(true);        // Display the window
        //setVisible(true);        // Display the window
    }
    
    
    
    public static void compute(String sentence){
    	
    	ClausIE clausIE = new ClausIE();
      clausIE.initParser();
 //     clausIE.getOptions().print(System.out, "# ");
    	
		//sentence = "Sorry for the delay, but I've finally updated PEP 389, the argparse PEP, based on all the feedback from python-dev.  ";
    	
        System.out.println("\nInput sentence  d : " + sentence);
        System.out.print("Ollie c");
        // parse tree
 //       System.out.print("Parse time       : ");
        long start = System.currentTimeMillis();
        clausIE.parse(sentence);
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000. + "s");
        System.out.print("Dependency parse : ");
        System.out.println(clausIE.getDepTree().pennString()
                .replaceAll("\n", "\n                   ").trim());
        System.out.print("Semantic graph   : ");
        System.out.println(clausIE.getSemanticGraph().toFormattedString()
                .replaceAll("\n", "\n                   ").trim());

        // clause detection
//        System.out.print("ClausIE time     : ");
        start = System.currentTimeMillis();
        clausIE.detectClauses();
        clausIE.generatePropositions();
        end = System.currentTimeMillis();
        System.out.println((end - start) / 1000. + "s");
        System.out.print("Clauses          : ");
        String sep = "";
        for (Clause clause : clausIE.getClauses()) {
            System.out.println(sep + clause.toString(clausIE.getOptions()));
            sep = "                   ";
        }

        // generate propositions
        System.out.print("Propositions     : ");
        sep = "";
        for (Proposition prop : clausIE.getPropositions()) {
            System.out.println(sep + prop.toString());
            sep = "                   ";
        }
		
	}
    
    public String checkProposition(String sentence, String subject, String verb, String object){
    	
        
        System.out.println("\nInput sentenced  p : " + sentence);

        // parse tree
//        System.out.print("Parse time       : ");
//        long start = System.currentTimeMillis();
        clausIE.parse(sentence);
//        long end = System.currentTimeMillis();
        
//        System.out.println((end - start) / 1000. + "s");
//        System.out.print("Dependency parse : ");
//        System.out.println(clausIE.getDepTree().pennString()
//                .replaceAll("\n", "\n                   ").trim());
//        System.out.print("Semantic graph   : ");
//        System.out.println(clausIE.getSemanticGraph().toFormattedString()
//                .replaceAll("\n", "\n                   ").trim());

        // clause detection
//        System.out.print("ClausIE time     : ");
//        start = System.currentTimeMillis();
        clausIE.detectClauses();
        clausIE.generatePropositions();
//        end = System.currentTimeMillis();
//        System.out.println((end - start) / 1000. + "s");
        System.out.print("Clauses          : ");
        String sep = "";
        
        List<String> c = new ArrayList<String>(); 
        
        for (Clause clause : clausIE.getClauses()) {
            System.out.println(sep + clause.toString(clausIE.getOptions()));
            sep = "                   ";
            c.add(clause.toString(clausIE.getOptions()));
        }
        
        List<String> p = new ArrayList<String>(); 
        
        // generate propositions
        System.out.print("Propositions     : ");
        sep = "";
        
        String s;
        String all = null;
        
        for (Proposition prop : clausIE.getPropositions()) {
            System.out.println(sep + prop.toString());
            sep = "                   ";
        }
        
        for (Proposition prop : clausIE.getPropositions()) {
            System.out.println(sep + prop.toString());
            sep = "                   ";
            p.add(prop.toString());
            
            s=prop.toString();
            //or combine all strings in one, just for now
	        all += s;            
            
            s= s.replace("(", "");
			s= s.replace(")", "");
			
			String terms[] = s.split(",");
			String v_idea =   terms[0];
			String v_subject =  terms[1];
			String v_verb    =  terms[2];
			String v_object  =  terms[3];	
			
			//if the poposistion contains the triple terms, return the lable/idea
			//can be further refines to see if it does not contain not, etc which would change meaning
			//also it return the first match found, can be further coded to handle cases when the idea label would be further down in the array
			if (v_verb.contains(verb)&& v_subject.contains(subject) && v_object.contains(v_object)  ){
				//return v_idea;
				System.out.println("MATCH FOUND");
			}

		}
        return null;
        
    }
}
