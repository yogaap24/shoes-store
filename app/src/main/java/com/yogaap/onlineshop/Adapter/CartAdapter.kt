package com.yogaap.onlineshop.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.project1762.Helper.ChangeNumberItemsListener
import com.example.project1762.Helper.ManagementCart
import com.yogaap.onlineshop.Model.ItemsModel
import com.yogaap.onlineshop.databinding.ViewHolderCartBinding

class CartAdapter(
    private val listItemSelected: ArrayList<ItemsModel>,
    context: Context,
    var changeNumberItemsListener: ChangeNumberItemsListener? = null
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private val managementcart = ManagementCart(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.ViewHolder {
        val binding = ViewHolderCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartAdapter.ViewHolder, position: Int) {
        val item = listItemSelected[position]
        val position = position

        holder.binding.cartHolderTitleVw.text = item.title
        holder.binding.cartHolderPricePerItem.text = "Rp. ${item.price}"
        holder.binding.cartHolderPriceAllItems.text = "Rp. ${item.price * item.numberInCart}"
        holder.binding.cartHolderTotalItem.text = item.numberInCart.toString()

        val requestOptions = RequestOptions().transform(CenterCrop())
        Glide.with(holder.itemView.context)
            .load(item.picUrl[0])
            .apply(requestOptions)
            .into(holder.binding.cartHolderImgVw)

        holder.binding.cartPlusItem.setOnClickListener {
            managementcart.plusItem(
                listItemSelected,
                position,
                object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        notifyItemChanged(position)
                        changeNumberItemsListener?.onChanged()
                    }
                })
        }

        holder.binding.cartMinusItem.setOnClickListener {
            managementcart.minusItem(
                listItemSelected,
                position,
                object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        notifyItemChanged(position)
                        changeNumberItemsListener?.onChanged()
                    }
                })
        }
    }

    override fun getItemCount(): Int = listItemSelected.size

    class ViewHolder(val binding: ViewHolderCartBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}