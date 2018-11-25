package com.shirley.android.breadcrumb;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by ZLJ on 2017/12/1.
 * 面包屑View
 */

public class BreadCrumbView extends RecyclerView {

    /**
     * 构造方法
     * @param context
     */
    public BreadCrumbView(Context context) {
        super(context);
        // 为了是使该控件在布局文件内就有横排展示的预览效果
        LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        this.setLayoutManager(layoutManager);
    }

    /**
     * 构造方法
     * @param context
     * @param attrs
     */
    public BreadCrumbView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法
     * @param context
     * @param attrs
     * @param defStyle
     */
    public BreadCrumbView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 为了是使该控件在布局文件内就有横排展示的预览效果
        LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        this.setLayoutManager(layoutManager);
    }
}
