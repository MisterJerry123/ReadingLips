package com.readinglips.mypage

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Gravity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.readinglips.R
import com.readinglips.databinding.ActivityLipreadingHistoryBinding
import com.readinglips.databinding.ActivityPronunciationTestHistoryBinding
import com.readinglips.lipReading.CameraCopy
import com.readinglips.lipReading.PronunciationTestNEW
import com.withsejong.retrofit.LoadLipReadingHistoryResponse
import com.withsejong.retrofit.LoadPronunciationHistoryResponse
import com.withsejong.retrofit.RetrofitClient

class LipReadingHistory:AppCompatActivity() {
    private lateinit var binding : ActivityLipreadingHistoryBinding
    lateinit var drawer : DrawerLayout
    private  var lipReadingList = ArrayList<LoadLipReadingHistoryResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLipreadingHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        val mockData = arrayListOf<LoadLipReadingHistoryResponse>(
//            LoadLipReadingHistoryResponse("안녕하시요","2024-06-02T18:52:23.363Z"),
//            LoadLipReadingHistoryResponse("하이 헬로","2024-06-03T12:52:23.363Z"),
//            LoadLipReadingHistoryResponse("김태식의 두마리 치킨","2024-06-03T18:52:23.363Z"),
//            LoadLipReadingHistoryResponse("이다예 복싱장","2024-06-04T02:52:23.363Z"),
//            )

        val loadLipReadingHistoryThread=Thread{
            val response = RetrofitClient.instance.loadLipReadingHistory("misterjerry12345@gmail.com").execute()

            if(response.isSuccessful){
                lipReadingList.addAll(response.body()!!)

                runOnUiThread {
                    binding.rcvLipReadingHistory.adapter = LipReadingHistoryAdapter(lipReadingList)
                    binding.rcvLipReadingHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                }

            }

        }
        loadLipReadingHistoryThread.join()
        loadLipReadingHistoryThread.start()

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
        val intentLipReading = Intent(this, CameraCopy::class.java)


        binding.navigationview.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menu_pronounce_test->{
                    startActivity(intentPronunciationTest)
                    finish()
                }
                R.id.menu_pronunciation_history->{
                    startActivity(intentPronunciationTestHistory)
                    finish()
                }
                R.id.menu_lipreading->{
                    startActivity(intentLipReading)
                    finish()
                }
            }
            true
        }
    }
}