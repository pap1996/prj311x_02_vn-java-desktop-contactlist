# prj311x_02_vn-java-desktop-contactlist
 This folder contains my submission to the assignment 3 ContactList in the course PRJ311x_02_VN (Programming Desktop App with Java), a part of the path Software Engineering provided by Funix - Learn with Mentor.


## About the project
- **ContactList** allows users to easily store and search information about friends, partners and relatives (hereinafter referred to as contacts). Each **Contact** includes information such as: full name, date of birth, contact phone number. Information about these **Contacts** will be stored in a file for easy processing and reuse later.

- This can be seen as a module in the entire contact management system - Contact Management System. The system helps users to search, update, delete, add information about Contacts, users can also easily manage this set of Contacts by grouping them into different **groups**, which helps significantly reduce searching time

## Functional description
The app has following user interfaces (or windows):
1. **Contact**: list all available contacts
- Filter dropdown: list all groups
- Search: list all available contacts as per the search keyword in the text field
- Delete,  Update, Add: delete selected contact, update selected contact and add new contact as per the following **Add New Contact** window
- Manage groups: manage (add, update, delete or search) groups

![Contact list](/res/image/contact.png)


2. **Add New Contact**: window to key in new information of a new a contact
- All fields must not be empty
- Phone contains digits only
- Email must follow the valid email pattern
- Birthday must be before the current date
- Group dropdown must be the same as filter in the dropdown of Contact window
- Any violation to above will show a warning and user cannot save as a new contact

![Add New Contact](/res/image/addnewcontact.png)

3. **Manage groups**: window to manage (add, update, delete or search) groups
- Search: list all groups available as per the keyword in the text field
- Save: add new group
- Update, Delete: update, delete a given selected group
- The list of groups in the window must be the same as the filter dropdown in **Contact** window and **Add New Contact** window

![Manage groups](/res/image/managegroup.png)

4. **Update a contact**: window to update a contact, somehow similar to add a new contact but the window must be filled with selected contact when opening (while Add New Contact, fields are given empty when opening the window)

![Update a contact](/res/image/updatecontact.png)

5. **Delete a contact**: new window to re-confirm the deletion of selected contact

![Delete a contact](/res/image/delete.png)
## Demo

[![Demo](/res/image/contact-demo.gif)](https://www.youtube.com/watch?v=GB74eQVRANo)



## Further enhancement

- Design a feature that allow switch the group of a Contact
- Add an attribute to show if Contact is active or inactive
- Design a feature to export a list of Contacts in a given group or selected Contacts to a text/flat file
