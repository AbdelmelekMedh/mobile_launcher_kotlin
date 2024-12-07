package com.hellodati.launcher.ui.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellodati.launcher.EateriesMobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ClickClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.FragmentMenuRestaurantDrinkBinding
import com.hellodati.launcher.databinding.InRoomFoodItemBinding
import com.hellodati.launcher.serializable_data.SerializableMenuAboutBooking
import com.hellodati.launcher.serializable_data.SerializableRestoDrinkItems
import com.hellodati.launcher.type.ServiceEnum
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor

class ItemRestaurantDrinkAdapter(
    private val dishRestaurantModel: List<EateriesMobileQuery.Item>?,
    val binding: FragmentMenuRestaurantDrinkBinding,
    var restaurantList: Int,
    var Categoryposition: Int
) :
    RecyclerView.Adapter<ItemRestaurantDrinkAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = InRoomFoodItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.in_room_food_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            when (LocalHelper.getLanguage(binding.root.context).toString()) {
                "en" -> {
                    if (dishRestaurantModel!![position].title.en.toString() != "null"){
                        title.text= dishRestaurantModel!![position].title.en.toString()
                    }else{
                        title.text= dishRestaurantModel!![position].title.default.toString()
                    }

                    if (dishRestaurantModel!![position].description.en.toString() != "null"){
                        summary.text= dishRestaurantModel!![position].description.en.toString()
                    }else{
                        summary.text= dishRestaurantModel!![position].description.default.toString()
                    }

                }

                "ar" -> {
                    if (dishRestaurantModel!![position].title.ar.toString() != "null"){
                        title.text= dishRestaurantModel!![position].title.ar.toString()
                    }else{
                        title.text= dishRestaurantModel!![position].title.default.toString()
                    }

                    if (dishRestaurantModel!![position].description.ar.toString() != "null"){
                        summary.text= dishRestaurantModel!![position].description.ar.toString()
                    }else{
                        summary.text= dishRestaurantModel!![position].description.default.toString()
                    }
                }

                "fr" -> {
                    if (dishRestaurantModel!![position].title.fr.toString() != "null"){
                        title.text= dishRestaurantModel!![position].title.fr.toString()
                    }else{
                        title.text= dishRestaurantModel!![position].title.default.toString()
                    }

                    if (dishRestaurantModel!![position].description.fr.toString() != "null"){
                        summary.text= dishRestaurantModel!![position].description.fr.toString()
                    }else{
                        summary.text= dishRestaurantModel!![position].description.default.toString()
                    }
                }

                else -> {
                    title.text= dishRestaurantModel!![position].title.default.toString()
                    summary.text= dishRestaurantModel!![position].description.default.toString()
                }
            }


            //price.text= "${dishRestaurantModel!![position].price!!.amount.toInt()} ${dishRestaurantModel!![position].price!!.currency}"

            if(dishRestaurantModel!![position].price!!.hasDiscount == true){
                if (dishRestaurantModel!![position].price!!.discountType.toString() == "percentege"){
                    val discountAmount : Double = dishRestaurantModel!![position].price!!.amount - ((dishRestaurantModel!![position].price!!.discountAmount!! * dishRestaurantModel!![position].price!!.amount) / 100.0)
                    if(discountAmount == floor(discountAmount)){
                        price.text = "${discountAmount.toInt()} ${dishRestaurantModel!![position].price!!.currency}"
                    }else{
                        price.text = "$discountAmount ${dishRestaurantModel!![position].price!!.currency}"
                    }
                    if(dishRestaurantModel!![position].price!!.amount == floor(dishRestaurantModel!![position].price!!.amount)){
                        oldprice.text = "${dishRestaurantModel!![position].price!!.amount.toInt()} ${dishRestaurantModel!![position].price!!.currency}"
                    }else{
                        oldprice.text = "${dishRestaurantModel!![position].price!!.amount} ${dishRestaurantModel!![position].price!!.currency}"
                    }

                    oldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }else{
                    val discountAmount = dishRestaurantModel!![position].price!!.amount - dishRestaurantModel!![position].price!!.discountAmount!!
                    if(discountAmount == floor(discountAmount)){
                        price.text = "${discountAmount.toInt()} ${dishRestaurantModel!![position].price!!.currency}"
                    }else{
                        price.text = "$discountAmount ${dishRestaurantModel!![position].price!!.currency}"
                    }
                    if(dishRestaurantModel!![position].price!!.amount == floor(dishRestaurantModel!![position].price!!.amount)){
                        oldprice.text = "${dishRestaurantModel!![position].price!!.amount.toInt()} ${dishRestaurantModel!![position].price!!.currency}"
                    }else{
                        oldprice.text = "${dishRestaurantModel!![position].price!!.amount} ${dishRestaurantModel!![position].price!!.currency}"
                    }
                    oldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
            }else{
                if(dishRestaurantModel!![position].price!!.amount == floor(dishRestaurantModel!![position].price!!.amount)){
                    price.text = "${dishRestaurantModel!![position].price!!.amount.toInt()} ${dishRestaurantModel!![position].price!!.currency}"
                }else{
                    price.text = "${dishRestaurantModel!![position].price!!.amount} ${dishRestaurantModel!![position].price!!.currency}"
                }
            }

            val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
            Glide.with(binding.root.context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/eatery_item_pictures/${dishRestaurantModel[holder.adapterPosition].id}_${dishRestaurantModel[holder.adapterPosition].image}?height=200")
                .into(image)
        }


        holder.itemView.setOnClickListener {
            GlobalScope.launch {

                withContext(Dispatchers.Main) {
                    val clickClient = ClickClient(binding.root.context)
                    clickClient.createClick(
                        ResidenceClient(binding.root.context).getResidenceId().toString(),dishRestaurantModel!![position].id,
                        ServiceEnum.EateryItem)
                }
            }
            val bundle = Bundle()
            bundle.putSerializable("item", SerializableRestoDrinkItems(position,Categoryposition))
            bundle.putSerializable("list",SerializableMenuAboutBooking(restaurantList))

            val navController = Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.menuRestoAndDrinkItems,bundle)
        }
    }

    override fun getItemCount() = dishRestaurantModel!!.size
}