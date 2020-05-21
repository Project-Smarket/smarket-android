package org.techtown.smarket_android.MainNavigation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.techtown.smarket_android.Alarm.alarm_fragment;
import org.techtown.smarket_android.Hotdeal.hotdeal_fragment;
import org.techtown.smarket_android.NewSearch.newsearch_fragment;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.UserLogin.user_login_fragment;
import org.techtown.smarket_android.searchItemList.search_fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainNavigationActivity extends AppCompatActivity {
    private static final String TAG = "alarmmanager_main";
    private BottomNavigationView bottomNavigationView;
    private newsearch_fragment search_fragment1;
    private hotdeal_fragment hotdeal_fragment2;
    //user_login_success user_fragment2; // 로그인 완료 창
    private user_login_fragment user_fragment3; // 로그인 창
    private alarm_fragment alarm_fragment4;

    private static final String SETTINGS_BOOKMARK_JSON = "settings_bookmark_json";

    private int alarm_unique_id = 1212;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        bottomNavigationView = findViewById(R.id.bottomNavigationView); //프래그먼트 생성
        search_fragment1 = new newsearch_fragment();//제일 처음 띄워줄 뷰를 세팅해줍니다. commit();까지 해줘야 합니다.

        hotdeal_fragment2 = new hotdeal_fragment(); // 스마켓 홈 창
        //user_fragment2 = new user_login_success(); // 로그인 완료 창
        user_fragment3 = new user_login_fragment(); // 로그인 창
        alarm_fragment4 = new alarm_fragment(); // 최저가 알림창

        //set_bookmarkFolderList(); // 디폴트 북마크 폴더 생성 (함수 한번 실행시 어플이 삭제될 때까지 데이터 존재)

        set_navigation();
        //check_alarmManager();
        set_Time();

    }

    private void set_navigation(){
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, search_fragment1).commitAllowingStateLoss(); //bottomnavigationview의 아이콘을 선택 했을때 원하는 프래그먼트가 띄워질 수 있도록 리스너를 추가합니다.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) { //menu_bottom.xml에서 지정해줬던 아이디 값을 받아와서 각 아이디값마다 다른 이벤트를 발생시킵니다.
                    case R.id.tab1: {

                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, search_fragment1).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.tab2: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, hotdeal_fragment2).commitAllowingStateLoss(); //로그인 완료 창
                        //getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, user_fragment2).commitAllowingStateLoss(); // 로그인 창
                        return true;
                    }
                    case R.id.tab3: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, user_fragment3).commitAllowingStateLoss();
                        return true;
                    }

                    case R.id.tab4: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, alarm_fragment4).commitAllowingStateLoss();
                        return true;
                    }

                    default:
                        return false;
                }
            }
        });
    }

    // 설정된 알람 삭제
    private void check_alarmManager() {

        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, alarm_unique_id, intent, PendingIntent.FLAG_NO_CREATE);

        if (sender == null) {
            // TODO: 이미 설정된 알람이 없는 경우
            Log.d(TAG, "check_alarmManager: 알람이 없습니다");
        } else {
            // TODO: 이미 설정된 알람이 있는 경우
            sender = PendingIntent.getBroadcast(this, 0, intent, 0);
            Log.d(TAG, "check_alarmManager: 알람을 지웁니다");
            am.cancel(sender);
            sender.cancel();

        }

    }

    private void set_Time() {

        // 알람 시간 설정
        Calendar calendar = Calendar.getInstance();

        // 알람 10분 - 오후 12시
        if (calendar.get(Calendar.MINUTE) >= 0 && calendar.get(Calendar.MINUTE) < 10) {
            calendar.set(Calendar.MINUTE, 10);
        }
        // 알람 20분 - 오후 3시
        else if (calendar.get(Calendar.MINUTE) >= 10 && calendar.get(Calendar.MINUTE) < 20) {
            calendar.set(Calendar.MINUTE, 20);
        }
        // 알람 30분 - 오후 6시
        else if (calendar.get(Calendar.MINUTE) >= 20 && calendar.get(Calendar.MINUTE) < 30) {
            calendar.set(Calendar.MINUTE, 30);
        } // 알람 40분 - 오후 9시
        else if (calendar.get(Calendar.MINUTE) >= 30 && calendar.get(Calendar.MINUTE) < 40) {
            calendar.set(Calendar.MINUTE, 40);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            calendar.set(Calendar.MINUTE, 10);
        }


        set_alarmManager(calendar);
    }

    private void set_alarmManager(Calendar calendar) {
        // 현재 시간
        Date date = new Date();

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, alarm_unique_id, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("알람", date.toString() + " : 알람이 " + calendar.get(Calendar.MINUTE) + "분로 설정되었습니다");
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }
    }

}


