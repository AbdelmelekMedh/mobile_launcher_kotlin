package com.hellodati.launcher.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hellodati.launcher.GetMobileReservationsQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.databinding.FragmentHistoryBookingBinding
import com.hellodati.launcher.databinding.ItemHistoryBookingBinding
import com.hellodati.launcher.type.ReservationStatusEnum
import com.hellodati.launcher.type.ReservationTypeEnum
import com.hellodati.launcher.ui.helper.LocalHelper
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HistoryBookingAdapter(
    private val dishRestaurantModel: List<GetMobileReservationsQuery.GetMobileReservation>,
    val binding: FragmentHistoryBookingBinding
) :
    RecyclerView.Adapter<HistoryBookingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemHistoryBookingBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history_booking, parent, false)
        /*  val animation = AnimationUtils.loadAnimation(parent.context, R.anim.item_animation)
          view.startAnimation(animation)*/
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            quantity.text= " - ${dishRestaurantModel[position].numberOfPerson.toInt().toString()} "+binding.root.resources.getString(R.string.persons)
            dateAndTime.text= binding.root.resources.getString(R.string.reservation_number) +" "+ dishRestaurantModel[position].ReservationNumber

       /*     try {
                title.text= dishRestaurantModel[position].leisureService!!.title.default.toString()
            }catch (e:Exception){
                Log.e("rrt",e.message.toString())
            }*/

          try {
              when (dishRestaurantModel[position].reservationType){
                  ReservationTypeEnum.concierge -> {
                      when (LocalHelper.getLanguage(binding.root.context).toString()) {
                          "en" -> {
                              title.text= dishRestaurantModel[position].leisureService!!.title.en.toString()
                          }

                          "ar" -> {
                              title.text= dishRestaurantModel[position].leisureService!!.title.ar.toString()
                          }

                          "fr" -> {
                              title.text= dishRestaurantModel[position].leisureService!!.title.fr.toString()
                          }

                          else -> {
                              title.text= dishRestaurantModel[position].leisureService!!.title.default.toString()
                          }
                      }
                      if (title.text.equals("null")){
                          title.text= dishRestaurantModel[position].leisureService!!.title.default.toString()
                      }
                  }
                  ReservationTypeEnum.eateries -> {
                      when (LocalHelper.getLanguage(binding.root.context).toString()) {
                          "en" -> {
                              if (dishRestaurantModel[position].eateryservice!!.title.en.toString() != "null"){
                                  title.text= dishRestaurantModel[position].eateryservice!!.title.en.toString()
                              }else{
                                  title.text= dishRestaurantModel[position].eateryservice!!.title.default.toString()
                              }
                          }

                          "ar" -> {
                              if (dishRestaurantModel[position].eateryservice!!.title.ar.toString() != "null"){
                                  title.text= dishRestaurantModel[position].eateryservice!!.title.ar.toString()
                              }else{
                                  title.text= dishRestaurantModel[position].eateryservice!!.title.default.toString()
                              }
                          }

                          "fr" -> {
                              if (dishRestaurantModel[position].eateryservice!!.title.fr.toString() != "null"){
                                  title.text= dishRestaurantModel[position].eateryservice!!.title.fr.toString()
                              }else{
                                  title.text= dishRestaurantModel[position].eateryservice!!.title.default.toString()
                              }
                          }

                          else -> {
                              title.text= dishRestaurantModel[position].eateryservice!!.title.default.toString()
                          }
                      }
                  }
                  ReservationTypeEnum.events -> {
                      when (LocalHelper.getLanguage(binding.root.context).toString()) {
                          "en" -> {
                              if (dishRestaurantModel[position].eventService!!.title.en.toString() != "null"){
                                  title.text= dishRestaurantModel[position].eventService!!.title.en.toString()
                              }else{
                                  title.text= dishRestaurantModel[position].eventService!!.title.default.toString()
                              }
                          }

                          "ar" -> {
                              if (dishRestaurantModel[position].eventService!!.title.ar.toString() != "null"){
                                  title.text= dishRestaurantModel[position].eventService!!.title.ar.toString()
                              }else{
                                  title.text= dishRestaurantModel[position].eventService!!.title.default.toString()
                              }
                          }

                          "fr" -> {
                              if (dishRestaurantModel[position].eventService!!.title.fr.toString() != "null"){
                                  title.text= dishRestaurantModel[position].eventService!!.title.fr.toString()
                              }else{
                                  title.text= dishRestaurantModel[position].eventService!!.title.default.toString()
                              }
                          }

                          else -> {
                              title.text= dishRestaurantModel[position].eventService!!.title.default.toString()
                          }
                      }

                  }
                  ReservationTypeEnum.leisure -> {
                      when (LocalHelper.getLanguage(binding.root.context).toString()) {
                          "en" -> {
                              if (dishRestaurantModel[position].leisureService!!.title.en.toString() != "null"){
                                  title.text= dishRestaurantModel[position].leisureService!!.title.en.toString()
                              }else{
                                  title.text= dishRestaurantModel[position].leisureService!!.title.default.toString()
                              }
                          }

                          "ar" -> {
                              if (dishRestaurantModel[position].leisureService!!.title.ar.toString() != "null"){
                                  title.text= dishRestaurantModel[position].leisureService!!.title.ar.toString()
                              }else{
                                  title.text= dishRestaurantModel[position].leisureService!!.title.default.toString()
                              }
                          }

                          "fr" -> {
                              if (dishRestaurantModel[position].leisureService!!.title.fr.toString() != "null"){
                                  title.text= dishRestaurantModel[position].leisureService!!.title.fr.toString()
                              }else{
                                  title.text= dishRestaurantModel[position].leisureService!!.title.default.toString()
                              }
                          }

                          else -> {
                              title.text= dishRestaurantModel[position].leisureService!!.title.default.toString()
                          }
                      }

                  }
                  ReservationTypeEnum.meeting -> {
                      when (LocalHelper.getLanguage(binding.root.context).toString()) {
                          "en" -> {
                              if (dishRestaurantModel[position].meetingService!!.title.en.toString() != "null"){
                                  title.text= dishRestaurantModel[position].meetingService!!.title.en.toString()
                              }else{
                                  title.text= dishRestaurantModel[position].meetingService!!.title.default.toString()
                              }
                          }

                          "ar" -> {
                              if (dishRestaurantModel[position].leisureService!!.title.ar.toString() != "null"){
                                  title.text= dishRestaurantModel[position].meetingService!!.title.ar.toString()
                              }else{
                                  title.text= dishRestaurantModel[position].meetingService!!.title.default.toString()
                              }
                          }

                          "fr" -> {
                              if (dishRestaurantModel[position].meetingService!!.title.fr.toString() != "null"){
                                  title.text= dishRestaurantModel[position].meetingService!!.title.fr.toString()
                              }else{
                                  title.text= dishRestaurantModel[position].meetingService!!.title.default.toString()
                              }
                          }

                          else -> {
                              title.text= dishRestaurantModel[position].meetingService!!.title.default.toString()
                          }
                      }

                  }
                  ReservationTypeEnum.wellbeingitems -> {
                      when (LocalHelper.getLanguage(binding.root.context).toString()) {
                          "en" -> {
                              title.text= binding.root.resources.getString(R.string.well_being)
                          }

                          "ar" -> {
                              title.text= binding.root.resources.getString(R.string.well_being)
                          }

                          "fr" -> {
                              title.text= binding.root.resources.getString(R.string.well_being)
                          }

                          else -> {
                              title.text= binding.root.resources.getString(R.string.well_being)
                          }
                      }
                  }
                  else -> {

                  }
              }
          }catch (e:Exception){
              Log.e("notif",e.message.toString())
          }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val outputFormatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val time = LocalDateTime.parse(dishRestaurantModel[position].date.toString(), formatter)
            val day = LocalDateTime.parse(dishRestaurantModel[position].date.toString(), formatter)
            date.text= time.format(outputFormatter) +" "+day.format(outputFormatterDate)
            when (dishRestaurantModel[position].status){
                ReservationStatusEnum.Accepted -> {
                    status.text= binding.root.resources.getString(R.string.accepted)
                    status.setTextColor(binding.root.context.resources.getColor(R.color.success))
                }
                ReservationStatusEnum.Waiting -> {
                    status.text= binding.root.resources.getString(R.string.waiting)
                    status.setTextColor(binding.root.context.resources.getColor(R.color.status_warning))
                }
                ReservationStatusEnum.Rejected ->{
                    status.text= binding.root.resources.getString(R.string.rejected)
                    status.setTextColor(binding.root.context.resources.getColor(R.color.near_boston_university_red))
                }
                else -> {
                    status.text= dishRestaurantModel[position].status.toString()
                    status.setTextColor(binding.root.context.resources.getColor(R.color.light_blue))
                }
            }
        }

    }

    override fun getItemCount() = dishRestaurantModel.size
}