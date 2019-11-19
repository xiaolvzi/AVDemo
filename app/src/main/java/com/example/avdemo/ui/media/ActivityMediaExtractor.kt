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
    private val allocate = ByteBuffer.allocate(100 * 1024)

    companion object {
        const val Target = ViewPathConst.ACTIVITY_MEDIA_EXTRACTOR
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.avdemo.R.layout.activity_media_extractor)
        bt_extractor.setOnClickListener { initMediaExtractor() }
        bt_mux.setOnClickListener { initMuxer() }
        initView()
    }

    private fun initView() {
        tv_status.text = "not started"
        filesDir.walk()
            .filter { it.isFile }
            .filter { it.extension == "mp4" }
            .forEach {
                tv_file_name.text = it.absolutePath
            }
    }

    /**
     * 这个地方流程写得有问题
     * 可参考 https://blog.csdn.net/u010126792/article/details/86510903
     * 来不及 纠正了 流程大致就是这样子，用到的时候再去细究具体问题
     */
    private fun initMuxer() {
        Thread {
            mediaMuxer = MediaMuxer(
                File(filesDir, "muxer.mp4").absolutePath,
                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
            )
            mediaExtractor = MediaExtractor()
            mediaExtractor.setDataSource(File(filesDir, "video.mp4").absolutePath)
            for (i in 0 until mediaExtractor.trackCount) {

                val format = mediaExtractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)

                mime?.let {
                    when {
                        it.startsWith("audio") -> {
                            doMuxerAudio(i, format)
                        }

                        it.startsWith("video") -> {
                            doMuxerVideo(i, format)
                        }
                    }
                }
            }
            mediaMuxer.start()

            writeVideoTrack()

            writeAudioTrack(allocate)

            mediaMuxer.stop()
            mediaMuxer.release()

            runOnUiThread { tv_status.text = "finished" }
        }.start()
    }

    private fun writeAudioTrack(allocate: ByteBuffer) {
        val info = MediaCodec.BufferInfo()
        info.presentationTimeUs = 0

        mediaExtractor.selectTrack(1)
        while (mediaExtractor.readSampleData(allocate, 0) > 0) {

            info.offset = 0
            info.size = mediaExtractor.readSampleData(allocate, 0)
            info.flags = mediaExtractor.sampleFlags
            info.presentationTimeUs += longFrameRate
            mediaMuxer.writeSampleData(audioTrackIndex, allocate, info)

            mediaExtractor.advance()
        }
    }

    private fun writeVideoTrack() {
        val info = MediaCodec.BufferInfo()
        info.presentationTimeUs = 0

        val allocate = ByteBuffer.allocate(640 * 360)
        mediaExtractor.selectTrack(0)
        while (mediaExtractor.readSampleData(allocate, 0) >= 0) {

            info.offset = 0
            info.size = mediaExtractor.readSampleData(allocate, 0)
            info.flags = mediaExtractor.sampleFlags
            info.presentationTimeUs += 1000 * 1000 / intFrameRate
            mediaMuxer.writeSampleData(videoTrackIndex, allocate, info)

            mediaExtractor.advance()
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
                            doAudio(i, format, mime)
                        }

                        it.startsWith("video") -> {
                            doVideo(i, format)
                        }
                    }
                }
            }
            runOnUiThread { tv_status.text = "finished" }
        }.start()

    }

    /**
     * 合成视频
     *
     * @param videoTrack
     * @param format
     */
    private fun doVideo(videoTrack: Int, format: MediaFormat) {
        mediaExtractor.selectTrack(videoTrack)

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

    /**
     * 合成音频
     *
     * @param videoTrack
     * @param format
     * @param mime
     */
    private fun doAudio(videoTrack: Int, format: MediaFormat, mime: String?) {


        run {
            mediaExtractor.selectTrack(videoTrack)//选择此音频轨道
            mediaExtractor.readSampleData(allocate, 0)
            val firstSampleTime = mediaExtractor.sampleTime
            mediaExtractor.advance()
            val secondSampleTime = mediaExtractor.sampleTime
            longFrameRate = abs(secondSampleTime - firstSampleTime)//时间戳
            mediaExtractor.unselectTrack(videoTrack)
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
        mediaExtractor.selectTrack(videoTrack)
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

    /**
     * 合成视频
     *
     * @param videoTrack
     * @param format
     */
    private fun doMuxerVideo(videoTrack: Int, format: MediaFormat) {
        mediaExtractor.selectTrack(videoTrack)
        intFrameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE)
        videoTrackIndex = mediaMuxer.addTrack(format)
    }

    /**
     * 合成音频
     *
     * @param videoTrack
     * @param format
     * @param mime
     */
    private fun doMuxerAudio(videoTrack: Int, format: MediaFormat) {
        val allocate = ByteBuffer.allocate(100 * 1024)

        run {
            mediaExtractor.selectTrack(videoTrack)//选择此音频轨道
            mediaExtractor.readSampleData(allocate, 0)
            val firstSampleTime = mediaExtractor.sampleTime
            mediaExtractor.advance()
            val secondSampleTime = mediaExtractor.sampleTime
            longFrameRate = abs(secondSampleTime - firstSampleTime)//时间戳
            mediaExtractor.unselectTrack(videoTrack)
        }

        audioTrackIndex = mediaMuxer.addTrack(format)

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaExtractor.release()
    }
}
