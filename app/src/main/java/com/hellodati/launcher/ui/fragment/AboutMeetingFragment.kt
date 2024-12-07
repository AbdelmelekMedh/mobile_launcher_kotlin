package com.hellodati.launcher.ui.fragment

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hellodati.launcher.Meeting_mobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.MeetingClient
import com.hellodati.launcher.databinding.FragmentAboutMeetingBinding
import com.hellodati.launcher.serializable_data.SerializableMeeting
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.floor

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_ITEM = "item"


class AboutMeetingFragment : Fragment() {

    private var itemPosition : Int = 0
    private lateinit var binding: FragmentAboutMeetingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable(ARG_ITEM) as SerializableMeeting
            itemPosition = item.meetingPosition
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAboutMeetingBinding.inflate(inflater, container, false)
        GlobalScope.launch (Dispatchers.Main){
            val meetingClient = MeetingClient(requireView().context)
            val meetingList = meetingClient.getMeetings()
            if (meetingList.isEmpty()){

            } else {
                val itemReceived = meetingList[itemPosition]
                binding.image.visibility = View.VISIBLE
                binding.generalDescriptionTitle.visibility = View.VISIBLE

                val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
                Glide.with(binding.root.context)
                    .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/meeting_pictures/${itemReceived!!.id}_${itemReceived!!.picture}?height=300")
                    .apply { RequestOptions().fitCenter() }
                    .into(binding.image)


                when (LocalHelper.getLanguage(binding.root.context).toString()) {
                    "en" -> {
                        if (itemReceived!!.title.en.toString() != "null") {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.title.en.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.title.default.toString()
                        }
                        if (itemReceived!!.description.en.toString() != "null") {
                            binding.description.text = itemReceived!!.description.en.toString()
                        } else {
                            binding.description.text = itemReceived!!.description.default.toString()
                        }
                    }

                    "ar" -> {
                        if (itemReceived!!.title.ar.toString() != "null") {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.title.ar.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.title.default.toString()
                        }
                        if (itemReceived!!.description.ar.toString() != "null") {
                            binding.description.text = itemReceived!!.description.ar.toString()
                        } else {
                            binding.description.text = itemReceived!!.description.default.toString()
                        }
                    }

                    "fr" -> {
                        if (itemReceived!!.title.fr.toString() != "null") {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.title.fr.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.title.default.toString()
                        }
                        if (itemReceived!!.description.fr.toString() != "null") {
                            binding.description.text = itemReceived!!.description.fr.toString()
                        } else {
                            binding.description.text = itemReceived!!.description.default.toString()
                        }
                    }

                    else -> {
                        binding.generalDescriptionTitle.text =
                            itemReceived!!.title.default.toString()
                        binding.description.text = itemReceived!!.description.default.toString()
                    }
                }

                if (itemReceived!!.price!!.hasDiscount == true) {
                    if (itemReceived!!.price!!.discountType.toString() == "percentege") {
                        val discountAmount: Double =
                            itemReceived!!.price!!.amount - ((itemReceived!!.price!!.discountAmount!! * itemReceived!!.price!!.amount) / 100.0)
                        if (discountAmount == floor(discountAmount)) {
                            binding.price.text =
                                "${discountAmount.toInt()} ${itemReceived!!.price!!.currency}"
                        } else {
                            binding.price.text =
                                "$discountAmount ${itemReceived!!.price!!.currency}"
                        }
                        if (itemReceived!!.price!!.amount == floor(itemReceived!!.price!!.amount)) {
                            binding.oldprice.text =
                                "${itemReceived!!.price!!.amount.toInt()} ${itemReceived!!.price!!.currency}"
                        } else {
                            binding.oldprice.text =
                                "${itemReceived!!.price!!.amount} ${itemReceived!!.price!!.currency}"
                        }

                        binding.oldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        val discountAmount =
                            itemReceived!!.price!!.amount - itemReceived!!.price!!.discountAmount!!
                        if (discountAmount == floor(discountAmount)) {
                            binding.price.text =
                                "${discountAmount.toInt()} ${itemReceived!!.price!!.currency}"
                        } else {
                            binding.price.text =
                                "$discountAmount ${itemReceived!!.price!!.currency}"
                        }
                        if (itemReceived!!.price!!.amount == floor(itemReceived!!.price!!.amount)) {
                            binding.oldprice.text =
                                "${itemReceived!!.price!!.amount.toInt()} ${itemReceived!!.price!!.currency}"
                        } else {
                            binding.oldprice.text =
                                "${itemReceived!!.price!!.amount} ${itemReceived!!.price!!.currency}"
                        }
                        binding.oldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    }
                } else {
                    if (itemReceived!!.price!!.amount == floor(itemReceived!!.price!!.amount)) {
                        binding.price.text =
                            "${itemReceived!!.price!!.amount.toInt()} ${itemReceived!!.price!!.currency}"
                    } else {
                        binding.price.text =
                            "${itemReceived!!.price!!.amount} ${itemReceived!!.price!!.currency}"
                    }
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(item: SerializableMeeting) =
            AboutMeetingFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ITEM, item)
                }
            }
    }
}