package com.example.activitytest.Util;

public class Memo {
    private String Id; // 数据库中Id
    private String title; // 标题栏
    private String content; // 内容
    private int ind; // MemoList中对应的下标

    public Memo(String title, String content){
        this.title = title;
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setInd(int ind) {
        this.ind = ind;
    }

    public int getInd() {
        return ind;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getId() {
        return Id;
    }
}
