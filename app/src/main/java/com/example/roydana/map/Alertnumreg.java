package com.example.roydana.map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

public class Alertnumreg extends AppCompatActivity {
    DBnum ndb;
EditText num1, num2;
Button reg;
TextView tvname,t;

ImageButton upd;
String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertnumreg);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        num1=(EditText)findViewById(R.id.m1);
        num2=(EditText)findViewById(R.id.m2);
        reg=(Button) findViewById(R.id.rmob);


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
                        Toast.makeText(Alertnumreg.this, "check", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Alertnumreg.this, "check", Toast.LENGTH_SHORT).show();
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

                if (num1.getText().toString().equals("")|| num2.getText().toString().equals("")) {
                   num1.requestFocus();
                   num2.requestFocus();
                    Toast.makeText(getApplicationContext(), "Please Enter MobileNo", Toast.LENGTH_SHORT).show();
                }
                else {
                    boolean re = ndb.insertnum(num1.getText().toString(), num2.getText().toString());
                    if (re) {
                        Toast.makeText(Alertnumreg.this, " Successfully added number", Toast.LENGTH_SHORT).show();
                        Intent mi = new Intent(Alertnumreg.this, MapsActivity.class);
                        startActivity(mi);
                    } else {
                        Toast.makeText(Alertnumreg.this, "please Try Again", Toast.LENGTH_SHORT).show();
                    }

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
