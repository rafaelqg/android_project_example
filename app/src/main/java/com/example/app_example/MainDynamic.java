package com.example.app_example;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import DataAccessObject.DataSource;
import DataAccessObject.REST.ContactDAOREST;
import DataAccessObject.REST.MessageDAOREST;
import DataAccessObject.REST_XML.ContactDAORESTXML;
import entities.Contact;
import entities.Message;
import viewhelper.GPSLocation;
import viewhelper.MainMenuViewHelper;

public class MainDynamic extends AppCompatActivity {

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dynamic);
        Toolbar toolbar = findViewById(R.id.toolbarMD);
        setSupportActionBar(toolbar);
        this.updateMessage();
       // new ContactDAORESTXML().readAllContacts();
      /*
        ThreadA a=new ThreadA();
        byte p1=5;
        byte p2=4;
        a.setParameterA(p1);
        a.setParameterA(p2);
       // a.start();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        this.runOnUiThread(a);



        ThreadGetMessagesXML myXMLThread= new ThreadGetMessagesXML();
       //myXMLThread.execute();

      ThreadReadExternalFileByURL externalFileThread= new ThreadReadExternalFileByURL();
        externalFileThread.execute("https://www.android.com/static/2016/pdfs/enterprise/Android_Enterprise_Security_White_Paper_2019.pdf","paper.pdf");




        //ThreadImageFile imageFileThread= new ThreadImageFile();
        //imageFileThread.execute();

        //ThreadGetMessages myThread= new ThreadGetMessages();
        //myThread.execute();


        ThreadCallPostRestService restPost= new  ThreadCallPostRestService();
        restPost.execute("Chris","Redfield");

       */

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateMessage(){
        LinearLayout ll=(LinearLayout)findViewById(R.id.LLItems);
        ll.removeAllViews();
        ArrayList<Contact> contacts= new DataSource(this).getDaoContact().readAllContacts();
        Contact.loadContacts(this);
        //contacts=filterContacts(contacts, "Luke");
        //new ContactDAOREST().readAllContacts();
        Contact currentLoggedUser=Contact.getCurrentContact();
        for (Contact c: contacts){
            if(currentLoggedUser.getId()==c.getId()) continue;
            // new MessageDAOREST().getAllMessagesToContact(c);
            //new MessageDAOREST().getFullConversationWithContact(c);

            LinearLayout horizontalLL = new LinearLayout(this);
            horizontalLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            horizontalLL.setOrientation(LinearLayout.HORIZONTAL);

            ll.addView(horizontalLL);

            // Create imageview programativally
            ImageView iv = new ImageView(getApplicationContext());
            iv.setImageDrawable(getDrawable(c.getImageId()));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(200, 300);
            iv.setLayoutParams(lp);
            horizontalLL.addView(iv);

            LinearLayout llData = new LinearLayout(this);
            llData.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            llData.setOrientation(LinearLayout.VERTICAL);
            horizontalLL.addView(llData);

            //create layout to concentrate name and date in the same space
            LinearLayout horizontalLLData1 = new LinearLayout(this);
            horizontalLLData1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            horizontalLLData1.setOrientation(LinearLayout.HORIZONTAL);

            //add name
            TextView t= new TextView(ll.getContext());
            t.setText(c.getName());
            t.setTextSize(14f);
            t.setTypeface(t.getTypeface(), Typeface.BOLD);
            t.setPadding(8,30,2, 15);
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView) v;
                    Intent myIntent = new Intent(getBaseContext(), ConversationActivity.class);
                    myIntent.putExtra("userId", c.getId());
                    myIntent.putExtra("userName", c.getName());
                    myIntent.putExtra("imageResourceId", c.getImageId());
                    startActivity(myIntent);
                }
            });
            horizontalLLData1.addView(t);

            //add date
            ArrayList<Message> messages=c.getMessages();
            SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            t= new TextView(horizontalLL.getContext());
            t.setText(dt1.format(messages!=null && messages.size()>0?messages.get(0).getDate():new Date()));
            t.setTextSize(14f);
            t.setPadding(2,30,2, 15);
            t.setGravity(Gravity.RIGHT);
            t.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            horizontalLLData1.addView(t);
            //add name and date line to llData layout
            llData.addView(horizontalLLData1);

            //add message to llData layout
            //create layout to concentrate message and share icons
            LinearLayout horizontalLLData2 = new LinearLayout(this);
            horizontalLLData2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            horizontalLLData2.setOrientation(LinearLayout.HORIZONTAL);

            t= new TextView(ll.getContext());
            t.setText(messages!=null && messages.size()>0?messages.get(0).getMessage():"No message in your history");
            t.setTextSize(15f);
            t.setPadding(8,5,2, 3);
            horizontalLLData2.addView(t);
            llData.addView(horizontalLLData2);

            iv = new ImageView(getApplicationContext());
            iv.setImageDrawable(getDrawable(android.R.drawable.ic_menu_share));
            //iv.setBackground(getDrawable(android.R.drawable.screen_background_dark));
            lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity=Gravity.RIGHT;
            iv.setLayoutParams(lp);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareTextContent(c.getMessages().get(0).getMessage(),c.getName());
                }
            });
            llData.addView(iv);



        }
    }
    public ArrayList<Contact> filterContacts(ArrayList<Contact> contacts, String filter){
        ArrayList<Contact> result= new ArrayList<Contact>();
        for(Contact c: contacts){
            if(c.getName().toLowerCase().contains(filter.toLowerCase())){
                result.add(c);
            }
        }
        return result;
    }
    public class ThreadA extends Thread
    {
        private byte parameterA;
        private byte parameterB;

        @Override
        public void run(){

            Log.i("ThreadStandard","requesting...");

            String resourceURI = "https://jsonplaceholder.typicode.com/posts";
            String httpParameters = "";//?id=0
            String formatedURL = resourceURI + httpParameters;
            URL url = null;

            try {
                url = new URL(formatedURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                con.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }

            InputStream is = null;
            try {
                is = con.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            String response = s.hasNext() ? s.next() : "";
            Log.i("ThreadStandard","requesting concluded."+response);



            response="Running thread A (A - B): A="+ parameterA+" B=" +parameterB +" Result=" +(this.parameterA-this.parameterB );
            Log.i("Thread",response);
            LinearLayout ll=(LinearLayout)findViewById(R.id.LLItems);
            TextView tv=new TextView(getBaseContext());
            tv.setText(response);
            tv.setTextColor(Color.WHITE);
            ll.addView(tv);
        }

        public byte getParameterA() {
            return parameterA;
        }

        public void setParameterA(byte parameterA) {
            this.parameterA = parameterA;
        }

        public byte getParameterB() {
            return parameterB;
        }

        public void setParameterB(byte parameterB) {
            this.parameterB = parameterB;
        }


    }

    private class ThreadGetMessages extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... integers) {
            Log.i("ASyncTask", "requesting...");

            String resourceURI = "http://192.168.0.14:3333/actors";

            String httpParameters = "";
            String formatedURL = resourceURI + httpParameters;
            URL url = null;

            try {
                url = new URL(formatedURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                con.setRequestMethod("GET");

            } catch (ProtocolException e) {
                e.printStackTrace();
            }

            InputStream is = null;
            try {
                is = con.getInputStream();
            } catch (IOException e) {
                Log.e("HTTP", "Error when connecting on "+ " with GET method.");
                e.printStackTrace();
            }
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            String response = s.hasNext() ? s.next() : "";
            Log.i("ASyncTask", "requesting concluded." + response);


            JsonParser parser = new JsonParser();
            //JsonObject obj = parser.parse(response).getAsJsonObject();
            JsonArray array = parser.parse(response).getAsJsonArray();
            JsonObject obj = (JsonObject) array.get(0);
            String firstName= obj.get("first_name").getAsString();

            Log.i("ASyncTask", "firstName>>" + firstName);

            return response;
        }

        @Override
        protected void onPostExecute (String result) {
            View v=findViewById(R.id.exportIcon);
            // Log.i("ASyncTask",result);
            LinearLayout ll=(LinearLayout)findViewById(R.id.LLItems);
            TextView tv=new TextView(getBaseContext());
            tv.setText(result);
            tv.setTextColor(Color.WHITE);
            ll.addView(tv);

        }
    }


    private class ThreadCallPostRestService extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... parameters) {
            String resourceURI = "http://192.168.0.14:3333/actors";
            String httpParameters ="";// "?firstName="+parameters[0]+"&lastName="+parameters[1];
            String formatedURL = resourceURI + httpParameters;
            try {
                URL url = new URL(formatedURL);
                HttpURLConnection con = null;
                con = (HttpURLConnection) url.openConnection();


                con.setDoOutput(true);
                
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Java client");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String urlParameters = "name=Jack&occupation=programmer";
                byte[] postData = urlParameters.getBytes();
                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.write(postData);
                }


                Log.i("ASyncTask", "Read input");
                InputStream is = null;
                is = con.getInputStream();
                java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                String response = s.hasNext() ? s.next() : "";
                Log.i("ASyncTask", "requesting POST concluded." + response);
                return response;

                    /*
                String jsonInputString = "{\"firstName\": \""+parameters[0]+"\", \"lastName\": \""+parameters[1]+"\"}";
                OutputStream os = con.getOutputStream();
                os.write(jsonInputString.getBytes());
                os.flush();
                os.close();
                con.connect();
                */
            } catch (Exception e) {
                Log.e("ASyncTask", "Error when connecting on "+ " with POST method:"+e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute (String result) {
            View v=findViewById(R.id.exportIcon);
            // Log.i("ASyncTask",result);
            LinearLayout ll=(LinearLayout)findViewById(R.id.LLItems);
            TextView tv=new TextView(getBaseContext());
            tv.setText(result);
            tv.setTextColor(Color.WHITE);
            ll.addView(tv);

        }
    }

    private class ThreadGetMessagesXML extends AsyncTask<Integer, Integer, String> {
            @Override
            protected String doInBackground(Integer... integers) {
                Log.i("AASyncTask", "requesting...");

                String resourceURI = "https://www.w3schools.com/xml/simple.xml";
                String httpParameters = "";//?id=0
                String formatedURL = resourceURI + httpParameters;
                URL url = null;

                try {
                    url = new URL(formatedURL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                HttpURLConnection con = null;
                try {
                    con = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    con.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }

                InputStream is = null;
                try {
                    is = con.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*
                java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                String response = s.hasNext() ? s.next() : "";
                Log.i("ASyncTask", "requesting concluded." + response);
                */
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
               // System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                NodeList nList = doc.getElementsByTagName("food");
                for (int i = 0; i < nList.getLength(); i++) {
                    NodeList childrenTags=nList.item(i).getChildNodes();
                    int childrenTagsCount=childrenTags.getLength();
                    Log.i("ASyncTask","Children count:"+childrenTagsCount);
                    for(byte j=0;j<childrenTagsCount;j++) {
                        if(childrenTags.item(j).getNodeName().equals("#text")) continue;
                        //Log.i("ASyncTask", "XML - text on food:" + nList.item(i).getTextContent());
                        Log.i("ASyncTask", "XML - node name:" + childrenTags.item(j).getNodeName());
                        Log.i("ASyncTask", "XML - node text content:" + childrenTags.item(j).getTextContent());
                    }
                }


                //Log.i("ASyncTask", "title>>"+title);

                return nList.item(0).getTextContent();
            }

            @Override
            protected void onPostExecute (String result) {
                View v=findViewById(R.id.exportIcon);
               // Log.i("ASyncTask",result);
                LinearLayout ll=(LinearLayout)findViewById(R.id.LLItems);
                TextView tv=new TextView(getBaseContext());
                tv.setText(result);
                tv.setTextColor(Color.WHITE);
                ll.addView(tv);

            }
        }


    private class ThreadImageFile extends AsyncTask<Integer, Integer, byte[]> {
        @Override
        protected byte[] doInBackground(Integer... integers) {
            String resourceURI = "https://static.wikia.nocookie.net/ptstarwars/images/0/01/Hansoloprofile.jpg/revision/latest/top-crop/width/360/height/450?cb=20120222133702";
            byte[] content=null;
            try {
                URL url = new URL(resourceURI);
                HttpURLConnection con = null;
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                InputStream is = con.getInputStream();
                ByteArrayOutputStream os= new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                content=os.toByteArray();

            }catch(Exception e){
                e.printStackTrace();
            }
            return content;
        }

        @Override
        protected void onPostExecute (byte[] result) {
            LinearLayout ll=(LinearLayout)findViewById(R.id.LLItems);
            ImageView iv= new ImageView(getBaseContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250, 250);
            iv.setLayoutParams( params);
            Bitmap bmp= BitmapFactory.decodeByteArray(result,0,result.length);
            iv.setImageBitmap(bmp);
            ll.addView(iv);
        }
    }



    private class ThreadReadExternalFileByURL extends AsyncTask<String, Integer, byte[]> {
        private String fileName;
        @Override
        protected byte[] doInBackground(String... params) {
            Log.i("Parameter", params[0]);
            this.fileName=params[1];
            String resourceURI = params[0];
            byte[] content=null;
            try {
                URL url = new URL(resourceURI);
                HttpURLConnection con = null;
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                InputStream is = con.getInputStream();
                ByteArrayOutputStream os= new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer, 0, buffer.length)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                content=os.toByteArray();
            }catch(Exception e){
                e.printStackTrace();
            }
            return content;
        }

        @Override
        protected void onPostExecute (byte[] result) {
            Log.i("FileDownload","Data to save"+String.valueOf(result.length));

            Log.i("FileDownload", "Directory"+ getExternalFilesDir(null).getAbsolutePath());
            File f= new File(getExternalFilesDir(null).getAbsolutePath() +this.fileName);
            //File f= new File("storage/0BED-080F/Download/" +this.fileName);


            try {

                FileOutputStream fos = new FileOutputStream(f);
                DataOutputStream dos = new DataOutputStream(fos);
                dos.write(result);
                dos.flush();
                dos.close();
                fos.flush();
                fos.close();
                Log.i("File Saved",f.getAbsolutePath());


                //Validating it was properly saved
               FileInputStream fis=new FileInputStream(f);
               DataInputStream dis= new DataInputStream(fis);
               Log.i("FileDownload", "File on disk size"+String.valueOf(dis.available()));
            }catch(Exception e){
                e.printStackTrace();
            }


        /*
           //Uri uri = Uri.parse("file:/"+f.getPath());
            //Uri uri=Uri.fromFile(f);
            String pathFile = getExternalFilesDir(null) + "/"+f.getName();
            File fP = new File(pathFile);
            Uri uri = FileProvider.getUriForFile(getBaseContext(), getBaseContext().getApplicationContext().getPackageName() + ".com.example.app_example", fP );
            String mime = getContentResolver().getType(uri);
            Log.i("Open file", "Creating Intent");
            // Open file with user selected app

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mime);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Log.i("Open file", "Starting activity");
            startActivity(intent);
         */
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return new MainMenuViewHelper().onCreateOptionsMenu(this,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new MainMenuViewHelper().onOptionsItemSelected(item,this);
        return super.onOptionsItemSelected(item);
    }


    private void shareTextContent(String content,String title){
        //1. First step - create an Intent for ACTION_SEND
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,  content); // inform the text to be shared
        sendIntent.setType("text/plain");
        //2. Create an intent to open another APP by createChooser method
        Intent shareIntent = Intent.createChooser(sendIntent, title);
        startActivity(shareIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        this.updateMessage();
    }




}