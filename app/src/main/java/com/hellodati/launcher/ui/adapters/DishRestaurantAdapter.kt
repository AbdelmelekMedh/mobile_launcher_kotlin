package com.hellodati.launcher.ui.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellodati.launcher.Ird_menuQuery
import com.hellodati.launcher.ui.helper.LocalHelper
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ClickClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.InRoomFoodItemBinding
import com.hellodati.launcher.serializable_data.SerializableInRoomFood
import com.hellodati.launcher.type.ServiceEnum
import com.hellodati.launcher.ui.fragment.BottomSheetInRoomFoodFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor

class DishRestaurantAdapter(
    private val dishRestaurantModel: List<Ird_menuQuery.Item>?,
    private val context: Context,
    val positions: Int,
    val isFood: Boolean
) :
    RecyclerView.Adapter<DishRestaurantAdapter.ViewHolder>() {
    private var nbr: Int? = 1

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

            when (LocalHelper.getLanguage(context).toString()) {
                "en" -> {
                    title.text = dishRestaurantModel!![position].title.en.toString()
                    summary.text = dishRestaurantModel!![position].description.en.toString()
                }

                "ar" -> {
                    title.text = dishRestaurantModel!![position].title.ar.toString()
                    summary.text = dishRestaurantModel!![position].description.ar.toString()
                }

                "fr" -> {
                    title.text = dishRestaurantModel!![position].title.fr.toString()
                    summary.text = dishRestaurantModel!![position].description.fr.toString()
                }

                else -> {
                    title.text = dishRestaurantModel!![position].title.default.toString()
                    summary.text = dishRestaurantModel!![position].description.default.toString()
                }
            }

            if (title.text.equals("null")){
                title.text= dishRestaurantModel!![position].title.default.toString()
            }

            if (summary.text.equals("null")){
                summary.text= dishRestaurantModel!![position].description.default.toString()
            }
            val hotelLinksPreferences = context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)

            Glide.with(context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/irditem_pictures/${dishRestaurantModel!![position].id}_${dishRestaurantModel!![position].image}?height=300")
                .into(image)


            //price.text = "${dishRestaurantModel!![position].price!!.amount} ${dishRestaurantModel!![position].price!!.currency}"

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

        holder.itemView.setOnClickListener {
            GlobalScope.launch {

                withContext(Dispatchers.Main) {
                    val clickClient = ClickClient(context)
                    clickClient.createClick(
                        ResidenceClient(context).getResidenceId().toString(),dishRestaurantModel!![position].id,
                        ServiceEnum.IrdEatItem)
                }
            }

            /*val bundle = Bundle()
            bundle.putSerializable("item", SerializableInRoomFood(position,positions))

            val navController = Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.menuRestaurantFragment,bundle)*/

            val bottomSheetFragment = BottomSheetInRoomFoodFragment.newInstance(SerializableInRoomFood(position,positions,isFood))
            bottomSheetFragment.show(
                (context as FragmentActivity).supportFragmentManager,
                bottomSheetFragment.tag
            )
        }

    }
    override fun getItemCount() = dishRestaurantModel!!.size
}