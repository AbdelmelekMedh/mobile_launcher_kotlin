package com.hellodati.launcher.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hellodati.launcher.R
import com.hellodati.launcher.model.Apps
import java.util.*

class AppsFragmentAdapter(private var appsArray: List<Apps>, private var context: Context) : RecyclerView.Adapter<AppsFragmentAdapter.AppsViewHolder>() {
  //  private var appsArray: ArrayList<Apps> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsFragmentAdapter.AppsViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return AppsFragmentAdapter.AppsViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
       return appsArray.size
    }

    override fun onBindViewHolder(holder: AppsFragmentAdapter.AppsViewHolder, position: Int) {
        holder.app_name.text = appsArray[position].label
        holder.app_icon.setImageDrawable(appsArray[position].icon)

        holder.itemView.setOnClickListener {
            val launchIntent =
                context.packageManager.getLaunchIntentForPackage(appsArray[position].packageName.toString())
            context.startActivity(launchIntent)
        }
    }


    class AppsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var app_icon: ImageView = itemView.findViewById(R.id.app_icon)
        var app_name: TextView = itemView.findViewById(R.id.app_name)



    }

}