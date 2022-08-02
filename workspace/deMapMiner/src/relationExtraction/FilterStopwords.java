package relationExtraction;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class FilterStopwords {
	public static void main(String[] args) throws IOException {
	    List<String> lines = new ArrayList<String>();
	    BufferedReader reader = null;
	    try {
	        reader = new BufferedReader(new FileReader("c:\\scripts\\stopwords2.txt"));
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            lines.add(line);
	            System.out.println("line: " + line);
	        }
	    } finally {
	        reader.close();
	    }
	   Object[] array = lines.toArray();
	    
	    //display
	    for (Object myArray : array) {
	    	   System.out.println(myArray);
	    	}
	}

}
