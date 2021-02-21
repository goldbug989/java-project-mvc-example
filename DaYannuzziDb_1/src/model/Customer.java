/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import dayannuzzidb.DaYannuzziDb;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author daYannuzzi
 */
public class Customer {

    private final StringProperty customerID;
    private final StringProperty customerName;
    private final StringProperty customerAddress;
    private final StringProperty customerPhone;
    private final StringProperty customerCity;
    private final StringProperty customerCountry;
    private final StringProperty customerZip;
    private final StringProperty customerActive;

    public static ObservableList<Customer> customers = FXCollections.observableArrayList();

    public Customer(String customerID, String customerName,
            String customerAddress, String customerPhone,
            String customerCity, String customerCountry,
            String customerZip, String customerActive) {

        //constructor with ID
        this.customerID = new SimpleStringProperty(customerID);
        this.customerName = new SimpleStringProperty(customerName);
        this.customerAddress = new SimpleStringProperty(customerAddress);
        this.customerPhone = new SimpleStringProperty(customerPhone);
        this.customerCity = new SimpleStringProperty(customerCity);
        this.customerCountry = new SimpleStringProperty(customerCountry);
        this.customerZip = new SimpleStringProperty(customerZip);
        this.customerActive = new SimpleStringProperty(customerActive);

    }

    //***************************************************************** 
    //                     getters
    //***************************************************************** 
    public String getCustomerID() {
        return customerID.get();
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public String getCustomerAddress() {
        return customerAddress.get();
    }

    public String getCustomerPhone() {
        return customerPhone.get();
    }

    public String getCustomerCity() {
        return customerCity.get();
    }

    public String getCustomerCountry() {
        return customerCountry.get();
    }

    public String getCustomerZip() {
        return customerZip.get();
    }

    public String getCustomerActive() {
        return customerActive.get();
    }

    //***************************************************************** 
    //setters
    //***************************************************************** 
    public void setCustomerID(String value) {
        customerID.set(value);
    }

    public void setCustomerName(String value) {
        customerName.set(value);
    }

    public void setCustomerAddress(String value) {
        customerAddress.set(value);
    }

    public void setCustomerPhone(String value) {
        customerPhone.set(value);
    }

    public void setCustomerCity(String value) {
        customerID.set(value);
    }

    public void setCustomerCountry(String value) {
        customerName.set(value);
    }

    public void setCustomerZip(String value) {
        customerAddress.set(value);
    }

    public void setCustomerActive(String value) {
        customerPhone.set(value);
    }

    //************************************************************************************
    //        method to insert customer into customer table in db
    //        after insertion , query db for customerid and return customerid
    //************************************************************************************
    public static String insertCustomer(String name, String addressid,
            Date date, DateFormat dateFormat, String createdBy, String lastUpdateBy) {

        String customerid = "";
        String active = "1";

        //insert customer into address table
        String sqlStatement = "insert into customer(customerName, "
                + " addressid, active, createDate, createdBy, lastUpdateBy) "
                + " values(\""
                + name + "\", \""
                + addressid + "\", \""
                + active + "\", \""
                + dateFormat.format(date) + "\", \""
                + createdBy + "\", \""
                + lastUpdateBy + "\"); ";

        //execute statement and create resultSet object
        Query.makeQuery(sqlStatement);

        sqlStatement = "select customerid from customer where "
                + "customerName = \"" + name + "\"; ";

        //execute statement and create resultSet object
        Query.makeQuery(sqlStatement);
        //get resultSet
        ResultSet result = Query.getResults();

        try {
            while (result.next()) {
                customerid = result.getString("customerid");

            }

        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }

        return customerid;
    }

    //*****************************************************************
    //                 delete customer
    //*****************************************************************                         
    public static boolean deleteCustomer(Customer customer) {

        try {
            DBconnect.makeConnection();
            //*************************************************
            //write sql to remove address from db
            String sqlStatement = "delete from address where "
                    + "address = \"" + customer.getCustomerAddress() + "\";";

            //execute statement and/or create resultSet object
            Query.makeQuery(sqlStatement);

            //************************************************
            //write sql statement to remove customer from db
            sqlStatement = "delete from customer where "
                    + "customerid = " + customer.getCustomerID() + ";";

            //execute statement and/or create resultSet object
            Query.makeQuery(sqlStatement);
            DBconnect.closeConnection();

        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }
        return customers.remove(customer);
    }

    //*****************************************************************                         
    //                get customers from db
    //*****************************************************************       
    public static void getCustomers() {
        try {
            //write sql statement to get customer data
            String sqlStatement = "select customer.customerid, customer.customerName,\n"
                    + "address.address, address.phone, city.city,\n"
                    + "country.country, address.postalCode, customer.active\n"
                    + " from customer join address\n"
                    + " on customer.addressid=address.addressid\n"
                    + " join city \n"
                    + " on address.cityid=city.cityid\n"
                    + " join country on \n"
                    + " city.countryid = country.countryid;";

            //execute statement and create resultSet object
            Query.makeQuery(sqlStatement);

            //get resultSet
            ResultSet result = Query.getResults();

            //remove customers to make way for updated ones
            Customer.customers.removeAll(customers);

            while (result.next()) {
                String id = result.getString("customerID");
                String name = result.getString("customerName");
                String address = result.getString("address");
                String phone = result.getString("phone");
                String city = result.getString("city");
                String country = result.getString("country");
                String zip = result.getString("postalCode");
                String active = result.getString("active");

                //create customers from table
                Customer customer = new Customer(id, name, address, phone,
                        city, country, zip, active);
                //add all customers to customerlist
                Customer.customers.add(customer);
            }
        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }

    }

}
