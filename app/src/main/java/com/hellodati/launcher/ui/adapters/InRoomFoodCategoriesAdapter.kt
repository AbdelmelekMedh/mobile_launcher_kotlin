package com.hellodati.launcher.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellodati.launcher.Ird_menuQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ClickClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.InRoomFoodCategoryBinding
import com.hellodati.launcher.type.ServiceEnum
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InRoomFoodCategoriesAdapter(
    private val collections: List<Ird_menuQuery.Ird_menu>,
    private val context: Context,
    private val isFood: Boolean
) :
    RecyclerView.Adapter<InRoomFoodCategoriesAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = InRoomFoodCategoryBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.in_room_food_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val collection = collections[position]

            when (LocalHelper.getLanguage(context).toString()) {
                "en" -> {
                    categoryName.text = collection.title.en.toString()
                }

                "ar" -> {
                    categoryName.text = collection.title.ar.toString()
                }

                "fr" -> {
                    categoryName.text = collection.title.fr.toString()
                }

                else -> {
                    categoryName.text = collection.title.default.toString()
                }
            }

            if (categoryName.text.equals("null")){
                categoryName.text = collection.title.default.toString()
            }

            val hotelLinksPreferences = context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
            Glide.with(context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/irdCategory_pictures/${collection.id}_${collection.image}?height=300")
                .into(categoryImage)

            rvSubItem.adapter = DishRestaurantAdapter(collection.items, context,position,isFood)
            rvSubItem.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            if (collection.items!!.isEmpty()) {
                arrowDown.visibility = View.GONE
                arrowUp.visibility = View.GONE
            }
            categoryImage.setOnClickListener {
                if (collection.items!!.isNotEmpty()) {

                    rvSubItem.visibility = if (rvSubItem.isShown) View.GONE else View.VISIBLE
                    arrowDown.visibility = if (rvSubItem.isShown) View.GONE else View.VISIBLE
                    arrowUp.visibility = if (!rvSubItem.isShown) View.GONE else View.VISIBLE

                    GlobalScope.launch {
                        withContext(Dispatchers.Main) {
                            val clickClient = ClickClient(context)
                            clickClient.createClick(
                                ResidenceClient(context).getResidenceId().toString(),collections!![position].id,
                                ServiceEnum.IrdEatCategory)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount() = collections.size
}