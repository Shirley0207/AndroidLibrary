package com.shirley.android.roundimage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Created by ZLJ on 2017/11/24.
 * Other's code CircleImageView https://github.com/hdodenhof/CircleImageView
 * 流程控制得比较严谨，比如setup方法的使用
 * updateShaderMatrix保证图片损失度最小和始终绘制图片正中央的那部分
 * 作者思路是画圆用渲染器位图填充，而不是把Bitmap重绘切割成一个圆形图片
 */

public class RoundImageView extends AppCompatImageView {

    // 默认边框宽度
    private static final int DEFAULT_BORDER_WIDTH = 0;
    // 默认边框颜色
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final int DEFAULT_CIRCLE_BACKGROUND_COLOR = Color.TRANSPARENT;
    // 默认不覆盖图片边界
    private static final boolean DEFAULT_BORDER_OVERLAY = false;

    // 缩放类型
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLOR_DRAWABLE_DIMENSION = 2;

    // 边界矩形
    private final RectF mBorderRect = new RectF();
    // 图片矩形
    private final RectF mDrawableRect = new RectF();

    // 初始false
    // 用来标识构造方法是否执行完毕，即是否读取好自定义参数，准备好去绘制了
    private boolean mReady;
    private boolean mSetupPending;
    // 边框是否要覆盖住图片
    private boolean mBorderOverlay;
    private boolean mDisableCircularTransformation;

    // 这里定义了圆形边缘的默认宽度和颜色
    // 边界宽度
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    // 边界颜色
    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mCircleBackgroundColor = DEFAULT_CIRCLE_BACKGROUND_COLOR;

    private Bitmap mBitmap;
    // 图片宽度（位图）
    private int mBitmapWidth;
    // 图片高度（位图）
    private int mBitmapHeight;
    // 渲染器（位图渲染）
    private BitmapShader mBitmapShader;

    // 图片画笔,这个画笔最重要的是关联了mBitmapShader，使canvas在执行的时候可以切割原图片(mBitmapShader是关联了原图的bitmap的)
    private final Paint mBitmapPaint = new Paint();
    // 边界画笔,这个描边，则与本身的原图bitmap没有任何关联
    private final Paint mBorderPaint = new Paint();
    private final Paint mCircleBackgroundPaint = new Paint();
    // 变换矩阵，用来设置图片的缩放以及平移
    private final Matrix mShaderMatrix = new Matrix();

    // 带边框的图片半径
    private float mBorderRadius;
    // 图片半径
    private float mDrawableRadius;

    // 颜色过滤器
    private ColorFilter mColorFilter;

    /**
     * 构造方法
     * @param context
     */
    public RoundImageView(Context context) {
        super(context);
        init();
    }

    /**
     * 构造方法
     * @param context
     * @param attrs
     */
    public RoundImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // TypedArray是存储资源数组的容器，它可以通过obtainStyledAttributes()创建出来
        // 不过创建完了，如果不再使用了，请注意调用recycle()把它释放掉
        // 通过obtainStyledAttributes获得一组值赋给TypedArray(数组)，这一组值来自于res/values/attrs.xml
        // 中的name="RoundImageView"的declare-styleable中
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleAttr, 0);

        // 通过TypedArray提供的一系列方法getXxx取得我们在xml里定义的参数值
        // 获取边界的宽度
        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_border_width, DEFAULT_BORDER_WIDTH);
        // 获取边界的颜色
        mBorderColor = typedArray.getColor(R.styleable.RoundImageView_border_color, DEFAULT_BORDER_COLOR);
        // 获取边界是否要压住图片
        mBorderOverlay = typedArray.getBoolean(R.styleable.RoundImageView_border_overlay, DEFAULT_BORDER_OVERLAY);

        if (typedArray.hasValue(R.styleable.RoundImageView_circle_background_color)){
            mCircleBackgroundColor = typedArray.getColor(R.styleable.RoundImageView_circle_background_color,
                    DEFAULT_CIRCLE_BACKGROUND_COLOR);
        } else if (typedArray.hasValue(R.styleable.RoundImageView_fill_color)){
            mCircleBackgroundColor = typedArray.getColor(R.styleable.RoundImageView_fill_color,
                    DEFAULT_CIRCLE_BACKGROUND_COLOR);
        }

        // 调用recycle()回收TypedArray，以便后面重用
        typedArray.recycle();

        init();
    }

    /**
     * 作用就是保证第一次执行setup方法里下面代码要在构造方法执行完毕时执行
     */
    private void init() {
        // 在这里ScaleType被强制设定为CENTER_CROP，就是将图片水平垂直居中，进行缩放
        super.setScaleType(SCALE_TYPE);
        mReady = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            // Android 5.0及以上版本中设置阴影的方法，5.0以下的版本是使用setOutline()
            setOutlineProvider(new OutlineProvider());
        }

        if (mSetupPending){
            // 此时mReady为true，setup()会执行后续代码
            setup();
            mSetupPending = false;
        }
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    /**
     * 这里明确指定，只支持CENTER_CROP
     * @param scaleType
     */
    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE){
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    /**
     * 是否保持可绘制对象的比例
     * @param adjustViewBounds
     */
    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds){
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        setup();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        setup();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(@ColorInt int borderColor) {
        if (borderColor == mBorderColor){
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public int getCircleBackgroundColor() {
        return mCircleBackgroundColor;
    }

    public void setCircleBackgroundColor(@ColorInt int circleBackgroundColor) {
        if (circleBackgroundColor == mCircleBackgroundColor){
            return;
        }

        mCircleBackgroundColor = circleBackgroundColor;
        mCircleBackgroundPaint.setColor(mCircleBackgroundColor);
        invalidate();
    }

    public void setCircleBackgroundColorResource(@ColorRes int circleBackgroundRes){
        setCircleBackgroundColor(getContext().getResources().getColor(circleBackgroundRes));
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth){
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }

    public boolean isBorderOverlay() {
        return mBorderOverlay;
    }

    public void setBorderOverlay(boolean borderOverlay) {
        if (borderOverlay == mBorderOverlay){
            return;
        }

        mBorderOverlay = borderOverlay;
        setup();
    }

    public boolean isDisableCircularTransformation() {
        return mDisableCircularTransformation;
    }

    public void setDisableCircularTransformation(boolean disableCircularTransformation) {
        if (disableCircularTransformation == mDisableCircularTransformation){
            return;
        }

        mDisableCircularTransformation = disableCircularTransformation;
        initializeBitmap();
    }

    // 以下四个方法都是重写AppCompatImageView的setImageXxx()方法
    // 注意这四个方法是先于构造函数执行完成

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initializeBitmap();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initializeBitmap();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        initializeBitmap();
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
        initializeBitmap();
    }

    /**
     * Drawable转Bitmap
     * @param drawable
     * @return
     */
    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null){
            return null;
        }

        if (drawable instanceof BitmapDrawable){
            // 通常来说，我们的代码就是执行到这里就返回了，返回的就是我们最原始的bitmap
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable){
                bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                // getIntrinsicWidth()和getIntrinsicHeight()，顾名思义他们是用来取得Drawable的固有的宽度和高度,是以dp为单位。
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError error){
            error.printStackTrace();
            return null;
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (colorFilter == mColorFilter){
            return;
        }

        mColorFilter = colorFilter;
        applyColorFilter();
        invalidate();
    }

    @Override
    public ColorFilter getColorFilter() {
        return mColorFilter;
    }

    private void initializeBitmap(){
        if (mDisableCircularTransformation){
            mBitmap = null;
        } else {
            mBitmap = getBitmapFromDrawable(getDrawable());
            setup();
        }
    }

    // 第一次被调用时mReady为false，设置mSetupPending为true就返回，
    // 第二次被调用时mReady为true，mSetupPending也为true，就会执行setup()的后续代码
    // 如此设计的原因是，第一次进入setImageXxx()时，我们无法获取自定义参数值，所以setup()下面的代码
    // 无法绘制我们想要的样式。而获取自定义参数只能在构造方法里，
    // 这样，通过设置mSetupPending和mReady控制setup方法里后面的代码要在构造函数执行完毕后执行
    // 再者，如果用户再进行setImageXxx()设置图片的话，就直接会执行setup()后面的代码，因为这之后mReady一直为true
    /**
     * 这个方法很关键，进行图片画笔边界画笔(Paint)一些重绘参数初始化
     * 构建渲染器BitmapShader用Bitmap来填充绘制区域，设置样式以及内外圆半径计算等
     * 以及调用updateShaderMatrix()和invalidate()
     */
    private void setup(){
        // mReady默认值为false，首次进入if语句为真，设置mSetupPending为true就返回，没有执行后续代码
        if (!mReady){
            mSetupPending = true;
            return;
        }

        if (getWidth() == 0 && getHeight() == 0){
            return;
        }

        // 防止空指针异常
        if (mBitmap == null){
            // 请求重新draw()，但只会绘制调用者本身
            invalidate();
            return;
        }

        // 构建渲染器，用mBitmap来填充绘制区域，参数值代表如果图片太小的话就直接拉伸
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        // 设置图片画笔反锯齿
        mBitmapPaint.setAntiAlias(true);
        // 设置图片画笔渲染器
        mBitmapPaint.setShader(mBitmapShader);

        // 设置边界画笔样式，设置画笔为空心,只描边不填充
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        // 设置边界画笔颜色
        mBorderPaint.setColor(mBorderColor);
        // 设置边界画笔宽度
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mCircleBackgroundPaint.setStyle(Paint.Style.FILL);
        mCircleBackgroundPaint.setAntiAlias(true);
        mCircleBackgroundPaint.setColor(mCircleBackgroundColor);

        // 取原图片的宽高
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        // 设置含边界显示区域，取的是CircleImageView的布局实际大小，为方形，查看xml也就是160dp(240px),getWidth得到的是某个view的实际尺寸
        mBorderRect.set(calculateBounds());
        // 就边框来看，有两个圆，内圆和外圆，获取内圆的半径才能从内圆画到外圆
        // 计算圆形带边界部分（外圆）的最小半径，取mBorderRect的宽高减去一个边缘大小的一半的较小值
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2.0f, (mBorderRect.width() - mBorderWidth) / 2.0f);

        // 初始图片显示区域为mBorderRect(CircleImageView的布局实际大小)
        mDrawableRect.set(mBorderRect);
        if (!mBorderOverlay && mBorderWidth > 0){
            // 不覆盖图片，则将图片显示的区域向内缩小，-1.0f的目的大概是想要留1px图片的边界在边框下，以致边框和图片之间不出现缝隙
            // 通过inset方法，使得图片显示的区域从mBorderRect大小上下左右内移边界的宽度形成区域
            mDrawableRect.inset(mBorderWidth - 1.0f, mBorderWidth - 1.0f);
        }
        // 这里计算的是内圆的最小半径，也即去除边界宽度的半径
        mDrawableRadius = Math.min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f);

        applyColorFilter();
        //设置渲染器的变换矩阵，也即是mBitmap用何种缩放形式填充
        updateShaderMatrix();
        // 手动触发ondraw()函数，完成最终的绘制
        invalidate();
    }

    private RectF calculateBounds(){
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        // 获取可用宽度和可用高度中较小的值
        int sideLength = Math.min(availableWidth, availableHeight);

        // 保证绘制的图片居中
        float left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLength) / 2f;

        return new RectF(left, top, left + sideLength, top + sideLength);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDisableCircularTransformation){
            super.onDraw(canvas);
            return;
        }

        // 如果图片不存在就不画
        if (mBitmap == null){
            return;
        }

        if (mCircleBackgroundColor != Color.TRANSPARENT){
            // 绘制一层背景色，会被图片覆盖
            canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mCircleBackgroundPaint);
        }
        // 绘制内圆形，参数内圆半径，图片画笔为mBitmapPaint
        canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mBitmapPaint);
        // 如果圆形边缘的宽度不为0，我们还要绘制带边界的外圆形，参数外圆半径，边界画笔为mBorderPaint
        if (mBorderWidth > 0){
            canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mBorderRadius, mBorderPaint);
        }
    }

    /**
     * 设置BitmapShader的Matrix参数，设置最小缩放比例，平移参数
     * 作用:保证图片损失度最小和始终绘制图片正中央的那部分
     */
    private void updateShaderMatrix() {
        float scale = 1;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        // 这个不等式也就是(mBitmapWidth / mDrawableRect.width()) > (mBitmapHeight / mDrawableRect.height())
        // 取最小的缩放比例
        // 取图片宽度与图片显示区域的宽度之比和图片高度与图片显示区域的高度之比中较小的一方，按较小的来缩放，保证图片损失度最小
        // 同时scale保证Bitmap的宽或高和目标区域一致，那么高或宽就需要进行位移，使得Bitmap居中
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight){
            // 图片高度与显示区域的高度之比较小，因此将显示区域的高度与图片高度之比作为缩放系数
            // y轴缩放，x轴平移，使得图片的y轴方向的边的尺寸缩放到图片显示区域(mDrawableRect)一样
            scale = mDrawableRect.height() / (float) mBitmapHeight;
//            // 为了使图片在x轴方向上是居中显示的
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            // 图片宽度与显示区域的宽度之比较小，因此将显示区域的宽度与图片宽度之比作为缩放系数
            // x轴缩放，y轴平移，使得图片的x轴方向的边的尺寸缩放到图片显示区域(mDrawableRect)一样
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            // 为了使图片在y轴方向上是居中显示的
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        // shader的变换矩阵，这里主要用于放大或者缩小
        mShaderMatrix.setScale(scale, scale);
        // 平移  postTranslate()方法内的参数是平移的距离，而不是平移目的地的坐标
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);

        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    /**
     * 设置颜色过滤
     */
    private void applyColorFilter(){
        if (mBitmapPaint != null){
            mBitmapPaint.setColorFilter(mColorFilter);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class OutlineProvider extends ViewOutlineProvider {
        @Override
        public void getOutline(View view, Outline outline) {
            Rect bounds = new Rect();
            // RectF转为Rect
            mBorderRect.roundOut(bounds);
            outline.setRoundRect(bounds, bounds.width() / 2.0f);
        }
    }
}
