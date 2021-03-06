package com.example.converter;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CurrencyList extends AppCompatActivity {



     ListView ListView;
     Boolean SelectMode;
     SearchView SearchField;
     SQLiteDatabase myDB;


    DecimalFormat precision = new DecimalFormat("0.0000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Currency list");

        ListView = (ListView)findViewById(R.id.ListView);
        SearchField = (SearchView)findViewById(R.id.search_view);


        DBConnections dbConnections = new DBConnections();

        myDB = openOrCreateDatabase("myCurr.db", MODE_PRIVATE, null);
        dbConnections.myDB = myDB;

        FillTheList("");



        Intent I = getIntent();
        SelectMode =  I.getBooleanExtra("SelectMode",false);


        SetListener();



    }


    public void FillTheList(String searchVal) {

        Cursor myCursor;

        myDB.execSQL("DROP TABLE IF EXISTS TempTableRates");
        myDB.execSQL("create temporary table TempTableRates AS SELECT Rates.ShortName,Rates.Rate,Rates.Nominal  FROM Rates WHERE (Rates.ShortName,Rates.Date) IN (SELECT Rates.ShortName,max(Rates.Date) FROM Rates GROUP BY Rates.ShortName)");


        if (searchVal.equals("")) {
             myCursor = myDB.rawQuery("SELECT Currency.ShortName,Currency.FullName,TempTableRates.Rate,TempTableRates.Nominal FROM Currency LEFT JOIN TempTableRates ON Currency.ShortName = TempTableRates.ShortName ORDER BY Currency.ShortName", null);
        } else {

            myCursor = myDB.rawQuery("SELECT Currency.ShortName,Currency.FullName,TempTableRates.Rate,TempTableRates.Nominal FROM Currency LEFT JOIN TempTableRates ON Currency.ShortName = TempTableRates.ShortName WHERE FullName LIKE ? ORDER BY Currency.ShortName", new String[]{"%" + searchVal +'%'});
        }


        List(myCursor);

    }



    public  void SetListener(){

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
                Intent  intent = new Intent(".CurrencyElement");
                intent.putExtra("NewObject",true);
                startActivity(intent);;

            }
        });

        SearchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ListView.setAdapter(null);
                if (newText.length() > 2) {
                    FillTheList(newText);
                } else {
                    FillTheList("");
                }
                return false;
            }
        });

    }




    public void List(Cursor myCursor){
        HashMap<String, Currency> CurrList = new HashMap<>();

        //Currency AZN = new Currency("AZN","Azerbaijan manat",1);
        //Currency USD = new Currency("USD","US dollar",0.7);
        //Currency EUR = new Currency("EUR","EU euro",1.9);
        //Currency GBP = new Currency("GBP","Great Britain pound",2.13);
        //Currency RUB = new Currency("RUB","Russian rouble",0.027);
        //Currency JPY = new Currency("JPY","Japaneese yen",1.57);




        //Cursor myCursor = myDB.rawQuery("select ShortName, FullName from Currency", null);
        //Cursor myCursor = myDB.rawQuery("SELECT Currency.ShortName,Currency.FullName,Rates.Rate FROM Currency LEFT JOIN Rates ON Currency.ShortName = Rates.ShortName", null);
        //myDB.execSQL("DROP TABLE IF EXISTS TempTableRates");
        //myDB.execSQL("create temporary table TempTableRates AS SELECT Rates.ShortName,Rates.Rate,Rates.Nominal  FROM Rates WHERE (Rates.ShortName,Rates.Date) IN (SELECT Rates.ShortName,max(Rates.Date) FROM Rates GROUP BY Rates.ShortName)");
        //myDB.execSQL("SELECT Rates.ShortName,Rates.Rate into TempTableRates FROM Rates WHERE Rates.Date IN (SELECT max(Rates.Date) FROM Rates GROUP BY Rates.ShortName)");


        //Cursor myCursor = myDB.rawQuery("SELECT Currency.ShortName,Currency.FullName,TempTableRates.Rate,TempTableRates.Nominal FROM Currency LEFT JOIN TempTableRates ON Currency.ShortName = TempTableRates.ShortName ORDER BY Currency.ShortName", null);
        //myDB.execSQL("DROP TABLE IF EXISTS TempTableRates");

        while(myCursor.moveToNext()) {
            String ShortName = myCursor.getString(0);
            String FullName = myCursor.getString(1);
            String    Rate = myCursor.getString(2);
            String    Nominal = myCursor.getString(3);

            Currency currency = new Currency(ShortName,FullName,Double.parseDouble(Rate),Double.parseDouble(Nominal));

            CurrList.put(currency.getShortName(), currency);
        }


        TreeMap<String, Currency> sorted = new TreeMap<>();
        sorted.putAll(CurrList);

        //CurrList.put(AZN.getShortName(), AZN);
        //CurrList.put(USD.getShortName(), USD);
        //CurrList.put(EUR.getShortName(), EUR);
        //CurrList.put(GBP.getShortName(), GBP);
        //CurrList.put(RUB.getShortName(), RUB);
        //CurrList.put(JPY.getShortName(), JPY);

        List<HashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[]{"Flag","First Line", "Second Line","Third Line"},
                new int[]{R.id.flagPic,R.id.text1, R.id.text_2,R.id.text3});


        Resources resources = getResources();

        Iterator it = sorted.entrySet().iterator();
        while (it.hasNext())
        {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry)it.next();

            final int resourceId = resources.getIdentifier( "ic_" + pair.getKey().toString().toLowerCase() , "drawable",
                    getPackageName());

            if (resourceId!=0) {
                resultsMap.put("Flag",String.valueOf(resourceId));
            }


            resultsMap.put("First Line", pair.getKey().toString());

            Currency Cur = (Currency) pair.getValue();

            resultsMap.put("Second Line", Cur.getFullName().toString());
            resultsMap.put("Third Line", precision.format(Cur.getRate()));
            listItems.add(resultsMap);
        }



        ListView.setAdapter(adapter);


        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap val = (HashMap)ListView.getItemAtPosition(position);
                //Toast.makeText(CurrencyList.this,"Позиция: " + position + ",Значение: " + val.get("First Line") ,Toast.LENGTH_SHORT).show();

                Currency currency = new Currency();

                //Currency currencyGetCurrencyByShortName(val.get("First Line"));
                Currency Curr = currency.GetCurrencyByShortName((String) val.get("First Line"));

               if (!SelectMode){
                   Intent  intent = new Intent(".CurrencyElement");
                   intent.putExtra("Object",Curr);
                   startActivity(intent);
               } else {


                   if (!Curr.getShortName().equals("AZN") ){
                       Intent intent = new Intent();
                       intent.putExtra("Curr", Curr);
                       setResult(RESULT_OK, intent);
                       finish();
                   } else {
                       Toast.makeText(CurrencyList.this,"The currency can not be selected!",Toast.LENGTH_SHORT).show();
                   }


               }

            }
        });




    }

}
