package com.example.activitytest.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.example.activitytest.Database.DatabaseHelper;
import com.example.activitytest.R;
import com.example.activitytest.Util.BaseActivity;
import com.example.activitytest.Util.Memo;

// 备忘录内容活动
public class MemoContentActivity extends BaseActivity {
    private static final String TAG = "MemoCreateActivity";

    private EditText titleEdit;
    private EditText contentEdit;

    private String mode; // 判断以何种模式打开了本活动
    private String initial_title; // 初始备忘标题
    private String initial_content; // 初始备忘内容
    private int ind; // 数组下标
    private String id; // 数据库主键

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate: begin");
        super.onCreate(savedInstanceState);
        setContentView((R.layout.memo_content));

        // 实例化控件
        titleEdit = findViewById(R.id.memo_title_in_content);
        titleEdit.setOnClickListener(v-> titleEdit.requestFocus());
        contentEdit = findViewById(R.id.memo_content_in_content);
        contentEdit.setOnClickListener(v-> contentEdit.requestFocus());

        // 接收 Intent 携带的消息
        mode = getIntent().getStringExtra("mode");
        if (mode.equals("create")){ // 以 create 模式被唤醒
            /* Toolbar 实例化 */
            init_Toolbar("新建备忘内容");

            titleEdit.setHint("标题");
            contentEdit.setHint("内容");
            Log.e(TAG, "MemoContentActivity 启动模式为 create");
        }
        else if (mode.equals("update")){ // 以 update 模式被唤醒
            /* Toolbar 实例化 */
            init_Toolbar("备忘内容");

            ind = getIntent().getIntExtra("memo_ind", -1);
            id = getIntent().getStringExtra("memo_id");
            titleEdit.setText(getIntent().getStringExtra("memo_title"));
            contentEdit.setText(getIntent().getStringExtra("memo_content"));
            Log.e(TAG, "MemoContentActivity 启动模式为 update");
        }

        initial_title = titleEdit.getText().toString();
        initial_content = contentEdit.getText().toString();

        Log.e(TAG, "onCreate: over");
    }

    private void init_Toolbar(String title){
        Toolbar toolbar = findViewById(R.id.memo_content_toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar); // 设置 toolbar 为 ActionBar 对象

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true); // 设置返回键可点击，此时左上角还没有 ⬅
        actionBar.setDisplayHomeAsUpEnabled(true); // 给左上角加上一个 ⬅
        // ⬅ 的点击事件
        toolbar.setNavigationOnClickListener(v -> {
            if (isCHANGE()) {
                Dialog();
            }
            else {
                finish();
            }
        });
    }

    private void Dialog(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle("Warning");
        builder.setMessage("是否保存更改？");
        builder.setCancelable(false);
        builder.setPositiveButton("保存", (dialogInterface, i) -> commit());
        builder.setNegativeButton("不保存", (dialogInterface, i) -> finish());
        builder.create().show();
    }

    /** inflate menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "onCreateOptionsMenu: begin");
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_memo_content, menu);
        Log.e(TAG, "onCreateOptionsMenu: over");
        return true;
    }

    /** 处理右侧菜单（Menu）点击事件 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "onOptionsItemSelected: begin");
        // 通过调用 getItemId() 方法来识别菜单项，该方法会返回菜单项的唯一ID
        // (由菜单资源中的 android:id 属性定义，或通过提供给 add() 方法的整型数定义)。
        switch (item.getItemId()) {
            case R.id.commit_content:
                if (isCHANGE()) {
                    commit();
                }
                Hide_Focus();
                Log.e(TAG, "onOptionsItemSelected: commit_content over");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** @return 用户有修改则返回 true，无修改则返回 false */
    private boolean isCHANGE (){
        String new_title = titleEdit.getText().toString();
        String new_content = contentEdit.getText().toString();
        Log.e(TAG, "isCHANGE initial_title: " + initial_title);
        Log.e(TAG, "isCHANGE new_title: " + new_title);
        Log.e(TAG, "isCHANGE initial_content: " + initial_content);
        Log.e(TAG, "isCHANGE new_content: " + new_content);
        return !new_title.equals(initial_title) || !new_content.equals(initial_content);
    }

    /** 用户对初始内容有修改并且选择保存修改 */
    private void commit(){
        String title = titleEdit.getText().toString();
        String content = contentEdit.getText().toString();
        // trim():移除字符串两侧的空白字符或其他预定义字符
        String tmpTitle = title.trim();
        String tmpContent = content.trim();

        // 移除空白字符后 备忘标题 或 备忘内容 任意一个不空
        if (!tmpTitle.isEmpty() || !tmpContent.isEmpty()) {
            // 标题为空内容不空则将内容同时作为标题
            if (tmpTitle.isEmpty()) {
                tmpTitle = tmpContent;
            }
            Memo memo = new Memo(tmpTitle, content);
            if (mode.equals("create")){
                // 存储时，标题需要去空格，内容不需要
                DatabaseHelper.add_memoDB(tmpTitle, content); // 新增的Memo条目信息加入数据库
                MemoTitleActivity.MemoList.add(0, memo); // 新增的Memo条目信息加入MemoList数组
                // 更新在数组中的下标信息
                MemoTitleActivity.refresh_MemoList();
                Log.e(TAG, "备忘内容界面以 create 模式结束");
            }
            else if (mode.equals("update")){
                // 修改后的Memo条目信息更新到数据库中
                DatabaseHelper.update_memoDB(id, tmpTitle, content);
                memo.setId(id); // 为修改后的临时对象赋予修改前对象的id
                // 先删除 MemoList 数组中存在的修改前的 Memo 条目信息
                MemoTitleActivity.MemoList.remove(ind);
                // 然后将修改后的 Memo 条目信息头插到 MemoList 数组中
                MemoTitleActivity.MemoList.add(0, memo);
                // 更新在数组中的下标信息
                MemoTitleActivity.refresh_MemoList();
                Log.e(TAG, "备忘内容界面以 update 模式结束");
            }
        }
        /* 将过滤后的标题与内容输入 EditText 控件 */
        titleEdit.setText(tmpTitle);
        contentEdit.setText(content);
        /* 保存更改记录，替换旧的已保存标题与内容 */
        initial_title = tmpTitle;
        initial_content = content;

        Toast toast = Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void Hide_Focus() {
        /* 隐藏键盘 */
        View v = getCurrentFocus();
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        /* 取消光标 */
        titleEdit.clearFocus();
        contentEdit.clearFocus();
    }
}