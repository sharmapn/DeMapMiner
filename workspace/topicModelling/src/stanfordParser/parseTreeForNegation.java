package stanfordParser;

import java.io.*;
import java.util.*;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.TreeCoreAnnotations.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.util.*;
public class parseTreeForNegation {
    public static void main (String[] args) throws IOException {
        // build pipeline
    	
    	String negation[] = {"if","once"};
        Properties props = new Properties();
        props.setProperty("annotators","tokenize, ssplit, pos, lemma, ner, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        //String text1 = "We were offered water for the table but were not told the Voss bottles of water were $8 a piece.";
        String text = "To some extent , anything else does n't match the oficial PEP process where the PEP is modified to match the proposed solution , and then the PEP is accepted or rejected once it 's finalized and ready for a decision .";
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);
        List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            Tree tree = sentence.get(TreeAnnotation.class);
            List<Tree> leaves = new ArrayList<>();
            leaves = tree.getLeaves(leaves);
            
            //display entire tree
            //printAllRootToLeafPaths(tree, new ArrayList<String>());
            
            for (Tree leave : leaves) {		//treat every tree as a leave
                String compare = leave.toString().toLowerCase();	//check leave 
                for(String neg: negation) {
	                if(compare.equals(neg) == true) {
	                    //System.out.println(tree);
	                    //System.out.println("---");
	                    System.out.println(leave);
	                    //System.out.println(leave.parent(tree));
	                    //System.out.println(    (  (  leave.parent(tree)  ).parent(tree) ).parent(tree)    );
	                    
	                    Tree curr = leave.parent(tree).parent(tree).parent(tree);
	                    List<String> nodes = printAllRootToLeafPaths(curr, new ArrayList<String>());
	                    System.out.println("displaying each node with negation");
	                    for (String node : nodes) {
	//                    	String node = test.toString().toLowerCase();
	                    	System.out.println(node.toString());
	                    }
	                    
	//                    while (curr != null) {
	                     //  System.out.println(curr.toString().toLowerCase());
	//                        curr = curr.parent(); // will be null if no parent
	//                    }
	                    
	//                    for (Tree test : curr) {
	//                    	String node = test.toString().toLowerCase();
	//                    	System.out.println(test);
	//                    }
	//                    
	                }
                }
            }
        }
    }
    
    private static List<String> printAllRootToLeafPaths(Tree tree, List<String> path) {
        if(tree != null) {
            if(tree.isLeaf()) {
                path.add(tree.nodeString());
            }
            if(tree.children().length == 0) {
              //  System.out.println(path.toString().replace("[", "").replace("]", ""));
            } else {
                for(Tree child : tree.children()) {
                    printAllRootToLeafPaths(child, path);
                }
            }
            //path.remove(tree.nodeString());
        }
        return path;
    }
}



