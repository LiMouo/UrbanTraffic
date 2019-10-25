package top.codepy.urbantraffic.LoginCatalog;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DBConnect {
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String url = "jdbc:mysql://192.168.3.5:3306/tpdb";
    private static final String user = "root";
    private static final String pass = "root";
    private static final String TAG = "DBConnect";
    Connection connection = null;
    PreparedStatement statement = null;

    public void linkMysql() {

        try {
            Class.forName(driver).newInstance();
            Log.e(TAG, "Mysql 8.0 驱动加载成功" );
            connection = DriverManager.getConnection(url,user,pass);
            String sql = "SELECT * FROM suser";
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Log.e(TAG, "成功:" +resultSet.getString("username"));
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "数据库连接成功" );

    }
}
