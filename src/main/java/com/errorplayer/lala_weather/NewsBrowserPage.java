package com.errorplayer.lala_weather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class NewsBrowserPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guardian_page);

        final WebView myWebView = (WebView) findViewById(R.id.guardian_page_webview);
        final EditText Address_Input = (EditText)findViewById(R.id.url_address_input);
        Button Search_Button = (Button)findViewById(R.id.search_address_button);
        String TargetURL = "http://www.jd.com";//getIntent().getStringExtra("NewsURL");
        myWebView.getSettings().setSupportZoom(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        //myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.getSettings().setSupportMultipleWindows(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebChromeClient(null);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.loadUrl(TargetURL);
        Search_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Search_URL = Address_Input.getText().toString();
                myWebView.loadUrl("http://"+Search_URL);

            }
        });
    }
}
