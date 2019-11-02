package com.example.converter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase myDB;
    Button ActionopenCurrList;


    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBConnections dbConnections = new DBConnections();

        myDB = openOrCreateDatabase("myCurr.db", MODE_PRIVATE, null);
        dbConnections.myDB = myDB;

        //myDB.execSQL(
                //"CREATE TABLE IF NOT EXISTS Currency (ShortName VARCHAR(200), FullName VARCHAR(200))");

        dbConnections.CreateCurrencyTable();
        dbConnections.CreateRatesTable();

//        dbConnections.myDB.close();

        ActionopenCurrList = (Button)findViewById(R.id.OpenCurrList);


        AddButtonListener();

        NetWork network = new NetWork();
        network.UpdateCurrency();
        network.LoadData();

        //mHandler.postDelayed(LoadDataRunnable, 100);

//        try {




//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    private Runnable LoadDataRunnable = new Runnable() {
        public void run() {

            NetWork network = new NetWork();
            network.UpdateCurrency();

            //mHandler.postDelayed(this, 200);
        }
    };








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
