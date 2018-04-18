package com.threemusketeers.healthmaster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

public class NotifyBreakfast extends BroadcastReceiver {

    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            int hour = sharedPreferences.getInt("BreakfastHour", 123123);
            int minute = sharedPreferences.getInt("BreakfastMinute", 123123);
            if (hour != 123123 && minute != 123123) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                    calendar.add(Calendar.DATE, 1);
                }
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(context, NotificationBreakfast.class);
                PendingIntent pi = PendingIntent.getBroadcast(context, 702, i, PendingIntent.FLAG_UPDATE_CURRENT);
                am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
            }
        }
    }
}



