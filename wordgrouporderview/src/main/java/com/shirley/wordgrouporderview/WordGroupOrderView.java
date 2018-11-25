package com.shirley.wordgrouporderview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shirley.wordgrouporderview.adapter.WordAdapter;
import com.shirley.wordgrouporderview.bean.Word;
import com.shirley.wordgrouporderview.helper.OrderWordHelper;

import java.util.List;

/**
 * 自定义控件
 * <br>1.根据单词首字母分组显示
 * <br>2.提供右侧索引导航栏
 * <p>Created by ZLJ on 2018/3/19.
 */
public class WordGroupOrderView extends LinearLayout implements IndexBar.UpdateRecyclerViewListener,
        TitleItemDecoration.DrawOverTextChangedListener {

    private RecyclerView mRecyclerView;
    private Context mContext;
    private IndexBar mIndexBar;
    private TextView mTvSideBarHint;
    private LinearLayoutManager mLayoutManager;
    /**
     * 是否需要真实数据索引，默认不需要
     */
    private boolean mNeedRealIndex;

    public WordGroupOrderView(Context context) {
        this(context, null);
    }

    public WordGroupOrderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WordGroupOrderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.widget_word_group_order,
                this);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mIndexBar = view.findViewById(R.id.index_bar);
        mTvSideBarHint = view.findViewById(R.id.tv_side_bar_hint);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WordGroupOrderView,
                defStyleAttr, 0);
        mNeedRealIndex = typedArray.getBoolean(R.styleable.WordGroupOrderView_needRealIndex,
                false);
        typedArray.recycle();
    }

    public WordGroupOrderView setNeedRealIndex(boolean mNeedRealIndex) {
        this.mNeedRealIndex = mNeedRealIndex;
        return this;
    }

    /**
     * 入口方法--设置数据
     * @param data 要显示的数据列表
     */
    public void setData(List<String> data){
        // 借助类OrderWordHelper进行排序与转换
        List<Word> wordList = OrderWordHelper.orderWord(data);
        WordAdapter adapter = new WordAdapter(wordList);
        mRecyclerView.setAdapter(adapter);
        // 初始化TitleItemDecoration
        TitleItemDecoration titleItemDecoration = new TitleItemDecoration(mContext, wordList);
        // 设置监听事件（悬停字母一改变便发送通知）
        titleItemDecoration.setDrawOverChangedListener(this);
        mRecyclerView.addItemDecoration(titleItemDecoration);
        // 初始化IndexBar
        mIndexBar.setTvToShowPressedLetter(mTvSideBarHint)
                .setNeedRealIndex(mNeedRealIndex)
                .setSourceData(wordList)
                .setUpdateRecyclerViewListener(this);
    }

    @Override
    public void updateRecyclerView(int position) {
        // IndexBar被点击或触摸，则让RecyclerView滚动到相应位置
        // mRecyclerView.scrollToPosition()或smoothScrollToPosition()，不能滚动到准确位置，
        // 大概是因为有ItemDecoration的存在
        mLayoutManager.scrollToPositionWithOffset(position, 0);
    }

    @Override
    public void drawOverTextChanged(String drawOverGroup) {
        // 悬停字母改变，则通知IndexBar进行更新，以保持悬停字母与IndexBar一致
        mIndexBar.updateIndexBar(drawOverGroup);
    }

}
