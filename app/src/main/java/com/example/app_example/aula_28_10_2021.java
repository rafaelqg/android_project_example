package com.example.P20212;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class ActivityLocalFileHandler extends AppCompatActivity {
    private ArrayList<Product> list= new ArrayList<Product>();
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_file_handler);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv=(TextView) this.findViewById(R.id.textViewOutput);

        /*

        Product productA= new Product();
        productA.setName("A");
        productA.setDescription("product A.....");
        productA.setPrice(6.5);
        list.add(productA);

        Product productB= new Product();
        productB.setName("B");
        productB.setDescription("product B.....");
        productB.setPrice(7.5);
        list.add(productB);

        Product productC= new Product();
        productC.setName("B");
        productC.setDescription("product B.....");
        productC.setPrice(7.5);
        list.add(productC);


*/
        try {
            //loadProducsXML();
           // saveProductsJSON();
           // Log.i("XML",list.toString());
            //saveProductsXML();
           // writeAndReadXML();
           // writeAndReadJSON();
           // writeAndReadXMLObject();


            MinhaThread t1= new MinhaThread();
            t1.start();

            ThreadAsyncTask  t2= new ThreadAsyncTask();
            //t2.execute("Parametro novo...");
           // ThreadImageFile t3= new ThreadImageFile();
           // t3.execute();
            // this.runOnUiThread(t1);


        }catch(Exception e){
            Log.e("HTTP", "Error HTTP");
            e.printStackTrace();
        }
    }

    class MinhaThread extends Thread{
        int n;
        @Override
        public void run(){
            try{
                Log.i("HTTP","THREAD:"+n);
                String resourceURI = "http://192.168.0.27:5500/posts.json";
                String httpParameters = "";//?id=0
                String formatedURL = resourceURI + httpParameters;
                URL url = new URL(formatedURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //https://stackoverflow.com/questions/15608499/getting-java-net-sockettimeoutexception-connection-timed-out-in-android
                con.setConnectTimeout(3000);
                con.setRequestMethod("GET");
                InputStream is = con.getInputStream();

//https://jsonplaceholder.typicode.com/posts
                java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                String response = s.hasNext() ? s.next() : "";
                tv.setText(response);

                Log.i("HTTP", response);

            }catch(Exception  e){
                Log.e("HTTP", "Error HTTP");
                e.printStackTrace();
            }
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
            }        return content;
        }

        @Override
        protected void onPostExecute (byte[] result) {
            Bitmap bmp= BitmapFactory.decodeByteArray(result,0,result.length);
            ImageView iv=(ImageView)findViewById(R.id.imageViewExternal);
            iv.setImageBitmap(bmp);
        }
    }



    private class ThreadAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.i("ASyncTask","doInBackground....." +params[0]);
            //params[0].setText("doInBackground....." );
            try{
                String resourceURI = "http://192.168.0.27:5500/posts.json";
                String httpParameters = "";//?id=0
                String formatedURL = resourceURI + httpParameters;
                URL url = new URL(formatedURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                Log.i("HTTP", String.valueOf(con.getResponseCode()));
                con.setRequestMethod("GET");
                try {
                    if(con.getResponseCode()==200) {
                        InputStream is = con.getInputStream();
                        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                        String response = s.hasNext() ? s.next() : "";
                        return response;
                    }
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }



               // Log.i("HTTP", response);

            }catch(Exception  e){
                Log.e("HTTP", "Error HTTP");
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute (String result) {
            if(result==null){
                ((TextView)findViewById(R.id.textViewOutput)).setText("Houve um erro....");
            }else{
                ((TextView)findViewById(R.id.textViewOutput)).setText(result);
            }

        }
    }


    private void loadProducsXML() throws ParserConfigurationException, FileNotFoundException {
        list= new ArrayList<Product>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        File file = new File(this.getApplicationContext().getFilesDir(), "products.xml");

        //Read XML file
        DocumentBuilderFactory dbFactoryRead = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilderRead = null;
        try {
            dBuilderRead = dbFactoryRead.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document docRead = null;
        InputStream is= new FileInputStream(file);
        try {
            doc = dBuilder.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NodeList nList = doc.getElementsByTagName("products");

        for (int i = 0; i < nList.getLength(); i++) {
            NodeList childrenTags=nList.item(i).getChildNodes();
            int childrenTagsCount=childrenTags.getLength();
            Log.i("XML","Children count:"+childrenTagsCount);
            for(byte j=0;j<childrenTagsCount;j++) {
                if(childrenTags.item(j).getNodeName().equals("#text")) continue;
                Log.i("XML", "XML - node name:" + childrenTags.item(j).getNodeName());
                Product p= new Product();
                p.setPrice(Double.valueOf(childrenTags.item(j).getAttributes().getNamedItem("price").getTextContent()) );
                p.setName(childrenTags.item(j).getAttributes().getNamedItem("name").getTextContent());
                p.setDescription(childrenTags.item(j).getAttributes().getNamedItem("description").getTextContent());
                list.add(p);
            }
        }
    }

    public void saveProductsJSON(){
        Gson gson = new GsonBuilder().create();
        String json=gson.toJson(this.list);
        Log.i("JSON",json);
        File file = new File(this.getApplicationContext().getFilesDir(), "products.json");
        try {
            Log.i("Writing on file JSON: " ,file.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.write(json.getBytes() );
            dos.close();
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private void saveProductsXML() throws TransformerException, IOException, ParserConfigurationException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        //create root node
        Element rootTag = doc.createElement("products");//root tag
        //create child node

        for(Product p: list){
            Element firstLevel = doc.createElement("product");
            firstLevel.setAttribute("name", p.getName());
            firstLevel.setAttribute("description", p.getDescription());
            firstLevel.setAttribute("price",String.valueOf( p.getPrice()));
            //firstLevel.setTextContent("Text content of a tag element");
            //append child node to root node
            rootTag.appendChild(firstLevel);

        }
        //append root with complete tree to Document
        doc.appendChild(rootTag);

        //convert XML Document to a string
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        DOMSource domSource = new DOMSource(doc);
        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        Log.i("XML",sw.toString());
        //write XML string to the disk
        File file = new File(this.getApplicationContext().getFilesDir(), "products.xml");
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);
        dos.write(sw.toString().getBytes());
        dos.close();
        fos.close();


    }

    private void writeAndReadXML(){
        File file = new File(
                this.getApplicationContext().getFilesDir(),
                "myfile.xml");
        try {
            Log.i("File",file.getAbsolutePath());
            Log.i("File","Writting into file...");

            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.write("<?xml><data>test 10.07.2021</data>".getBytes() );
            dos.close();
            fos.close();
            Log.i("File","Reading file content...");
            FileInputStream fis= new FileInputStream(file);
            DataInputStream din= new DataInputStream(fis);
            byte[] data= new byte[din.available()];
            din.readFully(data);
            String content= new String(data);
            Log.i("File","read file content:"+content);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void writeAndReadJSON(){
        Gson gson = new GsonBuilder().create();
        Person p= new Person("Frodo Bolseiro","frodo@mail.lor","004109283624");
        String json=gson.toJson(p);
        Log.i("JSON",json);
        File file = new File(this.getApplicationContext().getFilesDir(), "file_07_10_2021.json");
        try {
            Log.i("Writing on file JSON: " ,file.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.write(json.getBytes() );
            dos.close();
            fos.close();

            Log.i("Reading file JSON:" ,file.getAbsolutePath());
            FileInputStream fis= new FileInputStream(file);
            DataInputStream din= new DataInputStream(fis);
            byte[] data= new byte[din.available()];
            din.readFully(data);
            String content= new String(data);
            din.close();
            fis.close();
            Log.i("File dir: " ,file.getAbsolutePath());
            Log.i("JSON File data: ",content);
            gson = new GsonBuilder().create();
            Person person = (Person) gson.fromJson(content,Person.class);
            Log.i("File object data: ",person.getName());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void writeAndReadXMLObject()  throws Exception{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        //create root node
        Element rootTag = doc.createElement("people");//root tag
        //create child node
        Person p= new Person("Frodo Baggins".toUpperCase(),"frodo.baggins@mail.lor","004109283624");
        Element firstLevel = doc.createElement("person");
        firstLevel.setAttribute("name", p.getName());
        firstLevel.setAttribute("email", p.getEmail());
        firstLevel.setAttribute("phone", p.getPhone());
        //firstLevel.setTextContent("Text content of a tag element");
        //append child node to root node
        rootTag.appendChild(firstLevel);

        //add a second object

        p= new Person("Samwise Gamgee","samwise.gamgee@mail.lor","004109285457");
        firstLevel = doc.createElement("person");
        firstLevel.setAttribute("name", p.getName());
        firstLevel.setAttribute("email", p.getEmail());
        firstLevel.setAttribute("phone", p.getPhone());
        //append child node to root node
        rootTag.appendChild(firstLevel);

        //append root with complete tree to Document
        doc.appendChild(rootTag);

        //convert XML Document to a string
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        DOMSource domSource = new DOMSource(doc);
        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        Log.i("XML",sw.toString());
        //write XML string to the disk
        File file = new File(this.getApplicationContext().getFilesDir(), "xml_object_07_10_2021.xml");
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream dos = new DataOutputStream(fos);
        dos.write(sw.toString().getBytes());
        dos.close();
        fos.close();



        //Read XML file
        DocumentBuilderFactory dbFactoryRead = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilderRead = null;
        try {
            dBuilderRead = dbFactoryRead.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document docRead = null;
        InputStream is= new FileInputStream(file);
        try {
            doc = dBuilder.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NodeList nList = doc.getElementsByTagName("people");
        ArrayList<Person> list=new ArrayList<Person>();
        for (int i = 0; i < nList.getLength(); i++) {
            NodeList childrenTags=nList.item(i).getChildNodes();
            int childrenTagsCount=childrenTags.getLength();
            Log.i("XML","Children count:"+childrenTagsCount);
            for(byte j=0;j<childrenTagsCount;j++) {
                if(childrenTags.item(j).getNodeName().equals("#text")) continue;
                Log.i("XML", "XML - node name:" + childrenTags.item(j).getNodeName());
                Log.i("XML", "XML - node text content:" + childrenTags.item(j).getTextContent());
                Log.i("XML", "XML - node attribute read:" +childrenTags.item(j).getAttributes().getNamedItem("name").getTextContent() );
                Log.i("XML", "XML - node attribute read:" +childrenTags.item(j).getAttributes().getNamedItem("email").getTextContent() );
                Log.i("XML", "XML - node attribute read:" +childrenTags.item(j).getAttributes().getNamedItem("phone").getTextContent() );

                Person person = new Person(childrenTags.item(j).getAttributes().getNamedItem("name").getTextContent() ,childrenTags.item(j).getAttributes().getNamedItem("email").getTextContent(),childrenTags.item(j).getAttributes().getNamedItem("phone").getTextContent()  );
                list.add(person);
            }
        }
        Gson gson = new GsonBuilder().create();
        Log.i("XML", gson.toJson(list));

    }

    class Person{
        private String name;
        private String email;
        private String phone;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Person(){

        }
        public Person(String name, String email, String phone){
            this.name=name;
            this.email=email;
            this.phone=phone;
        }
    }


    class Product{
        String description;
        String name;
        double price;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "description='" + description + '\'' +
                    ", name='" + name + '\'' +
                    ", price=" + price +
                    '}';
        }
    }
}
