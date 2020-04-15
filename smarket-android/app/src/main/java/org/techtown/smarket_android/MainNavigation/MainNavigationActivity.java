package org.techtown.smarket_android.MainNavigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.techtown.smarket_android.Alaram.alarm_fragment;
import org.techtown.smarket_android.Home.home_fragment;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.user_login_success;
import org.techtown.smarket_android.searchItemList.search_fragment;

import java.util.ArrayList;


public class MainNavigationActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    home_fragment home_fragment1;
    user_login_success user_fragment2; // 로그인 완료 창
    //user_login_fragment user_fragment2; // 로그인 창
    alarm_fragment alarm_fragment3;
    search_fragment search_fragment4;

    private static final String SETTINGS_BOOKMARK_JSON = "settings_bookmark_json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        bottomNavigationView = findViewById(R.id.bottomNavigationView); //프래그먼트 생성
        home_fragment1 = new home_fragment(); // 스마켓 홈 창
        user_fragment2 = new user_login_success(); // 로그인 완료 창
        //user_fragment2 = new user_login_fragment(); // 로그인 창
        alarm_fragment3 = new alarm_fragment(); // 최저가 알림창
        search_fragment4 = new search_fragment();//제일 처음 띄워줄 뷰를 세팅해줍니다. commit();까지 해줘야 합니다.

        //set_bookmarkFolderList(); // 디폴트 북마크 폴더 생성 (함수 한번 실행시 어플이 삭제될 때까지 데이터 존재)

        set_navigation();

    }

    private void set_navigation(){
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,home_fragment1).commitAllowingStateLoss(); //bottomnavigationview의 아이콘을 선택 했을때 원하는 프래그먼트가 띄워질 수 있도록 리스너를 추가합니다.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) { //menu_bottom.xml에서 지정해줬던 아이디 값을 받아와서 각 아이디값마다 다른 이벤트를 발생시킵니다.
                    case R.id.tab1: {

                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, home_fragment1).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.tab2: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, user_fragment2).commitAllowingStateLoss(); //로그인 완료 창
                        //getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, user_fragment2).commitAllowingStateLoss(); // 로그인 창
                        return true;
                    }
                    case R.id.tab3: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, alarm_fragment3).commitAllowingStateLoss();
                        return true;
                    }

                    case R.id.tab4: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, search_fragment4).commitAllowingStateLoss();
                        return true;
                    }
                    default:
                        return false;
                }
            }
        });
    }

    private void set_bookmarkFolderList(){

        ArrayList<String> bookmarkFolderList = new ArrayList<String>();
        bookmarkFolderList.add("첫번째 폴더");
        bookmarkFolderList.add("두번째 폴더");

        setStringArrayPref(getApplicationContext(), SETTINGS_BOOKMARK_JSON, bookmarkFolderList);
    } // 디폴트 북마크 폴더리스트 생성

    private void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }
}


