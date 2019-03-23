package com.wave.digitalidentity.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wave.digitalidentity.Activities.MainActivity;
import com.wave.digitalidentity.Models.AppNotification;

import java.util.Map;

import letswave.co.in.wave.R;

public class AppNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData()!=null) getImage(remoteMessage);
    }

    private void getImage(final RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        AppNotification appNotification = new AppNotification(data.get("title"), data.get("content"), data.get("imageUrl"));
        sendNotification(appNotification);
    }


    private void sendNotification(AppNotification appNotification) {
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo1);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "101";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_MAX);
            notificationChannel.setDescription("App Notifications");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{500});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(bitmap)
                .setContentTitle(appNotification.getTitle())
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentText(appNotification.getContent())
                .setContentIntent(pendingIntent)
                .setStyle(style)
                .setLargeIcon(bitmap)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX);
        if (appNotification.getImageUrl()==null) notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(bitmap)
                .setContentTitle(appNotification.getTitle())
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentText(appNotification.getContent())
                .setContentIntent(pendingIntent)
                .setStyle(style)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX);

        notificationManager.notify(1, notificationBuilder.build());
    }

}
