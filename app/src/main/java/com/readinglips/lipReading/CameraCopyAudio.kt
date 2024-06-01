package com.readinglips.lipReading

import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.PermissionChecker
import com.readinglips.R
import com.readinglips.databinding.ActivityLipReadingDoingBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CameraCopyAudio:AppCompatActivity() {
    private lateinit var binding: ActivityLipReadingDoingBinding

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var audioFile : File


    private lateinit var cameraExecutor: ExecutorService

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLipReadingDoingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var cameraController = LifecycleCameraController(this)

        binding.imgbtnCamerachange.setOnClickListener {
            if(cameraSelector==CameraSelector.DEFAULT_FRONT_CAMERA){
                cameraSelector=CameraSelector.DEFAULT_BACK_CAMERA
            }
            else{
                cameraSelector=CameraSelector.DEFAULT_FRONT_CAMERA
            }
        }



        val fileName = "myRecording.3gp"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        audioFile = File(storageDir, fileName)







        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
//        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        binding.tvSubtitle.setOnClickListener {
            val recorder = MediaRecorder(this).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                // 출력 파일 포맷 설정
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                // 오디오 인코더를 설정
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                // 출력 파일 이름 설정
                setOutputFile(audioFile)
                // 초기화 완료
                prepare()
            }
            recorder.start()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        // ImageCapture UseCase에 대한 참조를 가져온다. 만약 초기화되지 않았다면 함수를 종료.
        // UseCase는 이미지 캡처가 설정되기 전에 사진 버튼을 탭하면 null 된다.
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        // MediaStore 콘텐츠 값을 만든다.
        // MediaStore의 표시 이름이 고유하도록 현재 시간을 기준으로 타임스탬프를 사용한다.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        // ContentValues를 사용하여 이미지에 대한 메타데이터 설정
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            // Android Q 이상에서는 RELATIVE_PATH를 사용하여 저장 경로 지정
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        // OutputFileOptions 객체를 생성.
        // 이미지 저장 옵션 설정. MediaStore를 통해 이미지 저장 위치와 메타데이터 지정
        // 이 객체에서 원하는 출력 방법을 지정할 수 있다.
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        // takePicture()를 호출한다. 이미지 캡처 및 저장
        // outputOptions, 실행자, 이미지가 저장될 때 콜백을 전달
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                // 캡처에 실패하지 않으면 사진을 저장하고 완료되었다는 토스트 메시지를 표시한다.
                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    // Implements VideoCapture use case, including start and stop capturing.
    private fun captureVideo() {
        // 현재 VideoCapture 객체의 참조를 확인하거나 초기화되지 않았으면 함수를 종료한다.
        val videoCapture = this.videoCapture ?: return

        // CameraX에서 요청 작업을 완료할 때까지 UI 사용을 중지한다.
        // VideoRecordListener에서 중복 녹화를 방지하기 위해 다시 설정 된다.
        binding.tvSubtitle.isEnabled = false

        val curRecording = recording
        // 진행 중인 활성 녹화 세션이 있으면 중지하고 현재 recording 자원을 해제한다.
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return
        }

        // create and start a new recording session
        // 녹화를 시작하기 위해 비디오 녹화를 위한 파일 이름을 생성한다.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        // 비디오 파일의 메타데이터를 설정한다.
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        // 콘텐츠의 외부 저장 위치를 옵션으로 설정하기 위해 빌더를 만들고 인스턴스를 빌드한다.
        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()


        // 비디오 캡처 출력 옵션을 설정하고 녹화 영상 출력을 위한 세션을 만든다.
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                // 오디오를 사용 설정
                if (PermissionChecker.checkSelfPermission(this@CameraCopyAudio,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                // 새 녹음을 시작하고 리스너를 등록
                when(recordEvent) {
                    // 요청 녹화를 시작하면 텍스트 전환
                    is VideoRecordEvent.Start -> {
                        binding.tvSubtitle.apply {
                            text = getString(R.string.stop_capture)
                            isEnabled = true
                        }
                    }
                    // 녹화가 완료되면 메시지를 등록하고 다시 텍스트 전환
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                        }
                        binding.tvSubtitle.apply {
                            text = getString(R.string.start_capture)
                            isEnabled = true
                        }
                    }
                }
            }
    }

    private fun startCamera() {
        // ProcessCameraProvider 인스턴스를 생성한다.
        // 카메라의 수명 주기를 수명 주기 소유자와 바인딩하는 데 사용된다.
        // CameraX가 수명 주기를 인식하므로 카메라를 열고 닫는 작업이 필요하지 않게 된다.
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // cameraProviderFuture에 리스너를 추가한다.
        // 첫 번째 인수에는 Runnable를 넣는다.
        // 두 번째는 기본 스레드에서 실행되는 Executor를 넣는다.
        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            // 카메라 수명 주기를 애플리케이션 프로세스 내의 LifecycleOwner에 바인딩한다.
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview 객체를 초기화 하고 뷰파인더에서 노출 영역 제공자를 가져온 다음 Preview에서 설정
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.camaraPreview.surfaceProvider)
                }

            // imageCaputre 인스턴스를 빌드
            imageCapture = ImageCapture.Builder().build()


            // VideoCapture UseCase를 생성한다.
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST,
                    FallbackStrategy.higherQualityOrLowerThan(Quality.SD)))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)


            // Select back camera as a default
            // 후면 카메라를 선택하는 객체를 생성
            //val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            //240520 cameraSelector를 밖으로 뺌


            // cameraProvider에 바인딩된 항목이 없도록 한 다음
            // 위에서 생성한 객체들을 cameraProvider에 바인딩
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, videoCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}