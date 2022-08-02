package miner.process;

public class ErrorTracing {
	
	//usage
//	 catch (Exception e){ 
//	  	    System.out.println(m.getMessage_ID() + " ______here  " + e.toString() + "\n" );
//	  	    //System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
//	  	  System.out.println(StackTraceToString(e)  );	
//	    	//continue;
//	    } 
	
	public static String StackTraceToString(Exception ex) {
	    String result = ex.toString() + "\n";
	    StackTraceElement[] trace = ex.getStackTrace();
	    for (int i=0;i<trace.length;i++) {
	        result += trace[i].toString() + "\n";
	    }
	    return result;
	}
	
}
