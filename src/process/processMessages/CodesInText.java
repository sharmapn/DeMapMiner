package Process.processMessages;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Jan 2019, since in topic modelling we are looking for decision making terms,
// here we check to see if a sentence contains these code terms
public class CodesInText {
	/*
	static String codesTerms[] = {"lambda", "syntax", "expression", "exception", "parenthesis", "interpreter", "global", "variables", "function", "call", 
			 "method", "platform", "mapping", "getter", "system", "def", "return", "arg", "func", "print", "args", "pass","foo","retruns", "return", "cvs", 
			 "directory", "tmp", "nondist", "cvsroot", "serv", "code", "org", "http", "bit",
			 "bool", "www", "pgp", "api", "net", "sandbox", "path", "cgi, insert", "operator", "param", "parameter", "functions", "object", "class", "style", 
			 "string", "strings", "max","float","nan", "error", "register",
			 "processor", "threading", "long", "function", "define", "base", "call", "keyword", "package", "packages", "file", "files", "version", "path", 
			 "objects","processors", "scale", "diff","expressions", "cond", "break", "threadings", "parenthesis", "obj","tree", "true", "false",
			 "function", "nested", "parameters", "write", "variables", "execution", "module", "import", "modules", "code", "expr", "path", "pth", "locking",
			 "gmail","mail","dict","select", "selects","values","break","line","builtin","source","code", "method","methods","multi","line","arguments","data","colon",
			 "threads","performance","window","lang","operators","int","parenthesis","case","sequence","run","point","exceptions","exception","include","addurl",
			 //tricky terms inlcuded
			 "list", "argument", "call", "implementing"
	 		 }; 
	*/
	//patch, bug
	public static void main(String args[]) throws Exception { 
		
		
		String sent = "This function lambda keyword is depleted";
		//System.out.println(textHasCodingTerms(sent, stringArr));
	}
	
	static public boolean textHasCodingTerms(String sentence, String codeTerms[]){
		//List<String> CodeList = new ArrayList<>(Arrays.asList(codeTerms));
		try{
			
			//	System.out.println("terms: " + terms + " sentence: "+sentence);			
			if (sentence == "null" || sentence == null || sentence.length() == 0){	System.out.println("here a");
				return false; 
			}
			else{ //System.out.println("here b");
				sentence = sentence.replace(".", " .").replace(",", " ,").replace("!", " !").replace("-", " -").replace(";", " ;").replace(":", " :");	//dec 2018					
				String[] sentenceStr = sentence.toLowerCase().split(" ");
				//if(terms.contains(" ")){	terms = terms.replace("  "," ");  } //replace double spaces
				for (String x: codeTerms) {
					if( Arrays.asList(sentenceStr).contains(x)) {
						//System.out.println("Contains term " + x);
						return true;
					}
					//if( sentence.contains(x))
					//	System.out.println("Contains term direct match " + x);
				}
					//String[] splited = terms.toLowerCase().split(" ");	//if terms does not contain space no spaces, it woudl create an array of one element
				/*
				List<String> SentenceTermList = new ArrayList<>(Arrays.asList(sentenceStr));
				System.out.println("Arrays.asList(sentenceStr) " + Arrays.asList(sentenceStr));
				System.out.println("Arrays.asList(codesTerms) "  + Arrays.asList(codesTerms));
				System.out.println("SentenceTermList " + SentenceTermList.toString());
				SentenceTermList.retainAll(CodeList);
				System.out.println("Common " + SentenceTermList.toString());
				if(Arrays.asList(sentenceStr).contains(Arrays.asList(codesTerms))) { System.out.println("here 233");
					return true;
				}else {	System.out.println("here d");
					return false;
				}
				*/							
			}
			//return Arrays.asList(sentenceStr).containsAll(Arrays.asList(splited));
		}  //end try       
		catch (Exception e){ //			System.out.println("Error splited: " + splited + " sentenceStr: "+sentenceStr );
			System.out.println("Error Checking terms______here 348  " + e.toString() + "\n" ); System.out.println(StackTraceToString(e)  );	
		}
		return false;
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
