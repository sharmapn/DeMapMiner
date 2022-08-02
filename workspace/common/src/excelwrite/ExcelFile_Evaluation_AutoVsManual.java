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

public class ExcelFile_Evaluation_AutoVsManual {
	
	static XSSFWorkbook workbook;	static XSSFSheet sheet, sheetUnique;
	static String filename="";
	static int rowNum= 0, rowNumUnique=0; 
	
    public static void initialiseExcelFile(String v_fileName) {
    	workbook = new XSSFWorkbook();	
    	sheet = workbook.createSheet("Evaluation Results");	  sheetUnique = workbook.createSheet("Evaluation Unique Results");	       
    	filename = v_fileName;     	 System.out.println("Creating excel");  // for Post Process Results
    }
	
    public static void writeEvaluationResultsToExcelFile(Integer proposal,Integer messageID, String state, String label, String manualSentence, String autoSentence, Integer ranking,String location, double percentMatch,
    		double sentenceHintProbablity,double sentenceLocationHintProbability,double messageSubjectHintProbablityScore,double dateDiffProbability, double authorRoleProbability,double negationTermPenalty) {
//    	XSSFCellStyle style=workbook.createCellStyle();//    	style.setBorderBottom(XSSFCellStyle.);
//    	style.setBorderTop(XSSFCellStyle.BORDER_THIN);//    	style.setBorderRight(XSSFCellStyle.BORDER_THIN);
//    	style.setBorderLeft(XSSFCellStyle.BORDER_THIN);   
    	Row row;      
        try	{
        	int colNum = 0;
        	row = sheet.createRow(rowNum++);
        	
			//add cell to excel..pepnumber
			Cell cell = row.createCell(colNum++); cell.setCellValue((Integer)  proposal);			Cell cel2 = row.createCell(colNum++); cel2.setCellValue((Integer)  messageID);			
			Cell cel3 = row.createCell(colNum++); cel3.setCellValue((String)   state);
			Cell cel4 = row.createCell(colNum++); cel4.setCellValue((String)   label);				Cell cel5 = row.createCell(colNum++); cel5.setCellValue((String)   manualSentence);	
			Cell cel6 = row.createCell(colNum++); cel6.setCellValue((String)   autoSentence); 		Cell cel7 = row.createCell(colNum++); cel7.setCellValue((Integer)  ranking); 
			Cell cel8 = row.createCell(colNum++); cel8.setCellValue((String)   location);			Cell cel9 = row.createCell(colNum++); cel9.setCellValue((double)  percentMatch); 			
			Cell cel10 = row.createCell(colNum++); cel10.setCellValue((double)  sentenceHintProbablity);	Cell cel11 = row.createCell(colNum++); cel11.setCellValue((double)  sentenceLocationHintProbability); 
			Cell cel12 = row.createCell(colNum++); cel12.setCellValue((double)  messageSubjectHintProbablityScore); Cell cel13 = row.createCell(colNum++); cel13.setCellValue((double)  dateDiffProbability); 
			Cell cel14 = row.createCell(colNum++); cel14.setCellValue((double)  authorRoleProbability); 		Cell cel15 = row.createCell(colNum++); cel15.setCellValue((double)  negationTermPenalty); 
			//rowNum++;
    		//System.out.println("Excel File for "+ pepTypeToFilter + " is created successfully.");
    	}  	catch (Exception e){
    			System.out.println("Exception 2219 (FILE) " + e.toString()); 
    			System.out.println(StackTraceToString(e)  );
    	}  //System.out.println("Done");
    }
    public static void writeEvaluationResultsToExcelFile_UniqueRows(Integer proposal,Integer messageID, String state, String label, String manualSentence, String autoSentence, Integer ranking,String location, double percentMatch,
    		double sentenceHintProbablity,double sentenceLocationHintProbability,double messageSubjectHintProbablityScore,double dateDiffProbability, double authorRoleProbability,double negationTermPenalty) {
    	Row row;      
        try	{
        	int colNum = 0;
        	row = sheetUnique.createRow(rowNumUnique++);        	
			//add cell to excel..pepnumber
			/*
        	Cell cell = row.createCell(colNum++); cell.setCellValue((Integer)  proposal); 				Cell cel2 = row.createCell(colNum++); cel2.setCellValue((String)   state);
			Cell cel3 = row.createCell(colNum++); cel3.setCellValue((String)   label);					Cell cel4 = row.createCell(colNum++); cel4.setCellValue((String)   manualSentence);	
			Cell cel5 = row.createCell(colNum++); cel5.setCellValue((String)   autoSentence);			Cell cel6 = row.createCell(colNum++); cel6.setCellValue((Integer)  ranking); 
			Cell cel7 = row.createCell(colNum++); cel7.setCellValue((String)   location);				Cell cel8 = row.createCell(colNum++); cel8.setCellValue((double)  percentMatch); 
			Cell cel9 = row.createCell(colNum++); cel9.setCellValue((double)  sentenceHintProbablity);	Cell cel10 = row.createCell(colNum++); cel10.setCellValue((double)  sentenceLocationHintProbability); 
			Cell cel11 = row.createCell(colNum++); cel11.setCellValue((double)  messageSubjectHintProbablityScore);			Cell cel12 = row.createCell(colNum++); cel12.setCellValue((double)  dateDiffProbability); 
			Cell cel13 = row.createCell(colNum++); cel13.setCellValue((double)  authorRoleProbability); Cell cel14 = row.createCell(colNum++); cel14.setCellValue((double)  negationTermPenalty); 
			*/
        	Cell cell = row.createCell(colNum++); cell.setCellValue((Integer)  proposal);			Cell cel2 = row.createCell(colNum++); cel2.setCellValue((Integer)  messageID);			
			Cell cel3 = row.createCell(colNum++); cel3.setCellValue((String)   state);
			Cell cel4 = row.createCell(colNum++); cel4.setCellValue((String)   label);				Cell cel5 = row.createCell(colNum++); cel5.setCellValue((String)   manualSentence);	
			Cell cel6 = row.createCell(colNum++); cel6.setCellValue((String)   autoSentence); 		Cell cel7 = row.createCell(colNum++); cel7.setCellValue((Integer)  ranking); 
			Cell cel8 = row.createCell(colNum++); cel8.setCellValue((String)   location);			Cell cel9 = row.createCell(colNum++); cel9.setCellValue((double)  percentMatch); 			
			Cell cel10 = row.createCell(colNum++); cel10.setCellValue((double)  sentenceHintProbablity);	Cell cel11 = row.createCell(colNum++); cel11.setCellValue((double)  sentenceLocationHintProbability); 
			Cell cel12 = row.createCell(colNum++); cel12.setCellValue((double)  messageSubjectHintProbablityScore); Cell cel13 = row.createCell(colNum++); cel13.setCellValue((double)  dateDiffProbability); 
			Cell cel14 = row.createCell(colNum++); cel14.setCellValue((double)  authorRoleProbability); 		Cell cel15 = row.createCell(colNum++); cel15.setCellValue((double)  negationTermPenalty); 
        	
        	
        	
			//rowNum+;
    		//System.out.println("Excel File for "+ pepTypeToFilter + " is created successfully.");
    	}  	catch (Exception e){
    			System.out.println("Exception 2219b (FILE) " + e.toString()); 
    			System.out.println(StackTraceToString(e)  );
    	}  //System.out.println("Done");
    }
    public void writeEvaluationResultsHeaderToExcelFile(String proposal,String MID, String state, String label, String manualSentence, String autoSentence, String ranking,String location, String percentMatch,
    		String sentenceHintProbablity,String sentenceLocationHintProbability,String messageSubjectHintProbablityScore,String dateDiffProbability,String authorRoleProbability,
    		String negationTermPenalty) {
        try	{
	        	int colNum = 0;
				Row row = sheet.createRow(rowNum++);				
				//add cell to excel..pepnumber
				Cell cell = row.createCell(colNum++); cell.setCellValue((String)  proposal);				
				Cell cel2 = row.createCell(colNum++); cel2.setCellValue((String)   MID);
				Cell cel3 = row.createCell(colNum++); cel3.setCellValue((String)   state);
				Cell cel4 = row.createCell(colNum++); cel4.setCellValue((String)   label);					Cell cel5 = row.createCell(colNum++); cel5.setCellValue((String)   manualSentence);	
				Cell cel6 = row.createCell(colNum++); cel6.setCellValue((String)   autoSentence);			Cell cel7 = row.createCell(colNum++); cel7.setCellValue((String)  ranking); 
				Cell cel8 = row.createCell(colNum++); cel8.setCellValue((String)   location);				Cell cel9 = row.createCell(colNum++); cel9.setCellValue((String)  percentMatch); 
				Cell cel10 = row.createCell(colNum++); cel10.setCellValue((String)  sentenceHintProbablity);	Cell cel11 = row.createCell(colNum++); cel11.setCellValue((String)  sentenceLocationHintProbability); 
				Cell cel12 = row.createCell(colNum++); cel12.setCellValue((String)  messageSubjectHintProbablityScore);	Cell cel13 = row.createCell(colNum++); cel13.setCellValue((String)  dateDiffProbability); 
				Cell cel14 = row.createCell(colNum++); cel14.setCellValue((String)  authorRoleProbability); Cell cel15 = row.createCell(colNum++); cel15.setCellValue((String)  negationTermPenalty); 
        	
			//rowNum++;
    		//System.out.println("Excel File for "+ pepTypeToFilter + " is created successfully.");
    	}  	catch (Exception e){
    			System.out.println("Exception 2219a (FILE) " + e.toString()); 
    			System.out.println(StackTraceToString(e)  );
    	}  //System.out.println("Done");
    }
    public void writeEvaluationResultsHeaderToExcelFile_UniqueRows(String proposal,String MID, String state, String label, String manualSentence, String autoSentence, String ranking,String location, String percentMatch,
    		String sentenceHintProbablity,String sentenceLocationHintProbability,String messageSubjectHintProbablityScore,String dateDiffProbability,String authorRoleProbability,
    		String negationTermPenalty) {
        try	{
	        	int colNum = 0;
				Row row = sheetUnique.createRow(rowNumUnique++);				
				//add cell to excel..pepnumber
				Cell cell = row.createCell(colNum++); cell.setCellValue((String)  proposal); 				
				Cell cel2 = row.createCell(colNum++); cel2.setCellValue((String)  MID); 
				Cell cel3 = row.createCell(colNum++); cel3.setCellValue((String)   state);
				Cell cel4 = row.createCell(colNum++); cel4.setCellValue((String)   label);					Cell cel5 = row.createCell(colNum++); cel5.setCellValue((String)   manualSentence);	
				Cell cel6 = row.createCell(colNum++); cel6.setCellValue((String)   autoSentence);			Cell cel7 = row.createCell(colNum++); cel7.setCellValue((String)  ranking); 
				Cell cel8 = row.createCell(colNum++); cel8.setCellValue((String)   location);				Cell cel9 = row.createCell(colNum++); cel9.setCellValue((String)  percentMatch); 
				Cell cel10 = row.createCell(colNum++); cel10.setCellValue((String)  sentenceHintProbablity);	Cell cel11 = row.createCell(colNum++); cel11.setCellValue((String)  sentenceLocationHintProbability); 
				Cell cel12 = row.createCell(colNum++); cel12.setCellValue((String)  messageSubjectHintProbablityScore);	Cell cel13 = row.createCell(colNum++); cel13.setCellValue((String)  dateDiffProbability); 
				Cell cel14 = row.createCell(colNum++); cel14.setCellValue((String)  authorRoleProbability); Cell cel15 = row.createCell(colNum++); cel15.setCellValue((String)  negationTermPenalty); 
        	
			//rowNum++;
    		//System.out.println("Excel File for "+ pepTypeToFilter + " is created successfully.");
    	}  	catch (Exception e){
    			System.out.println("Exception 2219a (FILE) " + e.toString()); 
    			System.out.println(StackTraceToString(e)  );
    	}  //System.out.println("Done");
    }
    
    public void writeAndCloseFile() {
    	 try {
             FileOutputStream outputStream = new FileOutputStream(filename);
             workbook.write(outputStream);             
             workbook.close();
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }
    
	private static String trimStringLessThanThousandChars(String pp) {
		if(pp ==null || pp.isEmpty()) {}
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
    	XSSFWorkbook workbook = new XSSFWorkbook();        XSSFSheet sheet = workbook.createSheet("Datatypes in Java");
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