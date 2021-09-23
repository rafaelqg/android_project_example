package com.example.app_example;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static android.graphics.BlendMode.COLOR;

public class Atividade10903 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade10903);

        findViewById(R.id.buttonIMC).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        EditText pesoEt=(EditText)findViewById(R.id.editTextTextPeso);
                        EditText alturaEt=(EditText)findViewById(R.id.editTextTextAltura);

                        try {
                            double peso = Double.parseDouble(pesoEt.getText().toString());
                            double altura = Double.parseDouble(alturaEt.getText().toString());
                            double imc=peso/(altura*altura);

                            TextView tv= findViewById(R.id.textViewIMC);
                            tv.setText(String.valueOf(imc));

                            if(imc>25){
                                tv.setTextColor(Color.RED);
                            }else if(imc <20){
                                tv.setTextColor(Color.MAGENTA);
                            }else if(imc>=20 && imc<=25){
                                tv.setTextColor(Color.BLUE);
                            }

                        }catch(NumberFormatException e){
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
}