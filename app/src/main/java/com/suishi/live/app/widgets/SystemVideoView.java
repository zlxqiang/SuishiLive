package com.suishi.live.app.widgets;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.VideoView;


/**
 * Created by weight68kg on 2018/6/20.
 */
public class SystemVideoView extends VideoView {


    private int videoWidth;//width
    private int videoHeight;
    private int displayAspectRatio = 1;

    public SystemVideoView(Context context) {
        super(context);
    }

    public SystemVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SystemVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    protected void init(Context context) {
        this.videoHeight = context.getResources().getDisplayMetrics().heightPixels;
        this.videoWidth = context.getResources().getDisplayMetrics().widthPixels;

        super.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {

                SystemVideoView.this.videoWidth = mp.getVideoWidth();
                SystemVideoView.this.videoHeight = mp.getVideoHeight();
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            if (onCorveHideListener != null) {
                                onCorveHideListener.requestHide();
                            }
                        }
                        if (onInfoListener != null) {
                            onInfoListener.onInfo(mp, what, extra);
                        }
                        return false;
                    }
                });

                if (onPreparedListener != null) {
                    onPreparedListener.onPrepared(mp);
                }
            }
        });
    }

    MediaPlayer.OnPreparedListener onPreparedListener = null;

    public interface OnCorveHideListener {
        void requestHide();
    }

    @Override
    public void setOnInfoListener(MediaPlayer.OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
    }

    MediaPlayer.OnInfoListener onInfoListener;

    public void setOnCorveHideListener(OnCorveHideListener onCorveHideListener) {
        this.onCorveHideListener = onCorveHideListener;
    }

    OnCorveHideListener onCorveHideListener;

    @Override
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        this.onPreparedListener = l;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        MeasureUtil.Size measure = MeasureUtil.measure(displayAspectRatio, widthMeasureSpec, heightMeasureSpec, videoWidth, videoHeight);
        setMeasuredDimension(measure.width, measure.height);

    }


    public void setDisplayAspectRatio(int var1) {
        displayAspectRatio = var1;
        this.requestLayout();

    }


    public int getDisplayAspectRatio() {
        return displayAspectRatio;
    }

    public void setCorver(int resource) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resource, opts);

    }
}
