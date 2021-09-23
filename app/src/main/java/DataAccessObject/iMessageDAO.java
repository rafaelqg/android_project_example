package DataAccessObject;

import java.util.ArrayList;

import entities.Contact;
import entities.Message;

public interface iMessageDAO {
    public boolean saveMessage(Message m, Contact sentBy, Contact sendTo);
    public ArrayList<Message> getAllMessagesToContact(Contact c);
    public ArrayList<Message> getFullConversationWithContact(Contact c);
}
