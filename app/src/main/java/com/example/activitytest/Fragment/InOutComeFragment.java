package com.example.activitytest.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activitytest.Database.DatabaseHelper;
import com.example.activitytest.R;
import com.example.activitytest.Util.InOutComeAdapter;
import com.example.activitytest.Util.InOutcome;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import yalantis.com.sidemenu.interfaces.ScreenShotable;

/** Fragment不能自定义带参数的构造函数 */
public class InOutComeFragment extends Fragment implements ScreenShotable {
    private static final String TAG = "MainActivity";
    protected static int res;

    public static int MODE; // 0代表收入界面，1代表支出界面

    @SuppressLint("StaticFieldLeak")
    private static TextView totalText;

    public static List<InOutcome> InOutComeList = new ArrayList<>();
    private InOutComeAdapter Adapter; // 适配器
    private Context context;

    // 单例模式实例化
    public static InOutComeFragment newInstance() {
        return new InOutComeFragment();
    }

    @Override
    public void onStart() { // Fragment此时已可见
        super.onStart();
        context = getContext(); // 绑定上下文
        if (context == null) {
            Log.e(TAG, "onStart context is null");
        }
        else {
            Log.e(TAG, "onStart context isn't null");
        }
    }

    /** 初始化除视图之外的一切*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper.Instance(context); // 初始化数据库，如果数据库已存在不会重复执行内部逻辑

        context = getContext(); // 绑定上下文
        if (context == null) {
            Log.e(TAG, "onCreate context is null");
        }
        else {
            Log.e(TAG, "onCreate context isn't null " + context);
        }

        Log.e(TAG, "fragment onCreate 完成, MODE = " + MODE);
    }

    /** 初始化视图，onCreate阶段完成后调用 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.in_out_frag, container, false);

        TabLayout tabLayout = rootView.findViewById(R.id.in_out_TabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e(TAG, "onTabSelected 当前处于哪个位置: " + tab.getPosition());
                if (tab.getPosition() == 0){
                    Log.e(TAG, "onTabSelected: 收入界面");
                    MODE = 0;
                    RecyclerViewInit(rootView);
                    String tmpText = "总收入：" + getTotalBill();
                    totalText.setText(tmpText);
                }
                else if(tab.getPosition() == 1) {
                    Log.e(TAG, "onTabSelected: 支出界面");
                    MODE = 1;
                    RecyclerViewInit(rootView);
                    String tmpText = "总支出：" + getTotalBill();
                    totalText.setText(tmpText);
                }
                else {
                    Log.e(TAG, "onTabSelected: 滑动意外");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        RecyclerViewInit(rootView);

        totalText = rootView.findViewById(R.id.total_bill);
        String tmpText = "总收入：" + getTotalBill();
        totalText.setText(tmpText);

        // 用于新增账目信息
        FloatingActionButton FAB = rootView.findViewById(R.id.add_in_out_item);
        FAB.setOnClickListener(view -> {
            // 新增条目信息
            DateDialog();
        });
        Log.e(TAG, "onCreateView 完成");
        return rootView;
    }

    /* RecyclerView 实例化*/
    private void RecyclerViewInit(View rootView) {
        if (MODE == 0){
            // 加载所有收入信息到数组
            InOutComeList = DatabaseHelper.InOutcome_ALL("Income");
            Log.e(TAG, "收入信息数组大小: " + InOutComeList.size());
        }
        else {
            // 加载所有支出信息到数组
            InOutComeList = DatabaseHelper.InOutcome_ALL("Outcome");
            Log.e(TAG, "支出信息数组大小: " + InOutComeList.size());
        }
        Log.e(TAG, "RecyclerViewInit MODE = : " + MODE);

        RecyclerView recyclerView = rootView.findViewById(R.id.RV_in_out);
        // 布局方式
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        // 设置RecyclerView布局方式为线性布局
        recyclerView.setLayoutManager(layoutManager);
        // 用数组和上下文实例化适配器
        Adapter = new InOutComeAdapter(InOutComeList, context);
        // 为布局添加适配器
        recyclerView.setAdapter(Adapter);
        Log.e(TAG, "RecyclerViewInit LoanList 数组大小" + InOutComeList.size());
    }

    private static String getTotalBill(){
        double sum = 0;
        for (int i = 0; i < InOutComeList.size(); i++){
            sum += InOutComeList.get(i).getValue();
        }
        return String.valueOf(sum);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        InOutComeList.clear(); // 删除所有账目信息
        MODE = 0;
        Log.e(TAG, "onDestroy 收支界面");
    }

    @Override
    public void takeScreenShot() {

    }

    @Override
    public Bitmap getBitmap() {
        return null;
    }

    /* 更新 InOutcome 对象的 Ind 属性以重新映射其和 IncomeList 的关系 */
    public static void refresh_itemList(){
        int len = InOutComeList.size();
        for (int i = 0; i < len; i++){
            InOutcome item = InOutComeList.get(i); // 获得下标i对应的item
            item.setInd(i); // 更新item对象的Ind变量
        }
        String tmpText;
        if (MODE == 0){
            tmpText = "总收入：" + getTotalBill();
        }
        else {
            tmpText = "总支出：" + getTotalBill();
        }
        totalText.setText(tmpText);
    }

    /** 通过FAB新建条目时/点击子项修改条目时 弹出对话框 */
    @SuppressLint("NotifyDataSetChanged")
    public void DateDialog() {
        // 加载弹窗视图
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.in_out_dialog_item, null);
        Log.e(TAG, "DateDialog parent: " + view.getParent());

        // 控件实例化
        Spinner spinner = view.findViewById(R.id.kind_spinner);
        init_spinnerList(spinner);
        TextView time_tx = view.findViewById(R.id.income_time_tv);
        EditText value_et = view.findViewById(R.id.input_amount);

        // 点击事件
        time_tx.setOnClickListener(view1 -> setDatePickerDialog(time_tx));

        // 构建弹窗
        builder.setTitle("新增账目");
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("保存", (dialogInterface, i) -> save_InOutcome
                (Adapter, "insert", spinner, time_tx, value_et, null, -1));
        builder.setNegativeButton("取消", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button button_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button_positive.setEnabled(false);

        value_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String STmp = String.valueOf(s);
                if (!(STmp.isEmpty())){
                    float FTmp = Float.parseFloat(STmp);
                    button_positive.setEnabled(FTmp > 0);
                    // 预算只能输入到分，之后的输入无效化
                    int tLen = STmp.length();
                    if (STmp.contains(".") && (tLen - 1 - STmp.indexOf(".") > 2)) {
                        STmp = STmp.substring(0, STmp.indexOf(".") + 2 + 1);
                        s.replace(0, s.length(), STmp);
                    }
                }
                else {
                    button_positive.setEnabled(false);
                }
            }
        });
    }

    public void init_spinnerList(Spinner spinner){
        String[] strings;
        if (MODE == 0){
            strings = getResources().getStringArray(R.array.in_kind_array);
        }
        else {
            strings = getResources().getStringArray(R.array.out_kind_array);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (context, android.R.layout.simple_list_item_1, strings);
        spinner.setAdapter(arrayAdapter);
    }

    /** 修改time时，使用 DatePickerDialog */
    public static void setDatePickerDialog(TextView textView){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener listener = (datePicker, year1, month1, day1) -> {
            String date = year1 +"-"+(month1 +1)+"-"+ day1;
            textView.setText(date);
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog
                (textView.getContext(), R.style.dialog_date, listener, year, month, day);
        datePickerDialog.updateDate(year, month, day); // 每次点击 datePickerDialog 都显示当前日期
        datePickerDialog.show();
        Date date = new Date();
        long nowTime = date.getTime();
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMaxDate(nowTime);
        Log.e(TAG, "setDatePickerDialog now time: " + nowTime);
    }

    public static void save_InOutcome(InOutComeAdapter Adapter, String mode, Spinner spinner,
                                      TextView time_tx, EditText value_et, String id, int ind){
        // 接收控件内容
        String db_kind;
        String db_time;
        double db_value;

        // 接收种类信息
        db_kind = spinner.getSelectedItem().toString();
        Log.e(TAG, "DateDialog: " + db_kind);

        // 接收时间信息
        if (time_tx.getText().toString().isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            db_time = year +"-"+(month +1)+"-"+ day;
        }
        else {
            db_time = time_tx.getText().toString();
            Log.e(TAG, "DateDialog: " + db_time);
        }

        // 接收数值信息
        if (value_et.getText().toString().isEmpty()) {
            db_value = 0;
        }
        else {
            db_value = Double.parseDouble(value_et.getText().toString());
            Log.e(TAG, "DateDialog: " + db_value);
        }

        String table;
        if (MODE == 0){ // 当前为收入模块
            table = "Income";
        }
        else { // 支出模块
            table = "Outcome";
        }
        Log.e(TAG, "DateDialog table:" + table);

        // 通过接受的信息构造临时对象
        InOutcome tmpInOutcome = new InOutcome(db_kind, db_time, db_value);

        if (mode.equals("insert")) {
            // 将收集到的信息新增到数据库
            long last_id = DatabaseHelper.add_InOutcomeDB(table, db_kind, db_time, db_value);
            tmpInOutcome.setId(String.valueOf(last_id));
            Log.e(TAG, "DateDialog MODE:" + MODE);

            // 将临时对象新增到数组
            InOutComeList.add(0, tmpInOutcome);
            refresh_itemList(); // 新增操作改变了原有的数组下标的映射关系，因此需要更新
            Adapter.notifyItemInserted(0);
            Adapter.notifyItemRangeChanged(0, InOutComeList.size());
            Log.e(TAG, "DateDialog: 数组大小" + InOutComeList.size());
        }
        else if(mode.equals("update")) {
            // 将收集到的信息修改到数据库
            DatabaseHelper.update_LoanDB(table, id, db_kind, db_time, db_value);

            // 将旧对象的数据库id赋给新的 tmpInOutcome
            tmpInOutcome.setId(id);
            // 然后将修改后的 tmpInOutcome 条目信息头插到 LoanList 数组中
            InOutComeList.set(ind, tmpInOutcome);
            // 更新在数组中的下标信息
            refresh_itemList();
            Adapter.notifyItemRangeChanged(0, InOutComeList.size());
            Log.e(TAG, "更新子项内容之后的数组大小" + InOutComeList.size());
        }
        else {
            Log.e(TAG, "save_InOutcome: 模式出错");
        }
    }
}