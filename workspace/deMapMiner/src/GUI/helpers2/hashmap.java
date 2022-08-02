package GUI.helpers2;

import java.util.HashMap;
import java.util.Set;

public class hashmap {
    public static void main(String[] args) {

	// Create HashMap of three entries.
	HashMap<Integer, String> h = new HashMap<>();
	h.put(1,"apple");
	h.put(1,"banana");
	h.put(2,"peach");
	h.put(3,"guava");
	h.put(1,"apple");
	h.put(1,"banana");
	h.put(2,"peach");
	h.put(3,"guava");
	// Get keys.
	Set<Integer> keys = h.keySet();

	// Loop over String keys.
	for (Integer key : keys) {
	    System.out.println(key);
	}
    }
}
