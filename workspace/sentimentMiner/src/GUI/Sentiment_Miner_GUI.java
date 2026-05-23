package GUI;

import javax.swing.JFrame;

import connections.PropertiesFile;
import utilities.ReadFile;

/*
 * Sentiment Miner GUI
 *
 * This launcher follows the existing DeMaP Miner GUI pattern.
 *
 * Sentiment Miner is intended to study sentence-level emotional climate,
 * criticism, conflict intensity, toxicity, and project-health signals in
 * OSS proposal discussions.
 */
public class Sentiment_Miner_GUI extends GUI_ElementMethods {

    public static void main(String[] args) {

        GUI_ElementMethods g = new GUI_ElementMethods();
        g.initialiseElements();

        Sentiment_Miner_GUI sentimentGUI = new Sentiment_Miner_GUI();
        sentimentGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        PropertiesFile wpf = new PropertiesFile();
        proposalIdentifier = wpf.readFromPropertiesFile("proposalIdentifier", false).toLowerCase();

        System.out.println("Sentiment Miner started.");
        System.out.println("proposalIdentifier: " + proposalIdentifier);

        ReadFile rf = new ReadFile();
        String fileName = "c:\\scripts\\pepLabels\\pep.txt";
    }
}
