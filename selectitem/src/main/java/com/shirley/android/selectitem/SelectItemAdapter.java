package com.shirley.android.selectitem;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ZLJ on 2017/11/30.
 * 单选多选适配器
 */

public class SelectItemAdapter extends RecyclerView.Adapter<SelectItemAdapter.ViewHolder> {

    private List<ItemBean> data;
    // true --> 单选， false --> 多选
    private boolean isSingle;

    /**
     * 构造方法
     * @param data 数据
     * @param isSingle 是否单选，true为单选，false为多选
     */
    public SelectItemAdapter(List<ItemBean> data, boolean isSingle) {
        this.data = data;
        this.isSingle = isSingle;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.text.setText(data.get(position).getText());
        if (data.get(position).isSelected()){
            // 选中状态
            holder.image.setImageResource(R.drawable.icon_selected);
        } else {
            // 未选中状态
            holder.image.setImageResource(R.drawable.icon_unselected);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSingle){
                    // 单选
                    refreshState();
                    data.get(position).setSelected(true);
                } else {
                    // 多选
                    boolean b = !data.get(position).isSelected();
                    data.get(position).setSelected(b);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout layout;
        TextView text;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            text = itemView.findViewById(R.id.text);
            image = itemView.findViewById(R.id.icon);
        }
    }

    /**
     * 刷新所有状态，即将所有项的状态都改为未选中状态
     */
    public void refreshState(){
        for (int i = 0; i < data.size(); i++){
            data.get(i).setSelected(false);
        }
    }
}
