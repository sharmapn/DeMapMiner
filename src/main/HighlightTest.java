package main;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
public class HighlightTest {
    String[] words = new String[]{"world", "cruel"};
    int[] wordsStartPos = new int[]{6, 21};
    String text = "Hello world, Goodbye cruel world";
    public HighlightTest() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Test");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                JTextPane jta = new JTextPane();
                jta.setText(text);
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setBackground(sas, Color.RED);
                StyledDocument doc = jta.getStyledDocument();
                for (int i = 0; i < wordsStartPos.length; i++) {
                    doc.setCharacterAttributes(wordsStartPos[i], words[i].length(), sas, false);
                }
                frame.add(jta);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
    public static void main(String[] args) {
        new HighlightTest();
    }
}
