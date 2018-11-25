package com.shirley.android.breadcrumb;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ZLJ on 2017/12/1.
 * 面包屑内容适配器
 */

public class BreadContentAdapter extends RecyclerView.Adapter<BreadContentAdapter.ViewHolder> {

    /**
     * 数据
     */
    private List<BreadCrumbBean> data;
    /**
     * 点击事件监听器
     */
    private OnItemClickListener listener;

    public BreadContentAdapter(List<BreadCrumbBean> data) {
        this.data = data;
    }

    public void setData(List<BreadCrumbBean> data) {
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bread_content, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.text.setText(data.get(position).getText());
        holder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, position, data.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout lay;
        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.text);
            lay = itemView.findViewById(R.id.lay);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position, BreadCrumbBean data);
    }
}
