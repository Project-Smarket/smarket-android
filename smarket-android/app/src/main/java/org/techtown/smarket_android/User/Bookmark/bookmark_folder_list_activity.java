package org.techtown.smarket_android.User.Bookmark;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.techtown.smarket_android.MainActivity;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Item;
import org.techtown.smarket_android.searchItemList.RecyclerAdapter;
import org.techtown.smarket_android.searchItemList.RecyclerDecoration;
import org.techtown.smarket_android.searchItemList.searchdetail_fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.view.KeyEvent.KEYCODE_ENTER;

public class bookmark_folder_list_activity extends AppCompatActivity {

    private static final String TAG = "";
    private InputMethodManager imm;
    private RecyclerDecoration spaceDecoration = new RecyclerDecoration(40); // 리사이클러뷰 데코레이션

    private TextView add_folder_btn; // + 새 폴더 추가 버튼
    private EditText bookmark_folder_name; // 추가할 폴더 이름 입렵텍스트

    private RecyclerView recyclerView; // 북마크 폴더 리스트 리사이클러뷰
    private bookmark_folder_list_adapter adapter; // 북마크 폴더 리스트 어댑터

    private List<String> bookmarkFolderList = new ArrayList<>(); // 북마크 폴더 리스트

    private static final String SETTINGS_BOOKMARK_JSON = "settings_bookmark_json";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_folder_list);
        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        set_add_folder_btn();

        bookmark_folder_list_set();


    }

    private void set_add_folder_btn(){
        add_folder_btn = findViewById(R.id.add_folder_btn);
        add_folder_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folder_add();
            }
        });
    } // 폴더 추가 버튼 설정
    private void folder_add() {
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.bookmark_plus_dialog, null);
        bookmark_folder_name = dialogView.findViewById(R.id.bookmark_folder_name);
        bookmark_folder_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_ENTER) {
                    hideKeyboard();
                }
                return false;
            }
        });

        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("북마크 폴더 추가");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String folder_name = bookmark_folder_name.getText().toString();

                if(folder_name.equals("")){
                    Toast.makeText(getApplicationContext(), "폴더명을 입력해주세요", Toast.LENGTH_LONG).show();
                }
                else if(!folder_name.equals("")){
                    char except_enter[] = folder_name.toCharArray();
                    if (except_enter[except_enter.length - 1] == '\n') {

                        char result_char[] = new char[except_enter.length - 1];
                        System.arraycopy(except_enter, 0, result_char, 0, except_enter.length - 1);
                        folder_name = String.valueOf(result_char);

                    } // 한글 입력 후 엔터시 개행문자 발생하는 오류 처리
                    adapter.add_folder(folder_name);
                    adapter.notifyDataSetChanged();
                    updateBookmarkFolderList(getApplicationContext(), SETTINGS_BOOKMARK_JSON, bookmarkFolderList);
                    //edit_bookmarkFolderList();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); // 다이얼로그 생성시 EditText 활성화 1
        dialog.show();
        if (bookmark_folder_name.requestFocus())
            ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(bookmark_folder_name, 0); // 다이얼로그 생성시 EditText 활성화 2


    } // 폴더 추가 버튼 기능

    private void bookmark_folder_list_set(){
        recyclerView = findViewById(R.id.folder_list);
        recyclerView.addItemDecoration(spaceDecoration);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, 1)); // 리사이클러뷰 구분선 추가
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        set_bookmarkFolderList(); // 북마크 폴더 리스트 셋
        adapter = new bookmark_folder_list_adapter(bookmarkFolderList);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new bookmark_folder_list_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                onBackPressed();
            }
        });
    } // 북마크 폴더 리스트 설정

    private void set_bookmarkFolderList() {
        List<String> result = getStringArrayPref(getApplicationContext(), SETTINGS_BOOKMARK_JSON); // 폴더 리스트 데이터 가져오기
        for (int i = 0; i < result.size(); i++) {
            String folder = result.get(i);
            bookmarkFolderList.add(folder);
        }

    } // 북마크 폴더 리스트 데이터 설정
    private ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> folders = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String folder = a.optString(i);
                    folders.add(folder);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return folders;
    } // 폴더 리스트 데이터 가져오기

    /*private void edit_bookmarkFolderList(){
        SharedPreferences test = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = test.edit();
        editor.clear();

        List<String> edited_bookmarkFolderList = this.bookmarkFolderList;

        setStringArrayPref(getApplicationContext(), SETTINGS_BOOKMARK_JSON, edited_bookmarkFolderList);

    }*/

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
    } // 북마크 폴더 업데이트

    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(bookmark_folder_name.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(bookmark_folder_name.getWindowToken(), 0);
    }// 키보드 입력 후 엔터 입력시 키보드 창 내림
}
