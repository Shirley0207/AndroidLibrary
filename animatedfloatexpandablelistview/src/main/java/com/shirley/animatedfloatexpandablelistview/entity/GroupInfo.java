package com.shirley.animatedfloatexpandablelistview.entity;

/**
 * 用于保存关于group的信息
 * Created by ZLJ on 2018/5/31
 */
public class GroupInfo {
    public boolean animating = false;
    public boolean expanding = false;
    public int firstChildPosition;
    /**
     * 该变量包含虚拟视图的最后已知高度值。我们保存这些信息，以便如果用户在完全展开之前折叠一个组，
     * 折叠动画将从虚拟视图的当前高度开始，而不是从完全展开的高度开始。
     */
    public int dummyHeight = -1;
}
