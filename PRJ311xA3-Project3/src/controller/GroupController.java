package controller;


import dao.ContactDAO;
import dao.GroupDAO;
import entity.Contact;
import entity.Group;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


public class GroupController {

    @FXML
    private Button btnSearchGroup, btnSaveGroup, btnUpdateGroup, btnDeleteGroup, btnClose;

    @FXML
    private TextField groupName, groupSearch;

    @FXML
    private ListView<Group> groupList;

    private GroupDAO groupDAO = GroupDAO.getGroupInstance();

    private ObservableList<Group> groups = GroupDAO.getGroups();

    private final String GROUPFILE = "data" + File.separator + "group.txt";
    private String CONTACTFILE = "data" + File.separator + "contact.txt";


    private ContactDAO contactDAO = ContactDAO.getContactInstance();
    private ObservableList<Contact> contacts = ContactDAO.getContacts();

    @FXML
    void initialize() {

        // load groups
        showGroup(groups);


        // set up handler on action for button
        btnClose.setOnAction(evt -> {
            Node source = (Node) evt.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        });
        btnSearchGroup.setOnAction(actionEvent -> searchAction());
        groupSearch.setOnKeyReleased(keyEvent -> searchAction());
        btnSaveGroup.setOnAction(actionEvent -> {
            try {
                addAction(actionEvent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // set up Delete button and Group button become Disable when no record in ListView is selected
        btnDeleteGroup.setDisable(true);
        btnUpdateGroup.setDisable(true);
        groupList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Group>() {
            @Override
            public void changed(ObservableValue<? extends Group> observableValue, Group group, Group t1) {
                if (t1 != null) {
                    btnDeleteGroup.setDisable(false);
                    btnUpdateGroup.setDisable(false);
                    btnSaveGroup.setDisable(true);
                    groupName.setText(t1.getName());
                } else {
                    btnDeleteGroup.setDisable(true);
                    btnUpdateGroup.setDisable(true);
                    btnSaveGroup.setDisable(false);

                    groupName.setText(null);
                }
            }
        });


        btnDeleteGroup.setOnAction(actionEvent -> {
            try {
                deleteAction();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }


    //search action
    public void searchAction() {

        String searchKey = groupSearch.getText();
        showGroup(groupDAO.search(groups, searchKey));

    }

    //add new group action
    public void addAction(ActionEvent evt) throws Exception {
        String name = groupName.getText().trim();
        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Group name cannot be empty");
            alert.showAndWait();
        } else {
            Group g = new Group(name);
            int i = groupDAO.indexOf(groups, g);
            if (i != -1) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Group name exists already, choose another name");
                alert.showAndWait();
            } else {
                groupDAO.saveGroupToList(groups, g);
                groupDAO.saveGroupToFile(groups, GROUPFILE);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Information");
                alert.setContentText("A new group has been added");
                alert.showAndWait();


            }
        }
    }

    //update group name
    public void updateAction() throws Exception {

        Group selectedGroup = groupList.getSelectionModel().getSelectedItem();
        int index = groupDAO.indexOf(groups, selectedGroup);
        String newGroup = groupName.getText().trim();
        if (newGroup.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Group name cannot be empty");
            alert.showAndWait();
            groupName.setText(selectedGroup.getName());
        } else {
            String oldGroup = selectedGroup.getName();
            boolean isUpdate = groupDAO.updateGroup(groups, index, oldGroup, groupName.getText());
            if (isUpdate) {
                groupDAO.saveGroupToFile(groups, GROUPFILE);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("A group has been updated");
                alert.showAndWait();


                /** Index run fail since we change the predicate -> cons of using filterList*/
                contacts.forEach(contact -> {

                    if (contact.getGroup().equals(oldGroup)) {
                        int i = contactDAO.indexOf(contacts, contact);
                        contact.setGroup(groupName.getText());
                        contactDAO.updateContact(contacts, contact, i);
                    }

                });

                contactDAO.saveToFile(contacts, CONTACTFILE);

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Group name exists already, update unsucessfully");
                alert.showAndWait();
                groupName.setText(selectedGroup.getName());
            }
        }
    }

    //delete a group, delete failed if there are some contact is in deleted one
    public void deleteAction() throws Exception, IndexOutOfBoundsException {
        Group selectedGroup = groupList.getSelectionModel().getSelectedItem();
        int i = contactDAO.contactByGroup(contacts, selectedGroup.getName()).size();
        if (i > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("The group has some contacts, cannot delete this one");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Do you wanna delete selected group?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {

                groups.remove(selectedGroup);
                groupDAO.saveGroupToFile(groups, GROUPFILE);
            }
        }
    }

    //operations on each button on window
    public void groupAction(ActionEvent evt) throws Exception {
        throw new UnsupportedOperationException("Remove this line and implement your code here!");
    }

    //output all groups to table view
    public void showGroup(ObservableList<Group> groups) {
        groupList.setItems(groups);
    }


}
