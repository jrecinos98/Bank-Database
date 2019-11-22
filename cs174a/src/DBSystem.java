package cs174a;

import java.io.File; 
import java.util.Scanner; 
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBSystem{
	public static boolean execute_queries_from_file(String filename, OracleConnection connection){
		File file = new File(filename);
	   	String query = "";
	   	boolean all_succeeded = true;
		try{
	    	Scanner sc = new Scanner(file);
		    while (sc.hasNextLine()) {
		    	String next = sc.nextLine();
		    	query = query.concat(next); 
		  	}
		  	for (String retval: query.split("#NEW#")) {
       	  		try( Statement statement = connection.createStatement() ) {
					try{
						int updates = statement.executeUpdate( retval );
					}catch(SQLException e){
						e.printStackTrace();
						all_succeeded = false;
					}
				}catch(SQLException e){
					e.printStackTrace();
					all_succeeded = false;
				}

      		}
		}catch(Exception e){
			e.printStackTrace();
			all_succeeded = false;
		}

		return all_succeeded;
	}

}

