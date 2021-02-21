/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dayannuzzidb.view;

import dayannuzzidb.DaYannuzziDb;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
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
import javafx.scene.control.Label;
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
public class LoginController implements Initializable {

    @FXML
    private Button btnLogin;

    @FXML
    private TextField txtUser;

    @FXML
    private TextField txtPassword;

    @FXML
    private Label lblLogin;

    static String userName;

    static String userid;

    String loginAlertTitle = "Login Access Denied";
    String loginAlertHeader = "login failed!";
    String loginOK = "click OK";

    //************************************************************************************
    //      login handler action event
    //************************************************************************************       
    @FXML
    void loginHandler(ActionEvent event) {
        try {
            DBconnect.makeConnection();

            //get data from text box
            String user = txtUser.getText();
            String pass = txtPassword.getText();

            //verify user with Db
            String sqlStatement = "select password,userid from user "
                    + "where userName = \"" + txtUser.getText() + "\"; ";

            Query.makeQuery(sqlStatement);
            ResultSet result = Query.getResults();
            String verifyPass = "";

            while (result.next()) {
                //check password vs. user table col password
                verifyPass = result.getString("password");
                userid = result.getString("userid");
            }

            if (txtPassword.getText().equals(verifyPass)
                    && !(txtPassword.getText().isEmpty())
                    && !(txtUser.getText().isEmpty())) {

                userName = txtUser.getText();
                
                //append user and timestamp to logfile.txt note* try with resources
                try (PrintWriter pw = new PrintWriter(new FileOutputStream( new File("loginfile.txt"),true))){
                    //PrintWriter pw = new PrintWriter(new FileOutputStream(
                    //        new File("loginfile.txt"),true));
                    //set timezone
                    ZoneId zone = ZoneId.of("UTC");  
                    //format 15 width for username ,  append to logfile
                    pw.append(String.format("%-15s %s", userName, ZonedDateTime.now().withZoneSameInstant(zone)+"\n"));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("error: " + ex.getMessage());
                }                
                
                //check for upcoming appointments and notify user
                checkCurrentAppts();

                Parent parent = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
                Scene scene = new Scene(parent);
                //This line gets the Stage information
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
            } else {
                //alert for upcoming appt within 15 minutes
                Alert loginAlert = new Alert(Alert.AlertType.WARNING);
                loginAlert.setTitle(loginAlertTitle);
                loginAlert.setHeaderText(loginAlertHeader);
                loginAlert.setContentText(loginOK);
                Optional<ButtonType> loginResult = loginAlert.showAndWait();
            }

        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }

    }

    //************************************************************************************
    //      check for upcoming appointments within fifteen minutes and notify user
    //************************************************************************************       
    public void checkCurrentAppts() {
        //get current time and compare to appt time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime currentTime = LocalDateTime.now();

        Appointment.appts.forEach((appt) -> {
            String start = appt.getApptStart();
            //break down string dates to create ZonedDateTime
            int startYear = Integer.parseInt(start.substring(0, 4));
            int startMonth = Integer.parseInt(start.substring(5, 7));
            int startDay = Integer.parseInt(start.substring(8, 10));
            int startHour = Integer.parseInt(start.substring(11, 13));
            int startMin = Integer.parseInt(start.substring(14, 16));
            //create localDate times to use in ZonedDateTime constructors
            LocalDateTime startlocalDateTime = LocalDateTime.of(startYear, startMonth,
                    startDay, startHour, startMin);
            //if appointment is starting within 15 minutes of current time send alert
            if (currentTime.isEqual(startlocalDateTime)
                    || (currentTime.plusMinutes(16).isAfter(startlocalDateTime)
                    && (startlocalDateTime.isAfter(currentTime)))) {
                //alert for upcoming appt within 15 minutes
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Appointment starts soon!");
                alert.setHeaderText("Appt with "
                        + appt.getApptCustName() + " starts at "
                        + appt.getApptStart().substring(11, 16));
                alert.setContentText("click OK!");
                Optional<ButtonType> result = alert.showAndWait();
            }
        });
    }

    //************************************************************************************
    //      initialize 
    //************************************************************************************           
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //test change locale
        //Locale locale = new Locale("es");
        //Locale.setDefault(locale);

        //set login alerts for english language
        loginAlertTitle = "Login Access Denied";
        loginAlertHeader = "login failed!";
        loginOK = "click OK";

        //if locale language is 'es' then change language to spanish
        if (Locale.getDefault().getLanguage().toUpperCase().equals("ES")) {
            lblLogin.setText("iniciar sesión");
            btnLogin.setText("iniciar sesión");
            txtUser.setPromptText("nombre de usuario");
            txtPassword.setPromptText("contraseña");
            loginAlertTitle = "acceso denegado";
            loginAlertHeader = "error de inicio de sesion";
            loginOK = "haga clic Aceptar";
        }
    }
}
