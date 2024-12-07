package com.hellodati.launcher.ui.adapters

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hellodati.launcher.GetMobileOrdersQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.databinding.FragmentOrderHistoryBinding
import com.hellodati.launcher.databinding.ItemHistoryContentBinding
import com.hellodati.launcher.type.OrderItemStatusEnum
import com.hellodati.launcher.type.ReservationTypeEnum
import com.hellodati.launcher.ui.helper.LocalHelper
import java.lang.Exception
import kotlin.math.floor

class ItemHistoryOrderAdapter(
    private var dishRestaurantModel: List<GetMobileOrdersQuery.OrderItem>?,
    val binding: FragmentOrderHistoryBinding
) :
    RecyclerView.Adapter<ItemHistoryOrderAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemHistoryContentBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_content, parent, false)
        /*  val animation = AnimationUtils.loadAnimation(parent.context, R.anim.item_animation)
          view.startAnimation(animation)*/
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            Log.e("ordertest", dishRestaurantModel!![position].item!!.title.en.toString())
            try {
                when (LocalHelper.getLanguage(binding.root.context).toString()) {
                    "en" -> {
                        if (dishRestaurantModel!![position].title.en.toString() != "null") {
                            quantity.text =
                                dishRestaurantModel!![position].item!!.title.en.toString()
                        } else {
                            quantity.text =
                                dishRestaurantModel!![position].item!!.title.default.toString()
                        }
                    }

                    "ar" -> {
                        if (dishRestaurantModel!![position].title.ar.toString() != "null") {
                            quantity.text =
                                dishRestaurantModel!![position].item!!.title.ar.toString()
                        } else {
                            quantity.text =
                                dishRestaurantModel!![position].item!!.title.default.toString()
                        }
                    }

                    "fr" -> {
                        if (dishRestaurantModel!![position].title.fr.toString() != "null") {
                            quantity.text =
                                dishRestaurantModel!![position].item!!.title.fr.toString()
                        } else {
                            quantity.text =
                                dishRestaurantModel!![position].item!!.title.default.toString()
                        }
                    }

                    else -> {
                        quantity.text =
                            dishRestaurantModel!![position].item!!.title.default.toString()
                    }
                }

                title.text = dishRestaurantModel!![position].quantity.toInt().toString() + " - " + binding.root.resources.getString(R.string.item)
                when (dishRestaurantModel!![position].status){
                    OrderItemStatusEnum.cancelled -> {
                        status.text = binding.root.context.resources.getString(R.string.item_purchase_cancelled)
                    }
                    OrderItemStatusEnum.delivered -> {
                        status.text = binding.root.context.resources.getString(R.string.item_purchase_delivred)
                    }
                    OrderItemStatusEnum.in_delivery -> {
                        status.text = binding.root.context.resources.getString(R.string.item_purchase_in_delivery)
                    }
                    OrderItemStatusEnum.in_preparation -> {
                        status.text = binding.root.context.resources.getString(R.string.item_purchase_preparation)
                    }
                    OrderItemStatusEnum.in_progress -> {
                        status.text = binding.root.context.resources.getString(R.string.item_purchase_in_progress)
                    }
                    OrderItemStatusEnum.ready -> {
                        status.text = binding.root.context.resources.getString(R.string.item_purchase_ready)
                    }
                    OrderItemStatusEnum.rejected -> {
                        status.text = binding.root.context.resources.getString(R.string.item_purchase_rejected)
                    }
                    OrderItemStatusEnum.waiting -> {
                        status.text = binding.root.context.resources.getString(R.string.item_purchase_waiting)
                    }
                    else -> {}
                }

                status.setTextColor(binding.root.context.resources.getColor(R.color.light_blue))
                //uniPrice.text = "${dishRestaurantModel!![position].price.amount * dishRestaurantModel!![position].quantity} ${dishRestaurantModel!![position].price.currency.toString()}"

                if(dishRestaurantModel!![position].price!!.hasDiscount == true){
                    if (dishRestaurantModel!![position].price!!.discountType.toString() == "percentege"){
                        val discountAmount = dishRestaurantModel!![position].price!!.amount - ((dishRestaurantModel!![position].price!!.discountAmount!! * dishRestaurantModel!![position].price!!.amount) / 100.0)
                        if(discountAmount == floor(discountAmount)){
                            uniPrice.text = "${discountAmount.toInt()} ${dishRestaurantModel!![position].price!!.currency}"
                        }else{
                            uniPrice.text = "$discountAmount ${dishRestaurantModel!![position].price!!.currency}"
                        }
                    }else{
                        val discountAmount = dishRestaurantModel!![position].price!!.amount - dishRestaurantModel!![position].price!!.discountAmount!!
                        if(discountAmount == floor(discountAmount)){
                            uniPrice.text = "${discountAmount.toInt()} ${dishRestaurantModel!![position].price!!.currency}"
                        }else{
                            uniPrice.text = "$discountAmount ${dishRestaurantModel!![position].price!!.currency}"
                        }
                    }
                }else{
                    if(dishRestaurantModel!![position].price!!.amount == floor(dishRestaurantModel!![position].price!!.amount)){
                        uniPrice.text = "${dishRestaurantModel!![position].price!!.amount.toInt()} ${dishRestaurantModel!![position].price!!.currency}"
                    }else{
                        uniPrice.text = "${dishRestaurantModel!![position].price!!.amount} ${dishRestaurantModel!![position].price!!.currency}"
                    }
                }

            } catch (e: Exception) {
                Log.e("ordertt", e.message.toString())
            }

        }

        /*   holder.itemView.setOnClickListener {
               binding..text = dishRestaurantModel[position].subItemTitle

           }*/
    }

    override fun getItemCount() = dishRestaurantModel!!.size
}