package org.techtown.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RetrofitClient retrofitClient = new RetrofitClient();
        TestService service = retrofitClient.getApiService();
        Call<reqres> request = service.getTest();
        request.enqueue(new Callback<reqres>() {
            @Override
            public void onResponse(Call<reqres> call, Response<reqres> response) {
                //Log.d("RETROFIT", "onResponse: "+ response.body().toString());
                reqres reqres = response.body();
                Log.d("RETROFIT", "onResponse: " + reqres.page);
            }

            @Override
            public void onFailure(Call<reqres> call, Throwable t) {

            }
        });
    }
}
