数据库连接池设计和实现(Java版本)
===

[TOC]



# 1 前言
数据库连接池是目前系统开发必须面对和考虑的问题，原理并不复杂，主要是减少重复连接数据库的代价；在系统中创建预期数量的数据库连接，并将这些连接以一个集合或类似生活中的池一样管理起来，用到的时候直接拿过来使用，用完返回给系统管理；需要注意和主要的难点：  
1. 连接池的同步；
2. 连接使用和空闲管理；
3. 连接池满时的管理和响应。

# 2 连接池应用场景
1. 在线系统；
2. 高并发和多线程系统；
3. 有独立服务管理数据库连接的系统，比如中间件；

# 3 设计
![20200912-数据库连接池设计和实现01](G:\Knowledge World\Java\20200912-数据库连接池设计和实现01.png)
ConnectionPool 定义连接池的结构、功能信息；  
PooledConnection 连接池内的连接对象数据结构；  
ConnectionPoolImpl 连接池内部结构和实现，创建/关闭/获取连接；   
ConnectionPoolUtil 连接池工具类，对外开放；  
DBOperation 数据库操作(read/write)简单实现类；

# 4 开发测试软件版本
JDK 1.8，MySQL 5.7.30

# 5 源码
https://github.com/LanstonWu/DBConnectionPool

# 6 使用示例

## 6.1 定义数据库连接配置文件
```xml
# 数据库类型
dbType=MySQL
# 数据库驱动类
jdbcDriver=com.mysql.jdbc.Driver
# 数据库url
dbUrl=jdbc:mysql://10.192.168.1:3306/test?useSSL=false
# 数据库用户名
dbUsername=user01
# 数据库密码
dbPassword=Uw@0801%
# 初始化数据库连接
initialConnections=5
# 连接池满后自动扩展连接数
incrementalConnections=5
# 最大数据库连接
maxConnections=30
```

## 6.2 连接池的使用
```java
//获得数据库连接池工具类实例
ConnectionPoolUtil pool = ConnectionPoolUtil.getInstance();
//根据db配置文件初始化连接池
pool.initPool("/tmp/dbConnConfig");
		
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
String[] filt={"dd01","test001"};
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
 * 关闭连接池中所有连接
 */
pool.closeConnectionPool();
```
测试输出；
```xml
2020-09-12 17:15:16.347 INFO  com.sywu.dao.ConnectionPoolImpl 261 <init> - dbType:MySQL,dbUrl:jdbc:mysql://10.192.168.1:3306/test?useSSL=false,dbUsername:user01,initialConnections:5,incrementalConnections:5,maxConnections:30
2020-09-12 17:15:16.874 INFO  com.sywu.dao.ConnectionPoolImpl 97 createConnections - 数据库连接:Conn_1a6deeb1-2a97-4307-a2c3-ec99b0970a45被创建并加入连接池中...
2020-09-12 17:15:16.874 INFO  com.sywu.dao.ConnectionPoolImpl 102 createConnections - 数据库连接己创建 ....
2020-09-12 17:15:16.950 INFO  com.sywu.dao.ConnectionPoolImpl 97 createConnections - 数据库连接:Conn_c310ebaa-c880-496e-a4d6-c45a1df51ca8被创建并加入连接池中...
2020-09-12 17:15:16.950 INFO  com.sywu.dao.ConnectionPoolImpl 102 createConnections - 数据库连接己创建 ....
2020-09-12 17:15:17.016 INFO  com.sywu.dao.ConnectionPoolImpl 97 createConnections - 数据库连接:Conn_3b209618-1319-4cac-8a88-8571e9dfbc54被创建并加入连接池中...
2020-09-12 17:15:17.016 INFO  com.sywu.dao.ConnectionPoolImpl 102 createConnections - 数据库连接己创建 ....
2020-09-12 17:15:17.082 INFO  com.sywu.dao.ConnectionPoolImpl 97 createConnections - 数据库连接:Conn_e163d3b7-50e2-46c0-81ec-ff2d9d3a4f7f被创建并加入连接池中...
2020-09-12 17:15:17.082 INFO  com.sywu.dao.ConnectionPoolImpl 102 createConnections - 数据库连接己创建 ....
2020-09-12 17:15:17.152 INFO  com.sywu.dao.ConnectionPoolImpl 97 createConnections - 数据库连接:Conn_313319ac-8a7b-463d-a396-155de18915c0被创建并加入连接池中...
2020-09-12 17:15:17.152 INFO  com.sywu.dao.ConnectionPoolImpl 102 createConnections - 数据库连接己创建 ....
2020-09-12 17:15:17.153 INFO  com.sywu.dao.ConnectionPoolImpl 323 createPool - 数据库连接池创建成功... 
row:1,column:1,columnName:CURRENT_TIMESTAMP(),value:2020-09-12 17:15:07.0
2020-09-12 17:15:17.182 WARN  com.sywu.dao.ConnectionPoolImpl 183 findFreeConnection - Conn_1a6deeb1-2a97-4307-a2c3-ec99b0970a45 连接被使用...
row:1,column:1,columnName:CURRENT_TIMESTAMP(),value:2020-09-12 17:15:07.0
2020-09-12 17:15:17.193 WARN  com.sywu.dao.ConnectionPoolImpl 183 findFreeConnection - Conn_c310ebaa-c880-496e-a4d6-c45a1df51ca8 连接被使用...
row:1,column:1,columnName:dbname,value:lg_tyrz_db03
row:1,column:2,columnName:tabname,value:tyrz_athena_api
row:1,column:3,columnName:partspec,value:day
row:1,column:4,columnName:location,value:hdfs://gdhlwtz/user/lg_tyrz_user03/data/athena-api
2020-09-12 17:15:17.206 INFO  com.sywu.dao.ConnectionPoolImpl 365 returnConnection - Conn_1a6deeb1-2a97-4307-a2c3-ec99b0970a45 连接被返回到连接池中...
row:1,column:1,columnName:CURRENT_TIMESTAMP(),value:2020-09-12 17:15:07.0
2020-09-12 17:15:17.218 WARN  com.sywu.dao.ConnectionPoolImpl 183 findFreeConnection - Conn_1a6deeb1-2a97-4307-a2c3-ec99b0970a45 连接被使用...
row:1,column:1,columnName:CURRENT_TIMESTAMP(),value:2020-09-12 17:15:07.0
2020-09-12 17:15:17.229 WARN  com.sywu.dao.ConnectionPoolImpl 183 findFreeConnection - Conn_3b209618-1319-4cac-8a88-8571e9dfbc54 连接被使用...
row:1,column:1,columnName:dbname,value:lg_tyrz_db03
row:1,column:2,columnName:tabname,value:tyrz_athena_api
row:1,column:3,columnName:partspec,value:day
row:1,column:4,columnName:location,value:hdfs://gdhlwtz/user/lg_tyrz_user03/data/athena-api
2020-09-12 17:15:17.242 INFO  com.sywu.dao.ConnectionPoolImpl 365 returnConnection - Conn_1a6deeb1-2a97-4307-a2c3-ec99b0970a45 连接被返回到连接池中...
row:1,column:1,columnName:CURRENT_TIMESTAMP(),value:2020-09-12 17:15:07.0
2020-09-12 17:15:17.254 WARN  com.sywu.dao.ConnectionPoolImpl 183 findFreeConnection - Conn_1a6deeb1-2a97-4307-a2c3-ec99b0970a45 连接被使用...
row:1,column:1,columnName:CURRENT_TIMESTAMP(),value:2020-09-12 17:15:07.0
2020-09-12 17:15:17.267 WARN  com.sywu.dao.ConnectionPoolImpl 183 findFreeConnection - Conn_e163d3b7-50e2-46c0-81ec-ff2d9d3a4f7f 连接被使用...
sql:com.mysql.jdbc.JDBC42PreparedStatement@5ae50ce6: insert into flume_hdfs_monitor_detail(path,createdate,status) values('hdfs:/tmp/t001.gz','2020-09-12-17:15:17','1'),insert or update:1 rows.
2020-09-12 17:15:17.280 INFO  com.sywu.dao.ConnectionPoolImpl 365 returnConnection - Conn_1a6deeb1-2a97-4307-a2c3-ec99b0970a45 连接被返回到连接池中...
row:1,column:1,columnName:CURRENT_TIMESTAMP(),value:2020-09-12 17:15:08.0
2020-09-12 17:15:17.292 WARN  com.sywu.dao.ConnectionPoolImpl 183 findFreeConnection - Conn_1a6deeb1-2a97-4307-a2c3-ec99b0970a45 连接被使用...
row:1,column:1,columnName:CURRENT_TIMESTAMP(),value:2020-09-12 17:15:08.0
2020-09-12 17:15:17.304 WARN  com.sywu.dao.ConnectionPoolImpl 183 findFreeConnection - Conn_313319ac-8a7b-463d-a396-155de18915c0 连接被使用...
sql:com.mysql.jdbc.JDBC42PreparedStatement@be64738: update flume_hdfs_monitor_detail set status='2',closedate='2020-09-12-17:15:17' where path='hdfs:/tmp/t001.gz' and status=1,insert or update:1 rows.
2020-09-12 17:15:17.317 INFO  com.sywu.dao.ConnectionPoolImpl 365 returnConnection - Conn_1a6deeb1-2a97-4307-a2c3-ec99b0970a45 连接被返回到连接池中...
2020-09-12 17:15:17.317 INFO  com.sywu.dao.ConnectionPoolImpl 409 closeConnectionPool - Connection Pools has 5 connections,closing now...
2020-09-12 17:15:27.318 INFO  com.sywu.dao.ConnectionPoolImpl 421 closeConnectionPool - Connection Pools has closed...
```