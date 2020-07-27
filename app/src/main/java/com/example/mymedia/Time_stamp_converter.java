package com.example.mymedia;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Time_stamp_converter {

    public static String time_Converter(Timestamp timestamp)
    {
        Date date=timestamp.toDate();
        long time_diff=(System.currentTimeMillis()-date.getTime());
        String date_str="",date_str_temp=date.toString();

        date_str+=date_str_temp.substring(8,10);
        date_str+=" ";
        date_str+=date_str_temp.substring(4,7)+" ";
        date_str+=date_str_temp.substring(30,34);

        Log.d("time", "time_Converter: "+date_str);

        long days=TimeUnit.MILLISECONDS.toDays(time_diff);
        if(days>30)
        {
            return date_str;
        }
        String ans="";
        long hour=0,min=0,sec=0,time_print = -1;
        String time_unit = null;
        sec= TimeUnit.MILLISECONDS.toSeconds(time_diff);
        min=TimeUnit.MILLISECONDS.toMinutes(time_diff);
        hour=TimeUnit.MILLISECONDS.toHours(time_diff);
        if(days==0)
        {
            if(hour==0)
            {
                if(min==0)
                {
                    if(sec==0)
                    {
                        ans="Just Now";
                    }
                    else
                    {
                        ans=Long.toString(sec)+" seconds ago";
                    }
                }
                else
                {
                    ans=Long.toString(min)+" minutes ago";
                }
            }
            else
            {
                ans=Long.toString(hour)+" hours ago";
            }

        }
        else
        {
            ans=Long.toString(days)+" days ago";
        }

    return ans;
    }
}
