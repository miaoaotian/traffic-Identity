package com.miaoaotian.smallcar.pojo;

public class AllSignalVO {
    private String signalType;
    private int count;

    public AllSignalVO(String signalType, int count) {
        this.signalType = signalType;
        this.count = count;
    }

    public String getSignalType() {
        return signalType;
    }

    public int getCount() {
        return count;
    }
}
