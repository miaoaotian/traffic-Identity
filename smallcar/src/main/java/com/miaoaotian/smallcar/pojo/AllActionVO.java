package com.miaoaotian.smallcar.pojo;

public class AllActionVO {
    private String objTypeCn;
    private int count;

    // 构造函数
    public AllActionVO(String objTypeCn, int count) {
        this.objTypeCn = objTypeCn;
        this.count = count;
    }

    // Getter and Setter
    public String getObjTypeCn() {
        return objTypeCn;
    }

    public void setObjTypeCn(String objTypeCn) {
        this.objTypeCn = objTypeCn;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

