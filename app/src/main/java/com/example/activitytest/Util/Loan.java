package com.example.activitytest.Util;

public class Loan {
    private String Id; // 数据库中Id
    private String source; // 借贷来源
    private String time; // 借贷时间
    private double value; // 借贷金额
    private int ind; // List数组 中对应的下标

    public Loan(String source, String time, double value){
        this.source = source;
        this.time = time;
        this.value = value;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getId() {
        return Id;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
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
