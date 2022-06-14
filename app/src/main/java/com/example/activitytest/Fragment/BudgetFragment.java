package com.example.activitytest.Fragment;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.activitytest.Activity.MainActivity;
import com.example.activitytest.Database.DatabaseHelper;
import com.example.activitytest.R;
import com.example.activitytest.Util.InOutcome;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BudgetFragment extends Fragment {
    private static final String TAG = "MainActivity Budget";

    private TextView Remaining_budget_value;
    private TextView budget_value_tx;
    private TextView outcome_value_tx;
    private PieChart pieChart;

    private SharedPreferences.Editor editor;

    private float budget_value = 0;
    private float sum_out = 0;

    public static BudgetFragment newInstance() {
        return new BudgetFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        editor = pref.edit();

        budget_value = pref.getFloat("budget_value", -1);
        if (budget_value == -1){
            // 提示输入预算
            Dialog();
        }
    }

    public void Dialog() {
        // 加载弹窗视图
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = View.inflate(getContext(), R.layout.budget_dialog_item, null);

        // 控件实例化
        EditText input_et = view.findViewById(R.id.budget_dialog_et);
        input_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});

        // 构建弹窗
        builder.setTitle("");
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("保存", (dialog, which) -> {
            String STmp = input_et.getText().toString();
            budget_value = Float.parseFloat(STmp);
            editor.putFloat("budget_value", budget_value);
            editor.apply();
            LinearLayoutInit();
            initPieChart();
        });
        builder.setNegativeButton("取消", (dialog, which) -> {
            if (budget_value == -1) {
                MainActivity activity = (MainActivity) getActivity();
                assert activity != null;
                activity.changeByFragment();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button button_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button_positive.setEnabled(false);

        input_et.addTextChangedListener(new TextWatcher() {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.budget_frag, container, false);

        LinearLayout linearLayout = rootView.findViewById(R.id.budget_linearLayout);
        linearLayout.setOnClickListener(v -> Dialog());

        Remaining_budget_value = rootView.findViewById(R.id.Remaining_budget_value);
        budget_value_tx = rootView.findViewById(R.id.budget_value);
        outcome_value_tx = rootView.findViewById(R.id.outcome_value);
        refresh_sum_out();
        LinearLayoutInit();

        // 饼状图
        pieChart = rootView.findViewById(R.id.PieChart);
        initPieChart();

        return rootView;
    }

    private void refresh_sum_out(){
        // 存储支出数据的数组
        List<InOutcome> OutComeList = DatabaseHelper.InOutcome_ALL("Outcome");
        for (int i=0; i< OutComeList.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);
            String NowMonth = String.valueOf(month + 1); // 当前月份
            Log.e(TAG, "onCreateView NowMonth:" + NowMonth);
            String date_time = OutComeList.get(i).getTime(); // 数据中的时间
            Log.e(TAG, "onCreateView 日期: " + date_time);
            String regex = "-(.*)-"; // 匹配规则
            /* 截取月份 */
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(date_time);
            while (matcher.find()) {
                String date_month = matcher.group(1); // 数据中的月份
                Log.e(TAG, "onCreateView dateMonth: " +  date_month);
                assert date_month != null;
                if (date_month.equals(NowMonth)) {
                    sum_out += OutComeList.get(i).getValue();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sum_out = 0;
    }

    private void LinearLayoutInit(){
        Remaining_budget_value.setText(String.valueOf(budget_value - sum_out)); // 剩余预算
        budget_value_tx.setText(String.valueOf(budget_value)); // 总预算
        outcome_value_tx.setText(String.valueOf(sum_out)); // 支出
    }

    private void initPieChart(){
        pieChart.setUsePercentValues(true); // 使用百分比绘制而非原始值

        pieChart.setDragDecelerationFrictionCoef(0.95f); // 动画速度
        pieChart.animateXY(1400, 1400); // 动画

        pieChart.getDescription().setEnabled(false); // 饼状图描述不显示
        pieChart.getLegend().setEnabled(false); // 图例不显示

        pieChart.setDrawHoleEnabled(true); // true则图饼中心为空
        pieChart.setHoleColor(Color.WHITE); // 中心颜色
        pieChart.setDrawCenterText(true); // 将此设置为true以绘制显示在饼图中心的文本

        // 设置透明圆的透明度应为0=完全透明，255=完全不透明。默认值为100。
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setHoleRadius(45f); // 中心半径
        pieChart.setTransparentCircleRadius(61f); // 透明度

        pieChart.setRotationAngle(0); // 旋转偏移量
        // 触摸旋转，默认为true
        pieChart.setRotationEnabled(true);
        // 将此设置为false可防止轻触手势突出显示值。仍然可以通过拖动或编程方式突出显示值。默认值：true
        pieChart.setHighlightPerTapEnabled(true);

        // 数据
        ArrayList<PieEntry> entries = new ArrayList<>();

        float percentage;
        if (sum_out > budget_value) {
            // 只显示溢出部分
            percentage = (int) ((sum_out-budget_value) / budget_value) * 100;
            entries.add(new PieEntry(percentage, "预算溢出"));
            setData(entries, "overflow"); //设置数据
            pieChart.setCenterText("已超支");
            pieChart.setCenterTextSize(30f);
            pieChart.setCenterTextColor(Color.parseColor("#CD5C5C"));
        }
        else {
            // 显示支出占比
            percentage = (int) ((budget_value - sum_out) / budget_value * 100);
            entries.add(new PieEntry(percentage, "剩余预算"));
            entries.add(new PieEntry(100 - percentage, "支出"));
            setData(entries, "normal"); //设置数据
            pieChart.setCenterText("");
        }
        pieChart.setCenterTextSize(30f);
        // 饼状图上的描述，即预算溢出和支出占比这两个文字的描述
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(20f);
    }

    //设置数据
    private void setData(ArrayList<PieEntry> entries, String mode) {
        PieDataSet dataSet = new PieDataSet(entries, null);
        // 设置dp中PieChart切片之间的剩余空间。默认值：0-->无空间，最大20f
        dataSet.setSliceSpace(3f);
        // 设置此数据集高亮显示的PieChart切片从图表中心“偏移”的距离，默认为12f
        dataSet.setSelectionShift(5f);

        // 数据和颜色
        ArrayList<Integer> colors = new ArrayList<>();
        if (mode.equals("overflow")) {
            colors.add(Color.parseColor("#CD5C5C"));
        }
        else if (mode.equals("normal")) {
            colors.add(Color.parseColor("#87CEFA"));
            colors.add(R.color.Linen);
        }
        else {
            Log.e(TAG, "setData 意外情况");
        }
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        // 为此数据对象包含的所有数据集设置自定义IValueFormatter。
        data.setValueFormatter(new PercentFormatter());
        // 设置此数据对象包含的所有数据集的值文本的大小（以dp为单位）。
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
        //刷新
        pieChart.invalidate();
    }
}
