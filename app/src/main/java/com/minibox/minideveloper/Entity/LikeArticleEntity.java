/*
 * 作者：远赤Copyright (c) 2023
 * 未经允许，禁止转载！！
 * 联系方式：QQ
 * 2308762185
 */

package com.minibox.minideveloper.Entity;

import java.io.Serializable;
import java.util.List;

public class LikeArticleEntity implements Serializable {

    private Integer code;
    private String message;
    private List<ListArticle> list;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ListArticle> getList() {
        return list;
    }

    public void setList(List<ListArticle> list) {
        this.list = list;
    }

    public static class ListArticle implements Serializable {
        private String id;
        private String postUserId;
        private String postUserImg;
        private String postUserName;
        private String postTime;
        private String postTitle;
        private String postContent;
        private String postSubscribe;
        private String postNewOld;
        private String postLook;
        private String postLike;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPostUserId() {
            return postUserId;
        }

        public void setPostUserId(String postUserId) {
            this.postUserId = postUserId;
        }

        public String getPostUserImg() {
            return postUserImg;
        }

        public void setPostUserImg(String postUserImg) {
            this.postUserImg = postUserImg;
        }

        public String getPostUserName() {
            return postUserName;
        }

        public void setPostUserName(String postUserName) {
            this.postUserName = postUserName;
        }

        public String getPostTime() {
            return postTime;
        }

        public void setPostTime(String postTime) {
            this.postTime = postTime;
        }

        public String getPostTitle() {
            return postTitle;
        }

        public void setPostTitle(String postTitle) {
            this.postTitle = postTitle;
        }

        public String getPostContent() {
            return postContent;
        }

        public void setPostContent(String postContent) {
            this.postContent = postContent;
        }

        public String getPostSubscribe() {
            return postSubscribe;
        }

        public void setPostSubscribe(String postSubscribe) {
            this.postSubscribe = postSubscribe;
        }

        public String getPostNewOld() {
            return postNewOld;
        }

        public void setPostNewOld(String postNewOld) {
            this.postNewOld = postNewOld;
        }

        public String getPostLook() {
            return postLook;
        }

        public void setPostLook(String postLook) {
            this.postLook = postLook;
        }

        public String getPostLike() {
            return postLike;
        }

        public void setPostLike(String postLike) {
            this.postLike = postLike;
        }
    }
}
