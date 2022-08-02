package callIELibraries;

import de.linguatools.disco.CorruptConfigFileException;
import de.linguatools.disco.DISCO;
import de.linguatools.disco.ReturnDataBN;
import de.linguatools.disco.ReturnDataCol;
import de.linguatools.disco.WrongWordspaceTypeException;
import de.linguatools.disco.DISCO.SimilarityMeasure;
import de.linguatools.disco.TextSimilarity;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;

/*******************************************************************************
 *   Copyright (C) 2007, 2008, 2009, 2010, 2011, 2012, 2015 Peter Kolb
 *   peter.kolb@linguatools.org
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *   use this file except in compliance with the License. You may obtain a copy
 *   of the License at 
 *   
 *        http://www.apache.org/licenses/LICENSE-2.0 
 *
 *   Unless required by applicable law or agreed to in writing, software 
 *   distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *   WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 *   License for the specific language governing permissions and limitations
 *   under the License.
 *
 ******************************************************************************/

/**
 * sample code to show the use of the DISCO Java API version 2.0.
 * @author peter
 * @version 2.0
 */
public class UseDISCOSentenceSim {
    
    /************************************************************************
     * Call with: java UseDISCO WORDSPACE-DIR WORD
     * Make sure that disco-2.0.jar is in the classpath.
     * The word space must be of type DISCO.WordspaceType.SIM.
     * Set the JVM heap size to be larger than the word space that
     * will be loaded into RAM, otherwise an OutOfMemoryError will be thrown!
     * @param args
     * @throws java.io.IOException
     */
	
	DISCO disco;
	
	TextSimilarity ts;
	
    public static void main(String[] args) throws IOException{

        // first command line argument is path to the DISCO word space directory
        //String discoDir = args[0];
        String discoDir = "C:/Users/psharma/semantic/enwiki-20130403-sim-lemma-mwl-lc/"; 
        // second argument is the input word
        //String word = args[1];
        String word = "accept";

	/****************************************
	 * create instance of class DISCO.      *
	 * Do NOT load the word space into RAM. *
	 ****************************************/
        DISCO disco;
        try {
            disco = new DISCO(discoDir, false);
        } catch (FileNotFoundException | CorruptConfigFileException ex) {
            System.out.println("Error creating DISCO instance: "+ex);
            return;
        }

        // is the word space of type "sim"?
        if( disco.wordspaceType != DISCO.WordspaceType.SIM ){
           System.out.println("The word space "+discoDir+" is not of type SIM!");
           return; 
        }
        
        //START SENTENCE SIMILARITY
        
        String text1 = "BDFL has accepting the Pep";
        String text2 = "Pep has been accepted now by myself.";
        String similarityMeasure = "COSINE";
                
        //wordSim(String w1, String w2, DISCO disco, SimilarityMeasure similarityMeasure);
       
        long startTime = System.currentTimeMillis();
        
        TextSimilarity ts = new TextSimilarity();
        try {	
        	
        	System.out.println("Computing text similarity between \n"+text1+ " and \n" + text2);
			 System.out.println("TS "+ts.textSimilarity(text1, text2, disco, DISCO.SimilarityMeasure.COSINE));
		} catch (CorruptConfigFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //END SENTENCE SIMILARITY        
    
        // compute second order similarity between the input word and its most
        // similar words
        
	long endTime = System.currentTimeMillis();
	long elapsedTime = endTime - startTime;
	System.out.println("OK. Computation took "+elapsedTime+" ms.");
		
    }
    
    public UseDISCOSentenceSim() throws CorruptIndexException, IOException{
    	String discoDir = "C:/Users/psharma/semantic/enwiki-20130403-sim-lemma-mwl-lc/"; 
        
        try {
            disco = new DISCO(discoDir, false);
            System.out.println("DISCO instance created successfully: ");
            ts = new TextSimilarity();
        } catch (FileNotFoundException | CorruptConfigFileException ex) {
            System.out.println("Error creating DISCO instance: "+ex);
            return;
        }

        // is the word space of type "sim"?
        if( disco.wordspaceType != DISCO.WordspaceType.SIM ){
           System.out.println("The word space "+discoDir+" is not of type SIM!");
           return; 
        }
        
    }
    
    //This one is used for DISCOSIM
    public double matchSentences(String sentence1, String sentence2) throws CorruptIndexException, IOException{    	
    	//System.out.println("\nINSIDE DISCO SIM ");
        //START SENTENCE SIMILARITY  
        String similarityMeasure = "COSINE";                
        //wordSim(String w1, String w2, DISCO disco, SimilarityMeasure similarityMeasure);       
        
        
        try {        	
     //   	System.out.println("Computing text similarity between \n"+sentence1+ " and \n" + sentence2);
	//		 System.out.println("DISCO TS "+ts.textSimilarity(sentence1, sentence2, disco, DISCO.SimilarityMeasure.COSINE));
			 return ts.textSimilarity(sentence1, sentence2, disco, DISCO.SimilarityMeasure.COSINE);
		} catch (CorruptConfigFileException e) {
			e.printStackTrace();
			return 0;
		} 
    }
}
