package miner.models;

import java.io.IOException;

import de.mpii.clausie.ClausIE;
import de.mpii.clausie.Clause;
import de.mpii.clausie.Proposition;

public class Example {
    public static void main(String[] args) throws IOException {
    	
        System.out.print("*** Starting ***123445675454");
    	
        ClausIE clausIE = new ClausIE();
        clausIE.initParser();
        clausIE.getOptions().print(System.out, "# ");

       
    }
}
