/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dayannuzzidb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author macintoshhd
 */
public class DaYannuzziDb extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        //get connection
        try {//connect to mySQL db
            DBconnect.makeConnection();
            
            //populate customer from db
            Customer.getCustomers();
            
            //populate users from db
            User.getUsers();
            
            //populate appts from db
            Appointment.getAppts();

            DBconnect.closeConnection();

        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }

        Parent root = FXMLLoader.load(getClass().getResource("view/login.fxml"));

        //set scene on stage and show
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
