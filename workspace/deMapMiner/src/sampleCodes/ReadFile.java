package sampleCodes;

import java.io.*;

public class ReadFile {
	public static void main(String[] args) throws IOException {

		String inputLine = "";
		FileReader inputFile = new FileReader("c:\\scripts\\input-BaseIdeasMoreParam.txt");
		BufferedReader br = new BufferedReader(inputFile);

		System.out.println("\nThe contents of the file player.txt are ");

		// Read lines of text from the file, looping until the
		// null character (ctrl-z) is endountered
		while ((inputLine = br.readLine()) != null) {
			System.out.println(inputLine); // Write to the screen
		}
		br.close(); // Close the stream

	} // end of main method
} // end of class