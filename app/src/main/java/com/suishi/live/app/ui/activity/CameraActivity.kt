package com.suishi.live.app.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.media.MediaCodec
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
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
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.seu.magicfilter.filter.helper.MagicFilterType
import com.suishi.camera.CameraView
import com.suishi.camera.CircularProgressView
import com.suishi.camera.FocusImageView
import com.suishi.camera.camera.SensorControler
import com.suishi.camera.camera.SensorControler.CameraFocusListener
import com.suishi.camera.camera.gpufilter.SlideGpuFilterGroup.OnFilterChangeListener
import com.suishi.live.app.R
import com.suishi.live.app.modle.CameraInfo
import com.suishi.live.app.utils.OrientationLiveData
import com.suishi.live.app.utils.getPreviewOutputSize
import com.suishi.utils.ToastUtil
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImage3x3ConvolutionFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 *
 */
@RequiresApi(Build.VERSION_CODES.O)
class CameraActivity : AppCompatActivity(), View.OnClickListener, OnTouchListener, CameraFocusListener, OnFilterChangeListener, SurfaceHolder.Callback,LifecycleOwner {


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
    var mCapture: CircularProgressView? = null

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

    private val cameraManager:CameraManager by lazy{
        getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private val cameraList:MutableList<CameraInfo> by lazy {
        val availableCameras:MutableList<CameraInfo> = mutableListOf()
        cameraManager.cameraIdList.forEach { id->
            val characteristics=cameraManager.getCameraCharacteristics(id)
            val orientation=lensOrientationString(characteristics.get(CameraCharacteristics.LENS_FACING)!!)
            val capabilities=characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)!!
            val cameraConfig =characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            if(capabilities.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE)){
                val targetClass=MediaRecorder::class.java
                cameraConfig.getOutputSizes(targetClass).forEach { size->
                    val secondsPerFrame=cameraConfig.getOutputMinFrameDuration(targetClass, size) / 1_000_000_000.0
                    val fps=if(secondsPerFrame>0)(1.0/secondsPerFrame).toInt() else 0
                    val fpsLabel=if(fps> 0) "$fps" else "N/A"
                    availableCameras.add(CameraInfo("$orientation ($id) $size $fpsLabel FPS", id, size, fps))
                }
            }
        }
       availableCameras
    }

    private val characteristics:CameraCharacteristics by lazy{
        cameraManager.getCameraCharacteristics(cameraList[0].cameraId)
    }

    private val outputFile:File by lazy{
        val time = System.currentTimeMillis()
        val filePath=getPath("video/", "$time.mp4")
        File(filePath)
    }

    private val previewSurface:Surface by lazy{
        mCameraView!!.render.surfaceTexture.setDefaultBufferSize(cameraList[0].size!!.width,cameraList[0].size!!.height);
        Surface(mCameraView!!.render.surfaceTexture)
    }

    private val recorderSurface: Surface by lazy{
        val surface= MediaCodec.createPersistentInputSurface()
        createRecorder(surface).apply {
            prepare()
            release()
        }
        surface

    }


    /**
     * 保存视频
     */
    private val recorder:MediaRecorder by lazy{
        createRecorder(recorderSurface)
    }

    private val cameraThread =HandlerThread("CameraThread").apply{
        start()
    }

    private val cameraHandler=Handler(cameraThread.looper)


    private lateinit var session:CameraCaptureSession

    private lateinit var camera:CameraDevice

    private val imageReder:ImageReader by lazy{
        val imageReader=ImageReader.newInstance(cameraList[0].size!!.width, cameraList[0].size!!.height,
                ImageFormat.JPEG, 2)
        imageReader.setOnImageAvailableListener({
            val image = it.acquireLatestImage()
            val buffer = image.planes[0].buffer
            val data = ByteArray(buffer.remaining())
            buffer.get(data)
            it.close()
            mCameraView!!.render.onPreviewFrame(data,cameraList[0].size!!.width, cameraList[0].size!!.height)
        }, null)
        imageReader
    }

    private val previewRequest:CaptureRequest by lazy{
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
            addTarget(previewSurface)
        }.build()
    }

    private val recordRequest:CaptureRequest by lazy{
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
            addTarget(recorderSurface)
        }.build()
    }

    private var recordingStartMillis:Long=0L

    private lateinit var relativeOrientation: OrientationLiveData


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createRecorder(surface: Surface)=MediaRecorder().apply{
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setVideoSource(MediaRecorder.VideoSource.SURFACE)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setOutputFile(outputFile.absoluteFile)
        setVideoEncodingBitRate(RECORDER_VIDEO_BITRATE)
        if(cameraList[0].fps>0) setVideoFrameRate(cameraList[0].fps)
        setVideoSize(cameraList[0].size!!.width, cameraList[0].size!!.height)
        setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setInputSurface(surface)

    }

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
            setSize(cameraList[0].size)
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

        relativeOrientation=OrientationLiveData(this, characteristics).apply {
            observe(this@CameraActivity, Observer {
                //横竖屏

            })
        }

    }

    private fun initializeCamera()=lifecycleScope.launch(Dispatchers.Main){
        camera=openCamera(cameraManager, cameraList[0].cameraId!!, cameraHandler)
        val target=listOf(previewSurface, recorderSurface)
        session=createCaptureSession(camera, target, cameraHandler)
        session.setRepeatingRequest(previewRequest, null, cameraHandler)
        mCapture!!.setOnTouchListener(this@CameraActivity)
    }



    private suspend fun openCamera(manager: CameraManager, cameraId: String, handler: Handler? = null):CameraDevice= suspendCancellableCoroutine{ cont->
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ToastUtil.shortToast("没有相机权限")
        }else {
            manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cont.resume(camera)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    this@CameraActivity.finish()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    val msg = when (error) {
                        ERROR_CAMERA_DEVICE -> "相机设备发生了一个致命错误"
                        ERROR_CAMERA_DISABLED -> "Device policy"
                        ERROR_CAMERA_IN_USE -> "当前相机设备已经在一个更高优先级的地方打开了"
                        ERROR_MAX_CAMERAS_IN_USE -> "已打开相机数量到上限了，无法再打开新的相机了"
                        else -> "UnKnown"
                    }
                    val exc = RuntimeException("Camera $cameraId error:($error) $msg")
                    Log.e("open camera", exc.message, exc)

                }

            }, handler)
        }

    }

    private suspend fun createCaptureSession(device: CameraDevice, target: List<Surface>, handler: Handler? = null):CameraCaptureSession= suspendCoroutine{ cont->
        device.createCaptureSession(target, object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                cont.resume(session)
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("camera ${device.id} session configuration failed")
                Log.e("create cature session", exc.message, exc)
                cont.resumeWithException(exc)
            }

        }, handler)
    }


    override fun onClick(view: View) {

    }


    override fun onResume() {
        super.onResume()
        Log.e("B","onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.e("B","onPause")
        camera.close()
    }

    override fun onFilterChange(type: MagicFilterType) {

    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {

    }

    public override fun onDestroy() {
        super.onDestroy()
        Log.e("B","onDestroy")
        cameraThread.quitSafely()
        recorder.release()
        recorderSurface.release()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        val previewSize=getPreviewOutputSize(mCameraView!!.display, characteristics, SurfaceHolder::class.java)
        mCameraView!!.post{
            initializeCamera()
        }
    }
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }
    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN -> lifecycleScope.launch(Dispatchers.Main) {
                v.performClick()
                this@CameraActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
                session.setRepeatingRequest(recordRequest, null, cameraHandler)
                recorder.apply {
                    relativeOrientation.value?.let { setOrientationHint(it) }
                    prepare()
                    start()
                }
                recordingStartMillis = System.currentTimeMillis()
                Log.d("camera activity", "recording started")
            }
            MotionEvent.ACTION_UP -> lifecycleScope.launch(Dispatchers.Main) {
                v.performClick()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                val elapsedTimeMillis = System.currentTimeMillis() - recordingStartMillis
                if (elapsedTimeMillis < MIN_REQUIRED_TIME_MILLIS) {
                    delay(MIN_REQUIRED_TIME_MILLIS - elapsedTimeMillis)
                }
                Log.d("camera activity", "recording stopped output: $outputFile")
                recorder.stop()
                MediaScannerConnection.scanFile(v.context, arrayOf(outputFile.absolutePath), null, null)
                startActivity(Intent().apply {
                    action = Intent.ACTION_VIEW
                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(outputFile.extension)
                    val authority = this@CameraActivity.packageName + ".provider"
                    data = FileProvider.getUriForFile(v.context, authority, outputFile)
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_CLEAR_TOP
                })
                //delay(100L)
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