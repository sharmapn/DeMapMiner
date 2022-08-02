package GUI;

import java.awt.Color;

import javax.swing.JFrame;


import connections.PropertiesFile;
import utilities.ReadFile;

//21-July-2022 This class calls the 'GUI_ElementMethods.java' class in the DeMaP Miner project
//Note: have to set the `gettingScreenshotForSubstatesOnly = true;' in GUI_Elements.java

public class Rationale_Miner_GUI extends GUI_ElementMethods {	
	public static void main(String[] args) {
		//since the gui elements are initialised already, here we just call the methods/operations on them
		GUI_ElementMethods g = new GUI_ElementMethods();
		g.initialiseElements();
		
//$$	allStatesSubStates = l.getAllStatesList_StatesSubstates();
		
		Rationale_Miner_GUI exRepGUI = new Rationale_Miner_GUI();	
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