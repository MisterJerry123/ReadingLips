package com.readinglips.lipReading

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.readinglips.R
import com.readinglips.databinding.ActivityLipReadingBinding
import com.readinglips.pronunciationTest.PronunciationTest

class LipReading : AppCompatActivity() {
    private lateinit var binding:ActivityLipReadingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLipReadingBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //binding.tablayout.addTab(binding.tablayout.newTab().setIcon(R.drawable.icon_setting))
        //binding.tablayout.addTab(binding.tablayout.newTab().setIcon(R.drawable.icon_camerachange))
        val intent = Intent(this,LipReadingDoing::class.java)

        binding.btnLrStart.setOnClickListener {

            Log.d("LipReading",ContextCompat.checkSelfPermission(this@LipReading.applicationContext,android.Manifest.permission.CAMERA).toString() )
            //먼저 권한을 받았는지 확인
            if(ContextCompat.checkSelfPermission(this@LipReading.applicationContext,android.Manifest.permission.CAMERA
            )==PackageManager.PERMISSION_DENIED){
                requestPermission()
            }
            else if(ContextCompat.checkSelfPermission(this@LipReading.applicationContext,android.Manifest.permission.CAMERA
                )==PackageManager.PERMISSION_GRANTED){
                startActivity(intent)
                finish()
            }
        }

        //TODO 임시 코드. 나중에 반드시 지울 것
        val intent2 = Intent(this,PronunciationTest::class.java)
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