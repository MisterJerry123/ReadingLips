package com.readinglips.credits

import androidx.recyclerview.widget.RecyclerView
import com.readinglips.databinding.ItemCreditsBinding

class CreditsViewHolder(val binding: ItemCreditsBinding):RecyclerView.ViewHolder(binding.root) {
    var name = binding.tvNameIndicator
    var major = binding.tvMajor
    var part = binding.tvPartIndicator

}