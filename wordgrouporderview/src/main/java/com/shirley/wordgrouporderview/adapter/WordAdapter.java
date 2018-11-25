package com.shirley.wordgrouporderview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shirley.wordgrouporderview.R;
import com.shirley.wordgrouporderview.bean.Word;

import java.util.List;

/**
 * Created by ZLJ on 2018/3/20.
 * 单词适配器
 */
public class WordAdapter extends RecyclerView.Adapter<WordAdapter.MyViewHolder> {

    private List<Word> mData;

    public WordAdapter(List<Word> data) {
        this.mData = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_word, parent,
                false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvText.setText(mData.get(position).getWord());
        if (position < mData.size() - 1){
            if (!mData.get(position).getGroup().equals(mData.get(position + 1).getGroup())){
                holder.viewLine.setVisibility(View.GONE);
            } else {
                holder.viewLine.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvText;
        private View viewLine;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tv_text);
            viewLine = itemView.findViewById(R.id.view_line);
        }
    }

}
