package org.techtown.smarket_android.User.recent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.Class.BookmarkAlarm;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    private Button remove_bookmarkList;
    private TextView bookmarkList_foldername;

    private SharedPreferences userFile;
    private String user_id;
    private String access_token;
    private String refresh_token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        get_userFile();
        viewGroup = (ViewGroup) inflater.inflate(R.layout.recent_main, container, false);
        remove_bookmarkAlarmList = viewGroup.findViewById(R.id.remove_bookmarkalarm);
        bookmarkAlarmList_userId = viewGroup.findViewById(R.id.check_bookmarkalarm);
        bookmarkAlarmList_id = viewGroup.findViewById(R.id.check_bookmarkalarm2);
        bookmarkAlarmList_foldername = viewGroup.findViewById(R.id.check_bookmarkalarm3);
        bookmarkAlarmList_price = viewGroup.findViewById(R.id.check_bookmarkalarm4);
        bookmarkList_foldername = viewGroup.findViewById(R.id.check_bookmarklist);
        remove_bookmarkList = viewGroup.findViewById(R.id.remove_bookmarkList);

        set_bookmarkAlarmList();
        set_bookmarkFolderList();

        remove_bookmarkAlarmList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove();
                set_bookmarkAlarmList();
            }
        });

        remove_bookmarkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove_folderlist();
                set_bookmarkFolderList();
            }
        });
        return viewGroup;
    }

    private void remove() {
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("bookmarkAlarmList", null);
        editor.commit();
    }

    private void remove_folderlist(){
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("bookmarkFolderList", null);
        editor.commit();
    }

    private void set_bookmarkAlarmList() {

        List<BookmarkAlarm> bookmarks;
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        if (userFile.getString("bookmarkAlarmList", null) != null) {
            String myBookmarks = userFile.getString("bookmarkAlarmList", null);
            Type listType = new TypeToken<ArrayList<BookmarkAlarm>>() {
            }.getType();
            bookmarks = new GsonBuilder().create().fromJson(myBookmarks, listType);
            Log.d("Get myBookmarks", "bookmarkAlarmList: Complete Getting bookmarkAlarmList");
        } else {
            bookmarks = null;
        }

        String set1 = "";
        String set2 = "";
        String set3 = "";
        String set4 = "";
        if (bookmarks == null) {
            bookmarkAlarmList_id.setText("북마크가 없습니다");
            bookmarkAlarmList_foldername.setText("북마크가 없습니다");
            bookmarkAlarmList_userId.setText("북마크가 없습니다");
            bookmarkAlarmList_price.setText("북마크가 없습니다");
        } else {
            for (int i = 0; i < bookmarks.size(); i++) {
                set1 += bookmarks.get(i).getBookmark_id() + "\n";
            }
            bookmarkAlarmList_id.setText(set1);

            for (int i = 0; i < bookmarks.size(); i++) {
                set2 += bookmarks.get(i).getFolder_name() + "\n";
            }
            bookmarkAlarmList_foldername.setText(set2);

            for (int i = 0; i < bookmarks.size(); i++) {
                set3 += bookmarks.get(i).getUser_id() + "\n";
            }
            bookmarkAlarmList_userId.setText(set3);

            for (int i = 0; i < bookmarks.size(); i++) {
                set4 += bookmarks.get(i).getBookmark_price() + "\n";
            }
            bookmarkAlarmList_price.setText(set4);

        }
    }

    private void set_bookmarkFolderList(){
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
