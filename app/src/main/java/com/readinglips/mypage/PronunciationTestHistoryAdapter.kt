package com.readinglips.mypage

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.withsejong.retrofit.LoadPronunciationHistoryResponse

class PronunciationTestHistoryAdapter(val testHistory : ArrayList<LoadPronunciationHistoryResponse>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return testHistory.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}