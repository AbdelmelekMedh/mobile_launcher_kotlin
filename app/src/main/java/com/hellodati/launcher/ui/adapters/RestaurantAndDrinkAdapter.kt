package com.hellodati.launcher.ui.adapters

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellodati.launcher.EateriesMobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.databinding.FragmentRestaurantAndDrinkBinding
import com.hellodati.launcher.databinding.ItemRecyclerRestaurantDrinkBinding
import com.hellodati.launcher.api.ClickClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.type.ServiceEnum
import com.hellodati.launcher.serializable_data.SerializableMenuAboutBooking
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantAndDrinkAdapter(
    private val restaurantList: List<EateriesMobileQuery.EateriesMobile>,
    val binding: FragmentRestaurantAndDrinkBinding,
) :
    RecyclerView.Adapter<RestaurantAndDrinkAdapter.ViewHolder>() {
    private var lastPosition = -1
    private var itemPosition = 0
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRecyclerRestaurantDrinkBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_restaurant_drink, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setAnimation(holder.itemView, holder.adapterPosition)
        holder.binding.apply {

            when (LocalHelper.getLanguage(binding.root.context).toString()) {
                "en" -> {
                    if (restaurantList[holder.adapterPosition].title.en.toString() != "null"){
                        name.text= restaurantList[holder.adapterPosition].title.en.toString()
                    }else{
                        name.text= restaurantList[holder.adapterPosition].title.default.toString()
                    }

                    if (restaurantList[holder.adapterPosition].speciality!!.en.toString() != "null"){
                        speciality.text = restaurantList[holder.adapterPosition].speciality!!.en.toString()
                    }else{
                        speciality.text = restaurantList[holder.adapterPosition].speciality!!.default.toString()
                    }
                }

                "ar" -> {
                    if (restaurantList[holder.adapterPosition].title.ar.toString() != "null"){
                        name.text= restaurantList[holder.adapterPosition].title.ar.toString()
                    }else{
                        name.text= restaurantList[holder.adapterPosition].title.default.toString()
                    }

                    if (restaurantList[holder.adapterPosition].speciality!!.ar.toString() != "null"){
                        speciality.text = restaurantList[holder.adapterPosition].speciality!!.ar.toString()
                    }else{
                        speciality.text = restaurantList[holder.adapterPosition].speciality!!.default.toString()
                    }
                }

                "fr" -> {
                    if (restaurantList[holder.adapterPosition].title.fr.toString() != "null"){
                        name.text= restaurantList[holder.adapterPosition].title.fr.toString()
                    }else{
                        name.text= restaurantList[holder.adapterPosition].title.default.toString()
                    }

                    if (restaurantList[holder.adapterPosition].speciality!!.fr.toString() != "null"){
                        speciality.text = restaurantList[holder.adapterPosition].speciality!!.fr.toString()
                    }else{
                        speciality.text = restaurantList[holder.adapterPosition].speciality!!.default.toString()
                    }
                }

                else -> {
                    name.text= restaurantList[holder.adapterPosition].title.default.toString()
                    speciality.text = restaurantList[holder.adapterPosition].speciality!!.default.toString()
                }
            }


            val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
            //restaurantList[position].image?.let { image.setImageResource(it) }
            Glide.with(binding.root.context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/eatery_pictures/${restaurantList[holder.adapterPosition].id}_${restaurantList[holder.adapterPosition].image}?height=300")
                .into(image)
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("item", SerializableMenuAboutBooking(holder.adapterPosition))
            GlobalScope.launch {

                withContext(Dispatchers.Main) {
                    val clickClient = ClickClient(binding.root.context)
                    clickClient.createClick(
                        ResidenceClient(binding.root.context).getResidenceId().toString(),restaurantList!![position].id,
                        ServiceEnum.Eatery)
                }
            }

            val navController = Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.menuBookingAboutRestaurantDrinkFragment,bundle)
        }


    }

    override fun getItemCount() = restaurantList.size

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.item_animation)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }
}