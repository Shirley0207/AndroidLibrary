package com.shirley.searchfilter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by ZLJ on 2018/5/8
 * 解决了在ScrollView中嵌套使用GridView导致只出现一行数据的问题
 */
public class MultiButtonGridView extends GridView {
    public MultiButtonGridView(Context context) {
        super(context);
    }

    public MultiButtonGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiButtonGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
