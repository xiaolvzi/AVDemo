package com.example.avdemo.ui.video.capture

import android.graphics.SurfaceTexture
import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.TextureView
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.avdemo.common.ViewPathConst
import kotlinx.android.synthetic.main.activity_video_capture.*
import java.io.IOException
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


/**
 * desc: 以上视频采集 <br/>
 * time: 2019/10/24 10:10 <br/>
 * author: 吕昊臻 <br/>
 * since V 1.0 <br/>
 */
@Suppress("DEPRECATION")
@Route(path = ActivityVideoCapture.Target)
class ActivityVideoCapture : AppCompatActivity(), TextureView.SurfaceTextureListener {
    private var previewIsRunning: Boolean = false
    private val camera: Camera by lazy { Camera.open() }

    companion object {
        const val Target = ViewPathConst.ACTIVITY_VIDEO_CPATURE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.avdemo.R.layout.activity_video_capture)
        tv_camera_preview.surfaceTextureListener = this

    }

    override fun onResume() {
        super.onResume()
        startPreview()
    }

    override fun onPause() {
        super.onPause()
        stopPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.stopPreview()
        camera.release()
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        camera.setDisplayOrientation(90)
        camera.setPreviewTexture(surface)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        try {
            startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        stopPreview()
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    private fun startPreview() {
        if (!previewIsRunning) {
            camera.startPreview()
            previewIsRunning = true
        }
    }

    private fun stopPreview() {
        if (previewIsRunning ) {
            camera.stopPreview()
            previewIsRunning = false
        }
    }

}
