package com.hellodati.launcher.ui.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hellodati.launcher.Phone_notif_listQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.databinding.ItemGlobalNotifBinding
import com.hellodati.launcher.databinding.ItemNotificationBinding
import com.hellodati.launcher.type.PhoneNotifTypeEnum
import com.hellodati.launcher.type.ReservationTypeEnum
import com.hellodati.launcher.ui.helper.LocalHelper
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ItemsNotificationAdapter(
    private var notificationArray: List<Phone_notif_listQuery.Phone_notif_list>,
    var context: Context
) :
    RecyclerView.Adapter<ItemsNotificationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemNotificationBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return notificationArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {

        holder.binding.dateTextView.text = notificationArray[i].createdAt.toString()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val time = LocalDateTime.parse(notificationArray[i].createdAt.toString(), formatter)
        val formattedDate = time.format(outputFormatter)
        holder.binding.dateTextView.text = formattedDate.toString()


        when (notificationArray[i].type) {
            PhoneNotifTypeEnum.order -> {
                holder.binding.contentTextView.text =
                    holder.binding.root.context.resources.getString(R.string.notif_order)
            }

            PhoneNotifTypeEnum.reservation -> {
                holder.binding.contentTextView.text =
                    holder.binding.root.context.resources.getString(R.string.notif_booking)
            }

            PhoneNotifTypeEnum.concierge_request_reject ->{

                    when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                        "en" -> {
                            holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileConciergeRequest!!.concierge!!.title.en + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                        }

                        "ar" -> {
                            holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileConciergeRequest!!.concierge!!.title.ar + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                        }

                        "fr" -> {
                            holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileConciergeRequest!!.concierge!!.title.fr + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                        }

                        else -> {
                            holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileConciergeRequest!!.concierge!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                        }
                    }
                    if (holder.binding.contentTextView.text.equals("null")){
                        holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileConciergeRequest!!.concierge!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                    }

            }
            PhoneNotifTypeEnum.concierge_request_accept->{

                    when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                        "en" -> {
                            holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileConciergeRequest!!.concierge!!.title.en + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                        }

                        "ar" -> {
                            holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileConciergeRequest!!.concierge!!.title.ar + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                        }

                        "fr" -> {
                            holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileConciergeRequest!!.concierge!!.title.fr + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                        }

                        else -> {
                            holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileConciergeRequest!!.concierge!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                        }
                    }
                    if (holder.binding.contentTextView.text.equals("null")){
                        holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileConciergeRequest!!.concierge!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                    }

            }
            PhoneNotifTypeEnum.resevation_reject -> {
                when (notificationArray[i].mobileReservation!!.reservationType){
                    ReservationTypeEnum.eateries -> {
                        when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                if (notificationArray[i].mobileReservation!!.eateryservice!!.title.en.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            "ar" -> {
                                if (notificationArray[i].mobileReservation!!.eateryservice!!.title.ar.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            "fr" -> {
                                if (notificationArray[i].mobileReservation!!.eateryservice!!.title.fr.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                            }
                        }
                    }
                    ReservationTypeEnum.events -> {
                        when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                if (notificationArray[i].mobileReservation!!.eventService!!.title.en.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eventService!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eventService!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            "ar" -> {
                                if (notificationArray[i].mobileReservation!!.meetingService!!.title.ar.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eventService!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eventService!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            "fr" -> {
                                if (notificationArray[i].mobileReservation!!.meetingService!!.title.fr.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eventService!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eventService!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eventService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                            }
                        }
                    }
                    ReservationTypeEnum.leisure -> {
                        when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                if (notificationArray[i].mobileReservation!!.leisureService!!.title.en.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            "ar" -> {
                                if (notificationArray[i].mobileReservation!!.meetingService!!.title.ar.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            "fr" -> {
                                if (notificationArray[i].mobileReservation!!.meetingService!!.title.fr.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                            }
                        }
                    }
                    ReservationTypeEnum.meeting -> {
                        when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                if (notificationArray[i].mobileReservation!!.meetingService!!.title.en.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.meetingService!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.meetingService!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            "ar" -> {
                                if (notificationArray[i].mobileReservation!!.meetingService!!.title.ar.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.meetingService!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.meetingService!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            "fr" -> {
                                if (notificationArray[i].mobileReservation!!.meetingService!!.title.fr.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.meetingService!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.meetingService!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.meetingService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                            }
                        }

                    }
                    ReservationTypeEnum.wellbeingitems -> {
                       /* when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                if (notificationArray[i].mobileReservation!!.wellBeingService!!.title.en.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + holder.binding.root.resources.getString(R.string.well_being) + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + holder.binding.root.resources.getString(R.string.well_being) + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            "ar" -> {
                                if (notificationArray[i].mobileReservation!!.wellBeingService!!.title.ar.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + holder.binding.root.resources.getString(R.string.well_being) + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + holder.binding.root.resources.getString(R.string.well_being) + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            "fr" -> {
                                if (notificationArray[i].mobileReservation!!.wellBeingService!!.title.fr.toString() == "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + holder.binding.root.resources.getString(R.string.well_being) + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + holder.binding.root.resources.getString(R.string.well_being) + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                                }
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + holder.binding.root.resources.getString(R.string.well_being) + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                            }
                        }*/
                        holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + holder.binding.root.resources.getString(R.string.well_being) + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_rjected_p2)
                    }
                    else -> {

                    }
                }
                holder.itemView.setOnClickListener {
                    val navController = Navigation.findNavController(
                        (holder.binding.root.context as Activity),
                        R.id.nav_host_fragment_content_main
                    )

                    navController.navigate(R.id.historyBookingFragment)
                }
            }


            PhoneNotifTypeEnum.resevation_accept -> {
                when (notificationArray[i].mobileReservation!!.reservationType){
                    ReservationTypeEnum.eateries -> {

                        when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                if (notificationArray[i].mobileReservation!!.eateryservice!!.title.en.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }
                            }

                            "ar" -> {
                                if (notificationArray[i].mobileReservation!!.eateryservice!!.title.ar.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.ar.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }
                            }

                            "fr" -> {
                                if (notificationArray[i].mobileReservation!!.eateryservice!!.title.fr.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.fr.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                            }
                        }
                    }
                    ReservationTypeEnum.events -> {
                        when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                if (notificationArray[i].mobileReservation!!.eventService!!.title.en.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eventService!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eventService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }
                            }

                            "ar" -> {
                                if (notificationArray[i].mobileReservation!!.meetingService!!.title.ar.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eventService!!.title.ar.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eventService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }
                            }

                            "fr" -> {
                                if (notificationArray[i].mobileReservation!!.meetingService!!.title.fr.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eventService!!.title.fr.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eventService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.eventService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                            }
                        }
                    }
                    ReservationTypeEnum.leisure -> {
                        when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                if (notificationArray[i].mobileReservation!!.leisureService!!.title.en.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }
                            }

                            "ar" -> {
                                if (notificationArray[i].mobileReservation!!.meetingService!!.title.ar.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.ar.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }
                            }

                            "fr" -> {
                                if (notificationArray[i].mobileReservation!!.meetingService!!.title.fr.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.fr.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                            }
                        }
                    }
                    ReservationTypeEnum.meeting -> {
                            when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                                "en" -> {
                                    if (notificationArray[i].mobileReservation!!.meetingService!!.title.en.toString() != "null"){
                                        holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.meetingService!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                    }else{
                                        holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.meetingService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                    }
                                }

                                "ar" -> {
                                    if (notificationArray[i].mobileReservation!!.meetingService!!.title.ar.toString() != "null"){
                                        holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.meetingService!!.title.ar.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                    }else{
                                        holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.meetingService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                    }
                                }

                                "fr" -> {
                                    if (notificationArray[i].mobileReservation!!.meetingService!!.title.fr.toString() != "null"){
                                        holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.meetingService!!.title.fr.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                    }else{
                                        holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.meetingService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                    }
                                }

                                else -> {
                                        holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.meetingService!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                                }
                            }

                    }
                    ReservationTypeEnum.wellbeingitems -> {
                        when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + holder.binding.root.resources.getString(R.string.well_being) + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                            }

                            "ar" -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + holder.binding.root.resources.getString(R.string.well_being) + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                            }

                            "fr" -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + holder.binding.root.resources.getString(R.string.well_being) + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + holder.binding.root.resources.getString(R.string.well_being) + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                            }
                        }
                    }
                    else -> {

                    }
                }

                holder.itemView.setOnClickListener {
                    val navController = Navigation.findNavController(
                        (holder.binding.root.context as Activity),
                        R.id.nav_host_fragment_content_main
                    )

                    navController.navigate(R.id.historyBookingFragment)
                }

            }

            PhoneNotifTypeEnum.order_delivered -> {
                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your_order) +" "+notificationArray[i].mobileOrder!!.orderNumber + " "+ holder.binding.root.context.resources.getString(R.string.notif_order_delivered)
            }

            PhoneNotifTypeEnum.order_in_delivery -> {
                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your_order) + " "+ notificationArray[i].mobileOrder!!.orderNumber + " "+holder.binding.root.context.resources.getString(R.string.notif_order_in_delivery)
            }

            PhoneNotifTypeEnum.order_in_progress -> {
                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your_order) +" "+ notificationArray[i].mobileOrder!!.orderNumber + " "+ holder.binding.root.context.resources.getString(R.string.notif_order_in_progress)
            }

            PhoneNotifTypeEnum.order_item_rejected -> {
                /*when (notificationArray[i].mobileReservation!!.reservationType){
                    ReservationTypeEnum.concierge -> {
                        when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.en + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                            }

                            "ar" -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.ar + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                            }

                            "fr" -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.fr + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                            }
                        }
                        if (holder.binding.contentTextView.text.equals("null")){
                            holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your) + " " + notificationArray[i].mobileReservation!!.leisureService!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_booking_accepted_p2)
                        }
                    }
                    ReservationTypeEnum.eateries -> {
                        when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.en.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            "ar" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.ar.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.ar.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            "fr" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.fr.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.fr.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                            }
                        }
                    }
                    ReservationTypeEnum.events -> {
                        when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.en.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            "ar" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.ar.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.ar.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            "fr" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.fr.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.fr.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                            }
                        }
                    }
                    ReservationTypeEnum.leisure -> {
                        when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.en.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            "ar" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.ar.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.ar.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            "fr" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.fr.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.fr.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                            }
                        }
                    }
                    ReservationTypeEnum.meeting -> {
                        when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.en.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            "ar" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.ar.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.ar.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            "fr" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.fr.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.fr.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                            }
                        }
                    }
                    ReservationTypeEnum.wellbeingitems -> {
                        when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                            "en" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.en.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            "ar" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.ar.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            "fr" -> {
                                if (notificationArray[i].mobileOrder!!.orderItems!![i].item!!.title.fr.toString() != "null"){
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.fr.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }else{
                                    holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                                }
                            }

                            else -> {
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileReservation!!.eateryservice!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                            }
                        }
                    }
                    else -> {

                    }
                }*/

                //val orderItems = notificationArray[i].mobileOrder!!.orderItems!!.size
                for ( j: Int in  notificationArray[i].mobileOrder!!.orderItems!!.indices){
                    Log.e("itemId", notificationArray[i].mobileOrder!!.orderItems!![j].item!!.title.default.toString())
                    when (LocalHelper.getLanguage(holder.binding.root.context).toString()) {
                        "en" -> {
                            if (notificationArray[i].mobileOrder!!.orderItems!![j].item!!.title.en.toString() == "null"){
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileOrder!!.orderItems!![j].item!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                            }else{
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileOrder!!.orderItems!![j].item!!.title.en.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                            }
                        }

                        "ar" -> {
                            if (notificationArray[i].mobileOrder!!.orderItems!![j].item!!.title.ar.toString() == "null"){
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileOrder!!.orderItems!![j].item!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                            }else{
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileOrder!!.orderItems!![j].item!!.title.ar.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                            }
                        }

                        "fr" -> {
                            if (notificationArray[i].mobileOrder!!.orderItems!![j].item!!.title.fr.toString() == "null"){
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileOrder!!.orderItems!![j].item!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                            }else{
                                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileOrder!!.orderItems!![j].item!!.title.fr.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                            }
                        }

                        else -> {
                            holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p1) + " " + notificationArray[i].mobileOrder!!.orderItems!![j].item!!.title.default.toString() + " " + holder.binding.root.context.resources.getString(R.string.notif_order_item_rejected_p2)
                        }
                    }
                }
            }

            PhoneNotifTypeEnum.order_rejected -> {
                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your_order) +" "+notificationArray[i].mobileOrder!!.orderNumber + " "+ holder.binding.root.context.resources.getString(R.string.notif_order_rejected)
            }

            PhoneNotifTypeEnum.order_ready -> {
                holder.binding.contentTextView.text = holder.binding.root.context.resources.getString(R.string.your_order) +" "+notificationArray[i].mobileOrder!!.orderNumber + " "+ holder.binding.root.context.resources.getString(R.string.notif_order_item_ready)
            }

            PhoneNotifTypeEnum.reset_passcode -> {
                holder.binding.contentTextView.text =
                    holder.binding.root.context.resources.getString(R.string.reset_password)
            }

            else -> {
                // holder.binding.contentTextView.text = ""
                //holder.binding.cardCategory.visibility = View.GONE
            }
            // Add more cases here if needed
        }

        //holder.binding.contentTextView.text = notificationArray[i].orderItemId.toString()


        try {

            val outputFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = LocalDateTime.parse(notificationArray[i].createdAt.toString(), formatter)
            val formattedDate2 = date.format(outputFormatter2)
            val currentDate = LocalDate.now()

            val comparisonResult = formattedDate2.compareTo(currentDate.toString())

            if (comparisonResult > 0) {
                holder.binding.newCard.visibility = View.GONE
            } else if (comparisonResult < 0) {
                // the formatted date is before the current date
                holder.binding.newCard.visibility = View.GONE
            } else {
                // the formatted date is the same as the current date
                holder.binding.newCard.visibility = View.VISIBLE
            }

        } catch (e: DateTimeParseException) {
            // handle the exception
        }

        holder.itemView.setOnClickListener {
            when (notificationArray[i].type) {
                PhoneNotifTypeEnum.order -> {
                    holder.itemView.setOnClickListener {
                        val navController = Navigation.findNavController(
                            (holder.binding.root.context as Activity),
                            R.id.nav_host_fragment_content_main
                        )

                        navController.navigate(R.id.orderHistoryFragment)
                    }
                }

                PhoneNotifTypeEnum.reservation -> {
                    holder.itemView.setOnClickListener {
                        val navController = Navigation.findNavController(
                            (holder.binding.root.context as Activity),
                            R.id.nav_host_fragment_content_main
                        )

                        navController.navigate(R.id.historyBookingFragment)
                    }
                }

                PhoneNotifTypeEnum.resevation_reject -> {
                    holder.itemView.setOnClickListener {
                        val navController = Navigation.findNavController(
                            (holder.binding.root.context as Activity),
                            R.id.nav_host_fragment_content_main
                        )

                        navController.navigate(R.id.historyBookingFragment)
                    }
                }

                PhoneNotifTypeEnum.resevation_accept -> {
                    holder.itemView.setOnClickListener {
                        val navController = Navigation.findNavController(
                            (holder.binding.root.context as Activity),
                            R.id.nav_host_fragment_content_main
                        )

                        navController.navigate(R.id.historyBookingFragment)
                    }

                }

                PhoneNotifTypeEnum.order_delivered -> {
                    holder.itemView.setOnClickListener {
                        val navController = Navigation.findNavController(
                            (holder.binding.root.context as Activity),
                            R.id.nav_host_fragment_content_main
                        )

                        navController.navigate(R.id.orderHistoryFragment)
                    }
                }

                PhoneNotifTypeEnum.order_in_delivery -> {
                    holder.itemView.setOnClickListener {
                        val navController = Navigation.findNavController(
                            (holder.binding.root.context as Activity),
                            R.id.nav_host_fragment_content_main
                        )

                        navController.navigate(R.id.orderHistoryFragment)
                    }
                }

                PhoneNotifTypeEnum.order_in_progress -> {
                    holder.itemView.setOnClickListener {
                        val navController = Navigation.findNavController(
                            (holder.binding.root.context as Activity),
                            R.id.nav_host_fragment_content_main
                        )

                        navController.navigate(R.id.orderHistoryFragment)
                    }
                }

                PhoneNotifTypeEnum.order_item_rejected -> {
                    holder.itemView.setOnClickListener {
                        val navController = Navigation.findNavController(
                            (holder.binding.root.context as Activity),
                            R.id.nav_host_fragment_content_main
                        )

                        navController.navigate(R.id.orderHistoryFragment)
                    }
                }

                PhoneNotifTypeEnum.order_rejected -> {
                    holder.itemView.setOnClickListener {
                        val navController = Navigation.findNavController(
                            (holder.binding.root.context as Activity),
                            R.id.nav_host_fragment_content_main
                        )

                        navController.navigate(R.id.orderHistoryFragment)
                    }
                }

                PhoneNotifTypeEnum.order_ready -> {
                    holder.itemView.setOnClickListener {
                        val navController = Navigation.findNavController(
                            (holder.binding.root.context as Activity),
                            R.id.nav_host_fragment_content_main
                        )

                        navController.navigate(R.id.orderHistoryFragment)
                    }
                }

                PhoneNotifTypeEnum.reset_passcode -> {
                    holder.binding.contentTextView.text =
                        holder.binding.root.context.resources.getString(R.string.reset_password)
                }

                else -> {
                    // holder.binding.contentTextView.text = ""
                    //holder.binding.cardCategory.visibility = View.GONE
                }
                // Add more cases here if needed
            }
 /*           if (notificationArray[i].type == PhoneNotifTypeEnum.reservation) {
                holder.itemView.setOnClickListener {
                    val navController = Navigation.findNavController(
                        (holder.binding.root.context as Activity),
                        R.id.nav_host_fragment_content_main
                    )

                    navController.navigate(R.id.historyBookingFragment)
                }
            }

            if (notificationArray[i].type == PhoneNotifTypeEnum.resevation_accept) {
                holder.itemView.setOnClickListener {
                    val navController = Navigation.findNavController(
                        (holder.binding.root.context as Activity),
                        R.id.nav_host_fragment_content_main
                    )

                    navController.navigate(R.id.orderHistoryActivity)
                }
            }*/




        }


    }


}