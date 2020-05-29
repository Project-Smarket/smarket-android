package org.techtown.smarket_android.User.recent;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetRetrofit {
    private static NetRetrofit ourInstance = new NetRetrofit();

    public static NetRetrofit getInstance() {
        return ourInstance;
    }

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
            .addConverterFactory(GsonConverterFactory.create()) // 파싱등록
            .build();

    RetrofitService service = retrofit.create(RetrofitService.class);

    public RetrofitService getService() {
        return service;
    }
}
