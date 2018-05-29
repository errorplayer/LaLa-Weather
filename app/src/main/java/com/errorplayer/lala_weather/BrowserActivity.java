/*
 * Tencent is pleased to support the open source community by making VasSonic available.
 *
 * Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *
 *
 */

package com.errorplayer.lala_weather;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import com.errorplayer.lala_weather.util.SonicTools.SonicJavaScriptInterface;
import com.errorplayer.lala_weather.util.SonicTools.SonicRuntimeImpl;
import com.errorplayer.lala_weather.util.SonicTools.SonicSessionClientImpl;
import com.tencent.sonic.sdk.SonicCacheInterceptor;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicConstants;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;
import com.tencent.sonic.sdk.SonicSessionConnection;
import com.tencent.sonic.sdk.SonicSessionConnectionInterceptor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  A demo browser activity
 *  In this demo there are three modes,
 *  sonic mode: sonic mode means webview loads html by sonic,
 *  offline mode: offline mode means webview loads html from local offline packages,
 *  default mode: default mode means webview loads html in the normal way.
 *
 */

public class BrowserActivity extends Activity {



    public final static String PARAM_URL = "param_url";

    public final static String PARAM_MODE = "param_mode";

    private SonicSession sonicSession;

    private WebView webView;
    private EditText Address_Input;
    private Button Search_Button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String url = intent.getStringExtra(PARAM_URL);
        int mode = intent.getIntExtra(PARAM_MODE, -1);
        if (TextUtils.isEmpty(url) || -1 == mode) {
            finish();
            return;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        // init sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
        }

        SonicSessionClientImpl sonicSessionClient = null;

        // if it's sonic mode , startup sonic session at first time
        if (WeatherActivity.MODE_DEFAULT != mode) { // sonic mode
            SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();



            // create sonic session and run sonic flow
            sonicSession = SonicEngine.getInstance().createSession(url, sessionConfigBuilder.build());
            if (null != sonicSession) {
                sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
            } else {
                // this only happen when a same sonic session is already running,
                // u can comment following codes to feedback as a default mode.
                throw new UnknownError("create session fail!");
            }
        }

        // start init flow ...
        // in the real world, the init flow may cost a long time as startup
        // runtime、init configs....
        setContentView(R.layout.guardian_page);
        webView = (WebView) findViewById(R.id.guardian_page_webview);
        Address_Input = (EditText)findViewById(R.id.url_address_input);
        Search_Button = (Button)findViewById(R.id.search_address_button);
        // init webview
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (sonicSession != null) {
                    sonicSession.getSessionClient().pageFinish(url);
                }
            }

            @TargetApi(21)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (sonicSession != null) {
                    return (WebResourceResponse) sonicSession.getSessionClient().requestResource(url);
                }
                return null;
            }
        });

        WebSettings webSettings = webView.getSettings();

        // add java script interface
        // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
        // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
        // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
        webSettings.setJavaScriptEnabled(false);
        webView.removeJavascriptInterface("searchBoxJavaBridge_");
        //intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
        webView.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient, intent), "sonic");

        // init webview settings
        webSettings.setAllowContentAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);



        // webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient.bindWebView(webView);
            sonicSessionClient.clientReady();
        } else { // default mode
            webView.loadUrl(url);
        }

        Search_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Search_URL = Address_Input.getText().toString();
                webView.loadUrl("http://"+Search_URL);

            }
        });
    }

    @Override
    public void onBackPressed() {


        if (webView != null) {
            //如果h5页面可能返回，跳转到上个页面
            if (webView.canGoBack()) {
                webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK );
                webView.goBack();
            } else {
                //不能返回上个页面，直接finish当前Activity
                finish();
            }
        } else {
            finish();
        }
    }



    @Override
    protected void onDestroy() {
        if (null != sonicSession) {
            sonicSession.destroy();
            sonicSession = null;
        }
        super.onDestroy();
    }


    private static class OfflinePkgSessionConnection extends SonicSessionConnection {

        private final WeakReference<Context> context;

        public OfflinePkgSessionConnection(Context context, SonicSession session, Intent intent) {
            super(session, intent);
            this.context = new WeakReference<Context>(context);
        }

        @Override
        protected int internalConnect() {
            Context ctx = context.get();
            if (null != ctx) {
                try {
                    InputStream offlineHtmlInputStream = ctx.getAssets().open("sonic-demo-index.html");
                    responseStream = new BufferedInputStream(offlineHtmlInputStream);
                    return SonicConstants.ERROR_CODE_SUCCESS;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return SonicConstants.ERROR_CODE_UNKNOWN;
        }

        @Override
        protected BufferedInputStream internalGetResponseStream() {
            return responseStream;
        }

        @Override
        public void disconnect() {
            if (null != responseStream) {
                try {
                    responseStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getResponseCode() {
            return 200;
        }

        @Override
        public Map<String, List<String>> getResponseHeaderFields() {
            return new HashMap<>(0);
        }

        @Override
        public String getResponseHeaderField(String key) {
            return "";
        }
    }
}
