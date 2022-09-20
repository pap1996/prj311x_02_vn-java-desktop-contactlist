package controller;

import dao.ContactDAO;
import dao.GroupDAO;
import entity.Contact;
import entity.Group;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateContactController {

    @FXML
    private TextField firstName, lastName, phone, email;
    @FXML
    private DatePicker dob;
    @FXML
    private ComboBox<Group> cbGroup;
    @FXML
    private Label lblFirstName, lblLastName, lblPhone, lblEmail, lbldob;
    @FXML
    private Button btnAdd, btnClose;

    private Contact needUpdateContact;
    private ObservableList<Contact> contacts = ContactDAO.getContacts();
    private ContactDAO contactDAO = ContactDAO.getContactInstance();

    public void setContactController(ContactController contactController) {
        throw new UnsupportedOperationException("Remove this line and implement your code here!");
    }


    public void setContacts(List<Contact> contacts) {
        throw new UnsupportedOperationException("Remove this line and implement your code here!");
    }

    public void setUpdatedContact(Contact updatedContact) throws Exception {
        this.needUpdateContact = updatedContact;


        // output inform into textfield
        firstName.setText(needUpdateContact.getFirstName());
        lastName.setText(needUpdateContact.getLastName());
        email.setText(needUpdateContact.getEmail());
        phone.setText(needUpdateContact.getPhone());
        cbGroup.setItems(GroupDAO.getGroups());
        cbGroup.getSelectionModel().select(new Group(needUpdateContact.getGroup()));

        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(needUpdateContact.getDob());
        dob.setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

    }


    @FXML
    void initialize() throws Exception {

    }

    public void updateContact(ActionEvent evt) throws Exception {
        if (evt.getSource() == btnAdd) {
            System.out.println("Click Save");
            saveContact();
        } else if (evt.getSource() == btnClose) {
            Node source = (Node) evt.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
    }

    public void saveContact() throws Exception {
        System.out.println("Save Contact ...");
        int valiCheck = 0;
        valiCheck += checkEmpty(firstName) ? 1 : 0;
        valiCheck += checkEmpty(lastName) ? 1 : 0;
        valiCheck += checkEmpty(phone) ? 1 : checkDigit(phone) ? 1 : 0;
        valiCheck += checkEmpty(email) ? 1 : 0;
        valiCheck += checkValidEmail(email) ? 1 : 0;
        valiCheck += checkEmpty(dob) ? 1 : 0;
        valiCheck += checkValidDate(dob) ? 1 : 0;

        if (valiCheck == 0) {
            Contact c = new Contact(firstName.getText().trim(),
                    lastName.getText().trim(),
                    phone.getText().trim(),
                    email.getText().trim(),
                    dob.getValue().toString(),
                    cbGroup.getSelectionModel().getSelectedItem().getName());

            int i = contactDAO.indexOf(contacts, c);
            int j = contactDAO.indexOf(contacts, needUpdateContact);
            if (i == j || i == -1) {
                contactDAO.updateContact(contacts, c, j);
                contactDAO.saveToFile(contacts, "data/contact.txt");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("Contact has been updated");
                alert.setHeaderText(null);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Information of contact is exisited");
                alert.showAndWait();
            }
        }


    }


    private boolean checkEmpty(Node checkNode) {

//        System.out.println("Check empty ....");
        Pane parent = (Pane) ((Node) checkNode).getParent();
        Node childNode = parent.getChildren().filtered(node -> node.getStyleClass().contains("fixNote")
                || node.getStyleClass().contains("fixNoteVisible")).get(0);

        Node fixLabelNode = parent.getChildren().filtered(node -> node.getStyleClass().contains("fixLabel")).get(0);
        String label = ((Label) fixLabelNode).getText();
//        System.out.println(childNode);

        String valueCheck = checkNode instanceof TextField ? ((TextField) checkNode).getText().trim() :
                checkNode instanceof DatePicker ? ((DatePicker) checkNode).getValue() == null ? ""
                        : ((DatePicker) checkNode).getValue().toString() : "";
        if (valueCheck.isEmpty()) {
            ((Label) childNode).setText(label + " can not be empty");
            childNode.getStyleClass().set(childNode.getStyleClass().size() - 1, "fixNoteVisible");
            return true;
        }
        childNode.getStyleClass().set(childNode.getStyleClass().size() - 1, "fixNote");
        return false;
    }


    private boolean checkDigit(TextField textField) {
//        System.out.println("Check digit valid ....");
        Pane parent = (Pane) ((Node) textField).getParent();
        Node childNode = parent.getChildren().filtered(node -> node.getStyleClass().contains("fixNote")
                || node.getStyleClass().contains("fixNoteVisible")).get(0);
//        System.out.println(childNode);

        Node fixLabelNode = parent.getChildren().filtered(node -> node.getStyleClass().contains("fixLabel")).get(0);
        String label = ((Label) fixLabelNode).getText();
        if (!textField.getText().trim().matches("\\d+")) {
            ((Label) childNode).setText(label + " contains digit only");
            childNode.getStyleClass().set(childNode.getStyleClass().size() - 1, "fixNoteVisible");
            return true;
        }
        childNode.getStyleClass().set(childNode.getStyleClass().size() - 1, "fixNote");
        return false;
    }

    private boolean checkValidDate(DatePicker dob) {
        Pane parent = (Pane) ((Node) dob).getParent();
        Node childNode = parent.getChildren().filtered(node -> node.getStyleClass().contains("fixNote")
                || node.getStyleClass().contains("fixNoteVisible")).get(0);
//        System.out.println(childNode);

        Node fixLabelNode = parent.getChildren().filtered(node -> node.getStyleClass().contains("fixLabel")).get(0);
        String label = ((Label) fixLabelNode).getText();
        if (!dob.getValue().isBefore(LocalDate.now())) {
            ((Label) childNode).setText(label + " must be less than current date");
            childNode.getStyleClass().set(childNode.getStyleClass().size() - 1, "fixNoteVisible");
            return true;
        }
        childNode.getStyleClass().set(childNode.getStyleClass().size() - 1, "fixNote");
        return false;
    }


    private boolean checkValidEmail(TextField textField) {
        Pane parent = (Pane) ((Node) textField).getParent();
        Node childNode = parent.getChildren().filtered(node -> node.getStyleClass().contains("fixNote")
                || node.getStyleClass().contains("fixNoteVisible")).get(0);
//        System.out.println(childNode);

        Node fixLabelNode = parent.getChildren().filtered(node -> node.getStyleClass().contains("fixLabel")).get(0);
        String label = ((Label) fixLabelNode).getText();

        Pattern emailNamePtrn = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher testMatch = emailNamePtrn.matcher(textField.getText());
        if (!testMatch.matches()) {
            ((Label) childNode).setText(label + " is invalid");
            childNode.getStyleClass().set(childNode.getStyleClass().size() - 1, "fixNoteVisible");
            return true;
        }
        childNode.getStyleClass().set(childNode.getStyleClass().size() - 1, "fixNote");
        return false;
    }

}
