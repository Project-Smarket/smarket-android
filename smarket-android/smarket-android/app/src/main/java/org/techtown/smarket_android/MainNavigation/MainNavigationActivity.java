package org.techtown.smarket_android.MainNavigation;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.techtown.smarket_android.Alarm.alarm_fragment;
import org.techtown.smarket_android.Hotdeal.home_fragment;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.user_login_fragment;
import org.techtown.smarket_android.searchItemList.search_fragment;


public class MainNavigationActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    home_fragment home_fragment1;
    user_login_fragment user_fragment2;
    alarm_fragment alarm_fragment3;
    search_fragment search_fragment4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        bottomNavigationView = findViewById(R.id.bottomNavigationView); //프래그먼트 생성
        home_fragment1 = new home_fragment();
        user_fragment2 = new user_login_fragment();
        alarm_fragment3 = new alarm_fragment();
        search_fragment4 = new search_fragment();//제일 처음 띄워줄 뷰를 세팅해줍니다. commit();까지 해줘야 합니다.

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
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, user_fragment2).commitAllowingStateLoss();
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
}


