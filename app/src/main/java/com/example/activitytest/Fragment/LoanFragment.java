package com.example.activitytest.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.activitytest.Database.DatabaseHelper;
import com.example.activitytest.R;
import com.example.activitytest.Util.Loan;
import com.example.activitytest.Util.LoanAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LoanFragment extends Fragment {
    private static final String TAG = "MainActivity";

    public static List<Loan> LoanList = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    private static TextView TV_loan_total;

    private LoanAdapter loanAdapter;
    private Context context;

    public static LoanFragment newInstance() {
        return new LoanFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.loan_frag, container, false);

        RecyclerViewInit(rootView);

        TV_loan_total = rootView.findViewById(R.id.total_loan);
        String tmpText = "总借贷：" + getTotalLoan();
        TV_loan_total.setText(tmpText);

        // 用于新增借贷信息
        FloatingActionButton FAB = rootView.findViewById(R.id.add_in_out_item);
        FAB.setOnClickListener(view -> {
            // 新增条目信息
            DateDialog();
        });
        return rootView;
    }

    private void RecyclerViewInit(View rootView) {
        LoanList = DatabaseHelper.Loan_ALL();
        RecyclerView recyclerView = rootView.findViewById(R.id.RV_loan);
        // 布局方式
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        // 设置RecyclerView布局方式为线性布局
        recyclerView.setLayoutManager(layoutManager);
        // 用数组和上下文实例化适配器
        loanAdapter = new LoanAdapter(LoanList, context);
        // 为布局添加适配器
        recyclerView.setAdapter(loanAdapter);
    }

    private static String getTotalLoan(){
        double sum = 0;
        Log.e(TAG, "getTotalLoan: " + LoanList.size());
        for (int i = 0; i < LoanList.size(); i++){
            sum += LoanList.get(i).getValue();
            Log.e(TAG, "getTotalLoan sum : " + sum);
        }
        return String.valueOf(sum);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoanList.clear(); // 删除所有借贷信息
    }

    /* 更新 Loan 对象的 Ind 属性以重新映射其和 LoanList 的关系 */
    public static void refresh_itemList(){
        int len = LoanList.size();
        for (int i = 0; i < len; i++){
            Loan item = LoanList.get(i); // 获得下标i对应的item
            item.setInd(i); // 更新item对象的Ind变量
        }
        String tmpText = "总借贷：" + getTotalLoan();
        TV_loan_total.setText(tmpText);
    }

    /** 通过FAB新建条目时/点击子项修改条目时 弹出对话框 */
    @SuppressLint("NotifyDataSetChanged")
    public void DateDialog() {
        // 加载弹窗视图
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.loan_dialog_item, null);

        // 控件实例化
        Spinner SP_loan_source = view.findViewById(R.id.loan_kind_spinner);
        String[] strings = getResources().getStringArray(R.array.loan_kind_array);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (context, android.R.layout.simple_list_item_1, strings);
        SP_loan_source.setAdapter(arrayAdapter);
        TextView TV_loan_time = view.findViewById(R.id.TV_loan_time);
        EditText ED_loan_value = view.findViewById(R.id.ED_loan_amount);

        // 点击事件
        TV_loan_time.setOnClickListener(view1 -> InOutComeFragment.setDatePickerDialog(TV_loan_time));

        // 构建弹窗
        builder.setTitle("新增账目");
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("保存", (dialogInterface, i) -> save_loan
                (loanAdapter, "insert", SP_loan_source, TV_loan_time, ED_loan_value,
                        null, -1));
        builder.setNegativeButton("取消", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button button_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button_positive.setEnabled(false);

        ED_loan_value.addTextChangedListener(new TextWatcher() {
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
                        STmp = STmp.substring(0, STmp.indexOf(".") + 3);
                        s.replace(0, s.length(), STmp);
                        // // 金额只能到千万
                    }
                }
                else {
                    button_positive.setEnabled(false);
                }
            }
        });
    }

    // 弹窗的保存按钮点击事件
    public static void save_loan(LoanAdapter Adapter, String mode, Spinner spinner,
                          TextView TV_loan_time, EditText ED_loan_value, String id, int ind){
        // 接收控件内容
        String db_source;
        String db_time;
        double db_value;

        // 接收种类信息
        db_source = spinner.getSelectedItem().toString();
        Log.e(TAG, "DateDialog: " + db_source);

        // 接收时间信息
        if (TV_loan_time.getText().toString().isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            db_time = year +"-"+(month +1)+"-"+ day;
        }
        else {
            db_time = TV_loan_time.getText().toString();
            Log.e(TAG, "DateDialog: " + db_time);
        }

        // 接收数值信息
        if (ED_loan_value.getText().toString().isEmpty()) {
            db_value = 0;
        }
        else {
            db_value = Double.parseDouble(ED_loan_value.getText().toString());
            Log.e(TAG, "DateDialog: " + db_value);
        }

        // 通过接受的信息构造临时对象
        Loan tmpLoan = new Loan(db_source, db_time, db_value);

        if (mode.equals("insert")) {
            // 将收集到的信息新增到数据库
            long last_id = DatabaseHelper.add_LoanDB(db_source, db_time, db_value);
            tmpLoan.setId(String.valueOf(last_id));

            // 将临时对象新增到数组
            LoanList.add(0, tmpLoan);
            refresh_itemList(); // 新增操作改变了原有的数组下标的映射关系，因此需要更新
            Adapter.notifyItemInserted(0);
            Adapter.notifyItemRangeChanged(0, LoanList.size());
            Log.e(TAG, "DateDialog: 数组大小" + LoanList.size());
        }
        else if(mode.equals("update")) {
            // 将收集到的信息修改到数据库
            DatabaseHelper.update_LoanDB(id, db_source, db_time, db_value);

            // 将旧对象的数据库id赋给新的 tmpLoan
            tmpLoan.setId(id);
            // 然后将修改后的 tmpLoan 条目信息头插到 LoanList 数组中
            LoanList.set(ind, tmpLoan);
            // 更新在数组中的下标信息
            refresh_itemList();
            Adapter.notifyItemRangeChanged(0, LoanList.size());
            Log.e(TAG, "更新子项内容之后的数组大小" + LoanList.size());
        }
        else {
            Log.e(TAG, "save_InOutcome: 模式出错");
        }
    }
}
