package com.hellodati.launcher.ui.adapters

import android.content.Context
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hellodati.launcher.Phone_notif_listQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.databinding.ItemGlobalNotifBinding
import java.time.LocalDate
import java.time.LocalDateTime


import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class NotificationAdapter(
    private var notificationArray: List<Pair<String?, List<Phone_notif_listQuery.Phone_notif_list>>>,
    var context: Context
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemGlobalNotifBinding.bind(itemView)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_global_notif, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return notificationArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val outputFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        for ( j: Int in  notificationArray[i].second.indices){
            val date1 = LocalDateTime.parse(notificationArray[i].second[j].createdAt.toString(), formatter)
            val formattedDate1 = date1.format(outputFormatter2)
            holder.binding.txtDay.text = notificationArray[i].first.toString() + " - $formattedDate1"
        }

        holder.binding.recyclerView.layoutManager = LinearLayoutManager(context)
/*        val dividerItemDecoration = DividerItemDecoration(context ,LinearLayoutManager.VERTICAL)
        val customDividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider_notif)
        dividerItemDecoration.setDrawable(customDividerDrawable!!)
        holder.binding.recyclerView.addItemDecoration(dividerItemDecoration)*/
        holder.binding.recyclerView.adapter = ItemsNotificationAdapter(notificationArray[i].second, context)


        for ( j: Int in  notificationArray[i].second.indices){
            val date = LocalDateTime.parse(notificationArray[i].second[j].createdAt.toString(), formatter)
            val formattedDate2 = date.format(outputFormatter2)
            val currentDate = LocalDate.now()
            val comparisonResult = formattedDate2.compareTo(currentDate.toString())
            if (comparisonResult > 0) {

            } else if (comparisonResult < 0) {

            } else {
                holder.binding.txtDay.text = context.resources.getString(R.string.opening_time_today)

            }
        }
    }
}