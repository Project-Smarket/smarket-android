package org.techtown.smarket_android.Alarm;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.techtown.smarket_android.DTO_Class.Alarm;
import org.techtown.smarket_android.DTO_Class.DTO;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.Search.RecyclerDecoration;
import org.techtown.smarket_android.Search.search_detail_fragment;

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
    private List<DTO> alarmList;

    // ** 로그인 및 토큰 정보 ** //
    private SharedPreferences userFile;
    private String user_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.alarm_main, container, false);

        // 툴바 설정
        set_toolbar();

        // Toolbar의 메뉴 버튼 활성화
        setHasOptionsMenu(true);

        // 현재 로그인된 아이디 - userFile에 저장된 user_id 가져오기
        get_userFile();

        // alarmList 데이터 가져옴
        get_alarmList();

        // alarmList recyclerView 설정
        set_recyclerView();


        return viewGroup;
    }

    // alarmList 데이터 가져옴
    private void set_recyclerView() {
        RecyclerDecoration spaceDecoration = new RecyclerDecoration(8);

        alarmRecyclerView = viewGroup.findViewById(R.id.alarm_list_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
        alarmRecyclerView.setLayoutManager(linearLayoutManager);
        alarmRecyclerView.addItemDecoration(spaceDecoration);
        alarmListAdapter = new alarmListAdapter(getActivity(), getContext(), alarmList);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(alarmRecyclerView);
        alarmRecyclerView.setAdapter(alarmListAdapter);

    }

    // SharedPreference의 alarmList 데이터를 가져온다
    private void get_alarmList() {
        String key = user_id + "/alarmList";
        // 저장된 alarmList 있을 경우
        if (userFile.getString(key, null) != null) {
            String key_alarmList = userFile.getString(key, null);
            Type listType = new TypeToken<ArrayList<DTO>>() {
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
        String key = user_id + "/alarmList";
        // List<Alarm> 클래스 객체를 String 객체로 변환
        Type listType = new TypeToken<ArrayList<DTO>>() {
        }.getType();
        String json = new GsonBuilder().create().toJson(alarmList, listType);

        // 스트링 객체로 변환된 데이터를 alarmList에 저장
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString(key, json);
        editor.apply();
    }

    private void set_toolbar() {
        Toolbar toolbar = viewGroup.findViewById(R.id.alarm_toolbar);
        toolbar.setTitle("가격 변동 내역");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_trashcan: {
                delete_alarmList();
                return true;
            }


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.alarm_menu, menu);
    }

    private void delete_alarmList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("알람 리스트 삭제")
                .setMessage("알람 리스트를 전부 삭제하시겠습니까?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alarmList.clear();
                        alarmListAdapter.notifyDataSetChanged();
                        save_alarmList();
                    }
                })
                .setNegativeButton("취소", null)
                .setCancelable(true);
        builder.create();
        builder.show();
    }



    // userFile에 저장된 user_id 가져오기
    private void get_userFile() {
        userFile = getContext().getSharedPreferences("userFile", MODE_PRIVATE);
        user_id = userFile.getString("user_id", null);
    }

    //스와이프 테스트중
    // https://www.youtube.com/watch?v=M1XEqqo6Ktg
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            alarmList.remove(viewHolder.getAdapterPosition());
            alarmListAdapter.notifyDataSetChanged();
            save_alarmList();
        }
    };
}
