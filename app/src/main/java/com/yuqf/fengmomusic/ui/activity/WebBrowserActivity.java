package com.yuqf.fengmomusic.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.yuqf.fengmomusic.R;
import com.yuqf.fengmomusic.base.BaseActivity;
import com.yuqf.fengmomusic.utils.Global;

public class WebBrowserActivity extends BaseActivity {

    private ProgressBar progressBar;
    private WebView webView;
    private final int VISIBILITY_FLAG = 110;
    private final int GONE_FLAG = 120;

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (progressBar != null) {
                switch (msg.what) {
                    case VISIBILITY_FLAG:
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case GONE_FLAG:
                        progressBar.setVisibility(View.GONE);
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_browser);

        initTopBars();
        showToolBar();
        setToolbarHomeAsUp();

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setProgress(0);
        webView = (WebView) findViewById(R.id.web_view);
        progressBar.setVisibility(View.GONE);

        String url = "";
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra(Global.INTENT_TITLE_KEY);
            url = intent.getStringExtra(Global.INTENT_CONTENT_KEY);
            setToolbarTitle(title);
        }

        if (!TextUtils.isEmpty(url)) {
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            });
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    progressBar.setProgress(newProgress);
                    if (newProgress >= 100) {
                        handler.sendEmptyMessageDelayed(GONE_FLAG, 300);
                    } else {
                        if (progressBar.getVisibility() != View.VISIBLE) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                    super.onProgressChanged(view, newProgress);
                }
            });
            webView.loadUrl(url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
