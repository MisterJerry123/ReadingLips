package com.readinglips.login.singup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonParser
import com.readinglips.databinding.ActivityInputIdBinding
import com.readinglips.databinding.ActivityInputPwBinding
import com.withsejong.retrofit.RetrofitClient
import com.withsejong.retrofit.SignupResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InputInformation : AppCompatActivity() {

    private lateinit var binding: ActivityInputPwBinding
    private val TAG = "CheckEmail"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInputPwBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val emailSaved = intent.getStringExtra("email")
        binding.etId.setText(emailSaved)
        binding.etId.isEnabled=false



        val intent = Intent(this,CreatedAccount::class.java)

        binding.btnNext.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("name",binding.etName.text.toString())
            jsonObject.put("email",binding.etId.text.toString())
            jsonObject.put("password",binding.etPassword.text.toString())


            if(binding.etName.length()>0 && binding.etPassword.length()>0){
                val signupThread = Thread{
                    val response = RetrofitClient.instance.signup(JsonParser.parseString(jsonObject.toString())).execute()

                    if(response.isSuccessful){
                        intent.putExtra("name",binding.etName.text.toString())
                        startActivity(intent)
                        finish()
                    }
                    else{

                    }
                }
                signupThread.join()
                signupThread.start()
            }
            else{
                Toast.makeText(this,"비밀번호와 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            }

        }

    }
}