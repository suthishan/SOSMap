package com.example.roydana.map;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String Databasename= "Info";

    // User table name
    private static final String Tablename = "users";

    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_mob = "mobile";
    private static final String COLUMN_USER_email = "email";
    private static final String COLUMN_USER_password = "password";


    private String CREATE_USER_TABLE = "CREATE TABLE " + Tablename + "("
             + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_mob + " TEXT," + COLUMN_USER_email + " TEXT," + COLUMN_USER_password + " TEXT" +")";
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + Tablename;
    public DatabaseHelper( Context context) {
        super(context,Databasename,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);

        // Create tables again
        onCreate(db);
    }
    public boolean insertUser(String name, String mobile, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME,name);
        values.put(COLUMN_USER_mob, mobile);
        values.put(COLUMN_USER_email, email);
        values.put(COLUMN_USER_password,password);

        // Inserting Row
       long i= db.insert(Tablename, null, values);
       if(i==-1){
           return false;
       }
       else{
           return true;
       }


    }
}
