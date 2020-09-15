package com.suishi.live.app.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.RelativeLayout
import android.widget.TextView
import com.suishi.live.app.R
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission

/**
 *
 */
class MainActivity : Activity() {

    companion object{
        fun startActivity(context: Context){
            val intent=Intent(context,MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val grid = findViewById<GridView>(R.id.grid)

        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.CAMERA, Permission.Group.MICROPHONE, Permission.Group.STORAGE)
                .onGranted { permissions: List<String?>? ->
                    grid.adapter = HoloTilesAdapter()
                }
                .onDenied { permissions: List<String?>? ->
                    // Storage permission are not allowed.
                    finish()
                }
                .start()
    }

    inner class HoloTilesAdapter : BaseAdapter() {
        private val DRAWABLES = intArrayOf(
                R.drawable.blue_tile,
                R.drawable.green_tile,
                R.drawable.blue_tile
        )


        override fun getCount(): Int {
            return DRAWABLES.size
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v: RelativeLayout
            v = if (convertView == null) {
                layoutInflater.inflate(R.layout.grid_item, parent, false) as RelativeLayout
            } else {
                convertView as RelativeLayout
            }
            v.setBackgroundResource(DRAWABLES[position % 2])
            val textView1 = v.findViewById<TextView>(R.id.textView1)
            val textView2 = v.findViewById<TextView>(R.id.textView2)
            var string1 = ""
            var string2 = ""
            if (position == 0) {
                string1 = "Portrait"
                string2 = "Flv + Local"
            } else if (position == 1) {
                string1 = "Landscape"
                string2 = "Rtmp"
            } else if (position == 2) {
                string1 = "录制"
                string2 = "Part"
            } else if (position == 3) {
                string1 = "Portrait"
                string2 = "Screen + Rtmp"
            }
            textView1.text = string1
            textView2.text = string2
            v.setOnClickListener {
                if (position == 0) {
                    goPortraitAndLocal()
                } else if (position == 1) {
                    goPart()
                } else if (position == 2) {
                    camera()
                } else if (position == 3) {
                }
            }
            return v
        }
    }

    private fun goPortraitAndLocal() {
        val intent = Intent(this, VideoActivity::class.java)
        startActivity(intent)
    }

    private fun goPart() {
        val intent = Intent(this, PortraitActivity::class.java)
        startActivity(intent)
    }

    private fun camera() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }
}