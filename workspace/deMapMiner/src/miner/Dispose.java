package miner;

public class Dispose {
	public static void test(String[] args) {
		//class Resource r = new Resource();	
		try {	
		    //work	
		} finally {	
		   // r.dispose();	
		}
	}
	
	public static void main(String[] args) 
    { 
        String s = new String("RR");
        s = null; 
        // Requesting JVM to call Garbage Collector method 
        System.gc(); 
        System.out.println("Main Completes"); 
    }  
    // Here overriding finalize method 
    public void finalize() 
    { 
        System.out.println("finalize method overriden"); 
    } 
}	
	//also add the finale in every class yopu use
	//The finalize() function is the destructor.

