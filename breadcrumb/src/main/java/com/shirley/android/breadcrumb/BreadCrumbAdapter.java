package com.shirley.android.breadcrumb;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ZLJ on 2017/12/1.
 * 面包屑适配器
 */

public class BreadCrumbAdapter extends RecyclerView.Adapter<BreadCrumbAdapter.ViewHolder> {

    /**
     * 面包屑数据
     */
    private List<BreadCrumbBean> data;
    /**
     * 点击事件监听器
     */
    private OnItemClickListener listener;

    /**
     * 构造方法
     * @param data
     */
    public BreadCrumbAdapter(List<BreadCrumbBean> data) {
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bread_crumb, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.text.setText(data.get(position).getText());
        if (position == 0){
            // 第一项将箭头去掉
            holder.image.setVisibility(View.GONE);
        } else {
            // 显示箭头
            holder.image.setVisibility(View.VISIBLE);
        }
        if (position == data.size() - 1){
            // 最后一项字体颜色为灰色，而且要设置监听器为null
            holder.text.setTextColor(Color.parseColor("#999999"));
            holder.text.setOnClickListener(null);
        } else {
            // 不是最后一项，都要设置监听事件
            holder.text.setTextColor(Color.parseColor("#104E8B"));
            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, position, data.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView text;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.text);
            image = itemView.findViewById(R.id.image);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position, BreadCrumbBean data);
    }
}