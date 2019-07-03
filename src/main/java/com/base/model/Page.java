package com.base.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
public class Page<T> implements Serializable {
    private int code;
    private int count;
    private String message;
    private List<T> data;


    public Page(int count, List data) {
        this.code = 200;
        this.count = count;
        this.message = "success";
        this.data = Optional.ofNullable(data).orElse(Collections.EMPTY_LIST);
    }

    public Page(List data) {
        this.code = 200;
        this.count = Optional.ofNullable(data).orElse(Collections.EMPTY_LIST).size();
        this.message = "success";
        this.data = Optional.ofNullable(data).orElse(Collections.EMPTY_LIST);
    }
}
