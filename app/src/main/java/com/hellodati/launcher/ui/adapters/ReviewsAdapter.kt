package com.hellodati.launcher.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hellodati.launcher.EateriesMobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.databinding.ItemRecyclerReviewBinding
import com.hellodati.launcher.type.ServiceEnum
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ReviewsAdapter(
    private val serviceType: ServiceEnum,
    private val eateriesList: List<EateriesMobileQuery.Review>
):
    RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRecyclerReviewBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            commentTxt.text = eateriesList[position].comment.toString()
            commentRate.rating = eateriesList[position].rating!!.toFloat()
            countryName.text = eateriesList[position].guest!!.country
            flagImageView.countryCode = eateriesList[position].guest!!.country
            guestName.text = eateriesList[position].guest!!.lastName + eateriesList[position].guest!!.firstName

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val outputFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val date = LocalDateTime.parse(eateriesList[position].updatedAt.toString(), formatter)
            val formattedDate = date.format(outputFormatter2)

            commentDate.text = formattedDate

        }


    }

    override fun getItemCount() = eateriesList.size
}