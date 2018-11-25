package com.shirley.slidemenu.entity;

import java.util.List;

/**
 * 主体是目录，一个目录下有若干项内容<br>
 * Created by ZLJ on 2018/7/30
 */
public class CategoryContent {

    private Category category;
    private List<Content> contents;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

}
