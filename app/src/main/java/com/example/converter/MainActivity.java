package com.example.converter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase myDB;
    Button ActionopenCurrList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBConnections dbConnections = new DBConnections();

        myDB = openOrCreateDatabase("my.db", MODE_PRIVATE, null);
        dbConnections.myDB = myDB;

        //myDB.execSQL(
                //"CREATE TABLE IF NOT EXISTS Currency (ShortName VARCHAR(200), FullName VARCHAR(200))");

        dbConnections.CreateCurrencyTable();
        ActionopenCurrList = (Button)findViewById(R.id.OpenCurrList);

        AddButtonListener();
    }



    public void AddButtonListener(){
        ActionopenCurrList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(".CurrencyList");
                startActivity(intent);
            }
        });
    }

}
