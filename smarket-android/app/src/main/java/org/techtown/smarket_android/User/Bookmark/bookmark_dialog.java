package org.techtown.smarket_android.User.Bookmark;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import android.widget.TextView;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.R;

import java.util.List;

public class bookmark_dialog extends Dialog {
    private Activity mActivity;
    // 상단 타이틀 내용
    private String title;
    // 버튼 리스너
    // 리스트뷰 어뎁터
    private View.OnClickListener addFolderlistener;

    private bookmark_dialog_adapater bookmarkRecyclerviewAdapter;
    private RecyclerView recyclerView;
    private List<String> bookmarkFolderList;
    // 닫기 버튼
    private TextView addBt;
    // 닫기 버튼
    // 상단 타이틀뷰
    private TextView dialogTitle;
    // 리스트뷰

    /**
     * Custom Dialog
     *
     * @param activity
     * @param title
     * @param list
     * @param addFolderlistener
     */
    public bookmark_dialog(Activity activity, String title, bookmark_dialog_adapater adapter, List<String> list,
                           View.OnClickListener addFolderlistener) {
        //super(activity, android.R.style.Theme_Material);
        super(activity);
        this.mActivity = activity;
        this.title = title;
        this.bookmarkRecyclerviewAdapter = adapter;
        this.bookmarkFolderList = list;
        this.addFolderlistener = addFolderlistener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 메인 layout
        setContentView(R.layout.bookmark_folder_list_dialog);

        addBt = findViewById(R.id.dialog_add_btn);
        dialogTitle = findViewById(R.id.list_title);
        recyclerView = findViewById(R.id.bookmark_recyclerView);
        // 제목 설정
        dialogTitle.setText(title);
        // 리스트뷰 설정
        //listView.setAdapter(listAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(bookmarkRecyclerviewAdapter);


        // 버튼 리스너 설정
        addBt.setOnClickListener(addFolderlistener);
    }


}
