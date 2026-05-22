package GUI;

import javax.swing.JFrame;

import connections.PropertiesFile;
import utilities.ReadFile;

/*
 * Preference Miner GUI
 *
 * This class follows the same design as Rationale_Miner_GUI.
 * It reuses the DeMaP Miner GUI infrastructure through GUI_ElementMethods.
 *
 * Purpose:
 * - Load the normal DeMaP Miner interface.
 * - Reuse proposal timeline, messages, states, roles and database connection.
 * - Later call Preference Miner extraction classes to identify:
 *   (1) who expressed a preference,
 *   (2) whether the preference was positive or negative,
 *   (3) what role the person had,
 *   (4) whether the preference aligned with the final decision.
 */
public class Preference_Miner_GUI extends GUI_ElementMethods {

    public static void main(String[] args) {

        // Reuse DeMaP Miner GUI initialization.
        GUI_ElementMethods g = new GUI_ElementMethods();
        g.initialiseElements();

        // Create the Preference Miner GUI frame.
        Preference_Miner_GUI preferenceGUI = new Preference_Miner_GUI();
        preferenceGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Read proposal identifier, e.g. pep or bip.
        PropertiesFile wpf = new PropertiesFile();
        proposalIdentifier = wpf.readFromPropertiesFile("proposalIdentifier", false).toLowerCase();

        System.out.println("Preference Miner started.");
        System.out.println("proposalIdentifier: " + proposalIdentifier);

        // Existing label-file logic retained for compatibility.
        ReadFile rf = new ReadFile();
        String fileName = "c:\\scripts\\pepLabels\\pep.txt";
    }
}
