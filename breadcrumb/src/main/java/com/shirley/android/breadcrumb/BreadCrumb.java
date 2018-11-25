package com.shirley.android.breadcrumb;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZLJ on 2017/12/4.
 * 面包屑及内容控件
 */

public class BreadCrumb extends LinearLayout {

    View view;
    /**
     * 面包屑列表（横排）
     */
    private List<BreadCrumbBean> breadCrumbList = new ArrayList<>();
    /**
     * 面包屑内容列表（纵排）
     */
    private List<BreadCrumbBean> breadContentList = new ArrayList<>();
    /**
     * 面包屑适配器（横排）
     */
    private BreadCrumbAdapter breadCrumbAdapter;
    /**
     * 面包屑内容适配器（纵排）
     */
    private BreadContentAdapter breadContentAdapter;

    public BreadCrumb(Context context) {
        super(context);
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.bread_crumb, null);
        // 设置控件的布局方式为match_parent，默认是wrap_content
        view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(view);
    }

    public BreadCrumb(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BreadCrumb(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.bread_crumb, null);
        // 设置控件的布局方式为match_parent，默认是wrap_content
        view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(view);
    }

    /**
     * 对外接口
     * @param list 数据
     */
    public void setData(final List<BreadCrumbBean> list){

        // 横排面包屑
        // 首次只需要显示第一层数据
        // 因为面包屑需要不断添加移除，因此使用add方式，而不是赋值方式，add方式添加的是对象，而赋值方式赋的是引用
        breadCrumbList.add(list.get(0));
        breadCrumbAdapter = new BreadCrumbAdapter(breadCrumbList);

        // 竖排面包内容
        // 首次进入只需要显示第一层的子元素
        // 因为面包屑下面的内容是需要整个替换的，所以将要显示的内容赋值给一个变量，注意该变量不能执行clear,add,remove等操作，否则原始数据就会改变
        breadContentList = list.get(0).getChildren();
        breadContentAdapter = new BreadContentAdapter(breadContentList);

        // 横排面包屑点击事件
        final BreadCrumbView breadCrumbView = view.findViewById(R.id.bread_crumb);
        breadCrumbView.setAdapter(breadCrumbAdapter);
        breadCrumbAdapter.setOnItemClickListener(new BreadCrumbAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, BreadCrumbBean data) {
                // 移除面包屑中点击的位置之后的所有面包屑
                for (int i = breadCrumbList.size() - 1; i > position; i--){
                    breadCrumbList.remove(i);
                }
                // 更新面包屑
                breadCrumbAdapter.notifyDataSetChanged();

                // 显示当前点击的内容的子内容
                breadContentList = data.getChildren();
                breadContentAdapter.setData(breadContentList);
                // 更新面包屑内容
                breadContentAdapter.notifyDataSetChanged();
            }
        });

        // 竖排面包屑内容
        BreadContentView breadContentView = view.findViewById(R.id.bread_content);
        breadContentView.setAdapter(breadContentAdapter);
        breadContentAdapter.setOnItemClickListener(new BreadContentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, BreadCrumbBean data) {
                // 如果不是叶子节点
                if (data.getChildren() != null){
                    // 将当前点击的data添加到面包屑队列
                    breadCrumbList.add(data);
                    // 更新面包屑
                    breadCrumbAdapter.notifyDataSetChanged();
                    // 是面包屑自动滚动到队尾，即最新添加的那一项位置
                    breadCrumbView.scrollToPosition(breadCrumbList.size() - 1);
                    //更新面包屑内容
                    breadContentList = data.getChildren();
                    breadContentAdapter.setData(breadContentList);
                    breadContentAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
