package GUI;

import javax.swing.JFrame;

import connections.PropertiesFile;
import utilities.ReadFile;

/*
 * Sentiment/Health Miner GUI
 *
 * This class follows the same design as Preference_Miner_GUI,
 * Influence_Miner_GUI, and Conflict_Miner_GUI. It reuses the DeMaP Miner GUI
 * infrastructure through GUI_ElementMethods.
 *
 * Purpose:
 * - Load the normal DeMaP Miner interface.
 * - Reuse proposal timeline, messages, states, roles and database connection.
 * - Later call Sentiment/Health Miner extraction classes to identify:
 *   (1) sentiment and affective tone in OSS proposal discussions,
 *   (2) community-health signals such as support, fatigue, toxicity, repair and exclusion,
 *   (3) broader health dimensions such as emotional climate, governance stress and sustainability risk,
 *   (4) relationship between emotional/health signals and final proposal outcomes.
 */
public class Sentiment_Health_Miner_GUI extends GUI_ElementMethods {

    public static void main(String[] args) {

        // Reuse DeMaP Miner GUI initialization.
        GUI_ElementMethods g = new GUI_ElementMethods();
        g.initialiseElements();

        // Create the Sentiment/Health Miner GUI frame.
        Sentiment_Health_Miner_GUI sentimentHealthGUI = new Sentiment_Health_Miner_GUI();
        sentimentHealthGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Read proposal identifier, e.g. pep or bip.
        PropertiesFile wpf = new PropertiesFile();
        proposalIdentifier = wpf.readFromPropertiesFile("proposalIdentifier", false).toLowerCase();

        System.out.println("Sentiment/Health Miner started.");
        System.out.println("proposalIdentifier: " + proposalIdentifier);

        // Existing label-file logic retained for compatibility with the older GUI pattern.
        ReadFile rf = new ReadFile();
        String fileName = "c:\\scripts\\pepLabels\\pep.txt";
    }
}
