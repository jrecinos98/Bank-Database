package cs174a;

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

import java.util.Scanner;

public class Bank{

	public static String get_date(){
		return "xxxx-xx-xx";
	}

	public static boolean set_date(String day, String month, String year){
		return false;
	}

	public static String get_month(){
		return "xx";
	}

	public static String get_day(){
		return "xx";
	}

	public static String get_year(){
		return "xxxx";
	}


}