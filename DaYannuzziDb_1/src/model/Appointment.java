/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import dayannuzzidb.DaYannuzziDb;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
public class Appointment {

    private final StringProperty apptID;
    private final StringProperty apptCustID;
    private final StringProperty apptCustName;
    private final StringProperty apptUserID;
    private final StringProperty apptTitle;
    private final StringProperty apptDesc;
    private final StringProperty apptLocation;
    private final StringProperty apptContact;
    private final StringProperty apptType;
    private final StringProperty apptStart;
    private final StringProperty apptEnd;

    public static ObservableList<Appointment> appts = FXCollections.observableArrayList();

    public Appointment(String apptID, String apptCustID, String apptCustName,
            String apptUserID, String apptTitle,
            String apptDesc, String apptLocation,
            String apptContact, String apptType,
            String apptStart, String apptEnd) {

        //constructor with ID
        this.apptID = new SimpleStringProperty(apptID);
        this.apptCustID = new SimpleStringProperty(apptCustID);
        this.apptCustName = new SimpleStringProperty(apptCustName);
        this.apptUserID = new SimpleStringProperty(apptUserID);
        this.apptTitle = new SimpleStringProperty(apptTitle);
        this.apptDesc = new SimpleStringProperty(apptDesc);
        this.apptLocation = new SimpleStringProperty(apptLocation);
        this.apptContact = new SimpleStringProperty(apptContact);
        this.apptType = new SimpleStringProperty(apptType);
        this.apptStart = new SimpleStringProperty(apptStart);
        this.apptEnd = new SimpleStringProperty(apptEnd);

    }
    //***************************************************************** 
    //setters
    //*****************************************************************     

    public String getApptID() {
        return apptID.get();
    }

    public String getApptCustID() {
        return apptCustID.get();
    }

    public String getApptCustName() {
        return apptCustName.get();
    }

    public String getApptUserID() {
        return apptUserID.get();
    }

    public String getApptTitle() {
        return apptTitle.get();
    }

    public String getApptDesc() {
        return apptDesc.get();
    }

    public String getApptLocation() {
        return apptLocation.get();
    }

    public String getApptContact() {
        return apptContact.get();
    }

    public String getApptType() {
        return apptType.get();
    }

    public String getApptStart() {
        return apptStart.get();
    }

    public String getApptEnd() {
        return apptEnd.get();
    }

    //***************************************************************** 
    //setters
    //***************************************************************** 
    public void setApptID(String value) {
        apptID.set(value);
    }

    public void setApptCustID(String value) {
        apptCustID.set(value);
    }

    public void setApptCustName(String value) {
        apptCustName.set(value);
    }

    public void setApptUserID(String value) {
        apptUserID.set(value);
    }

    public void setApptTitle(String value) {
        apptTitle.set(value);
    }

    public void setApptDesc(String value) {
        apptDesc.set(value);
    }

    public void setApptLocation(String value) {
        apptLocation.set(value);
    }

    public void setApptContact(String value) {
        apptContact.set(value);
    }

    public void setApptType(String value) {
        apptType.set(value);
    }

    public void setApptStart(String value) {
        apptStart.set(value);
    }

    public void setApptEnd(String value) {
        apptEnd.set(value);
    }

    //************************************************************************************
    //        method to delete appt from db
    //************************************************************************************    
    public static boolean deleteAppt(Appointment appt) {
        try {
            DBconnect.makeConnection();
            //write sql statement to remove appt from db
            String sqlStatement = "delete from appointment where "
                    + "appointmentid = " + appt.getApptID() + ";";

            //execute statement and/or create resultSet object
            Query.makeQuery(sqlStatement);
            DBconnect.closeConnection();

        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }
        return appts.remove(appt);
    }

    //************************************************************************************
    //        method to get updated appointments converted to local time
    //************************************************************************************   
    public static void getAppts() {

        try {
            //write sql statement to get customer data
            String sqlStatement = "select appointmentid, "
                    + "appointment.customerid, "
                    + "customer.customerName, appointment.userid, "
                    + "title, description, location,contact, type, start, appointment.end "
                    + "from customer join appointment on "
                    + "customer.customerid = appointment.customerid; ";

            //execute statement and create resultSet object
            Query.makeQuery(sqlStatement);

            //get resultSet
            ResultSet result = Query.getResults();

            //remove appts to make way for updated ones
            Appointment.appts.removeAll(appts);

            while (result.next()) {
                String apptid = result.getString("appointmentid");
                String custid = result.getString("customerID");
                String custName = result.getString("customerName");
                String userid = result.getString("userid");
                String title = result.getString("title");
                String desc = result.getString("description");
                String loc = result.getString("location");
                String contact = result.getString("contact");
                String type = result.getString("type");

                String start = result.getString("start");
                start = start.substring(0, 16);

                String end = result.getString("end");
                end = end.substring(0, 16);

                //break down string dates to create ZonedDateTime
                int startYear = Integer.parseInt(start.substring(0, 4));
                int startMonth = Integer.parseInt(start.substring(5, 7));
                int startDay = Integer.parseInt(start.substring(8, 10));
                int startHour = Integer.parseInt(start.substring(11, 13));
                int startMin = Integer.parseInt(start.substring(14, 16));

                int endYear = Integer.parseInt(end.substring(0, 4));
                int endMonth = Integer.parseInt(end.substring(5, 7));
                int endDay = Integer.parseInt(end.substring(8, 10));
                int endHour = Integer.parseInt(end.substring(11, 13));
                int endMin = Integer.parseInt(end.substring(14, 16));

                //set zone id to UTC
                ZoneId zone = ZoneId.of("UTC");

                //create localDate times to use in ZonedDateTime constructors
                LocalDateTime startlocalDateTime = LocalDateTime.of(startYear, startMonth,
                        startDay, startHour, startMin);

                LocalDateTime endlocalDateTime = LocalDateTime.of(endYear, endMonth,
                        endDay, endHour, endMin);

                //create ZonedDateTimes with Zone based on UTC
                ZonedDateTime startzonedDateTime = ZonedDateTime.of(startlocalDateTime, zone);
                ZonedDateTime endzonedDateTime = ZonedDateTime.of(endlocalDateTime, zone);

                //convert zoned utc time to user local time
                ZonedDateTime startLocalDate = startzonedDateTime.withZoneSameInstant(ZoneOffset.systemDefault());
                ZonedDateTime endLocalDate = endzonedDateTime.withZoneSameInstant(ZoneOffset.systemDefault());

                //convert local time back to string with formatter
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String formattedLocalStart = startLocalDate.format(formatter);
                String formattedLocalEnd = endLocalDate.format(formatter);

                //create appts from table... adjusted for user local time
                Appointment appt = new Appointment(apptid, custid, custName, userid, title,
                        desc, loc, contact, type, formattedLocalStart, formattedLocalEnd);
                Appointment.appts.add(appt);

            }
        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }

    }

    //************************************************************************************
    //        method to insert appointment into appointment table in db
    //************************************************************************************
    public static String insertAppt(String custID, String custName, String userID,
            String title, String desc, String loc, String contact, String type,
            String start, String end, String createdBy,
            String lastUpdateBy, Date date, DateFormat dateFormat) {

        String url = "na";
        String apptid = "";

        //insert address into address table in db
        String sqlStatement = "insert into appointment(customerid, userid,"
                + " title, description, location, contact, type, url, "
                + " start, end, createDate, createdBy, lastUpdateBy)"
                + " values(\""
                + custID + "\", \""
                + userID + "\", \""
                + title + "\", \""
                + desc + "\", \""
                + loc + "\", \""
                + contact + "\", \""
                + type + "\", \""
                + url + "\", \""
                + start + "\", \""
                + end + "\", \""
                + dateFormat.format(date) + "\", \""
                + createdBy + "\", \""
                + lastUpdateBy + "\"); ";

        //execute statement 
        Query.makeQuery(sqlStatement);

        sqlStatement = "select last_insert_id();";
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResults();

        try {
            while (result.next()) {
                apptid = result.getString("last_insert_id()");
            }

        } catch (Exception ex) {
            Logger.getLogger(DaYannuzziDb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error: " + ex.getMessage());
        }
        return apptid;

    }

    //************************************************************************************
    //        method to insert appointment into customer table in db
    //************************************************************************************
    public static void updateAppt(Appointment appt, String userID,
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
    //        take string and location and return formatted utc string
    //        UTC times will be inserted into db
    //************************************************************************************       
    public static String getUTCdate(String start, String loc) {
        //break down string dates to create ZonedDateTime
        int startYear = Integer.parseInt(start.substring(0, 4));
        int startMonth = Integer.parseInt(start.substring(5, 7));
        int startDay = Integer.parseInt(start.substring(8, 10));
        int startHour = Integer.parseInt(start.substring(11, 13));
        int startMin = Integer.parseInt(start.substring(14, 16));

        //get zone id based on location of appt..start with default us/eastern
        ZoneId zone = ZoneId.of("US/Eastern");
        if (loc.equals("Phoenix")) {
            zone = ZoneId.of("US/Mountain");
        }
        if (loc.equals("London")) {
            zone = ZoneId.of("Europe/London");
        }

        //create localDate times to use in ZonedDateTime constructors
        LocalDateTime startlocalDateTime = LocalDateTime.of(startYear, startMonth,
                startDay, startHour, startMin);

        //create ZonedDateTimes with Zone based on Location of appt
        ZonedDateTime startzonedDateTime = ZonedDateTime.of(startlocalDateTime, zone);

        //convert zoned time to utc
        ZonedDateTime startutcDate = startzonedDateTime.withZoneSameInstant(ZoneOffset.UTC);

        //convert zonedTime back to string with formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedUTCstart = startutcDate.format(formatter);

        return formattedUTCstart;

    }

    //************************************************************************************
    //        parameters string and location and return formatted utc string
    //************************************************************************************       
    public static String getLocalStart(String start, String loc) {
        //break down string dates to create ZonedDateTime
        int startYear = Integer.parseInt(start.substring(0, 4));
        int startMonth = Integer.parseInt(start.substring(5, 7));
        int startDay = Integer.parseInt(start.substring(8, 10));
        int startHour = Integer.parseInt(start.substring(11, 13));
        int startMin = Integer.parseInt(start.substring(14, 16));

        //get zone id based on location of appt
        ZoneId zone = ZoneId.of("US/Eastern");
        if (loc.equals("Phoenix")) {
            zone = ZoneId.of("US/Mountain");
        }
        if (loc.equals("London")) {
            zone = ZoneId.of("Europe/London");
        }

        //create localDate times to use in ZonedDateTime constructors
        LocalDateTime startlocalDateTime = LocalDateTime.of(startYear, startMonth,
                startDay, startHour, startMin);

        //create ZonedDateTimes with Zone based on Location of appt
        ZonedDateTime startzonedDateTime = ZonedDateTime.of(startlocalDateTime, zone);

        //convert zonedTime back to string with formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String localStart = startzonedDateTime.format(formatter);

        return localStart;

    }

    //************************************************************************************
    //        parameters string and location and return formatted utc string
    //************************************************************************************       
    public static boolean checkOverlap(String start, String end) {
        //true means no overlap
        boolean validAppt = true;
        //note -- start and end are passed in as utc strings
        ZonedDateTime newStart = getNewDateTimeObj(start);
        ZonedDateTime newEnd = getNewDateTimeObj(end);
        
        //check new appt against existing appointments   
        for (Appointment a : appts) {

            //get existing appointment start,end,(in local time) convert to datetime object
            ZonedDateTime aStart = getDateTimeObj(a.getApptStart());
            ZonedDateTime aEnd = getDateTimeObj(a.getApptEnd());

            //check overlap against existing appt
            if (newEnd.isBefore(aStart)
                    || newEnd.isEqual(aStart)
                    || newStart.isAfter(aEnd)
                    || newStart.isEqual(aEnd)) {
                validAppt = true;
            } else {
                validAppt = false;
                break;
            }
        }
        return validAppt;
    }
    //**************************************************************************
    //     get zoned datetime object for existing appointment in utc
    //**************************************************************************

    public static ZonedDateTime getDateTimeObj(String s) {
        int year = Integer.parseInt(s.substring(0, 4));
        int month = Integer.parseInt(s.substring(5, 7));
        int day = Integer.parseInt(s.substring(8, 10));
        int hour = Integer.parseInt(s.substring(11, 13));
        int min = Integer.parseInt(s.substring(14, 16));
        //create zonedDateTime object 
        ZonedDateTime z = ZonedDateTime.of(year, month, day,
                hour, min, 0, 0, ZoneId.systemDefault());
        //convert local time to utc
        ZoneId utczone = ZoneId.of("UTC");
        z = z.withZoneSameInstant(utczone);
        return z;
    }

    //*************************************************************************
    //      get zoned datetime object for new appointment in utc
    //*************************************************************************
    public static ZonedDateTime getNewDateTimeObj(String s) {
        int year = Integer.parseInt(s.substring(0, 4));
        int month = Integer.parseInt(s.substring(5, 7));
        int day = Integer.parseInt(s.substring(8, 10));
        int hour = Integer.parseInt(s.substring(11, 13));
        int min = Integer.parseInt(s.substring(14, 16));
        //create zonedDateTime object 
        ZonedDateTime z = ZonedDateTime.of(year, month, day,
                hour, min, 0, 0, ZoneId.of("UTC"));
        return z;
    }
}
