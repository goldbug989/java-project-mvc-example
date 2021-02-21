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
import java.time.DayOfWeek;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.DBconnect;
import model.Query;

/**
 * FXML Controller class
 *
 * @author macintoshhd
 */
public class ModifyAppointmentController implements Initializable {

    //Customer customer = MainScreenController.selectedCustomer;
    Appointment appt = MainScreenController.selectedAppt;

    @FXML
    private TextField txtCustomer;

    @FXML
    private TextField txtTitle;

    @FXML
    private TextField txtContact;

    @FXML
    private ComboBox<String> cboType;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> cboStart;

    @FXML
    private ComboBox<String> cboEnd;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    private ComboBox<String> cboLocation;

    String sdate = appt.getApptStart();

    //************************************************************************************
    //       cancel add customer on cancel button
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
    //      datepicker action event
    //************************************************************************************        
    @FXML
    void dateHandler(ActionEvent event) {

        sdate = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        DayOfWeek dayOfWeek = datePicker.getValue().getDayOfWeek();
        //Warning for Saturday or Sunday!
        if ((dayOfWeek.equals(DayOfWeek.SATURDAY) || (dayOfWeek.equals(DayOfWeek.SUNDAY)))
                || datePicker.getValue().isBefore(LocalDate.now())) {
            //confirmation alert for cancel button
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("invalid appointment day");
            alert.setHeaderText("Date must be MON-FRI in future");
            alert.setContentText("click OK!");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                datePicker.setValue(LocalDate.now());
            }
        }

        initializeStart();

    }

    //************************************************************************************
    //       save button event save appointment
    //************************************************************************************        
    @FXML
    void saveHandler(ActionEvent event) throws IOException {

        //get text fields and combobox selections
        //and save to strings for update of db
        String title = txtTitle.getText();
        String contact = txtContact.getText();
        String type = cboType.getValue();
        String loc = cboLocation.getValue();
        String desc = "na";
        String custName = appt.getApptCustName();
        String custID = appt.getApptCustID();

        //get userid from login screen
        String userID = LoginController.userid;

        //get appt start
        String start = sdate.substring(0, 10) + " " + cboStart.getValue();

        //get appt end note-end is length of appt value
        String end = sdate.substring(0, 10) + " " + cboEnd.getValue();

        //convert start time to utc based on location
        String formattedUTCstart = Appointment.getUTCdate(start, loc);
        String formattedUTCend = Appointment.getUTCdate(end, loc);

        //get user info for update fields in db
        String createdBy = LoginController.userName;
        String lastUpdateBy = LoginController.userName;
        String apptid = appt.getApptID();

        //get datetime formatted for insertion into db    
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        //check to see if all fields are filled with data before saving
        if (title.isEmpty() || contact.isEmpty() || type.isEmpty()
                || loc.isEmpty() || datePicker.getValue() == null
                || cboStart.getValue() == null || cboEnd.getValue() == null) {
            //user tried save button without completing fields
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("a field, date, or time is empty");
            alert.setHeaderText("complete all fields, date, and time pickers");
            Optional<ButtonType> result = alert.showAndWait();
        } else {
            //check times against existing appointments
            if (Appointment.checkOverlap(formattedUTCstart, formattedUTCend))
                {            
                //call method to update db at existing apptID 
                Appointment.updateAppt(appt, userID, title,
                        desc, loc, contact, type, formattedUTCstart,
                        formattedUTCend, lastUpdateBy);

                //create new appt object
                Appointment modifiedAppt = new Appointment(apptid, custID, custName, userID,
                        title, desc, loc, contact, type, formattedUTCstart,
                        formattedUTCend);

                //get index of appt to remove and replace w modified appt.
                int index = Appointment.appts.indexOf(appt);
                Appointment.appts.set(index, modifiedAppt);
                }
            else{
                //alert user of appointment overlap
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("times not available");
                alert.setHeaderText("check appointment table and choose different times");
                Optional<ButtonType> result = alert.showAndWait();                
            }            

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
    //        method to insert appointment into customer table in db
    //************************************************************************************
    public void updateAppt(String userID,
            String title, String desc, String loc, String contact, String type,
            String start, String end,
            String lastUpdateBy) {

        //insert address into address table in db
        String sqlStatement = "update appointment set "
                + "title = \"" + title + "\", "
                + "description = \"" + desc + "\", "
                + "location = \"" + loc + "\", "
                + "type = \"" + type + "\", "
                + "contact = \"" + contact + "\", "
                + "start = \"" + start + "\", "
                + "end = \"" + end + "\", "
                + "lastUpdateBy = \"" + lastUpdateBy + "\", "
                + "userid = \"" + userID + "\""
                + "where appointmentid = " + appt.getApptID();

        //execute statement 
        Query.makeQuery(sqlStatement);

    }

    //************************************************************************************
    //        populate location combobox with locations from db
    //************************************************************************************
    @FXML
    private void initializeLocation() {

        String sqlStatement = "select city from city";
        //execute statement and create resultSet object
        Query.makeQuery(sqlStatement);

        //get resultSet
        ResultSet result = Query.getResults();
        cboLocation.getItems().clear();

        try {
            while (result.next()) {
                cboLocation.getItems().add(result.getString("city"));
            }

        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }
    }

    //************************************************************************************
    //        populate location combobox with locations from db
    //************************************************************************************
    @FXML
    private void initializeType() {

        cboType.getItems().clear();
        cboType.getItems().addAll(
                "Initial",
                "Plan",
                "Sales",
                "Consult");

    }

    //************************************************************************************
    //        populate start time 
    //************************************************************************************    
    @FXML
    private void initializeStart() {

        cboStart.getItems().clear();
        cboStart.getItems().addAll(
                "09:00", "09:15", "09:30", "09:45",
                "10:00", "10:15", "10:30", "10:45",
                "11:00", "11:15", "11:30", "11:45",
                "12:00", "12:15", "12:30", "12:45",
                "13:00", "13:15", "13:30", "13:45",
                "14:00", "14:15", "14:30", "14:45",
                "15:00", "15:15", "15:30", "15:45",
                "16:00", "16:15", "16:30", "16:45");
    }

    //************************************************************************************
    //        populate end time combobox -note they are relative to start time
    //************************************************************************************   
    @FXML
    private void initializeEnd() {

        ArrayList<String> availableEndTimes = new ArrayList<String>();
        Boolean flag = false;
        String start = cboStart.getValue();
        String[] endTimes = {"09:00", "09:15", "09:30", "09:45",
            "10:00", "10:15", "10:30", "10:45",
            "11:00", "11:15", "11:30", "11:45",
            "12:00", "12:15", "12:30", "12:45",
            "13:00", "13:15", "13:30", "13:45",
            "14:00", "14:15", "14:30", "14:45",
            "15:00", "15:15", "15:30", "15:45",
            "16:00", "16:15", "16:30", "16:45",
            "17:00"};

        //loop through times, add available to endtimes list
        for (String time : endTimes) {
            //once start time is reached set flag true
            if (flag) {
                availableEndTimes.add(time);
            }
            if (start == time) {
                flag = true;
            }

        }
        //add available entimes to list and combo box
        cboEnd.getItems().clear();
        cboEnd.getItems().addAll(availableEndTimes);
        //cboEnd.setValue(appt.getApptEnd().substring(11, 16));
        cboEnd.setValue("end time");
    }
//****************************************************************************

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //get connection
        try {
            DBconnect.makeConnection();
            //initialize combobox, fields, times
            initializeLocation();
            initializeType();
            initializeStart();
            initializeEnd();
            txtCustomer.setText(appt.getApptCustName());
            txtTitle.setText(appt.getApptTitle());
            txtContact.setText(appt.getApptContact());
            cboType.setValue(appt.getApptType());
            cboLocation.setValue(appt.getApptLocation());

            //convert start time from system default local to Location of Appt local(NYC or Phoenix or London)
            String localStart = Appointment.getLocalStart(appt.getApptStart(), appt.getApptLocation());

            //get localdate from string value and set datepicker 
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateString = localStart.substring(0, 10);
            //String dateString = appt.getApptStart().substring(0, 10);
            LocalDate localdate = LocalDate.parse(dateString, dateTimeFormatter);
            datePicker.setValue(localdate);

            //set start time to current start time
            //cboStart.setValue(localStart.substring(11, 16));
            cboStart.setValue("start time");
            initializeEnd();

        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());

        }

    }
}
