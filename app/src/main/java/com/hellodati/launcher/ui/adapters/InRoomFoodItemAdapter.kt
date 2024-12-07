package com.hellodati.launcher.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.hellodati.launcher.R
import com.hellodati.launcher.databinding.FragmentRestaurantAndDrinkBinding
import com.hellodati.launcher.databinding.ItemRecyclerMenuContentBinding
import com.hellodati.launcher.model.RestaurantModel

class InRoomFoodItemAdapter(
    private val restaurantList: List<RestaurantModel>,
    val binding: FragmentRestaurantAndDrinkBinding,
) :
    RecyclerView.Adapter<InRoomFoodItemAdapter.ViewHolder>() {
    private var lastPosition = -1
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRecyclerMenuContentBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_menu_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setAnimation(holder.itemView, position)
        holder.binding.apply {
            contentTitle.text= restaurantList[position].title
            restaurantList[position].image?.let { contentImage.setImageResource(it) }

        }

  /*      holder.itemView.setOnClickListener {
            binding.tabContent.visibility = View.GONE
            binding.tabs.visibility = View.VISIBLE
            binding.chosenContentLayout.visibility = View.VISIBLE

        }*/
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