package com.example.avdemo.ui.media

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.avdemo.R
import com.example.avdemo.common.ViewPathConst

@Route(path = ActivityMediaMuxer.Target)
class ActivityMediaMuxer : AppCompatActivity() {

    companion object {
        const val Target = ViewPathConst.ACTIVITY_MEDIA_MUXER
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_muxer)
    }
}
