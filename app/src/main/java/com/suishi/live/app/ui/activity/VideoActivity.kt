package com.suishi.live.app.ui.activity

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.suishi.live.app.R

/**
 * 播放
 */
class VideoActivity : AppCompatActivity() {

    private var mVideoPath: String? = "rtmp://172.18.2.90/live/stream"

    private var mVideoUri: Uri? = null

   // private var mMediaController: AndroidMediaController? = null

  //  private var mVideoView: IjkVideoView? = null

    private var mToastTextView: TextView? = null

    private var mHudView: TableLayout? = null

    private var mDrawerLayout: DrawerLayout? = null

    private var mRightDrawer: ViewGroup? = null

    //private Settings mSettings;
    private var mBackPressed = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        mVideoPath = intent.getStringExtra("videoPath")
        val intent = intent
        val intentAction = intent.action
        if (!TextUtils.isEmpty(intentAction)) {
            if (intentAction == Intent.ACTION_VIEW) {
                mVideoPath = intent.dataString
            } else if (intentAction == Intent.ACTION_SEND) {
                mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    val scheme = mVideoUri!!.getScheme()
                    if (TextUtils.isEmpty(scheme)) {
                        Log.e(TAG, "Null unknown scheme\n")
                        finish()
                        return
                    }
                    mVideoPath = if (scheme == ContentResolver.SCHEME_ANDROID_RESOURCE) {
                        mVideoUri!!.getPath()
                    } else if (scheme == ContentResolver.SCHEME_CONTENT) {
                        Log.e(TAG, "Can not resolve content below Android-ICS\n")
                        finish()
                        return
                    } else {
                        Log.e(TAG, "Unknown scheme $scheme\n")
                        finish()
                        return
                    }
                }
            }
        }

//        if (!TextUtils.isEmpty(mVideoPath)) {
//            new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
//        }

        // init UI
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
       // mMediaController = AndroidMediaController(this, false)
       // mMediaController!!.setSupportActionBar(actionBar)
        mToastTextView = findViewById<View>(R.id.toast_text_view) as TextView
        mHudView = findViewById<View>(R.id.hud_view) as TableLayout
        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        mRightDrawer = findViewById<View>(R.id.right_drawer) as ViewGroup
        mDrawerLayout!!.setScrimColor(Color.TRANSPARENT)

        // init player
//        mVideoView = findViewById<View>(R.id.video_view) as IjkVideoView
//        mVideoView!!.setMediaController(mMediaController)
//        mVideoView!!.setHudView(mHudView)
//        // prefer mVideoPath
//        if (mVideoPath != null) mVideoView!!.setVideoPath(mVideoPath) else if (mVideoUri != null) mVideoView!!.setVideoURI(mVideoUri!!) else {
//            Log.e(TAG, "Null Data Source")
//            finish()
//            return
//        }
//        mVideoView!!.start()
    }

    override fun onBackPressed() {
        mBackPressed = true
        super.onBackPressed()
    }

    override fun onStop() {
        super.onStop()
//        if (mBackPressed || !mVideoView!!.isBackgroundPlayEnabled) {
//            mVideoView!!.stopPlayback()
//            mVideoView!!.release(true)
//            mVideoView!!.stopBackgroundPlay()
//        } else {
//            mVideoView!!.enterBackground()
//        }
    }

    companion object {
        private const val TAG = "VideoActivity"
        fun newIntent(context: Context?, videoPath: String?, videoTitle: String?): Intent {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra("videoPath", videoPath)
            intent.putExtra("videoTitle", videoTitle)
            return intent
        }

        fun intentTo(context: Context, videoPath: String?, videoTitle: String?) {
            context.startActivity(newIntent(context, videoPath, videoTitle))
        }
    }
}