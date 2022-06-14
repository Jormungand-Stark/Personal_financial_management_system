package com.example.activitytest.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.activitytest.R;
import com.example.activitytest.Util.GetTime;

/** 注册界面 */
public class RegisterActivity extends AppCompatActivity {
    public  static String uri;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText accountEdit;
    private EditText passwordEdit;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        accountEdit = findViewById(R.id.account);
        passwordEdit = findViewById(R.id.password);
        imageView = findViewById(R.id.imageView);
        init_Background(); // 根据时间更改背景图

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();

        Button login = findViewById(R.id.button_sign_up);
        login.setOnClickListener((View v)->{
            // 文本框中用户填入的账号密码
            String account = accountEdit.getText().toString();
            String password = passwordEdit.getText().toString();

            // 用户已存在
            if (pref.contains(account)){
                accountEdit.setText("");
                passwordEdit.setText("");
                EditTextWarning("该用户名已存在");
            } else if (account.isEmpty() || password.isEmpty()) {
                EditTextWarning("用户名或密码不应为空");
            } else if (account.length() > 10 || password.length() > 10) {
                accountEdit.setText("");
                passwordEdit.setText("");
                EditTextWarning("用户名或密码的长度不应超过10位");
            }else {
                editor = pref.edit();
                editor.putString(account, password);
                editor.apply();

                Toast.makeText(this, "注册成功", Toast.LENGTH_LONG).show();
                Intent intent= new Intent(this, LoginActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("password", password);
                uri = intent.toUri(Intent.URI_INTENT_SCHEME);
                // Log.e(TAG, "onCreate: uri: " + uri);
                startActivity(intent);
                finish();
            }
        });
    }

    private void EditTextWarning (String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Warning");
        builder.setMessage(s);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    // 根据时间更改背景图
    private void init_Background() {
        int nowTime = GetTime.NowTime();
        // Log.e(TAG, "nowTime: " + nowTime);
        if(nowTime >= 18){
            imageView.setImageResource(R.drawable.good_night_img);
        }
        else {
            imageView.setImageResource(R.drawable.good_morning_img);
        }
    }
}