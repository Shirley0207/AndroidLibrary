package com.shirley.android.buttongroup;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZLJ on 2017/11/13.
 * ButtonGroup 适配器
 */

public class ButtonGroupAdapter extends BaseAdapter {

    List<ButtonGroupItemBean> dataList = new ArrayList<>();
    Activity activity;

    public ButtonGroupAdapter(Activity activity, List<ButtonGroupItemBean> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HoldView holdView;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_button_group, null);
            holdView = new HoldView();
            holdView.llLay = convertView.findViewById(R.id.ll_lay);
            holdView.tvDesc = convertView.findViewById(R.id.tv_desc);
            holdView.flFrame = convertView.findViewById(R.id.fl_frame);
            holdView.ivImage = convertView.findViewById(R.id.iv_image);
            holdView.tvNum = convertView.findViewById(R.id.tv_num);

            convertView.setTag(holdView);
        } else {
            holdView = (HoldView) convertView.getTag();
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holdView.llLay.getLayoutParams();
        // 个数小于5就以实际个数均分，大于5则以5均分，增加层数
        int count = dataList.size() > 5 ? 5 : dataList.size();
        params.width = Utils.getScreenWidth(activity) / count;
        holdView.llLay.setLayoutParams(params);

        holdView.tvDesc.setText(dataList.get(position).getDesc());
        holdView.ivImage.setImageBitmap(dataList.get(position).getImg());

        if (dataList.get(position).getNewMessage() > 0){
            // 新消息个数大于0就显示
            holdView.tvNum.setVisibility(View.VISIBLE);
            holdView.tvNum.setText(dataList.get(position).getNewMessage() + "");
        }

        return convertView;
    }

    class HoldView {

        LinearLayout llLay;
        FrameLayout flFrame;
        ImageView ivImage;
        TextView tvNum;
        TextView tvDesc;

    }
}
