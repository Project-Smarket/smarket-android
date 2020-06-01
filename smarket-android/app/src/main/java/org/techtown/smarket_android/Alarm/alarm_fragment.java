package org.techtown.smarket_android.Alarm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.techtown.smarket_android.DTO_Class.Alarm;
import org.techtown.smarket_android.R;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class alarm_fragment extends Fragment {


    private static final String TAG = "알람리스트";
    private ViewGroup viewGroup;

    private RecyclerView alarmRecyclerView;
    private alarmListAdapter alarmListAdapter;
    private List<Alarm> alarmList;


    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.alarm_main, container, false);

        // 현재 로그인된 아이디 - userFile에 저장된 user_id 가져오기
        get_userFile();

        // alarmList 데이터 가져옴
        get_alarmList();

        // 현재 로그인된 아이디와 일치하는 알람만 가져오기 - user_id가 일치하지 않는 alarm은 삭제
        if (alarmList.size() != 0) {
            for (int i = alarmList.size() - 1; i >= 0; i--) {
                if (!alarmList.get(i).getUser_id().equals(user_id)) {
                    alarmList.remove(i);
                }
            }
        }
        // alarmList recyclerView 설정
        set_recyclerView();


        return viewGroup;
    }

    // alarmList 데이터 가져옴
    private void set_recyclerView() {
        alarmRecyclerView = viewGroup.findViewById(R.id.alarm_list_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
        alarmRecyclerView.setLayoutManager(linearLayoutManager);
        alarmListAdapter = new alarmListAdapter(getActivity(), getContext(), alarmList);
        alarmRecyclerView.setAdapter(alarmListAdapter);
    }

    // SharedPreference의 alarmList 데이터를 가져온다
    private void get_alarmList() {
        // 저장된 alarmList 있을 경우
        if (userFile.getString("alarmList", null) != null) {
            String key_alarmList = userFile.getString("alarmList", null);
            Type listType = new TypeToken<ArrayList<Alarm>>() {
            }.getType();
            alarmList = new GsonBuilder().create().fromJson(key_alarmList, listType);

        }// 저장된 alarmList 없을 경우
        else {
            alarmList = new ArrayList<>();
            save_alarmList();
        }
    }

    // SharedPreference에 alarmList 데이터 저장
    private void save_alarmList() {
        // List<Alarm> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<Alarm>>() {
        }.getType();
        String json = new GsonBuilder().create().toJson(alarmList, listType);

        // 스트링 객체로 변환된 데이터를 alarmList에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("alarmList", json);
        editor.apply();
    }

    // userFile에 저장된 user_id 가져오기
    private void get_userFile() {
        userFile = getContext().getSharedPreferences("userFile", MODE_PRIVATE);
        user_id = userFile.getString("user_id", null);
    }
}
