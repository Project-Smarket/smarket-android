package org.techtown.smarket_android.sharedpref;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.techtown.smarket_android.R;

import java.util.ArrayList;
import java.util.List;

public class test2 extends AppCompatActivity {


    private static final String TAG = "get";
    private TextView textView1;
    private static final String SETTINGS_PLAYER_JSON = "settings_item_json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test2);

        textView1 = (TextView)findViewById(R.id.textView2);

        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        List<String> result = getStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON);
        String text = "";
        for (int i = 0; i < result.size(); i++) {
            text = text + result.get(i);
        }
        textView1.setText(text);
    }

    private ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }
}

