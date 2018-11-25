package com.shirley.searchfilter.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by ZLJ on 2018/05/28.
 * 重写computeScrollDeltaToGetChildRectOnScreen()，设置返回值为0
 * 为了解决点击其他地方让LinearLayout(ScrollView的子组件)获取焦点(从而让输入框失去焦点)时，
 * ScrollView会自动滑到顶部
 */
public class MyScrollView extends ScrollView {

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }
}
