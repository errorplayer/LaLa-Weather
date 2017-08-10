package com.errorplayer.lala_weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class NewsBrowserPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guardian_page);

        WebView myWebView = (WebView) findViewById(R.id.guardian_page_webview);
        String TargetURL = getIntent().getStringExtra("NewsURL");
        myWebView.loadUrl(TargetURL);
    }
}
