package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Logger;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;

/**
 * This class contains the database queries for users.
 */
public class UserDAO extends User {
    private static User userLogin;

    /**
     * This is the constructor representing the user.
     *
     * @param userID        The user ID.
     * @param userName      The username.
     * @param password      The password.
     * @param userFirstName The user's first name.
     * @param userLastName  The user's last name.
     */
    public UserDAO(int userID, String userName, String password, String userFirstName, String userLastName) {
        super(userID, userName, password, userFirstName, userLastName);
    }

    /**
     * This method queries user selections for the combo box.
     *
     * @return The list of all users.
     * @throws SQLException The exception to throw if there is an error regarding the query for combo box selections.
     */
    public static ObservableList<UserDAO> allUsers() throws SQLException {
        ObservableList<UserDAO> users = FXCollections.observableArrayList();
        String userQuery = "SELECT User_ID, User_Name, Password, First_Name, Last_Name FROM users ORDER BY " +
                "User_ID ASC";
        PreparedStatement ps = JDBC.connection.prepareStatement(userQuery);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int userID = rs.getInt("User_ID");
            String userName = rs.getString("User_Name");
            String password = rs.getString("Password");
            String userFirstName = rs.getString("First_Name");
            String userLastName = rs.getString("Last_Name");

            UserDAO userDAO = new UserDAO(userID, userName, password, userFirstName, userLastName);
            users.add(userDAO);
        }
        return users;
    }

    /**
     * This method is a boolean that will check for a valid user in the database.
     *
     * @param username The username to verify.
     * @param password The password to verify.
     */
    public static Boolean validateUser(String username, String password) {
        try {
            String loginQuery = "SELECT * FROM users WHERE User_Name='" + username + "' AND password='" + password + "'";
            Statement statement = JDBC.connection.createStatement();
            ResultSet rs = statement.executeQuery(loginQuery);

            if (rs.next()) {
                userLogin = new User();
                userLogin.setUsername(rs.getString("User_Name"));
                userLogin.setUserFirstName(rs.getString("First_Name"));

                // logs end-user login attempt as successful
                Logger.trackerLog(username, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"), true);
                return true;
            } else {
                // logs end-user login attempt as failed
                Logger.trackerLog(username, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"), false);
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the user login.
     *
     * @return The user login.
     */
    public static User getUserLogin() {
        return userLogin;
    }

    /**
     * Sets the user login.
     *
     * @param userLogin The user login.
     */
    public static void setUserLogin(User userLogin) {
        UserDAO.userLogin = userLogin;
    }

    @Override

    // Display combo box options
    public String toString() {
        return (getUserFirstName() + " " + getUserLastName());
    }
}
