package dao;

import controller.ContactController;
import entity.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;


public class ContactDAO {

    private static ContactDAO contactInstance = new ContactDAO();;

    private ContactDAO() {};

    private static ObservableList<Contact> contacts;

    public ObservableList<Contact> loadContact(String fname) throws IOException {

        contacts =  FXCollections.observableArrayList();

        // Note: FilteredList not modify the list, must use getSource() to
//        try {
//            contacts.add(new Contact("John", "Snow", "84987234123", "john@gmail.com", "12-31-1987", "Family"));
//            contacts.add(new Contact("Adam", "Smith", "84980720100", "adam.smithn@gmail.com", "1990-07-22", "Friend"));
//            contacts.add(new Contact("Jean", "Tonogbanua", "85231678987", "jean@yahoo.com", "09-09-1993", "Family"));
//            contacts.add(new Contact("An", "Ha", "84123098345", "AnHa@gmail.com", "0017-08-31", "Friend"));
//
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }

        BufferedReader fileReader = new BufferedReader(new FileReader(fname));
        String line;
        while ((line = fileReader.readLine()) != null) {
           String[] stringPart = line.trim().split(":");
           contacts.add(new Contact(stringPart[0].trim(),stringPart[1].trim(),stringPart[2].trim()
                   , stringPart[3].trim(), stringPart[4].trim(), stringPart[5].trim()));
        }

        fileReader.close();

        return contacts;
    }

    public static ObservableList<Contact> getContacts() {
        return contacts;
    }

    public static ContactDAO getContactInstance() {
        return contactInstance;
    }

    //save all Contacts from a given list to a text file
    public  void saveToFile(ObservableList<Contact> g, String fname) throws Exception {
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fname));
        for (Contact contact: g) {
            fileWriter.write(contact.toString());
        }
        fileWriter.close();
    }

    //return the first position of a given contact g in the list
    //otherwise return -1
    public int indexOf(ObservableList<Contact> list, Contact g) {
        for (int i = 0; i < list.size(); i++) {
            Contact x = list.get(i);
            if (x.getFirstName().trim().equalsIgnoreCase(g.getFirstName()) &&
                    x.getLastName().trim().equalsIgnoreCase(g.getLastName())){
                return i;
            }
        }
        return -1;
    }
    //save a Contact to a current list
    public  void saveToList(ObservableList<Contact> list, Contact g) {
        list.add(g);
    }
    //update information of a contact c at position i in the list
    public  void updateContact(ObservableList<Contact> list, Contact c, int i) {
        list.set(i, c);
    }
    //return a list of Contact who information matched given search word
    public  ObservableList<Contact> search(ObservableList<Contact> c, String group, String search) {


        ObservableList<Contact> searchGroupContacts= contactByGroup(c,group);
        FilteredList<Contact> searchedContact = new FilteredList<>(searchGroupContacts, new Predicate<Contact>() {
            @Override
            public boolean test(Contact contact) {
                return contact.toString().toLowerCase().contains(search.toLowerCase());
            }
        });

        return searchedContact;

    }
    //return a list of Contact who is in a given group
    public  ObservableList<Contact> contactByGroup(ObservableList<Contact> c, String group) {

        FilteredList<Contact> groups =  new FilteredList<Contact>(c, new Predicate<Contact>() {
            @Override
            public boolean test(Contact contact) {
                return group.equals("All") ? true: contact.getGroup().trim().equals(group);
            }
        });


        return groups;

    }
}
