package com.suishi.camera.camera.init;

import android.util.Size;

public class CameraInfo {
    private String name;
    private String cameraId;
    private Size size;
    private int fps;

    public CameraInfo(String name, String cameraId, Size size, int fps) {
        this.name = name;
        this.cameraId = cameraId;
        this.size = size;
        this.fps = fps;
    }

    public String getName() {
        return name;
    }

    public String getCameraId() {
        return cameraId;
    }

    public Size getSize() {
        return size;
    }

    public int getFps() {
        return fps;
    }
}
