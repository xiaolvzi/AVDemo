package com.example.avdemo.ui.record.capture

import android.media.AudioRecord
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.avdemo.common.ViewPathConst
import kotlinx.android.synthetic.main.acitivity_audio_record.*
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioFormat.CHANNEL_IN_MONO
import android.media.MediaRecorder.AudioSource.MIC
import java.io.File


/**
 * desc: Audio采集 <br/>
 * time: 2019/10/24 10:10 <br/>
 * author: 吕昊臻 <br/>
 * since V 1.0 <br/>
 */
@Route(path = ActivityAudioRecord.Target)
class ActivityAudioRecord : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val Target = ViewPathConst.ACTIVITY_AUDIO_RECORD
        /**
         * 采样率
         * 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
         * 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
         */
        const val AUDIO_SAMPLE_RATE = 44100
    }

    /**
     * AudioRecord的状态:true->录音ing，false->停止录音
     */
    private var isRecording = false
    /**
     * 缓冲区大小：缓冲区字节大小
     */
    private val bufferSizeInBytes by lazy {
        AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, CHANNEL_IN_MONO, ENCODING_PCM_16BIT)
    }
    private val audioRecord by lazy {
        AudioRecord(MIC, AUDIO_SAMPLE_RATE, CHANNEL_IN_MONO, ENCODING_PCM_16BIT, bufferSizeInBytes)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.avdemo.R.layout.acitivity_audio_record)
        initListener()
    }

    private fun initListener() {
        bt_start_record.setOnClickListener(this)
        bt_stop_record.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            com.example.avdemo.R.id.bt_start_record -> startRecord()
            com.example.avdemo.R.id.bt_stop_record -> stopRecord()
        }
    }

    private fun startRecord() {
        if (!isRecording) {
            audioRecord.startRecording()
            isRecording = true
            Thread(Runnable { writeData2File() }).start()
        }
    }

    private fun stopRecord() {
        if (isRecording) {
            audioRecord.stop()
            isRecording = false
        }
    }

    private fun writeData2File() {
        val file = File(cacheDir, "record_${System.currentTimeMillis()}.pcm")
        val audioData = ByteArray(bufferSizeInBytes)
        while (isRecording) {
            val read = audioRecord.read(audioData, 0, bufferSizeInBytes)
            if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                file.appendBytes(audioData)
            }
        }
    }
}
