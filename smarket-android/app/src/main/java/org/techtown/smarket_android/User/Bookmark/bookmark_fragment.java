package org.techtown.smarket_android.User.Bookmark;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.StringRequest;

import org.techtown.smarket_android.R;

import java.util.ArrayList;


public class bookmark_fragment extends Fragment {

    public static bookmark_fragment newInstance() {
        return new bookmark_fragment();
    }


    ArrayList<String> bookmarkList;
    ArrayAdapter spinnerAdapter;
    ViewGroup viewGroup;
    Spinner bookmark_spinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.bookmark_main, container, false);

        bookmarkList = new ArrayList<>();
        bookmarkList.add("철수");


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

        return viewGroup;
    }

    private void folder_add(){
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.bookmark_plus_dialog, null);
        final EditText bookmark_folder_name = dialogView.findViewById(R.id.bookmark_folder_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setTitle("북마크 폴더 추가");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String folder_name = bookmark_folder_name.getText().toString();
                bookmarkList.add(folder_name); // 북마크 폴더 추가
                spinnerAdapter.notifyDataSetChanged(); // 어댑터 갱신
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }  // 북마크 폴더 추가 기능

    private void folder_remove(){

        LayoutInflater inflater = getLayoutInflater();
        final View dialoView = inflater.inflate(R.layout.bookmark_trashcan_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialoView);
        builder.setTitle("북마크 폴더 삭제");

        final String data[] = bookmarkList.toArray(new String[bookmarkList.size()]);
        final boolean checked[] = new boolean[bookmarkList.size()];
        

        builder.setMultiChoiceItems(data, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checked[which] = isChecked;
            }
        });
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }
}
