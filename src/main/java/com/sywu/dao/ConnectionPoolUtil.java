package com.sywu.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import com.properties.PropertiesUtil;

/** 连接池工具类(单实例)
 * @author 吴苏远
 * @version 1.0.0
 * @pdOid 6f03984a-d2d8-47e8-b102-6aacfc757743 */
public class ConnectionPoolUtil implements ConnectionPool {
   /** @pdOid e111d555-b3fb-4f2b-8e1c-20edc393e685
       @pdRoleInfo migr=yes name=ConnectionPoolImpl assc=association1 */
   private static ConnectionPoolImpl connPoolImpl = null;
   /** @pdOid c623bb9e-bd06-434f-8900-c0b5ccf4c81a
       @pdRoleInfo migr=yes name=ConnectionPoolUtil assc=association2 */
   private static ConnectionPoolUtil instance = new ConnectionPoolUtil();
   
   /** @pdOid a505b63a-ba23-4081-93fb-7901f3f22814 */
   private ConnectionPoolUtil() {
	}
   
   /***
    * 加载数据库连接配置信息并初始化连接池对象
    * @param connectionConfigFile
    */
   private static synchronized void initConnPoolImpl(String connectionConfigFile){
	   if(null==connPoolImpl){
		   //读取配置文件
	 		Properties p = PropertiesUtil.createPropertiesByFile(connectionConfigFile);
			connPoolImpl = new ConnectionPoolImpl(p.getProperty("jdbcDriver"), p.getProperty("dbUrl") ,p.getProperty("dbUsername"),p.getProperty("dbPassword"),Integer.parseInt(p.getProperty("initialConnections")),Integer.parseInt(p.getProperty("incrementalConnections")),Integer.parseInt(p.getProperty("maxConnections")),p.getProperty("dbType"));
			System.out.println("ConnectionPoolImpl created...");
		}
   }
   
   /** @pdOid 27edfc42-a54d-405f-a89f-d5863937f20c */
   public static ConnectionPoolUtil getInstance() {
	   return instance;
   }
   
   /** @pdOid ef8e5b65-7caa-485c-a2d3-94515cc81338 */
   @Override
   public int getInitialConnections() {
   	return connPoolImpl.getInitialConnections();
   }
   
   /** @param initialConnections
    * @pdOid 8497820a-9391-47c0-b9f8-f4ac95112f08 */
   @Override
   public void setInitialConnections(int initialConnections) {
   	connPoolImpl.setInitialConnections(initialConnections);
   }
   
   /** @pdOid ca7a8249-033b-434c-942c-9848be672b90 */
   @Override
   public int getIncrementalConnections() {
   	return connPoolImpl.getIncrementalConnections();
   }
   
   /** @param incrementalConnections
    * @pdOid 430e52e4-1547-42d4-bf42-881181ad357a */
   @Override
   public void setIncrementalConnections(int incrementalConnections) {
   	connPoolImpl.setIncrementalConnections(incrementalConnections);
   }
   
   /** @pdOid 3d7f085f-de40-40fd-b38c-f20cd0bb17d0 */
   @Override
   public int getMaxConnections() {
   	return connPoolImpl.getMaxConnections();
   }
   
   /** @param maxConnections
    * @pdOid e279b30d-50eb-4340-b15a-4ebcfdb58970 */
   @Override
   public void setMaxConnections(int maxConnections) {
   	connPoolImpl.setMaxConnections(maxConnections);
   }
   
   /**
    * 从配置文件中获取数据库连接信息并初始时连接池
    *  @pdOid 422b4c32-c593-4686-9686-9d2b433e9f02 
    *  */
   @Override
   public void initPool(String connectionConfigFile) {
   	try {
   		initConnPoolImpl(connectionConfigFile);
   		connPoolImpl.createPool();
   	} catch (Exception e) {
   		e.printStackTrace();
   	}
   }
   
   /** @pdOid 3d3803f1-ef75-48df-b83a-ce574f240a1d */
   @Override
   public Connection getConnection() {
   	Connection conn = null;
   	try {
   		conn = connPoolImpl.getConnection();
   	} catch (SQLException e) {
   		e.printStackTrace();
   	}
   	return conn;
   }
   
   /** @param conn
    * @pdOid d658da6d-5f06-4c9e-9aec-a6be1ab45aea */
   @Override
   public void returnConnection(Connection conn) {
	   connPoolImpl.returnConnection(conn);
   }
   
   /** @pdOid d1339a0f-ef22-4d97-9145-70e7ac07cd00 */
   @Override
   public void refreshConnections() {
   	try {
   		connPoolImpl.refreshConnections();
   	} catch (SQLException e) {
   		e.printStackTrace();
   	}
   }
   
   /** @pdOid 161a92b7-fd11-4cd4-a159-803f9efd1793 */
   @Override
   public void closeConnectionPool() {
   	try {
   		connPoolImpl.closeConnectionPool();
   	} catch (SQLException e) {
   		e.printStackTrace();
   	}
   }

}