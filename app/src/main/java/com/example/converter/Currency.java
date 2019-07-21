package com.example.converter;

public class Currency {

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



}
