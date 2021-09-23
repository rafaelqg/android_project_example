package DataAccessObject.REST;

import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import DataAccessObject.iMessageDAO;
import entities.Contact;
import entities.Message;

public class MessageDAOREST implements iMessageDAO {
    private boolean saveMessageResult;
    private ArrayList<Message> messages;
    @Override
    public boolean saveMessage(Message m, Contact sentBy, Contact sendTo)  {
        this.saveMessageResult=false;
        Thread t= new Thread(){
            public void run(){
                String resourceURI = "http://192.168.0.17:3333/messages";
                String message=m.getMessage();
                try{
                    message=URLEncoder.encode(m.getMessage(),"UTF-8");
                }catch(UnsupportedEncodingException e){
                    e.printStackTrace();
                }

                String httpParameters ="?sent_by="+sentBy.getId()+"&sent_to="+sendTo.getId()+"&message="+ message;
                String formatedURL = resourceURI + httpParameters;
                try {
                    URL url = new URL(formatedURL);
                    HttpURLConnection con = null;
                    con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");;
                    InputStream is = null;
                    is = con.getInputStream();
                    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                    String response = s.hasNext() ? s.next() : "";
                    Log.i("ASyncTask", "requesting POST concluded." + response);
                    saveMessageResult=true;
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
            this.saveMessageResult=false;
        }
        return this.saveMessageResult;
    }

    @Override
    public ArrayList<Message> getAllMessagesToContact(Contact c) {

        this.messages=new ArrayList<Message>();
        Thread t= new Thread(){
            public void run(){
                String resourceURI = "http://192.168.0.17:3333/messages/allsentby";
                String httpParameters ="?sent_by="+c.getId();
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
                    Log.i("ASyncTask", "Requesting GET concluded." + response);

                    JsonParser parser = new JsonParser();
                    //JsonObject obj = parser.parse(response).getAsJsonObject();
                    JsonArray array = parser.parse(response).getAsJsonArray();
                    for(char i=0;i<array.size();i++) {
                        JsonObject obj = (JsonObject) array.get(i);
                        String message = obj.get("message").getAsString();
                        Date dt=new Date();
                        try {
                            dt = new Date(obj.get("when").getAsLong());
                        }catch(IllegalArgumentException e){
                            e.printStackTrace();
                        }
                        Message m = new Message(message, dt);
                        messages.add(m);
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
        return this.messages;
    }

    @Override
    public ArrayList<Message> getFullConversationWithContact(Contact c) {
        this.messages=new ArrayList<Message>();
        Thread t= new Thread(){
            public void run(){
                String resourceURI = "http://192.168.0.17:3333/messages/getfullconversation";
                String httpParameters ="?user_id="+c.getId();
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
                    Log.i("ASyncTask", "Requesting GET concluded." + response);

                    JsonParser parser = new JsonParser();
                    //JsonObject obj = parser.parse(response).getAsJsonObject();
                    JsonArray array = parser.parse(response).getAsJsonArray();
                    for(char i=0;i<array.size();i++) {
                        JsonObject obj = (JsonObject) array.get(i);
                        String message = obj.get("message").getAsString();
                        Date dt = new Date();
                        try {
                            dt=new Date(obj.get("when").getAsLong());
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        Message m = new Message(message, dt);
                        if (obj.get("sent_by").getAsInt()==c.getId()) {
                            m.setSender(c);
                        }else{
                            m.setSender(Contact.getCurrentContact());
                        }

                        messages.add(m);
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
        return this.messages;
    }

    public boolean sendMultimedia(byte[] data, Contact sentBy, Contact sendTo, double latitude, double longitude)  {
        this.saveMessageResult=false;
        Thread t= new Thread(){

            @RequiresApi(api = Build.VERSION_CODES.O)
            public void run(){
                String resourceURI = "http://192.168.0.17:3333/multimedia";
                String message=java.util.Base64.getEncoder().encodeToString(data);
                try{
                    message=URLEncoder.encode(message,"UTF-8");

                    URL url = new URL( resourceURI);
                    Map<String,Object> params = new LinkedHashMap<>();

                    params.put("sent_by", sentBy.getId());
                    params.put("sent_to", sendTo.getId());
                    params.put("latitude", latitude);
                    params.put("longitude", longitude);
                    params.put("message",message);

                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String,Object> param : params.entrySet()) {
                        if (postData.length() != 0) postData.append('&');
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        //postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                        postData.append(param.getValue());
                    }
                    byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postDataBytes);

                    InputStream is = conn.getInputStream();
                    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                    String response = s.hasNext() ? s.next() : "";
                    Log.i("ASyncTask", "requesting POST concluded." + response);
                    saveMessageResult=true;


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
            this.saveMessageResult=false;
        }
        return this.saveMessageResult;
    }
}
