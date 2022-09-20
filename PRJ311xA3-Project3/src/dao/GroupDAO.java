package dao;


import controller.ContactController;
import entity.Group;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.function.Predicate;


public class GroupDAO {

    private static GroupDAO groupInstance = new GroupDAO();

    private static ObservableList<Group> groups = FXCollections.observableArrayList();

    private static ObservableList<Group> groupsToShow = FXCollections.observableArrayList(new Group("All"));

    private GroupDAO() {
    }

    public static ObservableList<Group> getGroups() {
        return groups;
    }

    public static GroupDAO getGroupInstance() {
        return groupInstance;
    }

    public static ObservableList<Group> getGroupsToShow() {
        return groupsToShow;
    }

    /*Family
    Friend
    Colleagues
        * */
    //load all groups from the file group in to a list
    public ObservableList<Group> loadGroup(String fname) throws Exception {

//        groups.add(new Group("Family"));
//        groups.add(new Group("Friend"));
//        groups.add(new Group("Colleagues"));

        BufferedReader fileReader = new BufferedReader(new FileReader(fname));
        String line;
        while ((line = fileReader.readLine()) != null) {
            groups.add(new Group(line.trim()));
        }

        fileReader.close();

        groupsToShow.addAll(groups);

        groups.addListener(new ListChangeListener<Group>() {
            @Override
            public void onChanged(Change<? extends Group> change) {

                while (change.next()) {
                    if (change.wasReplaced()) {
                        Group oldGroup = change.getRemoved().get(0);
                        Group newGroup = change.getAddedSubList().get(0);
                        int i = groupsToShow.indexOf(oldGroup);
                        groupsToShow.set(i,newGroup);
                    } else if (change.wasAdded()) {
                        groupsToShow.addAll(change.getAddedSubList());
                    } else if (change.wasRemoved()) {
                        groupsToShow.removeAll(change.getRemoved());
                    }
                }

            }
        });

        return groups;
    }

    //save all groups from a given list to a text file
    public  void saveGroupToFile(ObservableList<Group> g, String fname) throws Exception {
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fname));
        for (Group group: g) {
            fileWriter.write(group.getName());
            fileWriter.write("\n");
        }
        fileWriter.close();
    }

    //return the first position of a given contact group in the list
    //otherwise return -1
    public int indexOf(ObservableList<Group> list, Group g) {

        for (int i=0; i < list.size(); i++) {
            Group item = list.get(i);
            if(item.getName().toLowerCase().trim().equals(g.getName().trim().toLowerCase())) {
                return i;
            }
        }
        return -1;
    }
    //save a group to a current list
    public  void saveGroupToList(ObservableList<Group> list, Group g) {
        list.add(g);
    }

    //return a list of Contact who information matched given search word

    /** Note this search -> return a new filterlist will affect the size of list later -> OutOfBound Exception */
    public  ObservableList<Group> search(ObservableList<Group> c, String search) {
        return search.trim().isEmpty() ? c :  new FilteredList<Group>(c, new Predicate<Group>() {
            @Override
            public boolean test(Group group) {
                return group.getName().toLowerCase().contains(search.trim().toLowerCase());
            }
        });

//        return   new FilteredList<Group>(c, new Predicate<Group>() {
//            @Override
//            public boolean test(Group group) {
//                return search.trim().isEmpty() ? true : group.getName().toLowerCase().contains(search.trim().toLowerCase());
//            }
//        });
    }

    //update a group in groups by a newGroup
    public  boolean updateGroup(ObservableList<Group> groups, int i, String oldGroup, String newGroup) {
        int c = 0;
        for (Group g: groups) {
            c += g.getName().trim().equalsIgnoreCase(newGroup) ? 1 : 0;
        }
        if (c ==0) {
//            groups.get(i).setName(newGroup);
            groups.set(i,new Group(newGroup));
            return true;
        }
        return false;
    }


    public void  deleteGroup(Group g) {
        this.groups.remove(g);
    }
}
