package com.readinglips.lipReading

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoOutput
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import com.readinglips.databinding.ActivityLipReadingDoingBinding
import java.io.File

class LipReadingDoing_old : AppCompatActivity() {
//    private lateinit var binding: ActivityLipReadingDoingBinding
//    val TAG = "LipReadingDoing"
//    private lateinit var videoCapture: VideoCapture<Recorder>
//    private var currentRecording: VideoCapture<Recorder>? = null
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//
//            val recorder = Recorder.Builder()
//                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
//                .build()
//            videoCapture = VideoCapture.withOutput(recorder)
//
//            try {
//                cameraProvider.unbindAll()
//
//                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//                cameraProvider.bindToLifecycle(this, cameraSelector, videoCapture)
//            } catch (exc: Exception) {
//                Log.e(TAG, "Use case binding failed", exc)
//            }
//        }, ContextCompat.getMainExecutor(this))
//    }
//    private fun startRecording() {
//        val videoFile = createVideoFile() // 영상을 저장할 파일 생성
//        val outputFileOptions = VideoOutput.FileOutputOptions.Builder(videoFile).build()
//
//        videoCapture.output.prepareRecording(this, outputFileOptions)
//            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
//                when (recordEvent) {
//                    is VideoRecordEvent.Start -> {
//                        // 녹화 시작 처리
//                    }
//                    is VideoRecordEvent.Finalize -> {
//                        if (!recordEvent.hasError()) {
//                            // 녹화 성공 처리
//                        } else {
//                            // 녹화 실패 처리
//                        }
//                    }
//                }
//            }
//    }
//    private fun stopRecording() {
//        currentRecording?.stop()
//        currentRecording = null
//    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityLipReadingDoingBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        startCamera()
//        binding.tvSubtitle.setOnClickListener {
//            startRecording()
//
//        }
//        binding.btnLrStop.setOnClickListener {
//            stopRecording()
//        }
//
//
//    }
//    private fun startCamera() {
//        //val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFuture.addListener({
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(binding.camarePreview.surfaceProvider)
//                }
//            val recorder = Recorder.Builder()
//                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
//                .build()
//            videoCapture = VideoCapture.withOutput(recorder)
//
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, videoCapture)
//            } catch(exc: Exception) {
//                Log.e("LipReadingDoing", "Use case binding failed", exc)
//            }
//
//        }, ContextCompat.getMainExecutor(this))
//    }
//    private fun startRecording() {
//        val videoFile = createVideoFile()
//        val outputFileOptions = FileOutputOptions.Builder(videoFile).build()
//        videoCapture.output.prepareRecording(this, outputFileOptions)
//            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
//                when(recordEvent) {
//                    is VideoRecordEvent.Start -> {
//                        // 녹화 시작
//                    }
//                    is VideoRecordEvent.Finalize -> {
//                        if (!recordEvent.hasError()) {
//                            val msg = "Video 저장 위치: ${videoFile.absolutePath}"
//                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//                        } else {
//                            // 녹화 중 오류 발생
//                        }
//                    }
//                }
//
//            }
//    }
//
//    private fun stopRecording() {
//        videoCapture.stopRecording()
//    }
//
//    private fun createVideoFile(): File {
//        val outputDirectory = getOutputDirectory()
//        val fileName = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
//            .format(System.currentTimeMillis()) + ".mp4"
//        return File(outputDirectory, fileName)
//    }
//
//    // 저장 위치를 위한 함수
//    private fun getOutputDirectory(): File {
//        val mediaDir = externalMediaDirs.firstOrNull()?.let {
//            File(it, "ReadingLips").apply { mkdirs() }
//        }
//        return if (mediaDir != null && mediaDir.exists())
//            mediaDir else filesDir
//    }
}




