package com.example.app_example;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class InputsExample extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputs_example);

        findViewById(R.id.buttonConfirm).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        printFieldValue(R.id.editTextTextPersonName,"Nome");
                        printFieldValue(R.id.editTextTextPassword,"Password");
                        printFieldValue(R.id.editTextNumber,"Number");
                        printFieldValue(R.id.editTextNumberDecimal,"Number decimal");
                        printFieldValue(R.id.editTextDate,"Date");
                        printFieldValue(R.id.editTextTime,"Time");
                        printFieldValue(R.id.editTextTextEmailAddress,"e-mail");
                        printFieldValue(R.id.editTextPhone,"Phone");
                        printFieldValue(R.id.editTextTextMultiLine,"Multiline");
                        printFieldValue(R.id.editTextAutoComplete,"Auto complete");
                    }
                }
        );
        TextView tv=(TextView)findViewById(R.id.textViewConfiguration);
        tv.setTextColor(Color.rgb(0,102,102));

        /*
        myOnClickEvent obj=new myOnClickEvent();
        findViewById(R.id.editTextTextPersonName).setOnKeyListener(obj);

 /*
        v View: The view the key has been dispatched to.
        keyCode	int: The code for the physical key that was pressed
        event	KeyEvent: The KeyEvent object containing full information about the event.
        Returns
        boolean	True if the listener has consumed the event, false otherwise.

         */

        findViewById(R.id.editTextTextPersonName).setOnKeyListener(
                new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        Log.i("Key code:", String.valueOf(keyCode));
                        Log.i("Key char:", String.valueOf((char)event.getUnicodeChar()));
                        Log.i("ALT:", String.valueOf(event.isAltPressed()));
                        Log.i("CTRL:", String.valueOf(event.isCtrlPressed()));
                        Log.i("Shift:", String.valueOf( event.isShiftPressed()));
                        Log.i("NumberLock:", String.valueOf( event.isNumLockOn()));

                        if(event.getAction()== KeyEvent.ACTION_UP){
                            Log.i("My logic on keyup:",String.valueOf( event.getAction()));
                        }

                        if(event.getAction()== KeyEvent.ACTION_DOWN){
                            Log.i("My logic on keydown:", String.valueOf(event.getAction()));
                        }


                        Log.i("Event:", event.toString());

                        final Button button = (Button) findViewById(R.id.buttonConfirm);
                        final EditText et=(EditText)findViewById(R.id.editTextTextPersonName);
                        if(!et.getText().toString().equals("")) {
                            button.setEnabled(true);
                        }else{
                            button.setEnabled(false);
                        }
                        return true;
                    }
                });

        final Button button = (Button) findViewById(R.id.buttonConfirm);
        button.setEnabled(false);

        //List view code
        //set itens on list view
        ListView listview = (ListView) findViewById(R.id.listViewField);
        List<String> listElementsArrayList = new ArrayList<String>();
        listElementsArrayList.add("Portugues");
        listElementsArrayList.add("English");
        listElementsArrayList.add("France");
        listElementsArrayList.add("Deutsch");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listElementsArrayList);
        listview.setAdapter(adapter);
        //set onclick listener
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = parent.getItemAtPosition(position);
                String str = (String)o; //As you are using Default String Adapter
               // Log.i("ListView selection: ", str);
                final String msg="The language of the entire APP is going to translated to "+str+".";

/*
                new AlertDialog.Builder (view.getContext()).setTitle("Attention")
                        .setMessage(msg)
                        .setNeutralButton("Close", null)
                        .show();
*/

                new AlertDialog.Builder(view.getContext()).setTitle("Confirm language change?")
                        .setMessage("Do you really what to change the APP language to "+str+"?")
                        .setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.i("Dialog yes", "Language change confirmed.");
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("Dialog yes", "Language change canceled.");
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();

             //   Toast.makeText(view.getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void printFieldValue(int fieldId, String fieldName){
        EditText field = (EditText) findViewById(fieldId);
        Editable edt= field.getText();
        String textInput= edt.toString();
        Log.i("Field value",fieldName+": "+ textInput);
    }
}