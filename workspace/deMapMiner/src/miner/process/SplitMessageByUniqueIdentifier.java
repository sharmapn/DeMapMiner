package miner.process;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.apache.commons.lang.StringUtils;

public class SplitMessageByUniqueIdentifier {
	
    public static void main(String[] args) throws FileNotFoundException {
    	Integer pep = 209, mid=3898908;
        String text=readFile();        
        String pepTextMatched = splitMessageByUniqueIdentifierReturnTextAfterIdentifier(pep,mid, text);
        System.out.println("\nPEP Text Matched\n" + pepTextMatched);
    }
    public static String readFile() throws FileNotFoundException{
    	String alltext = "";
    	File text = new File("C:/test/test.txt");       

        //Creating Scanner instnace to read File in Java
        Scanner scnr = new Scanner(text);    

        //Reading each line of file using Scanner class
        int lineNumber = 1;
        while(scnr.hasNextLine()){
            String line = scnr.nextLine();
            alltext = alltext+line + "\n";
            //System.out.println("line " + lineNumber + " :" + line);
            lineNumber++;
        } 
        return alltext;
    }
	public static String splitMessageByUniqueIdentifierReturnTextAfterIdentifier(Integer currentIdentifierNumber,Integer mid,String v_Message){	
		v_Message= v_Message.toLowerCase();
		String pepNumber = currentIdentifierNumber.toString(); //"267";		
		//added		
		String text = "",part1,part2,extractedTextForcurrentIdentifierNumber="",CurrentSentenceString="";				
		List<String> v_searchKeyList = new ArrayList<String>();
		List<String> v_ignoreList = new ArrayList<String>();
		Integer i = null;
		String regex = "\\d+";
		
		//v_searchKeyList.clear();
		//System.out.println("\t##splitting message");
		
		
		v_searchKeyList.add("pep" + ": " + regex);		v_searchKeyList.add("pep" + " : " + regex);		v_searchKeyList.add("pep" + regex);
		v_searchKeyList.add("pep" + regex + " ");		v_searchKeyList.add("pep " + regex + " ");		v_searchKeyList.add(" pep" + regex + " ");		
		v_searchKeyList.add("pep " + regex + "\\?");	v_searchKeyList.add("pep " + regex + "\\."); 	v_searchKeyList.add(" pep-" + regex + " ");		v_searchKeyList.add(" pep-" + regex);		
		v_searchKeyList.add("pep " + regex + "\\,"); 	v_searchKeyList.add("pep " + regex + "\\;");	v_searchKeyList.add(" pep-" + regex + ": ");		
		v_searchKeyList.add("pep " + regex + "\\:");	v_searchKeyList.add("pep " + regex + "\\[");	v_searchKeyList.add(" pep " + regex + ": ");		   
		v_searchKeyList.add("pep-" + regex + ".txt");	v_searchKeyList.add("pep " + regex);
		//pep-0008.txt,1.26,1.27
		v_searchKeyList.add("pep-..." + regex+".txt");	v_searchKeyList.add("pep-..." + regex);			
		
//		v_ignoreList.clear();
//		v_ignoreList.add("pep: " + regex + ".");				v_ignoreList.add("pep: " + regex + "..");		v_ignoreList.add("pep: " + regex + "...");
		
		// we want to store the text after a pep number has been found		
		//best is to process one line each time
		//populate a variable "text" if pep if found, keep that line aside and keep adding to text until next pep found
		
		//we have to find out how many PEPs have been mentioned in that section/message
		//if only one, thn dont split - most likey all text would be aboutthat PEP
		
		Matcher m5 = null;		
		String[] lines = v_Message.split("\\n");
		//System.out.println("lines length="+lines.length);
		String previousIdentifier="",allTextOfCurrentPEP="";
		Boolean anotherPep= false;
		
		//we have to find out how many PEPs have been mentioned in that section/message
		//if only one, thn dont split - most likey all text would be aboutthat PEP
		//as it is coded, i add lines to string when pep is found and stop when new ppe is found
		//so i add code in teh end that checks taht if only one pep is found, 
		//we return original section rather than lines 
		
		boolean found=false;	
		String lastPEPNumberFound="";
	    for (String entireLine: lines){		//for all lines in message
	    	//System.out.println("Each Line: "+entireLine);
			for (String pattern : v_searchKeyList){		//for all patterns
				//System.out.println("\tpattern " + pattern);
				Pattern p5 = Pattern.compile(pattern);
				m5 = p5.matcher(entireLine);
				
				//pattern matched 
				while (m5.find()){ // while (m.find() || m9.find())
//					System.out.println(" Match found in mid ("+mid+"): line ("+entireLine+") Pattern ("+m5.toString()+")");
					if (found){	//output sentences next time a identifier is found, but output the previous identifier						
						//show all text in between two peps, in each case show text after the pep
//						System.out.println(" Identifier: " + pepNumber +"-was found in previous round------text ("+ text+")");
						if(pepNumber.equals(lastPEPNumberFound))  //if the pep number we want to find is same is pep number found in last iteration
						{}
						else{
//check here				//text="";
						}
						found=false;						
					}
					//m5.matches();
					//split before and after of message
					part1 = m5.group();	//get the ppe number/identifier
					part2 = m5.group(0);	
					
					if(part1.contains(pepNumber)){
						found =true;
						lastPEPNumberFound = pepNumber;	//sometimes same pep number occurs next time
					}
					else{
						//we want to know if in that lines any other pep is found
						//if so we know that a pep other than the current is found
						//then we divide the section, otherwise we treat the entire message is related to the current pep
						anotherPep=true;
					}
						//empty text
					previousIdentifier = part1;
				} // end while	
			} // end for
			//for every line, once a match is found, keep adding current sentence to variable -- and in that case, clear
			if (found){	
    			text= text + entireLine + "\n";
//    			allTextOfPEP = allTextOfPEP + entireLine + "\n";
    		}
	    }
	   // if (anotherPep)	
	    	return text; // extractedTextForcurrentIdentifierNumber;
	   // else 
	    //	return v_Message;
	}
	public void finalize() {	    //System.out.println("Probability instance is getting destroyed"); 
	}
}