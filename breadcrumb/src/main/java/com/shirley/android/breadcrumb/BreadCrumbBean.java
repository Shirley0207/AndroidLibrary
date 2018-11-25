package com.shirley.android.breadcrumb;

import java.util.List;

/**
 * Created by ZLJ on 2017/12/1.
 * 面包屑Bean
 */

public class BreadCrumbBean {

    private String text;
    private List<BreadCrumbBean> children;
    private int layer;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<BreadCrumbBean> getChildren() {
        return children;
    }

    public void setChildren(List<BreadCrumbBean> children) {
        this.children = children;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}