package org.techtown.smarket_android.User.Bookmark;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class bookmark_item_list_fragment extends Fragment implements bookmark_item_list_adapter.OnItemClick {

    public static bookmark_item_list_fragment newInstance() {
        return new bookmark_item_list_fragment();
    } // 프래그먼트 생성


    private ViewGroup viewGroup;

    private Spinner bookmark_spinner; // 북마크 스피너
    private ArrayAdapter spinnerAdapter; // 스피너 어댑터
    private List<String> bookmarkFolderList = Arrays.asList("폴더1", "폴더2"); // 북마크 폴더 리스트

    private EditText bookmark_folder_name; // 추가할 북마크 이름

    private RecyclerView recyclerView;// 북마크 아이템 리스트 리사이클러뷰
    private bookmark_item_list_adapter adapter;// 북마크 아이템 리스트 어댑터
    private List<Item> bookmarkItemList;// 북마크 아이템 리스트

    private InputMethodManager imm; // 키보드 설정

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.bookmark_main, container, false);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        set_bookmark_spinner(); // 북마크 스피너 설정
        set_plus_btn(); // 북마크 추가 버튼 설정
        set_trashcan_btn(); // 북마크 삭제 버튼 설정
        set_bookmark_item_list(); // 북마크 리스트 설정

        return viewGroup;
    }

    public List<String> get_bookmark_folder_list(){
        return this.bookmarkFolderList;
    } // 북마크 폴더 리스트 반환

    private void set_bookmark_spinner(){

        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, bookmarkFolderList);

        bookmark_spinner = (Spinner)viewGroup.findViewById(R.id.bookmark_folder);
        bookmark_spinner.setAdapter(spinnerAdapter);
        bookmark_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    } // 북마크 스피너 설정
    private void set_plus_btn(){
        ImageButton plus_btn = viewGroup.findViewById(R.id.plus_btn);
        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folder_add();  // 북마크 폴더 추가 기능
            }
        }); // 플러스 버튼
    } // 북마크 추가 버튼 설정
    private void set_trashcan_btn(){
        ImageButton trashcan_btn = viewGroup.findViewById(R.id.trashcan_btn);
        trashcan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folder_remove();
            }
        });
    } // 북마크 삭제 버튼 설정



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


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setTitle("북마크 폴더 추가");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String folder_name = bookmark_folder_name.getText().toString();

                if(folder_name.equals("")){
                    Toast.makeText(getContext(),"폴더명을 입력해주세요", Toast.LENGTH_LONG).show();
                }
                else if(!folder_name.equals("")){
                    char except_enter[] = folder_name.toCharArray();
                    if (except_enter[except_enter.length - 1] == '\n') {

                        char result_char[] = new char[except_enter.length - 1];
                        System.arraycopy(except_enter, 0, result_char, 0, except_enter.length - 1);
                        folder_name = String.valueOf(result_char);

                    } // 한글 입력 후 엔터시 개행문자 발생하는 오류 처리
                    bookmarkFolderList.add(folder_name); // 북마크 폴더 추가
                    spinnerAdapter.notifyDataSetChanged(); // 어댑터 갱신
                    bookmark_spinner.setSelection(bookmarkFolderList.size()-1); // 새로운 북마크 생성 시 생성된 북마크 페이지
                }


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



    private void set_bookmark_item_list(){
        bookmarkItemList = new ArrayList<>();

        recyclerView = viewGroup.findViewById(R.id.bookmark_itemList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        set_Data();
        adapter = new bookmark_item_list_adapter(getContext(), bookmarkItemList, this);
        recyclerView.setAdapter(adapter);


    } // 북마크 아이템 리스트 설정
    private void set_Data() {
        List<String> item_name = Arrays.asList("국화", "사막", "수국", "해파리", "코알라", "등대", "펭귄");
        List<String> item_value = Arrays.asList("1000","1100","1200","1300","1400","1500","1600");
        List<Integer> itemImage = Arrays.asList(R.drawable.premierball,R.drawable.premierball,R.drawable.premierball,
                R.drawable.premierball,R.drawable.premierball,R.drawable.premierball,R.drawable.premierball);

        for(int i=0; i<item_name.size(); i++){
            Item item = new Item();
            item.setList_item_name(item_name.get(i));
            item.setList_item_value(item_value.get(i));
            item.setItem_image(itemImage.get(i));
            bookmarkItemList.add(item);
        }

    }// 북마크 아이템 리스트 데이터셋

    @Override
    public void onClick(String value) {

    }

    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(bookmark_folder_name.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(bookmark_folder_name.getWindowToken(), 0);
    }// 키보드 입력 후 엔터 입력시 키보드 창 내림
}
