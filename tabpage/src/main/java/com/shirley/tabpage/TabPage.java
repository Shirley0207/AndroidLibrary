package com.shirley.tabpage;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

/**
 * 自定义控件--标签页式笔记本效果
 * Created by ZLJ on 2018/8/9
 */
public class TabPage extends LinearLayout {

    /**
     * 默认颜色值
     */
    private final int[] COLORS_DEFAULT = {getResources().getColor(R.color.bisque),
            getResources().getColor(R.color.navajoWhite), getResources().getColor(R.color.pink),
            getResources().getColor(R.color.plum), getResources().getColor(R.color.lightSalmon),
            getResources().getColor(R.color.orange), getResources().getColor(R.color.orangered),
            getResources().getColor(R.color.lightCoral), getResources().getColor(R.color.indianRed),
            getResources().getColor(R.color.maroon)};

    private Context mContext;
    /**
     * 左边的Tab布局
     */
    private LinearLayout mTabLayout;
    /**
     * 右边Page帧布局的父容器ScrollView
     */
    private ScrollView mContainerScrollView;
    /**
     * ScrollView的GradientDrawable对象，可设置ScrollView的背景颜色
     */
    private GradientDrawable mContainerScrollViewBg;
    /**
     * 右边的Page帧布局
     */
    private FrameLayout mContentLayout;
    /**
     * 数据
     */
    private List<TabBean> mData;
    /**
     * 上一个选中的Tab
     */
    private TextView mLastSelectedTab;
    /**
     * 第一个Tab
     */
    private TextView mFirstTab;

    public TabPage(Context context) {
        this(context, null);
    }

    public TabPage(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabPage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_tab_page, this);
        mTabLayout = view.findViewById(R.id.ll_tab);
        mContainerScrollView = view.findViewById(R.id.sv_container);
        mContentLayout = view.findViewById(R.id.fl_container);
    }

    /**
     * 设置数据<br>
     * 外界调用入口方法
     * @param tabs 数据
     * @throws Exception 若数据大于10条，则抛出异常
     */
    public void setData(List<TabBean> tabs) throws Exception {
        if (tabs.size() > 10){
            throw new Exception("The max count is 10");
        }
        mData = tabs;
        // 循环判断，如果外界未赋颜色值，则使用默认颜色值
        for (int i = 0; i < mData.size(); i++){
            if (mData.get(i).getTabColor() == 0){
                mData.get(i).setTabColor(COLORS_DEFAULT[i]);
            }
        }
        // 添加Tab
        addTab(mData);
        // 设置右边内容布局颜色，默认为第一个tab的颜色
        mContainerScrollViewBg = (GradientDrawable) mContainerScrollView.getBackground();
        mContainerScrollViewBg.setColor(mData.get(0).getTabColor());
        // 设置右边布局的内容，如果第一项的page不为空，则默认添加第一项的page到右边的帧布局中
        if (mData.get(0).getTabPage() != null){
            mContentLayout.addView(mData.get(0).getTabPage());
        }
    }

    /**
     * 依据内容添加tab
     * @param tabs Tabs的数据
     */
    private void addTab(final List<TabBean> tabs){
        for (int i = 0; i < tabs.size(); i++){
            //渲染布局并设置大小
            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_tab, null);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            // 让Tabs均分高度
            params.weight = 1;
            view.setLayoutParams(params);
            // 设置背景颜色
            if (i == 0){
                // 第一个tab采用bg_top(左上角为圆角)
                view.setBackground(getResources().getDrawable(R.drawable.bg_top));
            } else if (i == tabs.size() - 1){
                // 最后一个tab采用bg_bottom(左下角为圆角)
                view.setBackground(getResources().getDrawable(R.drawable.bg_bottom));
            } else {
                // 中间的tab采用bg_middle(无圆角)
                view.setBackground(getResources().getDrawable(R.drawable.bg_middle));
            }
            // 设置相应的背景颜色
            GradientDrawable viewBg = (GradientDrawable) view.getBackground();
            viewBg.setColor(tabs.get(i).getTabColor());
            // 展示tab的TextView
            ((TextView)view).setText(tabs.get(i).getTabName());
            // 默认选中第一项，设置第一项Tab的选中效果
            if (i == 0){
                mFirstTab = (TextView) view;
                // 存储第一项为上一项选中Tab，以便选中其他项之后清除上一项的选中效果
                mLastSelectedTab = mFirstTab;
                mLastSelectedTab.setTextSize(14);
                mLastSelectedTab.setSelected(true);
            }
            // 将Tab添加到Tab的线性布局中
            mTabLayout.addView(view);
            final int finalI = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 清除上一项的选中效果
                    mLastSelectedTab.setTextSize(12);
                    mLastSelectedTab.setSelected(false);
                    // 设置该项的选中效果
                    view.setSelected(true);
                    ((TextView)view).setTextSize(14);
                    // 存储该项为上一项选中的tab
                    mLastSelectedTab = (TextView) view;
                    // 将右边的ScrollView容器的颜色改为当前点击的tab的颜色
                    mContainerScrollViewBg.setColor(mData.get(finalI).getTabColor());
                    // 移除所有的view
                    mContentLayout.removeAllViews();
                    // 添加当前tab所代表的view
                    if (mData.get(finalI).getTabPage() != null){
                        mContentLayout.addView(mData.get(finalI).getTabPage());
                    }
                    // 需要注意的是，fullScroll方法不能直接被调用
                    // 因为Android很多函数都是基于消息队列来同步，所以需要异步操作，
                    // addView完之后，不等于马上就会显示，而是在队列中等待处理，
                    // 虽然很快，但是如果立即调用fullScroll， view可能还没有显示出来，
                    // 所以会出现不能滑动到顶部的效果，应该通过handler在新线程中更新
                    mContainerScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            // 滑动到顶部
                            mContainerScrollView.fullScroll(ScrollView.FOCUS_UP);
                        }
                    });
                }
            });
        }
    }

    /**
     * 设置ScrollView容器显示第一个tab所代表的view<br>
     * 如果是tab和tabPage是一次性构造好的，则不需要调用该方法，只需要调用setData()<br>
     * 如果tab和tabPage是分步构造的，则设置好tabPage之后可能还需要调用该方法，让控件默认显示第一个tab
     */
    public void initFirstTabPage(){
        // 先移除page布局上的所有view
        mContentLayout.removeAllViews();
        // 设置ScrollView的背景颜色为第一项tab的颜色，呈现出当前被选中的是第一个tab的效果
        mContainerScrollViewBg.setColor(mData.get(0).getTabColor());
        // 使ScrollView滑动到顶部
        mContainerScrollView.scrollTo(0, 0);
        // 加载第一个tabPage
        mContentLayout.addView(mData.get(0).getTabPage());
        // 如果上一个选中tab不为空，则恢复上一个tab为未选中状态
        if (mLastSelectedTab != null){
            mLastSelectedTab.setTextSize(12);
            mLastSelectedTab.setSelected(false);
        }
        // 设置第一个tab为选中状态
        if (mFirstTab != null){
            mFirstTab.setSelected(true);
            mFirstTab.setTextSize(14);
        }
        // 保存第一个tab为上一个选中tab
        mLastSelectedTab = mFirstTab;
    }
}
