package com.example.activitytest.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.activitytest.Activity.LoginActivity;
import com.example.activitytest.R;
import com.google.android.material.appbar.AppBarLayout;

public class BaseActivity extends AppCompatActivity {
    public static String TAG = "BaseActivity";

    private  ForceOfflineReceiver receiver;

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARNING = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;

    public static int level = VERBOSE;

    public static void v(String msg) {
        if (level <= VERBOSE) {
            Log.v(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (level <= DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (level <= INFO) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (level <= WARNING) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (level <= ERROR) {
            Log.e(TAG, msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    /** Toolbar 实例化
     * @param title 标题内容 */
    protected void newInstance_Toolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar); // 设置 toolbar 为 ActionBar 对象

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true); // 设置返回键可点击，此时左上角还没有 ⬅
        actionBar.setDisplayHomeAsUpEnabled(true); // 给左上角加上一个 ⬅
        toolbar.setNavigationOnClickListener(v -> finish()); // ⬅ 的点击事件
    }

    // 活动位于栈顶且准备好与用户交互
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.activitytest.Activity.FORCE_OFFLINE");
        receiver = new ForceOfflineReceiver();
        // 动态注册
        registerReceiver(receiver, intentFilter);
    }

    // 在系统准备去启动或者恢复另一个活动时调用，通常会释放一些占用 CPU 的资源，保存一些关键数据；
    @Override
    protected void onPause() {
        super.onPause();
        // 动态注册要在结束时取消注册
        // 本程序中点击提示框中的OK按钮后会跳转到登陆界面，此时就是强制下线广播职责结束的时候
        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    static class ForceOfflineReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(final Context context, Intent intent) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Warning");
            dialog.setMessage("You are forced to be offline. Please try to login again.");
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK", (dialog1, which) -> {
                ActivityCollector.finishAll(); // 销毁所有活动
                Intent intent1 = new Intent(context, LoginActivity.class);
                context.startActivity(intent1);
            });
            dialog.show();
        }
    }

    /** 更改 ActionBar 中的图标，并且实现打开或关闭 drawer (导航抽屉)的相关操作。*/
    public void setActionBar() {
        // Toolbar 标题栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // 设置 toolbar 为 ActionBar 对象

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        /* 启用ActionBar应用程序图标使其显示并且在点击时可以切换导航抽屉 */
        actionBar.setHomeButtonEnabled(true); // 决定左上角的图标是否可以点击。没有向左的小图标。
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头。
    }

    /** 提示 Toast */
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}