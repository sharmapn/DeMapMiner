package GeneralQueries;

/**
 *  Word Count Challenge
 *
 *  Dec 2010
 *
 *  Pass in a sentence in the form of a String and a breakdown
 *  of the number occurances of each word will be displayed.
 *
 */
 
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
 
 
public class WordCount {
 
    private String[] splitter;
    private int[] counter;
    HashMap map = new HashMap();
 
 
    public void retrieveMessagesFromDB (Integer v_PEP){
    //	 FileWriter writer = new FileWriter("c:\\scripts\\testoutput.csv");
         final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
         try {
             Class.forName("com.mysql.jdbc.Driver").newInstance();
           } catch (Exception e) {
             System.err.println("Unable to find and load driver");
             System.exit(1);
           }
         
         Integer pepNumber = v_PEP,message_ID = null;
         String Message = null,author,v_date,statusFrom,statusTo,status;
         Matcher m5 = null,mw = null;
         String wordsFoundList = null;
         
         //  Connect to an MySQL Database, run query, get result set
         String url = "jdbc:mysql://localhost:3306/peps";
         String userid = "root";
         String password = "root";																														//AND messageID = 11297
         String sql = "SELECT date2, sendername, analysewords, statusFrom, statusTo, statusChanged, messageID from allpeps WHERE pep = " + pepNumber + "  order by date2;"; 
         //can add messageid later
         
         Integer counter =0;
         String newMessage = null;
         String sentence;
         
         String entireLongList = null;
         
         // Java SE 7 has try-with-resources
         // This will ensure that the sql objects are closed when the program 
         // is finished with them
         try (Connection connection = DriverManager.getConnection( url, userid, password );
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery( sql ))
         {
         	 while (rs.next()){     //chec
         		Message = rs.getString(3);
         		message_ID = rs.getInt(7);
        // 		entireLongList += Message;
         		countWords(Message);
         		System.out.println("Message id-------------------------------------------- " + message_ID );
         		//printResults();
         		addMaps();
         	 }
         	//countWords(Message);
         	System.out.println("Final count-----------------------------------------------" );
         	printResults();
         	 
         }
         catch (SQLException e)
         {
             System.out.println( e.getMessage() );
         }
    }
    
    /*
     * @param String - represents the sentence to be parsed
     *
     */
    public void countWords(String text){         
        // remove any '\n' characters that may occur
        String temp = text.replaceAll("[\\n]", " "); 
        // replace any grammatical characters and split the String into an array
        splitter = temp.replaceAll("[.,?!:;/]", "").split(" "); 
        // intialize an int array to hold count of each word
        counter= new int[splitter.length]; 
        // loop through the sentence
        for(int i =0; i< splitter.length; i++){ 
            // hold current word in the sentence in temp variable
            temp = splitter[i]; 
                // inner loop to compare current word with those in the sentence
                // incrementing the counter of the adjacent int array for each match
                for (int k=0; k< splitter.length; k++){ 
                    if(temp.equalsIgnoreCase(splitter[k]))
                    {
                        counter[k]++;
                    }
                }
        }
    }
 
    public Map<String, Integer> mergeSumOfMaps(Map<String, Integer>... maps) {
        final Map<String, Integer> resultMap = new HashMap<>();
        for (final Map<String, Integer> map : maps) {
            for (final String key : map.keySet()) {
                final int value;
                if (resultMap.containsKey(key)) {
                    final int existingValue = resultMap.get(key);
                    value = map.get(key) + existingValue;
                }
                else {
                    value = map.get(key);
                }
                resultMap.put(key, value);
            }
        }
        return resultMap;
    }
    
    private void addMaps()
    {
    	 // create a HashMap to store unique combination of words and their counter
        // the word being the key and the number of occurences is the value
        HashMap tempmap = new HashMap();
   
        // populate the map
        for (int i=0; i< splitter.length; i++)
        {
            tempmap.put(splitter[i].toLowerCase(), counter[i]);
        }
        
        Map<String, Integer> result = (mergeSumOfMaps(map, tempmap));
        
        //merge hashmaps here
        map = (HashMap) result;
    }
    private void printResults()
    {
 
     
      
      //map3 = new HashMap();
     // map.putAll(tempmap);
      //map3.putAll(map2);
      
      // create an iterator on the map keys
      Iterator it = map.keySet().iterator();
 
      //  System.out.println("Word             Count");
     //   System.out.println("-----------------------");
 
        // loop for each key
        while(it.hasNext())
        {
            String temp =(String)it.next();
 
            // print the word itself
//            System.out.print(temp);
 
             // add relevant spacing to print consistently
            for (int i=0;i< (20 - temp.length());i++){
 //               System.out.print(" ");
            }
 
            // print the value (i.e. count of each word)
//            System.out.println(map.get(temp.toString()));
        }
        
        boolean ASC = true;
        boolean DESC = false;
        
        // Creating dummy unsorted map
       // Map<String, Integer> unsortMap = new HashMap<String, Integer>();
      //  unsortMap.put("B", 55);
       // unsortMap.put("A", 80);
      //  unsortMap.put("D", 20);
       // unsortMap.put("C", 70);

 //       System.out.println("Before sorting......");
 //       printMap(unsortMap);

        System.out.println("After sorting ascending order.............................");
    //    Map<String, Integer> sortedMapAsc = sortByComparator(map, ASC);
    //    printMap(sortedMapAsc);


   //     System.out.println("After sorting descindeng order......");
        Map<String, Integer> sortedMapDesc = sortByComparator(map, DESC);
        printMap(sortedMapDesc);

        
    }
 
    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    
    public static void printMap(Map<String, Integer> map)
    {
    	String heading1 = "Word";
    	String heading2 = "Count";

    	System.out.printf( "%-15s %30s %n", heading1, heading2);
    	System.out.println("-------------------------------------------------");
    	for (Entry<String, Integer> entry : map.entrySet())
        {
            //System.out.println("Key : " + entry.getKey() + " Value : "+ entry.getValue());
    		System.out.printf( "%-15s %30s %n", entry.getKey(), entry.getValue());
           // System.out.println("" + entry.getKey() + " "+ entry.getValue());
        }
    }
 
 
    // main method to test the class
    public static void main(String[] args){
 
        // example sentence
       /* String sentence = "How much wood, would a woodchuck chuck\n"+
                          "If a woodchuck could chuck wood?\n"+
                          "A woodchuck would chuck all the wood he could.\n"+
                          "If a woodchuck could chuck wood!";
        */
        WordCount wc = new WordCount();
 
       // wc.countWords(sentence);
        wc.retrieveMessagesFromDB (438);
 
    }
 
}
