package GUI;

import javax.swing.JFrame;

import connections.PropertiesFile;
import utilities.ReadFile;

/*
 * Influence Miner GUI
 *
 * This class follows the same design pattern as Rationale Miner and Preference Miner.
 * It reuses the DeMaP Miner GUI infrastructure through GUI_ElementMethods.
 *
 * Purpose:
 * - Load the normal DeMaP Miner interface.
 * - Reuse proposal timelines, states, messages, roles, and database connection.
 * - Later call Influence Miner extraction classes to identify strategic,
 *   operational, and functional influence statements in proposal discussions.
 */
public class Influence_Miner_GUI extends GUI_ElementMethods {

    public static void main(String[] args) {

        GUI_ElementMethods g = new GUI_ElementMethods();
        g.initialiseElements();

        Influence_Miner_GUI influenceGUI = new Influence_Miner_GUI();
        influenceGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        PropertiesFile wpf = new PropertiesFile();
        proposalIdentifier = wpf.readFromPropertiesFile("proposalIdentifier", false).toLowerCase();

        System.out.println("Influence Miner started.");
        System.out.println("proposalIdentifier: " + proposalIdentifier);

        ReadFile rf = new ReadFile();
        String fileName = "c:\\scripts\\pepLabels\\pep.txt";
    }
}
