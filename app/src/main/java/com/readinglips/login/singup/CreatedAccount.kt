package com.readinglips.login.singup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.readinglips.R
import com.readinglips.databinding.ActivityCreatedAccountBinding
import com.readinglips.lipReading.LipReading
import com.readinglips.login.MainActivity

class CreatedAccount : AppCompatActivity() {

    private lateinit var binding:ActivityCreatedAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatedAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")

        binding.greeting.text = "환영합니다 ${name}님!\n입술로 모든 것을 읽는 리딥립스에 오신 것을 환영합니다!"

        val intent = Intent(this,MainActivity::class.java)
        binding.btnStart.setOnClickListener {
            startActivity(intent)
            finish()
        }

    }
}