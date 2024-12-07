package com.hellodati.launcher.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.hellodati.launcher.Events_mobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.databinding.ItemEventBinding
import com.hellodati.launcher.ui.helper.LocalHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter



class ImageSlideAdapter(private val context: Context, private val eventList: List<Events_mobileQuery.Events_mobile>) : PagerAdapter() {
    override fun getCount(): Int {
        return eventList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
       // val view: View =  (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.item_event, null)
        val binding = ItemEventBinding.inflate(LayoutInflater.from(context), container, false)
        //val ivImages = view.findViewById<ImageView>(R.id.category_image)

        binding.apply {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val outputFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val date = LocalDateTime.parse(eventList[position].dateto.toString(), formatter)
            val formattedDate2 = date.format(outputFormatter2)


            when (LocalHelper.getLanguage(binding.root.context).toString()) {
                "en" -> {
                    if (eventList!![position].title.en.toString() != "null"){
                        eventName.text = eventList!![position].title.en.toString()
                    }else{
                        eventName.text = eventList!![position].title.default.toString()
                    }

                    if (eventList!![position].description.en.toString() != "null"){
                        eventDesc.text = eventList!![position].description.en.toString()
                    }else{
                        eventDesc.text = eventList!![position].description.default.toString()
                    }
                }

                "ar" -> {
                    if (eventList!![position].title.ar.toString() != "null"){
                        eventName.text = eventList!![position].title.ar.toString()
                    }else{
                        eventName.text = eventList!![position].title.default.toString()
                    }

                    if (eventList!![position].description.ar.toString() != "null"){
                        eventDesc.text = eventList!![position].description.ar.toString()
                    }else{
                        eventDesc.text = eventList!![position].description.default.toString()
                    }
                }

                "fr" -> {
                    if (eventList!![position].title.fr.toString() != "null"){
                        eventName.text = eventList!![position].title.fr.toString()
                    }else{
                        eventName.text = eventList!![position].title.default.toString()
                    }

                    if (eventList!![position].description.fr.toString() != "null"){
                        eventDesc.text = eventList!![position].description.fr.toString()
                    }else{
                        eventDesc.text = eventList!![position].description.default.toString()
                    }
                }

                else -> {
                    eventName.text = eventList[position].title.default.toString()
                    eventDesc.text = eventList[position].description.default.toString()
                }
            }

            val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
            txtOpeningTime.text = formattedDate2
            txtPhone.text = eventList[position].phone!!.number.toString()
            txtLocation.text = eventList[position].location
            contentPrice.text = "${eventList[position].price!!.amount.toInt()} ${eventList[position].price!!.currency}"
            Glide.with(binding.root.context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/event_pictures/${eventList!![position].id}_${eventList!![position].picture}?height=300")
                .into(categoryImage)
        }


        val vp = container as ViewPager
        vp.addView(binding.root, 0)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }
}