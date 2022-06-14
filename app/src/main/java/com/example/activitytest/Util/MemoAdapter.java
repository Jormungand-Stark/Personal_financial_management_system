package com.example.activitytest.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.activitytest.Activity.MemoContentActivity;
import com.example.activitytest.Activity.MemoTitleActivity;
import com.example.activitytest.Database.DatabaseHelper;
import com.example.activitytest.R;

import java.util.List;

/** 自定义适配器 */
public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder>{
    private static final String TAG = "MemoAdapter";

    private final List<Memo> memoList;
    private final Context context;

    // ViewBinderHelper 辅助保存/恢复每个视图的打开/关闭状态
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    // 构造函数
    public MemoAdapter(List<Memo> memoList, Context context){
        this.memoList = memoList;
        this.context = context;
    }

    /*
     * 仅当方向更改时需要恢复打开/关闭状态时。在
     * {@link Activity#onSaveInstanceState(Bundle)} 中调用此方法
     */
    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    /*
     * 仅当方向更改时需要恢复打开/关闭状态时。在
     * {@link Activity#onRestoreInstanceState(Bundle)} 中调用此方法
     */
    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }

    // 重写 ViewHolder 类，实例化 memoTitleText、memoContentText
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View item; // 我们希望拿到整个item的view
        private final SwipeRevealLayout swipeLayout;
        private final View deleteLayout;
        private final TextView memoTitleText;
        private final TextView memoContentText;

        // 构造函数
        public ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.recyclerView_item);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            deleteLayout = itemView.findViewById(R.id.IV_delete_memo);
            memoTitleText = itemView.findViewById(R.id.memo_title_item);
            memoContentText = itemView.findViewById(R.id.memo_content_item);
            // Log.e(TAG, "ViewHolder构造函数已完成");
        }
    }

    /* 以下三个为继承自 RecyclerView.Adapter 的函数 */
    /** 当 RecyclerView 创建子项 item 时调用。
     * 可以手动创建新 View，也可以从 XML 布局文件 inflate 。
     * 使用 onBindViewHolder(ViewHolder, int, List) 时，新的 ViewHolder 将用于显示适配器的 items 。
     * 由于它将被重复用于显示数据集中的不同 items，
     * 因此最好缓存对 View 的子视图的引用以避免不必要的 findViewById(int) 调用。 */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Log.e(TAG, "onCreateViewHolder begin");
        binderHelper.setOpenOnlyOne(true); // 每次只能打开一个侧滑窗口
        // 缓存对 View 的子视图的引用
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memo_title_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        // 点击 RecyclerView 中备忘录标题时，启动 MemoContentActivity
        holder.item.setOnClickListener((View v)->{
            Log.e(TAG, "点击备忘标题列表子项");
            // 获取点击项的News实例
            Memo memo = memoList.get(holder.getBindingAdapterPosition());
            // 启动新的活动显示备忘录内容
            Intent intent = new Intent(context, MemoContentActivity.class);
            intent.putExtra("mode","update"); // update模式启动备忘内容活动
            intent.putExtra("memo_title", memo.getTitle());
            intent.putExtra("memo_content", memo.getContent());
            intent.putExtra("memo_ind", memo.getInd());
            intent.putExtra("memo_id", memo.getId());
            context.startActivity(intent);

        });
        return holder;
    }

    /** 更新 ViewHolder#itemView 的内容以反映给定位置的 item。
     * 注意，与 ListView 不同的是，如果 item 在数据集中的位置发生变化，
     * RecyclerView 不会再次调用该方法，除非 item 本身失效或者无法确定新的位置。
     * 因此，应该只在获取该方法内部的相关数据项时使用 position 参数，并且不应保留该参数的副本。
     * 如果稍后需要某个项目的位置（例如在单击侦听器中），
     * 请使用 ViewHolder#getBindingAdapterPosition()，它将获得更新后的适配器位置。*/
    // 将获得的 Memo 实例作为 RecyclerView 子项控件 TextView 的值
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder begin: " + position);
        binderHelper.setOpenOnlyOne(true);

        Memo memo = memoList.get(position);
        holder.memoTitleText.setText(memo.getTitle());
        holder.memoContentText.setText(memo.getContent());

        // 保存/恢复打开/关闭状态。
        // 需要提供定义数据对象的唯一字符串id。
        int Ind = memo.getInd();
        String id = memo.getId();
        binderHelper.bind(holder.swipeLayout, id);
        holder.deleteLayout.setOnClickListener(v -> {
            Log.e(TAG, "删除点击事件：");

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context, R.style.AlertDialogCustom);
            builder.setTitle("Warning");
            builder.setMessage("是否确定删除？");
            builder.setCancelable(false);
            builder.setPositiveButton("删除", (dialogInterface, i) -> {
                memoList.remove(Ind);
                DatabaseHelper.delete_DB("Memo", id);
                MemoTitleActivity.refresh_MemoList();
                notifyItemRemoved(position);
                notifyItemRangeChanged(0, getItemCount()); // 避免删越界
                Log.e(TAG, "onBindViewHolder position: " + position);
            });
            builder.setNegativeButton("取消", (dialogInterface, i) -> {
                dialogInterface.cancel(); // 关闭弹窗
                binderHelper.closeLayout(id); // 隐藏侧滑窗口
            });
            builder.create().show();

        });
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }
}