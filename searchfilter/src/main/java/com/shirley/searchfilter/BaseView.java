package com.shirley.searchfilter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shirley.searchfilter.entity.SearchFilterData;

/**
 * Created by ZLJ on 2018/5/8
 * 输入框、多选按钮组等View的父类
 */
public abstract class BaseView extends LinearLayout {

    public Context mContext;
    public SearchFilterData result = new SearchFilterData();
    // 类别名称控件，每个子View都有
    public TextView categoryTx;

    public BaseView(Context context) {
        this(context, null);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        LayoutInflater.from(mContext).inflate(getLayout(), this);
        categoryTx = findViewById(R.id.category);
        init();
    }

    protected abstract int getLayout();

    protected abstract void init();

    /**
     * 设置数据
     * @param data
     */
    public void setData(SearchFilterData data){
        categoryTx.setText(data.getCategory());
        result.setType(data.getType());
        result.setCategory(data.getCategory());
    }

    /**
     * 返回筛选结果
     * @return
     */
    public SearchFilterData getResult() {
        return result;
    }

    /**
     * 重置
     */
    public void reset(){
        result.setData(null);
    }
}
