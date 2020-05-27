package org.techtown.smarket_android.User.Bookmark;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.techtown.smarket_android.R;
import org.techtown.smarket_android.smarketClass.BookmarkAlarm;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class bookmark_price_alarm_fragment extends Fragment {

    public static bookmark_price_alarm_fragment newInstance(bookmark_item_list_adapter.bmViewHolder bmViewHolder) {
        return new bookmark_price_alarm_fragment(bmViewHolder);
    }


    private ConstraintLayout timeSet_view;
    private ViewGroup viewGroup;
    private int nSelectItem = -1;
    private TextView timeSet_tv;
    private Switch alarm_switch;
    private Toolbar toolbar;



    private SharedPreferences userFile;
    private List<BookmarkAlarm> bookmarkAlarmList;

    // 생성자에서 초기화
    private String this_bookmark_id; // 선택된 북마크의 itemId
    private boolean this_alarm_check; // 선택된 북마크 alarm_check
    private int this_alarm_time; // 선택된 북마크 alarm_time

    // get_this_bookmark()에서 초기화
    private BookmarkAlarm this_bookmarkAlarm; // 선택된 북마크알람 객체
    private int this_bookmarkAlarm_index; // 선택된 북마크 인덱스

    bookmark_price_alarm_fragment(bookmark_item_list_adapter.bmViewHolder bmViewHolder) {
        this_bookmark_id = bmViewHolder.getBookmark_id();
        this_alarm_check = bmViewHolder.getAlarm_check();
        this_alarm_time = bmViewHolder.getAlarm_time();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.bookmark_price_alarm, container, false);
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);

        // 선택된 bookmark_title을 이용해 SharedPreference에서 북마크 알람 파일 가져오기
        get_this_bookmarkAlarmList();



        alarm_switch = viewGroup.findViewById(R.id.alarm_setting_switch);
        alarm_switch.setChecked(this_alarm_check);
        alarm_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!this_alarm_check) {
                    this_alarm_check = true;
                    set_alarm_check();
                } else {
                    this_alarm_check = false;
                    set_alarm_check();
                }
            }
        });

        timeSet_view = viewGroup.findViewById(R.id.timeSet_view);
        timeSet_tv = viewGroup.findViewById(R.id.timeSet_tv2);
        switch (this_alarm_time){
            case 0 : timeSet_tv.setText("오후 12시");break;
            case 1 : timeSet_tv.setText("오후 3시");break;
            case 2 : timeSet_tv.setText("오후 6시");break;
            case 3 : timeSet_tv.setText("오후 9시");break;
        }


        timeSet_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_timer();
            }
        });

        //상단 메뉴바설정
        settingToolbar();
        setHasOptionsMenu(true);


        return viewGroup;
    }

    private void settingToolbar(){
        toolbar = viewGroup.findViewById(R.id.bookmarkToolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                set_myBookmarks();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().remove(bookmark_price_alarm_fragment.this).commit();
                fm.popBackStack();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.bookmark_menu, menu);
    }

    private void set_timer() {
        final String[] timeList = {"오후 12시", "오후 3시", "오후 6시", "오후 9시"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        dialog.setTitle("시간 설정")
                .setSingleChoiceItems(timeList, this_alarm_time, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nSelectItem = which;
                    }
                })
                .setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (nSelectItem >= 0) {
                            timeSet_tv.setText(timeList[nSelectItem]);
                            this_alarm_time = nSelectItem;
                            set_alarm_time();
                            nSelectItem = -1;
                        }
                    }
                })
                .setCancelable(false)
                .show();
    }

    // 클라이언트에 저장된 모든 북마크 알람 정보 가져오기
    private void get_this_bookmarkAlarmList(){
        if(userFile.getString("bookmarkAlarmList", null) != null){
            String bookmarkAlarm = userFile.getString("bookmarkAlarmList", null);
            Type listType = new TypeToken<ArrayList<BookmarkAlarm>>(){}.getType();
            bookmarkAlarmList = new GsonBuilder().create().fromJson(bookmarkAlarm, listType);
            Log.d("Get bookmarkAlarmList", "bookmarkAlarmList : Complete Getting bookmarkAlarmList");
        }else{
            bookmarkAlarmList = null;
        }

        for (int i = 0; i < bookmarkAlarmList.size(); i++) {
            // 현재 선택된 북마크의 bookmark_id와 SharedPreference의 bookmark_id 일치하면 해당 데이터의 index와 객체를 가져옴
            if(bookmarkAlarmList.get(i).getBookmark_id().equals(this_bookmark_id)){
                this_bookmarkAlarm_index = i;
                this_bookmarkAlarm = bookmarkAlarmList.get(i);
                break;
            }
        }
    }

    // alarm_check 값 설정
    private void set_alarm_check(){
        this_bookmarkAlarm.setAlarm_check(this_alarm_check);
        bookmarkAlarmList.set(this_bookmarkAlarm_index, this_bookmarkAlarm);
    }

    // alarm_time 값 설정
    private void set_alarm_time(){
        this_bookmarkAlarm.setAlarm_time(this_alarm_time);
        bookmarkAlarmList.set(this_bookmarkAlarm_index, this_bookmarkAlarm);
    }

    private void set_myBookmarks(){
        // List<Bookmark> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<BookmarkAlarm>>(){}.getType();
        String json = new GsonBuilder().create().toJson(bookmarkAlarmList, listType);

        // 스트링 객체로 변환된 데이터를 myBookmarks에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("bookmarkAlarmList", json);
        editor.commit();
    }
}
