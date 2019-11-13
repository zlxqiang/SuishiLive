package com.suishi.camera.camera;

import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;


import com.suishi.camera.camera.utils.CameraParamUtil;
import com.suishi.camera.camera.utils.DensityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cj on 2017/8/2.
 * desc 相机的管理类 主要是Camera的一些设置
 * 包括预览和录制尺寸、闪光灯、曝光、聚焦、摄像头切换等
 */

public class CameraController implements ICamera {

    /**
     * 相机的宽高及比例配置
     */
    private ICamera.Config mConfig;
    /**
     * 相机实体
     */
    private Camera mCamera;
    /**
     * 预览的尺寸
     */
    private Camera.Size preSize;
    /**
     * 实际的尺寸
     */
    private Camera.Size picSize;

    private Point mPreSize;
    private Point mPicSize;
    private int cameraAngle = 90;//摄像头角度   默认为90度


    public Camera getmCamera() {
        return mCamera;
    }

    public CameraController() {
        /**初始化一个默认的格式大小*/
        mConfig = new ICamera.Config();
        mConfig.minPreviewWidth = 720;
        mConfig.minPictureWidth = 720;
        mConfig.rate = 1.778f;
    }

    public void open(int cameraId) {
        mCamera = Camera.open(cameraId);
        safeToTakePicture = true;
        doStartPreview();
    }

    private void doStartPreview() {
        if (mCamera != null) {
                /**选择当前设备允许的预览尺寸*/
                Camera.Parameters param = mCamera.getParameters();
                preSize = getPropPreviewSize(param.getSupportedPreviewSizes(), mConfig.rate,
                        mConfig.minPreviewWidth);
                picSize = getPropPictureSize(param.getSupportedPictureSizes(), mConfig.rate,
                        mConfig.minPictureWidth);
                param.setPictureSize(picSize.width, picSize.height);
                param.setPreviewSize(preSize.width, preSize.height);

                mCamera.setParameters(param);
                Camera.Size pre = param.getPreviewSize();
                Camera.Size pic = param.getPictureSize();
                mPicSize = new Point(pic.height, pic.width);
                mPreSize = new Point(pre.height, pre.width);

                if (CameraParamUtil.getInstance().isSupportedFocusMode(
                        param.getSupportedFocusModes(),
                        Camera.Parameters.FOCUS_MODE_AUTO)) {
                    param.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
                if (CameraParamUtil.getInstance().isSupportedPictureFormats(param.getSupportedPictureFormats(),
                        ImageFormat.JPEG)) {
                    param.setPictureFormat(ImageFormat.JPEG);
                    param.setJpegQuality(100);
                }
                mCamera.setParameters(param);
//                mCamera.setPreviewDisplay(holder);  //SurfaceView
//                mCamera.setDisplayOrientation(cameraAngle);//浏览角度
                mCamera.startPreview();//启动浏览
        }
    }

    @Override
    public void setPreviewTexture(SurfaceTexture texture) {
        if (mCamera != null) {
            try {
                Log.e("hero", "----setPreviewTexture");
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setConfig(Config config) {
        this.mConfig = config;
    }

    @Override
    public void setOnPreviewFrameCallback(final PreviewFrameCallback callback) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    callback.onPreviewFrame(data, mPreSize.x, mPreSize.y);
                    Log.e("cameraController","onPreviewFrame");
                }
            });
        }
    }

    @Override
    public void preview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    @Override
    public Point getPreviewSize() {
        return mPreSize;
    }

    @Override
    public Point getPictureSize() {
        return mPicSize;
    }

    @Override
    public boolean close() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return false;
    }

    /**
     * 手动聚焦
     *
     * @param point 触屏坐标 必须传入转换后的坐标
     */
    public void onFocus(Point point, Camera.AutoFocusCallback callback) {
        if (mCamera==null)return;
        Camera.Parameters parameters = mCamera.getParameters();
        boolean supportFocus = true;
        boolean supportMetering = true;
        //不支持设置自定义聚焦，则使用自动聚焦，返回
        if (parameters.getMaxNumFocusAreas() <= 0) {
            supportFocus = false;
        }
        if (parameters.getMaxNumMeteringAreas() <= 0) {
            supportMetering = false;
        }
        List<Camera.Area> areas = new ArrayList<Camera.Area>();
        List<Camera.Area> areas1 = new ArrayList<Camera.Area>();
        //再次进行转换
        point.x = (int) (((float) point.x) / DensityUtils.getScreenWidth() * 2000 - 1000);
        point.y = (int) (((float) point.y) / DensityUtils.getScreenHeight() * 2000 - 1000);

        int left = point.x - 300;
        int top = point.y - 300;
        int right = point.x + 300;
        int bottom = point.y + 300;
        left = left < -1000 ? -1000 : left;
        top = top < -1000 ? -1000 : top;
        right = right > 1000 ? 1000 : right;
        bottom = bottom > 1000 ? 1000 : bottom;
        areas.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
        areas1.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
        if (supportFocus) {
            parameters.setFocusAreas(areas);
        }
        if (supportMetering) {
            parameters.setMeteringAreas(areas1);
        }

        try {
            mCamera.setParameters(parameters);// 部分手机 会出Exception（红米）
            mCamera.autoFocus(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Camera.Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);
        int i = 0;
        for (Camera.Size s : list) {
            if ((s.height >= minWidth) && equalRate(s, th)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }

    private Camera.Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Camera.Size s : list) {
            if ((s.height >= minWidth) && equalRate(s, th)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }

    private static boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

    private Comparator<Camera.Size> sizeComparator = new Comparator<Camera.Size>() {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.height == rhs.height) {
                return 0;
            } else if (lhs.height > rhs.height) {
                return 1;
            } else {
                return -1;
            }
        }
    };

    private boolean safeToTakePicture = true;

    /**
     * 照相
     */
    public void takePicture(final TakePictureCallBack callBack) {
        if (safeToTakePicture) {
            safeToTakePicture = false;


            mCamera.takePicture(new Camera.ShutterCallback() {
                @Override
                public void onShutter() {
                    if (callBack != null) {
                        callBack.onShutter();
                    }
                }
            }, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    if (callBack != null) {
                        callBack.onRawPictureTaken(data, camera);
                    }
                }
            }, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    if (callBack != null) {
                        callBack.onJpegPictureTaken(data, camera);
                    }
                }
            });
        }
    }

    //开启闪光灯
    public void OpenLightOn() {
        if (null == mCamera) {
            mCamera = Camera.open();
        }
        Camera.Parameters parameters = mCamera.getParameters();
        // 判断闪光灯当前状态來修改
        if (Camera.Parameters.FLASH_MODE_OFF.equals(parameters.getFlashMode())) {
            turnOn(parameters);
        } else if (Camera.Parameters.FLASH_MODE_ON.equals(parameters.getFlashMode())) {
            turnOff(parameters);
        }
    }

    //開
    private void turnOn(Camera.Parameters parameters) {
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    //關
    private void turnOff(Camera.Parameters parameters) {
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    //关闭闪光灯
    public void CloseLightOff() {
        if (mCamera != null) {
            //直接关闭
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);//关闭
            mCamera.setParameters(parameters);
            mCamera.release();
        }
    }


    public interface TakePictureCallBack {
        /* 要处理raw data?写?否 */
        public void onRawPictureTaken(byte[] data, Camera camera);

        //在takepicture中调用的回调方法之一，接收jpeg格式的图像
        public void onJpegPictureTaken(byte[] data, Camera camera);

        /* 按下快门瞬间会调用这里的程序 */
        public void onShutter();
    }

}
