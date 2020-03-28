package com.example.roydana.map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String Databasename= "Info";

    // User table name
    private static final String Tablename = "users";

    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_mob = "mobile";
    private static final String COLUMN_USER_email = "email";
    private static final String COLUMN_USER_password = "password";
    private static final String COLUMN_Num_1 = "number1";
    private static final String COLUMN_Num_2 = "number2";


    private String CREATE_USER_TABLE = "CREATE TABLE " + Tablename + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_mob + " TEXT," + COLUMN_USER_email + " TEXT," + COLUMN_USER_password + " TEXT,"
            + COLUMN_Num_1 + " TEXT," + COLUMN_Num_2 + " TEXT"+")";
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
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_email, user.getEmail());
        values.put(COLUMN_USER_password, user.getPassword());
        values.put(COLUMN_USER_mob, user.getMobile());

        // Inserting Row
        db.insert(Tablename, null, values);
        db.close();
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
       db.close();
       if(i==-1){
           return false;
       }
       else{
           return true;
       }
    }

    public List<User> getAllUser() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_NAME,
                COLUMN_USER_mob,
                COLUMN_USER_email,
                COLUMN_USER_password
        };
        // sorting orders
        String sortOrder =
                COLUMN_USER_NAME + " ASC";
        List<User> userList = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(Tablename, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_email)));
                user.setMobile(cursor.getString(cursor.getColumnIndex(COLUMN_USER_mob)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_password)));
                // Adding user record to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return userList;
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME,user.getName());
        values.put(COLUMN_USER_mob, user.getMobile());
        values.put(COLUMN_USER_email, user.getEmail());
        values.put(COLUMN_USER_password,user.getPassword());
        values.put(COLUMN_Num_1, user.getNumber1());
        values.put(COLUMN_Num_2,user.getNumber2());

        // updating row
        db.update(Tablename, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(Tablename, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public boolean checkUser(String email) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_email + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(Tablename, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    public boolean checkUserPass(String email, String password) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_email + " = ?" + " AND " + COLUMN_USER_password + " = ?";

        // selection arguments
        String[] selectionArgs = {email, password};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(Tablename, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }


}
