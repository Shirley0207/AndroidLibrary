package com.shirley.slidemenu.entity;

/**
 * 主体是内容，每个内容都对应着一个目录<br>
 * Created by ZLJ on 2018/8/21
 */
public class ContentCategory {

    private Content content;
    private Category category;

    public ContentCategory(Content content, Category category) {
        this.content = content;
        this.category = category;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
