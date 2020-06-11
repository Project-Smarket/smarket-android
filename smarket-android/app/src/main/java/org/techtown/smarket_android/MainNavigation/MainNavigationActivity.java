package org.techtown.smarket_android.MainNavigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.techtown.smarket_android.Alarm.alarm_fragment;
import org.techtown.smarket_android.Alarm.fluctuation_fragment;
import org.techtown.smarket_android.Hotdeal.hotdeal_fragment;
import org.techtown.smarket_android.Search.OnBackpressedListener;
import org.techtown.smarket_android.Search.search_detail_fragment;
import org.techtown.smarket_android.Search.search_fragment;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.Bookmark.bookmark_fragment;
import org.techtown.smarket_android.User.Latest.latest_fragment;
import org.techtown.smarket_android.User.UserLogin.user_login_fragment;
import org.techtown.smarket_android.User.UserLogin.user_login_success;


public class MainNavigationActivity extends AppCompatActivity {
    private static final String TAG = "alarmmanager_main";
    private BottomNavigationView bottomNavigationView;
    private long backKeyPressedTime = 0;
    private search_fragment search_fragment1;
    private hotdeal_fragment hotdeal_fragment2;
    //user_login_success user_fragment2; // 로그인 완료 창
    private user_login_fragment user_fragment3; // 로그인 창
    private alarm_fragment alarm_fragment4;
    private boolean back_check = false;

    private SharedPreferences userFile;
    private String userID;
    private String user_nickname;
    private String access_token;
    private String refresh_token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        bottomNavigationView = findViewById(R.id.bottomNavigationView); //프래그먼트 생성

        set_navigation();

        checkNotification();     //알림으로 들어올시 실행되는 메소드


    }

    // 각 프래그먼트에 addToBackStack 선언 = 뒤로가기를 누르면 스택에 쌓인 프래그먼트가 없어지는 형태

    private void set_navigation() {
        search_fragment1 = new search_fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, search_fragment1, "search").commit(); //bottomnavigationview의 아이콘을 선택 했을때 원하는 프래그먼트가 띄워질 수 있도록 리스너를 추가합니다.

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                switch (menuItem.getItemId()) { //menu_bottom.xml에서 지정해줬던 아이디 값을 받아와서 각 아이디값마다 다른 이벤트를 발생시킵니다.
                    case R.id.tab1: {
                        search_fragment1 = new search_fragment();
                        fragmentTransaction.replace(R.id.main_layout, search_fragment1, "search");
                        back_check = false;
                        break;
                    }
                    case R.id.tab2: {
                        hotdeal_fragment2 = new hotdeal_fragment(); // 스마켓 홈 창
                        fragmentTransaction.replace(R.id.main_layout, hotdeal_fragment2, "hotDeal");
                        back_check = false;
                        //getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, user_fragment2).commitAllowingStateLoss(); // 로그인 창
                        break;
                    }
                    case R.id.tab3: {
                        user_fragment3 = new user_login_fragment(); // 로그인 창
                        user_login_success user_login_success = new user_login_success();
                        get_userFile();
                        if(TextUtils.isEmpty(userID) && TextUtils.isEmpty(access_token))
                            fragmentTransaction.replace(R.id.main_layout, user_fragment3, "login");
                        else{
                            fragmentTransaction.replace(R.id.main_layout, user_login_success, "loginS");
                        }

                        break;
                    }
                    case R.id.tab4: {
                        alarm_fragment4 = new alarm_fragment(); // 최저가 알림창
                        fragmentTransaction.replace(R.id.main_layout, alarm_fragment4, "alarm");
                        back_check = false;
                        break;
                    }
                    default:
                        break;
                }

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_layout);
                // 상세보기 && 가격 변동 내역 && 로그인 창은 백스택에 추가하지 않음
                if (currentFragment instanceof search_detail_fragment || currentFragment instanceof fluctuation_fragment || currentFragment instanceof user_login_fragment || currentFragment instanceof user_login_success) {

                    fragmentTransaction.commit(); //로그인 완료 창
                }
                // 나머지 프래그먼트는 백스택에 추가함
                else {
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit(); //로그인 완료 창
                }

                return true;
            }
        });
    }

    public void updateBottomMenu(final BottomNavigationView bottomNavigationView) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_layout);
                if (currentFragment instanceof search_fragment)
                    bottomNavigationView.getMenu().findItem(R.id.tab1).setChecked(true);
                else if (currentFragment instanceof hotdeal_fragment)
                    bottomNavigationView.getMenu().findItem(R.id.tab2).setChecked(true);
                else if (currentFragment instanceof user_login_success || currentFragment instanceof user_login_fragment || currentFragment instanceof bookmark_fragment || currentFragment instanceof latest_fragment)
                    bottomNavigationView.getMenu().findItem(R.id.tab3).setChecked(true);
                else if (currentFragment instanceof alarm_fragment)
                    bottomNavigationView.getMenu().findItem(R.id.tab4).setChecked(true);
            }
        }, 100);
    }

    //뒤로가기 버튼
    @Override
    public void onBackPressed() {
        Fragment search = getSupportFragmentManager().findFragmentByTag("search");
        Fragment logout = getSupportFragmentManager().findFragmentByTag("logout");
        Fragment loginS = getSupportFragmentManager().findFragmentByTag("loginS");
        Fragment detail = getSupportFragmentManager().findFragmentByTag("detail");

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_layout);

        if (currentFragment instanceof search_fragment) {

            // search 프래그먼트 인스턴스를 가져옴 - 프래그먼트의 데이터에 접근할 수 있음
            search_fragment search_fragment = (org.techtown.smarket_android.Search.search_fragment) getSupportFragmentManager().findFragmentById(R.id.main_layout);

            // search 프래그먼트의 back_check 값이 true 인 경우 검색창 내림
            if (search_fragment.get_backCheck()) {
                ((OnBackpressedListener) currentFragment).onBackPressed();

            }
            // search 프래그먼트의 back_check 값이 true 인 경우 popStack
            else if (!search_fragment.get_backCheck()) {
                // 스택에 쌓인 프래그먼트가 없을 경우 - 두번 클릭으로 앱 종료
                if (fragmentManager.getBackStackEntryCount() == 0) {

                    if (back_check) {
                        this.finish();
                    } else if (!back_check) {
                        Toast.makeText(this, "한번 더 누르면 종료합니다", Toast.LENGTH_LONG).show();
                        back_check = true;
                    }
                }
                // 스택에 프래그먼트가 1개라도 있을 경우 뒤로가기 : popStack
                else if (fragmentManager.getBackStackEntryCount() >= 0) {

                    // 로그아웃 시 && 로그인 완료 시 뒤로가기 막음
                    if ((logout != null && logout.isVisible()) || (loginS != null && loginS.isVisible())) {
                        // Nothing
                    } else {
                        super.onBackPressed();
                        BottomNavigationView bnv = findViewById(R.id.bottomNavigationView);
                        updateBottomMenu(bnv);
                    }
                }
            }
        } else if (fragmentManager.getBackStackEntryCount() == 0) {

            if (back_check) {
                this.finish();
            } else if (!back_check) {
                Toast.makeText(this, "한번 더 누르면 종료합니다", Toast.LENGTH_LONG).show();
                back_check = true;
            }
        }
        // 스택에 프래그먼트가 1개라도 있을 경우 뒤로가기 : popStack
        else if (fragmentManager.getBackStackEntryCount() >= 0) {

            // 로그아웃 시 && 로그인 완료 시 뒤로가기 막음
            if ((logout != null && logout.isVisible()) || (loginS != null && loginS.isVisible())) {
                // Nothing
            } else {
                super.onBackPressed();
                BottomNavigationView bnv = findViewById(R.id.bottomNavigationView);
                updateBottomMenu(bnv);
            }
        }

    }

    private void checkNotification() {
        String str = getIntent().getStringExtra("data");

        if (str != null && str.equals("data")) {
            Handler handler = new Handler();
            handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            bottomNavigationView.setSelectedItemId(R.id.tab4);
                        }
                    }, 300);
        }
    }

    public boolean get_backCheck() {
        return back_check;
    }

    public void set_backCheck(boolean value) {
        back_check = value;
    }

    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        userFile = getSharedPreferences("userFile", Context.MODE_PRIVATE);
        userID = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
    }
}


