package com.shirley.slidemenu.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shirley.slidemenu.R;
import com.shirley.slidemenu.entity.CategoryContent;

import java.util.List;

/**
 * 目录列表适配器<br>
 * Created by ZLJ on 2018/8/8
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryContent> mData;
    /**
     * 上一个被选中的项
     */
    private CategoryViewHolder mLastSelectedHolder;
    /**
     * 当前选中项的索引值
     */
    private int mCurSelectedIndex;
    private OnCategoryItemClickListener mOnCategoryItemClickListener;

    public CategoryAdapter(List<CategoryContent> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent,
                false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, final int position) {
        holder.textView.setText(mData.get(position).getCategory().getName());
        if (mCurSelectedIndex == position){
            holder.textView.setSelected(true);
            mLastSelectedHolder = holder;
        } else {
            holder.textView.setSelected(false);
        }
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastSelectedHolder != null){
                    mLastSelectedHolder.textView.setSelected(false);
                }
                holder.textView.setSelected(true);
                mLastSelectedHolder = holder;
                mCurSelectedIndex = position;
                if (mOnCategoryItemClickListener != null){
                    mOnCategoryItemClickListener.onCategoryItemClick(mData.get(position).getCategory().getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view);
        }
    }

    public interface OnCategoryItemClickListener {
        void onCategoryItemClick(String clickedCategory);
    }

    public void setOnCategoryItemClickListener(
            OnCategoryItemClickListener onCategoryItemClickListener) {
        this.mOnCategoryItemClickListener = onCategoryItemClickListener;
    }

    /**
     * 更新选中效果，由外部类SlideMenuView调用
     * @param pos
     */
    public void updateChosenCategory(int pos){
        mCurSelectedIndex = pos;
        notifyDataSetChanged();
    }
}
