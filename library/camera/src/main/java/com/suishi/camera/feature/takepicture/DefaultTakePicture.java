package com.suishi.camera.feature.takepicture;

import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;

import com.suishi.camera.camera.CameraBuilder2;
import com.suishi.camera.feature.init.CameraInfo;
import com.suishi.camera.feature.init.DefaultInit;

import java.nio.ByteBuffer;

public class DefaultTakePicture extends TakePicture<CameraBuilder2> implements ImageReader.OnImageAvailableListener {

    private ImageReader mImageReader;

    @Override
    public void cameraBuilder(CameraBuilder2 builder) {
        super.cameraBuilder(builder);
        DefaultInit init = builder.getInit();
        if(init!=null) {
            CameraInfo info = init.getCurrentCamera();
            mImageReader = ImageReader.newInstance(info.getSize().getWidth(), info.getSize().getHeight(),
                    ImageFormat.JPEG, 2);
            mImageReader.setOnImageAvailableListener(this,null);
        }
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = reader.acquireLatestImage();
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        reader.close();

    }
}
