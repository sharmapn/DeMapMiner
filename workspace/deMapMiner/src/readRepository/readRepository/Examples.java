/*
 * The MIT License
 *
 * Copyright 2015 Thibault Debatty.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package readRepository.readRepository;

import javaStringSimilarity.info.debatty.java.stringsimilarity.CharacterSubstitutionInterface;
import javaStringSimilarity.info.debatty.java.stringsimilarity.Cosine;
import javaStringSimilarity.info.debatty.java.stringsimilarity.Damerau;
import javaStringSimilarity.info.debatty.java.stringsimilarity.Jaccard;
import javaStringSimilarity.info.debatty.java.stringsimilarity.JaroWinkler;
import javaStringSimilarity.info.debatty.java.stringsimilarity.KShingling;
import javaStringSimilarity.info.debatty.java.stringsimilarity.Levenshtein;
import javaStringSimilarity.info.debatty.java.stringsimilarity.LongestCommonSubsequence;
import javaStringSimilarity.info.debatty.java.stringsimilarity.NGram;
import javaStringSimilarity.info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import javaStringSimilarity.info.debatty.java.stringsimilarity.QGram;
import javaStringSimilarity.info.debatty.java.stringsimilarity.SorensenDice;
import javaStringSimilarity.info.debatty.java.stringsimilarity.WeightedLevenshtein;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.*;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;


/**
 *
 * @author Thibault Debatty
 */
public class Examples {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Levenshtein
        // ===========
        Levenshtein levenshtein = new Levenshtein();
        //System.out.println("levenshtein.distance "+ levenshtein.distance("My string", "My $tring"));
        //System.out.println("levenshtein.distance "+ levenshtein.distance("My string", "M string2"));
        //System.out.println("levenshtein.distance "+ levenshtein.distance("My string", "My $tring"));

        System.out.println("levenshtein.distance "+ levenshtein.distance("PEP 30xx: Access to Module/Class/Function Currently	Being Defined (this)", " Access to Current Module/Class/Function)"));
        
        //[Python-Dev] Conditional Expression Resolution
        //[conditional expressions]
        System.out.println("levenshtein.distance "+ levenshtein.distance("[Python-Dev] Conditional Expression Resolution", " [conditional expressions]"));
        System.out.println("levenshtein.distance "+ levenshtein.distance("Conditional Expression ", "[conditional expressions]"));
        System.out.println("levenshtein.distance e "+ levenshtein.distance("new io", "new ht"));
        
        // Jaccard index
        // =============
        Jaccard j2 = new Jaccard(2);
        // AB BC CD DE DF
        // 1  1  1  1  0
        // 1  1  1  0  1
        // => 3 / 5 = 0.6
        System.out.println(j2.similarity("ABCDE", "ABCDF"));

        // Jaro-Winkler
        // ============
        JaroWinkler jw = new JaroWinkler();

        // substitution of s and t : 0.9740740656852722
        System.out.println(jw.similarity("My string", "My tsring"));

        // substitution of s and n : 0.8962963223457336
        System.out.println(jw.similarity("My string", "My ntrisg"));

        // Cosine
        // ======
        Cosine cos = new Cosine(3);

        // ABC BCE
        // 1  0
        // 1  1
        // angle = 45°
        // => similarity = .71
        System.out.println(cos.similarity("ABC", "ABCE"));

        cos = new Cosine(2);
        // AB BA
        // 2  1
        // 1  1
        // similarity = .95
        System.out.println(cos.similarity("ABAB", "BAB"));

        // Damerau
        // =======
        Damerau damerau = new Damerau();

        // 1 substitution
        System.out.println(damerau.distance("ABCDEF", "ABDCEF"));

        // 2 substitutions
        System.out.println(damerau.distance("ABCDEF", "BACDFE"));

        // 1 deletion
        System.out.println(damerau.distance("ABCDEF", "ABCDE"));
        System.out.println(damerau.distance("ABCDEF", "BCDEF"));

        System.out.println(damerau.distance("ABCDEF", "ABCGDEF"));

        // All different
        System.out.println(damerau.distance("ABCDEF", "POIU"));

        // Longest Common Subsequence
        // ==========================
        LongestCommonSubsequence lcs = new LongestCommonSubsequence();

        // Will produce 4.0
        System.out.println(lcs.distance("AGCAT", "GAC"));

        // Will produce 1.0
        System.out.println(lcs.distance("AGCAT", "AGCT"));

        // NGram
        // =====
        // produces 0.416666
        NGram twogram = new NGram(2);
        System.out.println(twogram.distance("ABCD", "ABTUIO"));

        System.out.println("ngram");
        
        // produces 0.97222
        //String s1 = "PEP 201 has been accepted by the BDFL";
        //String s2 = "PEP 201 is accepted by BDFL";
        String s1 = "PEP 30xx: Access to Module/Class/Function Currently Being Defined (this)";
        String s2 = "Access to Current Module/Class/Function";
        NGram ngram = new NGram(4);
        System.out.println(ngram.distance(s1, s2));
        System.out.println("end ngram");
        
        float diff = parse2(s2, s1);
        System.out.println("similarity= " + diff);

        
        // Normalized Levenshtein
        // ======================
        NormalizedLevenshtein l = new NormalizedLevenshtein();

        System.out.println(l.distance("My string", "My $tring"));
        System.out.println(l.distance("My string", "M string2"));
        System.out.println(l.distance("My string", "abcd"));

        // QGram
        // =====
        QGram dig = new QGram(2);

        // AB BC CD CE
        // 1  1  1  0
        // 1  1  0  1
        // Total: 2
        System.out.println(dig.distance("ABCD", "ABCE"));

        System.out.println(dig.distance("", "QSDFGHJKLM"));
        
        System.out.println("-----");
        
        System.out.println(dig.distance(
        		"Access to Current Module/Class/Function",
                "PEP 30xx: Access to Module/Class/Function Currently Being Defined (this)"));
                
        
        System.out.println("-----");

        // Sorensen-Dice
        // =============
        SorensenDice sd = new SorensenDice(2);

        // AB BC CD DE DF FG
        // 1  1  1  1  0  0
        // 1  1  1  0  1  1
        // => 2 x 3 / (4 + 5) = 6/9 = 0.6666
        System.out.println(sd.similarity("ABCDE", "ABCDFG"));

        // Weighted Levenshtein
        // ====================
        WeightedLevenshtein wl = new WeightedLevenshtein(
                new CharacterSubstitutionInterface() {
                    public double cost(char c1, char c2) {

                        // The cost for substituting 't' and 'r' is considered
                        // smaller as these 2 are located next to each other
                        // on a keyboard
                        if (c1 == 't' && c2 == 'r') {
                            return 0.5;
                        }

                        // For most cases, the cost of substituting 2 characters
                        // is 1.0
                        return 1.0;
                    }
                });

        System.out.println(wl.distance("String1", "Srring2"));
        
        // K-Shingling
        s1 = "my string,  \n  my song";
        s2 = "another string, from a song";
        KShingling ks = new KShingling(4);
        System.out.println(ks.getProfile(s1));
        System.out.println(ks.getProfile(s2));
        
        ks = new KShingling(2);
        System.out.println(ks.getProfile("ABCAB"));
        
    }
    
   // @Test						//title		//sentence
    public static float parse2(String title, String sentence) {
//		System.out.println("title= " + title);
//		System.out.println("sentence= " + sentence);
		//approach a
		title= title.trim();
		sentence = sentence.trim();
		//for all terms in sentence
		Integer termFoundCounter=0;
		if (sentence!=null ){
//			System.out.print("Found ");
			//for (String sentenceTerm: sentence.split("\\s+")){
				for (String titleTerm: title.split(" ")){					
					if (sentence.contains(titleTerm.trim())){
						termFoundCounter++;
//						System.out.print("[" + titleTerm+"] ");
					}
				}
			//}
		}
//		System.out.println();
//		System.out.println("termFoundCounter= " + (float) termFoundCounter);
//		System.out.println("title.length()= " + (float) title.split(" ").length);
		float diff = 100* ( (float)termFoundCounter/ (float) title.split(" ").length);   
//		System.out.println("similarity= " + diff);
		//System.out.println("similarity= " +  100* ( (float)termFoundCounter/ (float) title.split(" ").length)   );
		
		return diff;
    }
	public static void parse(String title, String sentence) {
		System.out.println("title= " + title);
		System.out.println("sentence= " + sentence);
		//approach a
		//for all terms in sentence
		Integer termFoundCounter=0;
		if (sentence!=null ){
			System.out.print("Found ");
			//for (String sentenceTerm: sentence.split("\\s+")){
				for (String titleTerm: title.split(" ")){					
					if (sentence.contains(titleTerm.trim())){
						termFoundCounter++;
						System.out.print("[" + titleTerm+"] ");
					}
				}
			//}
		}
		System.out.println();
		System.out.println("termFoundCounter= " + (float) termFoundCounter);
		System.out.println("title.length()= " + (float) title.split(" ").length);
		float diff = ( (float) termFoundCounter/ (float) title.length());
		System.out.println("similarity= " + diff);
		
		
		String test = "PEP 30xx: Access to Module/Class/Function Currently	Being Defined (this)";
		
		if (test.contains("Current"))
		{
			System.out.println("contains= ");
		}
		
		//------------------new approach
//		List<String> stringList = new ArrayList<String>();
//        stringList.add(sentence);
//        String val = new String(" Current");
//        if (stringList.contains(val)) {
//            System.out.println("The value is in there");
//        } else {
//            System.out.println("There's no such value here");
//        }		
		
		//approach b
		String s1 = title;
		String s2 = sentence;
		
		
		Float min;
		if (s1.length() < s2.length())
			min = (float) s1.split("\\s+").length;
		else
			min = (float) s2.split("\\s+").length;
		
		Splitter splitter = Splitter.onPattern(" ").trimResults().omitEmptyStrings();

		Set<String> ss1 = Sets.newHashSet(splitter.split(s1));
		Set<String> ss2 = Sets.newHashSet(splitter.split(s2));
		
		Set<String> intersection = Sets.intersection(ss1, ss2);		
		System.out.println(intersection);
		
		System.out.println("i " + intersection.size());
		System.out.println("min " + min); 
		
		System.out.println("Percentage same for shorter string: "  + (intersection.size()/min)*100 );
	}

}
