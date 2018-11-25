package com.shirley.searchfilter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shirley.searchfilter.adapter.AnimatedChoiceListAdapter;
import com.shirley.searchfilter.entity.SingleChoiceData;
import com.shirley.searchfilter.widget.AnimatedExpandableListView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by ZLJ on 2018/5/16
 * 包含收缩动画的二级列表的自定义View
 */
public class ChoiceListView extends LinearLayout implements View.OnClickListener {

    private AnimatedExpandableListView mAnimatedExpandableListView;
    private AnimatedChoiceListAdapter mAnimatedChoiceListAdapter;
    private List<SingleChoiceData> mData;

    public ChoiceListView(Context context, List<SingleChoiceData> data) {
        super(context);
        mData = data;
        LayoutInflater.from(context).inflate(R.layout.view_choice_list, this);
        mAnimatedExpandableListView = findViewById(R.id.animated_expandable_list_view);
        mAnimatedChoiceListAdapter = new AnimatedChoiceListAdapter(context, mAnimatedExpandableListView, mData);
        mAnimatedExpandableListView.setAdapter(mAnimatedChoiceListAdapter);
        mAnimatedExpandableListView.setGroupIndicator(null);
        // 默认DividerHeight为2，因为效果不理想，因此就原生的Divider的height设置为0，而自己添加了Divider
        mAnimatedExpandableListView.setDividerHeight(0);

        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // 调用EventBus事件机制，去执行回退操作（移除当前view）
        EventBus.getDefault().post("");
    }

}
