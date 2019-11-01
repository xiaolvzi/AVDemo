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
import android.media.CamcorderProfile
import android.media.CamcorderProfile.QUALITY_HIGH
import android.media.MediaRecorder
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.avdemo.R
import java.io.File


/**
 * desc: 以上视频采集 <br/>
 * time: 2019/10/24 10:10 <br/>
 * author: 吕昊臻 <br/>
 * since V 1.0 <br/>
 */
@Suppress("DEPRECATION")
@Route(path = ActivityVideoCapture.Target)
class ActivityVideoCapture : AppCompatActivity(), TextureView.SurfaceTextureListener, Camera.PreviewCallback, View.OnClickListener {

    private lateinit var camera: Camera
    private var surface: SurfaceTexture? = null
    private var isTakingPicture: Boolean = false
    private var isStopPreview: Boolean = false
    private var isRecording: Boolean = false
    private val mediaRecorder by lazy { MediaRecorder() }

    companion object {
        const val Target = ViewPathConst.ACTIVITY_VIDEO_CPATURE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("lv", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_capture)
        tv_camera_preview.surfaceTextureListener = this
        initCamera()
        initListener()

    }

    private fun initCamera() {
        camera = Camera.open()
        camera.setPreviewCallback(this)
    }

    private fun initListener() {
        bt_take_picture.setOnClickListener(this)
        bt_capture_video.setOnClickListener(this)
    }

    override fun onResume() {
        Log.e("lv", "onResume")
        super.onResume()
        camera.startPreview()
    }

    override fun onPause() {
        Log.e("lv", "onPause")
        super.onPause()
        isStopPreview = true
        camera.stopPreview()
    }

    override fun onDestroy() {
        Log.e("lv", "onDestroy")
        super.onDestroy()
        isStopPreview = true
        camera.stopPreview()
        camera.release()
    }

    //SurfaceTextureListener--------------------------------start-----------------------------------
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        Log.e("lv", "onSurfaceTextureAvailable")
        this.surface = surface
        camera.setDisplayOrientation(90)
        camera.setPreviewTexture(surface)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        Log.e("lv", "onSurfaceTextureSizeChanged")
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        Log.e("lv", "onSurfaceTextureUpdated")
        if (!isTakingPicture && !isStopPreview) {
            camera.startPreview()
        }
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        Log.e("lv", "onSurfaceTextureDestroyed")
        isStopPreview = true
        return false
    }

    //onPreviewFrame------------------------------------start---------------------------------------
    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {

    }

    //OnClickListener-----------------------------------start---------------------------------------
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bt_take_picture -> takePicture()//拍照
            R.id.bt_audio_capture -> captureVideo()//录像
        }
    }

    /**
     * 拍照
     */
    private fun takePicture() {
        Log.e("lv", "takePicture")
        isTakingPicture = true
        camera.takePicture({
            Toast.makeText(this, "拍照", Toast.LENGTH_LONG).show()
        }, { data, camera ->
            data?.let {
                File(cacheDir, "picture_${System.currentTimeMillis()}.raw").writeBytes(data)
                camera.startPreview()
            }
        }, { data, camera ->
            File(cacheDir, "picture${System.currentTimeMillis()}.jpeg").writeBytes(data)
            camera.startPreview()
            isTakingPicture = false
        })
    }

    /**
     * 录像
     */
    private fun captureVideo() {

        if (!isRecording) {
            stopCapture()
        } else {
            startCapture()
        }
    }

    private fun startCapture() {
        isRecording = true
        bt_capture_video.text = getString(R.string.stop_capture)
        camera.unlock()
        with(mediaRecorder) {
            setCamera(camera)
            setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
            setVideoSource(MediaRecorder.VideoSource.CAMERA)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setVideoEncoder(MediaRecorder.VideoEncoder.H263)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setProfile(CamcorderProfile.get(QUALITY_HIGH))
            setOutputFile("${cacheDir.absolutePath} video${System.currentTimeMillis()}.mp4")
//            setPreviewDisplay(this@ActivityVideoCapture.surface.)
            prepare()
            Thread {
                start()
            }.start()
        }
    }

    private fun stopCapture() {
        isRecording = false
        bt_capture_video.text = getString(R.string.video_capture)
        with(mediaRecorder) {
            stop()
            release()
        }

    }

}
