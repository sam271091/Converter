package com.example.converter;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;




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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase myDB;
//    Button ActionopenCurrList;
    TextView Val1,Val2;
    EditText  Sum,Result;
    double Rate,Nominal;
    ImageView OpenCL,Switcher;
    boolean switcherActive;
    GraphView graph;
    Currency AZN = new Currency("AZN","Azərbaycan manatı",1,1);

    Currency CurrentCurrency;

    NetWork network = new NetWork();

    private Handler mHandler = new Handler();

    LineGraphSeries<DataPoint> series;


    DecimalFormat precision = new DecimalFormat("#,##0.0000");

    SimpleDateFormat sdf = new SimpleDateFormat(" dd.MM.YYYY");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBConnections dbConnections = new DBConnections();

        myDB = openOrCreateDatabase("myCurr.db", MODE_PRIVATE, null);
        dbConnections.myDB = myDB;

        //precision.setGroupingUsed(false);

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

        Sum.setText("0.0000");
        Result.setText("0.0000");

        OpenCL = (ImageView)findViewById(R.id.OpenCL);

        Switcher = (ImageView)findViewById(R.id.Switcher);

        graph = (GraphView) findViewById(R.id.graph);

        graph.removeAllSeries();

        AddButtonListener();




        Val2.setText(AZN.getShortName());


        mHandler.postDelayed(LoadDataRunnable, 2000);


        SetClickable();



//        MakeGraph();

//        try {




//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }




    private void MakeGraph(){

//
//
//
         graph.removeAllSeries();

         if (CurrentCurrency != null) {
             Cursor myCursor = CurrentCurrency.GetRatesAndDatesByCurr(CurrentCurrency.getShortName());

             CreateGraph(myCursor);
         }



    }


    private void CreateGraph(Cursor myCursor){
        DataPoint[] dp = new DataPoint[myCursor.getCount()];

        TimeAndDate timeAndDate = new TimeAndDate();

        int i = 0;

//        TimeAndDate TD = new TimeAndDate();

        Date FirstDate = new Date();

        Date LastDate = new Date();


        while(myCursor.moveToNext()) {
            String SName = myCursor.getString(0);
            String Rate = myCursor.getString(1);
            long DateInt = myCursor.getLong(2);


            DateInt = timeAndDate.getStartOfTheDay(DateInt);


            Date date = new Date(DateInt);


//


            dp[i] = new DataPoint(date.getTime(), Double.parseDouble(Rate));

            i = i + 1;

            if (i==1){
                FirstDate = date;
            }else if(i==myCursor.getCount()) {
                LastDate = date;
            }


        }




//




        series = new LineGraphSeries<>(dp);


        series.setAnimated(true);
        series.setColor(Color.GREEN);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(12);
        series.setThickness(8);



        graph.addSeries(series);




        //graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return sdf.format(value);
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });



        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space




        graph.getViewport().setMinX(FirstDate.getTime());
        graph.getViewport().setMaxX(LastDate.getTime());
        graph.getViewport().setXAxisBoundsManual(false);


        graph.getViewport().setScalable(true);

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

        graph.setTitle("The dynamics of changes for the period: " +
                dateFormat.format(FirstDate.getTime()) + "-" + dateFormat.format(LastDate.getTime()) );


        graph.getGridLabelRenderer().setHorizontalLabelsAngle(1);






        graph.getGridLabelRenderer().setHumanRounding(true);
    }

    private void Calculate(){

        String SumValue = Sum.getText().toString();

        if (SumValue.equals("")){
            SumValue = "0.0";
        }

        double s = Double.parseDouble(SumValue);


        double Res;

        try {
            if (Nominal !=0){
                if (!switcherActive) {
                    Res = s * Rate/Nominal;
                } else Res = s / Rate*Nominal;
            } else Res = 0;


        }
        catch(IllegalArgumentException e) {
            Res = 0.0;
        }



        Result.setText(precision.format(Res));
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

        Val2.setOnClickListener(new View.OnClickListener() {
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

        Switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switcherActive){
                    switcherActive = false;
                    Val1.setText(Val2.getText());
                    Val2.setText(AZN.getShortName());
                } else {
                    switcherActive = true;
                    Val2.setText(Val1.getText());
                    Val1.setText(AZN.getShortName());
                }

                SetClickable();

                Calculate();
            }
        });

    }


    public void SetClickable(){
        Val1.setClickable(!switcherActive);
        Val2.setClickable(switcherActive);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                Currency Curr = (Currency)data.getSerializableExtra("Curr");
                if (!switcherActive){
                    Val1.setText(Curr.getShortName() + " " + Curr.getFullName());
                } else Val2.setText(Curr.getShortName() + " " + Curr.getFullName());

                CurrentCurrency = Curr;

                Rate = Curr.getRate();
                Nominal = Curr.getNominal();
                Calculate();
                MakeGraph();
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
       savedInstanceState.putBoolean("switcherActive", switcherActive);
        savedInstanceState.putDouble("Rate", Rate);
        savedInstanceState.putDouble("Nominal", Nominal);
        savedInstanceState.putDouble("Sum", Double.parseDouble(Sum.getText().toString()));
//        savedInstanceState.putInt("MyInt", 1);
        savedInstanceState.putString("Val1", Val1.getText().toString());
        savedInstanceState.putString("Val2", Val2.getText().toString());
        savedInstanceState.putSerializable("CurrentCurrency",CurrentCurrency);

        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
          switcherActive = savedInstanceState.getBoolean("switcherActive");
//        double myDouble = savedInstanceState.getDouble("myDouble");
//        int myInt = savedInstanceState.getInt("MyInt");
        Rate = savedInstanceState.getDouble("Rate");
        Nominal = savedInstanceState.getDouble("Nominal");
        String StringVal1 = savedInstanceState.getString("Val1");
        Val1.setText(StringVal1);
        String StringVal2 = savedInstanceState.getString("Val2");
        Val2.setText(StringVal2);

        CurrentCurrency = (Currency)savedInstanceState.getSerializable("CurrentCurrency");

        double myDouble = savedInstanceState.getDouble("Sum");
        Sum.setText(Double.toString(myDouble));

        SetClickable();

        MakeGraph();
    }

}
