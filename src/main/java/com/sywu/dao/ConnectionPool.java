package com.sywu.dao;

import java.sql.Connection;

/**
 * 连接池接口定义
 * 
 * @author 吴苏远
 * @version 1.0.0
 * @pdOid db789b27-ac15-4df7-b3b8-660d791614ed
 */
public interface ConnectionPool {
	/**
	 * 获取最初初始化的连接数
	 * 
	 * @return
	 * @pdOid ed55f8e9-43bc-42de-ba53-63896b5a88f1
	 */
	int getInitialConnections();

	/**
	 * 设置初始化连接数
	 * 
	 * @param initialConnections
	 * @pdOid 593acf8d-6bfe-4e11-9c73-8e9b73699ef0
	 */
	void setInitialConnections(int initialConnections);

	/**
	 * 每次增长值
	 * 
	 * @return
	 * @pdOid 4854150b-ef4a-462c-bbb2-ee4afa8fed19
	 */
	int getIncrementalConnections();

	/**
	 * 设置每次增长值
	 * 
	 * @param incrementalConnections
	 * @pdOid ac99558c-a29c-4d55-80e1-c8f3285b1656
	 */
	void setIncrementalConnections(int incrementalConnections);

	/**
	 * 获取最大连接数
	 * 
	 * @return
	 * @pdOid dbc46826-a56e-4313-a7cf-c381f9194fc7
	 */
	int getMaxConnections();

	/**
	 * 设置最大连接数
	 * 
	 * @param maxConnections
	 * @pdOid 188f50fb-9d7c-46b1-9efa-2252621f7d99
	 */
	void setMaxConnections(int maxConnections);

	/**
	 * 初始化池
	 * 
	 * @pdOid e041efe0-9281-4551-a5e2-3ac72eb023ef
	 */
	void initPool(String connectionConfigFile);

	/**
	 * 获取连接
	 * 
	 * @return
	 * 
	 * @pdOid 70cc28e9-26ba-4d0b-b379-07ecdf26ba53
	 */
	Connection getConnection();

	/**
	 * 释放(返还)连接到池子
	 * 
	 * @param conn
	 * @pdOid bf73bb8c-9c26-4487-baa8-8d9d5876a049
	 */
	void returnConnection(Connection conn);

	/**
	 * 刷新连接池
	 * 
	 * @pdOid 92c82b3d-6a65-4ba8-89eb-7022527e9ba4
	 */
	void refreshConnections();

	/**
	 * 关闭连接池
	 * 
	 * @pdOid 6cdbff6e-2e0e-42f6-842a-13627cbf4356
	 */
	void closeConnectionPool();

}