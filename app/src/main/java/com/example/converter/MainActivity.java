package com.example.converter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
    TextView Val1,Val2;
    EditText  Sum,Result;
    double Rate;
    ImageView OpenCL;
    NetWork network = new NetWork();

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

        //ActionopenCurrList = (Button)findViewById(R.id.OpenCurrList);


        network.UpdateCurrency();

        Val1 = (TextView)findViewById(R.id.Val1);

        Val2 = (TextView)findViewById(R.id.Val2);

        Sum = (EditText)findViewById(R.id.Sum);

        Result = (EditText)findViewById(R.id.Result);

        Sum.setText("0.0");

        OpenCL = (ImageView)findViewById(R.id.OpenCL);

        AddButtonListener();


        Currency AZN = new Currency("AZN","Azərbaycan manatı",1);

        Val2.setText(AZN.getShortName());


        mHandler.postDelayed(LoadDataRunnable, 2000);

//        try {




//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }


    private void Calculate(){

        double s = Double.parseDouble(Sum.getText().toString());


        double Res;

        try {
            Res = s * Rate;
        }
        catch(IllegalArgumentException e) {
            Res = 0.0;
        }

        Result.setText(Double.toString(Res));
    }



    private Runnable LoadDataRunnable = new Runnable() {
        public void run() {

            network.LoadData();



            //mHandler.postDelayed(this, 200);
        }
    };








    public void AddButtonListener(){
//        ActionopenCurrList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(".CurrencyList");
//                startActivity(intent);
//            }
//        });

        Val1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(".CurrencyList");
                intent.putExtra("SelectMode",true);
                startActivityForResult(intent,1);

            }
        });

        Sum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //double S = Double.parseDouble(Sum.getText().toString());
                if (s=="0"){
                    Sum.setText("0.0");
                    Sum.selectAll();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                Calculate();
            }
        });




        OpenCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(".CurrencyList");
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                Currency Curr = (Currency)data.getSerializableExtra("Curr");
                Val1.setText(Curr.getShortName() + " " + Curr.getFullName());
                Rate = Curr.getRate();
                Calculate();
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
//        savedInstanceState.putBoolean("MyBoolean", true);
          savedInstanceState.putDouble("Sum", Double.parseDouble(Sum.getText().toString()));
//        savedInstanceState.putInt("MyInt", 1);
        savedInstanceState.putString("Val1", Val1.getText().toString());
        savedInstanceState.putString("Val2", Val2.getText().toString());
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
//        boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
//        double myDouble = savedInstanceState.getDouble("myDouble");
//        int myInt = savedInstanceState.getInt("MyInt");
        String StringVal1 = savedInstanceState.getString("Val1");
        Val1.setText(StringVal1);
        String StringVal2 = savedInstanceState.getString("Val2");
        Val2.setText(StringVal2);

        double myDouble = savedInstanceState.getDouble("Sum");
        //Sum.setText(Double.toString(myDouble));
    }

}
