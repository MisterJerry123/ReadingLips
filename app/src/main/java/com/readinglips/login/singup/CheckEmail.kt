package com.readinglips.login.singup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.JsonParser
import com.readinglips.databinding.ActivityInputIdBinding
import com.withsejong.retrofit.RetrofitClient
import com.withsejong.retrofit.SignupResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckEmail : AppCompatActivity() {

    private lateinit var binding: ActivityInputIdBinding
    private val TAG = "CheckEmail"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInputIdBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val intent = Intent(this,InputInformation::class.java)

        binding.btnNext.setOnClickListener {
            if(binding.btnNext.text == "인증번호 받기") {


                if (binding.etId.length() > 0) {
                    val sendCodeThread = Thread {
                        val response =
                            RetrofitClient.instance.sendEmailCode(binding.etId.text.toString(), 0)
                                .execute()

                        if (response.isSuccessful) {

                            runOnUiThread {
                                binding.etCode.visibility = View.VISIBLE
                                binding.tvCode.visibility = View.VISIBLE
                                binding.btnNext.text = "이메일 인증하기"
                                binding.etId.isEnabled = false
                            }

                        }

                    }
                    sendCodeThread.join()
                    sendCodeThread.start()

                } else {
                    Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            else if(binding.btnNext.text == "이메일 인증하기"){

                if(binding.etCode.length()==6){

                    val jsonObject = JSONObject()
                    jsonObject.put("email", binding.etId.text.toString())
                    jsonObject.put("code", binding.etCode.text.toString())
                    val codeThread = Thread{
                        val response = RetrofitClient.instance.checkEmailCode(JsonParser.parseString(jsonObject.toString())).execute()
                        if(response.body()==true){
                            intent.putExtra("email", binding.etId.text.toString())

                            runOnUiThread {
                                startActivity(intent)
                                finish()

                            }
                        }
                    }
                    codeThread.join()
                    codeThread.start()
                }

            }






        }

    }
}