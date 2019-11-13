package com.suishi.camera;

import java.io.Serializable;

/**
 * Created by dongtaizhao on 2017/10/16.
 *
 */

public class ImageFlag implements Serializable {

    public static String IMAGE_FLAG_ADD = "add";
    public static String IMAGE_FLAG_EDIT="edit";
    public static String IMAGE_FLAG_CANCLE = "cancel";

    private long id;
    private String flag;
    private String url;
    private String mark;
    private int fileType;
    private String cover;

    public ImageFlag(long id, String flag, String url, String mark, int fileType, String cover){
        this.id=id;
        this.flag=flag;
        this.url=url;
        this.mark=mark;
        this.cover=cover;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
