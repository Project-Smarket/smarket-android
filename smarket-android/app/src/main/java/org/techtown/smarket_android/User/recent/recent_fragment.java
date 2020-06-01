package org.techtown.smarket_android.User.recent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.techtown.smarket_android.DTO_Class.DTO;
import org.techtown.smarket_android.Alarm.AlarmReceiver;
import org.techtown.smarket_android.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class recent_fragment extends Fragment {
    public static recent_fragment newInstance() {
        return new recent_fragment();
    }

    private ViewGroup viewGroup;

    private Button remove_bookmarkAlarmList;
    private TextView bookmarkAlarmList_id;
    private TextView bookmarkAlarmList_foldername;
    private TextView bookmarkAlarmList_userId;
    private TextView bookmarkAlarmList_price;
    private TextView bookmarkAlarmList_time;

    private Button remove_bookmarkList;
    private TextView bookmarkList_foldername;

    private Button remove_alarmList;
    private TextView check_alarmList;
    private TextView check_alarmList_user_id;
    private TextView check_alarmList_item_title;

    private Button remove_alarm;
    private TextView alarm_textView;

    private int alarm_unique_id = 1212;

    private SharedPreferences userFile;
    private String user_id;
    private String access_token;
    private String refresh_token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Date date = new Date();
        Log.d("DATE", "onCreateView: " + date.toString());
        Calendar calendar = Calendar.getInstance();
        Log.d("Calandar", "onCreateView: " + calendar.getTimeInMillis());
        get_userFile();
        viewGroup = (ViewGroup) inflater.inflate(R.layout.recent_main, container, false);
        remove_bookmarkAlarmList = viewGroup.findViewById(R.id.remove_bookmarkalarm);
        bookmarkAlarmList_userId = viewGroup.findViewById(R.id.check_bookmarkalarm);
        bookmarkAlarmList_id = viewGroup.findViewById(R.id.check_bookmarkalarm2);
        bookmarkAlarmList_foldername = viewGroup.findViewById(R.id.check_bookmarkalarm3);
        bookmarkAlarmList_price = viewGroup.findViewById(R.id.check_bookmarkalarm4);
        bookmarkAlarmList_time = viewGroup.findViewById(R.id.check_bookmarkalarm5);
        bookmarkList_foldername = viewGroup.findViewById(R.id.check_bookmarklist);
        remove_bookmarkList = viewGroup.findViewById(R.id.remove_bookmarkList);

        remove_alarm = viewGroup.findViewById(R.id.remove_alarm);
        alarm_textView = viewGroup.findViewById(R.id.alarm_textview);

        remove_alarmList = viewGroup.findViewById(R.id.remove_alarmList);
        check_alarmList = viewGroup.findViewById(R.id.check_alarmlist);
        check_alarmList_user_id = viewGroup.findViewById(R.id.check_alarmlist_user_id);
        check_alarmList_item_title = viewGroup.findViewById(R.id.check_alarmlist_item_title);

        set_bookmarkFolderList();
        set_alamrList();

        remove_bookmarkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove_folderlist();
                set_bookmarkFolderList();
            }
        });

        remove_alarmList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove_alarmlist();
                set_alamrList();
            }
        });

        check_Alarm();

        remove_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove_Alarm();
            }
        });

        return viewGroup;
    }

    private void check_Alarm() {
        // 설정된 알람 삭제


        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(getContext(), alarm_unique_id, intent, PendingIntent.FLAG_NO_CREATE);

        if (sender == null) {
            // TODO: 이미 설정된 알람이 없는 경우
            alarm_textView.setText("알람이 없습니다");
        } else {
            // TODO: 이미 설정된 알람이 있는 경우
            alarm_textView.setText("알람이 설정되어있습니다");
        }

    }

    // 설정된 알람 삭제
    private void remove_Alarm() {

        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(getContext(), alarm_unique_id, intent, PendingIntent.FLAG_NO_CREATE);

        if (sender == null) {
            // TODO: 이미 설정된 알람이 없는 경우
        } else {
            // TODO: 이미 설정된 알람이 있는 경우
            sender = PendingIntent.getBroadcast(getContext(), 0, intent, 0);
            am.cancel(sender);
            sender.cancel();
        }

        check_Alarm();

    }

    private void remove_alarmlist() {
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("alarmList", null);
        editor.apply();
    }

    private void set_alamrList() {
        List<DTO> alarmList;
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        if (userFile.getString("alarmList", null) != null) {
            String myBookmarks = userFile.getString("alarmList", null);
            Type listType = new TypeToken<ArrayList<DTO>>() {
            }.getType();
            alarmList = new GsonBuilder().create().fromJson(myBookmarks, listType);
        } else {
            alarmList = null;
        }

        String set1 = "";
        String set2 = "";
        String set3 = "";
    }

    private void remove() {
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("bookmarkAlarmList", null);
        editor.apply();
    }

    private void remove_folderlist() {
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("bookmarkFolderList", null);
        editor.apply();
    }


    private void set_bookmarkFolderList() {
        List<String> bookmarkFolderList;
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        if (userFile.getString("bookmarkFolderList", null) != null) {
            String myBookmarks = userFile.getString("bookmarkFolderList", null);
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            bookmarkFolderList = new GsonBuilder().create().fromJson(myBookmarks, listType);
            Log.d("Get myBookmarks", "bookmarkFolderList: Complete Getting bookmarkAlarmList");
        } else {
            bookmarkFolderList = null;
        }
        String setB = "";
        if (bookmarkFolderList == null) {
            bookmarkList_foldername.setText("북마크가 없습니다");
        } else {
            for (int i = 0; i < bookmarkFolderList.size(); i++) {
                setB += bookmarkFolderList.get(i) + "\n";
            }
            bookmarkList_foldername.setText(setB);
        }
    }


    // userFile에 저장된 user_id 와 access_token 값 가져오기
    private void get_userFile() {
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        user_id = userFile.getString("user_id", null);
        access_token = userFile.getString("access_token", null);
        refresh_token = userFile.getString("refresh_token", null);
    }

}
