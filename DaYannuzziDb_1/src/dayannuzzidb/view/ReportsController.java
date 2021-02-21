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
import java.sql.SQLException;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import model.Query;
import model.User;

/**
 * FXML Controller class
 *
 * @author macintoshhd
 */
public class ReportsController implements Initializable {

    @FXML
    private Button btnApptByMonth;

    @FXML
    private Button btnUserSchedule;

    @FXML
    private Button btnApptByLoc;

    @FXML
    private Button btnExit;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnCustByName;

    @FXML
    private TextArea txtArea;

    @FXML
    private ComboBox<String> cboYear;

    String report = "";

    //************************************************************************************
    //      exit button  program action event
    //************************************************************************************       
    @FXML
    void exitHandler(ActionEvent event) {
        System.exit(0);
    }

    //************************************************************************************
    //      home button return to main screen action event
    //************************************************************************************       
    @FXML
    void homeHandler(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
        Scene scene = new Scene(parent);
        //get stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(scene);
        window.show();
    }

    //************************************************************************************
    //     report for appt types by month handler
    //************************************************************************************ 
    @FXML
    void apptByMonthHandler(ActionEvent event) {
        //declare variables, create arrays for month loops
        int salesCount, consultCount, initialCount, followUpCount;

        String[] months = {"01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12"};

        String[] monthName = {"Jan", "Feb", "Mar",
            "Apr", "May", "Jun", "Jul",
            "Aug", "Sep", "Oct",
            "Nov", "Dec"};

        //clear report
        report = "";
        //set heading
        report += "                "
                + "Appointment Report for Year: " + cboYear.getValue() + "\n"
                + "------------------------------------------------------\n"
                + "  \ttype:\tSales  Consult  Initial  Plan \n";
        report += "------------------------------------------------------\n";
        //for each month in year chosen of combo box
        for (String month : months) {
            //call method to return number of Appts by type
            salesCount = getApptTypeCount("Sales", month);
            consultCount = getApptTypeCount("Consult", month);
            initialCount = getApptTypeCount("Initial", month);
            followUpCount = getApptTypeCount("Plan", month);

            //add results to String for output
            report += monthName[Integer.parseInt(month) - 1] + "\t"
                    + cboYear.getValue() + ": "
                    + "\tSale: " + salesCount + " Cons: " + consultCount
                    + " Init: " + initialCount + " Plan: " + followUpCount + "\n"
                    + "-------------------------------------------------------\n";
        }

        txtArea.setText(report);

    }

    //************************************************************************************
    //      report for schedule of all consultants(users) handler
    //************************************************************************************ 
    @FXML
    void userScheduleHandler(ActionEvent event) {
        //clear report
        report = "";
        //set heading
        report += "                "
                + "Schedule per Consultant for Year: " + cboYear.getValue() + "\n"
                + "------------------------------------"
                + "------------------------------------\n"
                + "Cnsltnt\t Customer \t\t\tDate\t \t\tStart\t \tEnd\t\n"
                + "------------------------------------"
                + "------------------------------------\n";

        for (User consultant : User.users) {
            getApptsForEach(consultant.getUserName());
            report += "------------------------------------"
                    + "------------------------------------\n";
        }
        txtArea.setText(report);
    }

    //************************************************************************************
    //      Report for appts by location handler
    //************************************************************************************ 
    @FXML
    void apptByLocHandler(ActionEvent event) {
        //create string array of cities
        String[] cities = {"London", "Phoenix", "New York City"};
        //clear report
        report = "";
        //set heading
        report += "                "
                + "Appointment by Location for Year: " + cboYear.getValue() + "\n"
                + "------------------------------------------------------------\n"
                + "\tCustomer:     \tDate   \tLocation   \n";
        report += "------------------------------------------------------------\n";

        //use lambda to check city against appointment city
        BiPredicate<String, String> bi = (a, b) -> a.equalsIgnoreCase(b);
        // for city in city table do loop
        for (String city : cities) {
            // get appts in each city and add to report
            Appointment.appts.stream().filter((a) -> (bi.test(city, a.getApptLocation()))).forEachOrdered((a) -> {
                report += (a.getApptCustName()
                        + "\t\t" + a.getApptStart().substring(5, 10)
                        + "\t" + a.getApptLocation() + "\n");
            });
            report += "------------------------------------------------------------\n";

        }
        txtArea.setText(report);
    }

    //************************************************************************************
    //      sort customers by name and print to text area
    //************************************************************************************      
    @FXML
    void custByNameHandler(ActionEvent event) {
        report = "";//reset text
        report += " CUSTOMERS\n";
        report += "---------------\n";
        //sort customers by name using Lambda
        Collections.sort(Customer.customers, (p1, p2)
                -> p1.getCustomerName().compareToIgnoreCase(p2.getCustomerName()));

        for (Customer c : Customer.customers) {
            report += c.getCustomerName() + "\n";
        }
        txtArea.setText(report);
    }

    //************************************************************************************
    //      query db to get appt count for each type of appt for each month
    //************************************************************************************     
    int getApptTypeCount(String type, String month) {
        // select count(type) from appointment where type = "Consult" and 
        // start like '2019-04%';   
        int count = 0;
        String year = cboYear.getValue();

        try {
            //get number of appointments in a given month
            String sqlStatement = "select count(type) from appointment"
                    + " where type = \"" + type + "\" and "
                    + "start like '" + year + "-" + month + "%';";

            //execute statement
            Query.makeQuery(sqlStatement);
            //get resultSet
            ResultSet result = Query.getResults();
            while (result.next()) {
                count = result.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }
        return count;
    }

    //************************************************************************************
    //      query db to get appt schedule on a per consultant(user) basis
    //************************************************************************************        
    void getApptsForEach(String user) {

        String userName = "";
        String customer = "";
        String date = "";
        String start = "";
        String end = "";
        //get year from combo box value
        String year = cboYear.getValue();

        try {
            //get appt info  on a per consultant(user) basis
            String sqlStatement = ""
                    + "select user.username,customer.customerName, "
                    + "appointment.start, appointment.end "
                    + "from appointment join customer "
                    + "on appointment.customerid = customer.customerid "
                    + "join user "
                    + "on user.userid = appointment.userid "
                    + "where user.username = \"" + user + "\" "
                    + "and appointment.start like '" + year + "%';";

            //execute statement
            Query.makeQuery(sqlStatement);
            //get resultSet
            ResultSet result = Query.getResults();
            while (result.next()) {
                userName = result.getString(1);
                customer = result.getString(2);
                date = result.getString(3).substring(5, 10);
                start = result.getString(3).substring(11, 16);
                end = result.getString(4).substring(11, 16);

                //add result to report
                report += userName + "\t\t" + customer + "\t\t"
                        + "date:" + date + "\t"
                        + "start:" + start + "\t"
                        + "end:" + end + "\t\n";
                report += "------------------------------------"
                        + "------------------------------------\n";
            }
        } catch (SQLException ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }
    }

    @FXML
    void yearHandler(ActionEvent event) {

    }

    //***********************************************************************************
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cboYear.getItems().clear();
        cboYear.getItems().addAll("2020","2019");
        cboYear.setValue("2020");

    }

}
