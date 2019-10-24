package com.example.avdemo.ui.activity

import androidx.appcompat.app.AppCompatActivity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.example.avdemo.R
import com.example.avdemo.common.PermissionConst
import com.example.avdemo.ui.record.ActivityAudioRecord

import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

import com.example.avdemo.common.ViewPathConst.Companion.ACTIVITY_MAIN
import kotlinx.android.synthetic.main.activity_main.*

/**
 * desc: App入口 <br></br>
 * time: 2019/10/24 10:10 <br></br>
 * author: 吕昊臻 <br></br>
 * since V 1.0 <br></br>
 */
@Route(path = ActivityMain.Target)
class ActivityMain : AppCompatActivity(), View.OnClickListener {

    companion object {
       const val Target = ACTIVITY_MAIN
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListener()
    }

    private fun initListener() {
        bt_audio_capture.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            //音频采集
            R.id.bt_audio_capture -> checkAudioPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(PermissionConst.PERMISSION_AUDIO)
    private fun checkAudioPermission() {
        val perms = arrayOf(Manifest.permission.RECORD_AUDIO)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            // Already have permission, do the thing
//            ARouter.getInstance().build(ActivityAudioRecord.Target).navigation()
            startActivity(Intent(this,ActivityAudioRecord::class.java))
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "record permission",
                    PermissionConst.PERMISSION_AUDIO, *perms)
        }
    }

}
