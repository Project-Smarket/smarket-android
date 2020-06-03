package org.techtown.smarket_android.AppOnpage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.techtown.smarket_android.MainActivity;
import org.techtown.smarket_android.MainNavigation.MainNavigationActivity;
import org.techtown.smarket_android.R;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    //푸시 알림 설정
    private String color = "";
    private String device_token = "";
    private String user_id = "";
    private int cnt = 0;

    private Context mContext;

    public MyFirebaseMessagingService(){}
    public MyFirebaseMessagingService(Context context){
        mContext = context;
    } // 이거 없애면 팅김;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //푸시 메시지가 왔을때 실행되는 메소드.
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            scheduleJob();
            sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body")); // 수신한 푸시 메시지를 Notification으로 전송하여 띄우게 됨.
        }

        //Notification 사용했을때 data 가져오기
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getColor());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getIcon());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }


    @Override
    public void onNewToken(String token) { // 새로운 토큰이 발행될때 호출됨.
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

//        sendRegistrationToServer(token);
    }


    private void scheduleJob() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }


    // 참고사이트 https://webnautes.tistory.com/665
    // https://stackoverflow.com/questions/20881226/pending-intent-in-notification-not-working
    // https://like-tomato.tistory.com/156
    // https://medium.com/@logishudson0218/intent-flag%EC%97%90-%EB%8C%80%ED%95%9C-%EC%9D%B4%ED%95%B4-d8c91ddd3bfc

    private void sendNotification(String title, String body) { //Notification으로 전송하는 메소드.
        if(title == null){
            title = "푸시 알림";
        }
        Intent intent = new Intent(this, MainNavigationActivity.class);//Intent는 아직 모름ㅠ
        intent.putExtra("notification", "Notification");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int)(System.currentTimeMillis()/1000), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "채널 ID";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_smarket_round)
                        .setContentTitle(title) // 푸시 알람에 title 적용
                        .setContentText(body) // body 적용
                        .setAutoCancel(true) // 노티바에서 터치하면 자동으로 삭제되도록 설정
                        .setSound(defaultSoundUri)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify((int)(System.currentTimeMillis()/1000), notificationBuilder.build());;
    }

}

