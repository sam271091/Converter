package com.example.converter;

import java.util.Calendar;

public class TimeAndDate {


    public  Long CurrentDate;
    public  Long StartOfTheDay;

    public Long getCurrentDate() {
        Long CurrentDate = Calendar.getInstance().getTimeInMillis();

        return CurrentDate;
    }

    public Long getStartOfTheDay(Long Date) {
        long secondInaDay = 60 * 60 * 24;
        long currentMilliSecond = Date / 1000;
        long StartOfTheDay= currentMilliSecond - (currentMilliSecond %secondInaDay);

        return StartOfTheDay;
    }
}
