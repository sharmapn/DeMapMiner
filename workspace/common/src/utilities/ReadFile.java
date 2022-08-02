package utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadFile {
	//can be a generic function
		public static String[] readKeywordsFromFile(String fileName) {
			String[] keyWords = null;
			try {
				BufferedReader in = new BufferedReader(new FileReader(fileName));
				String str;
				List<String> list = new ArrayList<String>();
				while((str = in.readLine()) != null){
					str = str.trim();
					if(str.isEmpty() || str== null || str.length()==0 || str.equals("")  || str.startsWith("%") || str.startsWith("#")) {
						continue;  //continue to read next line
					}
//					else if (str.startsWith("%") || str.startsWith("#"))
//					{ continue; }
//					else if(str.startsWith("##") ){	//|| str.trim().isEmpty()
//						System.out.println("breaking out of loop");
//						break;	//break out of loop
//					}
				    else{
				    	list.add(str);
				    	System.out.println("added to list: "+str);
				    }
				}
				keyWords = list.toArray(new String[0]);
				System.out.println("returning array");
			}
			catch (FileNotFoundException f){
				
			}
			catch (IOException io) {
				
			}
			return keyWords;
		}
}
