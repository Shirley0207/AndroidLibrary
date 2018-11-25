package com.shirley.android.tabbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Created by ZLJ on 2017/11/22.
 */

public class TabButtonRecyclerView extends RecyclerView {

    private Context context;
    private OnItemViewClickListener listener;
    /**
     * Tab Button 的颜色
     */
    private int mainColor;

    public TabButtonRecyclerView(Context context) {
        super(context);
        this.context = context;
        // 为了是使该控件在布局文件内就有横排展示的预览效果
        LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        this.setLayoutManager(layoutManager);
    }

    public TabButtonRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabButtonRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        // TypedArray是存储资源数组的容器，它可以通过obtainStyledAttributes()创建出来
        // 不过创建完了，如果不再使用了，请注意调用recycle()把它释放掉
        // 通过obtainStyledAttributes获得一组值赋给TypedArray(数组)，这一组值来自于res/values/attrs.xml
        // 中的name="TabButtonRecyclerView"的declare-styleable中
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabButtonRecyclerView, defStyle, 0);
        // 默认color.xml中定义的mainColor
        mainColor = typedArray.getColor(R.styleable.TabButtonRecyclerView_mainColor, getResources().getColor(R.color.mainColor));
        // 为了是使该控件在布局文件内就有横排展示的预览效果
        LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        this.setLayoutManager(layoutManager);

    }

    public void setOnItemViewClickListener(OnItemViewClickListener listener) {
        this.listener = listener;
    }

    /**
     * 对外接口
     * @param list button内容
     */
    public void setData(List<TabButtonItemBean> list) {

        TabButtonRecyclerAdapter adapter = new TabButtonRecyclerAdapter(list, context, mainColor);
        this.setAdapter(adapter);
        adapter.setOnItemClickListener(new TabButtonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, TabButtonItemBean data) {
                listener.onItemViewClick(position, data);
            }
        });
    }

    public interface OnItemViewClickListener {
        void onItemViewClick(int position, TabButtonItemBean data);
    }
}
