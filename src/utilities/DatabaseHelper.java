package utilities;

import java.sql.*;

// Singleton Database Obj for handling all queries.
public class DatabaseHelper {
	
	private static DatabaseHelper databaseHelper = null;
	private Connection connect = null;
	
	private DatabaseHelper() {
		System.out.println("Singleton class initiated");
	}
	
	public static DatabaseHelper getInstance() {
		if(databaseHelper == null) {
			databaseHelper = new DatabaseHelper();
		}
		
		return databaseHelper;
	}
	
	
	// [8]
	// Create Database Connection and return that
	public Connection getDBConnection() {
		
		if (this.connect != null) {
			return this.connect;
		}
		else {
			try {
				connect = DriverManager.getConnection("jdbc:sqlite:C:/Users/Yogeshwar Chaudhari/IdeaProjects/ProjectTeamFormation/sqlite/projectTeamFormation.db");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return connect;
	}
	
	
	// Execute Simple SQL query and return result set
	public ResultSet runSimpleQuery(String queryStr) {
		Connection connect = this.getDBConnection(); 
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connect.createStatement();
			rs = stmt.executeQuery(queryStr);
		} catch (SQLException e) {
			e.printStackTrace();
		}  
		
		return rs;
	}


	// Close connection after use. Now to be used with every request.
	public void closeConnetion() {
		try {
			if(this.connect != null) {
				this.connect.close();
				this.connect = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
