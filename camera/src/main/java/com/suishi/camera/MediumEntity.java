package com.suishi.camera;

import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;
import java.util.Vector;

/**
 * Created by weight68kg on 2018/5/23.
 */

public class MediumEntity implements Serializable {

    public final static int TYPE_EMPTY = 1;
    //默认为正常的图片行
    public final static int TYPE_ITEM = 0;
    //未上传
    public static final int NOT_UPLOAD = -1;
    //已上传
    public static final int STATE_UPLOADED = 0;
    //正在上传
    public static final int STATE_UPLOADING = 1;
    //异常
    public static final int UPLODAD_STATE_FAIL = 2;
    //绝对路径
    protected String mediumPath;
    //为了适配选扯图片，增加此分类，只包括空的图片item及正常的图片项
    protected int typeId;
    //默认为新增图片
    protected String flag = ImageFlag.IMAGE_FLAG_ADD;
    //上传状态
    protected int uploadState;
    protected long mediumId;
    // image/jpeg
    protected String mineType;
    protected String title;
    protected String displayName;
    //文件大小
    protected long size;
    protected int height;
    protected int width;
    //纬度
    protected double latitude;
    //经度
    protected double longitude;
    //文件类型 匹配系统
    protected int fileType;

    private String editedImageFile;
    //用于剪裁时判定是否选中该图片
    private boolean isChked = false;
    //视频时长
    protected long duration;
    //视频封面
    private String coverurl;
    //是否上传封面，可能会单独上传
    private boolean isUploadCover = true;


    public boolean isUploadCover() {
        return isUploadCover;
    }

    public void setUploadCover(boolean uploadCover) {
        isUploadCover = uploadCover;
    }

    public MediumEntity() {
    }


    public MediumEntity(int typeId) {
        this.typeId = typeId;
    }


    public MediumEntity(int typeId, String mediumPath) {
        this.typeId = typeId;
        this.mediumPath = mediumPath;
    }

    public MediumEntity(int typeId, File file) {
        this.typeId = typeId;
        this.editedImageFile = this.mediumPath = file.getAbsolutePath();
    }

    public String getMineType() {
        return mineType;
    }

    public void setMineType(String mineType) {
        this.mineType = mineType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMediumPath() {
        return mediumPath;
    }

    public void setMediumPath(String mediumPath) {
        this.mediumPath = mediumPath;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getUploadState() {
        return uploadState;
    }

    public void setUploadState(int uploadState) {
        this.uploadState = uploadState;
    }

    public long getMediumId() {
        return mediumId;
    }

    public void setMediumId(long mediumId) {
        this.mediumId = mediumId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getCoverurl() {
        return coverurl;
    }

    public void setCoverurl(String coverurl) {
        this.coverurl = coverurl;
    }

    public int getFileType() {
        return fileType;
    }

    public boolean isChked() {
        return isChked;
    }

    public void setIsChked(boolean isChked) {
        this.isChked = isChked;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getEditedImageFile() {
        return editedImageFile;
    }

    public String getTag() {
        if (!TextUtils.isEmpty(mediumPath)) return mediumPath;
        else if (!TextUtils.isEmpty(editedImageFile)) return editedImageFile;
        else return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediumEntity that = (MediumEntity) o;
        if (mediumPath != null && mediumPath.equals(that.mediumPath)) return true;
        else if (editedImageFile != null && editedImageFile.equals(that.editedImageFile))
            return true;
        else return false;
    }

    @Override
    public int hashCode() {
        if (mediumPath != null) return mediumPath.hashCode();
        else if (editedImageFile != null) return editedImageFile.hashCode();
        else return 0;
    }

    public void setEditedImageFile(String editedImageFile) {
        this.editedImageFile = editedImageFile;

    }

    public static Vector<MediumEntity> removeEmptyElement(Vector<MediumEntity> realImages) {
        Vector<MediumEntity> imageEntities = new Vector<>();
        for (MediumEntity imageEntity : realImages) {
            if (imageEntity.getTypeId() == ImageEntity.TYPE_ITEM && !imageEntity.getFlag().equals(ImageFlag.IMAGE_FLAG_CANCLE)) {
                imageEntities.add(imageEntity);
            }
        }
        return imageEntities;
    }
}
