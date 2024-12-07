package com.hellodati.launcher.ui.adapters

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
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
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hellodati.launcher.Meeting_mobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ClickClient
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.MeetingClient
import com.hellodati.launcher.api.ReservationClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.DialogPasscodeBinding
import com.hellodati.launcher.databinding.FragmentBottomSheetMeetingBinding
import com.hellodati.launcher.databinding.FragmentMeetingBinding
import com.hellodati.launcher.databinding.ItemMeetBinding
import com.hellodati.launcher.serializable_data.SerializableMeeting
import com.hellodati.launcher.type.CreateReservationInput
import com.hellodati.launcher.type.ReservationTypeEnum
import com.hellodati.launcher.type.ServiceEnum
import com.hellodati.launcher.ui.fragment.BasketFragment
import com.hellodati.launcher.ui.fragment.BottomSheetMeetingFragment
import com.hellodati.launcher.ui.fragment.myCalendar
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.floor

class MeetingAdapter (
    private val meetingList: List<Meeting_mobileQuery.Meeting_mobile>,
    val binding: FragmentMeetingBinding,
) :
    RecyclerView.Adapter<MeetingAdapter.ViewHolder>() {
    private var nbr: Int? = 1
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemMeetBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {


            when (LocalHelper.getLanguage(binding.root.context).toString()) {
                "en" -> {
                    roomName.text= meetingList!![position].title.en
                }

                "ar" -> {
                    roomName.text= meetingList!![position].title.ar
                }

                "fr" -> {
                    roomName.text= meetingList!![position].title.fr
                }

                else -> {
                    roomName.text= meetingList!![position].title.default
                }
            }

            if (roomName.text.isNullOrEmpty()){
                roomName.text= meetingList!![position].title.default
            }

            roomNbrPerson.text = binding.root.context.getString(R.string.capacity)+" "+ meetingList!![position].capacity.toInt().toString()+" "+ binding.root.context.getString(R.string.guests)

            val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
            Glide.with(binding.root.context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/meeting_pictures/${meetingList!![position].id}_${meetingList!![position].picture}?height=300")
                .into(roomImage)

            if(meetingList!![position].price!!.hasDiscount == true){
                if (meetingList!![position].price!!.discountType.toString() == "percentege"){
                    val discountAmount : Double = meetingList!![position].price!!.amount - ((meetingList!![position].price!!.discountAmount!! * meetingList!![position].price!!.amount) / 100.0)
                    if(discountAmount == floor(discountAmount)){
                        price.text = "${discountAmount.toInt()} ${meetingList!![position].price!!.currency}"
                    }else{
                        price.text = "$discountAmount ${meetingList!![position].price!!.currency}"
                    }
                    if(meetingList!![position].price!!.amount == floor(meetingList!![position].price!!.amount)){
                        oldprice.text = "${meetingList!![position].price!!.amount.toInt()} ${meetingList!![position].price!!.currency}"
                    }else{
                        oldprice.text = "${meetingList!![position].price!!.amount} ${meetingList!![position].price!!.currency}"
                    }

                    oldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }else{
                    val discountAmount = meetingList!![position].price!!.amount - meetingList!![position].price!!.discountAmount!!
                    if(discountAmount == floor(discountAmount)){
                        price.text = "${discountAmount.toInt()} ${meetingList!![position].price!!.currency}"
                    }else{
                        price.text = "$discountAmount ${meetingList!![position].price!!.currency}"
                    }
                    if(meetingList!![position].price!!.amount == floor(meetingList!![position].price!!.amount)){
                        oldprice.text = "${meetingList!![position].price!!.amount.toInt()} ${meetingList!![position].price!!.currency}"
                    }else{
                        oldprice.text = "${meetingList!![position].price!!.amount} ${meetingList!![position].price!!.currency}"
                    }
                    oldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
            }else{
                if(meetingList!![position].price!!.amount == floor(meetingList!![position].price!!.amount)){
                    price.text = "${meetingList!![position].price!!.amount.toInt()} ${meetingList!![position].price!!.currency}"
                }else{
                    price.text = "${meetingList!![position].price!!.amount} ${meetingList!![position].price!!.currency}"
                }
            }

        }

        holder.itemView.setOnClickListener {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    val clickClient = ClickClient(binding.root.context)
                    clickClient.createClick(
                        ResidenceClient(binding.root.context).getResidenceId().toString(),meetingList!![position].id,
                        ServiceEnum.Meetings)
                }
            }
            /*val bundle = Bundle()
            bundle.putSerializable("item", SerializableMeeting(position) )
            val navController = Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.globalMeetingFragment,bundle)*/

            val bottomSheetFragment = BottomSheetMeetingFragment.newInstance(SerializableMeeting(position))
            bottomSheetFragment.show(
                (binding.root.context as FragmentActivity).supportFragmentManager,
                bottomSheetFragment.tag
            )

        }
    }

    override fun getItemCount() = meetingList!!.size

}