package com.example.app_example;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import viewhelper.MainMenuViewHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i("Life cycle:", "onCreate ...");

        final String NETWORK_ISSUE="Network Issue";
        final String PERMISSION_ISSUE="Permission Issue";
        final String USER_LOG="User log";
        Log.v(USER_LOG, "Login button pressed at:"+ (new Date().toString()));
        Log.d(USER_LOG, "Debug: Error coded returned from the authentication service.");
        Log.i(PERMISSION_ISSUE, "Info: You are not authorized to exclude items created by another users.");
        Log.w(PERMISSION_ISSUE, "Warn: This action may take too long.");
        Log.e(NETWORK_ISSUE, "Error: not possible to connect on endpoint A");
        Log.e(NETWORK_ISSUE, "Error: not possible to connect on endpoint B");



        findViewById(R.id.buttonSearch).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        EditText field = (EditText) findViewById(R.id.editTextSearchText);
                        Editable edt= field.getText();
                        String textInput= edt.toString();
                        Log.i("On Click event:", textInput);
                    }
                }
        );

       int[] ids = {R.id.textViewContact_1,R.id.textViewContact_2,R.id.textViewContact_3,R.id.textViewContact_4,R.id.textViewContact_5};
       int[] avatarsIds= {R.drawable.luke,R.drawable.obi_wan,R.drawable.yoda, R.drawable.darth_vader, R.drawable.darth_maul};
       for(byte i=0;i<ids.length;i++) {
           int id=ids[i];
           int avatarId=avatarsIds[i];
           findViewById(id).setOnClickListener(
                   new View.OnClickListener() {
                       public void onClick(View v) {
                           TextView tv = (TextView) v;
                           Intent myIntent = new Intent(getBaseContext(), ConversationActivity.class);
                           myIntent.putExtra("userName", tv.getText().toString());
                            myIntent.putExtra("imageResourceId", avatarId);
                           startActivity(myIntent);
                       }
                   }
           );
       }



        findViewById(R.id.shareIcon1).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        shareTextContent( ((TextView)findViewById(R.id.textView14)).getText().toString(),"Choose an APP to share:");
                    }
                }
        );

        Bundle bundle = new Bundle();
        bundle.putString("edttext", "From Activity");
        // set Fragmentclass Arguments
        ActionItemsFragment fragobj = new ActionItemsFragment();
        fragobj.setArguments(bundle);
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Life cycle:", "onPause ...");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Life cycle:", "onStop ...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Life cycle:", "onDestroy ...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Life cycle:", "onResume ...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Life cycle:", "onStart ...");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("Life cycle:", "onRestart ...");
    }
}