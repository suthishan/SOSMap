package com.example.roydana.map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DBnum extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String Databasename = "Alertnum";

    // User table name
    private static final String Tablename = "mobnum";

    private static final String COLUMN_USER_Num1 = "number1";
    private static final String COLUMN_USER_Num2 = "number2";


    private String CREATE_USER_TABLE = "CREATE TABLE " + Tablename + "("
            + COLUMN_USER_Num1 + " TEXT,"
            + COLUMN_USER_Num2 + " TEXT" + ")";
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + Tablename;

    public DBnum(Context context) {
        super(context, Databasename, null, DATABASE_VERSION);
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

    public boolean insertnum(String num1, String num2) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_Num1, num1);
        values.put(COLUMN_USER_Num2, num2);

        // Inserting Row
        long i = db.insert(Tablename, null, values);
        if (i == -1) {
            return false;
        } else {
            return true;
        }

    }
    public boolean updatenum(String num1, String num2){
        int id=1;
       SQLiteDatabase db=this.getWritableDatabase();
      ContentValues values=new ContentValues();
       values.put(COLUMN_USER_Num1,num1);
       values.put(COLUMN_USER_Num2,num2);
        long im=db.update(Tablename, values,"ROWID=1",null);
      db.close();
        if (im == -1) {
            return false;
        } else {
            return true;
        }

        //  String upd="update"+Tablename+
        //"SET"+
          //      COLUMN_USER_Num1 +"="+ num1 +","+
            //    COLUMN_USER_Num2 +"="+ num2 +";";
        //db.execSQL(upd);
        //db.close();

    }
    public Cursor getnum(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor num= db.rawQuery("select*from mobnum",null);
        return num;
    }
}
