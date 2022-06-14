package com.example.activitytest.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activitytest.Database.DatabaseHelper;
import com.example.activitytest.R;
import com.example.activitytest.Util.BaseActivity;
import com.example.activitytest.Util.Memo;
import com.example.activitytest.Util.MemoAdapter;

import java.util.ArrayList;
import java.util.List;

public class MemoTitleActivity extends BaseActivity {
    private static final String TAG = "MemoTitleActivity";

    public static List<Memo> MemoList = new ArrayList<>();
    private MemoAdapter memoAdapter; // 适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_title);

        /* Toolbar 实例化 */
        newInstance_Toolbar("备忘记事");

        DatabaseHelper.Instance(this); // 初始化数据库，如果数据库已存在不会重复执行内部逻辑

        MemoList = DatabaseHelper.Memo_ALL(); // 加载所有备忘信息
        RecyclerViewInit();
    }

    /* RecyclerView 实例化*/
    private void RecyclerViewInit() {
        RecyclerView memoTitleRecyclerView = findViewById(R.id.memo_title_recycler_view);
        // 布局方式
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // 设置RecyclerView布局方式为线性布局
        memoTitleRecyclerView.setLayoutManager(layoutManager);
        // 实例化适配器
        memoAdapter = new MemoAdapter(MemoList, this);
        // 为布局添加适配器
        memoTitleRecyclerView.setAdapter(memoAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MemoList.clear(); // 删除所有备忘信息
    }

    /** 重写 Fragment 的 onCreateOptionsMenu() 回调。通过此方法，
     * 可以将菜单资源（使用 XML 定义）inflate 到 ToolBar 的 Menu 中。
     * 另一种方法是 注释掉setSupportActionBar(toolbar)。*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_memo_title, menu);
        return true;
    }

    /** 处理状态栏中弹出菜单（Menu）的点击事件 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 通过调用 getItemId() 方法来识别菜单项，该方法会返回菜单项的唯一ID
        // (由菜单资源中的 android:id 属性定义，或通过提供给 add() 方法的整型数定义)。
        switch (item.getItemId()) {
            case R.id.add_memo:
                Intent intent =
                        new Intent(MemoTitleActivity.this, MemoContentActivity.class);
                intent.putExtra("mode","create"); // 启动模式为create
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MemoList = DatabaseHelper.Memo_ALL(); // 加载所有备忘信息
        RecyclerViewInit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // 滑动布局方向更改时
        if (memoAdapter != null) {
            memoAdapter.saveStates(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // 滑动布局方向更改时
        if (memoAdapter != null) {
            memoAdapter.restoreStates(savedInstanceState);
        }
    }

    /* 更新 Memo 对象的 Ind 属性以重新映射其和 MemoList 的关系 */
    public static void refresh_MemoList(){
        int len = MemoList.size();
        for (int i = 0; i < len; i++){
            Memo item = MemoList.get(i);
            item.setInd(i);
        }
    }
}