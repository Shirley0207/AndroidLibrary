package com.shirley.android.selectitem;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.List;

/**
 * Created by ZLJ on 2017/11/30.
 * 单选多选自定义View
 */

public class SelectItemView extends RecyclerView {

    private Context context;
    private boolean isSingle;

    public SelectItemView(Context context) {
        super(context);
        this.context = context;
    }

    public SelectItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectItemView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        // TypedArray是存储资源数组的容器，它可以通过obtainStyledAttributes()创建出来
        // 不过创建完了，如果不再使用了，请注意调用recycle()把它释放掉
        // 通过obtainStyledAttributes获得一组值赋给TypedArray(数组)，这一组值来自于res/values/attrs.xml
        // 中的name="SelectItemView"的declare-styleable中
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectItemView, defStyle, 0);
        // 默认true，单选
        isSingle = typedArray.getBoolean(R.styleable.SelectItemView_isSingle, true);
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    /**
     * 对外接口
     * @param list 数据
     */
    public void setData(List<ItemBean> list){
        SelectItemAdapter adapter = new SelectItemAdapter(list, isSingle);
        LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        this.setLayoutManager(layoutManager);
        this.setAdapter(adapter);
    }
}
