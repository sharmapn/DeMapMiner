package relationExtraction;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import connections.MysqlConnectForQueries;
import connections.MysqlConnect;

public class Database {
	
	static MysqlConnect mc = new MysqlConnect();
	
	private static final String SUBJECT_FIELD = MyDBInfo.SUBJECT_FIELD;
	private static final String VERB_FIELD = MyDBInfo.VERB_FIELD;
	private static final String OBJECT_FIELD = MyDBInfo.OBJECT_FIELD;
	private static String TABLENAME = ""; 

	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
	private List<relations> relationsList;
	
	public Database(List<relations> relationsList) {
		this.relationsList = relationsList;
	}
	
	public void setTableName(String tableName) {
		TABLENAME = tableName;
	}

	private void readDataBase(String SQL) throws SQLException {
	    try {
	      connection = mc.connect();
	      statement = connection.createStatement();
	      resultSet = statement.executeQuery(SQL);	      
	      relationsList.clear();	
	      
	      while (resultSet.next())  {
	    	  relationsList.add(new relations(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4)));
	      }
	      
	    } catch (Exception e) {
	    	System.out.println(e);
	    	System.err.println("SQL: " + SQL);
	    } finally {
	 //     connection.close();
	    }
	  }
	
	public void readAll() {
		try {
			readDataBase("SELECT * FROM " + TABLENAME); //MyDBINFO.TABLE_NAME
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public void search(String subject, String verb, String object, boolean partialMatch,
			boolean popLargerThan) {
		if (subject.length() == 0 && verb.length() ==0 && object.length() == 0) {
			readAll();
			return;
		}
		
		StringBuilder sbSql = new StringBuilder();
		
		sbSql.append("SELECT * FROM ");
		sbSql.append(TABLENAME);
		sbSql.append(" WHERE ");
		
		List<String> whereStatments = new ArrayList<String>();
		
		if (subject.length() > 0 && partialMatch) {
			whereStatments.add(SUBJECT_FIELD + " LIKE '%" + subject + "%'");
		} else if (subject.length() > 0) {
			whereStatments.add(SUBJECT_FIELD + " = '" + subject + "'");
		}
		
		if (verb.length() > 0 && partialMatch) {
			whereStatments.add(VERB_FIELD + " LIKE '%" + verb + "%'");
		} else if (verb.length() > 0) {
			whereStatments.add(VERB_FIELD + " = '" + verb + "'");
		}
		
		if (object.length() != 0 && popLargerThan) {
			whereStatments.add(OBJECT_FIELD + " LIKE '%" + object + "%'");
		} else if (object.length() != 0) {
			whereStatments.add(OBJECT_FIELD + " = '" + object + "'");
		}
		
		Iterator<String> iter = whereStatments.iterator();
		boolean first = true;
		while (iter.hasNext()) {
			if (!first) {
				sbSql.append(" AND ");
			} else {
				first = false;
			}
			sbSql.append(iter.next());			
		}
		
		
		try {
			readDataBase(sbSql.toString());
		} catch (SQLException e) {
			System.out.println("SQL: " + sbSql.toString());
			e.printStackTrace();
		}
	}
	
	public void add(relations relation) throws SQLException {
		String sql = "INSERT INTO " + TABLENAME + " (" + SUBJECT_FIELD +" , " + VERB_FIELD + ", " +  OBJECT_FIELD + ") " + "VALUES (?, ? , ?)";
		
		if (relation == null) {
			System.out.println("City is null");
			return;
		}

		try {
			Connection connection = mc.connect();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, relation.getSubject());			preparedStatement.setString(2, relation.getVerb());			preparedStatement.setString(3, relation.getObject());
			preparedStatement.executeUpdate();
			relationsList.add(relation);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		} finally {
			connection.close();
			preparedStatement.close();
		}
	}	
}