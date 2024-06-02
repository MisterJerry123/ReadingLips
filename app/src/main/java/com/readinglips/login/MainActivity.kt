package com.readinglips.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonParser
import com.readinglips.databinding.ActivityMainBinding
import com.readinglips.lipReading.CameraCopy
import com.readinglips.login.fountPassword.ChangePassword
import com.readinglips.login.fountPassword.FindPassword
import com.readinglips.login.singup.CheckEmail
import com.withsejong.retrofit.LoginResponse
import com.withsejong.retrofit.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val intentCreateAccount = Intent(this, CheckEmail::class.java)
        val intentLogin = Intent(this, CameraCopy::class.java)
        val intentForgotPw = Intent(this, FindPassword::class.java)

        binding.btnSignup.setOnClickListener {
            startActivity(intentCreateAccount)
            finish()
        }
        binding.tvLostPw.setOnClickListener {
            startActivity(intentForgotPw)
            finish()
        }
        binding.btnLogin.setOnClickListener {

            val jsonObject = JSONObject()
            jsonObject.put("email", binding.etId.text.toString())
            jsonObject.put("password", binding.etPassword.text.toString())

            RetrofitClient.instance.login(JsonParser.parseString(jsonObject.toString())).enqueue(
                object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            val tokenSharedPreferences = getSharedPreferences("token", MODE_PRIVATE)
                            val tokenEditor = tokenSharedPreferences.edit()
                            //토큰들 쉐어드프리퍼런스에 저장
                            tokenEditor.putString(
                                "grantType",
                                response.body()?.authToken?.grantType.toString()
                            )
                            tokenEditor.putString(
                                "accessToken",
                                response.body()?.authToken?.accessToken.toString()
                            )
                            tokenEditor.putString(
                                "refreshToken",
                                response.body()?.authToken?.refreshToken.toString()
                            )
                            tokenEditor.apply()
                            startActivity(intentLogin)
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "아이디 또는 비밀번호를 확인해주세요!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Log.d("${TAG}_TAG", response.toString())
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.d("${TAG}_TAG", t.toString())
                    }

                }
            )

        }

    }
}