package com.example.activitytest.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.activitytest.Database.DatabaseHelper;
import com.example.activitytest.R;
import com.example.activitytest.Util.InOutcome;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;

public class TypeFragment extends Fragment {
    private static final String TAG = "MainActivity type";

    private PieChart PC_out;
    private PieChart PC_in;

    private float sum_out = 0;
    private float food = 0;
    private float clothe = 0;
    private float medical = 0;
    private float traffic = 0;
    private float education = 0;
    private float live = 0;

    private float sum_in = 0;
    private float wages = 0;
    private float financial  = 0;
    private float other  = 0;

    public static TypeFragment newInstance() {
        return new TypeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.type_frag, container, false);

        // 支出饼状图
        PC_out = rootView.findViewById(R.id.PC_out);
        PC_out.setCenterText("支出"); // 中心文字
        PC_out.setCenterTextSize(30f); // 中心文字大小
        PC_out.setCenterTextColor(Color.parseColor("#CD5C5C")); // 中心文字颜色
        initPieChart(PC_out);
        init_out_date();

        // 收入饼状图
        PC_in = rootView.findViewById(R.id.PC_in);
        PC_in.setCenterText("收入"); // 中心文字
        PC_in.setCenterTextSize(30f); // 中心文字大小
        PC_in.setCenterTextColor(Color.parseColor("#2E8B47")); // 中心文字颜色
        initPieChart(PC_in);
        init_in_date();

        return rootView;
    }

    private void initPieChart(PieChart pieChart){
        pieChart.setUsePercentValues(true); // 使用百分比绘制而非原始值

        pieChart.setDragDecelerationFrictionCoef(0.95f); // 动画速度
        pieChart.animateXY(1400, 1400); // 动画

        pieChart.getDescription().setEnabled(false); // 饼状图描述不显示
        pieChart.setDrawEntryLabels(false); // 不在饼图中显示标签

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

        //设置比例图
        Legend legend= pieChart.getLegend(); // 获取比例图
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // 显示的位置水平居中
        legend.setFormSize(10f); // 图例的图形大小
        legend.setTextSize(10f); // 图例的文字大小
    }

    private void init_out_date(){
        // 存储支出数据的数组
        List<InOutcome> OutComeList = DatabaseHelper.InOutcome_ALL("Outcome");
        for (int i=0; i<OutComeList.size(); i++){
            String kind = OutComeList.get(i).getKind();
            float value = (float) OutComeList.get(i).getValue();
            sum_out += value;
            switch (kind){
                case "食品" :
                    food += value;
                    break;
                case "衣物" :
                    clothe += value;
                    break;
                case "医疗" :
                    medical += value;
                    break;
                case "交通" :
                    traffic += value;
                    break;
                case "文教" :
                    education += value;
                    break;
                case "居住" :
                    live += value;
                    break;
            }
        }
        Log.e(TAG, "init_out_date food: " + food);
        Log.e(TAG, "init_out_date clothe: " + clothe);
        Log.e(TAG, "init_out_date medical: " + medical);
        Log.e(TAG, "init_out_date traffic: " + traffic);
        Log.e(TAG, "init_out_date education: " + education);
        Log.e(TAG, "init_out_date live: " + live);
        Log.e(TAG, "init_out_date sum_out: " + sum_out);

        // 数据
        ArrayList<PieEntry> entries = new ArrayList<>();

        float food_percentage = (food / sum_out) * 100;
        entries.add(new PieEntry(food_percentage, "食品"));
        Log.e(TAG, "init_out_date food_percent: " + food_percentage);
        float clothe_percentage = (clothe / sum_out) * 100;
        entries.add(new PieEntry(clothe_percentage, "衣物"));
        Log.e(TAG, "init_out_date clothe_percent: " + clothe_percentage);
        float medical_percentage = (medical / sum_out) * 100;
        entries.add(new PieEntry(medical_percentage, "医疗"));
        Log.e(TAG, "init_out_date medical_percent: " + medical_percentage);
        float traffic_percentage = (traffic / sum_out) * 100;
        entries.add(new PieEntry(traffic_percentage, "交通"));
        Log.e(TAG, "init_out_date traffic_percent: " + traffic_percentage);
        float education_percentage = (education / sum_out) * 100;
        entries.add(new PieEntry(education_percentage, "文教"));
        Log.e(TAG, "init_out_date education_percent: " + education_percentage);
        float live_percentage = (live / sum_out) * 100;
        entries.add(new PieEntry(live_percentage, "居住"));
        Log.e(TAG, "init_out_date live_percent: " + live_percentage);
        setData(entries, PC_out); //设置数据
    }

    private void init_in_date(){
        // 存储支出数据的数组
        List<InOutcome> InComeList = DatabaseHelper.InOutcome_ALL("Income");
        for (int i=0; i<InComeList.size(); i++){
            String kind = InComeList.get(i).getKind();
            float value = (float) InComeList.get(i).getValue();
            sum_in += value;
            switch (kind){
                case "工资" :
                    wages += value;
                    break;
                case "理财" :
                    financial += value;
                    break;
                case "其他" :
                    other += value;
                    break;
            }
        }
        Log.e(TAG, "init_out_date wages: " + wages);
        Log.e(TAG, "init_out_date financial: " + financial);
        Log.e(TAG, "init_out_date other: " + other);

        // 数据
        ArrayList<PieEntry> entries = new ArrayList<>();

        float wages_percentage = (wages / sum_in) * 100;
        entries.add(new PieEntry(wages_percentage, "食品"));
        Log.e(TAG, "init_out_date wages_percent: " + wages_percentage);
        float financial_percentage = (financial / sum_in) * 100;
        entries.add(new PieEntry(financial_percentage, "衣物"));
        Log.e(TAG, "init_out_date financial_percent: " + financial_percentage);
        float other_percentage = (other / sum_in) * 100;
        entries.add(new PieEntry(other_percentage, "医疗"));
        Log.e(TAG, "init_out_date other_percent: " + other_percentage);
        setData(entries, PC_in); //设置数据
    }

    //设置数据
    private void setData(ArrayList<PieEntry> entries, PieChart pieChart) {
        PieDataSet dataSet = new PieDataSet(entries, null);
        // 设置dp中PieChart切片之间的剩余空间。默认值：0-->无空间，最大20f
        dataSet.setSliceSpace(3f);
        // 设置此数据集高亮显示的PieChart切片从图表中心“偏移”的距离，默认为12f
        dataSet.setSelectionShift(5f);
        // 设置值显示的位置
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1Length(0.5f);

        // 数据和颜色
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#F08080"));
        colors.add(Color.parseColor("#BB86FC"));
        colors.add(Color.parseColor("#FF8C00"));
        colors.add(Color.parseColor("#03DAC5"));
        colors.add(Color.parseColor("#FFC0CB"));
        colors.add(Color.parseColor("#00D8FF"));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        // 为此数据对象包含的所有数据集设置自定义IValueFormatter。
        data.setValueFormatter(new PercentFormatter());
        // 设置此数据对象包含的所有数据集的值文本的大小（以dp为单位）。
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        //刷新
        pieChart.invalidate();
    }
}
