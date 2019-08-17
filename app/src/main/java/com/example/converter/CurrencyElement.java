package com.example.converter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CurrencyElement extends AppCompatActivity {

    Button buttonSave;
    TextView txShortName,txFullName,txRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_element);


        buttonSave = (Button)findViewById(R.id.buttonSave);
        txShortName =  (TextView)findViewById(R.id.txShortName);
        txRate =  (TextView)findViewById(R.id.txRate);
        txFullName = (TextView)findViewById(R.id.txFullName);

        SetOnClickListener();

    }

    public  void SetOnClickListener(){
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txShortName.getText().toString().equals("") || txFullName.getText().toString().equals("") || txRate.getText().toString().equals("")){
                    Toast.makeText(CurrencyElement.this,"Fill in  all the fields!",Toast.LENGTH_LONG).show();
                }else{
                   boolean DataExists = CheckDataExistence();
                   if (!DataExists){
                       AddDataToDataBase();
                   }else {
                       Toast.makeText(CurrencyElement.this,"Currency already exists! ",Toast.LENGTH_LONG).show();
                   }

                }
            }
        });
    }



    public  boolean CheckDataExistence(){

        DBConnections dbConnections = new DBConnections();
        SQLiteDatabase myDB = dbConnections.myDB;

        Cursor myCursor = myDB.rawQuery("select ShortName, FullName from Currency ", null);

        while(myCursor.moveToNext()) {
           // String name = myCursor.getString(0);
            //String email = myCursor.getString(1);


        }

        return  false;
    }


    public  void AddDataToDataBase(){

        DBConnections dbConnections = new DBConnections();
        SQLiteDatabase myDB = dbConnections.myDB;

        ContentValues row = new ContentValues();
        row.put("ShortName", txShortName.getText().toString());
        row.put("FullName",txFullName.getText().toString());
        row.put("Rate",txRate.getText().toString());

        myDB.insert("Currency", null, row);


    }



}
