package callIELibraries;

/* For representing a sentence that is annotated with pos tags and np chunks.*/
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import readRepository.readRepository.ReadLabels;
import connections.MysqlConnect;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;

/* String -> ChunkedSentence */
import edu.washington.cs.knowitall.nlp.OpenNlpSentenceChunker;

/* The class that is responsible for extraction. */
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;

/* The class that is responsible for assigning a confidence score to an
 * extraction.
 */
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunction;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunctionException;
import edu.washington.cs.knowitall.extractor.conf.ReVerbOpenNlpConfFunction;

/* A class for holding a (arg1, rel, arg2) triple. */
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import miner.process.LabelTriples;
import relationExtraction.InsertIntoRelationTable;
import utilities.ReadFileLinesIntoArray;

public class ReVerbFindRelations extends JPanel {
	
	static InsertIntoRelationTable ir = new InsertIntoRelationTable();
	
	 // Prints out extractions from the sentence.
	static  ReVerbExtractor reverb =null;
	static  ConfidenceFunction confFunc = null;
	static String tableName= "labels";

	public ReVerbFindRelations(){
		
		try {
			reverb = new ReVerbExtractor();
			confFunc = new ReVerbOpenNlpConfFunction();
		} catch (ConfidenceFunctionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    //Just to output reverb triples
    //no matching
    public static void computeReverb(String Str) throws IOException{
    	
    	System.out.println("\n\nComputing ReVerb");
        //System.out.println("Paragraph: " + Str);    
    	
    	System.out.println("\nSentence: " + Str);
                   		
		Reader reader = new StringReader(Str);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		// System.out.println("method.." + v_message);
		
		Str = Str.replace("-LRB- ","");
	    Str = Str.replace("-RRB- ","");
		
		String sentStr;
		
		for (List<HasWord> eachSentence : dp) {			
			sentStr = Sentence.listToString(eachSentence);			
			OpenNlpSentenceChunker chunker = new OpenNlpSentenceChunker();
	        ChunkedSentence sent = chunker.chunkSentence(sentStr);
			
			//CurrentSentenceString = pm.removeUnwantedText(CurrentSentenceString);
			//System.out.println("\nSentence: " + eachSentence);  
			// Prints out the (token, tag, chunk-tag) for the sentence
	        //System.out.println(sentStr);
	        for (int i = 0; i < sent.getLength(); i++) {
	            String token = sent.getToken(i);
	            String posTag = sent.getPosTag(i);
	            String chunkTag = sent.getChunkTag(i);
	           // System.out.println(token + " " + posTag + " " + chunkTag);
	        }
	        System.out.println();

	        // Prints out extractions from the sentence.
	        ReVerbExtractor reverb = new ReVerbExtractor();
	        ConfidenceFunction confFunc = new ReVerbOpenNlpConfFunction();
	       
	        //for all extractions in the sentence
	        for (ChunkedBinaryExtraction extr : reverb.extract(sent)) {
	            double conf = confFunc.getConf(extr);
	            	System.out.println("Triples: " + extr.getArgument1().toString() + " , " + extr.getRelation().toString()+ " , " + extr.getArgument2().toString());
	            
	            	
	        }
	        System.out.println();			
			
		}
		
		
    	
    }
    
    //this is called from within this frame when implemented as tab in GUI.java
    //this is similar function to the one after this, as to see why there wwere not matching a sit should 
    // as above function returns triples
    
    //this inludes the label matchin as it is directly taken from textbox
    public static void checkSelectionContainsLabels(String sentStr ) throws IOException{
    	
    	MysqlConnect mc = new MysqlConnect();
    	Connection conn= mc.connect();
    	
    	ArrayList<LabelTriples> labels = new ArrayList<LabelTriples>();
    	//read base ideas to match in string matching
		ReadLabels b = new ReadLabels();
		String baseIdeasFilename = "C:/Users/psharma/Google Drive/PhDOtago/scripts/inputFiles/input-BaseIdeas.txt";
		labels = b.readLabelsFromFile(baseIdeasFilename,labels, "Keyword matching using these base concepts",tableName,conn, false);
    	
    	//initialise variables			
		Boolean matchFound = false;
		
		//initialise to null values
		String space = " ";
		String v_idea = null;
		String subject = null;
		String object = null;
		String verb = null;
		
		sentStr = sentStr.replace("-LRB- ","");
	    sentStr = sentStr.replace("-RRB- ","");
	    
	    System.out.println("\nSentence: "+ sentStr);
	    
		//for (String bi : baseIdeas) {
	    if(!labels.isEmpty()){
  			int counter =0;
  			for (int x=0; x <labels.size(); x++){
  				
  				
  				if(labels.get(x).getIdea() != null)
  				{
  					v_idea = labels.get(x).getIdea();
  					subject = labels.get(x).getSubject();
  					verb = labels.get(x).getVerb();		//no verb egual to pep
  					object = labels.get(x).getObject();
  				
  					
					//this used to checks previous sentyences for part of the triple 
					//the input file has a previous sentence for checking
					//but now will code it differenntly.when?
		  			/*
					if (!bi.contains(",")){									
						String words[] = bi.split(" ");
						//capture states - doubles
						if(words.length==3 ){
							v_idea =  words[0] ;
							subject =   words[1];
							verb =  words[2];
							//important - set the object to null
							object = "";
							//stateDouble=true;
						}
						//capture object as well - form triples
						else if(words.length==4){
							v_idea =  words[0] ;
							subject =   words[1];
							verb =  words[2];
							object =  words[3];
							//triple =true;
						}				
						//capture the second set of triple, if more columns are provided in the inputfile
						else if (words.length>5){
							v_idea =  words[0] ;
							subject =   words[1];
							verb =  words[2];
							object =  words[3];
							//more few lines here
						}
						*/
				//expand terms....e.g. where S,V or O has two terms, e.g. was_accepted  = was accepted
				subject = subjectContainsUnderScore(subject);
				verb = subjectContainsUnderScore(verb);
				object = subjectContainsUnderScore(object);	
				
				boolean i = false;
				if(subject.toLowerCase().equals("i")){
					i = true;
					subject = " " + subject + " ";
					//CurrentSentenceString = " " + CurrentSentenceString;
				}
				
				//split selected text into sentences
				Reader reader = new StringReader(sentStr);
				DocumentPreprocessor dp = new DocumentPreprocessor(reader);
				// System.out.println("method.." + v_message);
				
				sentStr = sentStr.replace("-LRB- ","");
				sentStr = sentStr.replace("-RRB- ","");
				
				//String sentStr;
				
				for (List<HasWord> eachSentence : dp) 
				{					
					sentStr = Sentence.listToString(eachSentence);		
				
					if ( (sentStr.toLowerCase().contains(subject) && sentStr.toLowerCase().contains(verb) && sentStr.toLowerCase().contains(object))) {
						//Sentence has all the 3 terms
						
						 System.out.println("\nFound inputList-labels terms in sentence : " + v_idea + " , " + subject + " , " + verb + " , " +object + " , ");					
						
						 OpenNlpSentenceChunker chunker = new OpenNlpSentenceChunker();
					     ChunkedSentence sent = chunker.chunkSentence(sentStr);
						
					  // Prints out extractions from the sentence.
					        ReVerbExtractor reverb = new ReVerbExtractor();
					        ConfidenceFunction confFunc = new ReVerbOpenNlpConfFunction();
					        
					        String ar1= null;
					        String rel = null;
					        String ar2=null;
					        
				//	        System.out.println("\n"+sentStr);
				//	        System.out.println("Initial retrieved: s: " + subject + " v: "  + verb + " o: " + object);
					        
					        //REVERB
					        
					        //for all extractions in the sentence
					        for (ChunkedBinaryExtraction extr : reverb.extract(sent)) {
					            double conf = confFunc.getConf(extr);
	//				            System.out.println("Arg1=" + extr.getArgument1());
	//				            System.out.println("Rel=" + extr.getRelation());
	//				            System.out.println("Arg2=" + extr.getArgument2());
	//				            System.out.println("Conf=" + conf);
					           
					           //System.out.println("The Triples obtained: " + v_pepNumber + " , "+ v_message_ID + " , " + author + " , " + sentStr+ " , " + extr.getArgument1().toString() + " , " + extr.getRelation().toString()+ " , " + extr.getArgument2().toString()); 
					          System.out.println("ReVerb Triples Found in sentence: a1: " + extr.getArgument1().toString() + " , rel: " + extr.getRelation().toString()+ " , a2: " + extr.getArgument2().toString());
					          
					           //DO MATCHING
					           //inputlist, remove spaces
					           subject = subject.replaceAll(" ","");
					           verb = verb.replaceAll(" ","");
					           object = object.replaceAll(" ","");           
					           
					           //reverb
					           String v_subject = extr.getArgument1().toString();
					           String v_relation = extr.getRelation().toString();
					           String v_object = extr.getArgument2().toString();
					           
					           //if extracted triples contains the triples from input file
//					           if ((v_subject.contains(subject.toLowerCase()) || v_subject.contains(object.toLowerCase())) 
//					        		   && (v_relation.contains(verb.toLowerCase()) ) 
//					            		&& (v_object.contains(subject.toLowerCase()) || v_object.contains(object.toLowerCase())))
//					            {
					           if ((v_subject.contains(subject.toLowerCase()) || v_subject.contains(object.toLowerCase())) || 
					            		(v_object.contains(subject.toLowerCase()) || v_object.contains(object.toLowerCase())))
					            {
					            	//ar1 = processWord(extr.getArgument1().toString());
					            	//rel = processWord(extr.getRelation().toString());
					            	//ar2 = processWord(extr.getArgument2().toString());
					            	 //System.out.println( ar1 + " , " + rel + " , " + ar2);
					           	   System.out.println("\tMatched Triples in both: subject- "+ subject + "-" + extr.getArgument1().toString() + " , verb-"+ verb  + extr.getRelation().toString()+ " , object-" + object + " , "  + extr.getArgument2().toString());
					        	  // System.out.println("\t" + v_pepNumber + " , "+ v_date+ " , " +  v_message_ID + " , " + author + " ," + v_idea + " , " + sentStr);
					                 //System.out.println("Conf= " + conf);
					                 //ir.insert( extr.getArgument1().toString() ,extr.getRelation().toString(), extr.getArgument2().toString());
					           }
					           
					        }//end for loop
						
					} //end if
				} //end for loop
			} //end if
			
		} //end base ideas for loop
	    }  //end if
    }//end method
    
	private static String subjectContainsUnderScore(String subject) {
		if (subject.contains("_")){
			String[] subjectInclude = subject.split("_");								
			String str1 = Arrays.toString(subjectInclude)
					.replace(",", "")  //remove the commas
				    .replace("[", "")  //remove the right bracket
				    .replace("]", "")  //remove the left bracket
				    .trim();           //remove trailing spaces from partially initialized arrays
		    //replace starting "[" and ending "]" and ","
		    //str1 = str1.substring(1, str1.length()-1).replaceAll(",", "");						
			//subjectOptions = subject.split("-");
			subject = str1;
		}
		return subject;
	}
    
    //This method is called from main - pepstorylinereverb kkvl
    //this one is old now, new one below
    public static boolean findRelations_OLD(String sentStr,String subject, String object, String verb, String v_idea, Integer v_pepNumber, Date v_date, Integer v_message_ID, 
    		String author, Integer sentenceCounter, FileWriter writer, FileWriter writer2 ) throws Exception {
    	
    	//subject = "bdfl";
    	//object = "pep";

    	//System.out.println("Inside ReVerb");
        //String sentStr = "Michael McGinn is the mayor of Seattle.";

        // Looks on the classpath for the default model files.
        OpenNlpSentenceChunker chunker = new OpenNlpSentenceChunker();
        ChunkedSentence sent = chunker.chunkSentence(sentStr);

        // Prints out the (token, tag, chunk-tag) for the sentence
        
 //XXX       System.out.println("Sentence: " + sentStr);
        
//        for (int i = 0; i < sent.getLength(); i++) {
//            String token = sent.getToken(i);
//            String posTag = sent.getPosTag(i);
//            String chunkTag = sent.getChunkTag(i);
//            System.out.println(token + " " + posTag + " " + chunkTag);
//        }

//        // Prints out extractions from the sentence.
//        ReVerbExtractor reverb = new ReVerbExtractor();
//        ConfidenceFunction confFunc = new ReVerbOpenNlpConfFunction();
        
        String ar1= null;
        String rel = null;
        String ar2=null;
        
//qq        System.out.println("\n"+sentStr);
//qq        System.out.println("Initial Label retrieved: s: " + subject + " v: "  + verb + " o: " + object);
        
        sentStr = sentStr.replace("-LRB- ","");
        sentStr = sentStr.replace("-RRB- ","");
        
        //DO MATCHING
        //inputlist, remove spaces
        subject = subject.replaceAll(" ","");
        verb = verb.replaceAll(" ","");
        object = object.replaceAll(" ",""); 
        
        String v_subject = null;
        String v_relation = null;
        String v_object = null;
        boolean found =false;
        
        //REVERB        
        //for all extractions in the sentence
        for (ChunkedBinaryExtraction extr : reverb.extract(sent)) {
            double conf = confFunc.getConf(extr);
//            System.out.println("Arg1=" + extr.getArgument1());
//            System.out.println("Rel=" + extr.getRelation());
//            System.out.println("Arg2=" + extr.getArgument2());
//            System.out.println("Conf=" + conf);
           
//qq           System.out.println("The Triples obtained: " + v_pepNumber + " , "+ v_message_ID + " , " + author + " , " + sentStr+ " , " + extr.getArgument1().toString() + " , " + extr.getRelation().toString()+ " , " + extr.getArgument2().toString()); 
//xx           System.out.println("ReVerb Triples: a1: " + extr.getArgument1().toString() + " , rel: " + extr.getRelation().toString()+ " , a2: " + extr.getArgument2().toString());
          
                    
           //reverb
           v_subject = extr.getArgument1().toString();
           v_relation = extr.getRelation().toString();
           v_object = extr.getArgument2().toString();
           
           //if extracted triples contains the triples from input file
           if ((v_subject.toLowerCase().contains(subject.toLowerCase()) || v_subject.toLowerCase().contains(object.toLowerCase())) 
        		   &&  (v_relation.contains(verb.toLowerCase()) )   //additional if clause
            		&& (v_object.toLowerCase().contains(subject.toLowerCase()) || v_object.toLowerCase().contains(object.toLowerCase())))
            {
        	   found = true;
        	   //ar1 = processWord(extr.getArgument1().toString());
            	//rel = processWord(extr.getRelation().toString());
            	//ar2 = processWord(extr.getArgument2().toString());
            	 //System.out.println( ar1 + " , " + rel + " , " + ar2);        	   
        	   
        	   //old version
        	   /*
        	   writer.write	 ("| ReVerb "+ v_idea );
        	   writer2.write("\n"+"ReVerb  Triples Matched, " + v_idea + " | " + subject + " | " + verb + " | " + object + " | " + v_subject + " | " + v_relation + " | " + v_object);
        	   */
        	   //new version
        	   System.out.println("\tReVerb Triples Matched: "+ v_pepNumber + " , "+ v_date+ " , " +  v_message_ID + " , " + author + " ," + v_idea + " , " + subject + " , " + verb + " , " + object + " , " + v_subject + " , " + v_relation + " , " + v_object+ " , " + sentStr);
        	   writer2.write("\n"+"ReVerb  Triples Matched, " + v_idea + " | " + subject + " | " + verb + " | " + object + " | " + v_subject + " | " + v_relation + " | " + v_object);
        	   //System.out.println("Triples Matched " + extr.getArgument1().toString() + " , " + extr.getRelation().toString()+ " , " + extr.getArgument2().toString());
        	     //System.out.println("Conf= " + conf);
                 //ir.insert( extr.getArgument1().toString() ,extr.getRelation().toString(), extr.getArgument2().toString());
           }
           
        }
        //in output for disco, we just want to show that it matched....
        if (found==true){
        	 writer.write	 ("| R: "+ v_idea );
      	     
      	}
        else {
       	 writer.write	 ("| R: " );     	     
     	}
        return found;
    }
    
    //NEW ONE
    public static String[][] findRelations (String sentStr,String subject, String object, String verb, String v_idea, Integer v_pepNumber, Date v_date, Integer v_message_ID, 
    		String author, Integer sentenceCounter, FileWriter writer, FileWriter writer2 ,
    		String conditionalStrings[], String [] reasonTerms, ArrayList<LabelTriples> reasonLabels, Integer depthChecked
    		) throws Exception {
    	
    	//subject = "bdfl";
    	//object = "pep";

    	//System.out.println("Inside ReVerb");
        //String sentStr = "Michael McGinn is the mayor of Seattle.";

        // Looks on the classpath for the default model files.
        OpenNlpSentenceChunker chunker = new OpenNlpSentenceChunker();
        ChunkedSentence sent = chunker.chunkSentence(sentStr);

        // Prints out the (token, tag, chunk-tag) for the sentence
        
 //XXX       System.out.println("Sentence: " + sentStr);
        
//        for (int i = 0; i < sent.getLength(); i++) {
//            String token = sent.getToken(i);
//            String posTag = sent.getPosTag(i);
//            String chunkTag = sent.getChunkTag(i);
//            System.out.println(token + " " + posTag + " " + chunkTag);
//        }

//        // Prints out extractions from the sentence.
//        ReVerbExtractor reverb = new ReVerbExtractor();
//        ConfidenceFunction confFunc = new ReVerbOpenNlpConfFunction();
        
        String ar1= null;
        String rel = null;
        String ar2=null;
        
//qq        System.out.println("\n"+sentStr);
//qq        System.out.println("Initial Label retrieved: s: " + subject + " v: "  + verb + " o: " + object);
        
        sentStr = sentStr.replace("-LRB- ","");
        sentStr = sentStr.replace("-RRB- ","");
        
        //DO MATCHING
        //inputlist, remove spaces
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
        
        //REVERB        
        //for all extractions in the sentence
        for (ChunkedBinaryExtraction extr : reverb.extract(sent)) {
            double conf = confFunc.getConf(extr);
//            System.out.println("Arg1=" + extr.getArgument1());
//            System.out.println("Rel=" + extr.getRelation());
//            System.out.println("Arg2=" + extr.getArgument2());
//            System.out.println("Conf=" + conf);
           
//qq           System.out.println("The Triples obtained: " + v_pepNumber + " , "+ v_message_ID + " , " + author + " , " + sentStr+ " , " + extr.getArgument1().toString() + " , " + extr.getRelation().toString()+ " , " + extr.getArgument2().toString()); 
//xx           System.out.println("ReVerb Triples: a1: " + extr.getArgument1().toString() + " , rel: " + extr.getRelation().toString()+ " , a2: " + extr.getArgument2().toString());
          
                    
           //reverb
           v_subject = extr.getArgument1().toString();
           v_relation = extr.getRelation().toString();
           v_object = extr.getArgument2().toString();
           
           
         //assign the extracted triples
			triple_array[y_counter][0] = v_subject;
			triple_array[y_counter][1] = v_relation;
			triple_array[y_counter][2] = v_object;
			
			//RETURN THE TRIPLE IN DOUBLKE ARRAY
			
			//increment y counter
			y_counter++;
			
            //CODE DELETED
           
        }
        //the following not needed
        /*
        if (found==false){			
		     System.out.println("\tReverb Triples Not Matched");
		     writer2.write("\n"+"Reverb Triples Not Matched");
		     writer.write("| R: ");			
        }
        */
        return triple_array;
    }
    
  //This method is called from main - pepstorylinereverb
    public static void findRelationsOnly(String sentStr) throws Exception {    	
    
        // Looks on the classpath for the default model files.
        OpenNlpSentenceChunker chunker = new OpenNlpSentenceChunker();
        ChunkedSentence sent = chunker.chunkSentence(sentStr);


        // Prints out extractions from the sentence.
        ReVerbExtractor reverb = new ReVerbExtractor();
        ConfidenceFunction confFunc = new ReVerbOpenNlpConfFunction();
        
        String ar1= null;
        String rel = null;
        String ar2=null;

        
        sentStr = sentStr.replace("-LRB- ","");
        sentStr = sentStr.replace("-RRB- ","");
   
        
        String v_subject = null;
        String v_relation = null;
        String v_object = null;
        
        //REVERB        
        //for all extractions in the sentence
        for (ChunkedBinaryExtraction extr : reverb.extract(sent)) {
            double conf = confFunc.getConf(extr);
           
//qq           System.out.println("The Triples obtained: " + v_pepNumber + " , "+ v_message_ID + " , " + author + " , " + sentStr+ " , " + extr.getArgument1().toString() + " , " + extr.getRelation().toString()+ " , " + extr.getArgument2().toString()); 
           System.out.println("ReVerb Triples: a1: " + extr.getArgument1().toString() + " , rel: " + extr.getRelation().toString()+ " , a2: " + extr.getArgument2().toString());    
                    
           //reverb
           v_subject = extr.getArgument1().toString();
           v_relation = extr.getRelation().toString();
           v_object = extr.getArgument2().toString();   
           
        }
    }
    
    public static void extractRelations(String sentStr, Integer v_pepNumber, Integer v_message_ID, String author, Date v_date, String v_tablename ) throws Exception {
    	
        // Looks on the classpath for the default model files.
        OpenNlpSentenceChunker chunker = new OpenNlpSentenceChunker();
        ChunkedSentence sent = chunker.chunkSentence(sentStr); 

        // Prints out extractions from the sentence.
        ReVerbExtractor reverb = new ReVerbExtractor();
        ConfidenceFunction confFunc = new ReVerbOpenNlpConfFunction();
        
        MysqlConnect mc = new MysqlConnect();
    	Connection conn= mc.connect();
        
        String ar1= null;
        String rel = null;
        String ar2=null;
        
        InsertIntoRelationTable ir = new InsertIntoRelationTable();
        
        for (ChunkedBinaryExtraction extr : reverb.extract(sent)) {
            double conf = confFunc.getConf(extr);
            ar1 = extr.getArgument1().toString();
            rel = extr.getRelation().toString();
            ar2 = extr.getArgument2().toString();
            	System.out.println("Reverb relations Found: " + v_pepNumber + " , " + v_message_ID  + " , "+ v_date + " , " + author + " , " + sentStr+ " , " + ar1 + " , " + rel + " , " + ar2);
                ir.insert(ar1, rel, ar2, sentStr, v_tablename, conn);
                 //System.out.println("Conf= " + conf);
                 //ir.insert( extr.getArgument1().toString() ,extr.getRelation().toString(), extr.getArgument2().toString());
  //          }
           
        }
    }
    
    private static String processWord(String x) {
        return x.replaceAll("[][(){},'*&^@.;!?<>%]", "");
    }
    
    void insertIntoDB_RelationsTable(String arg1, String relation, String arg2){
    	
    	
    }
    
}
