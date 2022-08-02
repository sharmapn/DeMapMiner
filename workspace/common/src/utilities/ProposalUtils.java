package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import connections.MysqlConnect;

public class ProposalUtils {
	public static ArrayList returnUniqueProposalsInDatabase(String proposal) {
		// Utilities u = new Utilities();
		MysqlConnect mc = new MysqlConnect();
		Connection connection = mc.connect();
		// mc.testConnection();
		Integer counter = 0, proposalNumber;
		ArrayList<Integer> uniqueProposal = new ArrayList<>();
		// numbers.add(308);
		System.out.println("here");
		String sql = "SELECT distinct(" + proposal + ") from allmessages where " + proposal + " <> -1 UNION SELECT 0 from dual"; // "SELECT distinct(proposal) from allmessages_dev;";
																															
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) { // check every message
				proposalNumber = rs.getInt(1);
				uniqueProposal.add(proposalNumber);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return uniqueProposal;
	}

	public static ArrayList returnUniqueProposalInDatabase_ProposalDetails(String proposal) {
		// Utilities u = new Utilities();
		MysqlConnect mc = new MysqlConnect();
		// mc.testConnection();
		ArrayList<Integer> uniqueProposal = new ArrayList<>();
		// numbers.add(308);

		Integer proposalNumber;
		Connection connection = mc.connect();
		String sql = "SELECT distinct(" + proposal + ") from " + proposal + "details ;";
		// can add messageid later
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) { // check every message
				proposalNumber = rs.getInt(1);
				uniqueProposal.add(proposalNumber);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return uniqueProposal;
	}

	public static int returnMessageProposal(int v_messageID, String proposal) {
		MysqlConnect mc = new MysqlConnect();
		// mc.testConnection();
		Integer counter = 0;

		String sql = "SELECT distinct (" + proposal + ") from allmessages where messageID = " + v_messageID + " order by date2;";
		// can add messageid later
		Connection connection = mc.connect();
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) { // check every message
				counter++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return counter;

	}

	public static Integer returnLastProposalInResultsTable(String proposal) {
		// Utilities u = new Utilities();
		MysqlConnect mc = new MysqlConnect();
		// mc.testConnection();

		Integer proposalNumber = null;
		Connection connection = mc.connect();
		Statement stmt;
		String sql = "SELECT max(" + proposal + ") from results;";

		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next()) { // check every message
				proposalNumber = rs.getInt(1);
				// uniqueProposal.add(proposalNumber);
			} else {
				proposalNumber = null;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return proposalNumber;
	}

}
