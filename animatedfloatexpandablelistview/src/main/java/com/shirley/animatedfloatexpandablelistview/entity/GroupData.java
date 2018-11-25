package com.shirley.animatedfloatexpandablelistview.entity;

import java.util.List;

/**
 * Created by ZLJ on 2018/5/31.
 */
public class GroupData {

    private String name;
    private List<GroupData> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GroupData> getChildren() {
        return children;
    }

    public void setChildren(List<GroupData> children) {
        this.children = children;
    }
}
