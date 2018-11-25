package com.shirley.android.tabbutton;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZLJ on 2017/11/23.
 */

public class TabButtonRecyclerAdapter extends RecyclerView.Adapter<TabButtonRecyclerAdapter.ViewHolder> {

    private List<TabButtonItemBean> data;
    private Context context;
    private OnItemClickListener mListener;
    private List<ViewHolder> viewHolders = new ArrayList<>();
    /**
     * Tab Button 的颜色
     */
    private int mainColor;

    public TabButtonRecyclerAdapter(List<TabButtonItemBean> data, Context context, int mainColor) {
        this.data = data;
        this.context = context;
        this.mainColor = mainColor;
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab_button, parent, false);
        // 实例化ViewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        holder.tvButtonText.setText(data.get(position).getText());
        if (position == 0){
            // 第一个button，左边圆角，默认选中状态
            holder.tvButtonText.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvButtonText.setBackgroundResource(R.drawable.shape_left_select);
            // shape_left_select.xml里是一个shape元素，设置该元素的颜色为用户指定的颜色
            GradientDrawable drawable =(GradientDrawable)holder.tvButtonText.getBackground();
            drawable.setColor(mainColor);
        } else if (position == data.size() - 1){
            // 最后一个button，右边圆角，默认未选中状态
            holder.tvButtonText.setTextColor(mainColor);
            holder.tvButtonText.setBackgroundResource(R.drawable.shape_right);
            // shape_right.xml里有是一个shape元素，关于颜色定义了两个元素，一个是solid，另一个是stroke
            // 因为是未选择状态，背景为白色，边框为主色调，因此设置stroke的颜色为用户指定的颜色
            GradientDrawable drawable =(GradientDrawable)holder.tvButtonText.getBackground();
            drawable.setStroke(2, mainColor);
        } else {
            // 中间button，没有圆角，默认未选中状态
            holder.tvButtonText.setTextColor(mainColor);
            holder.tvButtonText.setBackgroundResource(R.drawable.shape_middle);
            // 此处使用的是LayerDrawable，是因为shape_middle.xml内定义的是一个layer-list元素
            // layer-list是将多个图片或shape、selector等种效果按照顺序层叠起来，每一个是用item包裹起来
            // 而shape_middle.xml包含一个边框shape和一个内容shape
            // 又因是未选择状态，内容为白色，边框为主色调，故先取出第一个元素（表示边框的shape），设置颜色为主色调
            LayerDrawable layerDrawable = (LayerDrawable) holder.tvButtonText.getBackground();
            GradientDrawable drawable = (GradientDrawable)layerDrawable.getDrawable(0);
            drawable.setColor(mainColor);
        }

        if (mListener != null) {
            holder.tvButtonText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshAllViews();
                    holder.tvButtonText.setTextColor(context.getResources().getColor(R.color.white));
                    // 设置选中状态
                    if (position == 0) {
                        // 第一个button，左边圆角
                        holder.tvButtonText.setBackgroundResource(R.drawable.shape_left_select);
                        // 设置按钮背景为用户使用时是定的主色调
                        GradientDrawable drawable =(GradientDrawable)holder.tvButtonText.getBackground();
                        drawable.setColor(mainColor);
                    } else if (position == data.size() - 1) {
                        holder.tvButtonText.setBackgroundResource(R.drawable.shape_right_select);
                        // 设置按钮背景为用户使用时是定的主色调
                        GradientDrawable drawable =(GradientDrawable)holder.tvButtonText.getBackground();
                        drawable.setColor(mainColor);
                    } else {
                        holder.tvButtonText.setBackgroundResource(R.drawable.shape_middle_select);
                        // shape_middle_select.xml内是一个layer-list元素
                        LayerDrawable layerDrawable = (LayerDrawable) holder.tvButtonText.getBackground();
                        // 中间按钮的上下边框和内容在选中状态下颜色都是主色调
                        GradientDrawable drawable1 = (GradientDrawable) layerDrawable.getDrawable(0);
                        drawable1.setColor(mainColor);
                        GradientDrawable drawable2 = (GradientDrawable) layerDrawable.getDrawable(1);
                        drawable2.setColor(mainColor);
                    }

                    mListener.onItemClick(v, position, data.get(position));
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvButtonText;

        public ViewHolder(View itemView) {
            super(itemView);
            tvButtonText = itemView.findViewById(R.id.tv_button_text);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position, TabButtonItemBean data);
    }

    /**
     * 设置所有的button都为未选择状态
     */
    public void refreshAllViews() {
        ViewHolder view;
        for (int i = 0; i < viewHolders.size(); i++) {
            view = viewHolders.get(i);
            view.tvButtonText.setTextColor(mainColor);
            if (i == 0) {
                // 第一个button，左边圆角
                view.tvButtonText.setBackgroundResource(R.drawable.shape_left);
                LayerDrawable layerDrawable = (LayerDrawable) view.tvButtonText.getBackground();
                GradientDrawable drawable = (GradientDrawable)layerDrawable.getDrawable(0);
                // 未选中状态下都是内容为白色，边框为主色调
                drawable.setStroke(2, mainColor);
            } else if (i == viewHolders.size() - 1) {
                // 最后一个button，右边圆角
                view.tvButtonText.setBackgroundResource(R.drawable.shape_right);
                GradientDrawable drawable =(GradientDrawable)view.tvButtonText.getBackground();
                // 未选中状态下都是内容为白色，边框为主色调
                drawable.setStroke(2, mainColor);
            } else {
                // 中间button，没有圆角
                view.tvButtonText.setBackgroundResource(R.drawable.shape_middle);
                // shape_middle.xml没是一个layer-list元素，因此复杂些，第一个元素表示边框shape
                LayerDrawable layerDrawable = (LayerDrawable) view.tvButtonText.getBackground();
                GradientDrawable drawable = (GradientDrawable)layerDrawable.getDrawable(0);
                drawable.setColor(mainColor);
            }
        }
    }

}