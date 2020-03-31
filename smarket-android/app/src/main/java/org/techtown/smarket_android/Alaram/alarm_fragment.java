package org.techtown.smarket_android.Alaram;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.techtown.smarket_android.R;

import java.util.ArrayList;
import java.util.List;

public class alarm_fragment extends Fragment {

    public static alarm_fragment newInstance(){
        return new alarm_fragment();
    }


    private LineChart lineChart;
    private ConstraintLayout timeSet_view;
    ViewGroup viewGroup;
    private int nSelectItem;
    private TextView timeSet_tv;
    private Switch alarm_switch;
    private boolean alarm_checked = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.alarm_main, container, false);

        lineChart = viewGroup.findViewById(R.id.linechart);

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1,1));
        entries.add(new Entry(2,2));
        entries.add(new Entry(3,0));
        entries.add(new Entry(4,4));

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

        alarm_switch = viewGroup.findViewById(R.id.alarm_setting_switch);
        if(alarm_checked){
            alarm_switch.setText("ON");
        }else {
            alarm_switch.setTextOff("OFF");
        }


        timeSet_view = viewGroup.findViewById(R.id.timeSet_view);
        timeSet_tv = viewGroup.findViewById(R.id.timeSet_tv2);
        timeSet_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSet();
            }
        });
        return viewGroup;
    }

    private void timeSet(){
        final String[] timeList = {"1시간 마다", "3시간 마다", "6시간 마다", "12시간 마다"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        dialog.setTitle("시간 설정")
                .setSingleChoiceItems(timeList, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nSelectItem = which;
                    }
                })
                .setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(nSelectItem>=0)
                            timeSet_tv.setText(timeList[nSelectItem]);
                    }
                })
                .setCancelable(false)
                .show();
    }
}
