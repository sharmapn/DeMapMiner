package miner.processLabels;
import java.lang.Math; // headers MUST be above the first class
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
//import org.apache.commons.lang.ArrayUtils;
import java.util.Collections;
// one class needs to have a main() method
public class HelloWorld
{
  // arguments are passed using the text field below this editor
  public static void main(String[] args)
  {        
    String pos = "before consensus was reached", s = "consensus", v = "was reached", o="";
    String negationTerms[] = {"if","not","do not","will be","can","can be","is n't","nor","should","should be","would","needs to be","may","will","hav n't","have n't","might", "n't","whether","zero","no","never","never been","need","would need","before","zero"}; // added later for test												// "should","should be"
	
    if(checkClauseContainsAllSVO_IfNegationJustPrecedesSVOTermsInSameClause(pos,s,v,o,negationTerms)) 
      System.out.println("\n\n\t\t  negation found");    
    else 
      System.out.println("\n\n\t\t  no negation found");
  }
  
  public static boolean checkClauseContainsAllSVO_IfNegationJustPrecedesSVOTermsInSameClause(String pos,String s, String v, String o,String [] v_negationTerms){
		System.out.println("\t\t oooooo  clauseContainsAll() pos ("+ pos+ ") s " +s +" v "+ v+ " o "+o );
		String[] posStr =null,vStr=null,sStr=null,oStr=null;		
		boolean negationFound=false;
		List<String> toCheckList=  new ArrayList<String>();
		
		//deal != ideal therefore we use equals function below
		if (pos == "null" || pos == null || pos.length() == 0){
			return false;
		}
		else{
			if (pos.contains(" ")){
				posStr = pos.toLowerCase().split(" ");
			}
			else{	//sentence may not have space, so therefore just one logical term and in this case return false
				return false;
			}
			if (s.contains(" ")){
				sStr = s.toLowerCase().split(" ");
				 // Add all elements to the ArrayList from an array.
		        Collections.addAll(toCheckList, sStr);
			}
			else{	//sentence may not have space, so therefore just one logical term and in this case return false
				toCheckList.add(s);
			}
			if (v.contains(" ")){
				vStr = v.toLowerCase().split(" ");
				Collections.addAll(toCheckList, vStr);
			}
			else{	//sentence may not have space, so therefore just one logical term and in this case return false
				toCheckList.add(v);
			}
				 //System.out.println("sStr "+ sStr + " vStr" +vStr);
		}
		if (o.isEmpty() || o.equals("")|| o == null || o.length() == 0){
			 System.out.println("object empty");
			 negationFound=checkIfNegationPrecedesSVOTerms(pos, v_negationTerms, posStr, toCheckList);	
		}
		else {
			if (o.contains(" ")){	//split object
				oStr = o.toLowerCase().split(" ");
				Collections.addAll(toCheckList, oStr);
			}
			else{	//sentence may not have space, so therefore just one logical term and in this case return false
				toCheckList.add(o);
			}
			 //first check..check if posStr contains the s,v
			negationFound=checkIfNegationPrecedesSVOTerms(pos, v_negationTerms, posStr, toCheckList);		
		}
		return negationFound;
	}

	private static boolean checkIfNegationPrecedesSVOTerms(String pos, String[] v_negationTerms, String[] posStr,List<String> toCheckList) {
		if( Arrays.asList(posStr).containsAll(toCheckList)) {
			//now check if negation precedes the pos
			for (String ns : v_negationTerms) {
				if(Arrays.asList(posStr).contains(ns)) {		//if pos string contains negation
					System.out.println("Negation term found in posStr: "+ ns);
					//check text after negation to see if it contains all terms of double triple
					String textAfterNegation = pos.split(ns)[1].trim();	//extract text after negation
					System.out.println("textAfterNegation: " + textAfterNegation);
					if(textAfterNegation.contains(" ")){	//make sure not null
						String termsAfterNegation[] = textAfterNegation.split(" ");
						System.out.println("termsAfterNegation " + termsAfterNegation[0] + " " +termsAfterNegation[1]);
						
							if(Arrays.asList(termsAfterNegation).containsAll(toCheckList)){	//text after negation contains all terms of double triple
								System.out.println("Negation term found ("+ns+") before triple terms in same clause: " + pos);
								return true;	//return true if all double or triple terms found after negation term
							}
					}		
				}
			}
		}
		return false;
	}
  
//  public static Boolean checkIfNegationJustPrecedes_TripleOrDouble_TermsInSameClause(String [] v_negationTerms, String v_extractedPOS,String s, String v, String o){		
//	  	System.out.println("Inside checkIfNegationJustPrecedes_TripleOrDouble_TermsInSameClause() s:" + s + " v " + v + " o "+o);
//	  	//new code
//	  	List<String> toCheckSVList=  new ArrayList<String>();
//	  	List<String> toCheckSVOList=  new ArrayList<String>();
//	  	toCheckSVList.add(s); toCheckSVList.add(v);
//	  	toCheckSVOList.add(s);	toCheckSVOList.add(v);	toCheckSVOList.add(o);
//		String[] extractedPOSTerms  = v_extractedPOS.toLowerCase().split(" ");
//		String[] vStr=null,sStr=null,oStr=null;		
//		boolean found=false;
//
//			for (String ns : v_negationTerms) {
//				//String negTerm[] = {ns}; 
//				if(Arrays.asList(extractedPOSTerms).contains(ns)) {		
//					System.out.println("Negation term found: "+ ns);
//					//check text after negation to see if it contains all terms of double triple
//					String textAfterNegation = v_extractedPOS.split(ns)[1].trim();	//extract text after negation
//					System.out.println("textAfterNegation: " + textAfterNegation);
//					if(textAfterNegation.contains(" ")){	//make sure not null
//						String termsAfterNegation[] = textAfterNegation.split(" ");
//						System.out.println("termsAfterNegation " + termsAfterNegation[0] + " " +termsAfterNegation[1]);
//						if (o.isEmpty() || o.equals("")|| o == null || o.length() == 0){
//							System.out.println("Negation term found ("+ns+") before double terms in same clause: " + v_extractedPOS);
//							//found = Arrays.asList(termsAfterNegation).contains(s) && Arrays.asList(termsAfterNegation).contains(v);
//							found = Arrays.asList(termsAfterNegation).containsAll(toCheckSVList);
//							if(found) {
//								System.out.println("found " + found);
//							}
//							else {
//								System.out.println(" not found " + found);
//							}
//						}					
//						else{
//							if(Arrays.asList(termsAfterNegation).containsAll(toCheckSVOList))		
//							{
//								System.out.println("Negation term found ("+ns+") before triple terms in same clause: " + v_extractedPOS);
//								return true;	//return true if all double or triple terms found after negation term
//							}
//						}
//					}		
//				}
//			}	
//			//if no negation found
//			return false;
//		}
  
}
// you can add other public classes to this editor in any order