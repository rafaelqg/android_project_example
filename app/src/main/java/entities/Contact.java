package entities;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;

import com.example.app_example.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import DataAccessObject.DataSource;
import DataAccessObject.iContactDAO;
import DataAccessObject.iMessageDAO;

public class Contact {
    private int id=1;
    private String name;
    private int imageId;
    private ArrayList<Message> messages=new ArrayList<Message>();


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Contact (String name, int imageId){
        this.name=name;
        this.imageId=imageId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public static ArrayList<Contact> loadContactsOnMemory(){
        ArrayList<Contact> list=new ArrayList<Contact>();
        Contact c=new Contact("Luke", R.drawable.luke);
        list.add(c);
        Message m= new Message("Hi Rafael! Lets not forget the next assigment we have scheduled for next week.", new Date());
        c.getMessages().add(m);

        c=new Contact("Yoda", R.drawable.yoda);
        list.add(c);
        m= new Message("Attached to this e-mail the manual for the new version are.", new Date());
        c.getMessages().add(m);

        c=new Contact("Obi Wan", R.drawable.obi_wan);
        list.add(c);
        m= new Message("Hi guys! The meeting was canceled, you are free next evening. Regards, Obi-wan", new Date());
        c.getMessages().add(m);


        c=new Contact("Darth Vader", R.drawable.darth_vader);
        list.add(c);
        m= new Message("Hi Rafael, have you already concluded the deployment of Feature set 67_2345?", new Date());
        c.getMessages().add(m);

        c=new Contact("Darth Maul", R.drawable.darth_maul);
        list.add(c);
        m= new Message("Hi! The customer has found a new bug... We need to fix that as soon as possible.", new Date());
        c.getMessages().add(m);

        return list;
    }

    public static ArrayList<Contact> loadContacts(Context context){
        //load data in database if it is empty
        DataSource ds= new DataSource(context);
        iContactDAO daoContact= ds.getDaoContact();
        iMessageDAO daoMessage= ds.getDaoMessage();

        Contact c=new Contact("Luke", R.drawable.luke);
        initializeContact(daoContact, daoMessage, c,"Hi Rafael! Lets not forget the next assigment we have scheduled for next week.");

        c=new Contact("Yoda", R.drawable.yoda);
        initializeContact(daoContact, daoMessage, c,"Attached to this e-mail the manual for the new version are.");

        c=new Contact("Obi Wan", R.drawable.obi_wan);
        initializeContact(daoContact, daoMessage, c,"Hi guys! The meeting was canceled, you are free next evening. Regards, Obi-wan");

        c=new Contact("Darth Vader", R.drawable.darth_vader);
        initializeContact(daoContact, daoMessage, c,"Hi Rafael, have you already concluded the deployment of Feature set 67_2345?");


        c=new Contact("Darth Maul", R.drawable.darth_maul);
        initializeContact(daoContact, daoMessage, c,"Hi! The customer has found a new bug... We need to fix that as soon as possible.");


        c=getCurrentContact();
        initializeContact(daoContact, daoMessage, c,"");

        //read all data from database
        ArrayList<Contact> list=daoContact.readAllContacts();
        return list;
    }

    private static void initializeContact(iContactDAO daoContact, iMessageDAO daoMessage, Contact c, String message){
        daoContact.saveContact(c);
        ArrayList<Message> messages=daoMessage.getAllMessagesToContact(c);
        if(messages.size()==0) {
            Message m = new Message(message, new Date());
            daoMessage.saveMessage(m,c,getCurrentContact());
        }
    }

    public static Contact getCurrentContact(){
        final String currentContactId="Rafael Q. Gonçalves";
        final int currentUserProfilePhotoId=R.drawable.rafael_goncalves_profile;
        Contact c= new Contact(currentContactId,currentUserProfilePhotoId);
        c.setId(6);
        return c;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(name, contact.name);
    }
}

