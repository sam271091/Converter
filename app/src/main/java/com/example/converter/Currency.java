package com.example.converter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;

public class Currency  implements Serializable{

    public  String ShortName;
    public  String FullName;
    public  double Rate;

    public double getRate() {
        return Rate;
    }

    public void setRate(double rate) {
        Rate = rate;
    }




    public  Currency(){

    }

    public  Currency(String Name,String FName,double ValRate){

        setShortName(Name);
        setFullName(FName);
        setRate(ValRate);
    }


    public  String getShortName() {
        return ShortName;
    }

    public  void setShortName(String shortName) {
        ShortName = shortName;
    }

    public  String getFullName() {
        return FullName;
    }

    public  void setFullName(String fullName) {
        FullName = fullName;
    }



    public   Currency GetCurrencyByShortName(String ShortName){

        DBConnections dbConnections = new DBConnections();
        SQLiteDatabase myDB = dbConnections.myDB;

        //Cursor myCursor = myDB.rawQuery("SELECT Currency.ShortName,Currency.FullName,Rates.Rate FROM Currency LEFT JOIN Rates ON Currency.ShortName = Rates.ShortName  WHERE Currency.ShortName = ?",new String[]{ShortName});

        myDB.execSQL("DROP TABLE IF EXISTS TempTableRates");
        myDB.execSQL("create temporary table TempTableRates AS SELECT Rates.ShortName,Rates.Rate  FROM Rates WHERE (Rates.ShortName,Rates.Date) IN (SELECT Rates.ShortName,max(Rates.Date) FROM Rates GROUP BY Rates.ShortName)");
        //myDB.execSQL("SELECT Rates.ShortName,Rates.Rate into TempTableRates FROM Rates WHERE Rates.Date IN (SELECT max(Rates.Date) FROM Rates GROUP BY Rates.ShortName)");


        Cursor myCursor = myDB.rawQuery("SELECT Currency.ShortName,Currency.FullName,TempTableRates.Rate FROM Currency LEFT JOIN TempTableRates ON Currency.ShortName = TempTableRates.ShortName WHERE Currency.ShortName = ?", new String[]{ShortName});



        while(myCursor.moveToNext()) {
             String SName = myCursor.getString(0);
             String FullName = myCursor.getString(1);
             String Rate = myCursor.getString(2);

            Currency currency = new Currency(SName,FullName,Double.parseDouble(Rate));

            return currency;

        }

        Currency currency = new Currency();

        return currency;
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

    public  void AddRatesDataToDataBase(String ShortName,String Rate,Long datetime){

        DBConnections dbConnections = new DBConnections();
        SQLiteDatabase myDB = dbConnections.myDB;

        ContentValues row = new ContentValues();
        row.put("ShortName", ShortName);
        row.put("Rate",Rate);
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

        Cursor myCursor = myDB.rawQuery("select ShortName, Rate,Date  from Rates  WHERE ShortName = ? and " +
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
