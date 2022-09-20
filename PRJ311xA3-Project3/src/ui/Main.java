package ui;

import dao.ContactDAO;
import dao.GroupDAO;
import entity.Group;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("contact.fxml"));
        primaryStage.setTitle("Contact Management System");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        ContactDAO.getContactInstance().loadContact("data/contact.txt");
        GroupDAO.getGroupInstance().loadGroup("data/group.txt");
    }
}
