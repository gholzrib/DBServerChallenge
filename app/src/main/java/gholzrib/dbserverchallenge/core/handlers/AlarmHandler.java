package gholzrib.dbserverchallenge.core.handlers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import gholzrib.dbserverchallenge.core.services.AlarmBroadcastReceiver;
import gholzrib.dbserverchallenge.core.utils.Constants;

/**
 * Created by Gunther Ribak on 02/01/2017.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class AlarmHandler {

    public static void finishDailyVoting(Context context, long timeInMills) {
        setAlarm(context, timeInMills, Constants.EXTRA_ALARM_TYPE_FINISH_DAILY_VOTING);
    }

    /**
     * Alarm to clear the weekly winners every sunday
     *
     * @param context
     */
    public static void scheduleClearWeeklyVoting(Context context) {
        Calendar calendar = Calendar.getInstance();

        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        setAlarm(context, calendar.getTimeInMillis(), Constants.EXTRA_ALARM_TYPE_CLEAR_WEEKLY_VOTING);
    }

    private static void setAlarm(Context context, long timeInMills, String alarmType) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(Constants.EXTRA_ALARM_TYPE, alarmType);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, timeInMills, pi);
    }

}
