package me.joaosilva22.rocketlaunchtracker.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import me.joaosilva22.rocketlaunchtracker.managers.NotificationPublisher;

public class NotificationHelper {

    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
    }

    public Notification getNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(android.support.design.R.drawable.notification_template_icon_bg);
        builder.setContentTitle(title);
        builder.setContentText(content);

        return builder.build();
    }

    public void scheduleNotification(Notification notification, int id, Long time) {
        Intent intent = new Intent(context, NotificationPublisher.class);

        intent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        intent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, time,
                PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT));

        Toast.makeText(context, "Notification scheduled", Toast.LENGTH_SHORT).show();
    }

    public void unscheduleNotification(int id) {
        Intent intent = new Intent(context, NotificationPublisher.class);
        PendingIntent pending =
                PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pending);

        Toast.makeText(context, "Notification canceled", Toast.LENGTH_SHORT).show();
    }
}
