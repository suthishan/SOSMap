package com.example.roydana.map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roydana.map.helpers.InputValidation;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = Register.this;
    ImageButton back;
    DatabaseHelper mydb;

    AppCompatButton create;

    SharedPreferences sharedPreferences;
    TextView ln;
    static String mypref;

    private InputValidation inputValidation;
    private User user;
    TextInputLayout l2,l3,l4,l5;
    TextInputEditText email,psw,na,mob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();
        initListeners();
        initObjects();
    }

    private void initViews() {
        l2 = (TextInputLayout) findViewById(R.id.l2);
        l3 = (TextInputLayout) findViewById(R.id.l3);
        l4 = (TextInputLayout) findViewById(R.id.l4);
        l5 = (TextInputLayout) findViewById(R.id.l5);
        na=(TextInputEditText)findViewById(R.id.na);
        email=(TextInputEditText)findViewById(R.id.email);
        psw=(TextInputEditText)findViewById(R.id.psw);
        mob=(TextInputEditText)findViewById(R.id.mob);
        ln=(TextView)findViewById(R.id.ln);
        back=(ImageButton)findViewById(R.id.back);
        create=(AppCompatButton)findViewById(R.id.create);
        sharedPreferences = getSharedPreferences(mypref, Context.MODE_PRIVATE);
    }

    private void initListeners() {
        ln.setOnClickListener(this);
        back.setOnClickListener(this);
        create.setOnClickListener(this);
    }

    private void initObjects() {
        inputValidation = new InputValidation(this);
        mydb = new DatabaseHelper(this);
        user = new User();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ln:
                movetoLogin();
                break;

            case R.id.back:
                finishback();
                break;

            case R.id.create:
                postDataToSQLite();
                break;

        }
    }

    private void finishback(){
                Intent bi=new Intent(Register.this,MainActivity.class);
                startActivity(bi);
                finish();
    }

    private void movetoLogin() {
        Intent li=new Intent(Register.this,MainActivity.class);
        startActivity(li);
    }

        private void postDataToSQLite() {
        if (!inputValidation.isInputEditTextFilled(email, l2, getString(R.string.error_message_email))) {
           return;
        }
        if (!inputValidation.isInputEditTextEmail(email, l2, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(psw, l3, getString(R.string.error_message_password))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(na, l4, getString(R.string.error_message_name))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(mob, l5, getString(R.string.error_message_mobile))) {
            return;
        }


        if (!mydb.checkUser(email.getText().toString().trim())) {

            user.setName(na.getText().toString().trim());
            user.setEmail(email.getText().toString().trim());
            user.setPassword(psw.getText().toString().trim());
            user.setMobile(mob.getText().toString().trim());

            mydb.addUser(user);
            Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
            Intent li=new Intent(Register.this,MainActivity.class);
            startActivity(li);
            emptyInputEditText();
        } else {
            Toast.makeText(Register.this, "Registration Failed.. Same User already exist", Toast.LENGTH_SHORT).show();
        }

    }

    private void emptyInputEditText() {
        na.setText(null);
        email.setText(null);
        psw.setText(null);
        mob.setText(null);
    }

}
