package com.example.activitytest.Util;

public class InOutcome {
    private String Id; // 数据库中Id
    private String kind; // 收入/支出来源
    private String time; // 获得收入/支出的时间
    private double value; // 收入/支出数值
    private int ind; // List数组 中对应的下标

    public InOutcome(String kind, String time, double value){
        this.kind = kind;
        this.time = time;
        this.value = value;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getId() {
        return Id;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getKind() {
        return kind;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setInd(int ind) {
        this.ind = ind;
    }

    public int getInd() {
        return ind;
    }
}
