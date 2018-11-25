package com.shirley.animatedfloatexpandablelistview.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.shirley.animatedfloatexpandablelistview.entity.GroupInfo;

/**
 * Created by ZLJ on 2018/5/31
 * 伸展动画
 */
public class ExpandAnimation extends Animation {

    private View view;
    private int baseHeight;
    private int delta;
    private GroupInfo groupInfo;

    /**
     * 伸展动画
     * @param view 要伸展的view
     * @param startHeight 开始伸展的高度
     * @param endHeight 结束伸展的高度
     * @param info 当前组的信息
     */
    public ExpandAnimation(View view, int startHeight, int endHeight, GroupInfo info) {
        this.view = view;
        baseHeight = startHeight;
        delta = endHeight - startHeight;
        groupInfo = info;

        this.view.getLayoutParams().height = startHeight;
        // 为什么要调用requestLayout，注释掉从效果上看也没什么影响
        // 请求布局，控件会重新执行onMeasure()和onLayout()
        this.view.requestLayout();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        // 动画生效的方法，这个方法会被回调多次，参数interpolatedTime会由0.0增加到1.0，从而实现动画效果
        int val;
        if (interpolatedTime < 1.0f){
            val = baseHeight + (int) (delta * interpolatedTime);
        } else {
            val = baseHeight + delta;
        }
        view.getLayoutParams().height = val;
        groupInfo.dummyHeight = val;
        view.requestLayout();
    }
}
