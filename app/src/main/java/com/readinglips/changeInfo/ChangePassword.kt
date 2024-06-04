package com.readinglips.changeInfo

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.gson.JsonParser
import com.readinglips.R
import com.readinglips.credits.Credits
import com.readinglips.databinding.ActivityChangePasswordBinding
import com.readinglips.databinding.ActivityChangePwBinding
import com.readinglips.lipReading.CameraCopy
import com.readinglips.lipReading.PronunciationTestNEW
import com.readinglips.mypage.LipReadingHistory
import com.readinglips.mypage.PronunciationTestHistory
import com.withsejong.retrofit.RetrofitClient
import org.json.JSONObject

class ChangePassword:AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    lateinit var drawer : DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val drawerbtn = binding.ibtnHamburgerManu
        drawerbtn.setOnClickListener {
            drawer.openDrawer(Gravity.LEFT)
        }
        drawer = binding.dloMenu
        //binding.toolbar.hideOverflowMenu()
        binding.navigationview.setCheckedItem(R.id.menu_lipreading_history)


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

        val intentPronunciationTest = Intent(this, PronunciationTestNEW::class.java)
        val intentPronunciationTestHistory = Intent(this, PronunciationTestHistory::class.java)
        val intentLipReadingHistory = Intent(this, LipReadingHistory::class.java)
        val intentDevelops = Intent(this, Credits::class.java)
        val intentChangePassword = Intent(this, ChangePassword::class.java)
        val intentLipReading = Intent(this, CameraCopy::class.java)



        binding.navigationview.setCheckedItem(R.id.menu_lipreading)
        binding.navigationview.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_lipreading->{
                    startActivity(intentLipReading)
                    finish()
                }
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
            }
            true
        }

        binding.btnNext.setOnClickListener {
            if(binding.etNewPasswordInput.text.toString() != binding.etNewPasswordOkInput.text.toString()){
                Toast.makeText(this,"두 비밀번호가 일치하지 않습니다!",Toast.LENGTH_SHORT).show()
            }
            else{
                val emailSharedPreference = getSharedPreferences("token", MODE_PRIVATE)
                val email = emailSharedPreference.getString("email","null")

                val jsonObject=JSONObject()
                jsonObject.put("email", email.toString())
                jsonObject.put("password", binding.etNewPasswordOkInput.text.toString())


                val changepwThread = Thread{
                    val response = RetrofitClient.instance.changePw(JsonParser.parseString(jsonObject.toString())).execute()

                    if(response.isSuccessful){
                        runOnUiThread {
                            Toast.makeText(this,"비번 변경이 완료되었습니다.",Toast.LENGTH_SHORT).show()
                            binding.btnNext.visibility = View.INVISIBLE
                            binding.etNewPasswordInput.isEnabled =false
                            binding.etNewPasswordOkInput.isEnabled=false
                        }
                    }
                    else{
                        runOnUiThread {
                            Toast.makeText(this,"비번 변경에 실패했습니다.",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                changepwThread.join()
                changepwThread.start()
            }



        }



    }

}