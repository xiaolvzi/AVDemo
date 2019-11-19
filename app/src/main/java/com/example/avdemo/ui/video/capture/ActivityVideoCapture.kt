package com.example.avdemo.ui.video.capture

import android.graphics.SurfaceTexture
import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.avdemo.common.ViewPathConst
import kotlinx.android.synthetic.main.activity_video_capture.*
import android.media.CamcorderProfile
import android.media.CamcorderProfile.QUALITY_HIGH
import android.media.MediaRecorder
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import com.example.avdemo.R
import java.io.File
import java.lang.Exception


/**
 * desc: 视频采集 <br/>
 * time: 2019/10/24 10:10 <br/>
 * author: 吕昊臻 <br/>
 * since V 1.0 <br/>
 */
@Route(path = ActivityVideoCapture.Target)
class ActivityVideoCapture : AppCompatActivity(), SurfaceHolder.Callback, View.OnClickListener {

    private lateinit var camera: Camera
    private var surface: SurfaceTexture? = null
    private var isRecording: Boolean = false
    private val mediaRecorder by lazy { MediaRecorder() }

    companion object {
        const val Target = ViewPathConst.ACTIVITY_VIDEO_CPATURE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_capture)
        tv_camera_preview.holder.addCallback(this)
        initListener()
    }

    private fun initListener() {
        bt_take_picture.setOnClickListener(this)
        bt_capture_video.setOnClickListener(this)
    }

    //SurfaceHolder.Callback--------------------------------start-----------------------------------
    override fun surfaceCreated(holder: SurfaceHolder?) {
        startPreview(surface)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        stopPreview()
    }

    /**
     * 开始预览
     *
     * @param surface 与camera关联的SurfaceTexture
     */
    private fun startPreview(surface: SurfaceTexture?) {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
        camera.setDisplayOrientation(90)
        camera.setPreviewTexture(surface)
        camera.startPreview()
    }

    /**
     * 停止预览
     */
    private fun stopPreview() {
        try {
            camera.stopPreview()
            camera.release()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    //OnClickListener-----------------------------------start---------------------------------------
    override fun onClick(v: View?) {
        when (v?.id) {
            com.example.avdemo.R.id.bt_take_picture -> takePicture()//拍照
            com.example.avdemo.R.id.bt_audio_capture -> captureVideo()//录像
        }
    }

    /**
     * 拍照
     */
    private fun takePicture() {
        Log.e("lv", "takePicture")
        camera.takePicture({
            Toast.makeText(this, "拍照", Toast.LENGTH_LONG).show()
        }, { data, camera ->
            data?.let {
                File(cacheDir, "picture_${System.currentTimeMillis()}.raw").writeBytes(data)
            }
        }, { data, camera ->
            File(cacheDir, "picture${System.currentTimeMillis()}.jpeg").writeBytes(data)
            stopPreview()
            startPreview(surface)
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
        bt_capture_video.text = getString(com.example.avdemo.R.string.stop_capture)
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
        bt_capture_video.text = getString(com.example.avdemo.R.string.video_capture)
        with(mediaRecorder) {
            stop()
            release()
        }

    }

}
