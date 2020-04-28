package org.techtown.smarket_android.User.Bookmark;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.techtown.smarket_android.Alaram.alarm_fragment;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Bookmark;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class bookmark_price_alarm_fragment extends Fragment {

    public static bookmark_price_alarm_fragment newInstance(bookmark_item_list_adapter.bmViewHolder bmViewHolder) {
        return new bookmark_price_alarm_fragment(bmViewHolder);
    }


    private LineChart lineChart;
    private ConstraintLayout timeSet_view;
    private ViewGroup viewGroup;
    private int nSelectItem = -1;
    private TextView timeSet_tv;
    private Switch alarm_switch;
    private ImageView back_btn;


//    private static final String SETTINGS_TIMELIST_JSON = "settings_timelist_json";
//    private static final String SETTINGS_BOOLEANVALUE_JSON = "settings_booleanvalue_json";

    private SharedPreferences userFile;
    private List<Bookmark> bookmarks;
    private int alarm_time;
    private boolean alarm_check;

    private Bookmark this_bookmark; // 선택된 북마크
    private String this_bookmark_name; // 선택된 북마크 이름
    private int this_bookmark_index; // 선택된 북마크 인덱스



    bookmark_price_alarm_fragment(bookmark_item_list_adapter.bmViewHolder bmViewHolder) {
        alarm_time = bmViewHolder.getAlarm_time();
        alarm_check = bmViewHolder.getAlarm_check();
        this_bookmark_name = bmViewHolder.getBookmark_name();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.bookmark_price_alarm, container, false);
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        get_this_bookmark();

        lineChart = (LineChart) viewGroup.findViewById(R.id.linechart);

        set_chart();

        alarm_switch = viewGroup.findViewById(R.id.alarm_setting_switch);
        alarm_switch.setChecked(alarm_check);
        alarm_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!alarm_check) {
                    alarm_check = true;
                    set_alarm_check();
                } else {
                    alarm_check = false;
                    set_alarm_check();
                }
            }
        });

        timeSet_view = viewGroup.findViewById(R.id.timeSet_view);
        timeSet_tv = viewGroup.findViewById(R.id.timeSet_tv2);
        timeSet_tv.setText(alarm_time+"시간 마다");

        timeSet_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_timer();
            }
        });

        back_btn = viewGroup.findViewById(R.id.price_alarm_back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_myBookmarks();
                getActivity().onBackPressed();
            }
        });
        return viewGroup;
    }

    private void set_chart(){
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 1));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 0));
        entries.add(new Entry(4, 4));

        LineDataSet lineDataSet = new LineDataSet(entries, "속성명1");


        lineDataSet.setLineWidth(2); // 선 굵기
        lineDataSet.setCircleRadius(6); // 곡률
        lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.graphColor)); // LineChart에서 Line Circle Color 설정
        lineDataSet.setCircleHoleColor(ContextCompat.getColor(getContext(), R.color.graphColor)); // LineChart에서 Line Hole Circle Color 설정
        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.graphColor));

        LineData lineData = new LineData(lineDataSet);

        lineData.setValueTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
        lineData.setValueTextSize(9);

        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private void set_timer() {
        final String[] timeList = {"1시간 마다", "3시간 마다", "6시간 마다", "12시간 마다"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        dialog.setTitle("시간 설정")
                .setSingleChoiceItems(timeList, alarm_time, new DialogInterface.OnClickListener() {
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
                            alarm_time = nSelectItem;
                            set_alarm_time();
                            nSelectItem = -1;
                        }
                    }
                })
                .setCancelable(false)
                .show();
    }

    // 클라이언트에 저장된 모든 북마크 정보 가져오기
    private void get_this_bookmark(){
        if(userFile.getString("myBookmarks", null) != null){
            String myBookmarks = userFile.getString("myBookmarks", null);
            Type listType = new TypeToken<ArrayList<Bookmark>>(){}.getType();
            bookmarks = new GsonBuilder().create().fromJson(myBookmarks, listType);
            Log.d("Get myBookmarks", "myBookmarks: Complete Getting myBookmarks");
        }else{
            bookmarks = null;
        }

        for (int i = 0; i < bookmarks.size(); i++) {
            if(bookmarks.get(i).getBookmark_name().equals(this_bookmark_name)){
                this_bookmark_index = i;
                this_bookmark = bookmarks.get(i);
                break;
            }
        }
    }

    // alarm_check 값 설정
    private void set_alarm_check(){
        this_bookmark.setAlarm_check(alarm_check);
        bookmarks.set(this_bookmark_index, this_bookmark);
    }

    // alarm_time 값 설정
    private void set_alarm_time(){
        this_bookmark.setAlarm_time(alarm_time);
        bookmarks.set(this_bookmark_index, this_bookmark);
    }

    private void set_myBookmarks(){
        // List<Bookmark> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<Bookmark>>(){}.getType();
        String json = new GsonBuilder().create().toJson(bookmarks, listType);

        // 스트링 객체로 변환된 데이터를 myBookmarks에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("myBookmarks", json);
        editor.commit();
    }
}
