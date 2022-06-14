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
import com.example.activitytest.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * <h3>术语表</h3>
 * <ul>
 *     <li><em>InOutComeAdapter:</em> {@link RecyclerView} 只是一个 {@link ViewGroup}，它只认识
 *     {@link View} ，不清楚构成 View 这个前端界面的 后端Data数据 的具体结构，因此，RecyclerView 需要一个
 *     {@link androidx.recyclerview.widget.RecyclerView.Adapter} 将 Data 转换为 RecyclerView 认识的
 *     {@link ViewHolder}。InOutComeAdapter 就是 {@link RecyclerView.Adapter} 的子类。
 *     后续文档中将 InOutComeAdapter 简称为 适配器Adapter。</li>
 *
 *      <li><em>child view:</em> {@link RecyclerView}列表 中的最小子元素，比如对于布局方式为
 *      线性布局LinearLayout 的 {@link RecyclerView}列表 来说，子视图child view 就是每一行。
 *      后续文档中将 child view 简称为 view。</li>
 *
 *      <li><em>LayoutManager:</em> 负责 view 布局的显示管理，有多种布局方式供选择，如：线性布局、网格
 *      布局等。 {@link androidx.recyclerview.widget.RecyclerView.LayoutManager} 只负责将 view 呈现在
 *      Recycle 中，并不直接负责对 view 的管理，view 的管理由下面的
 *      {@link androidx.recyclerview.widget.RecyclerView.Recycler} 负责。</li>
 *
 *     <li><em>Item:</em> view 是前端能看到的，在后端实现时，它是由一个自定义的类的对象
 *     子项item 实现的，InOutComeAdapter 中的 子项item 是 {@link InOutcome} 对象。</li>
 *
 *     <li><em>Position:</em> 正在操作的 子项item 在 适配器Adapter 中的位置.</li>
 *
 *     <li><em>Index:</em> 调用 {@link ViewGroup#getChildAt} 时使用的参数，是被使用的 view 的 索引Index。
 *     与 Position 形成对比。</li>
 *
 *     <li><em>Binding:</em> 将 view 与 适配器Adapter 中的某个 位置position 绑定。实际上也是
 *     把形成 view 的 子项item 与 位置position 绑定。
 *
 *     <li><em>inflate:</em> 将 xml文件资源 解析为 可编程对象。</li>
 *
 *     <li><em>ViewHolder:</em> 承载 子视图child view 的 子布局，InOutComeAdapter 中的 ViewHolder 是
 *     {@link ViewHolder}。</li>
 *
 *     <li><em>四级缓存：</em>
 *     <ul>
 *         <li><em>一级缓存 mAttachedScrap/mChangedScrap:</em> 缓存屏幕可见范围的
 *         {@link ViewHolder}。</li>
 *         <li><em>二级缓存 mCachedViews:</em> 按 child View 的 position 或 id 缓存滑动时即将与
 *         {@link RecyclerView} 分离的{@link ViewHolder}。</li>
 *         <li><em>三级缓存 mViewCacheExtension:</em> 开发者自行实现的缓存。</li>
 *         <li><em>四级缓存 mRecyclerPool:</em> ViewHolder缓存池，本质上是一个
 *         {@link android.util.SparseArray}，其中 key 是 ViewType(int类型)，value 存放的是
 *         {@link ArrayList <ViewHolder>}，默认每个 ArrayList 中最多存放5个 ViewHolder。</li>
 *     </ul></li>
 *
 *     <li><em>Recycle (view):</em> 管理不在前台的 View，也就是对 View 进行缓存，以便后续重用，避免每次
 *     都需要加载 view，显著提高性能。LayoutManager 在需要 View 的时候会向
 *     {@link androidx.recyclerview.widget.RecyclerView.Recycler} 进行索取，当 LayoutManager 不需要
 *     View (试图滑出)的时候，就直接将废弃的 View 丢给 Recycler。</li>
 *
 *     <li><em>Scrap (view):</em> 在布局期间已进入 临时分离temporarily detached 状态的子视图。
 *     Scrap views 可以在不与 parent RecyclerView 完全分离fully detached 的情况下重用。
 *     重用时需要做进一步判定：
 *     <ul>
 *         <li>如果不需要 rebinding重新绑定 则不需要修改。</li>
 *         <li>如果该 view 被视为 dirty，则由 适配器Adapter 进行修改。</li>
 *     </ul></li>
 *
 *     <li><em>Dirty (view):</em> 在显示之前必须由 适配器 重新绑定rebound 的子视图.</li>
 * </ul>
 *
 * 收支界面 {@link com.example.activitytest.Fragment.InOutComeFragment} 中 RecyclerView控件 的适配器。 */
public class InOutComeAdapter extends RecyclerView.Adapter<InOutComeAdapter.ViewHolder>{
    private static final String TAG = "MainActivity";

    private List<InOutcome> InOutcomeList;
    private final Context context;

    // ViewBinderHelper 辅助保存/恢复 侧滑菜单 的打开/关闭状态
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    /** 构造函数 constructed function
     * @param inOutcomeList - 存储 InOutcome 对象的数组，以时间顺序倒序存储 */
    public InOutComeAdapter(List<InOutcome> inOutcomeList, Context context){
        InOutcomeList = inOutcomeList;
        this.context = context;
        if (context == null){
            Log.e(TAG, "InOutComeAdapter: context is null");
        }
        else {
            Log.e(TAG, "InOutComeAdapter: context is not null");
        }
        binderHelper.setOpenOnlyOne(true); // 每次只能打开一个侧滑窗口
    }

    /** 重写 {@linkplain RecyclerView.ViewHolder} 类，对布局中各个控件进行管理 */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final SwipeRevealLayout swipeLayout;
        private final View deleteLayout;
        private final View item;
        private final TextView kindText;
        private final TextView timeText;
        private final TextView valueText;

        // 构造函数
        public ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe_layout_income);
            deleteLayout = itemView.findViewById(R.id.IV_delete_income);
            item = itemView.findViewById(R.id.income_item);
            kindText = itemView.findViewById(R.id.income_kind);
            timeText = itemView.findViewById(R.id.income_time);
            valueText = itemView.findViewById(R.id.income_value);
            // Log.e(TAG, "ViewHolder构造函数已完成");
        }
    }

    /* 以下三个为继承自 RecyclerView.Adapter 的函数 */
    /** 当 RecyclerView 创建子项 item 时调用。
     * 可以手动创建新 View，也可以从 XML 布局文件 inflate 。
     * 使用 onCreateViewHolder 时，新的 ViewHolder 将用于显示适配器的 items 。
     * 由于它将被重复用于显示数据集中的不同 items，
     * 因此最好缓存对 View 的子视图的引用以避免每次都要 inflate。 */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Log.e(TAG, "onCreateViewHolder begin");

        // 缓存对 View 的子视图的引用
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.income_item, parent, false);

        return new ViewHolder(view);
    }

    /** 更新 ViewHolder#itemView 的内容以反映给定位置的 item。
     * 注意，与 ListView 不同的是，如果 item 在数据集中的位置发生变化，
     * RecyclerView 不会再次调用该方法，除非 item 本身失效或者无法确定新的位置。
     * 因此，应该只在获取该方法内部的相关数据项时使用 position 参数，并且不应保留该参数的副本。
     * 如果稍后需要某个项目的位置（例如在单击侦听器中），
     * 请使用 ViewHolder#getBindingAdapterPosition()，它将获得更新后的适配器位置。*/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder begin:" + position);
        // 通过position获取InOutcome对象
        InOutcome inOutcome = InOutcomeList.get(position);
        String kind = inOutcome.getKind(); // 获取InOutcome对象的kind
        holder.kindText.setText(kind);
        String time = inOutcome.getTime(); // 获取InOutcome对象的time
        holder.timeText.setText(time);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        Double DValue = inOutcome.getValue(); // 获取InOutcome对象的value
        String SValue = nf.format(DValue); // 转为 string 类型
        holder.valueText.setText(SValue);
        Log.e(TAG, "onBindViewHolder: " + kind + " " + time + " " + SValue);

        // 绑定侧滑菜单，需要提供定义数据对象的唯一字符串id。
        int Ind = inOutcome.getInd();
        String id = inOutcome.getId();
        binderHelper.bind(holder.swipeLayout, id);
        // 点击删除垃圾桶图标进行删除
        String table;
        if (InOutComeFragment.MODE == 0){
            table = "Income";
        }
        else {
            table = "Outcome";
        }
        holder.deleteLayout.setOnClickListener(v -> {
            Log.e(TAG, "删除点击事件：");

            if (context == null){
                Log.e(TAG, "deleteLayout: context is null");
            }
            else {
                Log.e(TAG, "deleteLayout: context is not null " + context);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder
                    (context, R.style.AlertDialogCustom);
            builder.setTitle("Warning");
            builder.setMessage("是否确定删除？");
            builder.setCancelable(false);
            builder.setPositiveButton("删除", (dialogInterface, i) -> {
                InOutcomeList.remove(Ind);
                DatabaseHelper.delete_DB(table, id);
                InOutComeFragment.refresh_itemList();
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
            if (context == null) {
                Log.e(TAG, "item: context is null");
            }
            else {
                Log.e(TAG, "item: context isn't null " + context);
            }

            // 加载弹窗视图
            AlertDialog.Builder builder = new AlertDialog.Builder
                    (context, R.style.AlertDialogCustom);
            View DialogView = View.inflate(context, R.layout.in_out_dialog_item, null);

            // 控件实例化
            Spinner spinner = DialogView.findViewById(R.id.kind_spinner);
            String[] strings;
            assert context != null;
            if (InOutComeFragment.MODE == 0){
                strings = context.getResources().getStringArray(R.array.in_kind_array);
            }
            else {
                strings = context.getResources().getStringArray(R.array.out_kind_array);
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                    (context, android.R.layout.simple_list_item_1, strings);
            spinner.setAdapter(arrayAdapter);
            TextView time_tx = DialogView.findViewById(R.id.income_time_tv);
            EditText value_et = DialogView.findViewById(R.id.input_amount);

            // 点击事件
            time_tx.setOnClickListener(view1 -> InOutComeFragment.setDatePickerDialog(time_tx));

            /* 设置控件初始内容 */
            // 初始化spinner对应的数组
            int old_ind = 0;
            for (int i = 0; i < strings.length; i++){
                if (kind.equals(strings[i])){
                    old_ind = i;
                    break;
                }
            }
            spinner.setSelection(old_ind);
            time_tx.setText(time);
            value_et.setText(SValue);

            // 构建弹窗
            builder.setTitle("修改账目");
            builder.setView(DialogView);
            builder.setCancelable(false);
            builder.setPositiveButton("保存", (dialogInterface, i) -> {
                InOutComeFragment.save_InOutcome
                        (this, "update", spinner, time_tx, value_et, id, Ind);
                InOutcomeList = InOutComeFragment.InOutComeList;
            });
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
        return InOutcomeList.size();
    }
}