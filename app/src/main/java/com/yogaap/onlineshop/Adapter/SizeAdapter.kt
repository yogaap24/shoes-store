package com.yogaap.onlineshop.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yogaap.onlineshop.R
import com.yogaap.onlineshop.databinding.ViewHolderSizeBinding

class SizeAdapter(
    val items: MutableList<String>,
    val recyclerView: RecyclerView,
) : RecyclerView.Adapter<SizeAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION
    private var lastSelectedPosition = RecyclerView.NO_POSITION
    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SizeAdapter.ViewHolder {
        context = parent.context
        val binding = ViewHolderSizeBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SizeAdapter.ViewHolder, position: Int) {
        val position = holder.adapterPosition

        holder.binding.sizeTxtView.text = items[position]

        holder.binding.root.setOnClickListener {
            if (selectedPosition != position) {
                lastSelectedPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(lastSelectedPosition)
                notifyItemChanged(selectedPosition)
                recyclerView.smoothScrollToPosition(position)
            }
        }

        if (selectedPosition == position) {
            holder.binding.sizeLayout.setBackgroundResource(R.drawable.gray_bg_selected)
            holder.binding.sizeTxtView.setTextColor(context.resources.getColor(R.color.purple))
        } else {
            holder.binding.sizeLayout.setBackgroundResource(R.drawable.gray_bg)
            holder.binding.sizeTxtView.setTextColor(context.resources.getColor(R.color.black))
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ViewHolderSizeBinding) :
        RecyclerView.ViewHolder(binding.root)
}