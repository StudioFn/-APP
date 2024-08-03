package com.minibox.minideveloper.Entity;

import java.io.Serializable;

public class CommentEntity implements Serializable {

    private String id;
    private String postUserId;
    private String postUserImg;
    private String postUserName;
    private String postTime;
    private String postTitle;
    private String postContent;
    private String postSubscribe;
    private int postNewOld;
    private String postLook;
    private String postLike;
    private Boolean postLiked;
    private String postType;
    private String postImages;
    private String postStatus;

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

    public int getPostNewOld() {
        return postNewOld;
    }

    public void setPostNewOld(int postNewOld) {
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

    public Boolean getLike() {
        return postLiked;
    }

    public void setLike(Boolean like) {
        postLiked = like;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getPostImages() {
        return postImages;
    }

    public void setPostImages(String postImages) {
        this.postImages = postImages;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }
}
