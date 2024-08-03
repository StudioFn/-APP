package com.minibox.minideveloper.Entity;

import java.io.Serializable;
import java.util.List;

public class PostCommentEntity implements Serializable {


    private String PostID;

    private String UID;

    private String HeadPortrait;

    private String UserName;

    private String PostContent;

    private String PostTime;

    public String getPostID() {
        return PostID;
    }

    public void setPostID(String postID) {
        PostID = postID;
    }

    public String getUid() {
        return UID;
    }

    public void setUid(String uid) {
        this.UID = uid;
    }

    public String getHeadPortrait() {
        return HeadPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        HeadPortrait = headPortrait;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPostContent() {
        return PostContent;
    }

    public void setPostContent(String postContent) {
        PostContent = postContent;
    }

    public String getPostTime() {
        return PostTime;
    }

    public void setPostTime(String postTime) {
        PostTime = postTime;
    }
}
