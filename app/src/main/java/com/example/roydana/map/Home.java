package com.example.roydana.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Home extends AppCompatActivity {
    DBnum ndb;
    EditText num1, num2;
    FloatingActionButton reg;
    TextView tvname,t,skp;
    DBnum mydb;
    ImageButton upd;
    String edn1, edn2;
    String name;
    DatabaseHelper databaseHelper;
    User user;
    PrefManager preferenceData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
      //  ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
       // ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},1);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        preferenceData = new PrefManager(this);

        num1=(EditText)findViewById(R.id.m1);
        num2=(EditText)findViewById(R.id.m2);
        reg=(FloatingActionButton) findViewById(R.id.rmob);
        mydb = new DBnum(this);
        databaseHelper = new DatabaseHelper(this);
        user = new User();
        skp=(TextView) findViewById(R.id.skp);
        skp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mp=new Intent(Home.this,MapsActivity.class);
                startActivity(mp);
            }
        });
        Cursor number = mydb.getnum();
        if (number.getCount() == 0) {
            Toast.makeText(this, "nothingFound", Toast.LENGTH_SHORT).show();

        }

        if (number.moveToFirst()) {

            edn1 = number.getString(0);
            edn2 = number.getString(1);

        }
        num1.setText(edn1);
        num2.setText(edn2);
        num1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (num1.getRight() - num1.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        Toast.makeText(Home.this, "check", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);

                        startActivityForResult(intent, 0);
                        return true;
                    }
                }
                return false;
            }
        });
        num2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (num2.getRight() - num2.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        Toast.makeText(Home.this, "check", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);

                        startActivityForResult(intent, 1);
                        return true;
                    }
                }
                return false;
            }
        });
        ndb=new DBnum(this);
        tvname=(TextView) findViewById(R.id.tvname);
        t=(TextView) findViewById(R.id.tvwish);

        final SharedPreferences sharedpreferences = getSharedPreferences(Register.mypref, Context.MODE_PRIVATE);
        name=sharedpreferences.getString("Name",null);
        tvname.setText(name);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             
                boolean is=ndb.updatenum(num1.getText().toString(),num2.getText().toString());
                if(is){
                    user.setNumber1(num1.getText().toString());
                    user.setNumber2(num2.getText().toString());
                    databaseHelper.updateUser(user);
                    Toast.makeText(Home.this, user.getEmail(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(Home.this, user.getNumber1(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(Home.this, user.getNumber2(), Toast.LENGTH_SHORT).show();
                    preferenceData.setLogin(true);
                    Toast.makeText(Home.this, " update number", Toast.LENGTH_SHORT).show();
                    Intent mi=new Intent (Home.this,MapsActivity.class);
                    startActivity(mi);
                }
                else{
                    Toast.makeText(Home.this, "please Try Again", Toast.LENGTH_SHORT).show();
                }



            }
        });

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("HH");

        int temp = Integer.parseInt(sdf.format(calendar.getTimeInMillis()));

        if(temp <12)
        {
            t.setText("Good morning");
        }
        else if(temp>12&&temp<16)
        {
            t.setText("Good afternoon");
        }
        else if (temp>16){
            t.setText("Good Evening");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 0: if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Cursor cursor = managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();

                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                num1.setText(number);
            }
                break;
            case 1: if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Cursor cursor = managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();

                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                num2.setText(number);
            }
                break;
        }

    }

}
