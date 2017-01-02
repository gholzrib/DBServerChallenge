package gholzrib.dbserverchallenge.core.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;

import gholzrib.dbserverchallenge.R;
import gholzrib.dbserverchallenge.core.handlers.AlarmHandler;
import gholzrib.dbserverchallenge.core.models.Restaurant;
import gholzrib.dbserverchallenge.core.models.User;
import gholzrib.dbserverchallenge.core.utils.Constants;
import gholzrib.dbserverchallenge.core.utils.PreferencesManager;
import gholzrib.dbserverchallenge.ui.activities.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Gunther Ribak on 02/01/2017.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extra = intent.getExtras();
        if (extra != null && extra.containsKey(Constants.EXTRA_ALARM_TYPE)) {
            String alarmType = extra.getString(Constants.EXTRA_ALARM_TYPE);
            switch (alarmType) {
                case Constants.EXTRA_ALARM_TYPE_FINISH_DAILY_VOTING:
                    sendNotification(context);
                    clearUserDailyVotes(context);
                    // TODO: 02/01/2017 After the API for the most voted restaurant, added it to the weekly winners array
                    Restaurant restaurant = new Restaurant();
                    addRestaurantToWeeklyWinners(context, restaurant);
                    scheduleNextDay(context);
                    break;
                case Constants.EXTRA_ALARM_TYPE_CLEAR_WEEKLY_VOTING:
                    PreferencesManager.setWeeklyWinners(context, new ArrayList<Restaurant>());
                    AlarmHandler.scheduleClearWeeklyVoting(context);
                    break;
            }
        }
    }

    private void sendNotification(Context context) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);
        builder.build().flags |= Notification.FLAG_AUTO_CANCEL;

        builder.setSmallIcon(R.drawable.ic_restaurant_black_24dp);
        builder.setContentTitle(context.getString(R.string.daily_winner));
        // TODO: 02/01/2017 After the API for the most voted restaurant, update text with it's name
        builder.setContentText("Restaurant name");

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setVibrate(new long[] { 1000, 1000 });

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(0, builder.build());
    }

    private void clearUserDailyVotes(Context context) {
        User user = PreferencesManager.getUser(context);
        user.setVotesOfTheDay(new ArrayList<String>());
        PreferencesManager.setUser(context, user);
    }

    private void addRestaurantToWeeklyWinners(Context context, Restaurant restaurant) {
        ArrayList<Restaurant> weeklyWinners = PreferencesManager.getWeeklyWinners(context);
        weeklyWinners.add(restaurant);
        PreferencesManager.setWeeklyWinners(context, weeklyWinners);
    }

    private void scheduleNextDay(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        AlarmHandler.finishDailyVoting(context, calendar.getTimeInMillis());
    }

}
