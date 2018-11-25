package com.shirley.searchfilter.entity;

/**
 * Created by ZLJ on 2018/5/8
 * 需要筛选的数据
 */
public class SearchFilterData<T> {

    // 多选按钮类型
    public static final int MULTI_BUTTON = 0;
    // 时间区间类型
    public static final int TIME_PERIOD = 1;
    // 输入类型
    public static final int INPUT = 2;
    // 单项选择类型
    public static final int SINGLE_CHOICE = 3;

    // 数据类型，从以上四种类型中选择
    private int type;
    // 组名
    private String category;
    // 内容
    private T content;
    // 选择结果
    private T data;
    // 选择结果（用来存放时间区间的结束时间，而开始时间就存放在data里）
    private T data1;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData1() {
        return data1;
    }

    public void setData1(T data1) {
        this.data1 = data1;
    }
}
