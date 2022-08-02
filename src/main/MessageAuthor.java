package main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MessageAuthor {
   
        String author,valueToShow, authorShort;
        Integer numMessages;

        public MessageAuthor(String s, String sshort, Integer num) {
            super();
            this.author = s;   //this.author = s;
            this.valueToShow = s + " (" + num +")"; 
            //this.subjectShort = sshort;
            this.numMessages = num;
            this.authorShort = sshort + " (" + num +")";
            //this.author = sshort + " (" + num +")";
        }        

//        private ComboBoxDemo getOuterType() {
//            return ComboBoxDemo.this;
//        }

        @Override
        public String toString() {
            //return String.valueOf(authorShort);
        	if(valueToShow.length() > 25)
        		valueToShow = valueToShow.substring(0,24);
        	return String.valueOf(valueToShow);
        }
   
}
