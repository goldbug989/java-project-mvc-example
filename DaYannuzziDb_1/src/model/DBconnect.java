/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author macintoshhd
 */
public class DBconnect {

    private static final String dbName = "U05Ajn";
    private static final String db_url = "jdbc:mysql://3.227.166.251/" + dbName;
    private static final String userName = "U05Ajn";
    private static final String passWord = "53688444773";
    private static final String driver = "com.mysql.jdbc.Driver";
    static Connection conn;

    public static void makeConnection() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        conn = (Connection) DriverManager.getConnection(db_url, userName, passWord);

    }

    public static void closeConnection() throws ClassNotFoundException, SQLException {
        conn.close();
    }
}
