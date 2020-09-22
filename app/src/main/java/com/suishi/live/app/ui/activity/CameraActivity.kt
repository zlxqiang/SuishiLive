package com.suishi.live.app.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
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
import androidx.lifecycle.lifecycleScope
import com.suishi.camera.CameraView
import com.suishi.camera.CircularProgressView
import com.suishi.camera.FocusImageView
import com.suishi.camera.camera.CameraBuilder2
import com.suishi.camera.camera.CameraController
import com.suishi.camera.camera.SensorControler
import com.suishi.camera.camera.SensorControler.CameraFocusListener
import com.suishi.camera.feature.init.DefaultInit
import com.suishi.camera.feature.open.DefaultOpen
import com.suishi.camera.feature.privew.DefaultPreview
import com.suishi.camera.feature.privew.DefaultRecord
import com.suishi.live.app.R
import com.suishi.live.app.utils.OrientationLiveData
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


        private const val RECORDER_VIDEO_BITRATE:Int=10_000_000
        private const val MIN_REQUIRED_TIME_MILLIS:Long=1000L

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

        private fun lensOrientationString(value: Int) = when(value){
            CameraCharacteristics.LENS_FACING_BACK -> "Back"
            CameraCharacteristics.LENS_FACING_FRONT -> "Front"
            CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
            else-> "UnKnown"
        }

    }
    /**
     *
     */
    var mCameraView: CameraView? = null

    /**
     *
     */
    var mCameraChange: ImageView? = null

    /**
     *
     */
    lateinit var mCapture: CircularProgressView

    /**
     *
     */
    var mFocus: FocusImageView? = null

    /**
     *
     */
    var rlCameraBefore: RelativeLayout? = null

    /**
     *
     */
    var rlCameraLater: RelativeLayout? = null

    /**
     *
     */
    var ivCameraConfirm: ImageView? = null

    /**
     *
     */
    var imagePhoto: ImageView? = null

    /**
     *
     */
    var ll_function: LinearLayout? = null

    /**
     * 美颜设置
     */
    var mBeautySwitch: CheckBox? = null

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

        mBeautySwitch = (findViewById(R.id.iv_beauty_switch) as CheckBox).apply{
           visibility=View.GONE
            setOnClickListener(this@CameraActivity)
        }

        mCapture = findViewById(R.id.mCapture)
        mCameraView = (findViewById(R.id.camera_view) as CameraView).apply{
           // setOnTouchListener(this@CameraActivity)
           // setOnFilterChangeListener(this@CameraActivity)
            addCallBack2(this@CameraActivity)
        }


        mCameraChange = (findViewById(R.id.btn_camera_switch) as ImageView).apply{
            setOnClickListener(this@CameraActivity)
        }

        mFocus = findViewById(R.id.focusImageView)
        rlCameraBefore = findViewById(R.id.rl_camera_before)
        rlCameraLater = findViewById(R.id.rl_camera_later)
        ivCameraConfirm = (findViewById(R.id.iv_camera_confirm) as ImageView).apply{
            setOnClickListener(this@CameraActivity)
        }

        imagePhoto = findViewById(R.id.image_photo)
        ll_function = findViewById(R.id.ll_function)
        findViewById<View>(R.id.iv_camera_back).setOnClickListener(this)

        findViewById<View>(R.id.iv_flash_switch).setOnClickListener(this)
        mSensorControler = SensorControler.getInstance().apply{
            setCameraFocusListener(this@CameraActivity)
        }

//        relativeOrientation=OrientationLiveData(this, characteristics).apply {
//            observe(this@CameraActivity, Observer {
//                //横竖屏
//
//            })
//        }
        builder2= (CameraBuilder2()
                .setInit(DefaultInit(this@CameraActivity))
                .useOpen(DefaultOpen(this@CameraActivity))
                as CameraBuilder2?)!!
        builder2.usePreview(DefaultRecord(mCameraView!!,outputFile))
        controller=CameraController(builder2)
    }

    override fun onClick(view: View) {
        if(!mCapture.isRunning()) {
            mCapture.startOrStop()
            this@CameraActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
            controller.startRecord()
            Log.d("camera activity", "recording started")
        }else {
            mCapture.startOrStop()
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
        //cameraThread.quitSafely()
       // recorder.release()
        //recorderSurface.release()
        controller.close()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
      //  val previewSize=getPreviewOutputSize(mCameraView!!.display, characteristics, SurfaceHolder::class.java)
      LogUtils.e("cameraActivity","surfaceCreated")

    }
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        LogUtils.e("cameraActivity","surfaceChanged")
        mCameraView!!.post{
            controller.open()
            controller.startPreview()
            mCapture.setOnClickListener(this)
        }
    }
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        LogUtils.e("cameraActivity","surfaceDestroyed")
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