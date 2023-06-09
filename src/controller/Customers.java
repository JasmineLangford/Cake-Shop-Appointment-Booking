package controller;

import DAO.CustomerDAO;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class Customers implements Initializable {

    @FXML
    private RadioButton viewRegularCustomer;
    @FXML
    private ToggleGroup customerTypeToggle;
    @FXML
    private RadioButton viewCorpAcct;
    @FXML
    private TableColumn<Customer, String> customerType;
    @FXML
    private Button deleteCustomer;
    @FXML
    private TextField searchCustomer;
    @FXML
    private TableView<Customer> mainCustomerTable;
    @FXML
    private TableColumn<Customer, String> customerNameCol;
    @FXML
    private TableColumn<Customer, String> customerAddressCol;
    @FXML
    private TableColumn<Customer, String> customerPhoneCol;
    @FXML
    private TableColumn<Customer, String> customerCountryCol;
    @FXML
    private TableColumn<Customer, String> customerStateCol;
    @FXML
    private TableColumn<Customer, String> customerPostalCol;
    private FilteredList<Customer> filteredCustomers;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Set on screen launch for regular customer view
        customerNameCol.setText("Customer Name");
        searchCustomer.setPromptText("Search by Customer Name");
        deleteCustomer.setText("Delete Customer");

        // Set customer table
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
        customerCountryCol.setCellValueFactory(new PropertyValueFactory<>("customerCountry"));
        customerStateCol.setCellValueFactory(new PropertyValueFactory<>("division"));
        customerPostalCol.setCellValueFactory(new PropertyValueFactory<>("customerPostal"));
        customerType.setCellValueFactory(new PropertyValueFactory<>("customerType"));
        try {
            loadCustomerTable();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

        // Search customers
        try {
            filteredCustomers = new FilteredList<>(CustomerDAO.allCustomers());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        customerTypeToggle.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == viewRegularCustomer) {
                try {
                    changeToCustomer();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (newValue == viewCorpAcct) {
                try {
                    changeToCorp();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This method is the available search functionality after a radio button is selected.
     */
    public void applySearch() {
        searchCustomer.textProperty().addListener((observable, oldValue, newValue) ->
                filteredCustomers.setPredicate(customer -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String searchKeyword = newValue.toLowerCase();

                    if (customer.getCustomerName().toLowerCase().contains(searchKeyword)) {
                        return true;
                    } else return Integer.toString(customer.getCustomerId()).contains(searchKeyword);
                })
        );

        SortedList<Customer> sortedParts = new SortedList<>(filteredCustomers);
        sortedParts.comparatorProperty().bind(mainCustomerTable.comparatorProperty());
        mainCustomerTable.setItems(sortedParts);
        mainCustomerTable.setPlaceholder(new Label("No matches"));
    }

    /**
     * This method loads the view for when the Regular Customer radio button is selected.
     *
     * @throws SQLException The exception to throw if there is an issue with the SQL query.
     */
    public void loadCustomerTable() throws SQLException {
        ObservableList<Customer> regularCustomers = CustomerDAO.regularCustomers();
        mainCustomerTable.setItems(regularCustomers);
        mainCustomerTable.getSelectionModel().clearSelection();
    }

    /**
     * This method loads the view for when the Corporate Account radio button is selected.
     *
     * @throws SQLException The exception to throw is there is an error with the database.
     */
    public void loadAccountsTable() throws SQLException {
        ObservableList<Customer> corporateAccounts = CustomerDAO.corporateAccounts();
        mainCustomerTable.setItems(corporateAccounts);
        mainCustomerTable.getSelectionModel().clearSelection();
    }

    /**
     * This method loads the view for corporate accounts from another screen.
     *
     * @param actionEvent The button is clicked.
     * @throws IOException The exception to throw if there is an issue with I/O.
     * @throws SQLException The exception to throw if there is an issue with the SQL query.
     */
    public void loadCorpAccountsView (ActionEvent actionEvent) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/customers.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();Scene scene = new Scene(root, 1108, 538);
        scene.setFill(Color.TRANSPARENT);
        root.setStyle("-fx-background-radius: 30px 30px 30px 30px;");
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
        stage.setResizable(false);

        Customers controller = loader.getController();
        controller.viewCorpAcct.setSelected(true);
    }

    /**
     * This method will take the user to the Modify Customer screen where they can modify regular customers. The
     * data from the selected row will auto-populate to the modify form.
     *
     * @param actionEvent Modify button is clicked under the customer tableview.
     * @throws IOException The exception to throw if I/O error occurs.
     */
    public void updateCustomer(ActionEvent actionEvent) throws IOException, SQLException {

        if (mainCustomerTable.getSelectionModel().isEmpty()) {

            if(viewRegularCustomer.isSelected()){
            Alert modCustomerSelect = new Alert(Alert.AlertType.WARNING, "Please select a customer to be modified.");
            Optional<ButtonType> results = modCustomerSelect.showAndWait();
            if (results.isPresent() && results.get() == ButtonType.OK)
                modCustomerSelect.setOnCloseRequest(Event::consume);
            return;
            } else{
                Alert modCorpAcctSelect = new Alert(Alert.AlertType.WARNING, "Please select an account to be " +
                        "modified.");
                Optional<ButtonType> results = modCorpAcctSelect.showAndWait();
                if (results.isPresent() && results.get() == ButtonType.OK)
                    modCorpAcctSelect.setOnCloseRequest(Event::consume);
                return;
            }
        }

        if(viewRegularCustomer.isSelected()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/modify-customer.fxml"));
            loader.load();

            ModifyCustomer modifyCustomer = loader.getController();
            modifyCustomer.sendCustomer((Customer.RegularCustomer) mainCustomerTable.getSelectionModel().getSelectedItem());

            Parent scene = loader.getRoot();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(scene));
            stage.show();
            stage.centerOnScreen();
            stage.setResizable(false);
        }else if (viewCorpAcct.isSelected()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/modify-corp-acct.fxml"));
            loader.load();

            ModifyCorpAcct modifyCorpAcct = loader.getController();
            modifyCorpAcct.sendCorpAcct((Customer.CorporateAccount) mainCustomerTable.getSelectionModel().getSelectedItem());

            Parent scene = loader.getRoot();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(scene));
            stage.show();
            stage.centerOnScreen();
            stage.setResizable(false);
        }
    }

    /**
     * This method will delete a customer from the customers table in the database. If a customer has an associated
     * appointment, the appointment will also be deleted along with the customer if the user proceeds with the
     * deletion. A message will state the customer that was deleted.
     */
    public void deleteCustomerRow() throws SQLException {
        if (mainCustomerTable.getSelectionModel().isEmpty()) {

            if(viewRegularCustomer.isSelected()){
            Alert deleteCust = new Alert(Alert.AlertType.WARNING, "Please select a customer to be deleted.");
            Optional<ButtonType> result = deleteCust.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK)
                deleteCust.setOnCloseRequest(Event::consume);
            return;
            }else{
                Alert deleteCust = new Alert(Alert.AlertType.WARNING, "Please select an account to be deleted.");
                Optional<ButtonType> result = deleteCust.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK)
                    deleteCust.setOnCloseRequest(Event::consume);
                return;
            }
        }

        Customer deleteAssociatedAppts = mainCustomerTable.getSelectionModel().getSelectedItem();
        ObservableList<Appointment> associatedAppts =
                CustomerDAO.deleteAssociated(deleteAssociatedAppts.getCustomerId());

        try {
            if (associatedAppts.size() > 0) {
                Alert associatedAppt = new Alert(Alert.AlertType.WARNING, "All associated appointments will be " +
                        "deleted along with this customer. Are you sure you want to delete this customer?",
                        ButtonType.YES, ButtonType.NO);
                associatedAppt.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                associatedAppt.setTitle(" ");
                associatedAppt.setHeaderText("Associated Appointment Found!");
                Optional<ButtonType> results = associatedAppt.showAndWait();
                if (results.isPresent() && results.get() == ButtonType.YES) {
                    CustomerDAO.deleteCustomer(deleteAssociatedAppts);
                    loadCustomerTable();
                    mainCustomerTable.getSelectionModel().clearSelection();
                }
                if (results.isPresent() && results.get() == ButtonType.NO) {
                    associatedAppt.setOnCloseRequest(Event::consume);
                    mainCustomerTable.getSelectionModel().clearSelection();
                }

            } else {

                if(viewRegularCustomer.isSelected()){
                Alert deleteCustConfirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete "
                        + "this customer?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> result = deleteCustConfirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    Customer selectedCustomer = mainCustomerTable.getSelectionModel().getSelectedItem();
                    CustomerDAO.deleteCustomer(selectedCustomer);
                    loadCustomerTable();
                    mainCustomerTable.getSelectionModel().clearSelection();

                    Alert apptInfo = new Alert(Alert.AlertType.INFORMATION, "You have deleted the following " +
                            "customer: " + selectedCustomer.getCustomerName(), ButtonType.OK);
                    apptInfo.showAndWait();
                }
                }else{Alert deleteCustConfirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to " +
                        "delete this account?", ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> result = deleteCustConfirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.YES) {
                        Customer selectedCustomer = mainCustomerTable.getSelectionModel().getSelectedItem();
                        CustomerDAO.deleteCustomer(selectedCustomer);
                        loadAccountsTable();
                        mainCustomerTable.getSelectionModel().clearSelection();

                        Alert apptInfo = new Alert(Alert.AlertType.INFORMATION, "You have deleted the following " +
                                "account: " + selectedCustomer.getCustomerName(), ButtonType.OK);
                        apptInfo.showAndWait();
                    }}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will take the user to the Add Customer screen where a new regular customer can be added.
     *
     * @param actionEvent Add button is clicked under the customers' tableview.
     * @throws IOException The exception to throw if I/O error occurs.
     */
    public void addCustomer(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("/view/add-customer.fxml"))));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 500, 475);
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
        stage.setResizable(false);
    }

    /**
     * This method will take the user to the Add Corporate Account screen where a new corporate account can be added.
     *
     * @param actionEvent Add button is clicked under the customers' tableview.
     * @throws IOException The exception to throw if I/O error occurs.
     */
    public void addCorpAcct(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("/view/add-corp-account.fxml"))));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 500, 475);
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
        stage.setResizable(false);
    }

    /**
     * This method takes the user to the reports screen.
     *
     * @param actionEvent Reports is clicked (located on the right panel).
     * @throws IOException The exception to throw if I/O error occurs.
     */
    public void toReports(MouseEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(getClass().getResource("/view/reports.fxml"))));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1108, 538);
        scene.setFill(Color.TRANSPARENT);
        root.setStyle("-fx-background-radius: 30px 30px 30px 30px;");
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
        stage.setResizable(false);
    }

    /**
     * This method takes the user to the appointments screen.
     *
     * @param actionEvent Appointments is clicked (located on the right panel).
     * @throws IOException The exception to throw if I/O error occurs.
     */
    public void backToAppointments(MouseEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load((Objects.requireNonNull(Appointments.class.getResource("/view/appointments.fxml"))));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1108, 538);
        scene.setFill(Color.TRANSPARENT);
        root.setStyle("-fx-background-radius: 30px 30px 30px 30px;");
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
        stage.setResizable(false);
    }

    /**
     * This method closes the application and an alert will ask the user to confirm close.
     *
     * @param mouseEvent When the logout label is clicked on the left panel navigation bar.
     */
    public void toClose(MouseEvent mouseEvent) {
        Appointments exitApplication = new Appointments();
        exitApplication.toClose(mouseEvent);
    }

    /**
     * This method filters the appointments to display regular customers.
     *
     * @throws SQLException The exception to throw if there is an issue with the SQL query.
     */
    public void changeToCustomer() throws SQLException {
        ObservableList<Customer> regularCustomers = CustomerDAO.regularCustomers();
        if (regularCustomers.isEmpty()) {
            mainCustomerTable.setItems(regularCustomers);
            mainCustomerTable.setPlaceholder(new Label("No customers available"));
        } else {
            deleteCustomer.setText("Delete Customer");
            searchCustomer.setPromptText("Search by Customer Name ");
            customerNameCol.setText("Customer Name");
            filteredCustomers = new FilteredList<>(regularCustomers);
            applySearch();
        }
    }

    /**
     * This method filters the appointments to display corporate accounts.
     *
     * @throws SQLException The exception to throw if there is an issue with the SQL query.
     */
    public void changeToCorp() throws SQLException {
        ObservableList<Customer> corporateAccounts = CustomerDAO.corporateAccounts();
        if (corporateAccounts.isEmpty()) {
            mainCustomerTable.setItems(corporateAccounts);
            mainCustomerTable.setPlaceholder(new Label("No corporate accounts available"));
        } else {
            deleteCustomer.setText("Delete Account");
            searchCustomer.setPromptText("Search by Company Name");
            customerNameCol.setText("Company Name");
            filteredCustomers = new FilteredList<>(corporateAccounts);
            applySearch();
        }
    }
}
