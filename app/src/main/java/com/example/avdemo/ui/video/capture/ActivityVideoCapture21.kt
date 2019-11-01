package com.example.avdemo.ui.video.capture

import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.annotation.RequiresApi
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.avdemo.R
import com.example.avdemo.common.ViewPathConst
import kotlinx.android.synthetic.main.activity_video_capture21.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * desc: 21以上视频采集 <br/>
 * time: 2019/10/24 10:10 <br/>
 * author: 吕昊臻 <br/>
 * since V 1.0 <br/>
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@Route(path = ActivityVideoCapture21.Target)
class ActivityVideoCapture21 : AppCompatActivity(), TextureView.SurfaceTextureListener {


    private lateinit var cameraCaptureSessions: CameraCaptureSession
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var cameraId: String
    private lateinit var cameraManager: CameraManager
    private lateinit var imageDimension: Size
    private lateinit var cameraDevice: CameraDevice
    private lateinit var mBackgroundHandler: Handler
    private val mBackgroundThread by lazy { HandlerThread("Camera Background") }

    companion object {
        const val Target = ViewPathConst.ACTIVITY_VIDEO_CPATURE21
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("lv","ActivityVideoCapture21--->onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_capture21)
        tv_camera_preview.surfaceTextureListener = this
        startBackgroundThread()
    }

    override fun onResume() {
        Log.e("lv","ActivityVideoCapture21--->onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.e("lv","ActivityVideoCapture21--->onPause")
        stopBackgroundThread()
        super.onPause()
    }

    private fun startBackgroundThread() {
        mBackgroundThread.start()
        mBackgroundHandler = Handler(mBackgroundThread.looper)
    }

    private fun stopBackgroundThread() {
        mBackgroundThread.quitSafely()
        try {
            mBackgroundThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        Log.e("lv","TextureView--->onSurfaceTextureSizeChanged")

    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        Log.e("lv","TextureView--->onSurfaceTextureUpdated")
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        Log.e("lv","TextureView--->onSurfaceTextureDestroyed")
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        Log.e("lv","TextureView--->onSurfaceTextureAvailable")
        /** 1 */
        openCamera()
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        imageDimension = map!!.getOutputSizes(SurfaceTexture::class.java)[0]
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                Log.e("lv","CameraDevice--->onOpened")
                cameraDevice = camera
                /** 2 */
                createCameraPreview()
            }

            override fun onDisconnected(camera: CameraDevice) {
                Log.e("lv","CameraDevice--->onDisconnected")
                cameraDevice.close()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.e("lv","CameraDevice--->onError")
                cameraDevice.close()
            }
        }, null)
    }

    private fun createCameraPreview() {
        val surfaceTexture = tv_camera_preview.surfaceTexture
        surfaceTexture.setDefaultBufferSize(imageDimension.width, imageDimension.height)
        val surface = Surface(surfaceTexture)
        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder.addTarget(surface)
        cameraDevice.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) {
                Log.e("lv","CameraCaptureSession--->onConfigured")
                cameraCaptureSessions = session
                /** 3 */
                updatePreview()
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                Log.e("lv","CameraCaptureSession--->onConfigureFailed")

            }
        }, null)
    }

    private fun updatePreview() {
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), object :CameraCaptureSession.CaptureCallback(){
            override fun onCaptureStarted(session: CameraCaptureSession, request: CaptureRequest, timestamp: Long, frameNumber: Long) {
                Log.e("lv","CaptureCallback--->onCaptureStarted")
            }

            override fun onCaptureProgressed(session: CameraCaptureSession, request: CaptureRequest, partialResult: CaptureResult) {
                Log.e("lv","CaptureCallback--->onCaptureProgressed")
            }

            override fun onCaptureSequenceAborted(session: CameraCaptureSession, sequenceId: Int) {
                Log.e("lv","CaptureCallback--->onCaptureSequenceAborted")
            }

            override fun onCaptureBufferLost(session: CameraCaptureSession, request: CaptureRequest, target: Surface, frameNumber: Long) {
                Log.e("lv","CaptureCallback--->onCaptureBufferLost")
            }

            override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                Log.e("lv","CaptureCallback--->onCaptureCompleted")
            }

            override fun onCaptureFailed(session: CameraCaptureSession, request: CaptureRequest, failure: CaptureFailure) {
                Log.e("lv","CaptureCallback--->onCaptureFailed")
            }

            override fun onCaptureSequenceCompleted(session: CameraCaptureSession, sequenceId: Int, frameNumber: Long) {
                Log.e("lv","CaptureCallback--->onCaptureSequenceCompleted")
            }
        }, mBackgroundHandler)
    }

}
