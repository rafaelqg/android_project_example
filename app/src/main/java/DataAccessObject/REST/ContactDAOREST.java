package DataAccessObject.REST;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import DataAccessObject.SQLite.MessageDAOSQLite;
import DataAccessObject.iContactDAO;
import entities.Contact;
import entities.Message;

public class ContactDAOREST implements iContactDAO {
    private ArrayList<Contact> list= new ArrayList<>();
    @Override
    public boolean saveContact(Contact c) {
        return false;
    }

    @Override
    public boolean deleteContact(Contact c) {
        return false;
    }

    @Override
    public ArrayList<Contact> readAllContacts() {
        final String url="http://192.168.0.17:3333/contacts";
        //final String url="http://192.168.0.14:8080/JavaRestBackendExample/Contacts";
        Thread t= new Thread(){
            public void run(){
                String resourceURI =  url;
                String httpParameters ="";
                String formatedURL = resourceURI + httpParameters;
                try {
                    URL url = new URL(formatedURL);
                    HttpURLConnection con = null;
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    InputStream is = null;
                    is = con.getInputStream();
                    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                    String response = s.hasNext() ? s.next() : "";
                    Log.i("ASyncTask-18052021JAVA", "Requesting GET concluded." + response);

                    Gson gson = new GsonBuilder().create();
                    list= (ArrayList<Contact>) gson.fromJson(response, new TypeToken<ArrayList<Contact>>() {}.getType());
                    Log.i("ASyncTask", "Contacts parsed:" + list.size());
                    for(Contact c: list){
                        ArrayList<Message> messages=new MessageDAOREST().getFullConversationWithContact(c);
                        Log.i("ASyncTask", "Messages found:" + messages.size());
                        c.setMessages(messages);
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        t.start();
        try {
            t.join();
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Contact findContactByIdentifierName(String identifier) {
        return null;
    }



}
