package com.suishi.live.app.modle

import android.util.Size


class CameraInfo {

    constructor(name: String?, cameraId: String?, size: Size?, fps: Int) {
        this.name = name
        this.cameraId = cameraId
        this.size = size
        this.fps = fps
    }

    var name:String?=null
    var cameraId:String?=null
    var size: Size?=null
    var fps:Int=0

}