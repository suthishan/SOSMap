package com.example.roydana.map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends AppCompatActivity {
ImageButton back;
DatabaseHelper mydb;
Button create;
EditText name,mobile,email,password;

    SharedPreferences sharedPreferences;
    TextView ln;
    static String mypref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mydb=new DatabaseHelper(this);
        name=(EditText)findViewById(R.id.na);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.psw);
        mobile=(EditText)findViewById(R.id.mob);
        ln=(TextView)findViewById(R.id.ln);
        ln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent li=new Intent(Register.this,MainActivity.class);
                startActivity(li);
            }
        });
        back=(ImageButton)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bi=new Intent(Register.this,MainActivity.class);
                startActivity(bi);
                finish();
            }
        });
        create=(Button)findViewById(R.id.create);
        sharedPreferences = getSharedPreferences(mypref, Context.MODE_PRIVATE);
        Boolean f= sharedPreferences.getBoolean("username",false);

        create.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String n=name.getText().toString();
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("Name",n);
                editor.commit();
               boolean re= mydb.insertUser(name.getText().toString(),
                        mobile.getText().toString(),
                        email.getText().toString(),
                        password.getText().toString());
               if(re){
                   Toast.makeText(Register.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                   Intent bi=new Intent(Register.this,Alertnumreg.class);
                   startActivity(bi);


                   editor.putBoolean("username",true);
                   editor.commit();
               }
               else{
                   Toast.makeText(Register.this, "please Try Again", Toast.LENGTH_SHORT).show();
               }


            }
        });
    }
}
