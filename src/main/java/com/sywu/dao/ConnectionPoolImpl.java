package com.sywu.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 连接池内部实现
 * @author 吴苏远
 * @version 1.0.0
 * @pdOid e0636b34-2538-496f-94ff-2ab1374d8039
 */
public class ConnectionPoolImpl {
	Logger log=LogManager.getFormatterLogger(ConnectionPoolImpl.class);
	/**
	 * 数据库类型
	 * 
	 * @pdOid 2213f00c-6bb5-404c-91ac-6b53b28113ee
	 */
	private String dbType = "";
	/**
	 * 数据库驱动
	 * 
	 * @pdOid 0e0b1cbc-ae7a-44d8-ba4e-281e746145f6
	 */
	private String jdbcDriver = "";
	/**
	 * 数据 URL
	 * 
	 * @pdOid 38f01788-1bd7-4d25-a0f8-d264aed156ef
	 */
	private String dbUrl = "";
	/**
	 * 数据库用户名
	 * 
	 * @pdOid 8eb539e2-d1f3-40a8-8eec-e8801edd55af
	 */
	private String dbUsername = "";
	/**
	 * 数据库用户密码
	 * 
	 * @pdOid e5697e15-586d-453d-93bc-d193a884fff0
	 */
	private String dbPassword = "";
	/**
	 * 连接池的初始大小
	 * 
	 * @pdOid 4b873369-1d1a-4d57-8fb8-eb4fcc480e5e
	 */
	private int initialConnections = 0;
	/**
	 * 连接池自动增加的大小
	 * 
	 * @pdOid 8f862c81-c3b2-435c-8fde-79231a17336b
	 */
	private int incrementalConnections = 0;
	/**
	 * 连接池最大的大小
	 * 
	 * @pdOid a0e8683b-ac5d-4745-b37b-f0025b5aac81
	 */
	private int maxConnections = 0;
	/**
	 * 存放连接池中数据库连接的向量 
	 * 
	 * @pdOid 0e0f091a-c3ec-4623-b5ae-0fbf6ba7d766
	 */
	private Vector<PooledConnection> connections = null;

	/**
	 * @param numConnections
	 * @exception SQLException
	 * @pdOid 8eaf4059-ab14-4f19-8d66-e576b3df7bdd
	 */
	private void createConnections(int numConnections) throws SQLException {
		// 循环创建指定数目的数据库连接
		for (int x = 0; x < numConnections; x++) {
			/*
			 * 检查连接池中的数据库连接的数量是否己经达到最大，最大值由类成员 maxConnections指出，假如 maxConnections 为
			 * 0 或负数，表示连接数量没有限制。 假如连接数己经达到最大退出
			 */
			if (this.maxConnections > 0&& this.connections.size() >= this.maxConnections) {
				break;
			}
			// 把连接放入连接池中
			try {
				String dbUid=dbConnUID();
				connections.addElement(new PooledConnection(dbUid,newConnection()));
				log.info("数据库连接:"+dbUid+"被创建并加入连接池中...");
			} catch (SQLException e) {
				log.warn("数据库连接创建失败... " + e.getMessage());
				throw new SQLException();
			}
			log.info("数据库连接己创建 ....");
		}
	}

	/**
	 * 创建一个新的数据库连接
	 * @exception SQLException
	 * @pdOid be0952b8-5a4c-4c7f-88e8-d8ed1d1d1901
	 */
	private Connection newConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(dbUrl, dbUsername,dbPassword);
		/*如果这是第一次创建数据库连接，即检查数据库，获得此数据库支持的最大客户连接数,
		 * connections.size()==0 表示目前没有连接己被创建
		 */
		if (connections.size() == 0) {
			DatabaseMetaData metaData = conn.getMetaData();
			int driverMaxConnections = metaData.getMaxConnections();
			/* 1. 数据库返回的 driverMaxConnections 若为 0 ，表示此数据库没有最大连接限制，或数据库的最大连接限制不知道
			 * 2. driverMaxConnections 为返回的一个整数，表示此数据库返回客户最大支持的连接数
			 * 3. 假如连接池中设置的最大连接数量大于数据库答应的连接数目 , 则置连接池的最大连接数目为数据库答应的最大数目
			 * 
			 */
			if (driverMaxConnections > 0&& this.maxConnections > driverMaxConnections) {
				this.maxConnections = driverMaxConnections;
			}
		}
		return conn; // 返回创建的新的数据库连接
	}

	/**
	 * 从连接池中获得一个可用的数据库连接
	 * @exception SQLException
	 * @pdOid 084b9760-dbb3-4159-bfd4-3b85aad99ded
	 */
	private Connection getFreeConnection() throws SQLException {
		Connection conn = findFreeConnection();
		if (conn == null) {
			// 假如目前连接池中没有可用的连接，表示已无可用连接创建一些连接
			createConnections(incrementalConnections);
			// 重新从池中查找是否有可用连接
			conn = findFreeConnection();
			if (conn == null) {
				// 假如创建连接后仍获得不到可用的连接，则返回 null
				return null;
			}
		}
		return conn;
	}

	/**
	 * 从连接池中获得可用的数据库连接
	 * @exception SQLException
	 * @pdOid 344bcbd2-e93f-44e7-9d8d-a2216d8bae0a
	 */
	private Connection findFreeConnection() throws SQLException {
		Connection conn = null;
		PooledConnection poolConn = null;
		// 获得连接池向量中所有的对象
		Enumeration<PooledConnection> enumerate = connections.elements();
		// 遍历所有的对象，看是否有可用的连接
		while (enumerate.hasMoreElements()) {
			poolConn = (PooledConnection) enumerate.nextElement();
			if (!poolConn.isBusy()) {
				// 假如此对象不忙，则获得它的数据库连接并把它设为忙
				conn = poolConn.getConnection();
				poolConn.setBusy(true);
				// 测试此连接是否可用，如果不可用则从连接池中删除连接
				if (!testConnection(conn)) {
					log.warn(poolConn.getConnectionName()+" 已经不可用，将从连接池中删除，并创建新的连接...");
					// 假如此连接不可再用了，则创建一个新的连接，并将新的连接加入连接池中
					try {
						conn=newConnection();
						String dbUid=dbConnUID();
						connections.addElement(new PooledConnection(dbUid,conn,true));
						log.info(dbUid+" 连接被使用...");
						return conn;
					} catch (SQLException e) {
						log.error("数据库连接创建失败..." + e.getMessage());
						return null;
					}
				}
				log.warn(poolConn.getConnectionName()+" 连接被使用...");
				break; //己经找到可用的连接
			}
		}
		return conn; // 返回找到到的可用连接
	}

	/**
	 * @param conn
	 * @pdOid d6416db3-7038-4985-aebe-91416f902867
	 */
	private boolean testConnection(Connection conn) {
		try {
			String testSql = "";
			switch (dbType.toLowerCase()) {
			case "mysql":
				testSql = "select CURRENT_TIMESTAMP()";
				break;
			case "oracle":
				testSql = "select SYSDATE from dual";
				break;
			}
			DBOperation.read(conn, testSql, null);
		} catch (SQLException e) {
			//异常表示此连接可能己不可用，关闭它，并返回 false
			closeConnection(conn);
			return false;
		}
		// 连接可用，返回 true
		return true;
	}

	/**
	 * @param conn
	 * @pdOid b71e21d4-49e4-4631-875c-84c164607e19
	 */
	private void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			log.error("数据库连接关闭异常： " + e.getMessage());
		}
	}

	/**
	 * @param mSeconds
	 * @pdOid a5d0d4ff-832c-411d-9a81-06ecd6851432
	 */
	private void wait(int mSeconds) {
		try {
			Thread.sleep(mSeconds);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * 构造函数
	 * @param jdbcDriver
	 * @param dbUrl
	 * @param dbUsername
	 * @param dbPassword
	 * @param initialConnections
	 * @param incrementalConnections
	 * @param maxConnections
	 * @param dbType
	 * @pdOid c1965adc-26c2-4a24-9d19-7b165d5de22d
	 */
	public ConnectionPoolImpl(String jdbcDriver, String dbUrl,
			String dbUsername, String dbPassword, int initialConnections,
			int incrementalConnections, int maxConnections, String dbType) {
		this.jdbcDriver = jdbcDriver;
		this.dbUrl = dbUrl;
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
		this.initialConnections = initialConnections;
		this.incrementalConnections = incrementalConnections;
		this.maxConnections = maxConnections;
		this.dbType = dbType;
		log.info(String
						.format("dbType:%s,dbUrl:%s,dbUsername:%s,initialConnections:%s,incrementalConnections:%s,maxConnections:%s",
								dbType, dbUrl, dbUsername, initialConnections,
								incrementalConnections, maxConnections));
	}

	/** @pdOid 8e756597-aa16-42b3-84e9-9fa7aebfd40d */
	public int getInitialConnections() {
		return this.initialConnections;
	}

	/**
	 * @param initialConnections
	 * @pdOid 32a111dd-1be5-463d-bc60-7d2f415091b0
	 */
	public void setInitialConnections(int initialConnections) {
		this.initialConnections = initialConnections;
	}

	/** @pdOid 695ab41d-b638-4424-b0ac-e83bdad1f237 */
	public int getIncrementalConnections() {
		return this.incrementalConnections;
	}

	/**
	 * @param incrementalConnections
	 * @pdOid d1d9ddb7-79c0-450c-a1c5-3f15bf0a5e77
	 */
	public void setIncrementalConnections(int incrementalConnections) {
		this.incrementalConnections = incrementalConnections;
	}

	/** @pdOid 27b8ea28-a5bc-4a7c-8b01-652feb5d49e0 */
	public int getMaxConnections() {
		return this.maxConnections;
	}

	/**
	 * @param maxConnections
	 * @pdOid 36c0b2f7-eb4d-45cc-a216-fab621591ce5
	 */
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	/**
	 * 创建连接池
	 * @exception Exception
	 * @pdOid 6d6cd597-345e-43d6-87d8-3f7e96f9cc45
	 */
	public synchronized void createPool() throws Exception {
		//如果connections非空,表示连接池已创建
		if (connections != null) {
			return; 
		}
		// 实例化 JDBC Driver 中指定的驱动类实例
		Driver driver = (Driver) (Class.forName(this.jdbcDriver).newInstance());
		DriverManager.registerDriver(driver); //注册 JDBC 驱动程序
		// 创建保存连接的向量 , 初始时有 0 个元素
		connections = new Vector<PooledConnection>();
		// 根据 initialConnections 中设置的值，创建连接。
		createConnections(this.initialConnections);
		log.info("数据库连接池创建成功... ");
	}

	/**
	 * 从连接池中获得连接
	 * @exception SQLException
	 * @pdOid a97723bf-10c2-438b-acd6-30bca20034eb
	 */
	public synchronized Connection getConnection() throws SQLException {
		// 如果连接池还没创建，则返回 null
		if (connections == null) {
			return null;
		}
		Connection conn = getFreeConnection(); // 获得一个可用的数据库连接
		// 假如目前没有可以使用的连接，即所有的连接都在使用中
		while (conn == null) {
			// 等一会再试
			wait(250);
			conn = getFreeConnection(); // 重新再试，直到获得可用的连接
		}
		return conn; // 返回获得的可用的连接
	}

	/**
	 * @param conn
	 * @pdOid ada0a0b4-640c-480e-971a-e58e1ebd62c9
	 */
	public void returnConnection(Connection conn) {
		// 确保连接池存在，假如连接没有创建（不存在），直接返回
		if (connections == null) {
			log.warn("连接池不存在，无法返回此连接到连接池中 !");
			return;
		}
		PooledConnection pConn = null;
		Enumeration<PooledConnection> enumerate = connections.elements();
		// 遍历连接池中的所有连接，找到这个要返回的连接对象
		while (enumerate.hasMoreElements()) {
			pConn = (PooledConnection) enumerate.nextElement();
			// 先找到连接池中的要返回的连接对象
			if (conn == pConn.getConnection()) {
				// 找到了 , 设置此连接为空闲状态
				pConn.setBusy(false);
				log.info(pConn.getConnectionName()+" 连接被返回到连接池中...");
				break;
			}
		}
	}

	/**
	 * @exception SQLException
	 * @pdOid c3b90753-134b-4288-a4b7-bd3c2dc544ae
	 */
	public synchronized void refreshConnections() throws SQLException {
		// 确保连接池己创新存在
		if (connections == null) {
			log.warn("连接池不存在，无法刷新 !");
			return;
		}
		PooledConnection pConn = null;
		Enumeration<PooledConnection> enumerate = connections.elements();
		while (enumerate.hasMoreElements()) {
			// 获得一个连接对象
			pConn = (PooledConnection) enumerate.nextElement();
			// 假如对象忙则等 5 秒 ,5 秒后直接刷新
			if (pConn.isBusy()) {
				wait(5000); // 等 5 秒
			}
			// 关闭此连接，用一个新的连接代替它。
			closeConnection(pConn.getConnection());
			pConn.setConnection(newConnection());
			pConn.setBusy(false);
		}
	}

	/**
	 * @exception SQLException
	 * @pdOid e3f6bb52-efa2-4eb4-a151-7f630c0a4b86
	 */
	public synchronized void closeConnectionPool() throws SQLException {
		// 确保连接池存在，假如不存在，返回
		if (connections == null) {
			log.warn("连接池不存在，无法关闭 !");
			return;
		}
		PooledConnection pConn = null;
		Enumeration<PooledConnection> enumerate = connections.elements();
		log.info(String.format("Connection Pools has %d connections,closing now...",connections.size()));
		while (enumerate.hasMoreElements()) {
			pConn = (PooledConnection) enumerate.nextElement();
			// 假如忙，等 5 秒
			if (pConn.isBusy()) {
				wait(5000); // 等 5 秒
			}
			// 5 秒后直接关闭它
			closeConnection(pConn.getConnection());
			// 从连接池向量中删除它
			connections.removeElement(pConn);
		}
		log.info("Connection Pools has closed...");
		// 置连接池为空
		connections = null;
	}
	
	/**
	 * 产生数据库连接ID
	 * @return
	 */
	private String dbConnUID(){
		return "Conn_"+UUID.randomUUID().toString();
	}
}