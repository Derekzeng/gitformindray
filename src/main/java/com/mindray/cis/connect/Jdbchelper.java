/**
 * 
 */
package com.mindray.cis.connect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

/**
 * @author 50234332
 *
 */
public class Jdbchelper implements IConnect {
	private static final Logger log = LoggerFactory.getLogger(Jdbchelper.class);

	private static Connection conn = null;

	private static Jdbchelper jdbchelper;

	// 定义 数据库驱动
	private static String driverClass;
	// 定义 数据库的连接
	private static String url;
	// 定义 数据库用户
	private static String username;
	// 定义 数据库用户的密码
	private static String password;
	// 定义 数据库中访问的表
	private static String table;

	// 静态代码块
	static {
		try {
			// 1、创建一个输入流，把文本里的信息写入输入流中
			InputStream in = Jdbchelper.class.getClassLoader().getResourceAsStream("config.properties");
			// 2、创建一个properties对象，用于一会获取之前文本里面的配置信息
			Properties props = new Properties();
			// 在props对象中可以进行加载属性列表到Properties类对象
			props.load(in);
			// 获取键值对的信息
			driverClass = props.getProperty("driverClass");
			url = props.getProperty("url");
			username = props.getProperty("username");
			password = props.getProperty("password");
			table = props.getProperty("table");
			// 已经获取过配置文件中的属性键值对，将字节输入流进行释放关闭
			in.close();
		} catch (IOException e) {
			log.error(e.toString());
		}

		try {
			// 类加载-->驱动
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			log.error(e.toString());
		}
	}

	private synchronized static Jdbchelper getInstance() {
		if (null == jdbchelper) {
			jdbchelper = new Jdbchelper();
		}
		return jdbchelper;
	}

	/**
	 * 获取连接
	 * 
	 * @return: conn
	 */
	private Connection getConn() {
		if (conn == null) {
			try {
				// 连接类型 连接对象 = 驱动管理中的获取连接(连接，用户名，密码)
				conn = DriverManager.getConnection(url, username, password);
			} catch (Exception e) {
				log.error(e.toString());
			}
		}
		// 将连接进行返回
		return conn;
	}

	/**
	 * 释放资源 传递三个参数: 结果集对象 ，处理Sql语句对象 , 连接对象 无返回值状态
	 */
	public static void release(ResultSet rs, Statement stmt, Connection conn) {
		// 如果 结果集中不为空
		if (rs != null) {
			try {
				rs.close();// 将结果集中关闭
			} catch (SQLException e) {
				log.error(e.toString());
			}
			rs = null;
		}
		// 如果处理Sql语句对象不为空
		if (stmt != null) {
			try {
				stmt.close();// 将处理Sql语句对象关闭
			} catch (SQLException e) {
				log.error(e.toString());
			}
			stmt = null;
		}
		// 如果 连接不为空
		if (conn != null) {
			try {
				conn.close();// 将连接关闭
			} catch (SQLException e) {
				log.error(e.toString());
			}
			conn = null;
		}
	}

	// 获取列表
	@SuppressWarnings("unused")
	private Map<String, Object> findOne(String sql, Object... params) {
		Map<String, Object> one = new HashMap<String, Object>();
		try {
			Connection conn = Jdbchelper.getInstance().getConn();
			PreparedStatement ps = conn.prepareStatement(sql);
			if (null != params) {
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
			}
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsm = rs.getMetaData();
			while (rs.next()) {
				for (int i = 1; i <= rsm.getColumnCount(); i++) {
					String columnName = rsm.getColumnLabel(i);
					Object value = rs.getObject(columnName);
					one.put(columnName, value);
				}
			}
		} catch (Exception e) {
			log.error(e.toString());
		}
		return one;
	}

	// 获取列表
	private List<Map<String, Object>> findList(String sql/* , Object... params */) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			Connection conn = Jdbchelper.getInstance().getConn();
			PreparedStatement ps = conn.prepareStatement(sql);
//			if (null != params) {
//				for (int i = 0; i < params.length; i++) {
//					ps.setObject(i + 1, params[i]);
//				}
//			}
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ResultSetMetaData rsm = rs.getMetaData();
				Map<String, Object> map1 = new HashMap<String, Object>();
				for (int i = 1; i <= rsm.getColumnCount(); i++) {
					String columnName = rsm.getColumnLabel(i); // 获取别名
					// String columnName=rsm.getColumnName(i); //获取原来名字
					Object value = rs.getObject(columnName);
					map1.put(columnName, value);
				}
				list.add(map1);
			}
		} catch (Exception e) {
			log.error(e.toString());
		}
		return list;
	}

	// 删除 修改 新增保存
	@SuppressWarnings("unused")
	private int executeSave(String sql, Object... params) {
		int result = 0;
		try {
			Connection conn = Jdbchelper.getInstance().getConn();
			PreparedStatement ps = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
			}
			result = ps.executeUpdate();
		} catch (Exception e) {
			log.error(e.toString());
		}
		return result;
	}

	public static List<Map<String, Object>> getPatientInfoById(String fieldName, String patientId) {
		String sql = "select * from " + table + " where " + fieldName + "=" + patientId;
		log.info("query sql is:{}", sql);

		List<Map<String, Object>> list = Jdbchelper.getInstance().findList(sql);
		log.info("query result is:{}", list.toString());

		return list;
	}

//	public static void main(String[] args) {
//		String sql = "select * from " + table + " where patient_id=2";
//		log.info("query sql is:{}", sql);
//
//		List<Map<String, Object>> list = Jdbchelper.getInstance().findList(sql);
//		log.info("query result is:{}", list.toString());
//
//		for (int i = 0; i < list.size(); i++) {
//			Map<String, Object> map = list.get(i);
//			log.info("patient_id is:" + map.get("patient_id").toString());
//			log.info("name is:" + map.get("name").toString());
//			log.info("gender is:" + map.get("gender").toString());
//			log.info("age is:" + map.get("age").toString());
//			log.info("room is:" + map.get("room").toString());
//			log.info("bed is:" + map.get("bed").toString());
//			log.info("diag is:" + map.get("diag").toString());
//		}
//	}
}
