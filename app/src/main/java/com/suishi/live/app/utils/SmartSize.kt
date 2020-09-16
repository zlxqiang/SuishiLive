package com.suishi.live.app.utils

import android.graphics.Point
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.params.StreamConfigurationMap
import android.util.Size
import android.view.Display
import kotlin.math.max
import kotlin.math.min

class SmartSize(width:Int,height:Int) {
    var size= Size(width,height)
    var long=max(size.width,size.height)
    var short=min(size.width,size.height)
    override fun toString()="SmartSize(${long}x${short})"
}

val SIZE_1080P:SmartSize= SmartSize(1920,1080)

fun getDisplaySmartSize(display: Display):SmartSize{
    val outPoint=Point()
    display.getRealSize(outPoint)
    return SmartSize(outPoint.x,outPoint.y)
}

fun <T> getPreviewOutputSize(display:Display,characteristics: CameraCharacteristics,targetClass:Class<T>,format:Int?=null):Size{
    val screenSize= getDisplaySmartSize(display)
    val hdScreen=screenSize.long>= SIZE_1080P.long || screenSize.short>= SIZE_1080P.short
    val maxSize=if(hdScreen) SIZE_1080P else screenSize

    val config=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
    if(format==null){
        assert(StreamConfigurationMap.isOutputSupportedFor(targetClass))
    }else{
        assert(config.isOutputSupportedFor(format))
    }

    val allSize=if(format==null) config.getOutputSizes(targetClass) else config.getOutputSizes(format)

    val validSize=allSize.sortedWith(compareBy{it.height*it.width}).map { SmartSize(it.width,it.height) }.reversed()
    return validSize.first{it.long<=maxSize.long && it.short<=maxSize.short}.size
}