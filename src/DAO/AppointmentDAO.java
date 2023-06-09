package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.DateTimeUtil;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * This class contains the database queries for appointments.
 */
public class AppointmentDAO {

    /**
     * This method queries all appointments from the appointments table in the database.
     *
     * @return The list of appointments.
     * @throws SQLException The exception to throw if there is an error with database connection or with the query.
     */
    public static ObservableList<Appointment> allAppointments() throws SQLException {

        ObservableList<Appointment> listOfAppointments = FXCollections.observableArrayList();
        String apptQuery = "SELECT * FROM appointments ORDER BY Start ASC";
        PreparedStatement ps = JDBC.connection.prepareStatement(apptQuery);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int appointmentID = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            int contactID = rs.getInt("Contact_ID");
            String type = rs.getString("Type");
            LocalDateTime start = DateTimeUtil.toLocalDT(rs.getTimestamp("Start"));
            LocalDateTime end = DateTimeUtil.toLocalDT(rs.getTimestamp("End"));
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt("User_ID");

            Appointment appointment = new Appointment(appointmentID, title, description, location, contactID, type, start, end, customerID, userID);
            listOfAppointments.add(appointment);
        }
        return listOfAppointments;
    }

    /**
     * This method queries the current month's appointments.
     *
     * @return All appointments for the current month.
     * @throws SQLException The exception to throw if there are errors with database connection or the query.
     */
    public static ObservableList<Appointment> currentMonth() throws SQLException {

        ObservableList<Appointment> monthOfAppointments = FXCollections.observableArrayList();
        String monthApptQuery = "SELECT * FROM appointments WHERE month(Start) = month(curdate()) ORDER BY Start ASC";
        PreparedStatement ps = JDBC.connection.prepareStatement(monthApptQuery);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int appointmentID = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            int contactID = rs.getInt("Contact_ID");
            String type = rs.getString("Type");
            LocalDateTime start = DateTimeUtil.toLocalDT(rs.getTimestamp("Start"));
            LocalDateTime end = DateTimeUtil.toLocalDT(rs.getTimestamp("End"));
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt("User_ID");

            Appointment appointment = new Appointment(appointmentID, title, description, location, contactID, type, start, end, customerID, userID);
            monthOfAppointments.add(appointment);
        }
        return monthOfAppointments;
    }

    /**
     * This method queries the current week's appointments.
     *
     * @return All appointments for the current week.
     * @throws SQLException The exception to throw if there are errors with database connection or the query.
     */
    public static ObservableList<Appointment> currentWeek() throws SQLException {

        ObservableList<Appointment> weekOfAppointments = FXCollections.observableArrayList();
        String weekApptQuery = "SELECT * FROM appointments WHERE yearweek(Start) = yearweek(NOW()) ORDER BY Start ASC";
        PreparedStatement ps = JDBC.connection.prepareStatement(weekApptQuery);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int appointmentID = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            int contactID = rs.getInt("Contact_ID");
            String type = rs.getString("Type");
            LocalDateTime start = DateTimeUtil.toLocalDT(rs.getTimestamp("Start"));
            LocalDateTime end = DateTimeUtil.toLocalDT(rs.getTimestamp("End"));
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt("User_ID");

            Appointment appointment = new Appointment(appointmentID, title, description, location, contactID, type, start, end, customerID, userID);
            weekOfAppointments.add(appointment);
        }
        return weekOfAppointments;
    }

    /**
     * This method inserts a new appointment in appointments table in the database.
     *
     * @param addContact     The contact to add.
     * @param addCustID      The customer ID to add.
     * @param addDescription The description to add.
     * @param addLocation    The location to add.
     * @param addTitle       The title to add.
     * @param addType        The type to add.
     * @param addUserID      The user to add.
     * @param endDateTime    The end date/time to add.
     * @param startDateTime  The start date/time to add.
     * @throws SQLException The exception to throw if there are errors with database connection or the query.
     */
    public static void addAppointment(LocalDateTime startDateTime, LocalDateTime endDateTime,
                                      String addContact, String addType, String addTitle, String addDescription, String
                                              addLocation, int addCustID, int addUserID) throws SQLException {
        try {
            String addApptQuery = "INSERT INTO appointments (Title, Description, Location, Type, Start, " +
                    "End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = JDBC.connection.prepareStatement(addApptQuery);
            ps.setString(1, addTitle);
            ps.setString(2, addDescription);
            ps.setString(3, addLocation);
            ps.setString(4, addType);
            ps.setTimestamp(5, DateTimeUtil.toUTCStartDT(startDateTime));
            ps.setTimestamp(6, DateTimeUtil.toUTCEndDT(endDateTime));
            ps.setString(7, null);
            ps.setString(8, null);
            ps.setString(9, null);
            ps.setString(10, null);
            ps.setInt(11, addCustID);
            ps.setInt(12, addUserID);
            ps.setString(13, addContact);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method modifies data that is part of an existing appointment.
     *
     * @param modApptID        The appointment ID to reference.
     * @param modContact       The contact to modify.
     * @param modCustID        The customer ID to modify.
     * @param modDescription   The description to modify.
     * @param modEndDateTime   The end date/time to modify.
     * @param modLocation      The location to modify.
     * @param modStartDateTime The start date/time to modify.
     * @param modTitle         The title to modify.
     * @param modType          The type to modify.
     * @param modUserID        The user ID to modify.
     * @throws SQLException The exception to throw if there are errors with database connection or the query.
     */
    public static void modifyAppointment(int modApptID, String modTitle, String modDescription, String modLocation,
                                         String modType, LocalDateTime modStartDateTime, LocalDateTime modEndDateTime,
                                         int modCustID, int modUserID, String modContact) throws SQLException {
        try {
            String modApptQuery =
                    "UPDATE appointments SET Appointment_ID = ?, Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?," +
                            "Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?," +
                            " User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";

            PreparedStatement ps = JDBC.connection.prepareStatement(modApptQuery);
            ps.setInt(1, modApptID);
            ps.setString(2, modTitle);
            ps.setString(3, modDescription);
            ps.setString(4, modLocation);
            ps.setString(5, modType);
            ps.setTimestamp(6, DateTimeUtil.toUTCStartDT(modStartDateTime));
            ps.setTimestamp(7, DateTimeUtil.toUTCEndDT(modEndDateTime));
            ps.setString(8, null);
            ps.setString(9, null);
            ps.setString(10, null);
            ps.setString(11, null);
            ps.setInt(12, modCustID);
            ps.setInt(13, modUserID);
            ps.setString(14, modContact);
            ps.setInt(15, modApptID);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method deletes a selected appointment from the appointments table in the database.
     *
     * @param appointment The appointment to delete.
     * @throws SQLException The exception to throw if there are errors with database connection or the query.
     */
    public static void deleteAppt(Appointment appointment) throws SQLException {

        String deleteApptQuery = "DELETE FROM appointments WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(deleteApptQuery);
        ps.setInt(1, appointment.getAppointmentID());
        ps.executeUpdate();
    }
}
