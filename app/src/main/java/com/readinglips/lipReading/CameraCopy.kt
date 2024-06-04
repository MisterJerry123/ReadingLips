package com.readinglips.lipReading

import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
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
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.PermissionChecker
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.arthenica.mobileffmpeg.FFmpeg
import com.google.gson.JsonParser
import com.readinglips.R
import com.readinglips.changeInfo.ChangePassword
import com.readinglips.credits.Credits
import com.readinglips.databinding.ActivityLipReadingDoingBinding
import com.readinglips.mypage.LipReadingHistory
import com.readinglips.mypage.PronunciationTestHistory
import com.withsejong.retrofit.RetrofitClient
import com.withsejong.retrofit.UploadSubtitleResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class CameraCopy: AppCompatActivity() {
    private lateinit var binding: ActivityLipReadingDoingBinding
    var subtitleConcat:String = ""
    var isRecording=false
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var cameraSelector: CameraSelector

    private lateinit var cameraExecutor: ExecutorService

    lateinit var drawer: DrawerLayout

    var isactivated = false

    private var videoCount = 0
    private val maxVideos = 5
    private val handler = Handler(Looper.getMainLooper())

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
        val intentDevelops = Intent(this, Credits::class.java)
        val intentChangePassword = Intent(this, ChangePassword::class.java)



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
                R.id.menu_introducing->{
                    startActivity(intentDevelops)
                    finish()
                }
                R.id.menu_change_password->{
                    startActivity(intentChangePassword)
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
        val functionDialog = ExperimentalFunctionFragment()
        binding.imgbtnSetting.setOnClickListener {
            functionDialog.show(supportFragmentManager, functionDialog.tag)

        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        var isclicked=false


        binding.btnLrStop.setOnClickListener {

            binding.btnLrStop.text = "립리딩 중단"
            binding.tvSubtitle.visibility = View.VISIBLE
            binding.ibtnHamburgerManu.visibility = View.INVISIBLE
            binding.imgbtnCamerachange.visibility = View.INVISIBLE
            binding.imgbtnSetting.visibility = View.INVISIBLE
//runBlocking { delay(1000) }
            CoroutineScope(Dispatchers.Main).launch {
                while(!isclicked){
                    binding.btnLrStop.setOnClickListener {
                        isclicked=true
                    }

                        //TODO captureVideo함수에 서버로 동영상파일 보내는 코드 추가하고 response 자막tv에 표시하고 결과 string에 추가하는 로직 추가
                        captureVideo()
                        delay(3000)
                        Log.d("CameraCopy_TAG_capture","동영상저장됨" )

                // 녹화 중단부터 시작까지의 딜레이

                }
//                binding.btnLrStop.text = "립리딩 시작"
//                binding.tvSubtitle.visibility = View.INVISIBLE
//                binding.ibtnHamburgerManu.visibility = View.VISIBLE
//                binding.imgbtnCamerachange.visibility = View.VISIBLE
//                binding.imgbtnSetting.visibility = View.VISIBLE
                //TODO 발음테스트 결과 String 서버로 전송

                val lipReadingResultThread = Thread{
                    val jsonObject = JSONObject()
                    jsonObject.put("subtitle", subtitleConcat)
                    jsonObject.put("userEmail","misterjerry12345@gmail.com")

                   RetrofitClient.instance2.uploadSubtitle(JsonParser.parseString(jsonObject.toString())).execute()

                }
                lipReadingResultThread.join()
                lipReadingResultThread.start()
                val intentRestart = Intent(this@CameraCopy,CameraCopy::class.java)
                startActivity(intentRestart)
                finish()



            }





            //            CoroutineScope(Dispatchers.Main).launch {
//                captureVideo()
//                delay(1000)
//            }
//            CoroutineScope(Dispatchers.Main).launch {
//                delay(1000)
//
//            }




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

                        isRecording = true

                        // 1초 후에 녹화를 중지하는 핸들러 추가

                        recording?.stop()

//                        CoroutineScope(Dispatchers.Main). {
//
//                        }

//                        Log.d("CameraCopy_TAG_capture", "stop전")
//                        handler.postDelayed({
//                        }, 3000)
//                        Log.d("CameraCopy_TAG_capture", "stop후")



                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg =
                                "Video capture succeeded: ${recordEvent.outputResults.outputUri}"


//
                            val returnFilePath = changeResolution(getRealPathFromUri(recordEvent.outputResults.outputUri).toString())
                            Log.d("PronunciationTestNEW_TAG_new",returnFilePath.toString())

                            val videoBytes = convertVideoToBytes(returnFilePath.toString())
                            //val videoBytes = convertVideoToBytes(getRealPathFromUri(recordEvent.outputResults.outputUri).toString())
                            Log.d("PronunciationTestNEW_TAG_old",getRealPathFromUri(recordEvent.outputResults.outputUri).toString())

                            Log.d("PronunciationTESTNEW_TAG_videoBytes", recordEvent.outputResults.outputUri.toString())

                            Log.d("PronunciationTESTNEW_TAG_", recordEvent.outputResults.outputUri.toString())
                            //Log.d("PronunciationTESTNEW_TAG_", logByteArray(videoBytes!!,"PronunciationTESTNEW_TAG_").toString())
                            Log.d("PronunciationTESTNEW_TAG_", videoBytes.toString())



                                RetrofitClient.instance2.uploadSubtitle(

                                    JsonParser.parseString(
                                        JSONObject().put("audio",Base64.encodeToString(videoBytes, Base64.DEFAULT)).toString()
                                    )
                                )
                                    .enqueue(object : retrofit2.Callback<UploadSubtitleResponse> {
                                        override fun onResponse(
                                            call: Call<UploadSubtitleResponse>,
                                            response: Response<UploadSubtitleResponse>
                                        ) {
                                            if (response.isSuccessful) {
                                                subtitleConcat += response.body()?.subtitle
                                                binding.tvSubtitle.text = subtitleConcat
                                                Log.d("CameraCopy_TAG", "서버로 전송됨")

                                            } else {
                                                Log.d("CameraCopy_TAG", response.toString())
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<UploadSubtitleResponse>,
                                            t: Throwable
                                        ) {
                                            Log.d("CameraCopy_TAG", t.toString())
                                        }

                                    })






                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: ${recordEvent.error}")
                        }
                        isRecording = false
                        videoCount++
                        Log.d("CameraCopy_TAG", videoCount.toString())

                        //captureVideo()
//
//                        binding.btnLrStop.text = "립리딩 시작"
//                        binding.tvSubtitle.visibility = View.INVISIBLE
//                        binding.ibtnHamburgerManu.visibility = View.VISIBLE
//                        binding.imgbtnCamerachange.visibility = View.VISIBLE
//                        binding.imgbtnSetting.visibility = View.VISIBLE

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
    private fun changeResolution(filePath:String) : Uri{ //TODO 해상도 변경 함수

        val inputVideoPath = filePath

        Log.d("PronunciationTestNEW_TAG_changeResolution", inputVideoPath.toString())
        var outputVideoPath = inputVideoPath.substring(0, inputVideoPath.length - 4)+"after.mp4"
        Log.d("PronunciationTestNEW_TAG_changeResolution",outputVideoPath.toString())

// FFmpeg을 사용하여 해상도를 1x1로 변경
        val cmd = "-i $inputVideoPath -vf scale=1:1 $outputVideoPath"
        val executionId = FFmpeg.execute(cmd)

        if (executionId != 0) {
            Log.e("FFmpeg", "비디오 해상도 조정 실패")
        } else {
            Log.d("FFmpeg", "비디오 해상도 조정 성공")
        }
        return outputVideoPath.toUri()
    }
    fun getRealPathFromUri(uri: Uri): String? {
        val contentResolver: ContentResolver = contentResolver
        val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()

        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return filePath
    }
    private fun convertVideoToBytes(filePath: String): ByteArray? {

        val file = File(filePath)
        val byteArrayOutputStream = ByteArrayOutputStream()

        return try {
            val inputStream = FileInputStream(file)
            val buffer = ByteArray(1024)
            var length: Int

            while (inputStream.read(buffer).also { length = it } != -1) {
                byteArrayOutputStream.write(buffer, 0, length)
            }

            byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
//        finally {
//            try {
//                inputStream.close()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
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
