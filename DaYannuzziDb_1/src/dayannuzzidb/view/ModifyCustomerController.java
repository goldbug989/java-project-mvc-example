package dayannuzzidb.view;

import dayannuzzidb.DaYannuzziDb;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.Customer;
import model.DBconnect;
import model.Query;

public class ModifyCustomerController implements Initializable {

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    private ComboBox<String> cboCountry;

    @FXML
    private ComboBox<String> cboCity;

    @FXML
    private RadioButton radioActive;

    @FXML
    private ToggleGroup statusToggle;

    @FXML
    private RadioButton radioInactive;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtZipcode;

    @FXML
    void activeHandler(ActionEvent event) {

    }

    @FXML
    void inactiveHandler(ActionEvent event) {

    }

    //************************************************************************************
    //       cancel button handler
    //************************************************************************************    
    @FXML
    void cancelHandler(ActionEvent event) throws IOException {
            //return to mainscreen
            Parent parent = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
            Scene scene = new Scene(parent);
            //get stage info
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            window.setScene(scene);
            window.show();
        
    }

    //************************************************************************************
    //        save modified customer on save button
    //************************************************************************************
    @FXML
    void saveHandler(ActionEvent event) throws IOException {

        String name = txtName.getText();
        String address = txtAddress.getText();
        String zip = txtZipcode.getText();
        String phone = txtPhone.getText();
        String cityid, active;

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
            alert.setHeaderText("enter numbers for phone and/or zipcode");
            Optional<ButtonType> result = alert.showAndWait();            
            
            
        } else {

            //check radio button for active/inactive
            active = (radioActive.isSelected()) ? "1" : "0";
            //get city
            String city = cboCity.getValue();
            //get cityid from city table
            cityid = queryCity(city);
            //get country 
            String country = cboCountry.getValue();
            //get user from login
            String lastUpdateBy = LoginController.userName;
            //modify address in address table in db 
            modifyAddress(address, cityid, zip, phone, lastUpdateBy);
            //modify customer in customer table in db
            modifyCustomer(name, active, lastUpdateBy);
            //create new customer to replace existing   
            Customer modCustomer = new Customer(
                    MainScreenController.selectedCustomer.getCustomerID(),
                    name, address, phone, city, country, zip, active);
            //get index and replace existing customer
            int index = Customer.customers.indexOf(MainScreenController.selectedCustomer);
            Customer.customers.set(index, modCustomer);

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
            if (!Character.isDigit(c)
                    && c != '-'
                    && c != '('
                    && c != ')') {
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
    private void initializeCity() {

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
//            method to update address table in db
//************************************************************************************    
    public void modifyAddress(String address, String cityid, String zip,
            String phone, String lastUpdateBy) {

        String sqlStatement = "update address "
                + "set address = \"" + address + "\", "
                + "cityid = \"" + cityid + "\", "
                + "postalCode = \"" + zip + "\", "
                + "phone = \"" + phone + "\", "
                + "lastUpdateBy = \"" + lastUpdateBy + "\""
                + "where address = \""
                + MainScreenController.selectedCustomer.getCustomerAddress() + "\";";

        //execute statement and create resultSet object
        Query.makeQuery(sqlStatement);
    }

//************************************************************************************
//            method to update customer table in db
//************************************************************************************        
    public void modifyCustomer(String name, String active, String lastUpdateBy) {

        String sqlStatement = "update customer "
                + "set customerName = \"" + name + "\", "
                + "active = \"" + active + "\", "
                + "lastUpdateBy = \"" + lastUpdateBy + "\""
                + "where customerid = \""
                + MainScreenController.selectedCustomer.getCustomerID() + "\";";

        //execute statement and create resultSet object
        Query.makeQuery(sqlStatement);
    }
//******************************************************************************

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //populate fields with selected customer from mainscreen
        txtName.setText(MainScreenController.selectedCustomer.getCustomerName());
        txtAddress.setText(MainScreenController.selectedCustomer.getCustomerAddress());
        txtPhone.setText(MainScreenController.selectedCustomer.getCustomerPhone());
        cboCity.getSelectionModel().select(MainScreenController.selectedCustomer.getCustomerCity());
        cboCountry.getSelectionModel().select(MainScreenController.selectedCustomer.getCustomerCountry());
        txtZipcode.setText(MainScreenController.selectedCustomer.getCustomerZip());
        //set status active or inactive radio button
        if (Integer.parseInt(MainScreenController.selectedCustomer.getCustomerActive()) == 0) {
            radioInactive.setSelected(true);
        }

        //get connection
        try {
            DBconnect.makeConnection();
            initializeCountry();
            initializeCity();
        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }

    }

}
