package com.example.avdemo.ui.record

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.avdemo.R
import com.example.avdemo.common.ViewPathConst

/**
 * desc: Audio采集 <br/>
 * time: 2019/10/24 10:10 <br/>
 * author: 吕昊臻 <br/>
 * since V 1.0 <br/>
 */
@Route(path = ActivityAudioRecord.Target)
class ActivityAudioRecord : AppCompatActivity() {

    companion object{
        const val Target=ViewPathConst.ACTIVITY_AUDIO_RECORD
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_audio_record)
    }
}
