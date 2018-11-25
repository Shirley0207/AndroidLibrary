package com.shirley.searchfilter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shirley.searchfilter.adapter.MultiButtonAdapter;
import com.shirley.searchfilter.entity.SearchFilterData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZLJ on 2018/5/7
 * 多选按钮组
 */
public class MultiButtonView extends BaseView implements MultiButtonAdapter.OnItemSelectedListener, View.OnClickListener {

    private GridView gridView;
    private LinearLayout layoutOpen;
    private ImageView ivSwitch;
    private TextView tvChosen;
    private MultiButtonAdapter mAdapter;
    private List<String> values = new ArrayList<>();
    private List<String> allData = new ArrayList<>();
    // 存储前三个，剩下的隐藏
    private List<String> partialData = new ArrayList<>();
    // 标记当前按钮组是展开的还是合上的，默认合上
    private boolean isOpen;

    public MultiButtonView(Context context) {
        this(context, null);
    }

    public MultiButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayout() {
        return R.layout.view_multi_button;
    }

    @Override
    protected void init() {
        gridView = findViewById(R.id.grid_view);
        ivSwitch = findViewById(R.id.iv_switch);
        tvChosen = findViewById(R.id.tv_chosen);
        layoutOpen = findViewById(R.id.layout_open);
        layoutOpen.setOnClickListener(this);
    }

    @Override
    public void setData(SearchFilterData data) {
        super.setData(data);
        allData = (List)data.getContent();
        if (allData.size() > 3){
            for (int i = 0; i < 3; i++){
                partialData.add(allData.get(i));
            }
        } else {
            partialData.addAll(allData);
        }
        mAdapter = new MultiButtonAdapter(mContext, partialData, values);
        gridView.setAdapter(mAdapter);
        mAdapter.setOnItemSelectedListener(this);
        if (values.size() > 0){
            result.setData(values);
        } else {
            result.setData(null);
        }
    }

    @Override
    public void reset() {
        super.reset();
        // 数据清空
        values.clear();
        // 选中数据控件恢复为“全部”
        tvChosen.setText("全部");
        tvChosen.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
        // 通知控件更新（恢复初始状态）
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(String tag) {
        values.add(tag);
        setChosenTextView(values);
        result.setData(values);
    }

    @Override
    public void onItemCanceled(String tag) {
        values.remove(tag);
        setChosenTextView(values);
        if (values.size() > 0){
            result.setData(values);
        } else {
            result.setData(null);
        }
    }

    @Override
    public void onClick(View v) {
        if (isOpen){
            // 当前是展开的，执行合上操作
            ivSwitch.setImageResource(R.drawable.collapse);
            mAdapter.setData(partialData);
            isOpen = false;
        } else {
            // 当前是合上的，执行展开操作
            ivSwitch.setImageResource(R.drawable.expand);
            mAdapter.setData(allData);
            isOpen = true;
        }
    }

    /**
     * 显示选中的值
     * @param chosen
     */
    private void setChosenTextView(List<String> chosen){
        String str = "";
        for (int i = 0; i < chosen.size(); i++){
            str += chosen.get(i);
            if (i != chosen.size() - 1){
                str += ",";
            }
        }
        str = str.equals("") ? "全部" : str;
        tvChosen.setText(str);
        if (str.equals("全部")){
            tvChosen.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
        } else {
            tvChosen.setTextColor(mContext.getResources().getColor(R.color.indian_red));
        }
    }
}
