package readRepository.readRepositoryDistinctSenders;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import connections.MysqlConnectForQueries;
import connections.MysqlConnect;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClusterSameSendersGUI extends JFrame {
	static JTable parentTable, childTable;
	
	static MysqlConnect mc = new MysqlConnect();//static MysqlConnectForQueries mc = new MysqlConnectForQueries();
	
	public static void main(String[] args) {
		Connection conn = mc.connect();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				ClusterSameSendersGUI frame = new ClusterSameSendersGUI(conn);
				frame.setVisible(true);
			}
		});		
		//closeConnection(conn);		
	}
	
	/**
	 * Create the frame.
	 */
	public ClusterSameSendersGUI(Connection conn) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		setBounds(10, 10, 1500, 900);		setTitle("Cluster Sender IDs");		getContentPane().setLayout(null);

		// Customer List
		//JLabel lblCustomerList = new JLabel("Sender List");
		//lblCustomerList.setBounds(207, 44, 87, 14);
		//getContentPane().add(lblCustomerList);

		// ScrollPane
		JScrollPane parentScrollPane = new JScrollPane();		parentScrollPane.setBounds(10, 30, 400, 700);		
		JScrollPane childScrollPane = new JScrollPane();		childScrollPane.setBounds(410, 30, 900, 700);		
		getContentPane().add(parentScrollPane);		getContentPane().add(childScrollPane);		
		// Table
		parentTable = new JTable();			parentScrollPane.setViewportView(parentTable);		
		childTable = new JTable();		childScrollPane.setViewportView(childTable);
		
		//add(scrollPane1, BorderLayout.CENTER);
		//add(scrollPane2, BorderLayout.SOUTH);

		// Button Delete
		JButton btnCluster = new JButton("Cluster into One");
		btnCluster.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Object[] options = { "Yes", "No" };
				int n = JOptionPane.showOptionDialog(null, "Do you want to Cluster data?",	"Confirm to Cluster?",
								JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE, null, options,
								options[1]);
				if (n == 0) // Confirm Delete = Yes
				{
					for (int i = 0; i < childTable.getRowCount(); i++) {
						Boolean chkDel = Boolean.valueOf(childTable.getValueAt(i, 0).toString()); // Checked
						if(chkDel) // Checked to Delete
						{
							String strCustomerID = childTable.getValueAt(i, 1).toString(); // get CustomerID							
							ClusterIDs(strCustomerID, conn); // Delete Data
						}
					}					
					JOptionPane.showMessageDialog(null, "Clustered Data Successfully");
					PopulateData(conn); // Reload Table
				}

			}
		});
		btnCluster.setBounds(10, 10, 120, 20);
		getContentPane().add(btnCluster);
		
		//add textbox here
	
	    getContentPane().add(new JTextField("Text field 1"));	    getContentPane().add(new JTextField("Text field 2", 8));
	    JTextField t = new JTextField("Text field 3", 8);	    t.setHorizontalAlignment(JTextField.RIGHT);
	    getContentPane().add(t);
	    t = new JTextField("Text field 4", 8);	    t.setHorizontalAlignment(JTextField.CENTER);
	    getContentPane().add(t);
	    getContentPane().add(new JTextField("Text field 5", 3));
	    //pack();
	    //setVisible(true);
	    
		PopulateData(conn);
	}

	private static void PopulateData(Connection conn) {
		// Clear table
		parentTable.setModel(new DefaultTableModel());		childTable.setModel(new DefaultTableModel());

		// Model for Table
		DefaultTableModel modelParent = new DefaultTableModel() {
			public Class<?> getColumnClass(int column) {
				switch (column) {
				case 0:					return Boolean.class;
				case 1:					return String.class;
				case 2:					return String.class;
				case 3:					return String.class;
				case 4:					return String.class;
//				case 5:					return String.class;
//				case 6:					return String.class;
				default:				return String.class;
				}
			}
		};
		
		DefaultTableModel model = new DefaultTableModel() {
			public Class<?> getColumnClass(int column) {
				switch (column) {
				case 0:					return Boolean.class;
				case 1:					return String.class;
				case 2:					return String.class;
				case 3:					return String.class;
				case 4:					return String.class;
				case 5:					return String.class;
				case 6:					return String.class;
				case 7:					return String.class;
				default:				return String.class;
				}
			}
		};
		
		parentTable.setModel(modelParent);
		childTable.setModel(model);
		
		// Add Column
		modelParent.addColumn("Select");		modelParent.addColumn("id");		modelParent.addColumn("sendername");		modelParent.addColumn("emailaddress");		modelParent.addColumn("totalMessageCount");
		//modelParent.addColumn("senderLastName");
		//modelParent.addColumn("totalMessageCount");
		
		
		// Add Column
		model.addColumn("Select");		model.addColumn("id");		model.addColumn("sendername");		model.addColumn("emailaddress");		model.addColumn("senderFirstName");		model.addColumn("senderLastName");
		model.addColumn("levenshtein_distance");		model.addColumn("match");
		
		Statement s = null, sParent=null;
		//Connection conn = null;
		try {					
			s = conn.createStatement();			sParent = conn.createStatement();
			
			//PARENT
			String parentSQL = "select id,sendername, emailaddress,senderFirstName, senderLastName, totalMessageCount "
					  + " from distinctsenders WHERE clustered IS NULL order by totalMessageCount DESC limit 20";
			
			ResultSet recP = sParent.executeQuery(parentSQL);
			int rowParent = 0;	
			//Populate THE FIELDS FOR THE CHILD QUERY		    
			int parentID = 0;	//parentID 
			String parentSendername = null,parentEmailaddress, parentFirstName = null, parentLastName = null;
			
			while ((recP != null) && (recP.next())) {
				int id = recP.getInt("id");
				String sendername = recP.getString("sendername");				String emailaddres = recP.getString("emailaddress"); 				String firstName = recP.getString("senderFirstName");
				String lastName = recP.getString("senderLastName");				int totalMessageCount = recP.getInt("totalMessageCount");
				if(rowParent==0){				//get details of first row to compute child rows
					parentID = id;					parentSendername = sendername;					parentEmailaddress = emailaddres; 
					parentFirstName = firstName; 					parentLastName = lastName;
				}
				modelParent.addRow(new Object[0]);
				modelParent.setValueAt(false, rowParent, 0); // Checkbox
				modelParent.setValueAt(id, rowParent, 1);				modelParent.setValueAt(sendername, rowParent, 2);				modelParent.setValueAt(emailaddres, rowParent, 3);
				modelParent.setValueAt(totalMessageCount, rowParent, 4);
				//modelParent.setValueAt(recP.getString("senderLastName"), rowParent, 5);
				//modelParent.setValueAt(recP.getString("senderLastName"), rowParent, 6);
				rowParent++;
			}		
			
			//CHILD
			//distinctsenders containsall senders, distinctdevsenders contains only developers
			String sql = "select distinct (sendername) AS MATCHING ,id,sendername, emailaddress, senderFirstName, senderLastName,"
					   + "levenshtein_distance('"+parentSendername+"',sendername)  as levenshtein_distance,'sendername' as 'match' "
					   + "from distinctsenders WHERE clustered IS NULL limit 10"  //order by levenshtein_distance
											//WHERE clustered IS NULL	//where id != "+parentID+" -- we dont want to include the 
			 		   + " UNION "
			
					   + "select distinct (emailaddress) AS MATCHING ,id,sendername, emailaddress, senderFirstName, senderLastName,"
					   + "levenshtein_distance('guido@',emailaddress)  as levenshtein_distance,'emailaddress' as 'match' "
					   + "from distinctsenders WHERE clustered IS NULL limit 10 "
			
					   + " UNION "
					   + "select distinct (senderfirstname) AS MATCHING ,id,sendername, emailaddress, senderFirstName, senderLastName,"
					   + "levenshtein_distance('"+parentFirstName+"',senderfirstname)  as levenshtein_distance,'firstname' as 'match' "
					   + "from distinctsenders WHERE clustered IS NULL limit 10 "
						
					   + " UNION "
				       + "select distinct (senderlastname) AS MATCHING ,id,sendername, emailaddress, senderFirstName, senderLastName,"
					   + "levenshtein_distance('"+parentLastName+"',senderlastname)  as levenshtein_distance,'lastname' as 'match' "
					   + "from distinctsenders WHERE clustered IS NULL "
					   + "order by levenshtein_distance asc "
					   + "limit 30 ;";
			
			ResultSet rec = s.executeQuery(sql);
			
			
			
			int row = 0;
			while ((rec != null) && (rec.next())) {
				model.addRow(new Object[0]);
				model.setValueAt(false, row, 0); // Checkbox
				model.setValueAt(rec.getInt("id"), row, 1);				model.setValueAt(rec.getString("sendername"), row, 2);				model.setValueAt(rec.getString("emailaddress"), row, 3);
				model.setValueAt(rec.getString("senderFirstName"), row, 4);				model.setValueAt(rec.getString("senderLastName"), row, 5);				model.setValueAt(rec.getString("levenshtein_distance"), row, 6);
				model.setValueAt(rec.getString("match"), row, 7);
				row++;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}


	}

	private static Integer getMaxIDOrNULL(Connection conn, String tablename) throws SQLException {
		Integer id=0;
		String sql0 = "SELECT max(cluster) from "+tablename+";";  //there will be many rows since there is no seprate table to store this information
		Statement stmt0;
		stmt0 = conn.createStatement();
		ResultSet rs0 = stmt0.executeQuery( sql0 );			
		if (rs0.next())	{				
			id =rs0.getInt(1);	
		}
		if (id==null)
			id=0;
		return id;
	}	
	
	// Delete
	private void ClusterIDs(String ID, Connection conn) {
		//Connection conn = null;
		Statement s = null;

		try {
			conn = mc.connect();	
			s = conn.createStatement();

			//get last max cluster id from distinctdevsender
			Integer maxID = getMaxIDOrNULL(conn, "distinctDevSenders");
			
			//String sql = "DELETE FROM customer  WHERE " + "CustomerID = '"
//			String sql = "INSERT INTO distinctdevsenders (id, emailaddress, sendername,totalMessageCount,senderFirstName, senderLastName)"
//							+ " SELECT id, emailaddress, sendername,totalMessageCount,senderFirstName, senderLastName"
//							+ " FROM   distinctdevsenders"
//							+ " WHERE  id IN ("+ ID +")";			
					//+ strCustomerID + "' ";
						
			//update table set clusters marked = true
			String updateSQL = "UPDATE distinctdevsenders SET clustered = 1, cluster = "+ maxID +" WHERE id=" + ID + ";";
			s.execute(updateSQL);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}

//		try {
//			if (s != null) {
//				s.close();
//				conn.close();
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		}

	}
	
	public static void closeConnection(Connection conn){
		try {
			if (conn != null) {
				//s.close();
				conn.close();
			}
		} 
		catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
}
