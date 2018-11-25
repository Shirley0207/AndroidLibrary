package com.shirley.wordgrouporderview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import com.shirley.wordgrouporderview.bean.Word;

import java.util.List;

/**
 * 主要参考自：
 * 作者：张旭童
 * 链接：https://www.jianshu.com/p/0d49d9f51d2c
 * 來源：简书
 * 简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
 *
 * 部分参考自：
 * 作者：带心情去旅行
 * 链接：https://www.jianshu.com/p/b335b620af39
 * 來源：简书
 * 简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
 */
public class TitleItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * ItemDecoration的背景颜色
     */
    private static int COLOR_TITLE_BG;
    /**
     * ItemDecoration的字体颜色
     */
    private static int COLOR_TITLE_FONT = Color.BLACK;
    /**
     * 悬停字母的字体颜色
     */
    private static int COLOR_DRAW_OVER;
    /**
     * ItemDecoration的高
     */
    private int mHeight;
    private List<Word> mData;
    private Paint mPaint = new Paint();
    /**
     * 用于存放测量文字Rect
     */
    private Rect mBoundRect = new Rect();
    private static String mDrawOverGroup;
    private DrawOverTextChangedListener mDrawOverTextChangedListener;
    /**
     * 存储的是上一个悬停字母
     */
    private String mLastDrawOverGroup;

    public TitleItemDecoration(Context context, List<Word> data) {
        super();
        this.mData = data;
        // 初始化颜色
        COLOR_TITLE_BG = context.getResources().getColor(R.color.gainsboro);
        COLOR_DRAW_OVER = context.getResources().getColor(R.color.lightBlue);
        // 初始化高度和字体大小
        mHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
                context.getResources().getDisplayMetrics());
        int titleFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
                context.getResources().getDisplayMetrics());
        // 设置画笔相关属性
        mPaint.setTextSize(titleFontSize);
        mPaint.setAntiAlias(true);
        // 先赋值第一个group，之后好进行比较
        mLastDrawOverGroup = mData.get(0).getGroup();
    }

    // 最先调用，界面要显示几个item就会调用几次该方法
    // 该方法的作用是，使用outRect,为即将要绘制的ItemDecoration预留出相应的空间
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        // 界面上要刷新的Item的position
        int position = ((RecyclerView.LayoutParams)view.getLayoutParams()).getViewLayoutPosition();
        // RecyclerView的item position在重置时可能为-1.保险点判断一下
        if (position > -1){
            // 第一项Item肯定是需要绘制title的,所以预留出mHeight的高度
            if (position == 0){
                outRect.set(0, mHeight, 0, 0);
            } else {
                if (mData.get(position).getGroup() != null
                        && !mData.get(position).getGroup().equals(mData.get(position - 1).getGroup())){
                    // 不为空且跟前一个group不一样了，说明是新的分类，也要绘制title
                    outRect.set(0, mHeight, 0, 0);
                } else {
                    outRect.set(0, 0, 0, 0);
                }
            }
        }
    }

    // 其次调用该方法，绘制RecyclerView
    // 之后随着滚动，会和onDrawOver一起循环交替调用，当然，是onDraw先执行
    // 在该方法里循环要绘制的item，判断是否需要绘制ItemDecoration
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft(); // RecyclerView的左边界
        final int right = parent.getWidth() - parent.getPaddingRight(); // RecyclerView的右边界
        final int childCount = parent.getChildCount(); // 界面上要显示的child的个数
        for (int i = 0; i < childCount; i++){
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();
            // 通过child的布局参数获得child在列表中的position，而不是在界面中的position
            int position = params.getViewLayoutPosition();
            // RecyclerView的item position在重置时可能为-1.保险点判断一下
            if (position > -1){
                if (position == 0){
                    // 第一项则需要绘制Title
                    drawTitleArea(c, left, right, child, params, position);
                } else {
                    if (mData.get(position).getGroup() != null &&
                            !mData.get(position).getGroup().equals(mData.get(position - 1).getGroup())){
                        // 不为空且跟前一个group不一样了，说明是新的分类，也要绘制title
                        drawTitleArea(c, left, right, child, params, position);
                    }
                }
            }
        }
    }

    /**
     * 绘制Title区域背景和文字的方法
     * @param canvas 画布
     * @param left RecyclerView的左边边界
     * @param right RecyclerView的右边边界
     * @param child RecyclerView的一个子项
     * @param params 子项的布局参数
     * @param position 子项的位置参数
     */
    private void drawTitleArea(Canvas canvas, int left, int right, View child,
                               RecyclerView.LayoutParams params, int position){
        mPaint.setColor(COLOR_TITLE_BG);
        // 绘制ItemDecoration的矩形背景
        // getTop()是View顶部距离父容器顶部的距离，topMargin是view的layout_marginTop
        canvas.drawRect(left, child.getTop() - params.topMargin - mHeight, right,
                child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
        // 测量文字边界，为了能够居中绘制文字
        mPaint.getTextBounds(mData.get(position).getGroup(), 0,
                mData.get(position).getGroup().length(), mBoundRect);
        // 绘制ItemDecoration的文字
        canvas.drawText(mData.get(position).getGroup(), child.getPaddingLeft(),
                child.getTop() - params.topMargin - (mHeight / 2 - mBoundRect.height() / 2),
                mPaint);
    }

    // 最后调用该方法，绘制悬停View，浮在最上层
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        // 获得界面上第一项在列表中的索引值
        int pos = ((LinearLayoutManager)(parent.getLayoutManager())).findFirstVisibleItemPosition();
        // 根据索引值获得悬停文字
        mDrawOverGroup = mData.get(pos).getGroup();
        // 通过索引值获得相应的child
        View child = parent.findViewHolderForLayoutPosition(pos).itemView;
        int bottom; // 悬停View的底部到父容器顶部的距离，其实也是悬停View的实时高度
        if (isNewGroupComing(pos + 1)) {
            // 下一个组马上到达顶部
            // 获取高度值和当前child的底部到父容器顶部的距离中的较小值，
            // 为了实现当前悬停View被下一个悬停View顶上去消失不见的效果
            bottom = Math.min(child.getBottom(), mHeight);
        } else {
            // 普通情况
            bottom = mHeight;
        }
        mPaint.setColor(COLOR_TITLE_BG);
        // 通过bottom会控制悬停View的上移效果，其实是通过多次调用，每次调用减少bottom
        // 当上一个悬停项移除界面，则开始绘制下一组的悬停ItemDecoration
        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(),
                parent.getRight() - parent.getPaddingRight(), bottom, mPaint);
        mPaint.setColor(COLOR_DRAW_OVER);
        // 测量文字边界，为了能够居中绘制文字
        mPaint.getTextBounds(mDrawOverGroup, 0, mDrawOverGroup.length(), mBoundRect);
        // 垂直居中绘制文字
        c.drawText(mDrawOverGroup, child.getPaddingLeft(),
                bottom - (mHeight / 2 - mBoundRect.height() / 2), mPaint);
        // 如果当前悬停字母改变了，则通知外部控件WordGroupOrderView，让它去通知IndexBar进行更新
        if (!mLastDrawOverGroup.equals(mDrawOverGroup)){
            if (mDrawOverTextChangedListener != null){
                mDrawOverTextChangedListener.drawOverTextChanged(mDrawOverGroup);
            }
            mLastDrawOverGroup = mDrawOverGroup;
        }
    }

    /**
     * 判断是否有新的组到达
     * @param pos
     * @return
     */
    private boolean isNewGroupComing(int pos) {
        if (pos == 0) {
            // 判断是不是组中的第一个位置
            return true;
        } else {
            // 根据前一个组名，判断当前是否为新的组
            String prevGroup = mData.get(pos - 1).getGroup();
            String group = mData.get(pos).getGroup();
            return !TextUtils.equals(prevGroup, group);
        }
    }

    /**
     * 悬停字母改变监听器
     */
    public interface DrawOverTextChangedListener {
        /**
         * 悬停字母改变
         */
        void drawOverTextChanged(String drawOverGroup);
    }

    public void setDrawOverChangedListener(DrawOverTextChangedListener mDrawOverTextChangedListener) {
        this.mDrawOverTextChangedListener = mDrawOverTextChangedListener;
    }
}
