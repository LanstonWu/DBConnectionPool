package com.sywu.dao;

import java.sql.Connection;

/**
 * 连接池内的连接信息和状态
 * @author 吴苏远
 * @version 1.0.0
 * @pdOid 6fce2b78-f987-4069-a8dd-0a4dd58ee5e6
 */
public class PooledConnection {
	/**
	 * 数据库连接
	 * 
	 * @pdOid 2d56931e-53a0-43b9-b854-27bdd282f888
	 */
	private Connection connection = null;
	/**
	 * 此连接是否正在使用的标志，默认没有正在使用
	 * 
	 * @pdOid cf192a3f-7dc2-41f6-a4de-f1c600b39649
	 */
	private boolean busy = false;
	/** @pdOid b3e20269-b8f1-41c6-adc6-93a0971ae2f6 */
	private String connectionName;

	/**
	 * 构造函数，根据一个 Connection 构告一个 PooledConnection 对象
	 * 
	 * @param connectionName
	 * @param connection
	 * @pdOid 91c2ecdd-c1a5-4b24-8c35-ecb5ff0afcf6
	 */
	public PooledConnection(String connectionName, Connection connection) {
		this.connectionName = connectionName;
		this.connection = connection;
	}

	public PooledConnection(String connectionName, Connection connection,
			boolean busy) {
		this.connectionName = connectionName;
		this.connection = connection;
		this.busy = busy;
	}

	/**
	 * 返回此对象中的连接
	 * 
	 * @pdOid 026b9c2f-a3f9-4b97-9ff2-bc40a839aa7e
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * 设置此对象的连接
	 * 
	 * @param connection
	 * @pdOid 3460fc27-bf1a-4e68-ac24-55db7786c40b
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * 获得对象连接是否忙
	 * 
	 * @pdOid 03fa6484-2cf4-4e7c-925b-594e8baf6d5b
	 */
	public boolean isBusy() {
		return busy;
	}

	/**
	 * 设置对象的连接正在忙
	 * 
	 * @param busy
	 * @pdOid 6997954d-a65f-4174-9984-804602bdf4db
	 */
	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

}