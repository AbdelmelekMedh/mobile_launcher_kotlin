package com.hellodati.launcher.ui.adapters

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellodati.launcher.Ird_menuQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ClickClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.FragmentDrinksBinding
import com.hellodati.launcher.databinding.ItemCategoryDrinkBinding
import com.hellodati.launcher.serializable_data.SerializableInRoomDrink
import com.hellodati.launcher.type.ServiceEnum
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CategoryDrinkAdapter(
    private val categoryDrinkList: List<Ird_menuQuery.Ird_menu>,
    val binding: FragmentDrinksBinding,
) :
    RecyclerView.Adapter<CategoryDrinkAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemCategoryDrinkBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_drink, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
            when (LocalHelper.getLanguage(binding.root.context).toString()) {
                "en" -> {
                    categoryName.text= categoryDrinkList[position].title.en
                }

                "ar" -> {
                    categoryName.text= categoryDrinkList[position].title.ar
                }

                "fr" -> {
                    categoryName.text= categoryDrinkList[position].title.fr
                }

                else -> {
                    categoryName.text= categoryDrinkList[position].title.default
                }
            }

            if (categoryName.text.isNullOrEmpty()){
                categoryName.text= categoryDrinkList!![position].title.default
            }

            Glide.with(binding.root.context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/irdCategory_pictures/${categoryDrinkList!![position].id}_${categoryDrinkList!![position].image}?height=300")
                .into(categoryImage)
        }

        holder.itemView.setOnClickListener {
            GlobalScope.launch {

                withContext(Dispatchers.Main) {
                    val clickClient = ClickClient(binding.root.context)
                    clickClient.createClick(
                        ResidenceClient(binding.root.context).getResidenceId().toString(),categoryDrinkList!![position].id,
                        ServiceEnum.IrdDrinksCategory)
                }
            }
            val bundle = Bundle()
            bundle.putSerializable("item", SerializableInRoomDrink(position))
            val navController = Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.inRoomDrinkFragment,bundle)
        }
    }

    override fun getItemCount() = categoryDrinkList.size
}