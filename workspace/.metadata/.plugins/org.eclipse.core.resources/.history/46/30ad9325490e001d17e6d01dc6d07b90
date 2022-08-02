package GUI;

import java.awt.Color;

import javax.swing.JFrame;


import connections.PropertiesFile;
import utilities.ReadFile;

public class DeMAP_Miner_GUI extends GUI_ElementMethods {

//Ensure that the 'gettingScreenshotForSubstatesOnly' = true in GUI_Elements.java. Change the following line.
//public static boolean gettingScreenshotForSubstatesOnly = false;	
	public static void main(String[] args) {
		//since the gui elements are initialised already, here we just call the methods/operations on them
		GUI_ElementMethods g = new GUI_ElementMethods();
		g.initialiseElements();
		
//$$		allStatesSubStates = l.getAllStatesList_StatesSubstates();
		
		DeMAP_Miner_GUI exRepGUI = new DeMAP_Miner_GUI();	
		exRepGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//Ollie initialise		//JavaOllieWrapperGUI();

		PropertiesFile wpf = new PropertiesFile();	//Read from properties file
		proposalIdentifier = wpf.readFromPropertiesFile("proposalIdentifier",false).toLowerCase();	System.out.println("proposalIdentifier: " +proposalIdentifier);

		ReadFile rf = new ReadFile();	//add states from file to array
		String fileName = "c:\\scripts\\pepLabels\\pep.txt";
		//$$		terms = rf.readKeywordsFromFile(fileName);
		//statesAndTerms = ObjectArrays.concat(states, terms, String.class);		
	}
}