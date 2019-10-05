package com.example.converter;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

public class DBConnections extends AppCompatActivity {

    public static SQLiteDatabase myDB;


    public SQLiteDatabase Connection(){

        //myDB = openOrCreateDatabase("my.db", MODE_PRIVATE, null);

        return  myDB;
    }

    public void CreateCurrencyTable(){
        myDB.execSQL(
                "CREATE TABLE IF NOT EXISTS Currency (ShortName VARCHAR(200), FullName VARCHAR(200))");
    }

    public void CreateRatesTable(){
        myDB.execSQL(
                "CREATE TABLE IF NOT EXISTS Rates (ShortName VARCHAR(200), Rate VARCHAR(200),Date INTEGER)");
    }

}
