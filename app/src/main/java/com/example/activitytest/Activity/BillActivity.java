package com.example.activitytest.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.activitytest.R;

/* 记账功能子系统主要有用户信息管理、收支管理、收入支出统计管理、借贷管理、预算管理和类别管理等六个模块。*/

public class BillActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_layout);
    }
}