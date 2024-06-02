package com.readinglips.mypage

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.readinglips.databinding.ItemLipReadingHistoryBinding
import com.readinglips.databinding.ItemPronunciationTestHistoryBinding
import com.withsejong.retrofit.LoadLipReadingHistoryResponse
import com.withsejong.retrofit.LoadPronunciationHistoryResponse
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class LipReadingHistoryAdapter(val lipReadingHistory : ArrayList<LoadLipReadingHistoryResponse>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemLipReadingHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LipReadingHistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return lipReadingHistory.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(holder is LipReadingHistoryViewHolder){
            val utcDateTime = ZonedDateTime.parse(lipReadingHistory[position].createdAt, DateTimeFormatter.ISO_DATE_TIME)
            // 한국 시간대로 변환
            val koreaZoneId = ZoneId.of("Asia/Seoul")
            val koreaDateTime = utcDateTime.withZoneSameInstant(koreaZoneId)


            holder.createdAt.text = "립리딩 시간: ${koreaDateTime}"

            holder.afterTestScript.text = "립리딩 결과: ${lipReadingHistory[position].subtitle}"
        }

    }

}