package com.minibox.minideveloper.Entity;

import java.io.Serializable;
import java.util.List;


public  class CommentResponse implements Serializable {

    private Integer code;
    private List<PostCommentEntity> list;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<PostCommentEntity> getList() {
        return list;
    }

    public void setList(List<PostCommentEntity> list) {
        this.list = list;
    }
}
