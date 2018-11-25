package com.shirley.slidemenu.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shirley.slidemenu.R;
import com.shirley.slidemenu.entity.ContentCategory;

import java.util.List;

/**
 * 内容适配器
 * Created by ZLJ on 2018/8/9
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private List<ContentCategory> mData;

    public ContentAdapter(List<ContentCategory> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent,
                false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        holder.tvDescription.setText(mData.get(position).getContent().getDesc());
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivPic;
        private TextView tvDescription;

        public ContentViewHolder(View itemView) {
            super(itemView);
            ivPic = itemView.findViewById(R.id.iv_pic);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }
}
