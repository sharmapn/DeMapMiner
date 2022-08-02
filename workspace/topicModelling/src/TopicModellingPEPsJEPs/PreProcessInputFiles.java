package TopicModellingPEPsJEPs;

import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PreProcessInputFiles {
	//static String inputFile = "C:\\scripts\\TopicModelling\\PEPtopic-sentences.txt"; //"c:\\scripts\\ap.txt";
	//static String tempFile = "C:\\scripts\\TopicModelling\\~tmp.PEPtopic-sentences.txt";
	
	static File f = new File("C://scripts//TopicModelling//PEPtopic-sentences.txt");
    static File ftmp = new File("C://scripts//TopicModelling//~tmp.PEPtopic-sentences.txt");
    
	public static void main(String[] args) throws Exception {
    	//read file line by line and remove all links, all terms with dot
    	//File f = new File(inputFile);
	    //File ftmp = new File(tempFile, ".txt");
	    try
	    {
	    	String sentenceTermsForCheckingDots[];
	    	String CurrentSentenceString= "An HTML attachment was scrubbed... URL: <http://mail.python.org/pipermail/python-ideas/attachments/20171110/c679c3df/attachment.html>";
	    	sentenceTermsForCheckingDots = CurrentSentenceString.split(" ");
        	for(String s : sentenceTermsForCheckingDots) {
        		if (s.length() > 2 && s.contains(".")) {	//end of sentence would contain a dot and we dont want to replace that
        			CurrentSentenceString = CurrentSentenceString.replace(s, " ");
        		}
        	}
	    	System.out.println(CurrentSentenceString);
	    	/*
	        BufferedReader br = new BufferedReader(new FileReader(f));
	        BufferedWriter bw = new BufferedWriter(new FileWriter(ftmp));
	        String ln;
	        String[] temp;
	        while((ln = br.readLine()) != null)
	        {
	        	temp = ln.split(" ");
	        	for(String s : temp) {
	        		if (s.contains("\\.")) {
	        			ln.replace(s, " ");
	        		}
	        	}
	        	ln = ln.replaceAll("\\s+", " ").trim();	//remove double spaces and trim
	            //bw.write(ln.replace("phase", pstr).replace("db", dstr).replace("IP", ipstr));
	            bw.newLine();
	        }
	        br.close();
	        bw.close();
	        Files.move(ftmp.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
	        */
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	}
}
