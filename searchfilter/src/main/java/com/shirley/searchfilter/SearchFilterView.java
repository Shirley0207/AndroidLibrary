package com.shirley.searchfilter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shirley.searchfilter.entity.SearchFilterData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZLJ on 2018/5/7
 * 抽屉View（抽屉被拉出来展示的那一个view），需要组合其他子View，如多选按钮组view等
 */
public class SearchFilterView extends LinearLayout implements View.OnClickListener {

    // 外界传入的数据（筛选条件）
    private List<SearchFilterData> mData;
    // 给外界的结果（筛选结果）
    private List<SearchFilterData> mResult = new ArrayList<>();
    private LinearLayout container;
    private TextView okTx;
    private OnCompleteListener mListener;

    public SearchFilterView(Context context) {
        this(context, null);
    }

    public SearchFilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.view_search_filter, this);
        container = view.findViewById(R.id.container);
        okTx = view.findViewById(R.id.ok);
        TextView resetTx = view.findViewById(R.id.reset);
        okTx.setOnClickListener(this);
        resetTx.setOnClickListener(this);
    }

    /**
     * 根据数据类型添加对应的布局
     * @param data
     */
    public void setData(List<SearchFilterData> data){
        mData = data;
        for (int i = 0; i < mData.size(); i++){
            switch (mData.get(i).getType()){
                case SearchFilterData.MULTI_BUTTON:
                    // 添加多选按钮视图
                    MultiButtonView multiButtonView = new MultiButtonView(getContext());
                    multiButtonView.setData(mData.get(i));
                    container.addView(multiButtonView);
                    break;
                case SearchFilterData.TIME_PERIOD:
                    // 添加时间区间选择视图
                    TimePeriodView timePeriodView = new TimePeriodView(getContext());
                    timePeriodView.setData(mData.get(i));
                    container.addView(timePeriodView);
                    break;
                case SearchFilterData.INPUT:
                    // 添加输入框视图
                    InputView inputView = new InputView(getContext());
                    inputView.setData(mData.get(i));
                    container.addView(inputView);
                    break;
                case SearchFilterData.SINGLE_CHOICE:
                    // 添加单项选择视图
                    SingleChoiceView singleChoiceView = new SingleChoiceView(getContext());
                    singleChoiceView.setData(mData.get(i));
                    container.addView(singleChoiceView);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ok){
            mResult = getResult();
            if (mListener != null){
                mListener.onComplete(mResult);
            }
        } else if (v.getId() == R.id.reset){
            // 先把选中数据清空
            if(mResult != null){
                mResult.clear();
            }
            // 循环使各个子控件置空
            for (int i = 0; i < container.getChildCount(); i++){
                ((BaseView) container.getChildAt(i)).reset();
            }
        }
    }

    /**
     * 获取结果，循环添加的view，获得各个view的选中结果
     * @return
     */
    public List<SearchFilterData> getResult(){
        List<SearchFilterData> result = new ArrayList<>();
        for (int i = 0; i < container.getChildCount(); i++){
            if (((BaseView)container.getChildAt(i)).getResult().getData() != null){
                result.add(((BaseView)container.getChildAt(i)).getResult());
            }
        }
        return result.size() != 0 ? result : null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 点击屏幕让container获得焦点，从而可以使子控件InputView中EditText失去焦点，而且并不妨碍点击EditText时它自身可以获得焦点
        container.requestFocus();
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 筛选完成点击确定向外传输数据的接口
     */
    public interface OnCompleteListener{
        /**
         * 筛选完成点击确定向外传输数据的方法
         * @param chosenData
         */
        void onComplete(List<SearchFilterData> chosenData);
    }

    public void setOnCompleteListener(OnCompleteListener mListener) {
        this.mListener = mListener;
    }

}
