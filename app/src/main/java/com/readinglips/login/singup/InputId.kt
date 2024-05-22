package com.readinglips.login.singup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.JsonParser
import com.readinglips.R
import com.readinglips.databinding.ActivityInputIdBinding
import com.withsejong.retrofit.RetrofitClient
import com.withsejong.retrofit.SignupResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InputId : AppCompatActivity() {

    private lateinit var binding: ActivityInputIdBinding
    private val TAG = "InputId"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInputIdBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val intent = Intent(this,CreatedAccount::class.java)

        binding.btnNext.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("name",binding.etName.text.toString())
            jsonObject.put("email",binding.etId.text.toString())
            jsonObject.put("password",binding.etPassword.text.toString())

            RetrofitClient.instance.signup(JsonParser.parseString(jsonObject.toString())).enqueue(
                object :Callback<SignupResponse>{
                    override fun onResponse(
                        call: Call<SignupResponse>,
                        response: Response<SignupResponse>
                    ) {
                        if(response.isSuccessful){
                            intent.putExtra("name",binding.etName.text.toString())
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Log.d("${TAG}_TAG",response.toString())
                        }
                    }

                    override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                        Log.d("${TAG}_TAG",t.toString())
                    }

                }
            )



        }

    }
}