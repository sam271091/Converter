package com.example.converter;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CurrencyList extends AppCompatActivity {



     ListView ListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView = (ListView)findViewById(R.id.ListView);


        List();




        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
                Intent  intent = new Intent(".CurrencyElement");
                startActivity(intent);;

            }
        });
    }




    public void List(){
        HashMap<String, Currency> CurrList = new HashMap<>();

        //Currency AZN = new Currency("AZN","Azerbaijan manat",1);
        //Currency USD = new Currency("USD","US dollar",0.7);
        //Currency EUR = new Currency("EUR","EU euro",1.9);
        //Currency GBP = new Currency("GBP","Great Britain pound",2.13);
        //Currency RUB = new Currency("RUB","Russian rouble",0.027);
        //Currency JPY = new Currency("JPY","Japaneese yen",1.57);


        DBConnections dbConnections = new DBConnections();

        SQLiteDatabase myDB = openOrCreateDatabase("myCurr.db", MODE_PRIVATE, null);
        dbConnections.myDB = myDB;

        Cursor myCursor = myDB.rawQuery("select ShortName, FullName,Rate from Currency", null);

        while(myCursor.moveToNext()) {
            String ShortName = myCursor.getString(0);
            String FullName = myCursor.getString(1);
            String    Rate = myCursor.getString(2);

            Currency currency = new Currency(ShortName,FullName,Double.parseDouble(Rate));

            CurrList.put(currency.getShortName(), currency);
        }


        //CurrList.put(AZN.getShortName(), AZN);
        //CurrList.put(USD.getShortName(), USD);
        //CurrList.put(EUR.getShortName(), EUR);
        //CurrList.put(GBP.getShortName(), GBP);
        //CurrList.put(RUB.getShortName(), RUB);
        //CurrList.put(JPY.getShortName(), JPY);

        List<HashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[]{"First Line", "Second Line","Third Line"},
                new int[]{R.id.text1, R.id.text_2,R.id.text3});


        Iterator it = CurrList.entrySet().iterator();
        while (it.hasNext())
        {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultsMap.put("First Line", pair.getKey().toString());

            Currency Cur = (Currency) pair.getValue();

            resultsMap.put("Second Line", Cur.getFullName().toString());
            resultsMap.put("Third Line", Double.toString(Cur.getRate()));
            listItems.add(resultsMap);
        }

        ListView.setAdapter(adapter);


        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap val = (HashMap)ListView.getItemAtPosition(position);
                Toast.makeText(CurrencyList.this,"Позиция: " + position + ",Значение: " + val.get("First Line") ,Toast.LENGTH_SHORT).show();

                Currency currency = new Currency();

                //Currency currencyGetCurrencyByShortName(val.get("First Line"));
                Currency Curr = currency.GetCurrencyByShortName((String) val.get("First Line"));

                Intent  intent = new Intent(".CurrencyElement");
                intent.putExtra("Object",Curr);
                startActivity(intent);
            }
        });




    }

}
