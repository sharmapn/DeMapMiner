package main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MessageSubject {
   
        String subject, subjectShort;
        Integer numMessages;

        public MessageSubject(String s, String sshort, Integer num) {
            super();
            this.subject = s;
            //this.subjectShort = sshort;
            this.numMessages = num;
            this.subjectShort = sshort + " (" + num +")";
        }        

//        private ComboBoxDemo getOuterType() {
//            return ComboBoxDemo.this;
//        }

        @Override
        public String toString() {
            return String.valueOf(subjectShort);
        }
   
}
