package com.example;


import java.util.Calendar;
import java.util.Date;

public class MyClass {
    public static void main(String[] args){
        System.out.println("Hello World");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        System.out.println(calendar.getWeekYear());
    }
}
