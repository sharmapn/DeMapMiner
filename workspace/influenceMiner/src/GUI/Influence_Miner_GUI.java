package GUI;

import javax.swing.JFrame;

import connections.PropertiesFile;
import utilities.ReadFile;

/*
 * Influence Miner GUI
 *
 * This class follows the same design as Preference_Miner_GUI.
 * It reuses the DeMaP Miner GUI infrastructure through GUI_ElementMethods.
 *
 * Purpose:
 * - Load the normal DeMaP Miner interface.
 * - Reuse proposal timeline, messages, states, roles and database connection.
 * - Later call Influence Miner extraction classes to identify:
 *   (1) who influenced the decision,
 *   (2) what mechanism of influence was used,
 *   (3) whether influence was internal, external, or mixed,
 *   (4) whether the influence pushed toward support, blocking, or revision,
 *   (5) whether influence aligned with the final proposal outcome.
 */
public class Influence_Miner_GUI extends GUI_ElementMethods {

    public static void main(String[] args) {

        // Reuse DeMaP Miner GUI initialization.
        GUI_ElementMethods g = new GUI_ElementMethods();
        g.initialiseElements();

        // Create the Influence Miner GUI frame.
        Influence_Miner_GUI influenceGUI = new Influence_Miner_GUI();
        influenceGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Read proposal identifier, e.g. pep or bip.
        PropertiesFile wpf = new PropertiesFile();
        proposalIdentifier = wpf.readFromPropertiesFile("proposalIdentifier", false).toLowerCase();

        System.out.println("Influence Miner started.");
        System.out.println("proposalIdentifier: " + proposalIdentifier);

        // Existing label-file logic retained for compatibility with the older GUI pattern.
        ReadFile rf = new ReadFile();
        String fileName = "c:\\scripts\\pepLabels\\pep.txt";
    }
}
