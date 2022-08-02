package utilities;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.management.JMException;

public class MenuBar extends JMenuBar{
    
    public JMenu file,  qry,  member,  search,  utilities,readin, help;
    public JMenuItem   exit,  addBook,  listBook,about;
    public JMenuItem   addMember,  listMember;
    public JMenuItem  searchBooksAndMembers,  borrowBook,  returnBook, readinpeps;
    
    public ImageIcon[] icons;
    
    public String[] imgName1 = {"images/Print1.gif", "images/Exit1.gif",
        "images/Add1.gif", "images/List1.gif",
        "images/Edit1.gif", "images/Delete1.gif",
        "images/Information1.gif", "images/Find1.gif",
        "images/Export1.gif", "images/Import1.gif"};
    
    public MenuBar(){
        
        //add book,member,search,loan & help Menus to the MenuBar
        
        this.add(file = new JMenu("File"));
        this.add(qry = new JMenu("Query"));
        this.add(member = new JMenu("Members"));
        this.add(search = new JMenu("Search"));
        this.add(utilities = new JMenu("Utilities"));
        this.add(readin = new JMenu("Read-In"));
        this.add(help = new JMenu("Help"));
        //File menu shorcut keyword using Mnemonic
        
        file.setMnemonic('f');
        qry.setMnemonic('b');
        member.setMnemonic('m');
        search.setMnemonic('s');
        utilities.setMnemonic('l');
        readin.setMnemonic('r');
        
        //set Image Icons
        
        icons = new ImageIcon[12];
        
        for(int i=0;i<imgName1.length;i++){
            
            icons[i] = new ImageIcon(ClassLoader.getSystemResource(imgName1[i]));
        }
        
        file.add(exit = new JMenuItem("Exit",icons[1]));
        
        qry.add(addBook = new JMenuItem("QueryPeps-Shw Msg",icons[2]));
        qry.add(listBook = new JMenuItem("Pre-Next Sentences",icons[3]));
        
        member.add(addMember = new JMenuItem("Add Members",icons[4]));
        member.add(listMember = new JMenuItem("List All Members",icons[5]));
        
        search.add(searchBooksAndMembers = new JMenuItem("Search",icons[7]));
        
        utilities.add(borrowBook = new JMenuItem("Word Count",icons[8]));
        utilities.add(returnBook = new JMenuItem("Return Books",icons[9]));
        
        readin.add(readinpeps = new JMenuItem("Read In Peps",icons[10]));
        readin.add(readinpeps = new JMenuItem("Remove Duplicates",icons[11]));
        
        help.add(about = new JMenuItem("About"));
        
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,ActionEvent.ALT_MASK));
        searchBooksAndMembers.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
        
        addBook.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,ActionEvent.CTRL_MASK));
        listBook.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,ActionEvent.CTRL_MASK));
        
        addMember.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,ActionEvent.CTRL_MASK));
        listMember.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,ActionEvent.CTRL_MASK));
        
        borrowBook.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.CTRL_MASK));
        returnBook.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.CTRL_MASK));
        
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
    }
}
