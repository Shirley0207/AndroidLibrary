package com.shirley.slidemenu;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shirley.slidemenu.adapter.CategoryAdapter;
import com.shirley.slidemenu.adapter.ContentAdapter;
import com.shirley.slidemenu.decoration.CategoryItemDecoration;
import com.shirley.slidemenu.entity.CategoryContent;
import com.shirley.slidemenu.entity.Content;
import com.shirley.slidemenu.entity.ContentCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义控件--滑动菜单栏
 * Created by ZLJ on 2018/8/13
 */
public class SlideMenuView extends LinearLayout implements
        CategoryAdapter.OnCategoryItemClickListener,
        CategoryItemDecoration.OnOverTextChangedListener {

    private Context mContext;
    private RecyclerView mRVCategory;
    private RecyclerView mRVContent;
    /**
     * category列表适配器
     */
    private CategoryAdapter mCategoryAdapter;
    /**
     * 自定义的目录ItemDecoration
     */
    private CategoryItemDecoration mCategoryItemDecoration;
    /**
     * 结构化数据列表，每项内容是一个category下对应一个content列表，主要供左边的category列表使用
     */
    private List<CategoryContent> mCategoryContentData;
    /**
     * content数据列表，每个content都关联着有所属category的信息，主要供右边的content列表使用
     */
    private List<ContentCategory> mContentCategoryData;
    /**
     * 目录列表的整体高度
     */
    private int mCategoryRVHeight;
    /**
     * 标记当前是否点击了category<br>
     * 主要是用于区分当前引起内容滑动的是手指滑动屏幕还是点击了目录列表
     * 如果是点击了目录列表，则不需要滑动目录列表，如果是手指滑动屏幕引起内容滑动从而引起目录改变，
     * 则需要将当前目录滑动到目录列表中部
     */
    private boolean isClickedCategory;
    /**
     * 保存记录当前点击的目录
     */
    private String mClickedCategory;
    /**
     * content列表悬停的目录
     */
    private String mOverText;

    public SlideMenuView(Context context) {
        this(context, null);
    }

    public SlideMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_category_content,
                this);
        mRVCategory = view.findViewById(R.id.rv_category);
        mRVCategory.setLayoutManager(new LinearLayoutManager(context));
        mRVContent = view.findViewById(R.id.rv_content);
        mRVContent.setLayoutManager(new LinearLayoutManager(context));
    }

    /**
     * 设置数据，供外部调用
     * @param categoryContentList 结构化数据列表，一个category下对应一个content列表
     */
    public void setData(List<CategoryContent> categoryContentList){
        mCategoryContentData = categoryContentList;
        // 类别列表
        mCategoryAdapter = new CategoryAdapter(mCategoryContentData);
        // 注册监听Category被点击事件
        mCategoryAdapter.setOnCategoryItemClickListener(this);
        // 设置类别列表适配器
        mRVCategory.setAdapter(mCategoryAdapter);

        // 内容列表
        // 先将数据转换成内容类表所需的数据列表
        mContentCategoryData = convertData(mCategoryContentData);
        // 实例化自定义的ItemDecoration
        mCategoryItemDecoration = new CategoryItemDecoration(mContext, mContentCategoryData);
        // 注册监听悬停文字改变事件
        mCategoryItemDecoration.setOnOverTextChangedListener(this);
        // 添加自定义的ItemDecoration
        mRVContent.addItemDecoration(mCategoryItemDecoration);
        // 设置内容列表适配器
        mRVContent.setAdapter(new ContentAdapter(mContentCategoryData));
        // 为内容列表添加滚动监听器
        mRVContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    // 每次content列表停止滚动时，都要更新一下category的选中位置，
                    // 以保证category的选中位置与content列表悬停的category一致
                    mCategoryAdapter.updateChosenCategory(getPosInCategory(mOverText));
                    // 一次滚动停止，恢复默认值
                    isClickedCategory = false;
                }
            }
        });
    }

    /**
     * 转换数据，将{@link CategoryContent}类型的数据列表转换为{@link ContentCategory}类型的数据列表
     * @param categoryContentList 目录内容数据列表
     * @return 内容目录数据列表
     */
    private List<ContentCategory> convertData(List<CategoryContent> categoryContentList){
        List<ContentCategory> data = new ArrayList<>();
        for (CategoryContent categoryContent : categoryContentList){
            for (Content content : categoryContent.getContents()){
                data.add(new ContentCategory(content, categoryContent.getCategory()));
            }
        }
        return data;
    }

    @Override
    public void onCategoryItemClick(String clickedCategory) {
        // 点击Category项，则滚动content列表
        smoothScrollByInContent(getPosInContent(clickedCategory), getPosInCategory(clickedCategory));
        // 并且设置isClickedCategory为true，表明当前是因为点击category才引起的content列表滚动
        isClickedCategory = true;
        // 并记录下当前点击的category文字
        mClickedCategory = clickedCategory;
    }

    /**
     * 返回内容列表里第一个属于参数category下的内容的索引值
     * @param category 指定目录
     * @return 返回属于指定目录下的第一个内容索引值
     */
    private int getPosInContent(String category){
        for (int i = 0; i < mContentCategoryData.size(); i++){
            if (mContentCategoryData.get(i).getCategory().getName().equals(category)){
                return i;
            }
        }
        return -1;
    }

    /**
     * 平滑滚动内容列表<br>
     * 计算当前位置与要移动到的位置之间的偏移量，使用smoothScrollBy进行平滑滚动
     * @param toPosInContent 要滚动到的位置在Content列表中的索引值
     * @param toPosInCategory 要滚动到的位置在Category列表中的索引值
     */
    private void smoothScrollByInContent(int toPosInContent, int toPosInCategory){
        // 内容列表中第一个可见item在content列表中的索引值
        int firstVisiblePosInContent = mRVContent.getChildLayoutPosition(
                mRVContent.getChildAt(0));
        // 内容列表中第一个可见item所属的目录在目录列表中的索引值
        int firstCategoryPos = getPosInCategory(
                mContentCategoryData.get(firstVisiblePosInContent).getCategory().getName());
        // 内容列表中一项内容的高度,getChildAt(0)获取的就是列表中第一个可见对象
        int aContentItemHeight = mRVContent.getChildAt(0).getHeight();
        // 纵向偏移量为：项高度 * （项个数-1）+ 首项.getBottom + 项Title.高度 * 项Title的个数
        mRVContent.smoothScrollBy(0, aContentItemHeight *
                (toPosInContent - firstVisiblePosInContent - 1) +
                mRVContent.getChildAt(0).getBottom() +
                mCategoryItemDecoration.getHeight() * (toPosInCategory - firstCategoryPos - 1));
    }

    @Override
    public void onOverTextChanged(String overText) {
        // content列表的悬停文字发生改变
        if (mCategoryRVHeight <= 0){
            // 获取category列表的整体高度，用于将选中category定位在中间位置
            mCategoryRVHeight = mRVCategory.getHeight();
        }
        mOverText = overText;
        if (!isClickedCategory){
            // 如果是非点击Category触发的情况，即通过手指滑动content列表触发
            // 通过手指滑动触发，则需要实时改变选中的category的位置，
            // 使当前category自始至终都处于目录列表的中间位置
            TextView curCategoryTV = null;
            // 遍历寻找当前的category依附的TextView
            // mRVCategory.getChildCount()获取的是屏幕上可见的child的个数
            for (int i = 0; i < mRVCategory.getChildCount(); i++){
                if (((TextView) mRVCategory.getChildAt(i)).getText().equals(mOverText)){
                    curCategoryTV = (TextView) mRVCategory.getChildAt(i);
                    break;
                }
            }
            // 获取一项category的高度
            int aCategoryHeight = mRVCategory.getChildAt(0).getHeight();
            int offset = 0; // 需要滑动的偏移量
            if (curCategoryTV == null){
                // 说明当前category不在屏幕可见范围之内
                // 则先获取可见范围内的第一项
                TextView first = (TextView) mRVCategory.getChildAt(0);
                // 当前选中category在category列表中的索引值
                int curPos = getPosInCategory(mOverText);
                // 第一个可见category在category列表中的索引值
                int firstPos = getPosInCategory(first.getText().toString());
                // 获取可见范围内的最后一项
                TextView last = (TextView) mRVCategory.getChildAt(
                        mRVCategory.getChildCount() - 1);
                // 最后一个可见category在category列表中的索引值
                int lastPos = getPosInCategory(last.getText().toString());
                if (curPos < firstPos){
                    // 说明当前目录在可见范围之上
                    // aCategoryHeight/2，以保证选中目录在中间位置
                    // 从上往下滑，偏移量为负数
                    offset = aCategoryHeight * (curPos - firstPos) -
                            (aCategoryHeight / 2 - first.getBottom()) - mCategoryRVHeight / 2;
                } else if (curPos > lastPos){
                    // 说明当前目录在可见范围之下
                    // 从下往上话，偏移量为正数
                    offset = aCategoryHeight * (curPos - lastPos) +
                            (mCategoryRVHeight - last.getTop()) + mCategoryRVHeight / 2;
                }
            } else {
                // 当前category刚好在屏幕可见范围之内
                // 加上aCategoryHeight/2，以保证选中目录在中间位置
                // 否则会以child的上边界作为中间分界线，这样效果会呈现出选中目录在中间偏下位置
                offset = curCategoryTV.getTop() + aCategoryHeight / 2 - mCategoryRVHeight / 2;
            }
            // 计算当前child到RecyclerView中间位置的偏移量，并进行滑动，以保证当前child位于中间位置
            mRVCategory.smoothScrollBy(0, offset, new LinearInterpolator());
            // 快速滑动的话，需要实现选中效果一个个滑下来，而不是直接显示到目的地
            int pos = getPosInCategory(mOverText);
            mCategoryAdapter.updateChosenCategory(pos);
        } else {
            // 如果是点击Category触发
            // 点击Category，则会触发content列表滚动。滚动过程中每一次overText改变都会触发到此方法，
            // 而只要overText还不等于点击的category，则什么不执行，直到overText等于点击的category，
            // 此时将isClickedCategory设为false，结束，
            // 这样设计的目的是到达点击category，只会滚动内容列表而不会滚动目录列表的效果
            if (mOverText.equals(mClickedCategory)){
                // 表明Content列表的ItemCategory已经滑动到点击的category了，则恢复isClickedCategory
                isClickedCategory = false;
            }
        }
    }

    /**
     * 根据传入的目录返回该目录在目录列表里的索引值
     * @param category 指定目录
     * @return 返回指定目录在目录列表里的索引值
     */
    private int getPosInCategory(String category){
        for (int i = 0; i < mCategoryContentData.size(); i++){
            if (mCategoryContentData.get(i).getCategory().getName().equals(category)){
                return i;
            }
        }
        return -1;
    }

}
