package GUI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MessageDate {
   
        Date dt; String valueToShow;
        Integer numMessages;

        public MessageDate(Date s, Integer num) {
            super();
            this.dt = s;
            //this.subjectShort = sshort;
            this.numMessages = num;
            this.valueToShow = s + " (" + num + ")";
            //this.subjectShort = sshort + " (" + num +")";
        }        

//        private ComboBoxDemo getOuterType() {
//            return ComboBoxDemo.this;
//        }

        @Override
        public String toString() {
            return String.valueOf(valueToShow);
        }
   
}
