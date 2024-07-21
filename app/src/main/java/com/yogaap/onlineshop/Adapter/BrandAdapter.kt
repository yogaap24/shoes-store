package com.yogaap.onlineshop.Adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yogaap.onlineshop.Model.BrandModel
import com.yogaap.onlineshop.R
import com.yogaap.onlineshop.databinding.ViewHolderBrandBinding

class BrandAdapter(
    val items: MutableList<BrandModel>,
    val recyclerView: RecyclerView,
    val isDetailView: Boolean
) : RecyclerView.Adapter<BrandAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION
    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BrandAdapter.ViewHolder {
        context = parent.context
        val binding = ViewHolderBrandBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandAdapter.ViewHolder, position: Int) {
        val item = items[position]

        if (isDetailView) {
            holder.binding.detailLayout.visibility = View.VISIBLE
            holder.binding.homeLayout.visibility = View.GONE
            holder.binding.txtViewBrandDetail.text = item.title

            Glide.with(context)
                .load(item.picUrl)
                .into(holder.binding.imgViewBrandDetail)

            val layoutParams = holder.binding.detailLayout.layoutParams
            if (itemCount < 10) {
                layoutParams.width = dpToPx(context, 364)
                layoutParams.height = dpToPx(context, 164)

                holder.binding.txtViewBrandDetail.setPadding(
                    dpToPx(context, 8),
                    dpToPx(context, 8),
                    dpToPx(context, 8),
                    dpToPx(context, 8)
                )
                holder.binding.txtViewBrandDetail.textSize = 20f

            }
            holder.binding.detailLayout.layoutParams = layoutParams
        } else {
            holder.binding.detailLayout.visibility = View.GONE
            holder.binding.homeLayout.visibility = View.VISIBLE

            holder.binding.brandTxtView.text = item.title
            Glide.with(context)
                .load(item.picUrl)
                .into(holder.binding.brandImgView)

            holder.binding.root.setOnClickListener {
                val position = holder.adapterPosition
                if (selectedPosition == position) {
                    selectedPosition = RecyclerView.NO_POSITION
                } else {
                    val lastSelectedPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(lastSelectedPosition)
                    recyclerView.smoothScrollToPosition(position)
                }
                notifyItemChanged(position)
            }

            holder.binding.brandTxtView.setTextColor(context.resources.getColor(R.color.white))
            if (selectedPosition == position) {
                holder.binding.brandImgView.setBackgroundResource(0)
                holder.binding.brandLayout.setBackgroundResource(R.drawable.purple_bg)
                ImageViewCompat.setImageTintList(
                    holder.binding.brandImgView,
                    ColorStateList.valueOf(context.getColor(R.color.white))
                )
                holder.binding.brandTxtView.visibility = View.VISIBLE
            } else {
                holder.binding.brandImgView.setBackgroundResource(R.drawable.gray_bg)
                holder.binding.brandLayout.setBackgroundResource(0)
                ImageViewCompat.setImageTintList(
                    holder.binding.brandImgView,
                    ColorStateList.valueOf(context.getColor(R.color.black))
                )
                holder.binding.brandTxtView.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val binding: ViewHolderBrandBinding) :
        RecyclerView.ViewHolder(binding.root)
}

private fun RecyclerView.smoothScrollToPosition(position: Int) {
    val smoothScroller = object : LinearSmoothScroller(context) {
        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_END
        }
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}

private fun dpToPx(context: Context, dp: Int): Int {
    val density = context.resources.displayMetrics.density
    return (dp * density + 0.5f).toInt()
}