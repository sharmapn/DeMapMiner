package GUI;
import java.awt.*;
import java.net.MalformedURLException;
import javax.swing.*;
import javax.swing.text.*;
public class Test {
    public static void main(final String[] args) throws MalformedURLException {
        SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
            try {
                init();
            } catch (BadLocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    });
}
private static void init() throws BadLocationException {
    JFrame frame = new JFrame();
    final JTextArea textArea = new JTextArea();
    JScrollPane pane = new JScrollPane(textArea);
    textArea.setText("Something. Something else.\nA second line\na third line"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\n"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nSomething. Samething else.\nA second line\na third line"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\n"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nSomething. Sbmething else.\nA second line\na third line"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\n"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nSomething. Scmething else.\nA second line\na third line"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\n"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nSomething. Sdmething else.\nA second line\na third line"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\n"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nSomething. Semething else.\nA second line\na third line"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\n"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nSomething. Sfmething else.\nA second line\na third line"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\n"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nSomething. Sgmething else.\nA second line\na third line"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\n"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla\nBlabla\n");
    textArea.setSelectionColor(Color.RED);
    frame.add(pane);
    frame.setSize(300, 120);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    String turnToString2 = "Sdmething else.\nA second line\na third line"
            + "Blabla\nBlabla\nBlabla\nBlabla\nBlabla";
    int pos2 = textArea.getText().indexOf(turnToString2);
    Rectangle startIndex = textArea.modelToView(pos2);
    textArea.getHighlighter().addHighlight(pos2,
            pos2 + turnToString2.length(),
            new DefaultHighlighter.DefaultHighlightPainter(Color.yellow));    
    int y = startIndex.y + (pane.getHeight() - 10);
    System.out.println("Pane Height : " + pane.getHeight());
    System.out.println("X : " + startIndex.x);
    System.out.println("Y : " + y);
    System.out.println("Y (pos2) : " + startIndex.y);
    textArea.setCaretPosition(textArea.viewToModel(new Point(startIndex.x, y)));
    pane.scrollRectToVisible(new Rectangle(startIndex.x, y));
    }
}