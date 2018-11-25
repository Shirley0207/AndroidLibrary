package com.shirley.android.breadcrumb;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by ZLJ on 2017/12/1.
 * 面包屑内容View
 */

public class BreadContentView extends RecyclerView {

    /**
     * 构造方法
     * @param context
     */
    public BreadContentView(Context context) {
        super(context);
        LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        this.setLayoutManager(layoutManager);
    }

    /**
     * 构造方法
     * @param context
     * @param attrs
     */
    public BreadContentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法
     * @param context
     * @param attrs
     * @param defStyle
     */
    public BreadContentView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        this.setLayoutManager(layoutManager);
    }
}
