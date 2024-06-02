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
import android.view.Gravity
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.camera.core.ImageCaptureException
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
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.readinglips.R
import com.readinglips.databinding.ActivityPronunciationTestDoingNewBinding
import com.readinglips.mypage.LipReadingHistory
import com.readinglips.mypage.PronunciationTestHistory
import com.withsejong.retrofit.RetrofitClient
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class PronunciationTestNEW:AppCompatActivity() {
    private lateinit var binding: ActivityPronunciationTestDoingNewBinding

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var cameraSelector:CameraSelector
    private lateinit var cameraExecutor: ExecutorService

    lateinit var drawer : DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPronunciationTestDoingNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cameraSelectorSharedPreferences =getSharedPreferences("camera", MODE_PRIVATE)
        val editor = cameraSelectorSharedPreferences.edit()
        var savedCameraSelector = cameraSelectorSharedPreferences.getString("cameraSelector","back")
        if(savedCameraSelector=="front"){
            cameraSelector= CameraSelector.DEFAULT_FRONT_CAMERA
        }
        else{
            cameraSelector= CameraSelector.DEFAULT_BACK_CAMERA
        }

        Log.d("PronunciationTESTNEW_TAG1", binding.tvSubtitle.text.toString())
        loadScript() //초기 자막 설정
        Log.d("PronunciationTESTNEW_TAG2", binding.tvSubtitle.text.toString())



        var cameraController = LifecycleCameraController(this)

        //setSupportActionBar(binding.toolbar)
        //supportActionBar?.setDisplayShowTitleEnabled(false) //앱 이름 안나오게 하는 코드 추가

        val drawerbtn = binding.ibtnHamburgerManu
        drawerbtn.setOnClickListener {
            drawer.openDrawer(Gravity.LEFT)
        }
        drawer = binding.dloMenu
        //binding.toolbar.hideOverflowMenu()
        binding.navigationview.setCheckedItem(R.id.menu_lipreading)


        //drawer가 켜져을 때 뒤로가기 누를 경우 drawer끔
        val backActionCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (binding.dloMenu.isDrawerOpen(GravityCompat.START)) {
                    binding.dloMenu.closeDrawer(GravityCompat.START);
                }
                else{
                    //TODO 추후에 시간나면 2번 클릭시 종료합니다로 변경
                    finish()
                }
            }
        }
        this.onBackPressedDispatcher.addCallback(this,backActionCallback)
        val intentLipReading = Intent(this,CameraCopy::class.java)
        val intentPronunciationTestHistory = Intent(this,PronunciationTestHistory::class.java)
        val intentLipReadingHistory = Intent(this,LipReadingHistory::class.java)



        binding.navigationview.setCheckedItem(R.id.menu_lipreading)
        binding.navigationview.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menu_lipreading->{
                    startActivity(intentLipReading)
                    finish()
                }
                R.id.menu_pronunciation_history->{
                    startActivity(intentPronunciationTestHistory)
                    finish()
                }
                R.id.menu_lipreading_history->{
                    startActivity(intentLipReadingHistory)
                    finish()
                }
            }
            true
        }

        val intentCurrentActivity = Intent(this, PronunciationTestNEW::class.java)
        binding.imgbtnCamerachange.setOnClickListener {
            if(cameraSelector==CameraSelector.DEFAULT_FRONT_CAMERA){
                cameraSelector=CameraSelector.DEFAULT_BACK_CAMERA
                Toast.makeText(this,"카메라 전환완료",Toast.LENGTH_SHORT).show()
                editor.putString("cameraSelector", "back")
                editor.apply()
                startActivity(intentCurrentActivity)
                finish()
            }
            else{
                cameraSelector=CameraSelector.DEFAULT_FRONT_CAMERA
                Toast.makeText(this,"카메라 전환완료",Toast.LENGTH_SHORT).show()

                editor.putString("cameraSelector", "front")
                editor.apply()
                startActivity(intentCurrentActivity)
                finish()
            }
        }
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
//        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        //binding.tvSubtitle.setOnClickListener { captureVideo() }
        binding.btnLrStop.setOnClickListener {
            if(isRecording==false){
                captureVideo()
                //binding.btnLrStop.text = "립리딩 중단"
            }

        }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        //return super.onCreateOptionsMenu(menu)
//        val inflater = menuInflater
//        inflater.inflate(R.menu.drawable_layout,menu)
//
//
//        return true
//    }
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
                if (PermissionChecker.checkSelfPermission(this@PronunciationTestNEW,
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
//                        binding.tvSubtitle.apply {
//                            text = getString(R.string.stop_capture)
//                            isEnabled = true
//                        }
                        binding.btnLrStop.text="발음테스트 중단"
                        binding.tvSubtitle.visibility = View.VISIBLE

                        //버튼들 안보이게 해서 비활성화
                        binding.ibtnHamburgerManu.visibility = View.INVISIBLE
                        binding.imgbtnCamerachange.visibility = View.INVISIBLE
                        binding.imgbtnSetting.visibility = View.INVISIBLE

                    }
                    // 녹화가 완료되면 메시지를 등록하고 다시 텍스트 전환
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"//동영상 저장경로
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            //Log.d(TAG, msg)

                            val returnFilePath = changeResolution(getRealPathFromUri(recordEvent.outputResults.outputUri).toString())
                            Log.d("PronunciationTestNEW_TAG_new",returnFilePath.toString())


                            val videoBytes = convertVideoToBytes(returnFilePath.toString())
                            //val videoBytes = convertVideoToBytes(getRealPathFromUri(recordEvent.outputResults.outputUri).toString())
                            Log.d("PronunciationTestNEW_TAG_old",getRealPathFromUri(recordEvent.outputResults.outputUri).toString())

                            Log.d("PronunciationTESTNEW_TAG_videoBytes", recordEvent.outputResults.outputUri.toString())

                            Log.d("PronunciationTESTNEW_TAG_", recordEvent.outputResults.outputUri.toString())
                            //Log.d("PronunciationTESTNEW_TAG_", logByteArray(videoBytes!!,"PronunciationTESTNEW_TAG_").toString())
                            Log.d("PronunciationTESTNEW_TAG_", videoBytes.toString())


                            val videoBytesBase64 = Base64.encodeToString(videoBytes, Base64.DEFAULT)
                            Log.d("PronunciationTestNEW_TAG",videoBytesBase64)
//TODO 통신코드 임시 비활성화

                            //json만들기
                            val jsonObject = JSONObject()
                            jsonObject.put("audio", videoBytesBase64)
                            jsonObject.put("originalText", binding.tvSubtitle.text.toString())
                            //TODO 이거 바꾸셈 이메일 계정들의 아이디로
                            jsonObject.put("userEmail", "misterjerry12345@gmail.com")

                            val pronunciationTestThread = Thread{

                                Log.d("PronunciationTestNEW_TAG",jsonObject.toString())
                                val response = RetrofitClient.instance.uploadPronunciationTestVideo(JsonParser.parseString(jsonObject.toString())).execute()
                                if(response.isSuccessful){
                                    Log.d("PronunciationTestNEW_TAG",response.body()?.accuracy.toString())
                                }
                                Log.d("PronunciationTestNEW_TAG",response.toString())
                                Log.d("PronunciationTestNEW_TAG",response.body().toString())
                            }
                            pronunciationTestThread.join()
                            pronunciationTestThread.start()
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                        }
//                        binding.tvSubtitle.apply {
//                            text = getString(R.string.start_capture)
//                            isEnabled = true
//                        }
                        binding.btnLrStop.text="발음테스트 시작"
                        //binding.tvSubtitle.visibility = View.INVISIBLE
                        //버튼들 안보이게 해서 비활성화
                        binding.ibtnHamburgerManu.visibility = View.VISIBLE
                        binding.imgbtnCamerachange.visibility = View.VISIBLE
                        binding.imgbtnSetting.visibility = View.VISIBLE
                        loadScript() //중단 이후 새로운 자막 불러오기
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

    fun loadScript(){
        val scriptLoadingThread = Thread{
            val response = RetrofitClient.instance.loadText().execute()

            if(response.isSuccessful){

                runOnUiThread{
                    binding.tvSubtitle.text=response.body()?.originalText.toString()

                }
                Log.d("PronunciationTestNew_TAG", response.body()?.originalText.toString())
            }
        }
        scriptLoadingThread.start()
        scriptLoadingThread.join()
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

    private fun getRealPathFromUri(uri: Uri): String? {
        val contentResolver: ContentResolver = contentResolver
        val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()

        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return filePath
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





    fun logByteArray(byteArray: ByteArray, tag: String) {
        val previewSize = 1000  // 너무 많은 데이터를 출력하지 않도록 제한
        val preview = byteArray.take(previewSize).joinToString(", ")
        //Log.d(tag, "Video bytes preview: $preview")

        // 전체 바이트 배열을 로그로 출력 (주의: 매우 큰 데이터일 수 있음)
        //Log.d(tag, "Video bytes: ${byteArray.joinToString(", ")}")
    }
    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private var isRecording:Boolean = false

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