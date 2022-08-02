package callIELibraries;

import java.io.File;
import java.net.MalformedURLException;

import edu.knowitall.ollie.Ollie;
import edu.knowitall.ollie.OllieExtraction;
import edu.knowitall.ollie.OllieExtractionInstance;
import edu.knowitall.tool.parse.MaltParser;
import edu.knowitall.tool.parse.graph.DependencyGraph;

/** This is an example class that shows one way of using Ollie from Java. */
public class JavaOllieWrapper {
    // the extractor itself
    private static Ollie ollie;

    // the parser--a step required before the extractor
    private static MaltParser maltParser;

    // the path of the malt parser model file
    private static final String MALT_PARSER_FILENAME = "C:\\lib\\engmalt.linear-1.7.mco";

    public JavaOllieWrapper() throws MalformedURLException {
        // initialize MaltParser
        scala.Option<File> nullOption = scala.Option.apply(null);
        //maltParser = new MaltParser(new File(MALT_PARSER_FILENAME).toURI().toURL(), nullOption);
        maltParser = new MaltParser(new File("C:\\lib\\engmalt.linear-1.7.mco"));
        
        
        
        // initialize Ollie
        ollie = new Ollie();
    }

    /**
     * Gets Ollie extractions from a single sentence.
     * @param sentence
     * @return the set of ollie extractions
     */
    public static Iterable<OllieExtractionInstance> extract(String sentence) {
        // parse the sentence
        DependencyGraph graph = maltParser.dependencyGraph(sentence);

        // run Ollie over the sentence and convert to a Java collection
        Iterable<OllieExtractionInstance> extrs = scala.collection.JavaConversions.asJavaIterable(ollie.extract(graph));
        return extrs;
    }
    
    public static void computOllie(String sentence) throws MalformedURLException {
        System.out.println(JavaOllieWrapper.class.getResource("/logback.xml"));
        // initialize
        //JavaOllieWrapper ollieWrapper = new JavaOllieWrapper();
        System.out.println("here 23");
        // extract from a single sentence.
        //String sentence = "President Obama will meet with Congressional leaders on Friday, and House Republicans summoned lawmakers back for a Sunday session, in a last-ditch effort to avert a fiscal crisis brought on by automatic tax increases and spending cuts scheduled to hit next week.";
        
        //String sentence = "I made a conscious decision to do that, and I'm a bit alarmed at this decision being overridden at the last moment with no debate.";
        
        Iterable<OllieExtractionInstance> extrs = extract(sentence);
        

        // print the extractions.
        for (OllieExtractionInstance inst : extrs) {
            OllieExtraction extr = inst.extr();
            System.out.println("Arg1: " + extr.arg1().text()+"\t Rel: "+extr.rel().text()+"\t Arg 2:"+extr.arg2().text());
        }
    }

    public static void main(String args[]) throws MalformedURLException {
        System.out.println(JavaOllieWrapper.class.getResource("/logback.xml"));
        // initialize
        JavaOllieWrapper ollieWrapper = new JavaOllieWrapper();
        System.out.println("here");
        // extract from a single sentence.
        //String sentence = "President Obama will meet with Congressional leaders on Friday, and House Republicans summoned lawmakers back for a Sunday session, in a last-ditch effort to avert a fiscal crisis brought on by automatic tax increases and spending cuts scheduled to hit next week.";
        
        String sentence = "I made a conscious decision to do that, and I'm a bit alarmed at this decision being overridden at the last moment with no debate.";
        
        Iterable<OllieExtractionInstance> extrs = ollieWrapper.extract(sentence);
        

        // print the extractions.
        for (OllieExtractionInstance inst : extrs) {
            OllieExtraction extr = inst.extr();
            System.out.println("Arg1: " + extr.arg1().text()+"\t Rel: "+extr.rel().text()+"\t Arg 2:"+extr.arg2().text());
        }
    }
}
