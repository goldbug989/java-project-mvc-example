/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import dayannuzzidb.DaYannuzziDb;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author macintoshhd
 */
public class User {

    private final StringProperty userID;
    private final StringProperty userName;
    private final StringProperty userPassword;

    public static ObservableList<User> users = FXCollections.observableArrayList();

    public User(String userID, String userName,
            String userPassword) {

        //default constructor
        this.userID = new SimpleStringProperty(userID);
        this.userName = new SimpleStringProperty(userName);
        this.userPassword = new SimpleStringProperty(userPassword);

    }

    //***************************************************************** 
    //          getters
    //***************************************************************** 
    public String getUserID() {
        return userID.get();
    }

    public String getUserName() {
        return userName.get();
    }

    public String getUserPassword() {
        return userPassword.get();
    }

    //***************************************************************** 
    //       setters
    //***************************************************************** 
    public void setUserID(String value) {
        userID.set(value);
    }

    public void setUserName(String value) {
        userName.set(value);
    }

    public void setUserPassword(String value) {
        userPassword.set(value);
    }

    public static void getUsers() {
        try {
            //write sql statement to get user data
            String sqlStatement = "select userid, userName, password "
                    + "from user;";

            //execute statement and create resultSet object
            Query.makeQuery(sqlStatement);

            //get resultSet
            ResultSet result = Query.getResults();

            //remove users to make way for updated ones
            User.users.removeAll(users);

            while (result.next()) {
                String id = result.getString("userid");
                String name = result.getString("userName");
                String password = result.getString("password");

                //create user from db
                User user = new User(id, name, password);

                //add all users to user list
                User.users.add(user);
            }
        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }
    }

}
