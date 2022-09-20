package controller;

import dao.ContactDAO;
import dao.GroupDAO;
import entity.Contact;
import entity.Group;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;


public class ContactController {

    @FXML
    private TableView<Contact> tblContact;
    @FXML
    private TextField search;
    @FXML
    private ComboBox<Group> cbGroup;
    @FXML
    private Button btnSearch, btnGroup, btnDelete, btnAdd, btnUpdate;

    @FXML
    private AnchorPane mainWindow;

    private ContactDAO contactDAO;
    private ObservableList<Contact> contacts;

    private ObservableList<Group> groupsToShow;
    private ObservableList<Group> groups;

    private String CONTACTFILE = "data" + File.separator + "contact.txt";

    @FXML
    void initialize() {

        //get contactDAO and contacts;
        contactDAO = ContactDAO.getContactInstance();
        contacts = ContactDAO.getContacts();

        // get groups
        groups = GroupDAO.getGroups();
        groupsToShow = GroupDAO.getGroupsToShow();


        // Create columns and populate table with columns
        TableColumn<Contact, String> fname = new TableColumn<>("First name");
        fname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tblContact.getColumns().add(fname);

        TableColumn<Contact, String> lname = new TableColumn<>("Last name");
        lname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tblContact.getColumns().add(lname);

        TableColumn<Contact, String> phone = new TableColumn<>("Phone");
        phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tblContact.getColumns().add(phone);

        TableColumn<Contact, String> email = new TableColumn<>("Email");
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        tblContact.getColumns().add(email);

        TableColumn<Contact, String> dob = new TableColumn<>("Birth Date");
        dob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        tblContact.getColumns().add(dob);

        TableColumn<Contact, String> group = new TableColumn<>("Group Name");
        group.setCellValueFactory(new PropertyValueFactory<>("group"));
        tblContact.getColumns().add(group);


        // load data into tableview tblContact
        showContact(contacts);

        // load group into combobox cbGroup
        showGroup(groupsToShow);


        // set up InvisibleMode vs Visible for btnDelete and Update
        // btnDelete and btnUpdate will be visible if table is selected


        /** Another way is binding the button
         BooleanBinding tableSelected = Bindings.createBooleanBinding()
         */
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);

        btnUpdate.setOnAction(actionEvent -> {
            try {
                updateContact();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        btnDelete.setOnAction(actionEvent -> {
            try {
                deleteContact();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        btnGroup.setOnAction(actionEvent -> {
            try {
                groupPanel();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        tblContact.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Contact>() {
            @Override
            public void changed(ObservableValue<? extends Contact> observableValue, Contact contact, Contact t1) {
                if (t1 == null) {
                    btnDelete.setDisable(true);
                    btnUpdate.setDisable(true);
                } else {
                    btnDelete.setDisable(false);
                    btnUpdate.setDisable(false);
                }
            }
        });


    }

    //output all contact to tblContact
    public void showContact(ObservableList<Contact> c) {

        tblContact.setItems(c);
        /**
         // !!! NOTES: tbleView.getItems().addAll(list) -> cannot update obs list later
         // setItems(list) is preferred

         /* Save for further investigate
         System.out.println("location of get Items : " + tblContact.getItems().hashCode());
         System.out.println("location of contacts : " + contacts.hashCode());
         System.out.println("test the same adress : "+ tblContact.getItems().equals(contacts));
         //        System.out.println(contacts.toString());
         //        tblContact.setItems(c);
         tblContact.getItems().clear(); // !! kho hieu
         System.out.println("betwen 2 step : " + c);
         tblContact.setItems(c);
         //        tblContact.getItems().setAll(c);

         System.out.println("location of get Items - after: " + tblContact.getItems().hashCode());
         System.out.println("location of contacts - after : " + contacts.hashCode());
         System.out.println("test the same adress - after : "+ tblContact.getItems().equals(contacts));

         */
    }

    // output all groups to dropdown list

    public void showGroup(ObservableList<Group> g) {


        cbGroup.setItems(g);
        cbGroup.getSelectionModel().selectFirst();
    }

    //do corresponding actions for search, delete, update and add contact
    public void searchContact(ActionEvent evt) throws Exception {


        if (evt.getSource() == btnSearch) {
            searchOnKeyChanged();
        } else if (evt.getSource() == btnAdd) {
            addContact();
        } else if (evt.getSource() == btnUpdate) {
            updateContact();
        }
    }

    private void addContact() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/ui/addContact.fxml"));

        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initOwner(mainWindow.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(root));
        stage.setTitle("Add a new Contact");
        stage.show();

    }

    //manage the groups
    public void groupPanel() throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();
        StringBuilder builder = new StringBuilder();
        fxmlLoader.setLocation(getClass().getResource("/ui/group.fxml"));

        /** NOTES : distinguish between / and \ for file path and when use in getResource case
         *  (in getResource only use / to get file in Module)**/

        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initOwner(mainWindow.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(root));
        stage.setTitle("Manage Group");
        stage.show();
    }

    //update a contact
    public void updateContact() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/ui/updateContact.fxml"));

        Parent root = fxmlLoader.load();

        Contact needUpdateContact = tblContact.getSelectionModel().getSelectedItem();
        UpdateContactController updateContactController = fxmlLoader.getController();
        updateContactController.setUpdatedContact(needUpdateContact);


        Stage stage = new Stage();
        stage.initOwner(mainWindow.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(root));
        stage.setTitle("Update a Contact");
        stage.show();

    }

    //delete a selected contact
    public void deleteContact() throws Exception {

        Contact selectedItem = tblContact.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Do you want to delete selected contact?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            contacts.remove(selectedItem);
            contactDAO.saveToFile(contacts, CONTACTFILE);
        }

    }


    @FXML
    public void handleFilter(ActionEvent actionEvent) {
        Group selectedGroup = cbGroup.getSelectionModel().getSelectedItem();
        showContact(contactDAO.contactByGroup(contacts, selectedGroup.getName()));
    }


    // Reuse for search and search on Key Changed
    @FXML
    public void searchOnKeyChanged() {

        ContactDAO instance = ContactDAO.getContactInstance();
        ObservableList<Contact> collection = ContactDAO.getContacts();
        String group = cbGroup.getSelectionModel().getSelectedItem().getName();
        String searchKey = search.getText().trim();

        ObservableList<Contact> searchResult = instance.search(collection, group, searchKey);

        if (searchResult.size() > 0) {
            showContact(instance.search(collection, group, searchKey));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText("No contacts found!");
            Optional<ButtonType> result = alert.showAndWait();
        }

    }

}
