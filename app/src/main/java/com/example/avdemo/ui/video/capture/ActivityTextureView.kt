package com.example.avdemo.ui.video.capture

import android.graphics.SurfaceTexture
import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.TextureView
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.avdemo.R
import com.example.avdemo.common.ViewPathConst
import kotlinx.android.synthetic.main.activity_texture_view.*
import java.io.File
import java.io.IOException
import java.lang.Exception

@Suppress("DEPRECATION")
@Route(path = ActivityTextureView.Target)
class ActivityTextureView : AppCompatActivity(), TextureView.SurfaceTextureListener,
    Camera.PreviewCallback, View.OnClickListener {

    companion object {
        const val Target = ViewPathConst.ACTIVITY_TEXTURE_VIEW
    }

    private lateinit var camera: Camera
    private var surface: SurfaceTexture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_texture_view)
        tv_camera_preview.surfaceTextureListener = this
        initListener()
    }

    private fun initListener() {
        bt_take_picture.setOnClickListener(this)
    }

    //SurfaceTextureListener--------------------------------start-----------------------------------
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        Log.e("lv", "onSurfaceTextureAvailable")
        this.surface = surface
        startPreview(surface)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        Log.e("lv", "onSurfaceTextureSizeChanged")
        // Ignored, Camera does all the work for us
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        Log.e("lv", "onSurfaceTextureUpdated")
        // Invoked every time there's a new Camera preview frame
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        Log.e("lv", "onSurfaceTextureDestroyed")
        stopPreview()
        return true
    }

    /**
     * 开始预览
     *
     * @param surface 与camera关联的SurfaceTexture
     */
    private fun startPreview(surface: SurfaceTexture?) {
        camera = Camera.open()
        camera.setPreviewCallback(this)
        camera.setDisplayOrientation(90)
        try {
            camera.setPreviewTexture(surface)
            camera.startPreview()
        } catch (ioe: IOException) {
            // Something bad happened
        }
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

    //onPreviewFrame------------------------------------start---------------------------------------
    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {}

    //OnClickListener-----------------------------------start---------------------------------------
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bt_take_picture -> takePicture()//拍照
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
}
