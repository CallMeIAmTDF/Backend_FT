package com.example.familytree.utils;

import java.util.Date;
public class DateUtil {
    public static Date getCurrentDay(){
        java.util.Date currentDate = new java.util.Date();
        return new Date(currentDate.getTime());
    }
}
