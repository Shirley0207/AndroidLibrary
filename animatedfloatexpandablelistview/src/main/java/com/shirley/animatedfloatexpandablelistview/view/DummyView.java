package com.shirley.animatedfloatexpandablelistview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于扩展折叠动画的虚拟视图<br>
 * Created by ZLJ on 2018/5/31
 */
public class DummyView extends View {

    private List<View> views = new ArrayList<>();

    public DummyView(Context context) {
        super(context);
    }

    public void clearViews(){
        views.clear();
    }

    /**
     * 确定子view的位置，并添加到views里
     * @param childView
     */
    public void addFakeView(View childView){
        childView.layout(0, 0, getWidth(), childView.getMeasuredHeight());
        views.add(childView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        for (int i = 0; i < views.size(); i++){
            View view = views.get(i);
            view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
        }
    }

    // 绘制动画时循环调用onLayout()、ExpandAnimation的applyTransformation()、dispatchDraw()
    // 绘制View本身的内容，通过调用View.onDraw(canvas)函数实现,绘制自己的孩子通过dispatchDraw（canvas）实现

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // save()用来保存Canvas的状态。save之后，可以调用Canvas的平移、放缩、旋转、错切、裁剪等操作
        canvas.save();
        int len = views.size();
        for (int i = 0; i < len; i++){
            View view = views.get(i);
            // 绘制child
            canvas.save();
            // clipRect用于裁剪画布，也就是设置画布的显示区域
            canvas.clipRect(0, 0, getWidth(), view.getMeasuredHeight());
            view.draw(canvas);
            // 用来恢复Canvas之前保存的状态。防止save后对Canvas执行的操作对后续的绘制有影响。
            canvas.restore();
            // 当画布的原点移到指定位置
            canvas.translate(0, view.getMeasuredHeight());
        }
        canvas.restore();
    }
}
