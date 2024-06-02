package com.readinglips.login.fountPassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonParser
import com.readinglips.databinding.ActivityChangePwBinding
import com.readinglips.databinding.ActivityInputIdBinding
import com.readinglips.databinding.ActivityInputPwBinding
import com.readinglips.login.MainActivity
import com.withsejong.retrofit.RetrofitClient
import com.withsejong.retrofit.SignupResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePassword : AppCompatActivity() {

    private lateinit var binding: ActivityChangePwBinding
    private val TAG = "CheckEmail"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePwBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val emailSaved = intent.getStringExtra("email")
        binding.etId.setText(emailSaved)
        binding.etId.isEnabled=false



        val intent = Intent(this,MainActivity::class.java)

        binding.btnNext.setOnClickListener {

            if(binding.etPassword.text.toString()==binding.etPassword2.text.toString() && binding.etPassword.length()>0 && binding.etPassword2.length()>0){
                val jsonObject = JSONObject()
                jsonObject.put("email",binding.etId.text.toString())
                jsonObject.put("password",binding.etPassword.text.toString())

                val changePasswordThread = Thread{
                    val response = RetrofitClient.instance.changePw(JsonParser.parseString(jsonObject.toString())).execute()

                    if(response.isSuccessful){
                        runOnUiThread{
                            Toast.makeText(this,"비밀번호 변경 성공!",Toast.LENGTH_SHORT).show()

                        }
                        startActivity(intent)
                        finish()
                    }
                    else{
                        //TODO 통신 문제
                    }
                }
                changePasswordThread.join()
                changePasswordThread.start()

            }
            else{
                if(binding.etPassword2.text.toString() !=binding.etPassword.text.toString()){
                    Toast.makeText(this,"두 비밀번호가 일치하지 않습니다!",Toast.LENGTH_SHORT).show()
                }
                else if(binding.etPassword.length()<=0 || binding.etPassword2.length()<=0){
                    Toast.makeText(this,"바꿀 비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,"기타 오류",Toast.LENGTH_SHORT).show()
                }
            }




        }

    }
}