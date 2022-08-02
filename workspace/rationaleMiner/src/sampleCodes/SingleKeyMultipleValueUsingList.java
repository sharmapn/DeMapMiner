package sampleCodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * HashMap - Single Key and Multiple Values using List
 *
 * @author Jagadeesh Motamarri
 * @version 1.0
 */
public class SingleKeyMultipleValueUsingList {
	
	public class results{
		String message;
		Integer sentence, rank;
	}
	
	
    public static void main(String[] args) {
        // create map to store
        Map<Integer, List<results>> map = new HashMap<Integer, List<results>>();
        // create list one and store values
        List<String> valSetOne = new ArrayList<String>();
        valSetOne.add("Apple");
        valSetOne.add("Aeroplane");
        // create list two and store values
        List<String> valSetTwo = new ArrayList<String>();
        valSetTwo.add("Bat");
        valSetTwo.add("Banana");
        // create list three and store values
        List<String> valSetThree = new ArrayList<String>();
        valSetThree.add("Cat");
        valSetThree.add("Car");
        // put values into map
//        map.put(1, valSetOne);
//        map.put(2, valSetTwo);
 //       map.put(3, valSetThree);
        // iterate and display values
        System.out.println("Fetching Keys and corresponding [Multiple] Values n");
//        for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
//            Integer key = entry.getKey();
 //           List<String> values = entry.getValue();
 //           System.out.println("Key = " + key);
 //           System.out.println("Values = " + values + "n");
 //       }
    }
}
