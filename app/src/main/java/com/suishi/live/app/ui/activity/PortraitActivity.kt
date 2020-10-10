package com.suishi.live.app.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.suishi.live.app.R
import com.suishi.live.app.widgets.MultiToggleImageButton
import com.suishi.live.app.widgets.MultiToggleImageButton.OnStateChangeListener
import com.suishi.sslive.ui.LiveManager

/**
 * 推流
 */
class PortraitActivity : Activity() {

    companion object{
        fun startActivity(context: Context){
            val intent = Intent(context, PortraitActivity::class.java)
            context.startActivity(intent)
        }
    }

    /**
     *
     */
   // var filter: CameraGlSurfaceView? = null
    /**
     *
     */
    private var mLiveManager: LiveManager? = null
    /**
     *
     */
    private var mMicBtn: MultiToggleImageButton? = null
    /**
     *
     */
    private var mFlashBtn: MultiToggleImageButton? = null
    /**
     *
     */
    private var mFaceBtn: MultiToggleImageButton? = null
    /**
     *
     */
    private var mBeautyBtn: MultiToggleImageButton? = null
    /**
     *
     */
    private var mFocusBtn: MultiToggleImageButton? = null
    /**
     *
     */
    private var mRecordBtn: ImageButton? = null
    /**
     *
     */
    private var isGray = false
    /**
     *
     */
    private var isRecording = false

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portrait)

        mMicBtn = (findViewById(R.id.record_mic_button) as MultiToggleImageButton).apply{
            setOnStateChangeListener(object :OnStateChangeListener{
                override fun stateChanged(view: View?, state: Int) {

                }

            })
        }
        mFlashBtn = (findViewById(R.id.camera_flash_button) as MultiToggleImageButton).apply{
            setOnStateChangeListener(object : OnStateChangeListener{
                override fun stateChanged(view: View?, state: Int) {

                }
            })
        }
        mFaceBtn = (findViewById(R.id.camera_switch_button)  as MultiToggleImageButton).apply{
            setOnStateChangeListener(object :OnStateChangeListener{
                override fun stateChanged(view: View?, state: Int) {
                    // 切换摄像头
                   // mLiveManager!!.switchCamera()
                }

            })
        }
        mBeautyBtn = (findViewById(R.id.camera_render_button) as MultiToggleImageButton).apply{
            setOnStateChangeListener(object :OnStateChangeListener{
                override fun stateChanged(view: View?, state: Int) {
                    isGray = !isGray
                }

            })
        }
        mFocusBtn = (findViewById(R.id.camera_focus_button)   as MultiToggleImageButton).apply{
            setOnStateChangeListener(object :OnStateChangeListener{
                override fun stateChanged(view: View?, state: Int) {

                }

            })
        }
        mRecordBtn = (findViewById(R.id.btnRecord) as ImageButton).apply{
            setOnClickListener{ v: View? ->
                isRecording = if (isRecording) {
                    //停止推流
                    mRecordBtn!!.setBackgroundResource(R.mipmap.ic_record_start)
                    mLiveManager!!.onStopStream()
                    false
                } else {
                    //开始推流
                    mRecordBtn!!.setBackgroundResource(R.mipmap.ic_record_stop)
                    mLiveManager!!.onStartStream()
                    true
                }
            }
        }
    }

    /**
     * Dispatch onPause() to fragments.
     */
    override fun onPause() {
        super.onPause()
        mLiveManager!!.onStopStream()
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}