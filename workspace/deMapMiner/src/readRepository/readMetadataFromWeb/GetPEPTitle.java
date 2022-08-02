package readRepository.readMetadataFromWeb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;

public class GetPEPTitle {
	
	static Map peps = new HashMap();
	
	public static void main(String[] args) throws IOException {
			
	    URL url;
	    InputStream is = null;
	    BufferedReader br;
	    String line;
	    
	    //write final data
	    File fout = new File("c:\\scripts\\PEPNumberTitle.txt");
		FileOutputStream fos = new FileOutputStream(fout);	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

	    try {
	        url = new URL("https://github.com/python/peps");
	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));
	        File f = new File("c:\\scripts\\output\\a.txt");

	        while ((line = br.readLine()) != null) {
	            if(line.contains("href=")){
	               // System.out.println(line.trim());
	                String[] parts = line.split("<");
	                for(String p: parts){
	                	if(p.contains("href")){
	                		
	                		//https://github.com/python/peps/blob/master/pep-0001.txt
	                		
	                		int start = p.indexOf('"');
	                		int end = p.indexOf('"', start+1);
	                		String link = p.substring(start+1, end);
	                		link = "https://github.com" + link; 
	                		if (link.endsWith(".txt")&& !link.endsWith("README.txt")){
	                			
	                			//pep-3140.txt
	                			
	                			String pepno = link.substring(link.length() - 12);
	                			
	                			pepno = pepno.replace(".txt","");
	                			pepno = pepno.replace("pep-","");
	                			//System.out.println(pepno); 
	                			int pepNumber = Integer.parseInt(pepno);
	                			//506, 514, 
	                			if(pepNumber !=506 && pepNumber !=514)
	                				readFileGetPEPTitle(link, bw);
	                			//downloadFileFromURL(link,f);
//	                			System.out.println(start + " " + end+ " " + " link= " + link); // + " line= "+ p);
	                		}
	                	}
	                }
	            }
	        }
	    } catch (MalformedURLException mue) {
	         mue.printStackTrace();
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    } finally {
	        try {
	            if (is != null) is.close();
	        } catch (IOException ioe) {
	            //exception
	        }
	    }
	    
	    bw.close();
	    
	    System.out.println("Total peps: " + peps.size());
	    /*
	    System.out.print("Enter pep numbet to get title:");
	    String searchKey = System.console().readLine();	    
	    
	    if(peps.containsKey(searchKey))
	       System.out.println("Found total " + peps.get(searchKey) + " " + searchKey + " peps!\n");
	    else
	    	 System.out.println("Pep " + " " + searchKey + " not found!\n");
	    */
	    
	}
	
	static void readFileGetPEPTitle(String v_link, BufferedWriter bw) throws IOException{
		URL peplink = new URL(v_link);
        BufferedReader in = new BufferedReader(
        new InputStreamReader(peplink.openStream()));

        String inputLine;
        
        String pep = null;
        String title = null;
        while ((inputLine = in.readLine()) != null){      	
        	if (inputLine.contains(">PEP:")){
        		int start = inputLine.indexOf(">PEP:");
        		int end = inputLine.indexOf("</td>", start+5);
        		pep = inputLine.substring(start+5, end);
        		pep = pep.trim();
        		//System.out.println(pep);
            	
            }if (inputLine.contains(">Title:")){
            	int start = inputLine.indexOf(">Title:");
        		int end = inputLine.indexOf("</td>", start+7);
        		title = inputLine.substring(start+7, end);
        		//System.out.println(title);
            }             
            
        } 
 //       if(pep!=null && title!=null){
 //       	int pepNumber = Integer.parseInt(pep);
//        	peps.put(pep, title);
        	System.out.println(pep+" "+title);
        	bw.write(pep+","+title);
    		bw.newLine();
  //      }
        in.close();		
	}
}
