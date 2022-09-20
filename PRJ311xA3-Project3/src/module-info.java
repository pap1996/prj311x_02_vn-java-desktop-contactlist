module Project3 {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.base;

    opens ui ;
    opens controller ;
    opens entity;
    opens dao;
}