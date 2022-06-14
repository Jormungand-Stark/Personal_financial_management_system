package com.example.activitytest.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import com.example.activitytest.Listener.DialogWarning;
import com.example.activitytest.R;
import com.example.activitytest.Util.BaseActivity;
import com.example.activitytest.Util.GetTime;

import java.net.URISyntaxException;

/** 登陆界面 */
public class LoginActivity extends BaseActivity {
    private EditText accountEdit;
    private EditText passwordEdit;
    private CheckBox rememberPass;
    private ImageView imageView;
    private TextView textView;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        accountEdit = findViewById(R.id.account);
        passwordEdit = findViewById(R.id.password);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.greetings);
        init_greetings(); // 根据时间初始化问候语和背景图

        rememberPass = findViewById(R.id.remember_pass);

        boolean isRemember = pref.getBoolean("remember_password", false);
        if(isRemember){
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberPass.setChecked(true);
        }

        Button login = findViewById(R.id.button_sign_in);
        login.setOnClickListener((View v)->{
            String account = accountEdit.getText().toString();
            String password = passwordEdit.getText().toString();

            if (account.isEmpty() || password.isEmpty()){
                // 不必清空账号密码文本框
                DialogWarning.Warning("用户名或密码不应为空", LoginActivity.this);
            }
            else {
                if(pref.contains(account)){ // 用户名存在
                    String TruePW = pref.getString(account, "");
                    if (TruePW.equals(password)) { // 对应的密码正确
                        if(rememberPass.isChecked()){ // 检查复选框是否被选中
                            editor.putBoolean("remember_password", true);
                            editor.putString("account", account);
                            editor.putString("password", password);
                        }
                        else {
                            editor.putBoolean("remember_password", false);
                            editor.putString("account", "");
                            editor.putString("password", "");
                        }
                        editor.apply();

                        // 成功登陆，跳转界面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else { // 对应密码不正确
                        ClearEditText("密码输入错误");
                    }
                }
                else { // 用户名不存在
                    ClearEditText("用户不存在");
                }
            }
        });

        // 注册
        TextView TextView_sign_up = findViewById(R.id.TextView_sign_up);
        TextView_sign_up.setOnClickListener(v->{
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void ClearEditText(String s) {
        accountEdit.setText("");
        passwordEdit.setText("");
        DialogWarning.Warning(s, LoginActivity.this);
    }

    // 从注册页面跳回
    @Override
    protected void onRestart() {
        super.onRestart();
        init_greetings(); // 根据时间初始化问候语和背景图

        // 注册成功直接填入注册的账号密码
        if (RegisterActivity.uri != null) {
            Intent intentGet = null;
            try {
                intentGet = Intent.parseUri(RegisterActivity.uri, Intent.URI_INTENT_SCHEME);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            assert intentGet != null;

            String accountGet = intentGet.getStringExtra("account");
            String passwordGet = intentGet.getStringExtra("password");
            accountEdit.setText(accountGet);
            passwordEdit.setText(passwordGet);
        }
    }

    // 根据时间更改问候语和背景图
    private void init_greetings() {
        int nowTime = GetTime.NowTime();
        if(nowTime >= 18){
            imageView.setImageResource(R.drawable.good_night_img);
            textView.setText("休息一下吧！今天辛苦啦~");
        }
        else {
            imageView.setImageResource(R.drawable.good_morning_img);
            textView.setText("今天也是元气满满的一天！");
        }
    }
}

/*
* public class LoginActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    int count = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 请求窗口功能
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 首次调用setContentView()或getDecorView()必须先调用setFlags()
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_layout);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        imageView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeTop() {
            }

            public void onSwipeRight() {
                if (count == 0) {
                    imageView.setImageResource(R.drawable.good_night_img);
                    textView.setText("Night");
                    count = 1;
                } else {
                    imageView.setImageResource(R.drawable.good_morning_img);
                    textView.setText("Morning");
                    count = 0;
                }
            }

            public void onSwipeLeft() {
                if (count == 0) {
                    imageView.setImageResource(R.drawable.good_night_img);
                    textView.setText("Night");
                    count = 1;
                } else {
                    imageView.setImageResource(R.drawable.good_morning_img);
                    textView.setText("Morning");
                    count = 0;
                }
            }

            public void onSwipeBottom() {
            }

        });
    }
}
*/