package com.shirley.searchfilter.entity;

import java.util.List;

/**
 * Created by ZLJ on 2018/5/21
 * 单选数据结构（两层）
 */
public class SingleChoiceData {
    private String group;
    private List<String> children;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<String> getChildren() {
        return children;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }
}
