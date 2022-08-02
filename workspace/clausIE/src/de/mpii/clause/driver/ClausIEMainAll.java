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



//import reverb.ReVerbFindRelations;
//import reverb.ReVerbFindRelationsGUI;
//import Ollie.JavaOllieWrapper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.mpii.clausie.ClausIE;
import de.mpii.clausie.Clause;
import de.mpii.clausie.Proposition;


public class ClausIEMainAll extends JPanel {
	ClausIE clausIE;
//	public ClausIEMain2(){
//		clausIE = new ClausIE();
//        clausIE.initParser();
//        clausIE.getOptions().print(System.out, "# ");
//	}
	
	//public ClausIEMainAll() { 
    public static void main(String[] args)  throws IOException  {
        ClausIE clausIE = new ClausIE();
        clausIE.initParser();
//        clausIE.getOptions().print(System.out, "# ");
        
       // CREATE WINDOW
        JFrame aWindow = new JFrame("ClauseIE All");
        aWindow.setBackground(Color.RED);
        aWindow.getContentPane().setBackground(Color.RED);
        int windowWidth = 800;           // Window width in pixels
        int windowHeight = 800;          // Window height in pixels
        aWindow.setBounds(50, 100,       // Set position
             windowWidth, windowHeight);  // and size
        JPanel panel = new JPanel(new BorderLayout());
        //panel.setBackground(Color.GRAY);
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
        
 //       JavaOllieWrapper jw = new JavaOllieWrapper();
        
        //ReVerbFindRelationsGUI rv = new ReVerbFindRelationsGUI();
        
        Console.redirectOutput( output );
     
        
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
               String sentence = "" ; //Sorry for the delay, but I've finally updated PEP 389, the argparse PEP, based on all the feedback from python-dev.  ";
            	
            	sentence = tt.getText();
            	
                System.out.println("Input sentence  x : " + sentence);

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

                boolean foundOnce = false;
                
                // generate propositions
                System.out.print("Propositions     : ");
                sep = "";
                
              //additional code needed for the below code
                String v_subject = null;
                String v_relation = null;
                String v_object = null;
                
                String vv_subject = null;
                String vv_relation = null;
                String vv_object = null;
                boolean found =false;
                
                String conditionalStrings[] = {"if", "should", "when","i hope"};
            	boolean subjectFound = false;
            	boolean relationFound = false;
            	boolean objectFound = false;
            	String text = null;
            	String leftOver = null;                
                
                for (Proposition prop : clausIE.getPropositions()) {
                    System.out.println(sep + prop.toString());
                    sep = "                   ";
                    
                  
                    //split the string
                    String[] partsOfPreposition = prop.toString().split(",");
                    v_subject = partsOfPreposition[0];
                    v_relation = partsOfPreposition[1];
                    //sometimes the clauseIE just has two terms, subject and verb - no object. the following to prevent null pointer error
                    if (partsOfPreposition.length ==3) 
                    	v_object = partsOfPreposition[2];
                    else
                    	v_object = "";
                    
                    //-------------START CONDITIONAL CHECK
                	// we want to check and make sure if the idea is not conditional
                	// check all propositions
                	// check see if the proposition contains term if "if"
                	
                	for (String cs : conditionalStrings){
                    	if( v_subject.toLowerCase().contains(cs)) {
                    		subjectFound = true;
                    		text = v_subject;
                    		System.out.println("conditional in subject");
                    		
                    		int spaceIndex = text.toLowerCase().indexOf(cs) + cs.length();
                    		if (spaceIndex != -1)
                    		{															//-2 to handle these 2 characters: "}
                    			leftOver = text.toLowerCase().substring(spaceIndex,v_subject.length()-2);
                    		}
                    		//leftOver = text.replace(v_subject,"");
               			    System.out.println("subjectFound, leftOver: "+ leftOver);
                    		
                    	}
                    	if( v_relation.toLowerCase().contains(cs)) {
                    		relationFound = true;
                    		text = v_relation;
                    		System.out.println("conditional in verb");
                    		int spaceIndex = text.toLowerCase().indexOf(cs) + cs.length();
                    		if (spaceIndex != -1)
                    		{															//-2 to handle these 2 characters: "}
                    			leftOver = text.toLowerCase().substring(spaceIndex,v_relation.length()-2);
                    		}
               			    System.out.println("relationFound, leftOver: "+ leftOver);
                    	}
                    	if( v_object.toLowerCase().contains(cs)) {
                    		objectFound = true;
                    		text = v_object;
                    		System.out.println("conditional in object");
                    		int spaceIndex = text.toLowerCase().indexOf(cs) + cs.length();
                    		if (spaceIndex != -1)
                    		{															//-2 to handle these 2 characters: "}
                    			leftOver = text.toLowerCase().substring(spaceIndex,v_object.length()-2);
                    		}
               			    System.out.println("objectFound, leftOver: "+ leftOver);
                    	}
                	
                	// if contains, then subtract it from the next proposition section = remaining
                    	if(subjectFound || relationFound || objectFound)
                    	{
                    		foundOnce = true;
                    	}
//                    	else
//                    		foundOnce = false;
                	}
                }
                //Propositions     : ("john", "accepts", "pep")
                // ("i", "will go", "to suva if john accepts pep")
            	
            	if (foundOnce == true){
            		//go over all propositiona once again and match
                    //  use the remaining to extract relation again
                	String[] v_partsOfPreposition = null;
                	try {
                		System.out.println("trying to compute the propositiona again");
    					v_partsOfPreposition = computeClausIEOnlyForConditional(leftOver);
    				} catch (IOException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
                	// if the triples match the triple matched, then its a conditional and dont output
                	//split the string
    //xxx                v_partsOfPreposition = prop.toString().split(",");
                    vv_subject = v_partsOfPreposition[0];
                    vv_relation = v_partsOfPreposition[1];
                    //sometimes the clauseIE just has two terms, subject and verb - no object. the following to prevent null pointer error
                    if (v_partsOfPreposition.length ==3) 
                    	vv_object = v_partsOfPreposition[2];
                    else
                    	vv_object = "";
                	
                	//check
                    
                    if(v_subject.equals(vv_subject) || v_relation.equals(vv_relation) || v_object.equals(vv_object)){
                    	System.out.println("conditional");
                    }
                    else
                    	System.out.println("not conditional");
            		
            	}
            	
            	
            	
            	
                //-------------END CONDITIONAL CHECK
                
                
//                try {
//                	
//					System.out.println("ReVerb Output: ");
////**					rv.computeReverb(sentence);
//					System.out.println("Ollie Output: ");
//					jw.computOllie(sentence);
//				} catch (MalformedURLException e1) {
//					// TODO Auto-generated catch block
//					System.out.println("Malformed Output: ");
//					e1.printStackTrace();
//				} catch (IOException e1) {
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
					array = rf.readFromFile("C:\\DeMapMiner\\inputFiles\\inputLabels2.txt");
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
     //   setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        aWindow.setVisible(true);        // Display the window
        //setVisible(true);        // Display the window
    }
    
    public static String[] computeClausIEOnlyForConditional(String sentence) throws IOException{
      	 
    	ClausIE clausIE = new ClausIE();
        clausIE.initParser();
 //       clausIE.getOptions().print(System.out, "# ");
        
        clausIE.parse(sentence);
        clausIE.detectClauses();
        clausIE.generatePropositions();

        String v_subject = null;
        String v_relation = null;
        String v_object = null;        
        
        System.out.println("Clauses          : ");
        String sep = "";
        for (Clause clause : clausIE.getClauses()) {
            System.out.println( clause.toString(clausIE.getOptions()));		//sep +
            sep = "                   "; 
        }

        // generate propositions
        System.out.println("Propositions     : ");
        sep = "";
        String[] partsOfPreposition = null;
        for (Proposition prop : clausIE.getPropositions()) {
            System.out.println( prop.toString());							// sep +
            sep = "                   ";
            
            //split the string
            partsOfPreposition = prop.toString().split(",");
            v_subject = partsOfPreposition[0];
            v_relation = partsOfPreposition[1];
            //sometimes the clauseIE just has two terms, subject and verb - no object. the following to prevent null pointer error
            if (partsOfPreposition.length ==3) 
            	v_object = partsOfPreposition[2];
            else
            	v_object = "";
            
         
        }
        return partsOfPreposition;
	}
    
    public static void compute(String sentence){
    	
    	ClausIE clausIE = new ClausIE();
      clausIE.initParser();
      clausIE.getOptions().print(System.out, "# ");
    	
		//sentence = "Sorry for the delay, but I've finally updated PEP 389, the argparse PEP, based on all the feedback from python-dev.  ";
    	
        System.out.println("\nInput sentence z  : " + sentence);

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
    	
        
        System.out.println("\nInput sentenced  q : " + sentence);

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
