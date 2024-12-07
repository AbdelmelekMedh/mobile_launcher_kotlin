package com.hellodati.launcher.ui.adapters

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hellodati.launcher.Leisures_mobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ClickClient
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.LeisureClient
import com.hellodati.launcher.api.ReservationClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.DialogPasscodeBinding
import com.hellodati.launcher.databinding.FragmentBottomSheetEventBinding
import com.hellodati.launcher.databinding.FragmentBottomSheetLeisureBinding
import com.hellodati.launcher.databinding.FragmentLeisureBinding
import com.hellodati.launcher.databinding.FragmentMenuLeisureBinding
import com.hellodati.launcher.databinding.ItemLeisureBinding
import com.hellodati.launcher.serializable_data.SerializableLeisure
import com.hellodati.launcher.type.CreateReservationInput
import com.hellodati.launcher.type.ReservationTypeEnum
import com.hellodati.launcher.type.ServiceEnum
import com.hellodati.launcher.ui.fragment.BasketFragment
import com.hellodati.launcher.ui.fragment.BottomSheetLeisureFragment
import com.hellodati.launcher.ui.fragment.myCalendar
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.floor

class LeisureAdapter(
    private val leisureList: List<Leisures_mobileQuery.Leisures_mobile>,
    val binding: FragmentMenuLeisureBinding,
) :
    RecyclerView.Adapter<LeisureAdapter.ViewHolder>() {
    private var nbr: Int? = 1
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemLeisureBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leisure, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            when (LocalHelper.getLanguage(binding.root.context).toString()) {
                "en" -> {
                    if (leisureList!![position].title.en.toString() != "null"){
                        contentTitle.text = leisureList!![position].title.en.toString()
                    }else{
                        contentTitle.text = leisureList!![position].title.default.toString()
                    }

                    if (leisureList!![position].description.en.toString() != "null"){
                        contentDescription.text = leisureList!![position].description.en.toString()
                    }else{
                        contentDescription.text = leisureList!![position].description.default.toString()
                    }

                }

                "ar" -> {
                    if (leisureList!![position].title.ar.toString() != "null"){
                        contentTitle.text = leisureList!![position].title.ar.toString()
                    }else{
                        contentTitle.text = leisureList!![position].title.default.toString()
                    }

                    if (leisureList!![position].description.ar.toString() != "null"){
                        contentDescription.text = leisureList!![position].description.ar.toString()
                    }else{
                        contentDescription.text = leisureList!![position].description.default.toString()
                    }
                }

                "fr" -> {
                    if (leisureList!![position].title.fr.toString() != "null"){
                        contentTitle.text = leisureList!![position].title.fr.toString()
                    }else{
                        contentTitle.text = leisureList!![position].title.default.toString()
                    }

                    if (leisureList!![position].description.fr.toString() != "null"){
                        contentDescription.text = leisureList!![position].description.fr.toString()
                    }else{
                        contentDescription.text = leisureList!![position].description.default.toString()
                    }
                }

                else -> {
                    contentTitle.text= leisureList!![position].title.default.toString()
                    contentDescription.text = leisureList!![position].description.default.toString()
                }
            }



            //contentPrice.text = "${leisureList!![position].price!!.amount.toInt()} ${leisureList!![position].price!!.currency}"

            if(leisureList!![position].price!!.hasDiscount == true){
                if (leisureList!![position].price!!.discountType.toString() == "percentege"){
                    val discountAmount : Double = leisureList!![position].price!!.amount - ((leisureList!![position].price!!.discountAmount!! * leisureList!![position].price!!.amount) / 100.0)
                    if(discountAmount == floor(discountAmount)){
                        contentPrice.text = "${discountAmount.toInt()} ${leisureList!![position].price!!.currency}"
                    }else{
                        contentPrice.text = "$discountAmount ${leisureList!![position].price!!.currency}"
                    }
                    if(leisureList!![position].price!!.amount == floor(leisureList!![position].price!!.amount)){
                        contentOldprice.text = "${leisureList!![position].price!!.amount.toInt()} ${leisureList!![position].price!!.currency}"
                    }else{
                        contentOldprice.text = "${leisureList!![position].price!!.amount} ${leisureList!![position].price!!.currency}"
                    }

                    contentOldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }else{
                    val discountAmount = leisureList!![position].price!!.amount - leisureList!![position].price!!.discountAmount!!
                    if(discountAmount == floor(discountAmount)){
                        contentPrice.text = "${discountAmount.toInt()} ${leisureList!![position].price!!.currency}"
                    }else{
                        contentPrice.text = "$discountAmount ${leisureList!![position].price!!.currency}"
                    }
                    if(leisureList!![position].price!!.amount == floor(leisureList!![position].price!!.amount)){
                        contentOldprice.text = "${leisureList!![position].price!!.amount.toInt()} ${leisureList!![position].price!!.currency}"
                    }else{
                        contentOldprice.text = "${leisureList!![position].price!!.amount} ${leisureList!![position].price!!.currency}"
                    }
                    contentOldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
            }else{
                if(leisureList!![position].price!!.amount == floor(leisureList!![position].price!!.amount)){
                    contentPrice.text = "${leisureList!![position].price!!.amount.toInt()} ${leisureList!![position].price!!.currency}"
                }else{
                    contentPrice.text = "${leisureList!![position].price!!.amount} ${leisureList!![position].price!!.currency}"
                }
            }

            val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
            Glide.with(binding.root.context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/leisure_pictures/${leisureList!![position].id}_${leisureList!![position].image}?height=300")
                .into(contentImage)

            txtOpeningTime.text = "${binding.root.context.resources.getString(R.string.openning_time_from)} ${leisureList!![position].from} ${binding.root.context.resources.getString(R.string.openning_time_to)} ${leisureList!![position].to}"

        }

        holder.itemView.setOnClickListener {

            GlobalScope.launch {

                withContext(Dispatchers.Main) {
                    val clickClient = ClickClient(binding.root.context)
                    clickClient.createClick(
                        ResidenceClient(binding.root.context).getResidenceId().toString(),leisureList!![position].id,
                        ServiceEnum.Leisure)
                }
            }
            /*val bundle = Bundle()
            bundle.putSerializable("item",SerializableLeisure(position))

            val navController = Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.globalLeisureFragment, bundle)*/

            val bottomSheetFragment = BottomSheetLeisureFragment.newInstance(SerializableLeisure(position))
            bottomSheetFragment.show(
                (binding.root.context as FragmentActivity).supportFragmentManager,
                bottomSheetFragment.tag
            )
        }
    }
    override fun getItemCount() = leisureList.size

}