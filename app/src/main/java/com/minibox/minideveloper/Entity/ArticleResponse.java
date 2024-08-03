package com.minibox.minideveloper.Entity;

import java.io.Serializable;
import java.util.List;


public class ArticleResponse implements Serializable {

    private Integer code;
    private List<CommentEntity> list;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<CommentEntity> getList() {
        return list;
    }

    public void setList(List<CommentEntity> list) {
        this.list = list;
    }
}
