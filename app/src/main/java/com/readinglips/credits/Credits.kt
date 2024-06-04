package com.readinglips.credits

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.readinglips.R
import com.readinglips.changeInfo.ChangePassword
import com.readinglips.databinding.ActivityCreditsBinding
import com.readinglips.lipReading.CameraCopy
import com.readinglips.lipReading.PronunciationTestNEW
import com.readinglips.mypage.LipReadingHistory
import com.readinglips.mypage.PronunciationTestHistory
import com.withsejong.retrofit.Develops

class Credits:AppCompatActivity() {

    private lateinit var binding: ActivityCreditsBinding
    lateinit var drawer: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreditsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mockData = arrayListOf<Develops>(
            Develops("Backend", "채윤", "컴퓨터공학과",19011584),
            Develops("Frontend", "정건희", "지능기전공학부", 19011723),
            Develops("AI", "이승훈","컴퓨터공학과",18011541)
        )
        binding.rcvDeveloper.adapter = CreditsAdapter(mockData)
        binding.rcvDeveloper.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)



        val drawerbtn = binding.ibtnHamburgerManu
        drawerbtn.setOnClickListener {
            drawer.openDrawer(Gravity.LEFT)
        }
        drawer = binding.dloMenu
        binding.navigationview.setCheckedItem(R.id.menu_lipreading)

        val backActionCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.dloMenu.isDrawerOpen(GravityCompat.START)) {
                    binding.dloMenu.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        }
        this.onBackPressedDispatcher.addCallback(this, backActionCallback)
        val intentPronunciationTest = Intent(this, PronunciationTestNEW::class.java)
        val intentPronunciationTestHistory = Intent(this, PronunciationTestHistory::class.java)
        val intentLipReadingHistory = Intent(this, LipReadingHistory::class.java)
        val intentDevelops = Intent(this, Credits::class.java)
        val intentLipReading = Intent(this, CameraCopy::class.java)
        val intentChangePassword = Intent(this, ChangePassword::class.java)



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
                R.id.menu_change_password->{
                    startActivity(intentChangePassword)
                    finish()
                }
            }
            true
        }
    }
}