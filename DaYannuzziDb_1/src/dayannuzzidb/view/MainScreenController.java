/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dayannuzzidb.view;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import model.Query;

public class MainScreenController implements Initializable {

    @FXML
    private TableView<Customer> custTableView;

    @FXML
    private TableColumn<?, ?> custID;

    @FXML
    private TableColumn<?, ?> custName;

    @FXML
    private TableColumn<?, ?> custAddress;

    @FXML
    private TableColumn<?, ?> custPhone;

    @FXML
    private TableView<Appointment> apptTableView;

    @FXML
    private TableColumn<?, ?> apptCustName;

    @FXML
    private TableColumn<?, ?> apptType;

    @FXML
    private TableColumn<?, ?> apptStart;

    @FXML
    private TableColumn<?, ?> apptEnd;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnModify;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnExit;

    @FXML
    private Button btnAppt;

    @FXML
    private Button btnModifyAppt;

    @FXML
    private Button btnDeleteAppt;

    @FXML
    private RadioButton radioMonth;

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private RadioButton radioWeek;

    @FXML
    private Button btnReport;

    static Customer selectedCustomer;

    static Appointment selectedAppt;

    static ObservableList<Appointment> monthlyAppts = FXCollections.observableArrayList();
    static ObservableList<Appointment> weeklyAppts = FXCollections.observableArrayList();
    @FXML
    private Button btnSearch;
    @FXML
    private TextField txtSearch;

    //************************************************************************************
    //      exit program action event
    //************************************************************************************       
    @FXML
    void exitHandler(ActionEvent event) {
        System.exit(0);
    }

    //************************************************************************************
    //      go to reports page action event
    //************************************************************************************       
    @FXML
    void reportHandler(ActionEvent event) throws IOException {

        Parent parent = FXMLLoader.load(getClass().getResource("reports.fxml"));
        Scene scene = new Scene(parent);

        //This line gets the Stage information
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(scene);
        window.show();
    }

    //************************************************************************************
    //      add customer handler action event
    //************************************************************************************   
    @FXML
    void addHandler(ActionEvent event) throws IOException {

        Parent parent = FXMLLoader.load(getClass().getResource("addCustomer.fxml"));
        Scene scene = new Scene(parent);

        //This line gets the Stage information
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(scene);
        window.show();
    }

    //************************************************************************************
    //      search customers 
    //************************************************************************************    
    @FXML
    private void searchHandler(ActionEvent event) throws SQLException {
        //query db for name similar to search textfield entry
        String sqlStatement = "select * from customer "
                + "where customerName like \"" + '%' + txtSearch.getText() + '%' + "\"; ";
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResults();

        while (result.next()) {
            //loop through customers to locate searched for customer
            for (Customer customer : Customer.customers) {
                //highlight customer searched for and scroll if necessary
                if (result.getString("customerid").equalsIgnoreCase(customer.getCustomerID())) {
                    custTableView.getSelectionModel().select(customer);
                    custTableView.scrollTo(customer);
                }
            }

        }
    }

    //************************************************************************************
    //      delete customer handler action event
    //************************************************************************************       
    @FXML
    void deleteHandler(ActionEvent event) {

        try {
            Customer customer = custTableView.getSelectionModel().getSelectedItem();
            String customerid = customer.getCustomerID();
            //call delete customer from Db
            Customer.deleteCustomer(customer);
        } catch (Exception e) {
            //user tried delete button without selecting item from table
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No item selected");
            alert.setHeaderText("Select a customer from table to delete.");
            Optional<ButtonType> result = alert.showAndWait();
        }

    }

    //************************************************************************************
    //      modify appt handler action event
    //************************************************************************************   
    @FXML
    void modifyHandler(ActionEvent event) {
        try {
            //check selection and store in selectedCustomer to modify 
            selectedCustomer = custTableView.getSelectionModel().getSelectedItem();

            Parent parent = FXMLLoader.load(getClass().getResource("modifyCustomer.fxml"));
            Scene scene = new Scene(parent);

            //This line gets the Stage information
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            //user tried delete button without selecting item from table
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No customer selected");
            alert.setHeaderText("Select a customer from table to modify.");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    //************************************************************************************
    //      add appt handler action event
    //************************************************************************************       
    @FXML
    void apptHandler(ActionEvent event) {
        try {
            //check selection and store in selectedCustomer to modify 
            selectedCustomer = custTableView.getSelectionModel().getSelectedItem();
            String selectedid = selectedCustomer.getCustomerID();

            Parent parent = FXMLLoader.load(getClass().getResource("addAppt.fxml"));
            Scene scene = new Scene(parent);

            //This line gets the Stage information
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            //user tried delete button without selecting item from table
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No customer selected");
            alert.setHeaderText("Select a customer from table to make appt with");
            Optional<ButtonType> result = alert.showAndWait();
        }

    }

    //************************************************************************************
    //      modify appt handler action event
    //************************************************************************************   
    @FXML
    void modifyApptHandler(ActionEvent event) {
        try {
            //check selection and store in selectedAppt to modify 
            selectedAppt = apptTableView.getSelectionModel().getSelectedItem();
            String apptid = selectedAppt.getApptID();

            Parent parent = FXMLLoader.load(getClass().getResource("modifyAppointment.fxml"));
            Scene scene = new Scene(parent);

            //This line gets the Stage information
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            //user tried delete button without selecting item from table
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Appointment selected");
            alert.setHeaderText("Select Appointment from table to modify.");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    //************************************************************************************
    //      delete appointment action event
    //************************************************************************************   
    @FXML
    void deleteApptHandler(ActionEvent event) {
        try {
            Appointment appt = apptTableView.getSelectionModel().getSelectedItem();
            String apptid = appt.getApptID();
            //call delete appt from db
            Appointment.deleteAppt(appt);

        } catch (Exception e) {
            //user tried delete button without selecting item from table
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Appointment selected");
            alert.setHeaderText("Select Appointment from table to delete.");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    //************************************************************************************
    //      show appts in monthly format action event on radio button
    //************************************************************************************       
    @FXML
    void monthlyHandler() {
        monthlyAppts.removeAll(monthlyAppts);
        //get date        
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int localyear = localDate.getYear();
        int localmonth = localDate.getMonthValue();
        int localday = localDate.getDayOfMonth();
        int daysInMonth = localDate.lengthOfMonth();

        for (Appointment appt : Appointment.appts) {
            String startDate = appt.getApptStart();
            int year = Integer.parseInt(startDate.substring(0, 4));
            int month = Integer.parseInt(startDate.substring(5, 7));
            int day = Integer.parseInt(startDate.substring(8, 10));
            //if appointment is in the current month
            if ((year == localyear) && (month == localmonth) && (day >= localday)) {
                monthlyAppts.add(appt);
            }
        }
        apptTableView.setItems(monthlyAppts);
    }

    //************************************************************************************
    //      show appointments in weekly format action event on radio button
    //************************************************************************************       
    @FXML
    void weeklyHandler(ActionEvent event) {
        //clear out weekly appts
        weeklyAppts.removeAll(weeklyAppts);
        //get local date
        LocalDate firstOfWeek;
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        //get day of week enum 
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();

        switch (dayOfWeek) {
            case MONDAY: {
                firstOfWeek = localDate;
                break;
            }
            case TUESDAY: {
                firstOfWeek = localDate.minusDays(1);
                break;
            }
            case WEDNESDAY: {
                firstOfWeek = localDate.minusDays(2);
                break;
            }
            case THURSDAY: {
                firstOfWeek = localDate.minusDays(3);
                break;
            }
            case FRIDAY: {
                firstOfWeek = localDate.minusDays(4);
                break;
            }
            case SATURDAY: {
                firstOfWeek = localDate.plusDays(2);
                break;
            }
            case SUNDAY: {
                firstOfWeek = localDate.plusDays(1);
                break;
            }
            default: {
                firstOfWeek = null;
                break;
            }
        }
        //formatter for localDate object
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //loop through all appts and add weekly to weekly observable list
        for (Appointment appt : Appointment.appts) {
            //get startdate string 
            String startDate = appt.getApptStart();
            //remove the time 
            String apptDate = appt.getApptStart().substring(0, 10);
            //convert string to localDate object
            LocalDate apptDateObj = LocalDate.parse(apptDate, dateTimeFormatter);

            //if appt date is in current week ..after sunday ..&&..before saturday
            if (apptDateObj.isAfter(firstOfWeek.minusDays(1))
                    && (apptDateObj.isBefore(firstOfWeek.plusDays(5)))) {
                weeklyAppts.add(appt);
            }
        }
        apptTableView.setItems(weeklyAppts);

    }

    //************************************************************************************
    //      initalize  
    //************************************************************************************   
    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //set up the columns for data
        custID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        custName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        custAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        custPhone.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
        //populate tableview w customers
        custTableView.setItems(Customer.customers);

        //set up the columns for data
        apptCustName.setCellValueFactory(new PropertyValueFactory<>("apptCustName"));
        apptType.setCellValueFactory(new PropertyValueFactory<>("apptType"));
        apptStart.setCellValueFactory(new PropertyValueFactory<>("apptStart"));
        apptEnd.setCellValueFactory(new PropertyValueFactory<>("apptEnd"));
        //populate tableview w all appointments
        Appointment.getAppts();
        apptTableView.setItems(Appointment.appts);

    }

}
