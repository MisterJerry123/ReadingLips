package com.readinglips.pronunciationTest

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.readinglips.R
import com.readinglips.databinding.ActivityLipReadingBinding
import com.readinglips.databinding.ActivityPronunciationTestBinding
import com.readinglips.lipReading.LipReading

class PronunciationTest : AppCompatActivity() {
    private lateinit var binding:ActivityPronunciationTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPronunciationTestBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //binding.tablayout.addTab(binding.tablayout.newTab().setIcon(R.drawable.icon_setting))
        //binding.tablayout.addTab(binding.tablayout.newTab().setIcon(R.drawable.icon_camerachange))
        val intent = Intent(this,PronunciationTestDoing::class.java)

        binding.btnPtStart.setOnClickListener {

            Log.d("PronunciationTest",ContextCompat.checkSelfPermission(this@PronunciationTest.applicationContext,android.Manifest.permission.CAMERA).toString() )
            //먼저 권한을 받았는지 확인
            if(ContextCompat.checkSelfPermission(this@PronunciationTest.applicationContext,android.Manifest.permission.CAMERA
            )==PackageManager.PERMISSION_DENIED){
                requestPermission()
            }
            else if(ContextCompat.checkSelfPermission(this@PronunciationTest.applicationContext,android.Manifest.permission.CAMERA
                )==PackageManager.PERMISSION_GRANTED){
                startActivity(intent)
                finish()
            }
        }

        //TODO 이것도 임시 코드이므로 개발 후 지울 것
        val intent2 = Intent(this,LipReading::class.java)
        binding.imgbtnSetting.setOnClickListener {
            startActivity(intent2)
            finish()
        }
    }



    fun requestPermission(){
        val per1 = ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)

        if(per1== PackageManager.PERMISSION_DENIED){
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.CAMERA,
                ),
                1000 //왜 1000쓰는지는 모르겠네?
            )
        }
    }
}