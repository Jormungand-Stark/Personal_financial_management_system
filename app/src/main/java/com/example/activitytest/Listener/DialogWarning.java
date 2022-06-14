package com.example.activitytest.Listener;

import android.app.AlertDialog;
import android.content.Context;

import com.example.activitytest.R;

public abstract class DialogWarning {
    public static void Warning(String s, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle("Warning");
        builder.setMessage(s);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }
}
