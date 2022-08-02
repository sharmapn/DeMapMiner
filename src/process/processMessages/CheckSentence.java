package Process.processMessages;

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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.mpii.clausie.ClausIE;
import de.mpii.clausie.Clause;
import de.mpii.clausie.Proposition;
import utilities.Console;
import utilities.ReadFileLinesIntoArray;


public class CheckSentence extends JPanel {

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
 //               System.out.println(clausIE.getDepTree().pennString().replaceAll("\n", "\n                   ").trim());
                System.out.print("Semantic graph   : ");
//                System.out.println(clausIE.getSemanticGraph().toFormattedString().replaceAll("\n", "\n                   ").trim());

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
                
            	CheckSentenceHasTripleTerms_SendToIELibraries_InsertIntoDB cesdl = new CheckSentenceHasTripleTerms_SendToIELibraries_InsertIntoDB();
            	Boolean foundLabel = false;
//            	foundLabel = cesdl.checkEachSentenceAgaistDifferentLibrariesForLabels(v_message,g, sentence, tripleLabels,  PreviousSentenceString, wordsFoundList,
//						v_pepNumber, v_date, v_message_ID,author,  paragraphCounter, sentenceCounter, prevSentence,previousParagraph, nextParagraph, rvr,jw, cie,cm, checkReverb,  dependency, sentencesRemoveDuplicates, 
//						fc, ds, reasonLabels, pd, writerAll, writerForDisco,conditionalList, negationTerms, reasonTerms, reasons, pms, questionPhrases,
//						readDummyFile, replaceCommas, checkClauseIETrue, checkOllieTrue, tripleProcessingResult, statusProcessingResult, v_statesToGetRoles, v_unwantedTerms, dontCheckLabels); //, snlptdg);							
//			
                
 
                
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
					array = rf.readFromFile("C:\\scripts\\inputLabels2.txt");
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
//        System.out.println(clausIE.getDepTree().pennString().replaceAll("\n", "\n                   ").trim());
        System.out.print("Semantic graph   : ");
//        System.out.println(clausIE.getSemanticGraph().toFormattedString().replaceAll("\n", "\n                   ").trim());

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
    
    
}
