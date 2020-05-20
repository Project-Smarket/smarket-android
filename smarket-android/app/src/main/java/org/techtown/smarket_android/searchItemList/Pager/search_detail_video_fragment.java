package org.techtown.smarket_android.searchItemList.Pager;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class search_detail_video_fragment extends Fragment {

    private String txt;

    public search_detail_video_fragment(){

    }

    @Nullable
    @Override
    public ViewGroup onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.search_detail_video_fragment_layout, container, false);

        getBundle();

        goYoutube();

        return viewGroup;
    }

    public void getBundle(){
        if(getArguments()!=null){
            txt = getArguments().getString("txt");
        } else {
            Log.d(TAG, "dod : is not receive bundle");
        }
    }

    private void goYoutube(){
        Bundle bundle = new Bundle();
        bundle.putString("txt",txt);
//        youtubeFragment yFragment = new youtubeFragment();
//        yFragment.setArguments(bundle);
//        FragmentManager manager = getFragmentManager();
//        manager.beginTransaction().replace(R.id.detailVideoFragment, yFragment).addToBackStack(null).commit();
    }

}
