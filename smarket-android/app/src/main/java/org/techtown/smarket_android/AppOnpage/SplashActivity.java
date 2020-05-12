package org.techtown.smarket_android.AppOnpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.techtown.smarket_android.MainNavigation.MainNavigationActivity;
import org.techtown.smarket_android.R;

public class SplashActivity extends AppCompatActivity {

    public static Context context_push; //다른 액티비티나 클래스에서 접근할 수 있도록 컨텍스트를 만들어준다.
    public RequestQueue queue;
    public String pushToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.apponpage_splash);

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 3000); // 1초 후에 hd handler 실행  3000ms = 3초

        context_push = this; //다른 액티비티나 클래스에서 접근할 수 있도록 컨텍스트를 만들어준다.
        queue = Volley.newRequestQueue(this);
        createToken();
    }

    private class splashhandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), MainNavigationActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
            SplashActivity.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }

    public void createToken() {
        //파이어베이스 API에서 현재 토큰을 검색하는 메소드

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this,

                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {

                        String newToken = instanceIdResult.getToken();
                        pushToken = instanceIdResult.getToken();
                        Log.d( "PUSH_TOKEN", "새 토큰 : " + newToken );

                    }
                }
        );

        MyFirebaseMessagingService fms = new MyFirebaseMessagingService();
        fms.sendRegistrationToServer(pushToken);
    }
}
