package com.shirley.wordgrouporderview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.shirley.wordgrouporderview.bean.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义右侧索引导航栏
 * <p>
 * 参考自张旭童
 * 张旭童邮箱：mcxtzhang@163.com
 * 张旭童CSDN：http://blog.csdn.net/zxt0601
 * <p>
 * Created by ZLJ on 2018/7/18
 */
public class IndexBar extends View {

    /**
     * 默认的索引数据源(26个英文字母加#在最后面)
     */
    private static final String[] DEFAULT_INDEX_STRING = {"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y",
            "Z", "#"};

    /**
     * 索引数据源
     */
    private List<String> mIndexData = Arrays.asList(DEFAULT_INDEX_STRING);
    /**
     * RecyclerView数据源
     */
    private List<Word> mSourceData;
    private Paint mPaint = new Paint();
    /**
     * 控件的主要颜色
     */
    private int themeColor = Color.GRAY;
    /**
     * 除去paddingTop和paddingBottom，为每个字母分配的高度
     */
    private int mGapHeight;
    /**
     * 控件的宽度
     */
    private int mWidth;
    /**
     * 控件的高度
     */
    private int mHeight;
    private int paddingTop;
    private int paddingBottom;
    /**
     * 侧边栏的选中字母，默认为"A"
     */
    private String mPressLetter = "A";
    /**
     * 悬停字母，默认为"A"
     */
    private String mDrawOverGroup = "A";
    /**
     * 用于显示被点击的index值的TextView
     */
    private TextView mTvToShowPressedLetter;
    /**
     * 是否需要根据实际的数据来生成索引数据源（例如 只有 A B C 三种tag，那么索引栏就 A B C 三项）
     */
    private boolean mNeedRealIndex;
    private int mPressIndex;
    /**
     * 通知外部RecyclerView进行更新的监听器
     */
    private UpdateRecyclerViewListener mUpdateRecyclerViewListener;
    /**
     * 监听IndexBar点击事件
     */
    private OnIndexPressedListener mOnIndexPressedListener;

    public IndexBar(Context context) {
        this(context, null);
    }

    public IndexBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 获取自定义的属性，字体大小和按下时的背景颜色
        int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10,
                getResources().getDisplayMetrics());
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IndexBar,
                defStyleAttr, 0);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.IndexBar_textSize) {
                textSize = typedArray.getDimensionPixelSize(attr, textSize);
            } else if (attr == R.styleable.IndexBar_themeColor) {
                themeColor = typedArray.getColor(attr, themeColor);
            }
        }
        typedArray.recycle();

        // 画笔的相关设置
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        mPaint.setColor(themeColor);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);

        initIndexBarListener();
    }

    /**
     * 初始化index触摸点击监听器
     */
    private void initIndexBarListener() {
        setOnIndexPressedListener(new OnIndexPressedListener() {
            @Override
            public void onIndexPressed(int index, String text) {
                // 展示提示面板
                if (mTvToShowPressedLetter != null) {
                    mTvToShowPressedLetter.setVisibility(VISIBLE);
                    mTvToShowPressedLetter.setText(text);
                }
                // 记录当前点击的字母的索引值和文字并及时刷新，让被点击的字母立马呈现被选中效果
                mPressIndex = index;
                mPressLetter = text;
                invalidate();
            }

            @Override
            public void onMotionEventEnd() {
                // 隐藏mPressedShowTextView
                if (mTvToShowPressedLetter != null) {
                    mTvToShowPressedLetter.setVisibility(GONE);
                }
                // 判断悬停字母与索引字母是否一致，若不一致，则更新索引字母
                if (!mPressLetter.equals(mDrawOverGroup)) {
                    mPressIndex = getIndexPosByLetter(mDrawOverGroup);
                    mPressLetter = mIndexData.get(mPressIndex);
                    invalidate();
                }
            }
        });
    }

    /**
     * 从外部{@link WordGroupOrderView}传入用于显示点击的字母的TextView
     * @param mTvToShowPressedLetter 用于显示点击的字母的TextView
     * @return 返回自身，是为了可以形成链式调用
     */
    public IndexBar setTvToShowPressedLetter(TextView mTvToShowPressedLetter) {
        this.mTvToShowPressedLetter = mTvToShowPressedLetter;
        return this;
    }

    /**
     * 是否需要使用真实数据源索引
     * 一定要在设置数据源{@link #setSourceData(List)}之前调用
     * @param mNeedRealIndex 是否需要使用真实数据源索引
     * @return 返回自身，是为了可以形成链式调用
     */
    public IndexBar setNeedRealIndex(boolean mNeedRealIndex) {
        this.mNeedRealIndex = mNeedRealIndex;
        if (this.mNeedRealIndex) {
            // 如果需要真实数据源索引，则为mIndexData重新分配全新的对象
            // （因为在初始化时已为mIndexData分配了默认的索引）
            if (mIndexData != null) {
                mIndexData = new ArrayList<>();
            }
        }
        return this;
    }

    /**
     * 从外部{@link WordGroupOrderView}传入数据源
     * @param sourceData 数据源
     * @return 返回自身，是为了可以形成链式调用
     */
    public IndexBar setSourceData(List<Word> sourceData) {
        this.mSourceData = sourceData;
        if (mNeedRealIndex) {
            // 如果需要真实数据索引，则为mIndexData赋值为真实数据索引
            for (Word word : mSourceData) {
                if (!mIndexData.contains(word.getGroup())) {
                    mIndexData.add(word.getGroup());
                }
            }
        }
        // 更新悬停字母为真实数据的第一个group
        mDrawOverGroup = mSourceData.get(0).getGroup();
        // 记得mPressLetter和mPressIndex要同步更新，保持一致
        mPressLetter = mDrawOverGroup;
        mPressIndex = getIndexPosByLetter(mPressLetter);
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 取出宽高的MeasureSpec的Mode和Size
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        // 获得的wSize是该控件在布局文件中指定的layout_width
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        // 获得的hSize是该控件在布局文件中指定的layout_height;
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidth = 0, measureHeight = 0; // 最终测量出来的宽高
        Rect indexBounds = new Rect(); // 存放每个绘制的index的Rect区域
        String index; // 每个要绘制的index的内容
        // 循环结束后可以测量出每一个index的最大的高度和宽度
        for (int i = 0; i < mIndexData.size(); i++) {
            index = mIndexData.get(i);
            // 根据要绘制的文字测量计算文字所在矩形，可以得到宽高
            mPaint.getTextBounds(index, 0, index.length(), indexBounds);
            // 循环结束后，得到index的最大宽度
            measureWidth = Math.max(indexBounds.width(), measureWidth);
            // 循环结束后，得到index的最大高度
            measureHeight = Math.max(indexBounds.height(), measureHeight);
        }
        // 为了增加每个字母之间的间隔，给measureHeight增加一定的高度
        measureHeight += 10;
        // 高度 * index的个数 = 总高度
        measureHeight *= mIndexData.size();
        // 得到合适的宽度
        switch (wMode) { // 宽度模式，根据布局文件定义的layout_width
            case MeasureSpec.EXACTLY:
                // 布局文件中，layout_width指定了具体的值，因此模式是EXACTLY
                measureWidth = wSize;
                break;
            case MeasureSpec.AT_MOST:
                // wSize此时是父控件能给子View分配的最大空间
                measureWidth = Math.min(measureWidth, wSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        // 得到合适的高度
        switch (hMode) { // 高度模式，根据布局文件定义的layout_height
            case MeasureSpec.EXACTLY:
                measureHeight = hSize;
                break;
            case MeasureSpec.AT_MOST:
                // 布局文件中，layout_height指定了wrap_content,因此模式是AT_MOST
                // hSize此时是父控件能给子View分配的最大空间
                measureHeight = Math.min(measureHeight, hSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // w和h就是经过onMeasure()之后测量出来的宽度和高度
        mWidth = w;
        mHeight = h;
        paddingTop = getPaddingTop(); // 获取的是设置的paddingTop
        paddingBottom = getPaddingBottom(); // 获取的是设置的paddingBottom
        // 如果外部调用setNeedRealIndex()在调用setSourceData()之后，则会出现mIndexData.size为0
        // 所以进行捕捉并抛出提示
        try {
            // 计算每一个Index所需的高度
            mGapHeight = (mHeight - paddingTop - paddingBottom) / mIndexData.size();
        } catch (Exception e){
            e.printStackTrace();
            throw new ArithmeticException("setNeedRealIndex() should be called before setSourceData()");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect indexBounds = new Rect(); // 绘制字母的区域
        String index; // 要绘制的字母
        // 获得画笔的FontMetrics，用来计算baseLine，因为绘制文字时的y坐标需要baseline
        // FontMetrics似乎会受mPaint.setTextSize()的影响
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        // 计算baseLine值,用来确定字母能够绘制在竖直居中的位置
        int baseline = (int) ((mGapHeight - fontMetrics.bottom - fontMetrics.top) / 2);
        // 进行循环绘制字母
        for (int i = 0; i < mIndexData.size(); i++) {
            index = mIndexData.get(i);
            // 根据要绘制的文字测量计算文字所在矩形，可以得到文字的宽高
            mPaint.getTextBounds(index, 0, index.length(), indexBounds);
            mPaint.setColor(themeColor);
            // 如果当前是选中状态，则需要绘制选中背景
            if (i == mPressIndex) {
                // 计算左右margin，虽然用的是mGapHeight，但绘制字母时应当是一个正方形
                int marginLeftOrRight = (mWidth - mGapHeight) / 2;
                // 绘制包裹字母的RectF
                RectF rectF = new RectF(marginLeftOrRight, mGapHeight * i,
                        mWidth - marginLeftOrRight, mGapHeight * i + mGapHeight);
                // 先绘制一个背景圆，作为选中状态
                canvas.drawArc(rectF, 0, 360, true, mPaint);
                // 选中状态下，背景圆是主题色，字体应当是白色
                mPaint.setColor(Color.WHITE);
            }
            // 调用drawText，居中显示绘制字母
            // 之前调用过mPaint.getTextBounds,该方法会根据要绘制的文字而确定indexBounds的大小
            // 所以此处 mWidth / 2 - indexBounds.width() / 2 刚好确定了居中绘制字母的起始x坐标
            canvas.drawText(index, mWidth / 2 - indexBounds.width() / 2,
                    paddingTop + mGapHeight * i + baseline, mPaint);
        }
        // 如果当前索引字母与悬停字母不一致，则说明改变了索引字母，因此需要通知RecyclerView刷新
        if (!mPressLetter.equals(mDrawOverGroup)) {
            // 先根据索引字母获得RecyclerView数据源中对应的位置
            int position = getSourcePosByLetter(mPressLetter);
            // 先保存下最初的位置，以防止向后找完了之后向前寻找能直接从当前位置开始
            int originalIndex = mPressIndex;
            // true代表若在RecyclerView列表中不存在当前点击的group，则先向后寻找
            boolean backDirection = true;
            String nextLetter;
            while (position == -1) {
                // 如果在RecyclerView列表中不存在当前点击的group， 则开始循环查找周边存在的group
                if (backDirection) {
                    // 向后寻找
                    mPressIndex--;
                } else {
                    // 向前寻找
                    mPressIndex++;
                }
                if (mPressIndex < 0) {
                    // 如果向后到头了，则转换方向向前寻找
                    backDirection = false;
                    mPressIndex = originalIndex;
                } else {
                    // 根据索引获得字母
                    nextLetter = mIndexData.get(mPressIndex);
                    // 根据索引字母获得数据源内的第一个以索引字母开头的单词的位置
                    position = getSourcePosByLetter(nextLetter);
                }
            }
            if (mUpdateRecyclerViewListener != null) {
                // 通知RecyclerView更新位置
                mUpdateRecyclerViewListener.updateRecyclerView(position);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 注意这里没有break，因为down时，也要计算落点 回调监听器
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                // 通过计算判断落点在哪个区域
                mPressIndex = (int) ((y - paddingTop) / mGapHeight);
                // 边界处理（在手指move时，有可能已经移出边界，防止越界）
                if (mPressIndex < 0) {
                    mPressIndex = 0;
                } else if (mPressIndex > mIndexData.size() - 1) {
                    mPressIndex = mIndexData.size() - 1;
                }
                // 回调监听器
                if (mOnIndexPressedListener != null) {
                    mOnIndexPressedListener.onIndexPressed(mPressIndex, mIndexData.get(mPressIndex));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                // 回调监听器
                if (mOnIndexPressedListener != null) {
                    mOnIndexPressedListener.onMotionEventEnd();
                }
                break;
        }
        return true;
    }

    /**
     * 更新索引字母状态，供外部调用，当外部RecyclerView的悬停字母改变时则通过此方法通知IndexBar更新
     * @param drawOverGroup RecyclerView的悬停字母
     */
    public void updateIndexBar(String drawOverGroup) {
        // 更新相关属性
        mDrawOverGroup = drawOverGroup;
        mPressLetter = mDrawOverGroup;
        mPressIndex = getIndexPosByLetter(mDrawOverGroup);
        // 强制重绘
        invalidate();
    }

    /**
     * 当前被按下的index的监听器，联动IndexBar和RecyclerView
     */
    private interface OnIndexPressedListener {
        /**
         * 当某个字母被按下
         * @param index 字母索引值
         * @param text 字母文本
         */
        void onIndexPressed(int index, String text);
        /**
         * 当触摸事件结束（UP CANCEL）
         */
        void onMotionEventEnd();
    }

    private void setOnIndexPressedListener(OnIndexPressedListener mOnIndexPressedListener) {
        this.mOnIndexPressedListener = mOnIndexPressedListener;
    }

    /**
     * 根据字母查询RecyclerView数据源中对应的索引值
     * 因为mSourceData是已经排好序的数据，因此通过循环可以找到当前字母在数据源中对应的第一个单词
     * 的索引，这样就可以控制RecyclerView滚动到字母对应的单词
     *
     * @param letter 字母
     * @return 字母在数据源中对应的第一个单词的索引值
     */
    private int getSourcePosByLetter(String letter) {
        if (TextUtils.isEmpty(letter)) {
            return -1;
        }
        for (int i = 0; i < mSourceData.size(); i++) {
            if (letter.equalsIgnoreCase(mSourceData.get(i).getGroup())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据字母返回索引栏中对应的索引值
     *
     * @param letter 字母
     * @return 字母在索引栏中对应的索引值
     */
    private int getIndexPosByLetter(String letter) {
        if (TextUtils.isEmpty(letter)) {
            return -1;
        }
        for (int i = 0; i < mIndexData.size(); i++) {
            if (letter.equalsIgnoreCase(mIndexData.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 通知外部RecyclerView更新的监听器
     */
    public interface UpdateRecyclerViewListener {
        /**
         * 更新外部RecyclerView
         * @param position RecyclerView需要滑动到的位置
         */
        void updateRecyclerView(int position);
    }

    public IndexBar setUpdateRecyclerViewListener(
            UpdateRecyclerViewListener mUpdateRecyclerViewListener) {
        this.mUpdateRecyclerViewListener = mUpdateRecyclerViewListener;
        return this;
    }
}
