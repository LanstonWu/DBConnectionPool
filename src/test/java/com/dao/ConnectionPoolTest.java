package com.dao;

import java.sql.Connection;

import org.junit.Test;

import com.date.DateUtil;
import com.sywu.dao.ConnectionPoolUtil;

public class ConnectionPoolTest {
	@Test
	public void initPool() {
		//获得数据库连接池工具类实例
		ConnectionPoolUtil pool = ConnectionPoolUtil.getInstance();
		//根据db配置文件初始化连接池
		pool.initPool("G:\\git_project\\DBConnectionPool\\src\\main\\resources\\DBConn");
				
		/*
		 * 查询数据
		 */
		//从连接池中获得连接
		Connection conn = pool.getConnection(); 
		DBOperation.read(pool.getConnection(),"select * from partiton_tab_info limit 1",null);
		//把连接返回连接池
		pool.returnConnection(conn);
		
		/***
		 * 条件查询
		 */
		//从连接池中获得连接
		conn = pool.getConnection(); 
		String[] filt={"lg_tyrz_db03","tyrz_athena_api"};
		DBOperation.read(pool.getConnection(),"select * from partiton_tab_info where dbname=? and tabname=?",filt);
		//把连接返回连接池
		pool.returnConnection(conn);
		
		/***
		 * 插入数据
		 */
		// 从连接池中获得连接
		conn = pool.getConnection();
		String[] insert={"hdfs:/tmp/t001.gz",DateUtil.getCurrentDate("yyyy-MM-dd-HH:mm:ss"),"1"};
		DBOperation.write(pool.getConnection(),"insert into flume_hdfs_monitor_detail(path,createdate,status) values(?,?,?)",insert);
		//把连接返回连接池
		pool.returnConnection(conn);
		/***
		 * 更新数据
		 */
		// 从连接池中获得连接
		conn = pool.getConnection();
		String[] update = {"2", DateUtil.getCurrentDate("yyyy-MM-dd-HH:mm:ss"),"hdfs:/tmp/t001.gz"};
		DBOperation.write(pool.getConnection(),"update flume_hdfs_monitor_detail set status=?,closedate=? where path=? and status=1",update);
		//把连接返回连接池
		pool.returnConnection(conn);
		
		/***
		 * 关闭连接池中所有连接
		 */
		pool.closeConnectionPool();
	}
}