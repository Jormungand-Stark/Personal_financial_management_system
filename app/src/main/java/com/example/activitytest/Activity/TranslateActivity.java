package com.example.activitytest.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.activitytest.Listener.DialogWarning;
import com.example.activitytest.R;
import com.example.activitytest.Translate.TransApi;
import com.example.activitytest.Util.BaseActivity;

import java.util.Objects;

/** 中英互译 */
public class TranslateActivity extends BaseActivity {
    private static final String TAG = "TranslateActivity";

    private static boolean flag = true; // true表英译中，false表中译英
    private static final String APP_ID = "20220329001148587";
    private static final String SECURITY_KEY = "ps_k4ImcKEWzYSIrJjks";
    private static final TransApi api = new TransApi(APP_ID, SECURITY_KEY);

    private TextView to_translate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_layout);

        /* Toolbar 实例化 */
        newInstance_Toolbar("中英互译");

        TextView from_language = findViewById(R.id.from_language);
        TextView to_language = findViewById(R.id.to_language);

        Button change_language = findViewById(R.id.change_language);
        change_language.setOnClickListener(v-> {
            flag = !flag;
            if (flag) { // 英译中
                from_language.setText("英文");
                to_language.setText("中文");
            }
            else { // 中译英
                from_language.setText("中文");
                to_language.setText("英文");
            }
        });

        EditText from_translate = findViewById(R.id.from_translate);
        to_translate = findViewById(R.id.to_translate);

        Button begin_translate = findViewById(R.id.begin_translate);
        begin_translate.setOnClickListener(v->{
            String src_String = from_translate.getText().toString();
            if (src_String.isEmpty()){ // 待翻译内容为空
                DialogWarning.Warning("未输入待翻译内容", this);
            }
            else { // 非空
                if (flag) { // 英译中
                    translate(src_String, "en", "zh");
                }
                else { // 中译英
                    translate(src_String, "zh", "en");
                }
            }
        });
    }

    private void translate (String query, String from, String to) {
        new Thread(() -> { // 子线程执行逻辑，主线程负责UI
            String res = api.getTransResult(query, from, to);
            JSONObject json = (JSONObject) JSON.parse(res);
            Log.e(TAG, "translate: " + json);
            JSONArray jsonArray =
                    JSON.parseArray(Objects.requireNonNull(json.get("trans_result")).toString());
            Log.e(TAG, "translate: " + jsonArray);
            StringBuilder dst = new StringBuilder();
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                dst.append("\n").append(Objects.requireNonNull(jsonObject.get("dst")));
            }
            runOnUiThread(() -> to_translate.setText(dst.toString()));
            Log.e(TAG, "translate: setText over");
        }).start();
    }
}

