package com.hellodati.launcher.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellodati.launcher.EateriesMobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ClickClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.FragmentMenuBookingAboutRestaurantDrinkBinding
import com.hellodati.launcher.databinding.FragmentMenuRestaurantDrinkBinding
import com.hellodati.launcher.databinding.FragmentRestaurantAndDrinkBinding
import com.hellodati.launcher.databinding.InRoomFoodCategoryBinding
import com.hellodati.launcher.type.ServiceEnum
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryRestaurantDrinkAdapter(
    private val collections: List<EateriesMobileQuery.Category>?,
    var binding: FragmentMenuRestaurantDrinkBinding,
    var restaurantList: Int,
) : RecyclerView.Adapter<CategoryRestaurantDrinkAdapter.CollectionsViewHolder>() {

    lateinit var context: Context

    class CollectionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = InRoomFoodCategoryBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.in_room_food_category, parent, false)
        context = parent.context
        return CollectionsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionsViewHolder, position: Int) {
        holder.binding.apply {
            //val collection = collections[position]

            when (LocalHelper.getLanguage(binding.root.context).toString()) {
                "en" -> {
                    if (collections!![position].title.en.toString() != "null"){
                        categoryName.text = collections!![position].title.en.toString()
                    }else{
                        categoryName.text = collections!![position].title.default.toString()
                    }
                }

                "ar" -> {
                    if (collections!![position].title.en.toString() != "null"){
                        categoryName.text = collections!![position].title.ar.toString()
                    }else{
                        categoryName.text = collections!![position].title.default.toString()
                    }
                }

                "fr" -> {
                    if (collections!![position].title.en.toString() != "null"){
                        categoryName.text = collections!![position].title.fr.toString()
                    }else{
                        categoryName.text = collections!![position].title.default.toString()
                    }
                }

                else -> {
                    categoryName.text = collections!![position].title.default.toString()
                }
            }

            val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)

            Glide.with(binding.root.context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/eatery_category_pictures/${collections[holder.adapterPosition].id}_${collections[holder.adapterPosition].image}?height=200")
                .into(categoryImage)
            val dishRestaurantAdapter = ItemRestaurantDrinkAdapter(collections!![position].items, binding,restaurantList,position)
            rvSubItem.adapter = dishRestaurantAdapter
            rvSubItem.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            categoryImage.setOnClickListener {
                GlobalScope.launch {

                    withContext(Dispatchers.Main) {
                        val clickClient = ClickClient(binding.root.context)
                        clickClient.createClick(
                            ResidenceClient(binding.root.context).getResidenceId().toString(),collections!![position].id,
                            ServiceEnum.EateryCategory)
                    }
                }
                if (collections!![position].items!!.isNotEmpty()) {
                    rvSubItem.visibility = if (rvSubItem.isShown) View.GONE else View.VISIBLE
                    arrowDown.visibility = if (rvSubItem.isShown) View.GONE else View.VISIBLE
                    arrowUp.visibility = if (!rvSubItem.isShown) View.GONE else View.VISIBLE

                }

            }
        }
    }

    override fun getItemCount() = collections!!.size
}