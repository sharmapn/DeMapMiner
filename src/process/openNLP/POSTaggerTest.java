package Process.openNLP;

import org.junit.Test;

/**
 * Created by only2dhir on 11-07-2017.
 */

public class POSTaggerTest {
    
    public static void main(String[] args) {
        POSTaggingExample tagging = new POSTaggingExample();
        tagging.tag("If you have several test classes, you can combine them into a test suite. Running a test suite executes all test classes in that suite in the specified order. A test suite can also contain other test suites");
    }
}
