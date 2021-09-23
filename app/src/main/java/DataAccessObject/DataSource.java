package DataAccessObject;

import android.content.Context;

import DataAccessObject.REST.ContactDAOREST;
import DataAccessObject.REST.MessageDAOREST;
import DataAccessObject.REST_XML.ContactDAORESTXML;
import DataAccessObject.SQLite.ContactDAOSQLite;
import DataAccessObject.SQLite.MessageDAOSQLite;

public class DataSource {
    private iContactDAO daoContact;
    private iMessageDAO daoMessage;

    public DataSource(Context context){
        this.daoContact= new ContactDAOSQLite(context,ContactDAOSQLite.DB_NAME,null, ContactDAOSQLite.DB_VERSION);
        this.daoMessage= new MessageDAOSQLite(context,ContactDAOSQLite.DB_NAME,null, ContactDAOSQLite.DB_VERSION);
        //this.daoContact= new ContactDAOREST();
        //this.daoContact= new ContactDAORESTXML();
        //this.daoContact= new ContactDAOREST();
        //this.daoMessage= new MessageDAOREST();
    }

    public iContactDAO getDaoContact() {
        return daoContact;
    }

    public void setDaoContact(iContactDAO daoContact) {
        this.daoContact = daoContact;
    }

    public iMessageDAO getDaoMessage() {
        return daoMessage;
    }

    public void setDaoMessage(iMessageDAO daoMessage) {
        this.daoMessage = daoMessage;
    }
}
