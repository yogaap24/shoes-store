package com.yogaap.onlineshop.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.yogaap.onlineshop.Model.ItemModel
import com.yogaap.onlineshop.databinding.ViewHolderRecommendationBinding

class RecommendAdapter (
    val items : MutableList<ItemModel>,
    val isDetailView: Boolean
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
        holder.binding.titleTxtView.text = items[position].title
        holder.binding.priceTxtView.text = "Rp" + items[position].price.toString()
        holder.binding.ratingTxtView.text = items[position].rating.toString()

        val requestOptions = RequestOptions().transform(CenterCrop())
        Glide.with(context!!)
            .load(items[position].picUrl[0])
            .apply(requestOptions)
            .into(holder.binding.recommendImgView)

        if (isDetailView) {
            if (itemCount < 5) {
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
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder (val binding: ViewHolderRecommendationBinding) :
        RecyclerView.ViewHolder(binding.root)
}

private fun dpToPx(context: Context, dp: Int): Int {
    val density = context.resources.displayMetrics.density
    return (dp * density + 0.5f).toInt()
}