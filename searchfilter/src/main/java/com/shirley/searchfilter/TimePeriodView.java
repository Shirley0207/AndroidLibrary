package com.shirley.searchfilter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ZLJ on 2018/5/8
 * 时间区间选择
 */
public class TimePeriodView extends BaseView implements View.OnClickListener {

    private TextView startTimeTx;
    private TextView endTimeTx;
    private Date startTime;
    private Date endTime;
    // 标识当前点击的是开始时间还是结束时间，默认开始时间
    private boolean isEnd;

    public TimePeriodView(Context context) {
        this(context, null);
    }

    public TimePeriodView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayout() {
        return R.layout.view_time_period;
    }

    @Override
    protected void init() {
        startTimeTx = findViewById(R.id.start_time);
        endTimeTx = findViewById(R.id.end_time);
        startTimeTx.setOnClickListener(this);
        endTimeTx.setOnClickListener(this);
    }

    @Override
    public void reset() {
        super.reset();
        // 数据清空
        startTime = null;
        endTime = null;
        // 因为时间区间选择有些特殊，有两个值，data1已在父类里设置为null，因此此处还需设置data1为null
        result.setData1(null);
        // 控件清空
        startTimeTx.setText("开始时间");
        startTimeTx.setTextColor(mContext.getResources().getColor(R.color.light_grey));
        endTimeTx.setText("结束时间");
        endTimeTx.setTextColor(mContext.getResources().getColor(R.color.light_grey));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_time){
            isEnd = false;
        } else if (v.getId() == R.id.end_time){
            isEnd = true;
        }
        selectDate(isEnd);
    }

    /**
     * 选择日期
     * @param isEnd 标识当前点击的是开始时间还是结束时间
     */
    private void selectDate(final boolean isEnd){
        // 构建选择日期的Dialog
        DatePickerDialog.Builder builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker_Light){
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                DatePickerDialog dialog = (DatePickerDialog)fragment.getDialog();
                final int year = dialog.getYear();
                final int month = dialog.getMonth();
                final int day = dialog.getDay();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                if (isEnd){
                    // 当前点击的是结束时间
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    endTime = calendar.getTime();
                    String timeStr;
                    if (startTime != null && endTime.before(startTime)){
                        Toast.makeText(mContext, "结束时间不能早于开始时间", Toast.LENGTH_SHORT).show();
                        timeStr = "结束时间";
                        endTimeTx.setTextColor(mContext.getResources().getColor(R.color.light_grey));
                        // 设置结束时间为null
                        result.setData1(null);
                    } else {
                        timeStr = gainedDesignatedDateStr(endTime,"yyyy-MM-dd");
                        endTimeTx.setText(timeStr);
                        endTimeTx.setTextColor(mContext.getResources().getColor(R.color.black));
                        // 设置结束时间
                        if (result.getData() == null){
                            result.setData("");
                        }
                        result.setData1(endTime);
                    }
                    endTimeTx.setText(timeStr);
                } else {
                    // 当前点击的是开始时间
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    startTime = calendar.getTime();
                    String timeStr;
                    if (endTime != null && startTime.after(endTime)){
                        Toast.makeText(mContext, "开始时间不能晚于结束时间", Toast.LENGTH_SHORT).show();
                        timeStr = "开始时间";
                        startTimeTx.setTextColor(mContext.getResources().getColor(R.color.light_grey));
                        // 设置开始时间为null
                        result.setData(null);
                    } else {
                        timeStr = gainedDesignatedDateStr(startTime,"yyyy-MM-dd");
                        startTimeTx.setTextColor(mContext.getResources().getColor(R.color.black));
                        // 设置开始时间
                        result.setData(startTime);
                    }
                    startTimeTx.setText(timeStr);
                }
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        if (isEnd){
            // 当前点击的是结束时间
            builder.date(endTime == null ? new Date().getTime() : endTime.getTime())
                    .positiveAction("确定")
                    .negativeAction("取消");
        } else {
            // 当前点击的是开始时间
            builder.date(startTime == null ? new Date().getTime() : startTime.getTime())
                    .positiveAction("确定")
                    .negativeAction("取消");
        }

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((FragmentActivity)mContext).getSupportFragmentManager(),null);
    }

    /**
     * 获取指定日期的指定格式字符串
     * @param date
     * @param dateFormatStr
     * @return
     */
    public String gainedDesignatedDateStr(Date date, String dateFormatStr) {
        if(date == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
        String str = sdf.format(date);
        return str;
    }
}
