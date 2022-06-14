package com.example.activitytest.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.activitytest.Database.DatabaseHelper;
import com.example.activitytest.Fragment.BudgetFragment;
import com.example.activitytest.Fragment.InOutComeFragment;
import com.example.activitytest.Fragment.LoanFragment;
import com.example.activitytest.Fragment.TypeFragment;
import com.example.activitytest.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;

/** 成功登录后进入的主界面 */
// ViewAnimator.ViewAnimatorListener 设置动画播放时的其他操作
// ActionBar 中 Home 按钮的状态处理 以及侧边菜单子项的点击事件 onSwitch 的处理
public class MainActivity extends AppCompatActivity implements ViewAnimator.ViewAnimatorListener {
    private static final String TAG = "MainActivity";

    public static final String CLOSE = "Close";
    public static final String BILL = "Bill";
    public static final String TRANSLATE = "Translate";
    public static final String SHARES = "Shares";
    public static final String WEATHER = "Weather";
    public static final String CALCULATE = "Calculate";
    public static final String MEMORANDUM = "Memorandum";

    private DrawerLayout drawerLayout; // 最外层布局（drawer：抽屉）
    private ActionBarDrawerToggle drawerToggle;
    private final List<SlideMenuItem> list = new ArrayList<>(); // 抽屉内容-侧滑菜单
    private ViewAnimator<SlideMenuItem> viewAnimator;
    private LinearLayout linearLayout; // 左侧 ScrollView（控件-竖直滚动条） 内嵌布局 LinearLayout

    private InOutComeFragment inOutComeFragment;
    private LoanFragment loanFragment;
    private BudgetFragment budgetFragment;
    private TypeFragment typeFragment;

    private FragmentContainerView fragmentContainerView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        DatabaseHelper.Instance(this); // 初始化数据库，如果数据库已存在不会重复执行内部逻辑

        // 使用newInstance实例化Fragment布局内容
        inOutComeFragment = InOutComeFragment.newInstance();
        loanFragment = LoanFragment.newInstance();
        budgetFragment = BudgetFragment.newInstance();
        typeFragment = TypeFragment.newInstance();
        initFragment();

        fragmentContainerView = findViewById(R.id.main_frag);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            FragmentTransaction FT = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()){
                case R.id.BN_in_outcome:
                    FT.replace(R.id.main_frag, inOutComeFragment ).commit();
                    break;
                case R.id.BN_loan:
                    FT.replace(R.id.main_frag, loanFragment).commit();
                    break;
                case R.id.BN_budget:
                    FT.replace(R.id.main_frag, budgetFragment).commit();
                    break;
                case R.id.BN_type:
                    FT.replace(R.id.main_frag, typeFragment).commit();
                    break;
            }
            return true;
        });

        // 父布局（drawer：抽屉）
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT); // 设置抽屉打开后，除抽屉之外的界面的颜色

        // 左侧 ScrollView（控件-竖直滚动条） 内嵌布局LinearLayout
        linearLayout = findViewById(R.id.left_drawer);
        linearLayout.setOnClickListener(v -> drawerLayout.closeDrawers()); // 关闭所有抽屉

        InitActionBar(); // 自定义函数，ActionBar 与 DrawerLayout 通过 ActionBarDrawerToggle 建立事件关系
        createMenuList(); // 创建菜单列表，但还不可见

        // 通过自定义 ViewAnimator 进行相关的事件监控
        // 分别传入菜单数据 list、InOutComeFragment(实现 ScreenShotable 接口)、drawerLayout、当前 Activity
        // 主要的事件都是通过 ViewAnimator 回调 ViewAnimatorListener 方法，进行界面处理(MVP模式体现)
        viewAnimator = new ViewAnimator<>
                (this, list, inOutComeFragment, drawerLayout, this);

        // 一进入界面就打开左侧抽屉
        drawerLayout.openDrawer(GravityCompat.START); // 打开抽屉
        viewAnimator.showMenuContent(); // 展示左侧边栏
        Log.e(TAG, "onCreate: 完毕");
    }

    /** 预算fragment第一次不设置预算金额时模拟点击收支fragment并跳转到收支界面 */
    public void changeByFragment(){
        bottomNavigationView.setSelectedItemId(R.id.BN_in_outcome);
        Log.e(TAG, "changeByFragment 被调用了");
    }

    private void initFragment () {
        // 获取碎片管理器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        // 获取事务，beginTransaction开启一个事务
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        // 提供 容器id 和 待添加的碎片实例，实现像容器内动态替换碎片
        fragmentTransaction.replace(R.id.main_frag, inOutComeFragment);
        // 提交事务
        fragmentTransaction.commit();
    }

    // 创建左侧菜单列表
    private void createMenuList() {
        // SlideMenuItem 侧滑菜单子项
        SlideMenuItem menuItem0 =
                new SlideMenuItem(CLOSE, R.drawable.icn_close);
        list.add(menuItem0);

        SlideMenuItem menuItem6 =
                new SlideMenuItem(BILL, R.drawable.ic_bill_24);
        list.add(menuItem6);

        SlideMenuItem TranslateItem =
                new SlideMenuItem(TRANSLATE, R.drawable.ic_translate_24);
        list.add(TranslateItem);

        SlideMenuItem SharesItem =
                new SlideMenuItem(SHARES, R.drawable.ic_shares_24);
        list.add(SharesItem);

        SlideMenuItem WeatherItem =
                new SlideMenuItem(WEATHER, R.drawable.ic_weather_24);
        list.add(WeatherItem);

        SlideMenuItem CalculateItem =
                new SlideMenuItem(CALCULATE, R.drawable.ic_calculate_24);
        list.add(CalculateItem);

        SlideMenuItem MemorandumItem =
                new SlideMenuItem(MEMORANDUM, R.drawable.ic_memorandum_24);
        list.add(MemorandumItem);
    }

    /** 更改 ActionBar 中的图标，并且实现打开或关闭 drawer (导航抽屉)的相关操作。*/
    private void InitActionBar() {
        // Toolbar 标题栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // 设置 toolbar 为 ActionBar 对象

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true); // 设置返回键可，此时左上角还没有小箭头
        actionBar.setDisplayHomeAsUpEnabled(true); // 给左上角加上一个向左的小箭头

        InitDrawerListener(toolbar); // 绑定标题栏与抽屉
    }

    /**
     * 通过创建 <code>ActionBarDrawerToggle对象</code> 将 <code>标题栏ActionBar</code> 和
     * <code>抽屉布局DrawerLayout</code> 结合在一起。可以用 <code>ActionBarDrawerToggle</code> 充当
     * <code>抽屉布局DrawerLayout</code> 的 <code>监听器DrawerListener</code>，也可以自定义
     * <code>监听器DrawerListener</code> */
    private void InitDrawerListener(Toolbar toolbar){
        // 由于仅需使用一次，因此使用匿名内部类继承 ActionBarDrawerToggle 并重写部分方法
        drawerToggle = new ActionBarDrawerToggle(
                /* 构造函数有五个参数 */
                // 存放抽屉的 Activity活动
                this,
                // 绑定到 ActionBar标题栏 的 DrawerLayout布局对象
                drawerLayout,
                // 如果有独立的Toolbar，则填入该Toolbar
                // 如果为null，则绑定当前 Activity活动 的 ActionBar标题栏
                toolbar,
                // "open drawer" 描述打开抽屉的操作
                R.string.drawer_open,
                // "close drawer" 描述关闭抽屉的操作
                R.string.drawer_close
        ) {

            /** 滑动时调用，即当 drawer 正在打开时调用。
             * @param drawerView - 已打开的抽屉视图
             * @param slideOffset – 此抽屉在其范围内的新偏移量，从 0 到 1 */
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                // getChildCount 返回直接子元素的个数
                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0) {
                    viewAnimator.showMenuContent(); // 展示左侧边栏
                    Log.e(TAG, "onDrawerSlide: 正在滑动抽屉");
                }
            }

            /** 当 drawer 处于完全打开状态时调用。
             * @param drawerView - 现在打开的抽屉视图 */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                Log.e(TAG, "onDrawerOpened: 抽屉完全打开");
            }

            /** 当 drawer 处于完全关闭状态时调用。
             * @param view - 已关闭的抽屉视图 */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // 左侧竖直标题栏
                linearLayout.removeAllViews();
                linearLayout.invalidate(); // 重绘
                /*
                 * postInvalidate() 可以在 UI 线程中执行，也可以在工作线程中执行，
                 * 而 invalidate() 只能在 UI 线程中执行，但是从重绘速率上讲 invalidate() 效率更高。
                 */
                Log.e(TAG, "onDrawerClosed: 抽屉完全关闭");
            }

            /** 当 drawer 状态改变时调用（从 关闭状态变为打开状态 或者 从打开状态变为关闭状态 都算状态改变）。
             * @param newState - 抽屉的新状态 */
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                Log.e(TAG, "onDrawerStateChanged:抽屉状态更改 ");
            }
        };
        drawerLayout.addDrawerListener(drawerToggle); // 添加监听
    }

    /**
     * <p>发生 <code>onRestoreInstanceState</code> 后，在
     * {@link android.app.Activity#onPostCreate(android.os.Bundle) onPostCreate} 中调用
     * <code>syncState()</code> 将抽屉状态（开与关）和页面左上角的图标同步。</p>
     * <code>onCreate</code>阶段结束后调用，表示所有布局都已完成创建。 */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // drawerToggle的开关状态与左上角图标同步
        drawerToggle.syncState();
        Log.e(TAG, "onPostCreate 结束");
    }

    /**
     * <p>为了使用 <code>ActionBarDrawerToggle</code> 须在 Activity 中重写并调用以下两个方法</p>
     * <ul>
     * <li>{@link android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
     * onConfigurationChanged}
     * <li>{@link android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     * onOptionsItemSelected}</li>
     * </ul> */
    /*@Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    *//** 处理右侧菜单（Menu）点击事件 *//*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 将 MenuItem事件 传递给 ActionBarDrawerToggle
        // 如果返回true，则表示它已处理应用程序图标触摸事件
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // 通过调用 getItemId() 方法来识别菜单项，该方法会返回菜单项的唯一ID
        // (由菜单资源中的 android:id 属性定义，或通过提供给 add() 方法的整型数定义)。
        switch (item.getItemId()) {
            case
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    /** 处理左侧抽屉（drawer）的点击事件 */
    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable,
                                   int position) {
        switch (slideMenuItem.getName()) {
            case CLOSE:
                // 跳转回初始化的 inOutComeFragment 对象
                // 关闭侧边栏
                disableHomeButton();
                return screenShotable;
            case BILL:
                onRestart();
                return screenShotable;
            case TRANSLATE:
                // 跳转到中英互译界面
                Intent intent_translate = new Intent(this, TranslateActivity.class);
                startActivity(intent_translate);
                return screenShotable;
            case SHARES:
                // 跳转到股票界面
                Intent intent_shares = new Intent(this, SharesActivity.class);
                startActivity(intent_shares);
                return screenShotable;
            case WEATHER:
                // 跳转到天气界面
                Intent intent_weather = new Intent(this, WeatherActivity.class);
                startActivity(intent_weather);
                return screenShotable;
            case CALCULATE:
                // 跳转到计算器界面
                Intent intent_cal = new Intent(this, CalActivity.class);
                startActivity(intent_cal);
                return screenShotable;
            case MEMORANDUM:
                // 跳转到备忘录界面
                Intent intent_memo = new Intent(this, MemoTitleActivity.class);
                startActivity(intent_memo);
                return screenShotable;
            default:
                // 其他返回一个新的 inOutComeFragment 对象
                Log.e(TAG, "onSwitch: 点击其他");
                return screenShotable;
        }
    }

    /* 以下为 ViewAnimatorListener 要求实现的三个方法 */
    /** 标题栏最左侧的图标不可见 */
    @Override
    public void disableHomeButton() {
        // 图标变为⬅
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(false);
    }

    /** 标题栏左侧的图标可见 */
    @Override
    public void enableHomeButton() {
        // 图标变为三条横杠样式
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        drawerLayout.closeDrawers(); // 关闭抽屉（即左侧边栏）
    }

    /** 将抽屉子项视图添加到抽屉所在的linearLayout布局中 */
    @Override
    public void addViewToContainer(View view) {
        linearLayout.addView(view);
    }
}
