package com.example.roydana.map;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.roydana.map.helpers.InputValidation;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission_group.CAMERA;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = MainActivity.this;

    FloatingActionButton fab;
    Button login;
    DatabaseHelper databaseHelper;
    private InputValidation inputValidation;

    public static final int RequestPermissionCode = 7;

    private TextInputLayout l2,l3;

    private TextInputEditText email, psw;
    PrefManager preferenceData;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        user = new User();

        initViews();
        initListeners();
        initObjects();

    }

    private void initViews() {

        l2 = (TextInputLayout) findViewById(R.id.l2);
        l3 = (TextInputLayout) findViewById(R.id.l3);
        email = (TextInputEditText) findViewById(R.id.email);
        psw = (TextInputEditText) findViewById(R.id.psw);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        login = (Button) findViewById(R.id.login);

        if (CheckingPermissionIsEnabledOrNot()) {
            Toast.makeText(MainActivity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
        }

        // If, If permission is not enabled then else condition will execute.
        else {

            //Calling method to enable permission.
            RequestMultiplePermission();

        }

    }

    private void initListeners() {
        login.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
        inputValidation = new InputValidation(activity);
        preferenceData = new PrefManager(activity);
    }




    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        CALL_PHONE,

                        SEND_SMS,
                        ACCESS_FINE_LOCATION

                }, RequestPermissionCode);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean callphone = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean SendSMSPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean loc=grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (callphone &&  SendSMSPermission) {

                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    // Checking permission is enabled or not using function starts from here.
    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int third = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);


        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                third == PackageManager.PERMISSION_GRANTED;


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                verifyFromSQLite();
                break;
            case R.id.fab:
                // Navigate to RegisterActivity
                Intent intentRegister = new Intent(getApplicationContext(), Register.class);
                startActivity(intentRegister);
                break;
        }

    }

    private void verifyFromSQLite() {

        if (!inputValidation.isInputEditTextFilled(email, l2, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(email, l2, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(psw, l3, getString(R.string.error_message_password))) {
            return;
        }

        if (databaseHelper.checkUserPass(email.getText().toString().trim()
                , psw.getText().toString().trim())) {

            preferenceData.setLogin(true);
            preferenceData.setMainScreenOpen(0);
            Toast.makeText(MainActivity.this, user.getEmail(), Toast.LENGTH_LONG).show();
            Intent accountsIntent = new Intent(activity, Home.class);
            accountsIntent.putExtra("EMAIL", email.getText().toString().trim());
            emptyInputEditText();
            startActivity(accountsIntent);


        } else {
            Toast.makeText(MainActivity.this, "No User Found..!", Toast.LENGTH_SHORT).show();
        }



    }

    private void emptyInputEditText() {
        email.setText(null);
        psw.setText(null);
    }
}
