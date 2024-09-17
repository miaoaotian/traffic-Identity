package com.miaoaotian.smallcar.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public <T> Result(int success, T data) {
    }

    public static <T> Result<T> success(T data) {return new Result<>(Constant.SUCCESS,data);}
}
