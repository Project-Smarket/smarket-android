package org.techtown.smarket_android.Hotdeal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.Volley;

import org.techtown.smarket_android.R;

public class hotdeal_webView extends AppCompatActivity {

    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hotdeal_webview);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String mUrl = "";

        // 루리웹 mobile 웹페이지
        if(url.contains("https://bbs.ruliweb"))
            mUrl = url.replace("https://bbs.ruliweb", "https://m.ruliweb");
        // 뽐뿌 mobile 웹페이지
        else if(url.contains("http://www.ppomppu"))
            mUrl = url;

        Log.d("GET", "getUrl: " + mUrl);
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(mUrl);
    }

    public void onBackPressed(){
        if(webView.canGoBack()) webView.goBack();
        else finish();
    }

}
