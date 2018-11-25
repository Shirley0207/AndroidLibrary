package com.shirley.searchfilter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.shirley.searchfilter.entity.SearchFilterData;

/**
 * Created by ZLJ on 2018/5/8
 * 输入框筛选条件
 */
public class InputView extends BaseView implements View.OnFocusChangeListener {

    private EditText input;

    public InputView(Context context) {
        this(context, null);
    }

    public InputView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayout() {
        return R.layout.view_input;
    }

    @Override
    protected void init() {
        input = findViewById(R.id.input);
        input.setOnFocusChangeListener(this);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    result.setData(null);
                } else {
                    result.setData(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void setData(SearchFilterData data) {
        super.setData(data);
        input.setHint("请输入" + data.getCategory() + "进行筛选");
    }

    @Override
    public void reset() {
        super.reset();
        // 控件设置成默认值，数据也在父类里设置成默认值
        input.setText("");
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            ((Activity)mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        } else {
            // 失去焦点时强制隐藏软键盘
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }

}
