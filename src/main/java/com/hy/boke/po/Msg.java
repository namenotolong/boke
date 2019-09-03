package com.hy.boke.po;

import lombok.Data;

@Data
public class Msg<T> {
    private String name;
    private String code;
    private Boolean success;
    private T data;
}
