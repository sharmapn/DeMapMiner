package utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class ReturnNattyTimeStamp {
	
	
	//nlp datetime parser for all possible datetime formats	
		public static Date returnNattyDate(String dateStr) {
			try
			{
				List<java.util.Date> dateList = new ArrayList<java.util.Date>();	
				Parser parser = new Parser();
				
				//List<DateGroup> groups = parser.parse("the day before next thursday");
				List<DateGroup> groups = parser.parse(dateStr);
				for (DateGroup group : groups) {
					try{
						List<java.util.Date> dates = group.getDates();
						int line = group.getLine();
						int column = group.getPosition();
						String matchingValue = group.getText();
						String syntaxTree = group.getSyntaxTree().toStringTree();
						Map parseMap = group.getParseLocations();
						boolean isRecurreing = group.isRecurring();
						java.util.Date recursUntil = group.getRecursUntil();
					}
					catch (Exception e){
						System.out.println("Caught exception" + e.toString());
					}
	
					/* if any Dates are present in current group then add them to dateList */
					if (group.getDates() != null) {
						dateList.addAll(group.getDates());
					}
				}
				for (java.util.Date d : dateList){
					return d;	//just return first date found
				}
				
			}
			catch (Exception e) {
				e.printStackTrace();
				System.err.println("Got an exception! " + e.toString());
				System.err.println(e.getMessage());
				System.out.println(StackTraceToString(e));
			}
			return null;
		}
		public static String StackTraceToString(Exception ex) {
			String result = ex.toString() + "\n";
			StackTraceElement[] trace = ex.getStackTrace();
			for (int i=0;i<trace.length;i++) {
				result += trace[i].toString() + "\n";
			}
			return result;
		}
}
