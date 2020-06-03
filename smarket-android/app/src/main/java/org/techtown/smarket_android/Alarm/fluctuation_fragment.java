package org.techtown.smarket_android.Alarm;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.techtown.smarket_android.DTO_Class.Alarm;
import org.techtown.smarket_android.DTO_Class.Fluctuation;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.Search.RecyclerDecoration;
import org.techtown.smarket_android.User.Latest.latest_fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class fluctuation_fragment extends Fragment {


    private ViewGroup viewGroup;

    private LineChart lineChart;
    private RecyclerView ftRecyclerView;
    private ftAdapter ftAdapter;
    private List<Fluctuation> fluctuationList;


    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.price_fluctuation, container, false);

        // 툴바 설정
        set_toolbar();

        // Toolbar의 메뉴 버튼 활성화
        setHasOptionsMenu(true);

        // 현재 로그인된 아이디 - userFile에 저장된 user_id 가져오기
        get_userFile();

        // 번들로 데이터 가져옴
        receive_bundle(savedInstanceState);

        // lineChart 설정
        set_lineChart();

        // 리사이클러뷰 설정
        set_recyclerView();


        return viewGroup;
    }

    private void set_lineChart(){
        lineChart = viewGroup.findViewById(R.id.fluctuation_lineChart);

        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < fluctuationList.size(); i++) {
            values.add(new Entry(i+1,Integer.parseInt(fluctuationList.get(i).getLprice())));
        }

        LineDataSet set = new LineDataSet(values, "fluctuation");
        set.setColor(getResources().getColor(R.color.colorBlack));
        set.setCircleColor(getResources().getColor(R.color.colorBlack));
        set.setValueTextSize(12);
        set.setLineWidth(3);
        set.setCircleRadius(6);
        set.setCircleHoleRadius(3);
        set.setCircleHoleColor(getResources().getColor(R.color.colorWhite));
        set.setLabel("");


        LineData data = new LineData(set);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setLabelCount(0,false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        //lineChart.setDrawGridBackground(true);
        lineChart.setNoDataText("가겨 변동 내역이 없습니다");
        lineChart.setNoDataTextColor(getResources().getColor(R.color.colorBlack));
        lineChart.getLegend().setEnabled(false);
        lineChart.setDescription(null);
        lineChart.setExtraOffsets(30,30,30,10);
        lineChart.setScaleEnabled(false);

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(getContext(), "눌림" + e.getX(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });


        lineChart.setData(data);
    }

    // alarmList 데이터 가져옴
    private void set_recyclerView() {
        RecyclerDecoration spaceDecoration = new RecyclerDecoration(8);

        ftRecyclerView = viewGroup.findViewById(R.id.fluctuation_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
        ftRecyclerView.setLayoutManager(linearLayoutManager);
        ftRecyclerView.addItemDecoration(spaceDecoration);
        ftAdapter = new ftAdapter(getActivity(), getActivity(), fluctuationList);
        ftRecyclerView.setAdapter(ftAdapter);
    }


    private void set_toolbar() {
        Toolbar toolbar = viewGroup.findViewById(R.id.fluctuation_toolbar);
        toolbar.setTitle("가격 변동 내역");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // 뒤로가기 버튼 활성화
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void receive_bundle(Bundle bundle){
        bundle = getArguments();
        Alarm item_data = bundle.getParcelable("item_data");
        String id = item_data.getId();
        String item_title = item_data.getItem_title();
        Bitmap image_bitmap = bundle.getParcelable("item_image");

        // id를 통해 SharedPreference fluctuationList 데이터 가져옴
        get_fluctuation(id);

        ImageView fluctuation_image = viewGroup.findViewById(R.id.fluctuation_ImageView);
        Glide.with(getContext()).asBitmap().load(image_bitmap).into(fluctuation_image);

        TextView fluctuation_textView = viewGroup.findViewById(R.id.fluctuation_textView);
        fluctuation_textView.setText(item_title);

    }



    // SharedPreference의 alarmList 데이터를 가져온다
    private void get_fluctuation(String id) {
        String key = user_id + "/alarmList/" + id;
        // 저장된 alarmList 있을 경우
        if (userFile.getString(key, null) != null) {
            String key_fluctuationList = userFile.getString(key, null);
            Type listType = new TypeToken<ArrayList<Fluctuation>>() {
            }.getType();
            fluctuationList = new GsonBuilder().create().fromJson(key_fluctuationList, listType);

        }// 저장된 alarmList 없을 경우
        else {
            fluctuationList = new ArrayList<>();
        }
    }

    // userFile에 저장된 user_id 가져오기
    private void get_userFile() {
        userFile = getContext().getSharedPreferences("userFile", MODE_PRIVATE);
        user_id = userFile.getString("user_id", null);
    }

    // toolbar 뒤로가기 메소드
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().remove(fluctuation_fragment.this).commit();
                fm.popBackStack();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
