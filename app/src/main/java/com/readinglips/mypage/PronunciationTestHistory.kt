package com.readinglips.mypage

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Gravity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.readinglips.R
import com.readinglips.changeInfo.ChangePassword
import com.readinglips.credits.Credits
import com.readinglips.databinding.ActivityPronunciationTestHistoryBinding
import com.readinglips.lipReading.CameraCopy
import com.readinglips.lipReading.PronunciationTestNEW
import com.withsejong.retrofit.LoadPronunciationHistoryResponse

import com.withsejong.retrofit.RetrofitClient

class PronunciationTestHistory:AppCompatActivity() {
    private lateinit var binding : ActivityPronunciationTestHistoryBinding
    lateinit var drawer : DrawerLayout
    private  var pronunciationList = ArrayList<LoadPronunciationHistoryResponse>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPronunciationTestHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO 추후에 서버에서 받은 데이터로 띄울 것
//        val mockData = arrayListOf <LoadPronunciationHistoryResponse>(
//            LoadPronunciationHistoryResponse("간장공장공장장","간장공장공장장",1.0),
//            LoadPronunciationHistoryResponse("된장공장공장장","됀장공장공장장",0.8),
//
//            LoadPronunciationHistoryResponse("쌈장공장공장장","썀장굥장굥장장",.6),
//
//            )

        val getPronunciationListThread= Thread{

            val response = RetrofitClient
                .instance
                .getPronunciationList("misterjerry12345@gmail.com")
                .execute()

            if(response.isSuccessful){
                /**
                 * TODO : NULL 처리 하세영
                 */
                Log.d("PronunciationTestHistory_TAG1", response.body().toString())
                pronunciationList.addAll(response.body()!!)
                Log.d("PronunciationTestHistory_TAG2", pronunciationList.toString())

                runOnUiThread {
                    binding.rcvTestHistory.adapter = PronunciationTestHistoryAdapter(pronunciationList)
                    binding.rcvTestHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


                }

            }
        }
        getPronunciationListThread.join()
        getPronunciationListThread.start()

        Log.d("PronunciationTestHistory_TAG3", pronunciationList.toString())

        val drawerbtn = binding.ibtnHamburgerManu
        drawerbtn.setOnClickListener {
            drawer.openDrawer(Gravity.LEFT)
        }
        drawer = binding.dloMenu
        //binding.toolbar.hideOverflowMenu()
        binding.navigationview.setCheckedItem(R.id.menu_pronunciation_history)


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
        val intentLipReadingHistory = Intent(this, LipReadingHistory::class.java)
        val intentLipReading = Intent(this, CameraCopy::class.java)
        val intentDevelops = Intent(this, Credits::class.java)
        val intentChangePassword = Intent(this, ChangePassword::class.java)

        binding.navigationview.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menu_pronounce_test->{
                    startActivity(intentPronunciationTest)
                    finish()
                }
                R.id.menu_lipreading_history->{
                    startActivity(intentLipReadingHistory)
                    finish()
                }
                R.id.menu_lipreading->{
                    startActivity(intentLipReading)
                    finish()
                }
                R.id.menu_introducing->{
                    startActivity(intentDevelops)
                    finish()
                }
                R.id.menu_change_password->{
                    startActivity(intentChangePassword)
                    finish()
                }
            }
            true
        }


//        binding.rcvTestHistory.adapter=PronunciationTestHistoryAdapter(mockData)
//        binding.rcvTestHistory.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
    }
}