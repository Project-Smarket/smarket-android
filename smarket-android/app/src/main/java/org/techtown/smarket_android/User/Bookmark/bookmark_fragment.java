package org.techtown.smarket_android.User.Bookmark;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.StringRequest;

import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Item;
import org.techtown.smarket_android.searchItemList.RecyclerAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class bookmark_fragment extends Fragment {

    public static bookmark_fragment newInstance() {
        return new bookmark_fragment();
    }


    private ArrayList<String> bookmarkList;
    private ArrayAdapter spinnerAdapter;
    private ViewGroup viewGroup;
    private Spinner bookmark_spinner;
    private EditText bookmark_folder_name;
    private InputMethodManager imm;

    private RecyclerView recyclerView;
    private Context context;
    private bookmark_adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.bookmark_main, container, false);

        bookmarkList = new ArrayList<>();

        bookmarkList.add("철수");
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                bookmarkList);

        bookmark_spinner = (Spinner)viewGroup.findViewById(R.id.bookmark_folder);
        bookmark_spinner.setAdapter(spinnerAdapter);
        bookmark_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(),bookmarkList.get(i)+"가 선택되었습니다.",
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ImageButton plus_btn = viewGroup.findViewById(R.id.plus_btn);
        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folder_add();  // 북마크 폴더 추가 기능
            }
        }); // 플러스 버튼

        ImageButton trashcan_btn = viewGroup.findViewById(R.id.trashcan_btn);
        trashcan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folder_remove();
            }
        });


        recyclerView = viewGroup.findViewById(R.id.bookmark_itemList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new bookmark_adapter();
        recyclerView.setAdapter(adapter);
        get_Dataset();
        return viewGroup;
    }

    private void folder_add(){
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.bookmark_plus_dialog, null);
        bookmark_folder_name = dialogView.findViewById(R.id.bookmark_folder_name);
        bookmark_folder_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    hideKeyboard();
                }
                return false;
            }
        });
        /*bookmark_folder_name.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Toast.makeText(getContext(), "gg", Toast.LENGTH_LONG).show();
                }else{};
                return false;
            }
        });*/

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setTitle("북마크 폴더 추가");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String folder_name = bookmark_folder_name.getText().toString();

                char except_enter[] = folder_name.toCharArray(); // 한글 입력 후 엔터시 개행문자 발생하는 오류 처리
                char result_char[] = new char[except_enter.length-1];
                if(except_enter[except_enter.length-1] == '\n'){
                    System.arraycopy(except_enter, 0, result_char, 0, except_enter.length-1);
                    folder_name = String.valueOf(result_char);
                }

                bookmarkList.add(folder_name); // 북마크 폴더 추가
                spinnerAdapter.notifyDataSetChanged(); // 어댑터 갱신
                bookmark_spinner.setSelection(bookmarkList.size()-1); // 새로운 북마크 생성 시 생성된 북마크 페이지
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); // 다이얼로그 생성시 EditText 활성화 1
        dialog.show();
        if(bookmark_folder_name.requestFocus())
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(bookmark_folder_name, 0); // 다이얼로그 생성시 EditText 활성화 2

    }  // 북마크 폴더 추가 기능

    private void folder_remove(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("북마크 삭제")
                .setMessage("현재 북마크를 삭제 하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), bookmark_spinner.getSelectedItem().toString() + " 북마크가 삭제 되었습니다.", Toast.LENGTH_LONG).show();
                        spinnerAdapter.remove(bookmark_spinner.getSelectedItem());                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create();
        builder.show();

    }// 북마크 폴더 삭제 기능

    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(bookmark_folder_name.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(bookmark_folder_name.getWindowToken(), 0);
    }// 키보드 입력 후 엔터 입력시 키보드 창 내림

    private void get_Dataset() {
        //        items.add(new Item(getResources().getDrawable(R.drawable.premierball), "참깨라면", "1000원"));
//        items.add(new Item(getResources().getDrawable(R.drawable.premierball), "진라면", "2000원"));
//        items.add(new Item(getResources().getDrawable(R.drawable.premierball), "프라포치노", "3200원"));
//        items.add(new Item(getResources().getDrawable(R.drawable.premierball), "아이스커피", "4000원"));

        List<String> item_name = Arrays.asList("국화", "사막", "수국", "해파리", "코알라", "등대", "펭귄");
        List<String> item_value = Arrays.asList("1000","1100","1200","1300","1400","1500","1600");
        List<Integer> itemImage = Arrays.asList(R.drawable.premierball,R.drawable.premierball,R.drawable.premierball,
                R.drawable.premierball,R.drawable.premierball,R.drawable.premierball,R.drawable.premierball);

        for(int i=0; i<item_name.size(); i++){
            Item item = new Item();
            item.setList_item_name(item_name.get(i));
            item.setList_item_value(item_value.get(i));
            item.setItem_image(itemImage.get(i));
            adapter.addItem(item);
        }

        adapter.notifyDataSetChanged();
    }

}
