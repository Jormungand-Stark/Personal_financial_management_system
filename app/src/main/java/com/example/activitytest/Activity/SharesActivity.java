package com.example.activitytest.Activity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.example.activitytest.R;
import com.example.activitytest.Util.BaseActivity;

/** 股票信息 */
public class SharesActivity extends BaseActivity {
    private static final String TAG = "SharesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.shares_layout));

        /* Toolbar 实例化 */
        newInstance_Toolbar("股票信息");

        Log.e(TAG, "onCreate: begin");
        WebView webView = findViewById(R.id.web_view);
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true); // 是否使用自定义的窗口大小
        settings.setSupportZoom(true); // 支持手势缩放
        settings.setBuiltInZoomControls(true); // 是否应使用 Android 内置的缩放机制
        settings.setDisplayZoomControls(false); // 是否应显示屏幕缩放控件
        settings.setLoadWithOverviewMode(true); // 过宽时适配屏幕
        settings.setJavaScriptEnabled(true); // 使WebView支持JavaScript脚本
        webView.setWebViewClient(new WebViewClient()); // 用当前WebView显示网页而不是浏览器

        Log.e(TAG, "onCreate: wait");
        // webView.loadUrl("http://quotes.money.163.com/0000001.html");
        // webView.loadUrl("http://q.10jqka.com.cn/"); // 同花顺
        webView.loadUrl("http://quotes.money.163.com/usstock/NASDAQ.html");
    }
}

