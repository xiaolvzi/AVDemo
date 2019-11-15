package com.example.avdemo.ui.media

import android.media.MediaExtractor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.avdemo.common.ViewPathConst
import java.io.File
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import kotlinx.android.synthetic.main.activity_media_extractor.*
import java.nio.ByteBuffer
import android.media.MediaCodec
import kotlin.math.abs


@Route(path = ActivityMediaExtractor.Target)
class ActivityMediaExtractor : AppCompatActivity() {

    private lateinit var mediaExtractor: MediaExtractor
    private lateinit var mediaMuxer: MediaMuxer
    private var videoTrackIndex = 0
    private var audioTrackIndex = 0
    private var longFrameRate: Long = 0
    private var intFrameRate: Int = 0

    companion object {
        const val Target = ViewPathConst.ACTIVITY_MEDIA_EXTRACTOR
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.avdemo.R.layout.activity_media_extractor)
        bt_extractor.setOnClickListener { initMediaExtractor() }
        initView()
    }

    private fun initView() {
        tv_status.text="not started"
        filesDir.walk()
            .filter { it.isFile }
            .filter { it.extension == "mp4" }
            .forEach {
                tv_file_name.text = it.absolutePath
            }
    }

    private fun initMediaExtractor() {
        Thread {
            mediaExtractor = MediaExtractor()
            mediaExtractor.setDataSource(File(filesDir, "video.mp4").absolutePath)
            for (i in 0 until mediaExtractor.trackCount) {

                val format = mediaExtractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)

                mime?.let {
                    when {
                        it.startsWith("audio") -> {

                            val allocate = ByteBuffer.allocate(100 * 1024)

                            run {
                                mediaExtractor.selectTrack(i)//选择此音频轨道
                                mediaExtractor.readSampleData(allocate, 0)
                                val firstSampleTime = mediaExtractor.sampleTime
                                mediaExtractor.advance()
                                val secondSampleTime = mediaExtractor.sampleTime
                                longFrameRate = abs(secondSampleTime - firstSampleTime)//时间戳
                                mediaExtractor.unselectTrack(i)
                            }

                            mediaMuxer = MediaMuxer(
                                File(
                                    filesDir,
                                    "audio${System.currentTimeMillis()}.mp4"
                                ).absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
                            )
                            audioTrackIndex = mediaMuxer.addTrack(format)
                            mediaMuxer.start()

                            val info = MediaCodec.BufferInfo()
                            info.presentationTimeUs = 0

                            Log.e("lv", "divide audio media to file + $mime")
                            mediaExtractor.selectTrack(i)
                            val file = File(filesDir, "audio${System.currentTimeMillis()}.aac")
                            while (mediaExtractor.readSampleData(allocate, 0) > 0) {
                                file.appendBytes(ByteArray(allocate.remaining()))

                                info.offset = 0
                                info.size = mediaExtractor.readSampleData(allocate, 0)
                                info.flags = mediaExtractor.sampleFlags
                                info.presentationTimeUs += longFrameRate
                                mediaMuxer.writeSampleData(audioTrackIndex, allocate, info)

                                mediaExtractor.advance()
                            }

                            mediaMuxer.stop()
                            mediaMuxer.release()
                        }

                        it.startsWith("video") -> {
                            mediaExtractor.selectTrack(i)

                            intFrameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE)

                            mediaMuxer =
                                MediaMuxer(
                                    File(
                                        filesDir,
                                        "video${System.currentTimeMillis()}.mp4"
                                    ).absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
                                )
                            videoTrackIndex = mediaMuxer.addTrack(format)
                            mediaMuxer.start()

                            val info = MediaCodec.BufferInfo()
                            info.presentationTimeUs = 0

                            val file = File(filesDir, "video${System.currentTimeMillis()}.h264")
                            val allocate = ByteBuffer.allocate(640 * 360)

                            while (mediaExtractor.readSampleData(allocate, 0) >= 0) {
                                file.appendBytes(ByteArray(allocate.remaining()))


                                info.offset = 0
                                info.size = mediaExtractor.readSampleData(allocate, 0)
                                info.flags = mediaExtractor.sampleFlags
                                info.presentationTimeUs += 1000 * 1000 / intFrameRate
                                mediaMuxer.writeSampleData(videoTrackIndex, allocate, info)

                                mediaExtractor.advance()
                            }

                            mediaMuxer.stop()
                            mediaMuxer.release()
                        }
                    }
                }
            }
            runOnUiThread {tv_status.text="finished"}
        }.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaExtractor.release()
    }
}
