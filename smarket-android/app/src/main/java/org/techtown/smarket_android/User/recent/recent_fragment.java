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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Bookmark;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class recent_fragment extends Fragment {
    public static recent_fragment newInstance() {
        return new recent_fragment();
    }

    private ViewGroup viewGroup;

    private Button button;
    private TextView text;

    private SharedPreferences userFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.recent_main, container, false);
        button = viewGroup.findViewById(R.id.remove_bookmark);
        text = viewGroup.findViewById(R.id.check_bookmark);

        set_text();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove();
                set_text();
            }
        });
        return viewGroup;
    }

    private void remove() {
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userFile.edit();
        editor.putString("myBookmarks", null);
        editor.commit();
    }

    private void set_text() {

        List<Bookmark> bookmarks;
        userFile = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        if (userFile.getString("myBookmarks", null) != null) {
            String myBookmarks = userFile.getString("myBookmarks", null);
            Type listType = new TypeToken<ArrayList<Bookmark>>() {
            }.getType();
            bookmarks = new GsonBuilder().create().fromJson(myBookmarks, listType);
            Log.d("Get myBookmarks", "myBookmarks: Complete Getting myBookmarks");
        } else {
            bookmarks = null;
        }

        String setT = "";

        if (bookmarks == null) {
            text.setText("북마크가 없습니다");
        } else {
            for (int i = 0; i < bookmarks.size(); i++) {
                setT += bookmarks.get(i).getBookmark_name() + "\n";
            }
            text.setText(setT);
        }
    }
}
