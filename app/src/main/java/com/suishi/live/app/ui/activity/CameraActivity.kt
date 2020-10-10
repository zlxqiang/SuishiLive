package com.suishi.live.app.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.hardware.camera2.*
import android.media.MediaScannerConnection
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.View.OnTouchListener
import android.webkit.MimeTypeMap
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.suishi.camera.CameraView
import com.suishi.camera.CircularProgressView
import com.suishi.camera.FocusImageView
import com.suishi.camera.camera.CameraBuilder2
import com.suishi.camera.camera.CameraController
import com.suishi.camera.camera.SensorControler
import com.suishi.camera.camera.SensorControler.CameraFocusListener
import com.suishi.camera.feature.close.DefaultClose
import com.suishi.camera.feature.init.DefaultInit
import com.suishi.camera.feature.open.DefaultOpen
import com.suishi.camera.feature.privew.DefaultPreview
import com.suishi.camera.feature.privew.DefaultRecord
import com.suishi.live.app.R
import com.suishi.live.app.utils.OrientationLiveData
import com.suishi.live.app.widgets.MultiToggleImageButton
import com.suishi.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 *
 */
@RequiresApi(Build.VERSION_CODES.O)
class CameraActivity : AppCompatActivity(), View.OnClickListener, OnTouchListener, CameraFocusListener, SurfaceHolder.Callback,LifecycleOwner {


    companion object {
        fun startActivity(context: AppCompatActivity) {
            val intent = Intent(context, CameraActivity::class.java)
            context.startActivityForResult(intent, 1)
        }

        //获取VideoPath
        fun getPath(path: String, fileName: String): String {
            val baseFolder = Environment.getExternalStorageDirectory().toString() + "/sslive/"
            val fd = File(baseFolder)
            if (!fd.exists()) {
                fd.mkdirs()
            }
            val p = baseFolder + path
            val f = File(p)
            return if (!f.exists() && !f.mkdirs()) {
                baseFolder + fileName
            } else p + fileName
        }

    }
    /**
     *
     */
    var mCameraView: CameraView? = null

    /**
     *
     */
    var mCameraSwitch: ImageView? = null
    /**
     *
     */
    lateinit var mCircularProgressView: CircularProgressView

    /**
     *
     */
    var mFocus: FocusImageView? = null

    /**
     * 特效
     */
    private var mMicBtn: MultiToggleImageButton? = null
    /**
     * 闪光灯
     */
    private var mFlashBtn: MultiToggleImageButton? = null
    /**
     * 人脸识别开关
     */
    private var mFaceBtn: MultiToggleImageButton? = null
    /**
     * 美颜开关
     */
    private var mBeautyBtn: MultiToggleImageButton? = null
    /**
     * 焦点开关
     */
    private var mFocusBtn: MultiToggleImageButton? = null
    /**
     *
     */
    private var mSensorControler: SensorControler? = null

    private val outputFile:File by lazy{
        val time = System.currentTimeMillis()
        val filePath=getPath("video/", "$time.mp4")
        File(filePath)
    }

    private lateinit var relativeOrientation: OrientationLiveData

    var builder2=CameraBuilder2()
    lateinit var controller:CameraController<CameraBuilder2>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        mCircularProgressView = findViewById(R.id.mCapture)

        mCameraView = (findViewById(R.id.camera_view) as CameraView).apply{
            addCallBack2(this@CameraActivity)
                       // setZOrderOnTop(true)
        }

        mCameraSwitch = (findViewById(R.id.btn_camera_switch) as ImageView).apply{
            setOnClickListener(object :View.OnClickListener{
                override fun onClick(v: View?) {
                    controller.switchCamera()
                }
            })
        }

        mFocus = findViewById(R.id.focusImageView)

        mSensorControler = SensorControler.getInstance().apply{
            setCameraFocusListener(this@CameraActivity)
        }

        builder2= CameraBuilder2()
        builder2.setInit(DefaultInit(this,this))
        builder2.useOpen(DefaultOpen(this))
        builder2.usePreview(DefaultRecord(mCameraView!!,outputFile))
        builder2.close(DefaultClose())
        controller=CameraController(builder2)
    }

    override fun onClick(view: View) {
        if(!mCircularProgressView.isRunning()) {
            mCircularProgressView.startOrStop()
            this@CameraActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
            mCircularProgressView.post({
                controller.startRecord()
            })
            Log.d("camera activity", "recording started")
        }else {
            mCircularProgressView.startOrStop()
            controller.stopRecord()
            MediaScannerConnection.scanFile(this, arrayOf(outputFile.absolutePath), null, null)
            startActivity(Intent().apply {
                action = Intent.ACTION_VIEW
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(outputFile.extension)
                val authority = this@CameraActivity.packageName + ".provider"
                data = FileProvider.getUriForFile(this@CameraActivity, authority, outputFile)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }
    }


    override fun onResume() {
        super.onResume()
        Log.e("B","onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.e("B","onPause")
        controller.close()
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {

    }

    public override fun onDestroy() {
        super.onDestroy()
        Log.e("B","onDestroy")
        controller.release()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
      LogUtils.e("cameraActivity","surfaceCreated")
        mCameraView!!.post{
            controller.open()
            controller.startPreview()
            mCircularProgressView.setOnClickListener(this)
        }
    }
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        LogUtils.e("cameraActivity","surfaceChanged")

    }
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        LogUtils.e("cameraActivity","surfaceDestroyed")
        controller.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN -> lifecycleScope.launch(Dispatchers.Main) {
                v.performClick()

            }
            MotionEvent.ACTION_UP -> lifecycleScope.launch(Dispatchers.Main) {
                v.performClick()
                //
            }
        }
        return true;
    }

    override fun onFocus() {

    }


    override fun onStart() {
        super.onStart()
        Log.e("B","onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.e("B","onStop")
    }


}