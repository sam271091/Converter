package com.example.converter;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.util.HashMap;

public class Currency   implements Serializable{

    public  String ShortName;
    public  String FullName;
    public  double Rate;

    public double getNominal() {
        return Nominal;
    }

    public void setNominal(double nominal) {
        Nominal = nominal;
    }

    public  double Nominal;



    public double getRate() {
        return Rate;
    }

    public void setRate(double rate) {
        Rate = rate;
    }





    public  Currency(){

    }

    public  Currency(String Name,String FName,double ValRate,double Nominal){

        setShortName(Name);
        setFullName(FName);
        setRate(ValRate);
        setNominal(Nominal);
    }


    public  String getShortName() {
        return ShortName;
    }



    public  void setShortName(String shortName) {
        ShortName = shortName;
    }

    public  String getFullName() {



        String Pres = GetCurrPresentation();

        if (Pres==null){
            return FullName;
        }

        return Pres;

    }


    public String  GetCurrPresentation(){


        HashMap<String, String> Presentation = new HashMap<>();

        Presentation.put("BYN","Belarusian ruble");
        Presentation.put("AZN","Azerbaijani manat");
        Presentation.put("EUR","Euro");
        Presentation.put("CAD","Canadian dollar");
        Presentation.put("KGS","Kyrgyz Som");
        Presentation.put("ARS","Argentine Peso");
        Presentation.put("BRL","Brazilian real");
        Presentation.put("NZD","New Zealand Dollar");
        Presentation.put("AUD","Australian dollar");
        Presentation.put("SEK","Swedish Krona");

        Presentation.put("LBP","Lebanese pound");
        Presentation.put("EGP","Egyptian pound");
        Presentation.put("JPY","Japanese yen");
        Presentation.put("KZT","Kazakhstan tenge");
        Presentation.put("HKD","Hong Kong Dollar");
        Presentation.put("GBP","British pound sterling");
        Presentation.put("CZK","Czech Koruna");
        Presentation.put("MYR","Malaysian Ringgit");
        Presentation.put("DKK","Danish krone");
        Presentation.put("GEL","Georgian lari");

        Presentation.put("INR","Indian rupees");
        Presentation.put("TWD","Taiwanese dollar");
        Presentation.put("AED","UAE Dirham");
        Presentation.put("PLN","Polish zloty");
        Presentation.put("ZAR","South african rand");
        Presentation.put("CNY","Chinese yuan");
        Presentation.put("CHF","Swiss frank");
        Presentation.put("TRY","Turkish lira");
        Presentation.put("BYR","Belarusian ruble");
        Presentation.put("UAH","Ukrainian hryvnia");

        Presentation.put("ILS","Israeli Shekel");
        Presentation.put("KWD","Kuwaiti dinar");
        Presentation.put("NOK","Norwegian Krone");
        Presentation.put("TJS","Tajik somoni");
        Presentation.put("SAR","Saudi riyal");
        Presentation.put("RUB","Russian ruble");
        Presentation.put("TMT","Turkmen manat");
        Presentation.put("IRR","Iranian Rial");
        Presentation.put("SGD","Singapore Dollar");
        Presentation.put("MDL","Moldovan leu");


        Presentation.put("UZS","Uzbek Sum");
        Presentation.put("KRW","South Korean Won");
        Presentation.put("CLP","Chilean Peso");
        Presentation.put("USD","U.S. dollar");
        Presentation.put("IDR","Indonesian Rupee");
        Presentation.put("MXN","Mexican peso");
        Presentation.put("SDR","DR (Special IMF Drawing Rights)");



        return Presentation.get(ShortName);
    }



    public  void setFullName(String fullName) {
        FullName = fullName;
    }



    public   Currency GetCurrencyByShortName(String ShortName){

        DBConnections dbConnections = new DBConnections();
        SQLiteDatabase myDB = dbConnections.myDB;

        //Cursor myCursor = myDB.rawQuery("SELECT Currency.ShortName,Currency.FullName,Rates.Rate FROM Currency LEFT JOIN Rates ON Currency.ShortName = Rates.ShortName  WHERE Currency.ShortName = ?",new String[]{ShortName});

        myDB.execSQL("DROP TABLE IF EXISTS TempTableRates");
        myDB.execSQL("create temporary table TempTableRates AS SELECT Rates.ShortName,Rates.Rate,Rates.Nominal  FROM Rates WHERE (Rates.ShortName,Rates.Date) IN (SELECT Rates.ShortName,max(Rates.Date) FROM Rates GROUP BY Rates.ShortName)");
        //myDB.execSQL("SELECT Rates.ShortName,Rates.Rate into TempTableRates FROM Rates WHERE Rates.Date IN (SELECT max(Rates.Date) FROM Rates GROUP BY Rates.ShortName)");


        Cursor myCursor = myDB.rawQuery("SELECT  Currency.ShortName,Currency.FullName,TempTableRates.Rate,TempTableRates.Nominal FROM Currency LEFT JOIN TempTableRates ON Currency.ShortName = TempTableRates.ShortName WHERE Currency.ShortName = ? LIMIT 10", new String[]{ShortName});




        while(myCursor.moveToNext()) {
             String SName = myCursor.getString(0);
             String FullName = myCursor.getString(1);
             String Rate = myCursor.getString(2);
             String Nominal = myCursor.getString(3);

            Currency currency = new Currency(SName,FullName,Double.parseDouble(Rate),Double.parseDouble(Nominal));

            return currency;

        }

        Currency currency = new Currency();

        return currency;
    }


    public Cursor GetRatesAndDatesByCurr(String ShortName){
        DBConnections dbConnections = new DBConnections();
        SQLiteDatabase myDB = dbConnections.myDB;

        Cursor myCursor = myDB.rawQuery("SELECT Rates.ShortName,Rates.Rate,Rates.Date from Rates WHERE ShortName = ? LIMIT 4", new String[]{ShortName});

        return myCursor;

    }



    public  void AddDataToDataBase(String ShortName,String FullName){

        DBConnections dbConnections = new DBConnections();
        SQLiteDatabase myDB = dbConnections.myDB;

        ContentValues row = new ContentValues();
        row.put("ShortName", ShortName);
        row.put("FullName",FullName);
        //row.put("Rate",txRate.getText().toString());

        myDB.insert("Currency", null, row);


    }

    public  void AddRatesDataToDataBase(String ShortName,String Rate,Long datetime,String Nominal){

        DBConnections dbConnections = new DBConnections();
        SQLiteDatabase myDB = dbConnections.myDB;

        ContentValues row = new ContentValues();
        row.put("ShortName", ShortName);
        row.put("Rate",Rate);
        row.put("Nominal",Nominal);
        row.put("Date",datetime);

        myDB.insert("Rates", null, row);


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


    public  boolean CheckRatesDataExistence(String ShortName,String Rate,Long Date){

        DBConnections dbConnections = new DBConnections();
        SQLiteDatabase myDB = dbConnections.myDB;

        Cursor myCursor = myDB.rawQuery("select ShortName, Rate,Date,Nominal  from Rates  WHERE ShortName = ? and " +
                "Rate = ? and " +
                "Date = ?",new String[]{ShortName,Rate, String.valueOf(Date)});



        while(myCursor.moveToNext()) {
            // String name = myCursor.getString(0);
            //String email = myCursor.getString(1);

            return true;

        }

        return  false;
    }





}
