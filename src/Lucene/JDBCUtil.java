package Lucene;

import java.sql.*;
import java.util.Vector;

/**
 * Created by sony on 16-11-28.
 */
public class JDBCUtil {
    Connection conn;
    PreparedStatement ps;
    ResultSet rs = null;
    /**
     * 写一个连接数据库的方法
     */
    public Connection getConnection(){
        String url="jdbc:mysql://data.asec.buptnsrc.com:13306/asec";
        String userName="root";
        String password="123456";
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("找不到驱动！");
            e.printStackTrace();
        }
        try {
            conn=DriverManager.getConnection(url, userName, password);
            if(conn!=null){
                System.out.println("connection successful");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println( "connection fail");
            e.printStackTrace();
        }
        return conn;
    }
    /**
     * 写一个查询数据库语句的方法
     */
    public ResultSet QuerySql(String sql){
        //1、执行静态SQL语句。通常通过Statement实例实现。
        // 2、执行动态SQL语句。通常通过PreparedStatement实例实现。
        // 3、执行数据库存储过程。通常通过CallableStatement实例实现。
//        Vector<String> vector = new Vector<String>();
        System.out.println("query");
//		j.Connection();
//        String sql="select * from userInfo";
        try {
            conn=getConnection();//连接数据库
            ps=conn.prepareStatement(sql);// 2.创建Satement并设置参数
            rs=ps.executeQuery(sql);  // 3.ִ执行SQL语句
            System.out.println(rs.next());
//            while (rs.next()) {
//                System.out.println(rs.getString(16));
//
//
//            }
            return rs;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return rs;
        }
//        finally{
//            //释放资源
//            try {
//                rs.close();
//                ps.close();
//                conn.close();
//            } catch (SQLException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
    }

    public void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) { // 关闭记录集
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stmt != null) { // 关闭声明
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) { // 关闭连接对象
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
