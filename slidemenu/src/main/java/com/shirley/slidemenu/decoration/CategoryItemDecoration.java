package com.shirley.slidemenu.decoration;

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

import com.shirley.slidemenu.entity.Category;
import com.shirley.slidemenu.entity.ContentCategory;

import java.util.List;

/**
 * 主要参考自:
 * 作者：张旭童
 * 链接：https://www.jianshu.com/p/0d49d9f51d2c
 * 來源：简书
 * 简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
 * <br>
 * 部分参考自：
 * 作者：带心情去旅行
 * 链接：https://www.jianshu.com/p/b335b620af39
 * 來源：简书
 * 简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
 * <br>
 * Created by ZLJ on 2018/8/8.
 */
public class CategoryItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint = new Paint();
    private List<ContentCategory> mData;
    private Rect mTextBoundRect = new Rect();
    /**
     * ItemDecoration的高度
     */
    private int mHeight;
    private OnOverTextChangedListener mOnOverTextChangedListener;
    /**
     * 上一个悬停目录，主要用于在两个目录交接时进行区分
     */
    private Category mLastOverCategory;

    public CategoryItemDecoration(Context context, List<ContentCategory> data) {
        this.mData = data;
        mHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30,
                context.getResources().getDisplayMetrics());
        int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
                context.getResources().getDisplayMetrics());
        // 设置画笔属性
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        // 默认保存第一个category
        mLastOverCategory = mData.get(0).getCategory();
    }

    /**
     * 可供外部控件SlideMenu调用，用来计算需要滑动的偏移量
     * @return ItemDecoration的高度
     */
    public int getHeight() {
        return mHeight;
    }

    // 最先调用，界面要显示几个item就会调用几次该方法
    // 该方法的作用是，使用outRect,为即将要绘制的ItemDecoration预留出相应的空间
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        // 获取当前刷新的view的position
        int position = ((RecyclerView.LayoutParams)view.getLayoutParams()).getViewLayoutPosition();
        // RecyclerView的item position在重置时可能为-1.保险点判断一下
        if (position > -1){
            if (position == 0){
                // 预留出mHeight的高度
                outRect.set(0, mHeight, 0, 0);
            } else {
                if (mData.get(position).getCategory() != null &&
                        !mData.get(position).getCategory().getName().equals(
                                mData.get(position - 1).getCategory().getName())){
                    // 当前数据的category不为空且跟上一个的category不一样了，说明是新的组，也要绘制
                    outRect.set(0, mHeight, 0, 0);
                } else {
                    outRect.set(0, 0, 0, 0);
                }
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        // 先获取界面上显示的child的数量
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft(); // 列表左边界
        int right = parent.getWidth() - parent.getPaddingRight(); // 列表右边界
        // 循环child，判断哪些child需要绘制ItemDecoration
        for (int i = 0; i < childCount; i++){
            // 取出child
            View child = parent.getChildAt(i);
            // 获取child的布局参数
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)
                    child.getLayoutParams();
            // 通过布局参数可获得该child在整个列表中的position
            int position = layoutParams.getViewLayoutPosition();
            // RecyclerView的item position在重置时可能为-1.保险点判断一下
            if (position > -1){
                if (position == 0){
                    // 第一项，需要绘制ItemDecoration
                    drawCategory(c, position, child, layoutParams, left, right);
                } else {
                    if (mData.get(position).getCategory() != null &&
                            !mData.get(position).getCategory().getName().equals(
                                    mData.get(position - 1).getCategory().getName())){
                        // 新的组，也需要绘制ItemDecoration
                        drawCategory(c, position, child, layoutParams, left, right);
                    }
                }
            }
        }
    }

    /**
     * 绘制ItemDecoration
     * @param canvas 画布
     * @param position 当前需要绘制ItemDecoration的child在列表中的索引值
     * @param child 当前需要绘制ItemDecoration的child
     * @param params 当前需要绘制ItemDecoration的child的布局参数
     * @param left 整个列表的左边界
     * @param right 整个列表的右边界
     */
    private void drawCategory(Canvas canvas, int position, View child,
                              RecyclerView.LayoutParams params, int left, int right){
        mPaint.setColor(Color.WHITE); // ItemDecoration的背景颜色为白色
        // 绘制ItemDecoration的背景矩形框
        canvas.drawRect(left, child.getTop() - params.topMargin - mHeight, right,
                child.getTop() - params.topMargin, mPaint);
        mPaint.getTextBounds(mData.get(position).getCategory().getName(), 0,
                mData.get(position).getCategory().getName().length(), mTextBoundRect);
        mPaint.setColor(Color.BLACK);
        // 获得画笔的FontMetrics，用来计算baseLine，因为绘制文字时的y坐标需要baseline
        // FontMetrics似乎会受mPaint.setTextSize()的影响
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        // 计算baseLine值,用来确定字母能够绘制在竖直居中的位置
        int baseline = (int) ((mHeight - fontMetrics.bottom - fontMetrics.top) / 2);
        // 居中绘制category
        canvas.drawText(mData.get(position).getCategory().getName(), child.getPaddingLeft(),
                child.getTop() - params.topMargin - (mHeight - baseline), mPaint);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        // 获得界面上第一项在列表中的索引值
        int pos = ((LinearLayoutManager)parent.getLayoutManager()).findFirstVisibleItemPosition();
        String overText = mData.get(pos).getCategory().getName();
        View child = parent.findViewHolderForLayoutPosition(pos).itemView;
        int bottom; // 悬停View的底部到父容器顶部的距离，其实也是悬停View的实时高度
        if (isNewGroupComing(pos + 1)){
            // 下一个组马上到达顶部
            // 获取高度值和当前child的底部到父容器顶部的距离中的较小值，
            // 为了实现当前悬停View被下一个悬停View顶上去消失不见的效果
            bottom = Math.min(child.getBottom(), mHeight);
        } else {
            bottom = mHeight;
        }
        mPaint.setColor(Color.WHITE);
        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(),
                parent.getRight() - parent.getPaddingRight(), bottom, mPaint);
        mPaint.getTextBounds(overText, 0, overText.length(), mTextBoundRect);
        mPaint.setColor(Color.BLACK);
        // 获得画笔的FontMetrics，用来计算baseLine，因为绘制文字时的y坐标需要baseline
        // FontMetrics似乎会受mPaint.setTextSize()的影响
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        // 计算baseLine值,用来确定字母能够绘制在竖直居中的位置
        int baseline = (int) ((mHeight - fontMetrics.bottom - fontMetrics.top) / 2);
        // 居中绘制category
        c.drawText(overText, child.getPaddingLeft(), bottom - (mHeight - baseline), mPaint);
        // 如果当前悬停字母改变了，则通知外部控件CategoryContentView，
        // 让它去通知CategoryAdapter进行更新
        if (!overText.equals(mLastOverCategory.getName())) {
            if (mOnOverTextChangedListener != null){
                mOnOverTextChangedListener.onOverTextChanged(overText);
                mLastOverCategory = new Category(overText);
            }
        }
    }

    /**
     * 判断是否有新的category到达
     * @param pos
     * @return
     */
    private boolean isNewGroupComing(int pos){
        if (pos == 0){
            // 判断是不是组中的第一个位置
            return true;
        } else {
            // 根据前一个组名，判断当前是否为新的组
            String preCategory = mData.get(pos - 1).getCategory().getName();
            String curCategory = mData.get(pos).getCategory().getName();
            return !TextUtils.equals(preCategory, curCategory);
        }
    }

    public interface OnOverTextChangedListener {
        void onOverTextChanged(String overText);
    }

    public void setOnOverTextChangedListener(
            OnOverTextChangedListener onOverTextChangedListener) {
        this.mOnOverTextChangedListener = onOverTextChangedListener;
    }
}
