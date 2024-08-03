package com.minibox.minideveloper.Entity;

import java.io.Serializable;
import java.util.List;

public class DayArticle implements Serializable {


    private Integer code;
    private String msg;
    private DataDTO data;
    private Integer time;
    private String logId;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public static class DataDTO implements Serializable {
        private String date;
        private List<TopStoriesDTO> top_stories;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<TopStoriesDTO> getTopStories() {
            return top_stories;
        }

        public void setTopStories(List<TopStoriesDTO> topStories) {
            this.top_stories = topStories;
        }

        public static class TopStoriesDTO implements Serializable {
            private Integer id;
            private String url;
            private String hint;
            private Integer type;
            private String image;
            private String title;
            private String gaPrefix;
            private String imageHue;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getHint() {
                return hint;
            }

            public void setHint(String hint) {
                this.hint = hint;
            }

            public Integer getType() {
                return type;
            }

            public void setType(Integer type) {
                this.type = type;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getGaPrefix() {
                return gaPrefix;
            }

            public void setGaPrefix(String gaPrefix) {
                this.gaPrefix = gaPrefix;
            }

            public String getImageHue() {
                return imageHue;
            }

            public void setImageHue(String imageHue) {
                this.imageHue = imageHue;
            }
        }
    }
}