package com.example.app_example;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import DataAccessObject.DataSource;
import DataAccessObject.REST.MessageDAOREST;
import DataAccessObject.iMessageDAO;
import entities.Contact;
import entities.Message;
import viewhelper.ActivityLocationChangeHandler;
import viewhelper.GPSLocation;

public class ConversationActivity extends AppCompatActivity implements ActivityLocationChangeHandler {
    private Contact sendTo;
    //attribute for in memory photo
    static final int REQUEST_IMAGE_CAPTURE = 1;
    VideoView lastVideo=null;
    //attributes for in disk photo
    String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    File photoFile = null;
    //current user location
    private double latitude=0;
    private double longitude=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);


        String userName = this.getIntent().getStringExtra("userName");
        int imageResourceId= this.getIntent().getIntExtra("imageResourceId",0);
        int userId= this.getIntent().getIntExtra("userId",0);
        this.sendTo= new Contact(userName,imageResourceId);
        this.sendTo.setId(userId);
        View v=findViewById(R.id.textViewConversationWith);
        TextView tv=(TextView)v;
        tv.setText("Conversation with ".concat(userName));
        //created contact variable to be utilized as parameters to other methods


       Bitmap bitmap= BitmapFactory.decodeResource(getResources(), imageResourceId);
       ImageView iv= (ImageView) findViewById(R.id.imageViewContact);
       iv.setImageBitmap(bitmap);


        DataSource ds=new DataSource(this);
        iMessageDAO dao= ds.getDaoMessage();
        ArrayList<Message> messages=dao.getFullConversationWithContact(sendTo);

        LinearLayout ll=(LinearLayout)findViewById(R.id.llMessages);
        this.showMessages( ll,messages,  this.sendTo);


        //add action to export button
        ImageView ivExport=findViewById(R.id.exportIcon);
        ivExport.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  saveConversation();
              }
          });

        findViewById(R.id.imageViewPhotoSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        findViewById(R.id.imageViewPhotoSendFull).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntentFullSize();
            }
        });

        findViewById(R.id.imageViewShareLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLocation();
            }
        });


        findViewById(R.id.iconVideoPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastVideo!=null) {
                    Log.i("VideoView","Current position:"+String.valueOf(lastVideo.getCurrentPosition()));
                    if(lastVideo.isPlaying()){
                        Log.i("VideoView","Pausing...");
                        lastVideo.pause();
                    }else{
                        Log.i("VideoView","Playing...");
                        lastVideo.start();

                    }

                    Log.i("VideoView","Duration:"+String.valueOf(lastVideo.getDuration()));
                }
            }
        });

        findViewById(R.id.imageViewPicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });

        findViewById(R.id.imageViewVideoPicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickVideoFromGallery();
            }
        });




        findViewById(R.id.imageViewVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });

       //add action for send a new Message
       Button bt=findViewById(R.id.buttonSendMessage);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View v1=findViewById(R.id.editTextTextMultiLineMessage);
                EditText et=(EditText)v1;
                String messageText=et.getText().toString();
                Message m= new Message(messageText, new Date());
                Contact sentBy=Contact.getCurrentContact();

                DataSource ds= new DataSource(v.getContext());
                //iMessageDAO messageDAO=ds.getDaoMessage();
                iMessageDAO messageDAO=new MessageDAOREST();
                boolean result=messageDAO.saveMessage(m, sentBy,  sendTo);
                if(result){
                    Toast.makeText(v.getContext(), "Message sent!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(v.getContext(), "Message not sent...", Toast.LENGTH_LONG).show();
                }

                //update messages list
                ds=new DataSource(v.getContext());
                iMessageDAO dao= ds.getDaoMessage();
                ArrayList<Message> messages=dao.getFullConversationWithContact(sendTo);
                LinearLayout ll=(LinearLayout)findViewById(R.id.llMessages);
                showMessages( ll,messages,  sendTo);
            }
        });

        GPSLocation.initService(this);
    }

    public void showMessages(LinearLayout ll,ArrayList<Message> messages, Contact sendTo){
        ll.removeAllViews();
        for (Message m: messages) {
            LinearLayout horizontalLL = new LinearLayout(this);
            horizontalLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            horizontalLL.setOrientation(LinearLayout.VERTICAL);
            ll.addView(horizontalLL);

            TextView t= new TextView(ll.getContext());
            t.setText(m.getMessage());
            t.setTextSize(14f);
            t.setTypeface(t.getTypeface(), Typeface.BOLD);
            t.setPadding(8,30,2, 15);

            horizontalLL.addView(t);
            if (m.getSender().equals(sendTo)) {
                t.setGravity(Gravity.RIGHT);
            }
            t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


            //add date
            SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            t= new TextView(horizontalLL.getContext());

            t.setText(dt1.format(m.getDate()));
            t.setTextSize(14f);
            t.setPadding(2,30,2, 15);
            if ( m.getSender().equals(sendTo)) {
                t.setGravity(Gravity.RIGHT);
            }
            t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            horizontalLL.addView(t);

        }
    }

    public void saveConversation(){

        DataSource ds=new DataSource(this);
        iMessageDAO dao= ds.getDaoMessage();
        ArrayList<Message> messages=dao.getFullConversationWithContact(sendTo);

        Gson gson = new GsonBuilder().create();
        String json=gson.toJson(messages);

        File file = new File(this.getApplicationContext().getFilesDir(), "conversation.json");
        try {
            if (!file.exists()) {
                Log.i("Writing on file: " ,file.getAbsolutePath());
                FileOutputStream fos = new FileOutputStream(file);
                DataOutputStream dos = new DataOutputStream(fos);
                dos.write(json.getBytes() );
                dos.close();
                fos.close();
            }
            Log.i("Reading file:" ,file.getAbsolutePath());
            FileInputStream fis= new FileInputStream(file);
            DataInputStream din= new DataInputStream(fis);
            byte[] data= new byte[din.available()];
            din.readFully(data);
            String content= new String(data);
            din.close();
            fis.close();
            Log.i("File dir: " ,file.getAbsolutePath());
           Log.i("File data: ",content);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        Bitmap imageBitmap=null;
        ImageView iv= new ImageView(this);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(photoFile !=null){
                imageBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                imageBitmap= this.convertToGrayScale(imageBitmap);
                iv.setImageBitmap(imageBitmap);


                //convert  bitmap to byte[]
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                //send image to server
                new MessageDAOREST().sendMultimedia(byteArray, Contact.getCurrentContact(),  sendTo,this.latitude,this.longitude);


                //delete image from local disk
                photoFile.delete();
                photoFile=null;

            }else {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imageBitmap= this.convertToBlackAndWhite(imageBitmap);
                //convert  bitmap to byte[]
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();


                //send image to server
                new MessageDAOREST().sendMultimedia(byteArray, Contact.getCurrentContact(),  sendTo,this.latitude,this.longitude);


            }
            iv.setImageBitmap(imageBitmap);
            addViewToConversationInMemory(iv,500,500);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
        }

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            //import android.widget.VideoView;
            VideoView vv= new VideoView(this);
            vv.setVideoURI(videoUri);
            //add controlers
            MediaController mediaController = new MediaController(this);
            vv.setMediaController(mediaController);
            if(videoFileURI!=null){
                Log.i("VIDEO_RECORD", videoFileURI.getPath());
                File f= new File(videoFileURI.getPath());
                Log.i("VIDEO_RECORD", f.getName());
            }
            addViewToConversationInMemory(vv,500,500);




             }

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                imageBitmap= BitmapFactory.decodeStream(inputStream);
                //imageBitmap= this.convertToGrayScale(imageBitmap);
                //imageBitmap= this.convertToBlackAndWhite(imageBitmap);

                iv.setImageBitmap(imageBitmap);
                addViewToConversationInMemory(iv,500,500);
            }catch(Exception e){
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }

        if (requestCode == PICK_VIDEO && resultCode == RESULT_OK) {
            try {
                Uri mVideoURI = data.getData();

                VideoView vv= new VideoView(this);
                //MediaController mediaController = new MediaController(this);
                //vv.setMediaController(mediaController);

                vv.setVideoURI(mVideoURI);

                addViewToConversationInMemory(vv,500,500);
                vv.start();
                vv.setOnCompletionListener ( new MediaPlayer.OnCompletionListener (){
                    @Override
                    public void onCompletion (MediaPlayer mediaPlayer){
                            vv.start ();
                            Log.i("VideoView","Restarting....");
                        }
                });


                lastVideo=vv;
            }catch(Exception e){
                e.printStackTrace();
            }
       }


    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        Log.i("Image", currentPhotoPath );
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE );
        }
    }

    private void dispatchTakePictureIntentFullSize() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = null;
            try {
                photoFile = createImageFile();

                if (photoFile != null) {
                    Uri photoURI= FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider",  photoFile );
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }

            } catch (IOException ex) { }
        }
    }


    private void galleryAddPic() throws  Exception {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }



    static final int REQUEST_VIDEO_CAPTURE = 3;//custom code
    Uri videoFileURI=null;
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        //store video in a mp4 file
        videoFileURI = this.getNewVideoOutputMediaFile();  // create a file to save the video in specific folder (this works for video only)
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoFileURI);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high


        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    public Uri getNewVideoOutputMediaFile(){

           // File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "SMW_VIDEO");
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath());
            //File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.
            // Create the storage directory if it does not exist
            if (! mediaStorageDir.exists()) {
                if (! mediaStorageDir.mkdirs()) {

                    Log.d("VIDEO_RECORD", mediaStorageDir.getAbsolutePath());
                    Log.d("VIDEO_RECORD", "failed to create directory");
                    return null;
                }
            }
            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator +"VID_"+ timeStamp + ".mp4");
            return Uri.fromFile(mediaFile);

    }

    //https://stackoverflow.com/questions/5309190/android-pick-images-from-gallery
    static final int PICK_IMAGE = 4;//custom code
    private void pickImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    static final int PICK_VIDEO = 5;//custom code
    private void pickVideoFromGallery(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select video"), PICK_VIDEO);
    }

    private void shareLocation(){
        WebView wv= new WebView(this);
        //"https://www.google.com/maps/@-27.6777248,-48.475575,13.92z";
        GPSLocation.initService(this);
        String url="https://www.google.com/maps/@"+this.latitude+","+this.longitude+",11.48z";
        //String url="https://www.google.com/maps/@-27.6974835,-48.4735526,11.48z";
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true); // allows JS execution
        wv.loadUrl(url);

        this.addViewToConversationInMemory(wv,1000,1600);
    }


    private void addViewToConversationInMemory(View view, int width, int height){
        LinearLayout ll=(LinearLayout)findViewById(R.id.llMessages);
        LinearLayout horizontalLL = new LinearLayout(this);
        horizontalLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        horizontalLL.setOrientation(LinearLayout.VERTICAL);
        ll.addView(horizontalLL);

        //add date
        SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        TextView t= new TextView(horizontalLL.getContext());

        t.setText(dt1.format(new Date()));
        t.setTextSize(14f);
        t.setPadding(2,30,2, 15);
        t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        horizontalLL.addView(t);

        //add view
        view.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        horizontalLL.addView(view);


    }

    @Override
    public void updateLocation(double latitude, double longitude) {
        String latitudeFormatted=String.format("%.4f", latitude);
        String longitudeFormatted=String.format("%.4f", longitude);
        ((TextView)findViewById(R.id.textViewLatitude)).setText(String.valueOf(latitudeFormatted));
        ((TextView)findViewById(R.id.textViewLongitude)).setText(String.valueOf(longitudeFormatted));
        this.latitude=latitude;
        this.longitude=longitude;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GPSLocation.configService(this);
                } else {
                    Toast.makeText(this, "No permission for GPS acessing", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Bitmap convertToGrayScale(Bitmap image){
        Bitmap bitmap = image.copy(image.getConfig(), true);
        for(int i=0;i<bitmap.getWidth();i++){
            for(int j=0;j<bitmap.getHeight();j++){
                   Color c= bitmap.getColor(i,j);
                   float green=c.green();
                   float red=c.red();
                   float blue=c.blue();
                   float grayScale=(green+red+blue)/3;
                    int newColor=Color.argb(c.alpha(),grayScale,grayScale,grayScale);
                   bitmap.setPixel(i,j,newColor);
            }
        }
    return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Bitmap convertToBlackAndWhite(Bitmap image){
        Bitmap bitmap = image.copy(image.getConfig(), true);
        for(int i=0;i<bitmap.getWidth();i++){
            for(int j=0;j<bitmap.getHeight();j++){
                Color c= bitmap.getColor(i,j);
                //Log.i("Pixel color",String.valueOf(c.toArgb()));
                //32 bits for each pixel.
                Log.i("Pixel",String.valueOf(c.toArgb()));
                int color=c.toArgb()>-10000000?Color.WHITE: Color.BLACK;
                // int newColor=Color.rgb(grayScale, grayScale,grayScale);
                bitmap.setPixel(i,j,color);
            }
        }
        return bitmap;
    }
}