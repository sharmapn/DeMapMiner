package readCommits.Input;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class OutputWritter {
	
	public static void write(String input, String fileName){
		
		try {
			   
			File file = new File(fileName);
	
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
	
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(input);
			bw.close();
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}