package com.readinglips.credits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.readinglips.databinding.ItemCreditsBinding
import com.withsejong.retrofit.Develops

class CreditsAdapter(val develops: ArrayList<Develops>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCreditsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CreditsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return develops.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CreditsViewHolder) {
            holder.apply {
                major.text = develops[position].major
                name.text = develops[position].name
                part.text = develops[position].part
            }

        }
    }
}