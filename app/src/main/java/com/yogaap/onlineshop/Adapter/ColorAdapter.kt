package com.yogaap.onlineshop.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yogaap.onlineshop.R
import com.yogaap.onlineshop.databinding.ViewHolderColorBinding

class ColorAdapter(
    val items: MutableList<String>,
    val recyclerView: RecyclerView,
    private val onColorSelectedListener: OnColorSelectedListener
) : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION
    private var lastSelectedPosition = RecyclerView.NO_POSITION
    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ColorAdapter.ViewHolder {
        context = parent.context
        val binding = ViewHolderColorBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorAdapter.ViewHolder, position: Int) {
        val position = holder.adapterPosition

        Glide.with(context).load(items[position]).into(holder.binding.colorImgView)

        holder.binding.root.setOnClickListener {
            if (selectedPosition != position) {
                lastSelectedPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(lastSelectedPosition)
                notifyItemChanged(selectedPosition)
                recyclerView.smoothScrollToPosition(position)

                onColorSelectedListener.onColorSelected(items[position])
            }
        }

        if (selectedPosition == position) {
            holder.binding.colorLayout.setBackgroundResource(R.drawable.gray_bg_selected)
        } else {
            holder.binding.colorLayout.setBackgroundResource(R.drawable.gray_bg)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ViewHolderColorBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnColorSelectedListener {
        fun onColorSelected(picUrl: String)
    }

    fun setSelectedColor(picUrl: String) {
        val position = items.indexOf(picUrl)
        if (position != -1 && position != selectedPosition) {
            lastSelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(lastSelectedPosition)
            notifyItemChanged(selectedPosition)
            recyclerView.smoothScrollToPosition(selectedPosition)
        }
    }
}