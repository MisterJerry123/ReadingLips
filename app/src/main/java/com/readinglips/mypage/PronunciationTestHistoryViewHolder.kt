package com.readinglips.mypage

import androidx.recyclerview.widget.RecyclerView
import com.readinglips.databinding.ItemPronunciationTestHistoryBinding

class PronunciationTestHistoryViewHolder(val binding : ItemPronunciationTestHistoryBinding): RecyclerView.ViewHolder(binding.root) {
    val originalScript = binding.tvOriginalText
    val afterTestScript = binding.tvAfterTestText
    val accuracy = binding.tvAccuracyIndicator

}