package com.sywu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/** 数据操作类(读/写数据)
 * @author 吴苏远
 * @version 1.0.0
 * @pdOid 3f7f0b3d-574f-4a79-a503-135191bbd5df */
public class DBOperation {
   /** @param conn 
    * @param sql 
    * @param s
    * @pdOid a990783c-fc92-4500-aeba-d2cf5b32d2a3 */
   public static void write(Connection conn, String sql, String[] s)  {
     try {
   	PreparedStatement state=conn.prepareStatement(sql);
   	if (s != null && s.length >= 0) {
   		for (int i = 0; i < s.length; i++) {
   			state.setString(i+1, s[i]);
   		}
   	}
   	int rs=state.executeUpdate();
   	System.out.println(String.format("sql:%s,insert or update:%d rows.",state.toString(),rs));
   } catch (SQLException e) {
   	e.printStackTrace();
   }
    }
   
   /** *
    * 读取数据
    * 
    * @param conn 数据库连接
    * @param sql 
    * @param s 参数
 * @throws SQLException 
    * @pdOid dbf0213a-8238-4729-9443-197c413d86dc */
   public static void read(Connection conn, String sql, String[] s) throws SQLException  {
     ResultSet rs = null;
   	  PreparedStatement state=conn.prepareStatement(sql);
   	  if (s != null && s.length >= 0) {
   			for (int i = 0; i < s.length; i++) {
   				state.setString(i+1, s[i]);
   			}
   		}
   		rs = state.executeQuery();
   	if(null!=rs){
   		ResultSetMetaData rsd=rs.getMetaData();
   		int rsColCount=rsd.getColumnCount();
   		int row=1;
   		while(rs.next()){
   		  for(int i=1;i<=rsColCount;i++)
   			System.out.println(String.format("row:%d,column:%d,columnName:%s,value:%s",row,i,rsd.getColumnName(i), rs.getObject(i)));
   		}
   		row++;
   	}
 
    }

}