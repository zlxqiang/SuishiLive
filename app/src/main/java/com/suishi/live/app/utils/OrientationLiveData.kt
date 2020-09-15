package com.suishi.live.app.utils

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.view.OrientationEventListener
import android.view.Surface
import androidx.lifecycle.LiveData
import com.seu.magicfilter.utils.Rotation

class OrientationLiveData (context: Context,characteristics: CameraCharacteristics): LiveData<Int>() {

    companion object{

        private fun computeRelativeRotation(characteristics: CameraCharacteristics,surfaceRotation: Int):Int{
            val sensorOrientationDegree=characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
            val deviceOrientationDegree=when(surfaceRotation){
                Surface.ROTATION_0-> 0
                Surface.ROTATION_90-> 90
                Surface.ROTATION_180-> 180
                Surface.ROTATION_270 -> 270
                else -> 0
            }

            val sign = if(characteristics.get(CameraCharacteristics.LENS_FACING)==CameraCharacteristics.LENS_FACING_FRONT) 1 else -1
            return (sensorOrientationDegree-(deviceOrientationDegree*sign)+360) %360
        }

    }

    private val listener=object :OrientationEventListener(context.applicationContext){
        override fun onOrientationChanged(orientation: Int) {
            val rotation=when{
                orientation<=45-> Surface.ROTATION_0
                orientation<=135-> Surface.ROTATION_90
                orientation<=225-> Surface.ROTATION_180
                orientation<=315->Surface.ROTATION_270
                else -> Surface.ROTATION_0
            }
            val relative=computeRelativeRotation(characteristics,rotation)
            if(relative!=value) postValue(relative)
        }

    }


    override fun onActive() {
        super.onActive()
        listener.enable()
    }

    override fun onInactive() {
        super.onInactive()
        listener.disable()
    }

}