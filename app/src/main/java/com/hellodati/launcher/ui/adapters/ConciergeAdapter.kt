package com.hellodati.launcher.ui.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.bumptech.glide.Glide
import com.hellodati.launcher.ConciergeList_mobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ClickClient
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.ReservationClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.DialSendRequestBinding
import com.hellodati.launcher.databinding.DialogPasscodeBinding
import com.hellodati.launcher.databinding.FragmentConciergeServicesBinding
import com.hellodati.launcher.databinding.ItemConciergeBinding
import com.hellodati.launcher.serializable_data.SerializableConcierge
import com.hellodati.launcher.type.CreateConciergeRequestInput
import com.hellodati.launcher.type.ServiceEnum
import com.hellodati.launcher.ui.fragment.BasketFragment
import com.hellodati.launcher.ui.fragment.BottomSheetConciergeFragment
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConciergeAdapter(
    private val conciergeList: List<ConciergeList_mobileQuery.ConciergeList_mobile>,
    val binding: FragmentConciergeServicesBinding
) : RecyclerView.Adapter<ConciergeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemConciergeBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_concierge, parent, false)




        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            when (LocalHelper.getLanguage(binding.root.context).toString()) {
                "en" -> {
                    congName.text = conciergeList[position].title.en
                }

                "ar" -> {
                    congName.text = conciergeList[position].title.ar
                }

                "fr" -> {
                    congName.text = conciergeList[position].title.fr
                }

                else -> {
                    congName.text = conciergeList[position].title.default
                }
            }

            if (congName.text.isNullOrEmpty()){
                congName.text= conciergeList!![position].title.default
            }
            val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
            Glide.with(binding.root.context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/concierge_pictures/${conciergeList!![position].id}_${conciergeList!![position].image}?height=300")
                .into(congImage)
        }


        holder.itemView.setOnClickListener {
            GlobalScope.launch (Dispatchers.Main){
                try {
                    val clickClient = ClickClient(binding.root.context)
                    clickClient.createClick(
                        ResidenceClient(binding.root.context).getResidenceId().toString(),
                        conciergeList!![position].id,
                        ServiceEnum.Concierge
                    )
                } catch (e: ApolloException) {
                    Log.e("ConciergeClick", e.toString())
                }
            }
            if (conciergeList[position].listServices!!.default!!.isNotEmpty()){
                val bottomSheetFragment = BottomSheetConciergeFragment.newInstance(SerializableConcierge(position,false))
                bottomSheetFragment.show(
                    (binding.root.context as FragmentActivity).supportFragmentManager,
                    bottomSheetFragment.tag
                )
            }else{
                val bottomSheetFragment = BottomSheetConciergeFragment.newInstance(SerializableConcierge(position,true))
                bottomSheetFragment.show(
                    (binding.root.context as FragmentActivity).supportFragmentManager,
                    bottomSheetFragment.tag
                )
            }

        }

    }

    override fun getItemCount() = conciergeList.size
}