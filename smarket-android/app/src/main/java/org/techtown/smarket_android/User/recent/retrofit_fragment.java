package org.techtown.smarket_android.User.recent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.Hotdeal.hotdealViewPagerAdapter;
import org.techtown.smarket_android.R;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class retrofit_fragment extends Fragment {

    public static retrofit_fragment newInstance() {
        return new retrofit_fragment();
    }
    private ViewGroup viewGroup;

    String TAG = "retrofit";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.retrofit_layout, container, false);

        userLogin userLogin = new userLogin("smarket", "123456");
        RetrofitService retrofitService = new NetRetrofit().getService();

        retrofitService.do_login(userLogin).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    Log.d(TAG, "onResponse: "+ response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        return viewGroup;
    }
}

