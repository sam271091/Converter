package com.example.converter;

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

        Cursor myCursor = myDB.rawQuery("select ShortName, FullName,Rate from Currency  WHERE ShortName = ?",new String[]{ShortName});



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



}
