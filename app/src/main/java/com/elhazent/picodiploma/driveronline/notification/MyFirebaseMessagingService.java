package com.elhazent.picodiploma.driveronline.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.elhazent.picodiploma.driveronline.HistoryActivity;
import com.elhazent.picodiploma.driveronline.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String data;
    private String pesandarifirebase;
    private Map<String, String> datafirebase;
    private String datafirebasee;
    private JsonElement datadata;
    private JSONObject array;
    private String username;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            datafirebasee = remoteMessage.getData().get("datax");

            try {
                JSONObject object = new JSONObject(datafirebasee);

                array = object.getJSONObject("datax").getJSONObject("data");
                username = array.getString("user_nama");
                String hp = array.getString("user_hp");
                String catatan = array.getString("booking_catatan");
                Log.d("testnih", username);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


//  notif(pesandarifirebase, bodydarifirebase,a,b);

        showNotification(getApplicationContext(), username);
//            Log.d("dataku", pesandarifirebase);


    }

    public void showNotification(Context context, String data) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        Uri soundUri = Uri.parse("android.resource://" +
                getApplicationContext().getPackageName() + "/" + R.raw.notif);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ada order masuk")
                .setSound(soundUri)
                .setContentText(data);
        Intent intent = new Intent(context, HistoryActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }

    private void notif(String pesandarifirebase) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent ii = new Intent(getApplicationContext(), HistoryActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(pesandarifirebase);
        bigText.setBigContentTitle("Title");
        bigText.setSummaryText("Text in detail");
        Uri soundUri = Uri.parse("android.resource://" +
                getApplicationContext().getPackageName() + "/" + R.raw.notif);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Your Title");
        mBuilder.setContentText("Orderan Masuk");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setSound(soundUri);
        mBuilder.setVibrate(new long[]{500, 500, 500, 500});
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (soundUri != null) {
                // Changing Default mode of notification
                mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                // Creating Channel
                NotificationChannel notificationChannel = new NotificationChannel("CH_ID", "Testing_Audio", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setSound(soundUri, audioAttributes);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }
        mNotificationManager.notify(0, mBuilder.build());
    }
}
