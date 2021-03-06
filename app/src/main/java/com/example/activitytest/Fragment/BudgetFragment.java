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
            // ??????????????????
            Dialog();
        }
    }

    public void Dialog() {
        // ??????????????????
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = View.inflate(getContext(), R.layout.budget_dialog_item, null);

        // ???????????????
        EditText input_et = view.findViewById(R.id.budget_dialog_et);
        input_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});

        // ????????????
        builder.setTitle("");
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("??????", (dialog, which) -> {
            String STmp = input_et.getText().toString();
            budget_value = Float.parseFloat(STmp);
            editor.putFloat("budget_value", budget_value);
            editor.apply();
            LinearLayoutInit();
            initPieChart();
        });
        builder.setNegativeButton("??????", (dialog, which) -> {
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
                    // ???????????????????????????????????????????????????
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

        // ?????????
        pieChart = rootView.findViewById(R.id.PieChart);
        initPieChart();

        return rootView;
    }

    private void refresh_sum_out(){
        // ???????????????????????????
        List<InOutcome> OutComeList = DatabaseHelper.InOutcome_ALL("Outcome");
        for (int i=0; i< OutComeList.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);
            String NowMonth = String.valueOf(month + 1); // ????????????
            Log.e(TAG, "onCreateView NowMonth:" + NowMonth);
            String date_time = OutComeList.get(i).getTime(); // ??????????????????
            Log.e(TAG, "onCreateView ??????: " + date_time);
            String regex = "-(.*)-"; // ????????????
            /* ???????????? */
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(date_time);
            while (matcher.find()) {
                String date_month = matcher.group(1); // ??????????????????
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
        Remaining_budget_value.setText(String.valueOf(budget_value - sum_out)); // ????????????
        budget_value_tx.setText(String.valueOf(budget_value)); // ?????????
        outcome_value_tx.setText(String.valueOf(sum_out)); // ??????
    }

    private void initPieChart(){
        pieChart.setUsePercentValues(true); // ????????????????????????????????????

        pieChart.setDragDecelerationFrictionCoef(0.95f); // ????????????
        pieChart.animateXY(1400, 1400); // ??????

        pieChart.getDescription().setEnabled(false); // ????????????????????????
        pieChart.getLegend().setEnabled(false); // ???????????????

        pieChart.setDrawHoleEnabled(true); // true?????????????????????
        pieChart.setHoleColor(Color.WHITE); // ????????????
        pieChart.setDrawCenterText(true); // ???????????????true???????????????????????????????????????

        // ?????????????????????????????????0=???????????????255=??????????????????????????????100???
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setHoleRadius(45f); // ????????????
        pieChart.setTransparentCircleRadius(61f); // ?????????

        pieChart.setRotationAngle(0); // ???????????????
        // ????????????????????????true
        pieChart.setRotationEnabled(true);
        // ???????????????false????????????????????????????????????????????????????????????????????????????????????????????????????????????true
        pieChart.setHighlightPerTapEnabled(true);

        // ??????
        ArrayList<PieEntry> entries = new ArrayList<>();

        float percentage;
        if (sum_out > budget_value) {
            // ?????????????????????
            percentage = (int) ((sum_out-budget_value) / budget_value) * 100;
            entries.add(new PieEntry(percentage, "????????????"));
            setData(entries, "overflow"); //????????????
            pieChart.setCenterText("?????????");
            pieChart.setCenterTextSize(30f);
            pieChart.setCenterTextColor(Color.parseColor("#CD5C5C"));
        }
        else {
            // ??????????????????
            percentage = (int) ((budget_value - sum_out) / budget_value * 100);
            entries.add(new PieEntry(percentage, "????????????"));
            entries.add(new PieEntry(100 - percentage, "??????"));
            setData(entries, "normal"); //????????????
            pieChart.setCenterText("");
        }
        pieChart.setCenterTextSize(30f);
        // ??????????????????????????????????????????????????????????????????????????????
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(20f);
    }

    //????????????
    private void setData(ArrayList<PieEntry> entries, String mode) {
        PieDataSet dataSet = new PieDataSet(entries, null);
        // ??????dp???PieChart??????????????????????????????????????????0-->??????????????????20f
        dataSet.setSliceSpace(3f);
        // ?????????????????????????????????PieChart??????????????????????????????????????????????????????12f
        dataSet.setSelectionShift(5f);

        // ???????????????
        ArrayList<Integer> colors = new ArrayList<>();
        if (mode.equals("overflow")) {
            colors.add(Color.parseColor("#CD5C5C"));
        }
        else if (mode.equals("normal")) {
            colors.add(Color.parseColor("#87CEFA"));
            colors.add(R.color.Linen);
        }
        else {
            Log.e(TAG, "setData ????????????");
        }
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        // ?????????????????????????????????????????????????????????IValueFormatter???
        data.setValueFormatter(new PercentFormatter());
        // ????????????????????????????????????????????????????????????????????????dp???????????????
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
        //??????
        pieChart.invalidate();
    }
}
