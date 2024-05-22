package com.readinglips.lipReading

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.readinglips.databinding.ActivityLipReadingDoingBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class LipReadingDoing : AppCompatActivity() {
    private lateinit var binding:ActivityLipReadingDoingBinding
    private lateinit var cameraProvider: ProcessCameraProvider



    private var imageCapture: ImageCapture? = null
    var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var currentRecording: Recording? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null


    private lateinit var cameraExecutor: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLipReadingDoingBinding.inflate(layoutInflater)

        setContentView(binding.root)

        startCamera()

        binding.tvSubtitle.setOnClickListener {
            startRecording()
        }

//        binding.btnLrStop.setOnClickListener {
//
//        }

        binding.imgbtnCamerachange.setOnClickListener {
            if(cameraSelector== CameraSelector.DEFAULT_BACK_CAMERA){
                cameraSelector=CameraSelector.DEFAULT_FRONT_CAMERA
                bindCameraUseCases()

            }
            else{
                cameraSelector=CameraSelector.DEFAULT_BACK_CAMERA
                bindCameraUseCases()

            }

        }

        binding.imgbtnSetting.setOnClickListener {

        }

        //카메라 권한 관련

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, CameraTest.REQUIRED_PERMISSIONS, CameraTest.REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listeners for take photo and video capture buttons
//        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
//        viewBinding.videoCaptureButton.setOnClickListener { captureVideo() }

        cameraExecutor = Executors.newSingleThreadExecutor()


        //binding.tablayout.addTab(binding.tablayout.newTab().setIcon(R.drawable.icon_setting))
        //binding.tablayout.addTab(binding.tablayout.newTab().setIcon(R.drawable.icon_camerachange))
        val intent = Intent(this,LipReading::class.java)

        binding.btnLrStop.setOnClickListener {
            stopRecording()
//            startActivity(intent)
//            finish()
        }

    }

    private fun bindCameraUseCases() {
        // Set up the preview use case to display camera preview
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(binding.camarePreview.surfaceProvider)
            }

        // Select back or front camera
        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll()

            // Bind use cases to camera
            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview)

        } catch(exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun allPermissionsGranted() = CameraTest.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.camarePreview.surfaceProvider)
            }
            val videoCapture = VideoCapture.withOutput(Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build())
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, videoCapture)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun startCamera2() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)



        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.camarePreview.surfaceProvider)
                }

            // Select back camera as a default



            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview)

            } catch(exc: Exception) {
                Log.e(LipReadingDoing.TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))



    }

    fun startRecording2(){
        val outputOptions = FileOutputOptions.Builder(File(filesDir, "video.mp4")).build()

// videoCapture 초기화 코드
        val videoCapture = VideoCapture.withOutput(Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build())

// 권한 확인 코드 (필요한 경우 추가)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
            return
        }

// 녹화 시작 코드
        videoCapture.let { videoCapture ->
            Log.d(TAG, "test")
            currentRecording = videoCapture.output
                .prepareRecording(this, outputOptions)
                .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                    Log.d("TAG", currentRecording.toString())

                    when (recordEvent) {
                        is VideoRecordEvent.Start -> {
                            Log.d(TAG, "녹화가 시작되었습니다.")
                        }
                        is VideoRecordEvent.Finalize -> {
                            if (recordEvent.hasError()) {
                                Log.d(TAG, "녹화 중 오류 발생: ${recordEvent.error}")
                            } else {
                                Log.d(TAG, "녹화가 성공적으로 완료되었습니다.")
                            }
                        }
                    }
                }
        }
    }

//    fun startRecording2() {
//        // 녹화할 파일 생성
//        val outputDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath
//        val videoFile = File(outputDirectory, "video-${System.currentTimeMillis()}.mp4")
//        val outputOptions = FileOutputOptions.Builder(videoFile).build()
//
//        Log.d(TAG, "동영상이 저장될 경로: ${videoFile.absolutePath}")
//        Log.d(TAG, "test")
//        videoCapture?.let { videoCapture ->
//            Log.d(TAG, "test")
//            currentRecording = videoCapture.output
//                .prepareRecording(this, outputOptions)
//                .start(ContextCompat.getMainExecutor(this))
//                { recordEvent ->
//                    Log.d("TAG", currentRecording.toString())
//
//                    when(recordEvent)
//                    {
//
//                        is VideoRecordEvent.Start -> {
//                            Log.d(TAG, "녹화가 시작되었습니다.")
//                        }
//                        is VideoRecordEvent.Finalize -> {
//                            if (recordEvent.hasError()) {
//                                Log.d(TAG, "test오류")
//                            } else {
//                                Log.d(TAG, "test성공")
//
//                            }
//                        }
//                    }
//                }
//        }
//    }




    private fun startRecording() {
        // 권한이 부여되지 않은 경우 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED)

        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_MEDIA_VIDEO
                    ), REQUEST_CODE_PERMISSIONS)
            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ), REQUEST_CODE_PERMISSIONS)
            }
            //return
        }

        // 권한이 부여된 경우 녹화 시작
        startVideoCapture()
    }

    private fun startVideoCapture() {
        val outputOptions = FileOutputOptions.Builder(File(filesDir, "video.mp4")).build()

        // videoCapture 초기화 코드
        val videoCapture = VideoCapture.withOutput(Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build())

        // 녹화 시작 코드
        videoCapture.let { videoCapture ->
            Log.d(TAG, "녹화 시작 준비 완료")
            currentRecording = videoCapture.output
                .prepareRecording(this, outputOptions)
                .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                    Log.d(TAG, currentRecording.toString())

                    when (recordEvent) {
                        is VideoRecordEvent.Start -> {
                            Log.d(TAG, "녹화가 시작되었습니다.")
                        }
                        is VideoRecordEvent.Finalize -> {
                            if (recordEvent.hasError()) {
                                Log.d(TAG, "녹화 중 오류 발생: ${recordEvent.error}")
                            } else {
                                Log.d(TAG, "녹화가 성공적으로 완료되었습니다.")
                            }
                        }
                    }
                }
        }
    }

    fun stopRecording() {
        // 현재 녹화 중인 객체가 있는지 확인
        val recording = currentRecording ?: return


        // 녹화 종료 및 저장
        recording.stop()
        currentRecording = null // 녹화 참조를 null로 설정하여 녹화가 종료되었음을 표시
        Log.d(TAG, "녹화가 끝났습니다.")
    }


    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // 모든 권한이 부여된 경우 녹화 시작
                startRecording()
            } else {
                Log.d(TAG, "필수 권한이 부여되지 않았습니다.")
            }
        }
    }
}