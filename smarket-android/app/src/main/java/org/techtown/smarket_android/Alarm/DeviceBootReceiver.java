package org.techtown.smarket_android.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DeviceBootReceiver extends BroadcastReceiver {

    private SharedPreferences userFile;
    private String user_id;
    private Boolean user_alarm = false;
    private int alarm_unique_id = 1212;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 재부팅시 자동실행 설정 값을 가져옴

        String action = intent.getAction();                          // intent로 전달받은 Action을 얻는다.

        // 시스템 부팅이 완료되었고, user_alarm이 True면 알람 설정
        if (action.equals("android.intent.action.BOOT_COMPLETED") && user_alarm) {

            // 알람 시간 설정
            Calendar calendar = Calendar.getInstance();

            // 알람 10분 - 오후 12시
            if (calendar.get(Calendar.SECOND) >= 0 && calendar.get(Calendar.SECOND) < 10) {
                calendar.set(Calendar.SECOND, 10);
            }
            // 알람 20분 - 오후 3시
            else if (calendar.get(Calendar.SECOND) >= 10 && calendar.get(Calendar.SECOND) < 20) {
                calendar.set(Calendar.SECOND, 20);
            }
            // 알람 30분 - 오후 6시
            else if (calendar.get(Calendar.SECOND) >= 20 && calendar.get(Calendar.SECOND) < 30) {
                calendar.set(Calendar.SECOND, 30);
            } // 알람 40분 - 오후 9시
            else if (calendar.get(Calendar.SECOND) >= 30 && calendar.get(Calendar.SECOND) < 40) {
                calendar.set(Calendar.SECOND, 40);
            } else {
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
                calendar.set(Calendar.SECOND, 10);
            }

            // 알람을 재설정
            set_alarmManager(calendar, context);

        }
    }


    private void set_alarmManager(Calendar calendar, Context context) {
        // 현재 시간
        Date date = new Date();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alarm_unique_id, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(context, "DeviceBootReceiver : 재부팅완료 알람 재설정", Toast.LENGTH_SHORT).show();
            Log.d("알람", date.toString() + " : 알람이 " + calendar.get(Calendar.SECOND) + "분로 설정되었습니다");
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }
    }
}
