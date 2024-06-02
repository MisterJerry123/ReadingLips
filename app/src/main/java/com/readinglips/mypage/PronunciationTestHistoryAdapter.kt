package com.readinglips.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.readinglips.databinding.ItemPronunciationTestHistoryBinding
import com.withsejong.retrofit.LoadPronunciationHistoryResponse

class PronunciationTestHistoryAdapter(val testHistory : ArrayList<LoadPronunciationHistoryResponse>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemPronunciationTestHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PronunciationTestHistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return testHistory.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is PronunciationTestHistoryViewHolder){
            holder.originalScript.text="원본문장: ${testHistory[position].originalText}"
            holder.afterTestScript.text="발음분석 결과: ${testHistory[position].pronunciationText}"
            holder.accuracy.text = "정확도: ${testHistory[position].accuracy*100}%"

        }
    }
}