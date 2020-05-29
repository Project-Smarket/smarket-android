package org.techtown.smarket_android.User.recent;

import com.firebase.ui.auth.data.model.User;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitService {
    @POST("api/auth/login")
    Call<ResponseBody> do_login(@Body userLogin userLogin);


}
