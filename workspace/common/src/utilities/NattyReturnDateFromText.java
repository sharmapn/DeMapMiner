package utilities;

//======================
//MainActivity.java
//======================

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

//natty return date
public class NattyReturnDateFromText {
	
	public Date returnEventDateInText(String text) {
        List<Date> dateList = new ArrayList<Date>();
      
        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse("the day before next thursday");
        for (DateGroup group : groups) {
                List<Date> dates = group.getDates();
                int line = group.getLine();
                int column = group.getPosition();
                String matchingValue = group.getText();
                String syntaxTree = group.getSyntaxTree().toStringTree();
                Map parseMap = group.getParseLocations();
                boolean isRecurreing = group.isRecurring();
                Date recursUntil = group.getRecursUntil();

                /* if any Dates are present in current group then add them to dateList */
                if (group.getDates() != null) {
                        dateList.addAll(group.getDates());
                }
        }
        //for(Date dlo : dateList){
        //	System.out.println("Date captured: "+dlo);
        //}
        
        //System.out.println("Test app is complete");
        return dateList.get(0);
	}

    public static void main(String[] args) {
            List<Date> dateList = new ArrayList<Date>();
          
            Parser parser = new Parser();
            List<DateGroup> groups = parser.parse("the day before next thursday");
            for (DateGroup group : groups) {
                    List<Date> dates = group.getDates();
                    int line = group.getLine();
                    int column = group.getPosition();
                    String matchingValue = group.getText();
                    String syntaxTree = group.getSyntaxTree().toStringTree();
                    Map parseMap = group.getParseLocations();
                    boolean isRecurreing = group.isRecurring();
                    Date recursUntil = group.getRecursUntil();

                    /* if any Dates are present in current group then add them to dateList */
                    if (group.getDates() != null) {
                            dateList.addAll(group.getDates());
                    }
            }
            for(Date dlo : dateList){
            	System.out.println("Date captured: "+dlo);
            }
            
            System.out.println("Test app is complete");
    }

}
