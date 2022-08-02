package excelwrite;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ApachePOIExcelWrite {
	
  //get pep type
  	public static GetPEPDetails gpd = new GetPEPDetails(); 
  	
  	public static String[] states_to_find_reasons = {"accepted","rejected","withdrawn","vote","poll"};
    
    public static void writePostProcessResultsToExcelFile(String filename,ApachePOIExcelWrite ew, Connection conn, String pepTypeToFilter) {        
    	XSSFWorkbook workbook = new XSSFWorkbook();
//    	XSSFCellStyle style=workbook.createCellStyle();//    	style.setBorderBottom(XSSFCellStyle.);
//    	style.setBorderTop(XSSFCellStyle.BORDER_THIN);//    	style.setBorderRight(XSSFCellStyle.BORDER_THIN);
//    	style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        XSSFSheet sheet = workbook.createSheet("Post Process Results");       
        int rowNum = 0;
        System.out.println("Creating excel for Post Process Results");        
        try	{
        	//extract from postprocessed results table
        	Statement stmt = conn.createStatement();
			// the mysql insert statement																			
			ResultSet rs = stmt.executeQuery("SELECT pep, author, timestamp,NumberOfPEPsMentionedInMessage,folder,"	    //5	
												+ "file, messageID, label, subject, relation, "						//10
												+ "object, currentSentence, clausie, role, allReasons, "			//15
												+ "reasonsInSentence,reasonTermsInMatchedTriple, reasonTermsInNearbySentencesParagraphs, reasonTriplesInNearbySentencesParagraphs, "  //19
												+ "labelFoundInMsgSubject,isFirstParagraph, isLastParagraph, authorRole "
												+ "from results_postprocessed "
												+ "order by pep asc, timestamp asc, messageID asc");
			
			while (rs.next()) {	
				Integer pepNumber = rs.getInt(1);
				//in case when we want to see all peptype result for diagram OR a specific type
				//check and extract only the pepTypes we need
				String resultPEPType = gpd.getPepTypeForPep(pepNumber,conn).trim();
				//works in individual cases					//works in all cases
				if (pepTypeToFilter.equals(resultPEPType) || pepTypeToFilter.equals("All Types")){			
					//excel
    				Row row = sheet.createRow(rowNum++);
    				int colNum = 0;    		
    				
    				Timestamp datetimestamp = rs.getTimestamp(3);
    				String dts ="";
    				if(datetimestamp==null || datetimestamp.equals(""))
    					dts= "";
    				else{
    					 dts = datetimestamp.toString();
    				}
    				    				   				
    				//add cell to excel..pepnumber
    				Cell cell = row.createCell(colNum++); cell.setCellValue((Integer)  pepNumber); 		//pep   				
    				Cell cel2 = row.createCell(colNum++); cel2.setCellValue((String)   rs.getString(2));	//author
    				Cell cel23 = row.createCell(colNum++); cel23.setCellValue((String) rs.getString(23));	//authorRole
    				Cell cel3 = row.createCell(colNum++); cel3.setCellValue((String)   dts);		//date		//getTimestamp
    				Cell cel4 = row.createCell(colNum++); cel4.setCellValue((Integer)  rs.getInt(4)); 		//NumberOfPEPsMentionedInMessage
    				Cell cel5 = row.createCell(colNum++); cel5.setCellValue((String)   rs.getString(5));  	//folder
    				Cell cel6 = row.createCell(colNum++); cel6.setCellValue((String)   rs.getString(6)); 	//file
    				Cell cel7 = row.createCell(colNum++); cel7.setCellValue((Integer)  rs.getInt(7)); 		//messageid
    				Cell cel8 = row.createCell(colNum++); cel8.setCellValue((String)   rs.getString(8));	//label    				
    				
    				//processing result
    				Cell cel9 = row.createCell(colNum++); cel9.setCellValue((String) rs.getString(9));		//subject
    				Cell cel10 = row.createCell(colNum++); cel10.setCellValue((String) rs.getString(10));	 	//relation
    				Cell cel11 = row.createCell(colNum++); cel11.setCellValue((String) rs.getString(11));	//object
    				Cell cel12 = row.createCell(colNum++); cel12.setCellValue((String) rs.getString(12));	//currentsentence
    				Cell cel13 = row.createCell(colNum++); cel13.setCellValue((String) rs.getString(13));	//clausie
    				Cell cel14 = row.createCell(colNum++); cel14.setCellValue((String) rs.getString(14));	//role
    				
    				//reasons
    				Cell cel15 = row.createCell(colNum++); cel15.setCellValue((String) rs.getString(15));	//allReasons
    				Cell cel16 = row.createCell(colNum++); cel16.setCellValue((String) rs.getString(16));	//reasonsInSentence
    				Cell cel17 = row.createCell(colNum++); cel17.setCellValue((String) rs.getString(17));	//reasonTermsInMatchedTriple
    				Cell cel18 = row.createCell(colNum++); cel18.setCellValue((String) rs.getString(18));	//reasonTermsInNearbySentencesParagraphs
    				Cell cel19 = row.createCell(colNum++); cel19.setCellValue((String) rs.getString(19));	//reasonTriplesInNearbySentencesParagraphs
    				//dec 2018
    				Cell cel20 = row.createCell(colNum++); cel20.setCellValue((String) rs.getString(20));	//
    				Cell cel21 = row.createCell(colNum++); cel21.setCellValue((String) rs.getString(21));	//
    				Cell cel22 = row.createCell(colNum++); cel22.setCellValue((String) rs.getString(22));	//
				}
			}		
    		System.out.println("Excel File for "+ pepTypeToFilter + " is created successfully.");
    	}
    	catch (Exception e){
    			System.out.println("Exception 291 (FILE) " + e.toString()); 
    			System.out.println(StackTraceToString(e)  );
    	}
        
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("Done");
    }
    
    public static void writePostProcessResultsToExcelFile_ForMainStates(String filename,ApachePOIExcelWrite ew, Connection conn) {        
    	XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Post Process Results");       
        int rowNum = 0;
        System.out.println("Creating excel for Post Process Results - Main States");        
        try	{
        	//extract from postprocessed results table
        	Statement stmt = conn.createStatement();
			// the mysql insert statement																			
			ResultSet rs = stmt.executeQuery("SELECT pep, author, timestamp,NumberOfPEPsMentionedInMessage,folder,"	    //5	
												+ "file, messageID, label, subject, relation, "						//10
												+ "object, currentSentence, clausie, role, allReasons, "			//15
												+ "reasonsInSentence,reasonTermsInMatchedTriple, reasonTermsInNearbySentencesParagraphs, reasonTriplesInNearbySentencesParagraphs, "  //19
												+ "labelFoundInMsgSubject,isFirstParagraph, isLastParagraph, authorRole "
												+ "from results_postprocessed "
												+ " where messageid > 5000000 and messageid < 6000000 "
												+ "order by pep asc, timestamp asc, messageID asc");
			
			while (rs.next()) {	
				Integer pepNumber = rs.getInt(1);				
				if (1==1){			
					//excel
    				Row row = sheet.createRow(rowNum++);    				int colNum = 0;
    				Timestamp datetimestamp = rs.getTimestamp(3);
    				String dts ="";
    				if(datetimestamp==null || datetimestamp.equals(""))
    					dts= "";
    				else{
    					 dts = datetimestamp.toString();
    				}
    				    				   				
    				//add cell to excel..pepnumber
    				Cell cell = row.createCell(colNum++); cell.setCellValue((Integer)  pepNumber); 		//pep   				
    				Cell cel2 = row.createCell(colNum++); cel2.setCellValue((String)   rs.getString(2));	//author
    				Cell cel23 = row.createCell(colNum++); cel23.setCellValue((String) rs.getString(23));	//authorRole
    				Cell cel3 = row.createCell(colNum++); cel3.setCellValue((String)   dts);		//date		//getTimestamp
    				Cell cel4 = row.createCell(colNum++); cel4.setCellValue((Integer)  rs.getInt(4)); 		//NumberOfPEPsMentionedInMessage
    				Cell cel5 = row.createCell(colNum++); cel5.setCellValue((String)   rs.getString(5));  	//folder
    				Cell cel6 = row.createCell(colNum++); cel6.setCellValue((String)   rs.getString(6)); 	//file
    				Cell cel7 = row.createCell(colNum++); cel7.setCellValue((Integer)  rs.getInt(7)); 		//messageid
    				Cell cel8 = row.createCell(colNum++); cel8.setCellValue((String)   rs.getString(8));	//label    				
    				
    				//processing result
    				Cell cel9 = row.createCell(colNum++); cel9.setCellValue((String) rs.getString(9));		//subject
    				Cell cel10 = row.createCell(colNum++); cel10.setCellValue((String) rs.getString(10));	 	//relation
    				Cell cel11 = row.createCell(colNum++); cel11.setCellValue((String) rs.getString(11));	//object
    				Cell cel12 = row.createCell(colNum++); cel12.setCellValue((String) rs.getString(12));	//currentsentence
    				Cell cel13 = row.createCell(colNum++); cel13.setCellValue((String) rs.getString(13));	//clausie
    				Cell cel14 = row.createCell(colNum++); cel14.setCellValue((String) rs.getString(14));	//role
    				
    				//reasons
    				Cell cel15 = row.createCell(colNum++); cel15.setCellValue((String) rs.getString(15));	//allReasons
    				Cell cel16 = row.createCell(colNum++); cel16.setCellValue((String) rs.getString(16));	//reasonsInSentence
    				Cell cel17 = row.createCell(colNum++); cel17.setCellValue((String) rs.getString(17));	//reasonTermsInMatchedTriple
    				Cell cel18 = row.createCell(colNum++); cel18.setCellValue((String) rs.getString(18));	//reasonTermsInNearbySentencesParagraphs
    				Cell cel19 = row.createCell(colNum++); cel19.setCellValue((String) rs.getString(19));	//reasonTriplesInNearbySentencesParagraphs
    				//dec 2018
    				Cell cel20 = row.createCell(colNum++); cel20.setCellValue((String) rs.getString(20));	//
    				Cell cel21 = row.createCell(colNum++); cel21.setCellValue((String) rs.getString(21));	//
    				Cell cel22 = row.createCell(colNum++); cel22.setCellValue((String) rs.getString(22));	//
				}
			}		
    		System.out.println("Excel File for Main States is created successfully.");
    	}
    	catch (Exception e){
    			System.out.println("Exception 291 (FILE) " + e.toString()); 
    			System.out.println(StackTraceToString(e)  );
    	}        
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("Done");
    }
    
    public static void writeRawResultsToExcelFile(String filename,ApachePOIExcelWrite ew, Connection conn) {        
    	XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Raw Results");       
        int rowNum = 0;
        System.out.println("Creating excel for Raw Results");
        
        try
    	{
        	//extract from postprocessed results table
        	Statement stmt = conn.createStatement();
			// the mysql insert statement																			
			ResultSet rs = stmt.executeQuery("SELECT pep, author, datetimestamp,NumberOfPEPsMentionedInMessage,folder,"	    //5	
												+ "file, messageID, label, subject, relation, "						//10
												+ "object, currentSentence, clausie, role, allReasons, "			//15
												+ "reasonInSentence,reasonTermsInMatchedTriple, reasonTermsInNearbySentencesParagraphs, reasonTriplesInNearbySentencesParagraphs, "  //19
												+ "labelFoundInMsgSubject,isFirstParagraph, isLastParagraph, authorRole "
												+ "from results "
												+ "order by pep asc, datetimestamp asc, messageID asc");
			
			while (rs.next()) {	
				Integer pepNumber = rs.getInt(1);
				//in case when we want to see all peptype result for diagram OR a specific type
				//check and extract only the pepTypes we need
							
				//excel
				Row row = sheet.createRow(rowNum++);
				int colNum = 0;   		
				
				Timestamp datetimestamp = rs.getTimestamp(3);
				String dts ="";
				if(datetimestamp==null || datetimestamp.equals(""))
					dts= "";
				else{
					 dts = datetimestamp.toString();
				}
				    				   				
				//add cell to excel..pepnumber
				Cell cell = row.createCell(colNum++); cell.setCellValue((Integer)  pepNumber); 		//pep   				
				Cell cel2 = row.createCell(colNum++); cel2.setCellValue((String)   rs.getString(2));	//author
				Cell cel23 = row.createCell(colNum++); cel23.setCellValue((String) rs.getString(23));	//authorRole
				Cell cel3 = row.createCell(colNum++); cel3.setCellValue((String)   dts);		//date		//getTimestamp
				Cell cel4 = row.createCell(colNum++); cel4.setCellValue((Integer)  rs.getInt(4)); 		//NumberOfPEPsMentionedInMessage
				Cell cel5 = row.createCell(colNum++); cel5.setCellValue((String)   rs.getString(5));  	//folder
				Cell cel6 = row.createCell(colNum++); cel6.setCellValue((String)   rs.getString(6)); 	//file
				Cell cel7 = row.createCell(colNum++); cel7.setCellValue((Integer)  rs.getInt(7)); 		//messageid
				Cell cel8 = row.createCell(colNum++); cel8.setCellValue((String)   rs.getString(8));	//label    				
				
				//processing result
				Cell cel9 = row.createCell(colNum++); cel9.setCellValue((String) rs.getString(9));		//subject
				Cell cel10 = row.createCell(colNum++); cel10.setCellValue((String) rs.getString(10));	 	//relation
				Cell cel11 = row.createCell(colNum++); cel11.setCellValue((String) rs.getString(11));	//object
				Cell cel12 = row.createCell(colNum++); cel12.setCellValue((String) rs.getString(12));	//currentsentence
				Cell cel13 = row.createCell(colNum++); cel13.setCellValue((String) rs.getString(13));	//clausie
				Cell cel14 = row.createCell(colNum++); cel14.setCellValue((String) rs.getString(14));	//role
				
				//reasons
				Cell cel15 = row.createCell(colNum++); cel15.setCellValue((String) rs.getString(15));	//allReasons
				Cell cel16 = row.createCell(colNum++); cel16.setCellValue((String) rs.getString(16));	//reasonsInSentence
				Cell cel17 = row.createCell(colNum++); cel17.setCellValue((String) rs.getString(17));	//reasonTermsInMatchedTriple
				Cell cel18 = row.createCell(colNum++); cel18.setCellValue((String) rs.getString(18));	//reasonTermsInNearbySentencesParagraphs
				Cell cel19 = row.createCell(colNum++); cel19.setCellValue((String) rs.getString(19));	//reasonTriplesInNearbySentencesParagraphs
				//dec 2018
				Cell cel20 = row.createCell(colNum++); cel20.setCellValue((String) rs.getString(20));	//
				Cell cel21 = row.createCell(colNum++); cel21.setCellValue((String) rs.getString(21));	//
				Cell cel22 = row.createCell(colNum++); cel22.setCellValue((String) rs.getString(22));	//
				
			}		
    		System.out.println("Excel File for Raw Results created successfully.");
    	}
    	catch (Exception e){
    			System.out.println("Exception 291 (FILE) " + e.toString()); 
    			System.out.println(StackTraceToString(e)  );
    	}
        
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("Done");
    }
    
    public static void writePostProcessResultsReasonsToExcelFile(String filename,ApachePOIExcelWrite ew, Connection conn) {        
    	XSSFWorkbook workbook = new XSSFWorkbook();
        
        try
    	{	
        	//show pep and states beside
        	XSSFSheet sheet0 = workbook.createSheet("AllPEPsAndStates");
        	int rowNum0 = 0;        	
        	Statement stmt11 = conn.createStatement();
			// the mysql insert statement																			
			ResultSet rs11 = stmt11.executeQuery("SELECT pep, clausie from results_postprocessed order by pep asc, timestamp asc, messageID asc");
			Integer lastPEPNumber=null;
			Row row0 = null;
			int colNum0 = 0;
			while (rs11.next()) {
				Integer pepNumber = rs11.getInt(1);
				if(pepNumber==lastPEPNumber) {
					Cell cel13 = row0.createCell(colNum0++); cel13.setCellValue((String) rs11.getString(2));	//clausie
				}
				else {
					//rowNum0=0;
					colNum0 = 0;
					row0 = sheet0.createRow(rowNum0++);
					Cell cell = row0.createCell(colNum0++); cell.setCellValue((Integer)  pepNumber); 		//pep
					lastPEPNumber=pepNumber;
				}
			}		
    		System.out.println("Excel Sheet for AllPEPsAndStates is created successfully.");
        	
        	//All types of reasons extract from postprocessed results table
    		XSSFSheet sheet = workbook.createSheet("PEPReasons");  
        	Statement stmt = conn.createStatement();
			// the mysql insert statement																			
			ResultSet rs = stmt.executeQuery("SELECT pep, author, timestamp,NumberOfPEPsMentionedInMessage,folder,"	    //5	
												+ "file, messageID, label, subject, relation, "						//10
												+ "object, currentSentence, clausie, role, allReasons, "			//15
												+ "reasonsInSentence,reasonTermsInMatchedTriple, reasonTermsInNearbySentencesParagraphs, reasonTriplesInNearbySentencesParagraphs, "  //19
												+ "pp,ep,np "
												+ "from results_postprocessed "
												+ "order by pep asc, timestamp asc, messageID asc");
			int rowNum = 0;
			int colNum = 0; 
			//add header
			Row row00 = sheet.createRow(rowNum++);
			Cell cel01 = row00.createCell(colNum++); cel01.setCellValue((String) "PEP"); 		//pep
			Cell cel02 = row00.createCell(colNum++); cel02.setCellValue((String) "State");	//clausie
			Cell cel03 = row00.createCell(colNum++); cel03.setCellValue((String) "Sentence");	//clausie
			
			//reasons
			Cell cel07 = row00.createCell(colNum++); cel07.setCellValue((String) "allReasons");	//allReasons
			Cell cel08 = row00.createCell(colNum++); cel08.setCellValue((String) "reasonsInSentence");	//reasonsInSentence
			Cell cel09 = row00.createCell(colNum++); cel09.setCellValue((String) "reasonTermsInMatchedTriple");	//reasonTermsInMatchedTriple
			Cell cel10 = row00.createCell(colNum++); cel10.setCellValue((String) "reasonTermsInNearbySentencesParagraphs");	//reasonTermsInNearbySentencesParagraphs
			Cell cel11 = row00.createCell(colNum++); cel11.setCellValue((String) "reasonTriplesInNearbySentencesParagraphs");	//reasonTriplesInNearbySentencesParagraphs
			
			Cell cel04 = row00.createCell(colNum++); cel04.setCellValue((String) "Previous Paragraph");	//clausie
			Cell cel05 = row00.createCell(colNum++); cel05.setCellValue((String) "Entire Paragraph");	//clausie
			Cell cel06 = row00.createCell(colNum++); cel06.setCellValue((String) "Next Paragraph");	//clausie
			
			while (rs.next()) {	
				Integer pepNumber = rs.getInt(1);
				//excel
				Row row01 = sheet.createRow(rowNum++);
				colNum = 0;    		
												    				   				
				//add cell to excel..pepnumber
				Cell cela = row01.createCell(colNum++); cela.setCellValue((Integer)  pepNumber); 		//pep
				Cell celb = row01.createCell(colNum++); celb.setCellValue((String)   rs.getString(13));	//clausie
				Cell celc = row01.createCell(colNum++); celc.setCellValue((String)   rs.getString(12));	//sentence
				
				//reasons
				Cell celg = row01.createCell(colNum++); celg.setCellValue((String) rs.getString(15));	//allReasons
				Cell celh = row01.createCell(colNum++); celh.setCellValue((String) rs.getString(16));	//reasonsInSentence
				Cell celi = row01.createCell(colNum++); celi.setCellValue((String) rs.getString(17));	//reasonTermsInMatchedTriple
				Cell celj = row01.createCell(colNum++); celj.setCellValue((String) rs.getString(18));	//reasonTermsInNearbySentencesParagraphs
				Cell celk = row01.createCell(colNum++); celk.setCellValue((String) rs.getString(19));	//reasonTriplesInNearbySentencesParagraphs
				
				String pp = rs.getString(20);
				pp = trimStringLessThanThousandChars(pp);
				String ep = rs.getString(21);
				ep = trimStringLessThanThousandChars(ep);
				String np = rs.getString(22);
				np = trimStringLessThanThousandChars(np);
				
				Cell celd = row01.createCell(colNum++); celd.setCellValue((String) pp);	//pp  //had to truncate as 32,556 is max 
				Cell cele = row01.createCell(colNum++); cele.setCellValue((String) ep);	//ep
				Cell celf = row01.createCell(colNum++); celf.setCellValue((String) np);	//nn
			}		
    		System.out.println("Excel Sheet for PEP Reasons is created successfully.");
    		
    		//REASONS ordered by states    	    	
			System.out.println("\nPEP,  State, Reason ");
			for (String state: states_to_find_reasons){	
				rowNum=0;
				XSSFSheet statex = workbook.createSheet("PEPReasons_"+state); 
	  			//System.out.println();
				System.out.println("State: ("+state + ")");
				//extract from postprocessed results table
				PreparedStatement stmt0 = conn.prepareStatement("SELECT pep, author, timestamp,NumberOfPEPsMentionedInMessage,folder,"	    //5	
														+ "file, messageID, label, subject, relation, "						//10
														+ "object, currentSentence, clausie, role, allReasons, "			//15
														+ "reasonsInSentence,reasonTermsInMatchedTriple, reasonTermsInNearbySentencesParagraphs, reasonTriplesInNearbySentencesParagraphs, "  //19
														+ "pp,ep,np "
														+ "from results_postprocessed "
														+ "where clausie = ? "  
														+ " order by pep asc, timestamp asc, messageID asc");
	        	stmt0.setString(1, state);
				ResultSet rs0 = stmt0.executeQuery();
				Integer counter=0;
				
				rowNum = 0;
				colNum = 0; 
				//add header
				Row row000 = sheet.createRow(rowNum++);
				Cell cel001 = row000.createCell(colNum++); cel001.setCellValue((String) "PEP"); 		//pep
				Cell cel002 = row000.createCell(colNum++); cel002.setCellValue((String) "State");	//clausie
				Cell cel003 = row000.createCell(colNum++); cel003.setCellValue((String) "Sentence");	//clausie
				
				//reasons
				Cell cel007 = row000.createCell(colNum++); cel007.setCellValue((String) "allReasons");	//allReasons
				Cell cel008 = row000.createCell(colNum++); cel008.setCellValue((String) "reasonsInSentence");	//reasonsInSentence
				Cell cel009 = row000.createCell(colNum++); cel009.setCellValue((String) "reasonTermsInMatchedTriple");	//reasonTermsInMatchedTriple
				Cell cel010 = row000.createCell(colNum++); cel010.setCellValue((String) "reasonTermsInNearbySentencesParagraphs");	//reasonTermsInNearbySentencesParagraphs
				Cell cel011 = row000.createCell(colNum++); cel011.setCellValue((String) "reasonTriplesInNearbySentencesParagraphs");	//reasonTriplesInNearbySentencesParagraphs
				
				Cell cel004 = row000.createCell(colNum++); cel004.setCellValue((String) "Previous Paragraph");	//clausie
				Cell cel005 = row000.createCell(colNum++); cel005.setCellValue((String) "Entire Paragraph");	//clausie
				Cell cel006 = row000.createCell(colNum++); cel006.setCellValue((String) "Next Paragraph");	//clausie
				
				while (rs0.next()) {
					counter++;
					Integer pepNumber = rs0.getInt(1);
					//excel
					Row row = statex.createRow(rowNum++);
					colNum = 0;    		
													    				   				
					//add cell to excel..pepnumber
					Cell cell = row.createCell(colNum++); cell.setCellValue((Integer)  pepNumber); 		//pep
					Cell cel111 = row.createCell(colNum++); cel111.setCellValue((String) rs0.getString(13));	//clausie
					Cell cel112 = row.createCell(colNum++); cel112.setCellValue((String) rs0.getString(12));	//clausie
					
					//reasons
					Cell cel15 = row.createCell(colNum++); cel15.setCellValue((String) rs0.getString(15));	//allReasons
					Cell cel16 = row.createCell(colNum++); cel16.setCellValue((String) rs0.getString(16));	//reasonsInSentence
					Cell cel17 = row.createCell(colNum++); cel17.setCellValue((String) rs0.getString(17));	//reasonTermsInMatchedTriple
					Cell cel18 = row.createCell(colNum++); cel18.setCellValue((String) rs0.getString(18));	//reasonTermsInNearbySentencesParagraphs
					Cell cel19 = row.createCell(colNum++); cel19.setCellValue((String) rs0.getString(19));	//reasonTriplesInNearbySentencesParagraphs
					
					String pp = rs0.getString(20);
					pp = trimStringLessThanThousandChars(pp);
					String ep = rs0.getString(21);
					ep = trimStringLessThanThousandChars(ep);
					String np = rs0.getString(22);
					np = trimStringLessThanThousandChars(np);
					
					Cell cel12 = row.createCell(colNum++); cel12.setCellValue((String) pp);	//pp
					Cell cel13 = row.createCell(colNum++); cel13.setCellValue((String) ep);	//ep
					Cell cel14 = row.createCell(colNum++); cel14.setCellValue((String) np);	//np
				}
				System.out.println("counter "+counter);
	    		System.out.println("Excel Sheet for PEP Reasons is for state ("+state+") created successfully.");
				
			}
    		
    	}
        catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	catch (Exception e){
    			System.out.println("Exception 301 (FILE) " + e.toString()); 
    			System.out.println(StackTraceToString(e)  );
    	}
        
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("Done");
    }

	private static String trimStringLessThanThousandChars(String pp) {
		if(pp ==null || pp.isEmpty()) {
			
		}
		else {
			if(pp.length() >= 1000) {
				pp = pp.substring(0, 1000);
			}
		}
		return pp;
	}
    
    
    public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
    
    public static void main(String[] args) {
    	
    	String FILE_NAME = "c:\\scripts\\MyFirstExcel.xlsx";
        
    	XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Datatypes in Java");
        Object[][] datatypes = {
                {"Datatype", "Type", "Size(in bytes)"},
                {"int", "Primitive", 2},
                {"float", "Primitive", 4},
                {"double", "Primitive", 8},
                {"char", "Primitive", 1},
                {"String", "Non-Primitive", "No fixed size"}
        };
        
        int rowNum = 0;
 //       System.out.println("Creating excel");
        
        for (Object[] datatype : datatypes) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (Object field : datatype) {
                Cell cell = row.createCell(colNum++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }
        }
       
        try {
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Done");
    }
}