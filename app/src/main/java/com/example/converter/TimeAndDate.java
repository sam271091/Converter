package com.example.converter;

import java.util.Calendar;

public class TimeAndDate {


    public  Long CurrentDate;
    public  Long StartOfTheDay;

    public Long getCurrentDate() {
        //Long CurrentDate = Calendar.getInstance().getTimeInMillis();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTimeInMillis();
    }

    public Long getStartOfTheDay(Long Date) {
//        long secondInaDay = 60 * 60 * 24;
//        long currentMilliSecond = Date / 1000;
//        long StartOfTheDay= currentMilliSecond - (currentMilliSecond %secondInaDay);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Date);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

//        if (c.getTimeInMillis() == Date) {
//           return Date;
//        }



        return c.getTimeInMillis();
    }
}
