/*
 * 作者：远赤Copyright (c) 2023
 * 未经允许，禁止转载！！
 * 联系方式：QQ
 * 2308762185
 */

package com.minibox.minideveloper.Entity;

import java.io.Serializable;
import java.util.List;

public class WyMusicEntity implements Serializable {

    /**
     * result : {"searchQcReminder":null,"songs":[{"name":"踏上旅途（扩写版）","id":2043276824,"ar":[{"id":47125630,"name":"黑铱BlackIris","tns":[],"alias":[]}],"al":{"id":164568730,"name":"踏上旅途\u2014\u2014崩坏星穹铁道登车cg音乐扩写版","picUrl":"http://p1.music.126.net/miaIulvgZSHjn70LZ3SNcA==/109951168576468314.jpg","tns":[],"pic_str":"109951168576468314","pic":109951168576468314}}],"songCount":92}
     * code : 200
     */

    private ResultBean result;
    private int code;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class ResultBean implements Serializable {
        /**
         * searchQcReminder : null
         * songs : [{"name":"踏上旅途（扩写版）","id":2043276824,"ar":[{"id":47125630,"name":"黑铱BlackIris","tns":[],"alias":[]}],"al":{"id":164568730,"name":"踏上旅途\u2014\u2014崩坏星穹铁道登车cg音乐扩写版","picUrl":"http://p1.music.126.net/miaIulvgZSHjn70LZ3SNcA==/109951168576468314.jpg","tns":[],"pic_str":"109951168576468314","pic":109951168576468314}}]
         * songCount : 92
         */

        private List<SongsBean> songs;

        public List<SongsBean> getSongs() {
            return songs;
        }

        public void setSongs(List<SongsBean> songs) {
            this.songs = songs;
        }

        public static class SongsBean implements Serializable {
            /**
             * name : 踏上旅途（扩写版）
             * id : 2043276824
             * ar : [{"id":47125630,"name":"黑铱BlackIris","tns":[],"alias":[]}]
             * al : {"id":164568730,"name":"踏上旅途\u2014\u2014崩坏星穹铁道登车cg音乐扩写版","picUrl":"http://p1.music.126.net/miaIulvgZSHjn70LZ3SNcA==/109951168576468314.jpg","tns":[],"pic_str":"109951168576468314","pic":109951168576468314}
             */

            private String name;
            private int id;
            private AlBean al;
            private List<ArBean> ar;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public AlBean getAl() {
                return al;
            }

            public void setAl(AlBean al) {
                this.al = al;
            }

            public List<ArBean> getAr() {
                return ar;
            }

            public void setAr(List<ArBean> ar) {
                this.ar = ar;
            }

            public static class AlBean implements Serializable {
                /**
                 * id : 164568730
                 * name : 踏上旅途——崩坏星穹铁道登车cg音乐扩写版
                 * picUrl : http://p1.music.126.net/miaIulvgZSHjn70LZ3SNcA==/109951168576468314.jpg
                 * tns : []
                 * pic_str : 109951168576468314
                 * pic : 109951168576468314
                 */

                private int id;
                private String name;
                private String picUrl;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getPicUrl() {
                    return picUrl;
                }

                public void setPicUrl(String picUrl) {
                    this.picUrl = picUrl;
                }
            }

            public static class ArBean implements Serializable {
                /**
                 * id : 47125630
                 * name : 黑铱BlackIris
                 */

                private int id;
                private String name;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }
        }
    }
}
