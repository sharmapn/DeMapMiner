package GUI;

import javax.swing.JFrame;

import connections.PropertiesFile;
import utilities.ReadFile;

/*
 * Conflict Miner GUI
 *
 * This class follows the same design as Preference_Miner_GUI and
 * Influence_Miner_GUI. It reuses the DeMaP Miner GUI infrastructure through
 * GUI_ElementMethods.
 *
 * Purpose:
 * - Load the normal DeMaP Miner interface.
 * - Reuse proposal timeline, messages, states, roles and database connection.
 * - Later call Conflict Miner extraction classes to identify:
 *   (1) where conflict appears in OSS proposal discussions,
 *   (2) what type of conflict is present,
 *   (3) how intense the conflict is,
 *   (4) what target and stance the conflict involves,
 *   (5) whether objections appear unresolved or aligned with final outcome.
 */
public class Conflict_Miner_GUI extends GUI_ElementMethods {

    public static void main(String[] args) {

        // Reuse DeMaP Miner GUI initialization.
        GUI_ElementMethods g = new GUI_ElementMethods();
        g.initialiseElements();

        // Create the Conflict Miner GUI frame.
        Conflict_Miner_GUI conflictGUI = new Conflict_Miner_GUI();
        conflictGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Read proposal identifier, e.g. pep or bip.
        PropertiesFile wpf = new PropertiesFile();
        proposalIdentifier = wpf.readFromPropertiesFile("proposalIdentifier", false).toLowerCase();

        System.out.println("Conflict Miner started.");
        System.out.println("proposalIdentifier: " + proposalIdentifier);

        // Existing label-file logic retained for compatibility with the older GUI pattern.
        ReadFile rf = new ReadFile();
        String fileName = "c:\\scripts\\pepLabels\\pep.txt";
    }
}
