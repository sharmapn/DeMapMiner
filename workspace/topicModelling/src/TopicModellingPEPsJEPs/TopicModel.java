package TopicModellingPEPsJEPs;

import cc.mallet.util.*;
import connections.MysqlConnect;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.regex.*;

//import Process.processMessages.ProcessingRequiredParameters;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TopicModel {
	//static String inputFile = "C:\\scripts\\TopicModelling\\PEPtopic-sentences.txt"; //"c:\\scripts\\ap.txt";
	static String inputFile = "C:\\DeMap_Miner\\datafiles\\outputFiles\\TMOutFile.txt"; //"c:\\scripts\\ap.txt";
	static Integer numTopics = 25; //50; //100
	static Integer numTermsPerTopic = 15;	//70	//there would be very little occurence of all terms we are looking for
	
	static Statement stmt;
	static Connection conn;
	
	//static ProcessingRequiredParameters prp = new ProcessingRequiredParameters();
	
	public static void main(String[] args) throws Exception {
		connections.MysqlConnect mc = new MysqlConnect();		conn = mc.connect();	
		stmt = conn.createStatement();
		//We want to store and update each term frequency
		Statement sMainParent0=conn.createStatement();
		int update;
		String sqlReflect = "delete from topicmodel ;" ;
 		//email similarity, modify to check as long as 'senderEmailFirstSegment' is as long as 2 characters long					
		update = sMainParent0.executeUpdate(sqlReflect);
//        				if(sum>1)	//only want to see which messages are assigned under multiple pepss 
//        					System.out.println("Updated "+sum+" rows for messageid " + messageid);
		sMainParent0.close();
		
        // Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
        //pipeList.add( new TokenSequenceRemoveStopPattern(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
        //Jan 2019, we remove code terms
        
        pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/codeTerms.txt"), "UTF-8", false, false, false) );
        //remove firstname an d last names
        pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/fname.txt"), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/lname.txt"), "UTF-8", false, false, false) );
        //som,e terms that coudl be overlap, but we have to reduce so may miss some data
        pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/overlap.txt"), "UTF-8", false, false, false) );
        //additional stopwords
        pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/additionalstopwords.txt"), "UTF-8", false, false, false) );
        
        pipeList.add( new TokenSequence2FeatureSequence() );

        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

        Reader fileReader = new InputStreamReader(new FileInputStream(inputFile), "UTF-8");
        instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),3, 2, 1)); // data, label, name fields

        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while the second is the parameter for a single dimension of the Dirichlet prior.
        	//100
        ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

        model.addInstances(instances);

        // Use two parallel samplers, which each look at one half the corpus and combine statistics after every iteration.
        model.setNumThreads(2);

        // Run the model for 50 iterations and stop (this is for testing only, for real applications, use 1000 to 2000 iterations)
        model.setNumIterations(50);
        model.estimate();

        // Show the words and topics in the first instance

        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instances.getDataAlphabet();
        
        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;
        
        Formatter out = new Formatter(new StringBuilder(), Locale.US);
        for (int position = 0; position < tokens.getLength(); position++) {
            out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
        }
        System.out.println(out);
        
        // Estimate the topic distribution of the first instance, 
        //  given the current Gibbs state.
        double[] topicDistribution = model.getTopicProbabilities(0);

        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
        
        // Show top 5 words in topics with proportions for the first document
        for (int topic = 0; topic < numTopics; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
            
            out = new Formatter(new StringBuilder(), Locale.US);
            out.format("%d,\t%.3f,\t", topic, topicDistribution[topic]);
            int rank = 0;
            while (iterator.hasNext() && rank < numTermsPerTopic) {		
                IDSorter idCountPair = iterator.next();
                out.format("%s (%.0f), ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
                rank++;
                //feb 2019 .. insert into database table
                //term, numtopicscommon, count
                //for each topic we check if term is already in table, if so we increment, 
                //else we insert, topicmodel, term, topicscommon, totalcount
//                System.out.println(" term : " + dataAlphabet.lookupObject(idCountPair.getID())  );
                String sql = "SELECT * from topicmodel where term =  '"+dataAlphabet.lookupObject(idCountPair.getID())+"';" ; //, datetimestamp asc";
    			// results table or postprocessed table																				 
    			ResultSet rs = stmt.executeQuery(sql);  //date asc
    			int totalRows = mc.returnRSCount(sql, conn), processedCounter=0;	
    			//System.out.println("Term: " + " Number: " +idCountPair.getID());
    			Integer topicscommon=0, totalcount=0;
    			String term="";
    			try
    			{
	    			if (totalRows>0){
	    				//System.out.println("Proposal : " + proposal + " causeSentence: " + causeSentence);
	        			if (rs.next()) {// && totalRows < 1000) {		//dont process those peps which have too many messages
	        				//recordSetCounter++;	
	        				term = rs.getString("term"); topicscommon = rs.getInt("topicscommon"); 	totalcount = rs.getInt("totalcount");
	        			
	        				//add
	        				topicscommon = topicscommon + 1;
	        				totalcount = (int) (totalcount + idCountPair.getWeight());
	        				
	        				Statement sMainParent=conn.createStatement();
	        				int update2;
	        				String sqlReflect2 = "update topicmodel set topicscommon = "+ topicscommon +",totalcount = "+totalcount+"  where term = '" + dataAlphabet.lookupObject(idCountPair.getID()) +"' ;" ;
	        		 		//email similarity, modify to check as long as 'senderEmailFirstSegment' is as long as 2 characters long					
	        				update2 = sMainParent.executeUpdate(sqlReflect2);
	//        				if(sum>1)	//only want to see which messages are assigned under multiple pepss 
	//        					System.out.println("Updated "+sum+" rows for messageid " + messageid);
	        				sMainParent.close();
	        			}	
	    			}
	    			else { //if term not stored previously, we insert
	    				Statement sMainParent=conn.createStatement();
	    				int update3;
	    				String sqlReflect3 = "insert into topicmodel (term, topicscommon, totalcount) values ('"+dataAlphabet.lookupObject(idCountPair.getID())+"',1,"+idCountPair.getWeight()+");" ;
	    		 		//email similarity, modify to check as long as 'senderEmailFirstSegment' is as long as 2 characters long					
	    				update3 = sMainParent.executeUpdate(sqlReflect3);
	    				
	    			}
    			}
    			catch (Exception e)	{
    				System.err.println("Got an exception! ");			System.err.println(e.getMessage());
    				System.out.println(StackTraceToString(e)  );	
    			}
                
            }
            System.out.println(out);
        }
        
        // Create a new instance with high probability of topic 0
        StringBuilder topicZeroText = new StringBuilder();
        Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

        int rank = 0;
        while (iterator.hasNext() && rank < 5) {
            IDSorter idCountPair = iterator.next();
            topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
            rank++;
        }

        // Create a new instance named "test instance" with empty target and source fields.
        InstanceList testing = new InstanceList(instances.getPipe());
        testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

        TopicInferencer inferencer = model.getInferencer();
        double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
        System.out.println("0\t" + testProbabilities[0]);
    }
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}

}