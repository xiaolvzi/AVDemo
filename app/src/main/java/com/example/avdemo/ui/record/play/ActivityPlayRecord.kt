package com.example.avdemo.ui.record.play

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager.AUDIO_SESSION_ID_GENERATE
import android.media.AudioManager.STREAM_MUSIC
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.AudioTrack.STATE_UNINITIALIZED
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.avdemo.R
import com.example.avdemo.common.ViewPathConst
import com.example.avdemo.ui.record.capture.ActivityAudioRecord
import com.example.avdemo.ui.record.play.adapter.AdapterFileList
import kotlinx.android.synthetic.main.activity_play_record.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.ArrayList

@Route(path = ActivityPlayRecord.Target)
class ActivityPlayRecord : AppCompatActivity() {

    lateinit var audioTrack: AudioTrack
    val audioSample by lazy { Integer.parseInt(et_audio_sample.text.toString()) }

    /**
     * 缓冲区大小：缓冲区字节大小
     */
    private val bufferSizeInBytes by lazy {
        AudioRecord.getMinBufferSize(audioSample, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
    }

    companion object {
        const val Target = ViewPathConst.ACTIVITY_PLAY_RECORD
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_record)
        initAudioTrack()
        initView()
        initListener()
    }

    private fun initAudioTrack() {

        if (SDK_INT >= LOLLIPOP) {
            audioTrack = AudioTrack(
                    AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build(),
                    AudioFormat.Builder()
                            .setSampleRate(audioSample)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build(),
                    bufferSizeInBytes,
                    AudioTrack.MODE_STREAM, AUDIO_SESSION_ID_GENERATE)
        } else {
            audioTrack = AudioTrack(STREAM_MUSIC, audioSample, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes, AudioTrack.MODE_STREAM)
        }
    }

    private fun initView() {
        rv_file_list.layoutManager = LinearLayoutManager(this)
        rv_file_list.adapter = AdapterFileList(this, getFileNameList(),
                object : AdapterFileList.OnItemClickListener {
                    override fun onItemClick(fileName: String) {
                        playAudio(fileName)
                    }
                })
    }

    private fun initListener() {
        fab_stop_play.setOnClickListener { audioTrack.play() }
    }

    override fun onPause() {
        super.onPause()
        releaseAudioTrack()
    }

    private fun getFileNameList(): ArrayList<String> {
        var fileNameList = ArrayList<String>()
        cacheDir.walk()
                .filter { it.isFile }
                .filter { it.extension == "pcm" }
                .forEach {
                    fileNameList.add(it.absolutePath)
                }
        return fileNameList
    }

    private fun playAudio(fileName: String) {
        if (audioTrack.state == STATE_UNINITIALIZED) {
            initAudioTrack()
        }
        val inputStream = File(fileName).inputStream()
        Thread(Runnable { writeData2Buffer(inputStream) }).start()
    }

    private fun writeData2Buffer(inputStream: FileInputStream) {
        val audioData = ByteArray(bufferSizeInBytes)

        while (inputStream.available() > 0) {
            val readCount = inputStream.read(audioData)
            if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                continue
            }
            if (readCount != -1 && readCount != 0) {
                audioTrack.play()
                audioTrack.write(audioData, 0, readCount)
            }
        }
        try {
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun releaseAudioTrack() {
        audioTrack.pause()
        audioTrack.flush()
        audioTrack.stop()
        audioTrack.release()
    }
}
