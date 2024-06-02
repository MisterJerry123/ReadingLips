package com.readinglips.mypage

import androidx.recyclerview.widget.RecyclerView
import com.readinglips.databinding.ItemLipReadingHistoryBinding
import com.readinglips.databinding.ItemPronunciationTestHistoryBinding

class LipReadingHistoryViewHolder(val binding : ItemLipReadingHistoryBinding): RecyclerView.ViewHolder(binding.root) {
    val createdAt = binding.tvCreatedAt
    val afterTestScript = binding.tvAfterLipReadingText

}