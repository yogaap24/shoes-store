package com.yogaap.onlineshop.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.yogaap.onlineshop.Model.ItemsModel
import com.yogaap.onlineshop.activity.DetailActivity
import com.yogaap.onlineshop.databinding.ViewHolderRecommendationBinding

class RecommendAdapter (
    val items : MutableList<ItemsModel>,
    val isListAllView: Boolean
) : RecyclerView.Adapter<RecommendAdapter.ViewHolder>() {

    private var context : Context? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendAdapter.ViewHolder {
        context = parent.context
        val binding = ViewHolderRecommendationBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.titleTxtView.text = item.title
        holder.binding.priceTxtView.text = "Rp. ${item.price}"
        holder.binding.ratingTxtView.text = item.rating.toString()

        val requestOptions = RequestOptions().transform(CenterCrop())
        Glide.with(context!!)
            .load(item.picUrl[0])
            .apply(requestOptions)
            .into(holder.binding.recommendImgView)

        if (isListAllView) {
            if (itemCount < 10) {
                holder.binding.recommendImgView.layoutParams.width = dpToPx(context!!, 364)
                holder.binding.recommendImgView.layoutParams.height = dpToPx(context!!, 164)

                holder.binding.titleTxtView.setPadding(
                    dpToPx(context!!, 8),
                    dpToPx(context!!, 0),
                    dpToPx(context!!, 0),
                    dpToPx(context!!, 0)
                )

                holder.binding.priceTxtView.setPadding(
                    dpToPx(context!!, 8),
                    dpToPx(context!!, 2),
                    dpToPx(context!!, 0),
                    dpToPx(context!!, 0)
                )

                holder.binding.ratingTxtView.textSize = 20f
                holder.binding.starImgView.layoutParams.width = dpToPx(context!!, 20)
                holder.binding.starImgView.layoutParams.height = dpToPx(context!!, 20)
            }
        }

        holder.binding.root.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("item_object", item)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder (val binding: ViewHolderRecommendationBinding) :
        RecyclerView.ViewHolder(binding.root)
}

private fun dpToPx(context: Context, dp: Int): Int {
    val density = context.resources.displayMetrics.density
    return (dp * density + 0.5f).toInt()
}