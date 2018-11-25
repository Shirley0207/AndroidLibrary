package com.shirley.searchfilter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shirley.searchfilter.entity.SearchFilterData;
import com.shirley.searchfilter.entity.SingleChoiceData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by ZLJ on 2018/5/8
 * 单项选择（网点选择）
 */
public class SingleChoiceView extends BaseView implements View.OnClickListener {

    private TextView chosenTx;
    // 包裹SingleChoiceView的父容器SearchFilterView的父容器
    private FrameLayout flContainer;
    // 单项选择列表View，二级页面，添加到flContainer里
    private ChoiceListView choiceListView;
    private List<SingleChoiceData> mData;

    public SingleChoiceView(Context context) {
        this(context, null);
    }

    public SingleChoiceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayout() {
        return R.layout.view_single_choice;
    }

    @Override
    protected void init() {
        chosenTx = findViewById(R.id.chosen);
        LinearLayout layout = findViewById(R.id.layout);
        layout.setOnClickListener(this);
    }

    @Override
    public void setData(SearchFilterData data) {
        super.setData(data);
        mData = (List<SingleChoiceData>) data.getContent();
        // 在前面加上全部分类
        SingleChoiceData allKinds = new SingleChoiceData();
        allKinds.setGroup("全部分类");
        allKinds.setChildren(null);
        mData.add(0, allKinds);
    }

    @Override
    public void reset() {
        super.reset();
        // 控件设置成默认值
        chosenTx.setText("全部");
        chosenTx.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
    }

    @Override
    public void onClick(View v) {
        if (choiceListView == null){
            choiceListView = new ChoiceListView(mContext, mData);
        }
        // 先将choiceListView添加到根布局
        ((Activity) mContext).addContentView(choiceListView, new LayoutParams(0, 0));
        // 再通过获得根布局从而获得根布局里存放抽屉view的帧布局容器
        flContainer = ((ViewGroup) choiceListView.getParent()).getChildAt(2).findViewById(R.id.fl_container);
        // 移除choiceListView的父布局
        ((ViewGroup) choiceListView.getParent()).removeView(choiceListView);
        choiceListView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // 抽屉View的移动动画
        TranslateAnimation translateAnimation = new TranslateAnimation(SearchFilterWidget.screenWidth, 0, 0, 0);
        translateAnimation.setDuration(300);
        // 将choiceListView添加到帧布局容器
        if (flContainer.indexOfChild(choiceListView) == -1){
            flContainer.addView(choiceListView);
        }
        // 开始从右边移到左边的动画
        choiceListView.startAnimation(translateAnimation);
    }

    /**
     * 选择完毕回调
     * @param chosenValue
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChosenEvent(String chosenValue){
        TranslateAnimation translateAnimation = new TranslateAnimation(0, SearchFilterWidget.screenWidth, 0, 0);
        translateAnimation.setDuration(300);
        translateAnimation.setFillAfter(true);
        choiceListView.startAnimation(translateAnimation);
        flContainer.removeView(choiceListView);

        if (!chosenValue.equals("")){
            chosenTx.setText(chosenValue);
            if (chosenValue.equals("全部")){
                chosenTx.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
            } else {
                chosenTx.setTextColor(mContext.getResources().getColor(R.color.indian_red));
            }
            result.setData(chosenValue);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }
}
