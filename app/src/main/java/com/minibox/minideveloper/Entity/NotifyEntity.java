package com.minibox.minideveloper.Entity;

import java.io.Serializable;
import java.util.List;

public class NotifyEntity implements Serializable {

    private Integer code;
    private List<ListDTO> list;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<ListDTO> getList() {
        return list;
    }

    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public static class ListDTO implements Serializable {
        private String notifId;
        private String postId;
        private String postUid;
        private String notifyStatus;
        private String postUserId;
        private String postUserImg;
        private String notifUserId;
        private int articleType;
        private String postUserName;
        private String postCommentContent;

        public String getNotifId() {
            return notifId;
        }

        public void setNotifId(String notifId) {
            this.notifId = notifId;
        }

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public String getPostUid() {
            return postUid;
        }

        public void setPostUid(String postUid) {
            this.postUid = postUid;
        }

        public String getNotifyStatus() {
            return notifyStatus;
        }

        public void setNotifyStatus(String notifyStatus) {
            this.notifyStatus = notifyStatus;
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

        public String getNotifUserId() {
            return notifUserId;
        }

        public void setNotifUserId(String notifUserId) {
            this.notifUserId = notifUserId;
        }

        public String getPostUserName() {
            return postUserName;
        }

        public void setPostUserName(String postUserName) {
            this.postUserName = postUserName;
        }

        public String getPostCommentContent() {
            return postCommentContent;
        }

        public void setPostCommentContent(String postCommentContent) {
            this.postCommentContent = postCommentContent;
        }

        public int getArticleType() {
            return articleType;
        }

        public void setArticleType(int articleType) {
            this.articleType = articleType;
        }
    }
}
