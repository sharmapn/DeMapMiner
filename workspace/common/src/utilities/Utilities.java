package utilities;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 
 * import Process.models.Pep;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
 */

public class Utilities {

	static List<String> searchKeyList = new ArrayList<String>();
	static List<String> wordsList = new ArrayList<String>();
	static List<String> ignoreList = new ArrayList<String>();
	static List<String> statusList = new ArrayList<String>();
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/peps";

    //  Database credentials
	static final String USER = "root";
	static final String PASS = "root";

	public String TextClassifier(String v_analysewords) {
		String classification = null;
		// Split text into sentences
		// String str =
		// "This is how I tried to split a paragraph into a sentence. But, there is a problem. My paragraph includes dates like Jan.13, 2014 , words like U.S and numbers like 2.2. They all got splitted by the above code.";
		Pattern re = Pattern
				.compile(
						"[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)",
						Pattern.MULTILINE | Pattern.COMMENTS);
		Matcher reMatcher = re.matcher(v_analysewords);
		while (reMatcher.find()) {
			// System.out.println("REMATCHER " + reMatcher.group());
			reMatcher.group();
		}
		return classification;
	}
	
	public String removeQuotedText(String v_message) {
		Integer start = 0, end = 0, countchar = 0;
		String sub = null;

		// countchar = number_of_cccurrences_of_char_in_string_java(Message);
		while (v_message.contains("\n>")) {
			// System.out.println(countchar);
			// for (int h=0; h<countchar; h++) {
			// if (Message.toLowerCase().contains("\n>")){
			start = v_message.indexOf("\n>");
			end = v_message.indexOf("\n", start + 1);
			if (start != -1 && end != -1) {
				sub = v_message.substring(start, end);
				// System.out.println(start + " " + end + " sub" + sub);
				v_message = v_message.replace(sub, "");
				// // System.out.println(countchar);
				// // System.out.println("start end " + start + end +
				// " replaced" + sub);
			}
			// }
			// }
		}
		return v_message;
	}
	
	public static Boolean stringAfter(String v_sentence, String firstString, String secondString) {
		   Integer a;
		   Integer b;
		   a = v_sentence.indexOf(firstString);
		   b = v_sentence.indexOf(secondString);
		   if (b > a)
		   	   return true;
		   else 
			   return false;
	   }
	
}
