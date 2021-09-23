package DataAccessObject.REST_XML;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import DataAccessObject.REST.MessageDAOREST;
import DataAccessObject.iContactDAO;
import entities.Contact;
import entities.Message;

public class ContactDAORESTXML implements iContactDAO {
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
        final String url="http://192.168.0.15:3333/contacts";
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
                    con.setRequestProperty("Accept", "application/xml");
                    InputStream is = null;
                    is = con.getInputStream();
                    /*
                    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                    String response = s.hasNext() ? s.next() : "";
                    Log.i("XML", "Requesting GET concluded." + response);
                    */

                    //parse XML to object
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = null;
                    try {
                        dBuilder = dbFactory.newDocumentBuilder();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                    Document doc = null;
                    try {
                        doc = dBuilder.parse(is);
                    } catch (IOException | SAXException e) {
                        e.printStackTrace();
                    }

                    NodeList nList = doc.getElementsByTagName("object");
                    Log.i("XMLDAO","Children count:"+String.valueOf(nList.getLength()));
                    for (int i = 0; i < nList.getLength(); i++) {
                        NodeList childrenTags=nList.item(i).getChildNodes();
                        int childrenTagsCount=childrenTags.getLength();

                        /*
                        //generic loop over all fields
                        for(byte j=0;j<childrenTagsCount;j++) {
                            if(childrenTags.item(j).getNodeName().equals("#text")) continue;
                            Log.i("XML", "XML - node id:" + childrenTags.item(j).getNodeName());
                            Log.i("XML", "XML - node text content:" + );
                        }
                         */
                        int id=Integer.valueOf(childrenTags.item(0).getTextContent());
                        String name=childrenTags.item(1).getTextContent();
                        int imageId=Integer.valueOf(childrenTags.item(2).getTextContent());
                        Contact c= new Contact(name, imageId);
                        c.setId(id);
                        list.add(c);
                    }
                    for(Contact c: list){
                        ArrayList<Message> messages=new MessageDAOREST().getFullConversationWithContact(c);;
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
