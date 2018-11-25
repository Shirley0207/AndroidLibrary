package com.shirley.tabpage;

import android.view.View;

/**
 * Created by ZLJ on 2018/8/10
 */
public class TabBean {

    /**
     * Tab名称
     */
    private String tabName;
    /**
     * Tab颜色
     */
    private int tabColor;
    /**
     * Tab对应的View
     */
    private View tabPage;

    /**
     * 构造方法
     * @param tabName tab名称
     */
    public TabBean(String tabName) {
        this.tabName = tabName;
    }

    /**
     * 构造方法
     * @param tabName tab名称
     * @param tabColor tab颜色
     */
    public TabBean(String tabName, int tabColor) {
        this.tabName = tabName;
        this.tabColor = tabColor;
    }

    /**
     * 构造方法
     * @param tabName tab名称
     * @param tabPage tab对应的View
     */
    public TabBean(String tabName, View tabPage) {
        this.tabName = tabName;
        this.tabPage = tabPage;
    }

    /**
     * 构造方法
     * @param tabName tab名称
     * @param tabColor tab颜色
     * @param tabPage tab对应的View
     */
    public TabBean(String tabName, int tabColor, View tabPage) {
        this.tabName = tabName;
        this.tabColor = tabColor;
        this.tabPage = tabPage;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public int getTabColor() {
        return tabColor;
    }

    public void setTabColor(int tabColor) {
        this.tabColor = tabColor;
    }

    public View getTabPage() {
        return tabPage;
    }

    public void setTabPage(View tabPage) {
        this.tabPage = tabPage;
    }
}
