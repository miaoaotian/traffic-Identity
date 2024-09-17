package com.miaoaotian.smallcar.pojo;

public class DriveTypeActionVO {
    public DriveTypeActionVO(String actionDescription, String createTime) {
        this.actionDescription = actionDescription;
        this.createTime = createTime;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public String getCreateTime() {
        return createTime;
    }

    private String actionDescription;
    private String createTime;
}
