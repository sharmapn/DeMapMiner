package callIELibraries;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class SocketClientForNLP {
	static Socket s = null; // it automatically connects, assuming the server is running on hilton
	public static DataOutputStream dos = null;
	public static DataInputStream dis = null; 
	
	public static void init() throws UnknownHostException, IOException {
		s = new Socket("localhost",10450); // it automatically connects, assuming the server is running on hilton
		dos = new DataOutputStream(s.getOutputStream());
		dis = new DataInputStream(s.getInputStream());
	}	
		
	public static void main (String args[]) {
		try {
			init();
			
			boolean coreference = true, doublesTriples = false, ner= false;
			
			long startTime = System.nanoTime();
			//code			
		    //choose what we want to do
		    //coreference
			//for(int i=0;i<100;i++){
			if(coreference){
			    String printString = "John drove to Judy’s house. He made her dinner";
			    //String printString2 = "Barack Obama was born in Hawaii.  He is the president. Obama was elected in 2008. Barack Obama was born in Hawaii.  He is the president. Obama was elected in 2008.";			
			    
			    printString= "C"+printString;
			    dos.writeUTF(printString);
				String result = dis.readUTF();
				System.out.println("Coreferenced Text: " + result);
			}
			//}
			////extract doubles triples
			if(doublesTriples){
				String printString3 = "PEP 308 Acceptance";//"Propose rejection of PEP";//
				printString3= "T"+printString3;
				dos.writeUTF(printString3);
				String triple = dis.readUTF();
				System.out.println("SVO Returned: " + triple);
				String[] svoArray = triple.split(",");
				for(String pos: svoArray){
					System.out.print(pos+ " ");
				}
			}
			
			long endTime = System.nanoTime();
			System.out.println("Took "+(endTime - startTime) + " ns"); 
			
			Scanner sc=new Scanner(System.in);
			System.out.println("\nPress enter to exit.....");
			sc.nextLine();	
			
			//s.close();
		}
		catch (Exception e) {
			System.out.println("Exception " + e);
		}
	}
	
	public static void closeSocket() throws IOException{
		s.close();
	}

	
}