package org.techtown.smarket_android.User.Bookmark;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.techtown.smarket_android.Alaram.alarm_fragment;
import org.techtown.smarket_android.R;

import java.util.ArrayList;
import java.util.List;

public class bookmark_price_alarm_fragment extends Fragment {

    public static bookmark_price_alarm_fragment newInstance(int position) {
        return new bookmark_price_alarm_fragment(position);
    }


    private LineChart lineChart;
    private ConstraintLayout timeSet_view;
    ViewGroup viewGroup;
    private int nSelectItem = -1;
    private TextView timeSet_tv;
    private Switch alarm_switch;


    private List<String> timeList;
    private static final String SETTINGS_TIMELIST_JSON = "settings_timelist_json";
    private int itemPosition;

    private List<String> booleanValueList;
    private static final String SETTINGS_BOOLEANVALUE_JSON = "settings_booleanvalue_json";
    private boolean alarm_checked;

    bookmark_price_alarm_fragment(int position) {
        itemPosition = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.bookmark_price_alarm, container, false);

        lineChart = (LineChart) viewGroup.findViewById(R.id.linechart);

        set_chart();

        booleanValueList = getStringArrayPref(getContext(), SETTINGS_BOOLEANVALUE_JSON);
        alarm_checked = Boolean.parseBoolean(booleanValueList.get(itemPosition));
        alarm_switch = viewGroup.findViewById(R.id.alarm_setting_switch);
        alarm_switch.setChecked(alarm_checked);
        alarm_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!alarm_checked) {
                    alarm_checked = true;
                    set_booleanValueList(alarm_checked);
                } else {
                    alarm_checked = false;
                    set_booleanValueList(alarm_checked);
                }
            }
        });

        timeSet_view = viewGroup.findViewById(R.id.timeSet_view);
        timeSet_tv = viewGroup.findViewById(R.id.timeSet_tv2);
        timeList = getStringArrayPref(getContext(), SETTINGS_TIMELIST_JSON);
        timeSet_tv.setText(timeList.get(itemPosition));

        timeSet_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_timer();
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
        int checkedItem = -1;
        switch (timeSet_tv.getText().toString()) {
            case "1시간 마다":
                checkedItem = 0;
                break;
            case "3시간 마다":
                checkedItem = 1;
                break;
            case "6시간 마다":
                checkedItem = 2;
                break;
            case "12시간 마다":
                checkedItem = 3;
                break;
        }

        Toast.makeText(getContext(), String.valueOf(checkedItem), Toast.LENGTH_LONG).show();
        dialog.setTitle("시간 설정")
                .setSingleChoiceItems(timeList, checkedItem, new DialogInterface.OnClickListener() {
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
                            set_timeList(timeList[nSelectItem]);
                            nSelectItem = -1;
                        }
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void set_timeList(String selectedItem) {
        timeList.set(itemPosition, selectedItem);
        updateBookmarkFolderList(getContext(), SETTINGS_TIMELIST_JSON, timeList);
    }

    private void set_booleanValueList(boolean alarm_checked){
        String booleanValue = String.valueOf(alarm_checked);
        booleanValueList.set(itemPosition, booleanValue);
        updateBookmarkFolderList(getContext(), SETTINGS_BOOLEANVALUE_JSON, booleanValueList);
    }

    private ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }// 폴더 리스트 데이터 가져오기

    private void updateBookmarkFolderList(Context context, String key, List<String> values) {
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
    } // 북마크 폴더 리스트 업데이트
}
