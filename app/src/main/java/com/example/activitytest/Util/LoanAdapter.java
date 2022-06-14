package com.example.activitytest.Util;

import android.app.AlertDialog;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.activitytest.Database.DatabaseHelper;
import com.example.activitytest.Fragment.InOutComeFragment;
import com.example.activitytest.Fragment.LoanFragment;
import com.example.activitytest.R;

import java.text.NumberFormat;
import java.util.List;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.ViewHolder>{
    private static final String TAG = "MainActivity";

    private List<Loan> LoanList;
    private final Context context;

    // ViewBinderHelper 辅助保存/恢复 侧滑菜单 的打开/关闭状态
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public LoanAdapter(List<Loan> loanList, Context context){
        LoanList = loanList;
        this.context = context;
        if (context == null){
            Log.e(TAG, "InOutComeAdapter: context is null");
        }
        else {
            Log.e(TAG, "InOutComeAdapter: context is not null");
        }
        binderHelper.setOpenOnlyOne(true); // 每次只能打开一个侧滑窗口
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final SwipeRevealLayout swipeLayout;
        private final View deleteLayout;
        private final View item;
        private final TextView sourceText;
        private final TextView timeText;
        private final TextView valueText;

        // 构造函数
        public ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe_layout_loan);
            deleteLayout = itemView.findViewById(R.id.IV_delete_loan);
            item = itemView.findViewById(R.id.loan_item);
            sourceText = itemView.findViewById(R.id.TV_loan_source);
            timeText = itemView.findViewById(R.id.TV_loan_time);
            valueText = itemView.findViewById(R.id.TV_loan_value);
            // Log.e(TAG, "ViewHolder构造函数已完成");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Log.e(TAG, "onCreateViewHolder begin");

        // 缓存对 View 的子视图的引用
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.loan_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder begin:" + position);
        // 通过position获取InOutcome对象
        Loan loan = LoanList.get(position);
        String source = loan.getSource(); // 获取InOutcome对象的kind
        holder.sourceText.setText(source);
        String time = loan.getTime(); // 获取InOutcome对象的time
        holder.timeText.setText(time);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        Double DValue = loan.getValue(); // 获取InOutcome对象的value
        String SValue = nf.format(DValue); // 转为 string 类型
        holder.valueText.setText(SValue);
        Log.e(TAG, "onBindViewHolder: " + source + " " + time + " " + SValue);

        // 绑定侧滑菜单，需要提供定义数据对象的唯一字符串id。
        int Ind = loan.getInd();
        String id = loan.getId();
        binderHelper.bind(holder.swipeLayout, id);
        holder.deleteLayout.setOnClickListener(v -> {
            Log.e(TAG, "删除点击事件：");

            AlertDialog.Builder builder = new AlertDialog.Builder
                    (context, R.style.AlertDialogCustom);
            builder.setTitle("Warning");
            builder.setMessage("是否确定删除？");
            builder.setCancelable(false);
            builder.setPositiveButton("删除", (dialogInterface, i) -> {
                LoanList.remove(Ind);
                DatabaseHelper.delete_DB("Loan", id);
                LoanFragment.refresh_itemList();
                notifyItemRemoved(position);
                // 避免越界
                notifyItemRangeChanged(0, getItemCount());
                Log.e(TAG, "onBindViewHolder: " + v.getParent());
                Log.e(TAG, "onBindViewHolder position: " + position);
            });
            builder.setNegativeButton("取消", (dialogInterface, i) -> {
                dialogInterface.cancel(); // 关闭弹窗
                binderHelper.closeLayout(id); // 隐藏侧滑窗口
            });
            builder.create().show();
        });

        // 点击种类
        holder.item.setOnClickListener(view -> {
            // 加载弹窗视图
            AlertDialog.Builder builder = new AlertDialog.Builder
                    (context, R.style.AlertDialogCustom);
            View DialogView = View.inflate(context, R.layout.loan_dialog_item, null);

            // 控件实例化
            Spinner spinner = DialogView.findViewById(R.id.loan_kind_spinner);
            String[] strings = context.getResources().getStringArray(R.array.loan_kind_array);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                    (context, android.R.layout.simple_list_item_1, strings);
            spinner.setAdapter(arrayAdapter);

            TextView TV_loan_time = DialogView.findViewById(R.id.TV_loan_time);
            EditText ED_loan_value = DialogView.findViewById(R.id.ED_loan_amount);

            // 点击事件
            TV_loan_time.setOnClickListener(view1 ->
                    InOutComeFragment.setDatePickerDialog(TV_loan_time));

            /* 设置控件初始内容 */
            // 初始化spinner对应的数组
            int old_ind = 0;
            for (int i = 0; i < strings.length; i++){
                if (source.equals(strings[i])){
                    old_ind = i;
                    break;
                }
            }
            spinner.setSelection(old_ind);
            TV_loan_time.setText(time);
            ED_loan_value.setText(SValue);

            // 构建弹窗
            builder.setTitle("修改账目");
            builder.setView(DialogView);
            builder.setCancelable(false);
            builder.setPositiveButton("保存", (dialogInterface, i) -> {
                LoanFragment.save_loan(this, "update", spinner,
                        TV_loan_time, ED_loan_value, id, Ind);
                LoanList = LoanFragment.LoanList;
            });
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
        });
    }

    @Override
    public int getItemCount() {
        return LoanList.size();
    }
}