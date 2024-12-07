package com.hellodati.launcher.ui.adapters

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellodati.launcher.Events_mobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.databinding.FragmentEventBinding
import com.hellodati.launcher.databinding.FragmentMenuEventBinding
import com.hellodati.launcher.databinding.ItemEventBinding
import com.hellodati.launcher.ui.helper.LocalHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventAdapter(
    private val categoryDrinkList: List<Events_mobileQuery.Events_mobile>,
    val binding: FragmentMenuEventBinding,
    val bindingEvent: FragmentEventBinding,
) :
    RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemEventBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val outputFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val date = LocalDateTime.parse(categoryDrinkList[position].dateto.toString(), formatter)
            val formattedDate2 = date.format(outputFormatter2)


            when (LocalHelper.getLanguage(binding.root.context).toString()) {
                "en" -> {
                    if (categoryDrinkList!![position].title.en.toString() != "null"){
                        eventName.text = categoryDrinkList!![position].title.en.toString()
                    }else{
                        eventName.text = categoryDrinkList!![position].title.default.toString()
                    }

                    if (categoryDrinkList!![position].description.en.toString() != "null"){
                        eventDesc.text = categoryDrinkList!![position].description.en.toString()
                    }else{
                        eventDesc.text = categoryDrinkList!![position].description.default.toString()
                    }
                }

                "ar" -> {
                    if (categoryDrinkList!![position].title.ar.toString() != "null"){
                        eventName.text = categoryDrinkList!![position].title.ar.toString()
                    }else{
                        eventName.text = categoryDrinkList!![position].title.default.toString()
                    }

                    if (categoryDrinkList!![position].description.ar.toString() != "null"){
                        eventDesc.text = categoryDrinkList!![position].description.ar.toString()
                    }else{
                        eventDesc.text = categoryDrinkList!![position].description.default.toString()
                    }
                }

                "fr" -> {
                    if (categoryDrinkList!![position].title.fr.toString() != "null"){
                        eventName.text = categoryDrinkList!![position].title.fr.toString()
                    }else{
                        eventName.text = categoryDrinkList!![position].title.default.toString()
                    }

                    if (categoryDrinkList!![position].description.fr.toString() != "null"){
                        eventDesc.text = categoryDrinkList!![position].description.fr.toString()
                    }else{
                        eventDesc.text = categoryDrinkList!![position].description.default.toString()
                    }
                }

                else -> {
                    eventName.text = categoryDrinkList[position].title.default.toString()
                    eventDesc.text = categoryDrinkList[position].description.default.toString()
                }
            }

            val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
            txtOpeningTime.text = formattedDate2
            txtPhone.text = categoryDrinkList[position].phone!!.number.toString()
            txtLocation.text = categoryDrinkList[position].location
            contentPrice.text = "${categoryDrinkList[position].price!!.amount.toInt()} ${categoryDrinkList[position].price!!.currency}"
                Glide.with(binding.root.context)
                    .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/event_pictures/${categoryDrinkList!![position].id}_${categoryDrinkList!![position].picture}?height=300")
                    .into(categoryImage)
        }

        holder.binding.btnBooking.setOnClickListener {
   /*         val bookingEventFragment =
                BookingEventFragment.newInstance("${categoryDrinkList[position].id}", "")
            val fragmentManager =
                (holder.itemView.context as AppCompatActivity).supportFragmentManager
            bindingEvent.tabContent.removeAllViews()
            fragmentManager.beginTransaction()
                .replace(bindingEvent.tabContent.id, bookingEventFragment)
                .addToBackStack(null)
                .commit()*/

            val bundle = Bundle()
            bundle.putString("param1", categoryDrinkList[position].id)
            bundle.putString("param2", categoryDrinkList[position].dateto.toString())

            val navController = Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            )

            navController.navigate(R.id.bookingEventFragment, bundle)
        }
        /*        holder.itemView.setOnClickListener {
                    binding.chosenContentLayout.visibility = View.VISIBLE
                    val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                        binding.btnCancel,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f)
                    )
                    scaleDown.duration = 300
                    binding.recyclerBeverage.visibility = View.VISIBLE
                    binding.btnOrder.visibility = View.GONE
                    binding.btnCancel.visibility = View.GONE
                    binding.recyclerBeverage.layoutManager = GridLayoutManager(holder.itemView.context,2)
                    binding.recyclerBeverage.adapter = BeverageDrinkAdapter(categoryDrinkList[position].items,binding,fragmentDrinksBinding)
                    *//*  binding.chosenContentLayout.visibility = View.VISIBLE
              binding.menuRestaurantFrame.visibility = View.GONE*//*
        }*/
    }

    override fun getItemCount() = categoryDrinkList.size
}