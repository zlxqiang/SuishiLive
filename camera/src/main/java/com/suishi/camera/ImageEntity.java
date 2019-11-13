package com.suishi.camera;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Cong Hao on 2015/12/29.
 *
 * @author Cong Hao
 */
public class ImageEntity extends MediumEntity implements Serializable {





    private String flag = ImageFlag.IMAGE_FLAG_ADD;//默认为新增图片
    private File file;

public ImageEntity(){}

    public ImageEntity(int mediumId, String mediumPath, String flag) {
        this.mediumId = mediumId;
        this.mediumPath = mediumPath;
        this.flag = flag;
    }




    private String clickImageName;
    private String uploadingName;

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ImageEntity that = (ImageEntity) o;
//        if (mediumPath != null && mediumPath.equals(that.mediumPath)) return true;
//        else if (editedImageFile != null && editedImageFile.equals(that.editedImageFile))
//            return true;
//        else return false;
//    }
//
//    @Override
//    public int hashCode() {
//        if (mediumPath != null) return mediumPath.hashCode();
//        else if (editedImageFile != null) return editedImageFile.hashCode();
//        else return 0;
//    }




    public File getFile(){
        return new File(mediumPath);
    }


    public String getClickImageName() {
        return clickImageName;
    }

    public void setClickImageName(String clickImageName) {
        this.clickImageName = clickImageName;
    }





    public String getUploadingName() {
        return uploadingName;
    }

    public void setUploadingName(String uploadingName) {
        this.uploadingName = uploadingName;
    }

//    public String getTag() {
//        if (!TextUtils.isEmpty(mediumPath)) return mediumPath;
//        else if (!TextUtils.isEmpty(editedImageFile)) return editedImageFile;
//        else return null;
//    }


    public int getUploadState() {
        return uploadState;
    }

    public void setUploadState(int uploadState) {
        this.uploadState = uploadState;
    }




}
