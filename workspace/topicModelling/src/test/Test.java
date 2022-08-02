package test;

import cc.mallet.fst.SimpleTagger;
//In this class we traina  simple cause effect for OSSD repositories based on Anand Krishnan MSc thesis CRF training file structure
public class Test {
	
	/*
	When you start training, the first message that SimpleTagger gives you is:
	Number of features in training data: x
	Number of predicates: y
	The number of predicates, y, is the number of distinct tokens (or lines) that your training data contains.
	When you label a file using the model from the previous train (that had y predicates), you get a message:
	Number of predicates: z
	This z, is the sum of y and the number of distinct tokens (or lines) that the file you want to label contains. That is why z is always greater (or equal) than y. 
	If for example you try to label an empty of content text file with a model that had y predicates, you will get a number of predicates y, which is y + 0 = y, cause your empty file had 0 labels.
	*/
	
	static String trainFile=  "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\workspaces\\fifth\\WikiInfoboxExtractor\\sample";	//exactly same as the one in google drive, but modified as well		
	static String testFile	= "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\workspaces\\fifth\\WikiInfoboxExtractor\\stest";	//exactly same as the one in google drive, but modified as well		
	static String modelFile	= "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\workspaces\\fifth\\WikiInfoboxExtractor\\nouncrf";
	//another projects sample data
	static String trainFile2 =  "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\workspaces\\fifth\\mallet\\sample-data\\ner.txt";
	static String trainFile3 =  "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\workspaces\\fifth\\mallet\\sample-data\\schunk.txt";	
	//reason data
	static String reasonTrainFile =  "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\workspaces\\fifth\\Causality-Extraction\\turk\\genReasonCRFTest.txt";
	static String reasonTestFile =  "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\workspaces\\fifth\\Causality-Extraction\\turk\\.txt";
	
	//dummy reason training data
	static String reasonTrainingSampleFile = "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\workspaces\\fifth\\mallet\\reasonsSampleData\\reasonsSample.txt";
	static String reasonTestSampleFile 	   = "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\workspaces\\fifth\\mallet\\reasonsSampleData\\reasonsTest.txt";
	
	public static void main(String[] args) {
		//we try examples from http://mallet.cs.umass.edu/sequences.php
		//--train true --model-file nouncrf  sample
		//and then test the model with
		//--model-file nouncrf  stest
		
		
		//--train true --test lab --threads 2 ner.txt
		//--train true --test lab --threads 2 schunk.txt
		
		try {
			String[] args2 = {"--train","true","--model-file",modelFile,trainFile};		//train
			//SimpleTagger.main(args2);
			String[] args3 = {"--model-file",modelFile,testFile};
			//SimpleTagger.main(args3);
			
			String[] args4 = {"--train","true","--test","lab",modelFile,trainFile2};
			//SimpleTagger.main(args4);
			
			//Reason 
			//sample training
			String[] argsReason = {"--train","true","--model-file",modelFile,reasonTrainingSampleFile};
//$$			SimpleTagger.main(argsReason);
			String[] argsReasonTest = {"--model-file",modelFile,reasonTestSampleFile};
			SimpleTagger.main(argsReasonTest);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
