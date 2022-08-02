package utilities;

import java.util.Arrays;

public class ShowExceptions {
//	catch(Exception e){
//		   showExceptions(e);
//	   }
//		    
//		    System.out.println(" FINAL ROLE IS: " +   theRole);		
//			//role shud just be bdfl, bdfl-delegate or author		
//			return theRole;
//		}
	   
	   public static void showExceptions(Exception e){
		   System.out.println("Exception " + e.toString());
		   System.err.println("1");
	       System.err.println(e);
	       System.err.println("\n2");
	       System.err.println(e.getMessage());
	       System.err.println("\n3");
	       System.err.println(e.getLocalizedMessage());
	       System.err.println("\n4");
	       System.err.println(e.getCause());
	       System.err.println("\n5");
	       System.err.println(Arrays.toString(e.getStackTrace()));
	       System.err.println("\n6");
	       e.printStackTrace();
	   }
}
