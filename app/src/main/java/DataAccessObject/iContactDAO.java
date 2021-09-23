package DataAccessObject;

import java.util.ArrayList;

import entities.Contact;

public interface iContactDAO {

    public boolean saveContact(Contact c);
    public boolean deleteContact(Contact c);
    public ArrayList<Contact> readAllContacts();
    public Contact findContactByIdentifierName(String identifier);
}
