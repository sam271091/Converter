package com.example.converter;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class CurrencyElement extends AppCompatActivity {

    Button buttonSave;
    TextView txShortName,txFullName,txRate;
    boolean NewObject;
    String RateOnStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_element);


        buttonSave = (Button)findViewById(R.id.buttonSave);
        txShortName =  (TextView)findViewById(R.id.txShortName);
        txRate =  (TextView)findViewById(R.id.txRate);
        txFullName = (TextView)findViewById(R.id.txFullName);

        SetOnClickListener();

        RateOnStart = txRate.getText().toString();


        Intent i = getIntent();
        Currency Curr = (Currency)i.getSerializableExtra("Object");

         NewObject = (boolean) i.getBooleanExtra("NewObject",false);



        if (Curr!=null) {
            txShortName.setText(Curr.ShortName);
            txFullName.setText(Curr.FullName);
            txRate.setText(Double.toString(Curr.Rate));
        }

    }

    public  void SetOnClickListener(){
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txShortName.getText().toString().equals("") || txFullName.getText().toString().equals("") || txRate.getText().toString().equals("")){
                    Toast.makeText(CurrencyElement.this,"Fill in  all the fields!",Toast.LENGTH_LONG).show();
                }else{
                   boolean DataExists = CheckDataExistence(txShortName.getText().toString());

                   if (RateOnStart != txRate.getText().toString()){
                       DataExists = false;
                   }

                   if (!DataExists){
                       Currency Curr = new Currency();
                       Long currentTime = Calendar.getInstance().getTimeInMillis();
                       if (NewObject==true) {
                           Curr.AddDataToDataBase(txShortName.getText().toString(), txFullName.getText().toString());
                           Curr.AddRatesDataToDataBase(txShortName.getText().toString(), txRate.getText().toString(),currentTime);
                       }else {

                           Curr.AddRatesDataToDataBase(txShortName.getText().toString(), txRate.getText().toString(),currentTime);
                       };
                       finish();
                   }else {
                       Toast.makeText(CurrencyElement.this,"Currency already exists! ",Toast.LENGTH_LONG).show();
                   }

                }
            }
        });
    }



    public  boolean CheckDataExistence(String ShortName){

        DBConnections dbConnections = new DBConnections();
        SQLiteDatabase myDB = dbConnections.myDB;

        Cursor myCursor = myDB.rawQuery("select ShortName, FullName from Currency  WHERE ShortName = ?",new String[]{ShortName});



        while(myCursor.moveToNext()) {
           // String name = myCursor.getString(0);
            //String email = myCursor.getString(1);

            return true;

        }

        return  false;
    }






}
