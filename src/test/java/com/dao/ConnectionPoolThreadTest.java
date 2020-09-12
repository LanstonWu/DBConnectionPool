package com.dao;
import java.sql.Connection;

import com.sywu.dao.ConnectionPoolUtil;

class RandomThread extends Thread {

	Connection conn=null;
	public RandomThread(String name,Connection conn) {
		super(name);
		this.conn=conn;
	}

	@Override
	public void run() {
		try {
			System.out.println(String.format("Thread name:%s,conn:%s,starting...",Thread.currentThread().getName(),this.conn));
			Thread.sleep(1000);
			System.out.println(Thread.currentThread().getName());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

public class ConnectionPoolThreadTest {
	public static void main(String[]args){
		ConnectionPoolUtil pool = ConnectionPoolUtil.getInstance();
		pool.initPool("G:\\git_project\\DBConnectionPool\\src\\main\\resources\\DBConn");
		
		Thread[] th = new Thread[6];
		for (int i = 0; i < 6; i++) {
			th[i] = new RandomThread("RandomThread " + i,pool.getConnection());
		}

		for (Thread t : th) {
			t.start();
		}
		pool.closeConnectionPool();
	}
}
