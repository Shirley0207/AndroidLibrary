package com.shirley.wordgrouporderview.bean;

public class Word {

    private String word;
    private String group; // 单词的首字母

    public Word(String word, String group) {
        this.word = word;
        this.group = group;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
