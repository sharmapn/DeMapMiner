package callIELibraries;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import connections.MysqlConnect;
import edu.knowitall.ollie.Ollie;
import edu.knowitall.ollie.OllieExtraction;
import edu.knowitall.ollie.OllieExtractionInstance;
import edu.knowitall.tool.parse.MaltParser;
import edu.knowitall.tool.parse.graph.DependencyGraph;
import miner.process.LabelTriples;
import relationExtraction.InsertIntoRelationTable;

/** This is an example class that shows one way of using Ollie from Java. */
public class JavaOllieWrapperGUIInDev {
    // the extractor itself
    private static Ollie ollie;

    // the parser--a step required before the extractor
    private static MaltParser maltParser;

    // the path of the malt parser model file
    private static final String MALT_PARSER_FILENAME = "C:\\lib\\engmalt.linear-1.7.mco";
    
    static InsertIntoRelationTable ir = new InsertIntoRelationTable();
    
	 static MysqlConnect mc = new MysqlConnect();
	 static Connection conn = mc.connect();

    public JavaOllieWrapperGUIInDev()  {
        // initialize MaltParser
        scala.Option<File> nullOption = scala.Option.apply(null);
        //maltParser = new MaltParser(new File(MALT_PARSER_FILENAME).toURI().toURL(), nullOption);
        maltParser = new MaltParser(new File("C:\\lib\\engmalt.linear-1.7.mco"));
        
        
        
        // initialize Ollie
        ollie = new Ollie();
    }

    /**
     * Gets Ollie extractions from a single sentence.
     * @param sentence
     * @return the set of ollie extractions
     */
    public static Iterable<OllieExtractionInstance> extract(String sentence) {
        // parse the sentence
        DependencyGraph graph = maltParser.dependencyGraph(sentence);

        // run Ollie over the sentence and convert to a Java collection
        Iterable<OllieExtractionInstance> extrs = scala.collection.JavaConversions.asJavaIterable(ollie.extract(graph));
        return extrs;
    }
    
    //THIS ONE IS USED kkvl
    //now old one, new one below
    public static boolean computOllie_OLD(String sentence, String subject, String object, String verb, String v_idea, Integer v_pepNumber, Date v_date, Integer v_message_ID, 
    		String author, Integer sentenceCounter, FileWriter writer,FileWriter writer2) throws IOException {
 //xx       System.out.println(JavaOllieWrapperGUIInDev.class.getResource("/logback.xml"));
        // initialize
        //JavaOllieWrapperGUI ollieWrapper = new JavaOllieWrapperGUI();
        //System.out.println("Ollie Output: THIS ONE IS USED");
        // extract from a single sentence.
        //String sentence = "President Obama will meet with Congressional leaders on Friday, and House Republicans summoned lawmakers back for a Sunday session, in a last-ditch effort to avert a fiscal crisis brought on by automatic tax increases and spending cuts scheduled to hit next week.";
        
        //String sentence = "I made a conscious decision to do that, and I'm a bit alarmed at this decision being overridden at the last moment with no debate.";
        
        Iterable<OllieExtractionInstance> extrs = extract(sentence);
        
        subject = subject.replaceAll(" ","");
        verb = verb.replaceAll(" ","");
        object = object.replaceAll(" ",""); 
        
        String v_subject = null;
        String v_relation = null;
        String v_object = null;
        boolean found =false;
        
        // print the extractions.
        for (OllieExtractionInstance inst : extrs) {
            OllieExtraction extr = inst.extr();
//xx            System.out.println("Arg1: "+extr.arg1().text()+"\t Rel: "+extr.rel().text()+"\t Arg2: "+extr.arg2().text());
            
            v_subject = extr.arg1().text();
            v_relation = extr.rel().text();
            v_object = extr.arg2().text();
            
            //if extracted triples contains the triples from input file
            if ((v_subject.toLowerCase().contains(subject.toLowerCase()) || v_subject.toLowerCase().contains(object.toLowerCase())) 
            		&& (v_relation.toLowerCase().contains(verb.toLowerCase()) )   //additional if clause
             		&& (v_object.toLowerCase().contains(subject.toLowerCase()) || v_object.toLowerCase().contains(object.toLowerCase())))
             {
            	found = true;
             	//ar1 = processWord(extr.getArgument1().toString());
             	//rel = processWord(extr.getRelation().toString());
             	//ar2 = processWord(extr.getArgument2().toString());
             	 //System.out.println( ar1 + " , " + rel + " , " + ar2);
         	   
               /* old version
               System.out.println("\tOllie Triples Matched " + extr.arg1().text() + " , " +  extr.rel().text() + " , " + extr.arg2().text().toString());
         	   System.out.println("\t"+v_pepNumber + " , "+ v_date+ " , " +  v_message_ID + " , " + author + " ," + v_idea + " , " + subject + " , " + verb + " , " + object + " , " + v_subject + " , " + v_relation + " , " + v_object+ " , " + sentence);
         	   writer.write("| Ollie " + v_idea);
         	   writer2.write("\n"+ "  Ollie Triples Matched, " + v_idea + " | "+ subject + " | " + verb + " | " + object + " | " + v_subject + " | " + v_relation + " | " + v_object);
         	   */
            	//new version
 //xx              System.out.println("\tOllie Triples Matched " + extr.arg1().text() + " , " +  extr.rel().text() + " , " + extr.arg2().text().toString());
 //xx          	   System.out.println("\t"+v_pepNumber + " , "+ v_date+ " , " +  v_message_ID + " , " + author + " ," + v_idea + " , " + subject + " , " + verb + " , " + object + " , " + v_subject + " , " + v_relation + " , " + v_object+ " , " + sentence);
 //xx          	   writer2.write("\n"+ "  Ollie Triples Matched, " + v_idea + " | "+ subject + " | " + verb + " | " + object + " | " + v_subject + " | " + v_relation + " | " + v_object);
         	   //System.out.println("Conf= " + conf);
                  //ir.insert( extr.getArgument1().toString() ,extr.getRelation().toString(), extr.getArgument2().toString());
            }
            
            
        }
        //in output for disco, we just want to show that it matched....
        if (found==true){
        	 writer.write("| O: " + v_idea);       	     
        }
        else{
        	writer.write("| O: ");  
        }
        return found;
    }
    
  //THIS ONE IS USED kkvl
    public static String[][] computOllie(String sentence, String subject, String object, String verb, String v_idea, Integer v_pepNumber, Date v_date, Integer v_message_ID, 
    		String author, Integer sentenceCounter, FileWriter writer,FileWriter writer2,
    		String conditionalStrings[], String [] reasonTerms, ArrayList<LabelTriples>  reasonLabels, Integer depthChecked
    		) throws IOException {
 //xx       System.out.println(JavaOllieWrapperGUIInDev.class.getResource("/logback.xml"));
        // initialize
        //JavaOllieWrapperGUI ollieWrapper = new JavaOllieWrapperGUI();
        //System.out.println("Ollie Output: THIS ONE IS USED");
        // extract from a single sentence.
        //String sentence = "President Obama will meet with Congressional leaders on Friday, and House Republicans summoned lawmakers back for a Sunday session, in a last-ditch effort to avert a fiscal crisis brought on by automatic tax increases and spending cuts scheduled to hit next week.";
        
        //String sentence = "I made a conscious decision to do that, and I'm a bit alarmed at this decision being overridden at the last moment with no debate.";
        
        Iterable<OllieExtractionInstance> extrs = extract(sentence);
        
        subject = subject.replaceAll(" ","");
        verb = verb.replaceAll(" ","");
        object = object.replaceAll(" ",""); 
        
        String v_subject = null;
        String v_relation = null;
        String v_object = null;
        boolean found =false;
        
        String [][] triple_array = new String [100][3];
		
		Integer y_counter= 0;
		Integer x_counter=0;
		
		//in the above declaration y max is set to 100
		// and x max is set to 3
        
        // print the extractions.
        for (OllieExtractionInstance inst : extrs) {
            OllieExtraction extr = inst.extr();
 //xx           System.out.println("Arg1: "+extr.arg1().text()+"\t Rel: "+extr.rel().text()+"\t Arg2: "+extr.arg2().text());
            
            v_subject = extr.arg1().text();
            v_relation = extr.rel().text();
            v_object = extr.arg2().text();
            
            //First match the | terms
			
			//assign the extracted triples
			triple_array[y_counter][0] = v_subject;
			triple_array[y_counter][1] = v_relation;
			triple_array[y_counter][2] = v_object;
			
			//RETURN THE TRIPLE IN DOUBLKE ARRAY
			
			//increment y counter
			y_counter++;
            
            
        }
        //if no triple matched
        //not needed now
        /*
  		if (found==false){			
  			     System.out.println("\tClausIE Triples Not Matched");
  			     writer2.write("\n"+"ClausIE Triples Not Matched");
  			     writer.write("| C: ");			
  		}
  		*/
        return triple_array;
    }


    //just show the relations extracted
    //used for finding relations whgich can be later coded
    public static void computOllieOnly(String sentence) throws IOException {
//        System.out.println(JavaOllieWrapperGUIInDev.class.getResource("/logback.xml"));
        // initialize
        //JavaOllieWrapperGUI ollieWrapper = new JavaOllieWrapperGUI();
        //System.out.println("Ollie Output: THIS ONE IS USED");
        // extract from a single sentence.
        //String sentence = "President Obama will meet with Congressional leaders on Friday, and House Republicans summoned lawmakers back for a Sunday session, in a last-ditch effort to avert a fiscal crisis brought on by automatic tax increases and spending cuts scheduled to hit next week.";
        
        //String sentence = "I made a conscious decision to do that, and I'm a bit alarmed at this decision being overridden at the last moment with no debate.";
        
        Iterable<OllieExtractionInstance> extrs = extract(sentence);
        
        String v_subject = null;
        String v_relation = null;
        String v_object = null;
        
        // print the extractions.
        for (OllieExtractionInstance inst : extrs) {
            OllieExtraction extr = inst.extr();
 //xx           System.out.println("Arg1: "+extr.arg1().text()+"\t Rel: "+extr.rel().text()+"\t Arg2: "+extr.arg2().text());
            
            v_subject = extr.arg1().text();
            v_relation = extr.rel().text();
            v_object = extr.arg2().text();
            
            //if extracted triples contains the triples from input file
//            if ((v_subject.toLowerCase().contains(subject.toLowerCase()) || v_subject.toLowerCase().contains(object.toLowerCase())) &&
//             		(v_object.toLowerCase().contains(subject.toLowerCase()) || v_object.toLowerCase().contains(object.toLowerCase())))
//             {
//             	//ar1 = processWord(extr.getArgument1().toString());
//             	//rel = processWord(extr.getRelation().toString());
//             	//ar2 = processWord(extr.getArgument2().toString());
//             	 //System.out.println( ar1 + " , " + rel + " , " + ar2);
//         	   System.out.println("\tOllie Triples Matched " + extr.arg1().text() + " , " +  extr.rel().text() + " , " + extr.arg2().text().toString());
//         	   System.out.println("\t"+v_pepNumber + " , "+ v_date+ " , " +  v_message_ID + " , " + author + " ," + v_idea + " , " + subject + " , " + verb + " , " + object + " , " + v_subject + " , " + v_relation + " , " + v_object+ " , " + sentence);
//         	   writer.write(", Ollie " + v_idea);
//         	   writer2.write("\n"+ "  Ollie Triples Matched, " + v_idea + " , "+ subject + " , " + verb + " , " + object + " , " + v_subject + " , " + v_relation + " , " + v_object);
//         	   //System.out.println("Conf= " + conf);
//                  //ir.insert( extr.getArgument1().toString() ,extr.getRelation().toString(), extr.getArgument2().toString());
//            }
            
            
        }
    }
    
    
    //just show the relations extracted
    //used for finding relations whgich can be later coded
    public static void extractOllieRelationsw(String sentStr, Integer v_pepNumber, Integer v_message_ID, String author, Date v_date, String v_tablename, Connection conn) throws IOException {
       // System.out.println(JavaOllieWrapperGUIInDev.class.getResource("/logback.xml"));
        // initialize
        //JavaOllieWrapperGUI ollieWrapper = new JavaOllieWrapperGUI();
        System.out.println("Ollie Output: THIS ONE IS USED");
        // extract from a single sentence.
        //String sentence = "President Obama will meet with Congressional leaders on Friday, and House Republicans summoned lawmakers back for a Sunday session, in a last-ditch effort to avert a fiscal crisis brought on by automatic tax increases and spending cuts scheduled to hit next week.";
        
        //String sentence = "I made a conscious decision to do that, and I'm a bit alarmed at this decision being overridden at the last moment with no debate.";
        
        Iterable<OllieExtractionInstance> extrs = extract(sentStr);
        
        String v_subject = null;
        String v_relation = null;
        String v_object = null;
        
        // print the extractions.
        for (OllieExtractionInstance inst : extrs) {
            OllieExtraction extr = inst.extr();
 //xx           System.out.println("Ollie: Arg1: "+extr.arg1().text()+"\t Rel: "+extr.rel().text()+"\t Arg2: "+extr.arg2().text());
            
            v_subject = extr.arg1().text();
            v_relation = extr.rel().text();
            v_object = extr.arg2().text();
            

            ir.insert(v_subject, v_relation, v_object, sentStr, v_tablename, conn);
            
        }
    }
    
    
    public static void main(String args[]) throws MalformedURLException {
    	
    	JFrame aWindow = new JFrame("Ollie");
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
        
     
       // ClausIEMain cie = new ClausIEMain();
        
       // ReVerbFindRelationsGUI rv = new ReVerbFindRelationsGUI();
        
        Console.redirectOutput( output );
        
        btn1.addActionListener(new ActionListener() {
        	 
            public void actionPerformed(ActionEvent e)
            {
            	System.out.println(JavaOllieWrapperGUIInDev.class.getResource("/logback.xml"));
                // initialize
            	//JavaOllieWrapperGUIInDev ollieWrapper = new JavaOllieWrapperGUIInDev();
                System.out.println("Ollie Output");
                // extract from a single sentence.
                //String sentence = "President Obama will meet with Congressional leaders on Friday, and House Republicans summoned lawmakers back for a Sunday session, in a last-ditch effort to avert a fiscal crisis brought on by automatic tax increases and spending cuts scheduled to hit next week.";
                
               // String sentence = "I made a conscious decision to do that, and I'm a bit alarmed at this decision being overridden at the last moment with no debate.";
                String sentence = tt.getText();
                
                Iterable<OllieExtractionInstance> extrs = extract(sentence);
                

                // print the extractions.
                for (OllieExtractionInstance inst : extrs) {
                    OllieExtraction extr = inst.extr();
                    System.out.println(extr.arg1().text()+"\t"+extr.rel().text()+"\t"+extr.arg2().text());
                }
                
//                try {
//					//rv.computeReverb(sentence);
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
                System.out.println("Ollie Output");
               // cie.compute(sentence);
                System.out.println("Finished");
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
}
