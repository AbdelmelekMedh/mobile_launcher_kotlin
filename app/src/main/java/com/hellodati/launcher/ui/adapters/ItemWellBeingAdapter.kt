package com.hellodati.launcher.ui.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellodati.launcher.ui.helper.LocalHelper
import com.hellodati.launcher.R
import com.hellodati.launcher.WellBeing_mobileQuery
import com.hellodati.launcher.databinding.FragmentMenuWellBeingBinding
import com.hellodati.launcher.databinding.InRoomFoodItemBinding
import kotlin.math.floor

class ItemWellBeingAdapter(
    private val dishRestaurantModel: List<WellBeing_mobileQuery.Item>?,
    val binding: FragmentMenuWellBeingBinding
) :
    RecyclerView.Adapter<ItemWellBeingAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = InRoomFoodItemBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.in_room_food_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
            Glide.with(binding.root.context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/well_being_item_pictures/${dishRestaurantModel!![position].id}_${dishRestaurantModel!![position].image}?height=300")
                .into(image)

            when (LocalHelper.getLanguage(binding.root.context).toString()) {
                "en" -> {
                    if (dishRestaurantModel!![position].title.en.toString() != "null"){
                        title.text = dishRestaurantModel!![position].title.en.toString()
                    }else{
                        title.text = dishRestaurantModel!![position].title.default.toString()
                    }

                    if (dishRestaurantModel!![position].description.en.toString() != "null"){
                        summary.text = dishRestaurantModel!![position].description.en.toString()
                    }else{
                        summary.text = dishRestaurantModel!![position].description.default.toString()
                    }
                }

                "ar" -> {
                    if (dishRestaurantModel!![position].title.ar.toString() != "null"){
                        title.text = dishRestaurantModel!![position].title.ar.toString()
                    }else{
                        title.text = dishRestaurantModel!![position].title.default.toString()
                    }

                    if (dishRestaurantModel!![position].description.en.toString() != "null"){
                        summary.text = dishRestaurantModel!![position].description.en.toString()
                    }else{
                        summary.text = dishRestaurantModel!![position].description.default.toString()
                    }
                }

                "fr" -> {
                    if (dishRestaurantModel!![position].title.fr.toString() != "null"){
                        title.text = dishRestaurantModel!![position].title.fr.toString()
                    }else{
                        title.text = dishRestaurantModel!![position].title.default.toString()
                    }

                    if (dishRestaurantModel!![position].description.fr.toString() != "null"){
                        summary.text = dishRestaurantModel!![position].description.fr.toString()
                    }else{
                        summary.text = dishRestaurantModel!![position].description.default.toString()
                    }
                }

                else -> {
                    title.text = dishRestaurantModel!![position].title.default.toString()
                    summary.text = dishRestaurantModel!![position].description.default.toString()
                }
            }



            //price.text = "${dishRestaurantModel!![position].price!!.amount.toInt()} ${dishRestaurantModel!![position].price!!.currency}"

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

        }

       /* holder.itemView.setOnClickListener {
            binding.chosenContentLayout.visibility = View.VISIBLE
            binding.menuRestaurantFrame.visibility = View.GONE
            binding.chosenContentName.text = dishRestaurantModel!![position].title.default.toString()
            binding.choosenContentSummery.text = dishRestaurantModel!![position].description.default.toString()
            binding.chosenContentPrice.text = "${dishRestaurantModel!![position].price!!.amount.toString()} ${dishRestaurantModel!![position].price!!.currency}"
            Glide.with(binding.root.context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/irditem_pictures/${dishRestaurantModel!![position].id}_${dishRestaurantModel!![position].image}?height=300")
                .into(binding.chosenContentImage)

            binding.btnOrder.setOnClickListener {
                try {

                    var dataBase = AppDataBase.getInstance(binding.root.context)
                    val existingBasket = dataBase!!.basketDao().getBasketById(dishRestaurantModel[position].id)
                    if (existingBasket == null) {
                        val basket = dishRestaurantModel!![position].price?.let { it1 ->
                            Basket(
                                title = dishRestaurantModel!![position].title.default,
                                price = it1.amount,
                                quantity = 1,
                                date = LocalDate.now().toString(),
                                idDb = dishRestaurantModel!![position].id
                            )
                        }
                        val allBasket = basket?.let { it1 -> dataBase.basketDao().insert(it1) }

                        MainActivity.updateTextView(dataBase.basketDao().getAll().size.toString())
                        MainActivity.shakeBadge()
                    } else {
                        existingBasket.quantity = existingBasket.quantity+1

                        dataBase.basketDao().update(existingBasket)
                        MainActivity.shakeBadge()
                    }

                } catch (e: Exception) {
                    Log.e("ddatabase", e.message.toString())
                }

            }

        }*/
    }

    override fun getItemCount() = dishRestaurantModel!!.size
}