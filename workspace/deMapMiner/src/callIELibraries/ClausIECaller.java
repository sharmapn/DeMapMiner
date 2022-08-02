package callIELibraries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.mpii.clausie.ClausIE;
import de.mpii.clausie.Clause;
import de.mpii.clausie.Proposition;
import utilities.ReadFileLinesIntoArray;
import de.mpii.clause.driver.*;

public class ClausIECaller {
	ClausIEMain cm;
	
    public ClausIECaller(){
    	 cm = new ClausIEMain();
    	 
    }
    
    public String call(String sentence,  String subject, String  verb, String  object) throws IOException 
    {
    	return cm.checkProposition( sentence,  subject,  verb,  object);
    
    }
}
