package com.shirley.wordgrouporderview.helper;

import com.shirley.wordgrouporderview.bean.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZLJ on 2018/7/16
 * 先进行单词排序，在进行单词转换
 */
public class OrderWordHelper {

    /**
     * 单词转换，将String形式的单词列表转换成特定的单词对象列表
     * @param words 待转换的单词列表
     * @return 转换完的单词列表
     */
    private static List<Word> convertWord(List<String> words){
        List<Word> data = new ArrayList<>();
        for (int i = 0; i < words.size(); i++){
            Word word = new Word(words.get(i),
                    String.valueOf(words.get(i).charAt(0)).toUpperCase());
            data.add(word);
        }
        return data;
    }

    /**
     * 使用冒泡排序法为单词排序，供外部调用
     * @param sourceWords 待排序的单词列表
     * @return 排好序的单词列表
     */
    public static List<Word> orderWord(List<String> sourceWords) {
        String jStr, j1Str;
        for (int i = 0; i < sourceWords.size(); i++){
            for (int j = 0; j < sourceWords.size() - 1; j++){
                // 如果当前这个单词首字母是大写
                if (Character.isUpperCase(sourceWords.get(j).charAt(0))){
                    jStr = sourceWords.get(j).replace(sourceWords.get(j).charAt(0),
                            Character.toLowerCase(sourceWords.get(j).charAt(0)));
                } else {
                    jStr = sourceWords.get(j);
                }
                // 如果下一个单词首字母是大写
                if (Character.isUpperCase(sourceWords.get(j + 1).charAt(0))){
                    j1Str = sourceWords.get(j + 1).replace(sourceWords.get(j + 1).charAt(0),
                            Character.toLowerCase(sourceWords.get(j + 1).charAt(0)));
                } else {
                    j1Str = sourceWords.get(j + 1);
                }
                if (jStr.compareTo(j1Str) > 0){
                    String jWord = sourceWords.get(j);
                    String j1Word = sourceWords.get(j + 1);
                    sourceWords.set(j, j1Word);
                    sourceWords.set(j + 1, jWord);
                }
            }
        }
        return convertWord(sourceWords);
    }
}
