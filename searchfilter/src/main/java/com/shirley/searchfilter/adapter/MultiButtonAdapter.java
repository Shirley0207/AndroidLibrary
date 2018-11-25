package com.shirley.searchfilter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shirley.searchfilter.R;

import java.util.List;

/**
 * Created by ZLJ on 2018/5/8
 * 多选按钮View适配器
 */
public class MultiButtonAdapter extends BaseAdapter implements View.OnClickListener {

    private List<String> data;
    private List<String> chosenValues;
    private OnItemSelectedListener mListener;
    private Context mContext;

    public MultiButtonAdapter(Context context, List<String> data, List<String> chosenValues) {
        mContext = context;
        this.data = data;
        this.chosenValues = chosenValues;
    }

    public void setData(List<String> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_multi_button, null);
            viewHolder = new ViewHolder();
            viewHolder.btnTx = convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.btnTx.setText(data.get(position));
        // 当被选中时可以由tag知道当前选中的是哪一个
        viewHolder.btnTx.setTag(data.get(position));
        viewHolder.btnTx.setOnClickListener(this);
        // 下面这段代码主要是为了方便实现重置功能
        // 先设置为未选中状态
        viewHolder.btnTx.setSelected(false);
        viewHolder.btnTx.setTextColor(mContext.getResources().getColor(R.color.grey));
        // 在循环遍历当前btn是否被选中，若选中，则改变状态及文字颜色
        for (String value: chosenValues) {
            if (viewHolder.btnTx.getText().equals(value)){
                viewHolder.btnTx.setSelected(true);
                viewHolder.btnTx.setTextColor(mContext.getResources().getColor(R.color.white));
            }
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if (v.isSelected()){
            v.setSelected(false);
            ((TextView) v).setTextColor(mContext.getResources().getColor(R.color.grey));
            if (mListener != null){
                mListener.onItemCanceled((String) v.getTag());
            }
        } else {
            v.setSelected(true);
            ((TextView) v).setTextColor(mContext.getResources().getColor(R.color.white));
            if (mListener != null){
                mListener.onItemSelected((String) v.getTag());
            }
        }
    }

    private class ViewHolder{
        TextView btnTx;
    }

    /**
     * 按钮被选中或取消选中事件监听器
     */
    public interface OnItemSelectedListener{
        /**
         * 按钮被选中
         * @param tag
         */
        void onItemSelected(String tag);

        /**
         * 按钮取消选中
         * @param tag
         */
        void onItemCanceled(String tag);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener mListener) {
        this.mListener = mListener;
    }
}
