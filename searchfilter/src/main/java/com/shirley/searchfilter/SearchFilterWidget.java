package com.shirley.searchfilter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.shirley.searchfilter.entity.SearchFilterData;

import java.util.List;

/**
 * Created by ZLJ on 2018/5/14
 * 自定义查询筛选控件
 */
public class SearchFilterWidget extends LinearLayout implements View.OnClickListener, SearchFilterView.OnCompleteListener {

    public static int screenWidth;
    private Context mContext;
    private SearchFilterView mSearchFilterView;
    private OnCompleteListener mListener;
    // 背景View，在添加抽屉view时要先添加背景view
    private View bgView;
    // 抽屉view
    private LinearLayout layoutDrawer;
    // 判断当前控件是否已展开，默认关闭
    private static boolean isOpen;

    public SearchFilterWidget(Context context) {
        this(context, null);
    }

    public SearchFilterWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        LayoutInflater.from(context).inflate(R.layout.widget_search_filter, this);
        bgView = findViewById(R.id.bg_view);
        layoutDrawer = findViewById(R.id.layout_drawer);
        mSearchFilterView = findViewById(R.id.search_filter_view);
        mSearchFilterView.setOnCompleteListener(this);
        LinearLayout layoutTransparent = findViewById(R.id.layout_transparent);
        layoutTransparent.setOnClickListener(this);

        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        screenWidth = size.x;
    }

    /**
     * 对外设置数据的方法
     * @param mData
     */
    public void setData(List<SearchFilterData> mData) {
        mSearchFilterView.setData(mData);
        mSearchFilterView.setOnCompleteListener(this);
    }

    /**
     * 对外方法,控制控件的打开与关闭
     */
    public void switchOpenClose(){
        if (!isOpen){
            // 当前控件出于关闭状态，则展开
            open();
        } else {
            // 当前控件处于展开状态，则关闭
            close();
        }
    }

    /**
     * 添加打开动画
     * 先添加一层view，透明度从0变为0.5
     * 再添加自定义抽屉view，从屏幕右侧移到屏幕左侧
     */
    private void open() {
        // 抽屉View的移动动画
        TranslateAnimation translateAnimation = new TranslateAnimation(screenWidth, 0, 0, 0);
        translateAnimation.setDuration(300);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 0.5f);
        alphaAnimation.setDuration(300);
        // 设置动画定格在最后一帧
        alphaAnimation.setFillAfter(true);

        // 如果是以XML形式使用的，第一次时，bgView和layoutDrawer是有parent，添加它们时需先移除parent
        if (bgView.getParent() != null) {
            ((ViewGroup) bgView.getParent()).removeView(bgView);
        }
        if (layoutDrawer.getParent() != null) {
            ((ViewGroup) layoutDrawer.getParent()).removeView(layoutDrawer);
        }
        ((Activity) mContext).addContentView(bgView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        bgView.startAnimation(alphaAnimation);
        ((Activity) mContext).addContentView(layoutDrawer, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layoutDrawer.startAnimation(translateAnimation);
        isOpen = true;
    }

    /**
     * 将抽屉view移回屏幕右侧，将背景view透明度还原到0，并且移除它们
     */
    private void close() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, screenWidth, 0, 0);
        translateAnimation.setDuration(300);
        translateAnimation.setFillAfter(true);
        layoutDrawer.startAnimation(translateAnimation);
        // 移除抽屉view
        ((ViewGroup) layoutDrawer.getParent()).removeView(layoutDrawer);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 0);
        alphaAnimation.setDuration(300);
        // 设置动画定格在最后一帧
        alphaAnimation.setFillAfter(true);
        bgView.startAnimation(alphaAnimation);
        // 移除背景view
        ((ViewGroup) bgView.getParent()).removeView(bgView);
        isOpen = false;
    }

    @Override
    public void onClick(View v) {
        close();
        // 如果需要在点击透明区域时就传出数据，则需要放出下面这段代码
        /*if (mListener != null){
            mListener.onComplete(mSearchFilterView.getResult());
        }*/
    }

    @Override
    public void onComplete(List<SearchFilterData> chosenData) {
        close();
        if (mListener != null) {
            mListener.onComplete(chosenData);
        }
    }

    /**
     * 筛选完成点击确定向外传输数据的接口
     */
    public interface OnCompleteListener {
        /**
         * 筛选完成点击确定向外传输数据的方法
         *
         * @param chosenData
         */
        void onComplete(List<SearchFilterData> chosenData);
    }

    public void setOnCompleteListener(OnCompleteListener mListener) {
        this.mListener = mListener;
    }
}
