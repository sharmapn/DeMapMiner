package main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MessageSubjectDetails extends JFrame implements ActionListener {
    // declarations
    JButton btnPay = new JButton("Start");

    // ShoeSizeArray
    String[] sizes = { "5", "5.5", "6", "6.5", "7", "7.5", "8", "8.5", "9", "9.5", "10", "10.5", "11", "11.5", "12.5",
            "13.5", "14", "14.5", "15.5", "16" };

    // Sizecode Array
    String[] comboLabels = { "560", "570", "580", "590", "600", "610", "620", "630", "640", "650", "660", "670", "680",
            "690", "700", "710", "720", "730", "740", "750", "760", "770" };
    JComboBox<Shoesize> combo = new JComboBox<Shoesize>();
    JTextArea display = new JTextArea(5, 20);

    // constructor
    public MessageSubjectDetails() {
        super("Combo box");
        // panel for button and combobox
        JPanel buttonPanel = new JPanel();
        // add panel to frame
        add(buttonPanel, BorderLayout.SOUTH);
        for (int i = 0; i < sizes.length; i++) {
            Shoesize size = new Shoesize(Double.parseDouble(sizes[i]), comboLabels[i]);
            combo.addItem(size);
        }
        // add button and combobox to panel
        buttonPanel.add(btnPay);
        buttonPanel.add(combo);
        // register button with ActionListener
        btnPay.addActionListener(this);
        // add text area to center of frame
        add(display, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        Shoesize sizecode = (Shoesize) combo.getSelectedItem();
        display.append("\nYou selected the sizecode " + sizecode.label);
    }

    public static void main(String[] args) {
        JFrame fr = new MessageSubjectDetails();
        fr.setLocationRelativeTo(null);
        fr.setSize(200, 200);
        fr.setVisible(true);
    }

    class Shoesize {
        double size;
        String label;

        public Shoesize(double size, String label) {
            super();
            this.size = size;
            this.label = label;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            long temp;
            temp = Double.doubleToLongBits(size);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Shoesize other = (Shoesize) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (Double.doubleToLongBits(size) != Double.doubleToLongBits(other.size))
                return false;
            return true;
        }

        private MessageSubjectDetails getOuterType() {
            return MessageSubjectDetails.this;
        }

        @Override
        public String toString() {
            return String.valueOf(size);
        }

    }
}
