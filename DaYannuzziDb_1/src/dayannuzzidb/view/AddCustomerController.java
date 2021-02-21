/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dayannuzzidb.view;

import dayannuzzidb.DaYannuzziDb;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import model.DBconnect;
import model.Query;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Predicate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Customer;

public class AddCustomerController implements Initializable {

    @FXML
    private TextField txtID;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtZip;

    @FXML
    private TextField txtPhone;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    private ComboBox<String> cboCountry;

    @FXML
    private ComboBox<String> cboCity;

    //************************************************************************************
    //       cancel add customer on cancel button
    //************************************************************************************
    @FXML
    void cancelHandler(ActionEvent event) throws IOException {
        //confirmation alert for cancel button
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("Cancel add customer?");
        alert.setContentText("click OK to Cancel now.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Parent parent = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
            Scene scene = new Scene(parent);
            //get stage info
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            window.setScene(scene);
            window.show();
        }
    }

    //************************************************************************************
    //           save customer to customer table in db
    //           insert address into db
    //           then insert customer into db
    //************************************************************************************
    @FXML
    void saveHandler(ActionEvent event) throws IOException {
        //get values from text fields
        String name = txtName.getText();
        String address = txtAddress.getText();
        String address2 = "";//field in db
        String zip = txtZip.getText();
        String phone = txtPhone.getText();
        String active = "1";//new customer is active

        //check to see if all fields are filled with data before saving
        if (name.isEmpty() || address.isEmpty() || zip.isEmpty()
                || phone.isEmpty() || cboCity.getValue() == null
                || cboCountry.getValue() == null) {
            //user tried save button without filling all fields
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("a field is missing");
            alert.setHeaderText("complete fields, choose city, country");
            Optional<ButtonType> result = alert.showAndWait();

        } else if (isNonNumeric(zip) || isNonNumeric(phone)) {
            //check zip or phone is non numeric
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Zipcode and phone must be numeric");
            alert.setHeaderText("phone/zip must be numeric");
            Optional<ButtonType> result = alert.showAndWait();    
        } else {
            
            //assert zip and phone are numeric
            assert(!isNonNumeric(zip) && !isNonNumeric(phone));
                       

            //get values from combobox
            String city = cboCity.getValue();
            String country = cboCountry.getValue();

            //query Db for cityid and countryid       
            String countryid = queryCountry(country);
            String cityid = queryCity(city);

            //get datetime formatted for insertion into db    
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            //get user information from login for insertion into db
            String createdBy = LoginController.userName;
            String lastUpdateBy = LoginController.userName;

            //insert address into address table in db
            insertAddress(address, cityid, zip, phone,
                    date, dateFormat, createdBy, lastUpdateBy);

            //query for addressid
            String addressid = queryAddressID(address);

            //insert customer to db ... returns a customer id
            String customerid = Customer.insertCustomer(name, addressid, date, dateFormat, createdBy, lastUpdateBy);
            //create customer to add to observable list to display on mainscreen.fxml
            Customer customer = new Customer(customerid, name, address, phone,
                    city, country, zip, active);
            Customer.customers.add(customer);

            //return to mainscreen.fxml
            Parent parent = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
            Scene scene = new Scene(parent);
            //get stage info
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            window.setScene(scene);
            window.show();
        }
    }

    //************************************************************************************
    //      is non numeric test 
    //************************************************************************************       
    public boolean isNonNumeric(String s) {
        char[] chars = s.toCharArray();
        for (char c : chars) {
            // if char is not digit and not dash or parenthesis
            if (!Character.isDigit(c)
                    && c != '-') {
                return true;
            }
        }
        return false;
    }

    //************************************************************************************
    //        populate country combobox with values from db
    //************************************************************************************
    @FXML
    private void initializeCountry() {

        String sqlStatement = "select country from country";
        //execute statement and create resultSet object
        Query.makeQuery(sqlStatement);

        //get resultSet
        ResultSet result = Query.getResults();
        cboCountry.getItems().clear();

        try {
            while (result.next()) {
                cboCountry.getItems().add(result.getString("country"));
            }

        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }
    }

    //************************************************************************************
    //       populate city combobox with values from db
    //************************************************************************************
    @FXML
    private void initializeCity(ActionEvent event) {
        String country = cboCountry.getValue();
        String sqlStatement = "SELECT city.city "
                + "FROM city, country "
                + "WHERE city.countryid = country.countryid "
                + "AND country.country = \"" + country + "\";";

        //execute statement and create resultSet object
        Query.makeQuery(sqlStatement);

        //get resultSet
        ResultSet result = Query.getResults();
        cboCity.getItems().clear();

        try {
            while (result.next()) {
                cboCity.getItems().add(result.getString("city"));
            }

        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }
    }

    //************************************************************************************
    //        query db for countryid from country selected in combobox return countryid
    //************************************************************************************    
    public String queryCountry(String country) {

        String countryid = "";
        String sqlStatement = "select countryid from country where country = \""
                + country + "\";";
        //execute statement and create resultSet object
        Query.makeQuery(sqlStatement);
        //get resultSet
        ResultSet result = Query.getResults();

        try {
            while (result.next()) {
                countryid = result.getString("countryid");
            }

        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }
        return countryid;
    }

    //************************************************************************************
    //          query db for cityid from selected city in combobox return cityid
    //************************************************************************************    
    public String queryCity(String city) {
        String cityid = "";
        String sqlStatement = "select cityid from city where city = \""
                + city + "\";";
        //execute statement and create resultSet object
        Query.makeQuery(sqlStatement);
        //get resultSet
        ResultSet result = Query.getResults();

        try {
            while (result.next()) {
                cityid = result.getString("cityid");
            }

        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }
        return cityid;
    }

    //************************************************************************************
    //            query db for addressid for newly inserted address  return addressid
    //************************************************************************************
    public String queryAddressID(String address) {
        String addressid = "";
        String sqlStatement = "select addressid from address where address = \""
                + address + "\";";
        //execute statement and create resultSet object
        Query.makeQuery(sqlStatement);
        //get resultSet
        ResultSet result = Query.getResults();

        try {
            while (result.next()) {
                addressid = result.getString("addressid");

            }

        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }

        return addressid;
    }

//************************************************************************************
//            method to insert address into address table in db
//************************************************************************************    
    public void insertAddress(String address, String cityid, String zip,
            String phone, Date date, DateFormat dateFormat,
            String createdBy, String lastUpdateBy) {
        String address2 = "";
        //insert address into address table in db
        String sqlStatement = "insert into address(address, address2,"
                + " cityid, postalCode, phone, createDate,"
                + " createdBy, lastUpdateBy) values(\""
                + address + "\", \""
                + address2 + "\", \""
                + cityid + "\", \""
                + zip + "\", \""
                + phone + "\", \""
                + dateFormat.format(date) + "\", \""
                + createdBy + "\", \""
                + lastUpdateBy + "\"); ";

        //execute statement and create resultSet object
        Query.makeQuery(sqlStatement);

    }

    //************************************************************************************
    //      initialize 
    //************************************************************************************       
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //get connection
        try {
            DBconnect.makeConnection();
            initializeCountry();
        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());

        }

    }

}
