/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.ResultSet;
import java.sql.Statement;
import static model.DBconnect.conn;

/**
 *
 * @author macintoshhd
 */
public class Query {

    private static String query;
    private static Statement stmt;
    private static ResultSet result;

    public static void makeQuery(String q) {
        query = q;

        try {
            //create statement object
            stmt = conn.createStatement();

            //determine query type for execution
            if (q.toLowerCase().startsWith("select")) {
                result = stmt.executeQuery(query);
            }

            if (q.toLowerCase().startsWith("delete") || q.toLowerCase().startsWith("insert") || q.toLowerCase().startsWith("update")) {
                stmt.executeUpdate(query);
            }

        } catch (Exception e) {
            System.out.println("error " + e.getMessage());

        }
    }

    public static ResultSet getResults() {
        return result;
    }

}
