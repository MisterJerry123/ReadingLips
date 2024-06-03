package com.readinglips.lipReading

import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.PermissionChecker
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.readinglips.R
import com.readinglips.databinding.ActivityLipReadingDoingBinding
import com.readinglips.mypage.LipReadingHistory
import com.readinglips.mypage.PronunciationTestHistory
import java.text.SimpleDateFormat
import java.util.Locale

class CameraCopy:AppCompatActivity() {
    private lateinit var binding: ActivityLipReadingDoingBinding

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var cameraSelector: CameraSelector

    private lateinit var cameraExecutor: ExecutorService

    var isRecording = false

    lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLipReadingDoingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cameraSelectorSharedPreferences = getSharedPreferences("camera", MODE_PRIVATE)
        val editor = cameraSelectorSharedPreferences.edit()
        val savedCameraSelector = cameraSelectorSharedPreferences.getString("cameraSelector", "back")
        cameraSelector = if (savedCameraSelector == "front") {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        val cameraController = LifecycleCameraController(this)

        val drawerbtn = binding.ibtnHamburgerManu
        drawerbtn.setOnClickListener {
            drawer.openDrawer(Gravity.LEFT)
        }
        drawer = binding.dloMenu
        binding.navigationview.setCheckedItem(R.id.menu_lipreading)

        val backActionCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.dloMenu.isDrawerOpen(GravityCompat.START)) {
                    binding.dloMenu.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        }
        this.onBackPressedDispatcher.addCallback(this, backActionCallback)

        val intentPronunciationTest = Intent(this, PronunciationTestNEW::class.java)
        val intentPronunciationTestHistory = Intent(this, PronunciationTestHistory::class.java)
        val intentLipReadingHistory = Intent(this, LipReadingHistory::class.java)

        binding.navigationview.setCheckedItem(R.id.menu_lipreading)
        binding.navigationview.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_pronounce_test -> {
                    startActivity(intentPronunciationTest)
                    finish()
                }
                R.id.menu_pronunciation_history -> {
                    startActivity(intentPronunciationTestHistory)
                    finish()
                }
                R.id.menu_lipreading_history -> {
                    startActivity(intentLipReadingHistory)
                    finish()
                }
            }
            true
        }

        val intentCurrentActivity = Intent(this, CameraCopy::class.java)
        binding.imgbtnCamerachange.setOnClickListener {
            if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                Toast.makeText(this, "카메라 전환완료", Toast.LENGTH_SHORT).show()
                editor.putString("cameraSelector", "back")
                editor.apply()
                startActivity(intentCurrentActivity)
                finish()
            } else {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                Toast.makeText(this, "카메라 전환완료", Toast.LENGTH_SHORT).show()
                editor.putString("cameraSelector", "front")
                editor.apply()
                startActivity(intentCurrentActivity)
                finish()
            }
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnLrStop.setOnClickListener {
            if (!isRecording) {
                captureVideo()
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return
        binding.tvSubtitle.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            return
        }

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        recording = videoCapture.output.prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(
                        this@CameraCopy, Manifest.permission.RECORD_AUDIO
                    ) == PermissionChecker.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.btnLrStop.text = "립리딩 중단"
                        binding.tvSubtitle.visibility = View.VISIBLE
                        binding.ibtnHamburgerManu.visibility = View.INVISIBLE
                        binding.imgbtnCamerachange.visibility = View.INVISIBLE
                        binding.imgbtnSetting.visibility = View.INVISIBLE
                        isRecording = true

                        // 1초 후에 녹화를 중지하는 핸들러 추가
                        Handler(Looper.getMainLooper()).postDelayed({
                            recording?.stop()
                            isRecording = false
                        }, 1000)
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: ${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: ${recordEvent.error}")
                        }
                        binding.btnLrStop.text = "립리딩 시작"
                        binding.tvSubtitle.visibility = View.INVISIBLE
                        binding.ibtnHamburgerManu.visibility = View.VISIBLE
                        binding.imgbtnCamerachange.visibility = View.VISIBLE
                        binding.imgbtnSetting.visibility = View.VISIBLE
                    }
                }
            }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val resolutionSelector = ResolutionSelector.Builder().setResolutionStrategy(
                ResolutionStrategy(Size(244, 244), ResolutionStrategy.FALLBACK_RULE_NONE)
            ).build()

            val preview = Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .build()
                .also {
                    it.setSurfaceProvider(binding.camaraPreview.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val recorder = Recorder.Builder()
                .setQualitySelector(
                    QualitySelector.from(
                        Quality.HIGHEST,
                        FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                    )
                )
                .build()

            videoCapture = VideoCapture.withOutput(recorder)

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, videoCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}
