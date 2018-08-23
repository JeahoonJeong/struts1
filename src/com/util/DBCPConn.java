package com.util;

import java.sql.Connection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DBCPConn {
	private static Connection conn=null;
	
	public static Connection getConnection() {
		if(conn==null) {
			try {
				// 이름과 객체 바인딩
				Context ctx=new InitialContext();
				Context evt=(Context)ctx.lookup("java:/comp/env");
				DataSource ds = (DataSource)evt.lookup("jdbc/myOracle");
				conn=ds.getConnection();
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
		
		return conn;
	}
	
	public static void close() {
		if(conn != null) {
			try {
				if(!conn.isClosed())
					conn.close();
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
		
		conn=null;
	}
}
