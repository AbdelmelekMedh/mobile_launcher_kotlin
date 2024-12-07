package com.hellodati.launcher.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellodati.launcher.R
import com.hellodati.launcher.WellBeing_mobileQuery
import com.hellodati.launcher.api.ClickClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.FragmentMenuWellBeingBinding
import com.hellodati.launcher.databinding.InRoomFoodCategoryBinding
import com.hellodati.launcher.type.ServiceEnum
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryWellBeingAdapter(
    private val collections: List<WellBeing_mobileQuery.WellBeing_mobile>,
    var binding: FragmentMenuWellBeingBinding
) :
    RecyclerView.Adapter<CategoryWellBeingAdapter.CollectionsViewHolder>() {

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
            val collection = collections[position]

            when (LocalHelper.getLanguage(binding.root.context).toString()) {
                "en" -> {
                    if (collection.title.en.toString() != "null"){
                        categoryName.text = collection.title.en.toString()
                    }else{
                        categoryName.text = collection.title.default.toString()
                    }
                }

                "ar" -> {
                    if (collection.title.ar.toString() != "null"){
                        categoryName.text = collection.title.ar.toString()
                    }else{
                        categoryName.text = collection.title.default.toString()
                    }
                }

                "fr" -> {
                    if (collection.title.fr.toString() != "null"){
                        categoryName.text = collection.title.fr.toString()
                    }else{
                        categoryName.text = collection.title.default.toString()
                    }
                }

                else -> {
                    categoryName.text = collection.title.default.toString()
                }
            }

            val hotelLinksPreferences = context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
            Glide.with(binding.root.context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/well_being_category_pictures/${collection.id}_${collection.image}?height=300")
                .into(categoryImage)

            val itemWellBeingAdapter = ItemWellBeingAdapter(collection.items, binding)
            rvSubItem.adapter = itemWellBeingAdapter
            rvSubItem.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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
                            val clickClient = ClickClient(binding.root.context)
                            clickClient.createClick(
                                ResidenceClient(binding.root.context).getResidenceId().toString(),collections!![position].id,
                                ServiceEnum.WellbeingCategory)
                        }
                    }
                }

            }
        }
    }

    override fun getItemCount() = collections.size
}