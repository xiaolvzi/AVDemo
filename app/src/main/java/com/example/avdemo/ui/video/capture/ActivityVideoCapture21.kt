package com.example.avdemo.ui.video.capture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.avdemo.R
import com.example.avdemo.common.ViewPathConst
import com.example.avdemo.ui.record.capture.ActivityAudioRecord

/**
 * desc: 21以上视频采集 <br/>
 * time: 2019/10/24 10:10 <br/>
 * author: 吕昊臻 <br/>
 * since V 1.0 <br/>
 */
@Route(path = ActivityVideoCapture21.Target)
class ActivityVideoCapture21 : AppCompatActivity() {

    companion object {
        const val Target = ViewPathConst.ACTIVITY_VIDEO_CPATURE21
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_capture21)
    }
}
