package com.hellodati.launcher.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hellodati.launcher.Events_mobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.EventClient
import com.hellodati.launcher.databinding.FragmentAboutEventBinding
import com.hellodati.launcher.databinding.ItemEventBinding
import com.hellodati.launcher.serializable_data.SerializableEvent
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor



class AboutEventFragment : Fragment() {

    private lateinit var binding: FragmentAboutEventBinding
    private var itemPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable("item") as SerializableEvent
            itemPosition = item.event
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutEventBinding.inflate(inflater, container, false)
        GlobalScope.launch(Dispatchers.Main) {
            val eventClient = EventClient(binding.root.context)
            val itemReceived = eventClient.getAllEvent()
            if (itemReceived.isNotEmpty()){
                binding.image.visibility = View.VISIBLE
                binding.perPerson.visibility = View.VISIBLE
                binding.openingTimeOverview.visibility = View.VISIBLE
                when (LocalHelper.getLanguage(binding.root.context).toString()) {
                    "en" -> {
                        if (itemReceived[itemPosition]!!.title.en.toString() != "null") {
                            binding.generalDescriptionTitle.text =
                                itemReceived[itemPosition]!!.title.en.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived[itemPosition]!!.title.default.toString()
                        }

                        if (itemReceived[itemPosition]!!.title.en.toString() != "null") {
                            binding.description.text =
                                itemReceived[itemPosition]!!.description.en.toString()
                        } else {
                            binding.description.text =
                                itemReceived[itemPosition]!!.description.default.toString()
                        }

                    }

                    "ar" -> {
                        if (itemReceived[itemPosition]!!.title.ar.toString() != "null") {
                            binding.generalDescriptionTitle.text =
                                itemReceived[itemPosition]!!.title.ar.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived[itemPosition]!!.title.default.toString()
                        }

                        if (itemReceived[itemPosition]!!.title.ar.toString() != "null") {
                            binding.description.text =
                                itemReceived[itemPosition]!!.description.ar.toString()
                        } else {
                            binding.description.text =
                                itemReceived[itemPosition]!!.description.default.toString()
                        }
                    }

                    "fr" -> {
                        if (itemReceived[itemPosition]!!.title.fr.toString() != "null") {
                            binding.generalDescriptionTitle.text =
                                itemReceived[itemPosition]!!.title.fr.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived[itemPosition]!!.title.default.toString()
                        }

                        if (itemReceived[itemPosition]!!.title.fr.toString() != "null") {
                            binding.description.text =
                                itemReceived[itemPosition]!!.description.fr.toString()
                        } else {
                            binding.description.text =
                                itemReceived[itemPosition]!!.description.default.toString()
                        }
                    }

                    else -> {
                        binding.generalDescriptionTitle.text =
                            itemReceived[itemPosition]!!.title.default.toString()
                        binding.description.text =
                            itemReceived[itemPosition]!!.description.default.toString()
                    }
                }

                val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
                Glide.with(binding.root.context)
                    .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/event_pictures/${itemReceived[itemPosition]!!.id}_${itemReceived[itemPosition]!!.picture}?height=300")
                    .apply { RequestOptions().fitCenter() }
                    .into(binding.image)

                binding.price.text = itemReceived[itemPosition]!!.price!!.amount.toInt()
                    .toString() + " " + itemReceived[itemPosition]!!.price!!.currency

                if (itemReceived[itemPosition]!!.status!!.rawValue == "free") {
                    binding.price.text = "free"
                    binding.perPerson.text = ""
                } else {
                    if (itemReceived[itemPosition]!!.price!!.hasDiscount == true) {
                        if (itemReceived[itemPosition]!!.price!!.discountType.toString() == "percentege") {
                            val discountAmount: Double =
                                itemReceived[itemPosition]!!.price!!.amount - ((itemReceived[itemPosition]!!.price!!.discountAmount!! * itemReceived[itemPosition]!!.price!!.amount) / 100.0)
                            if (discountAmount == floor(discountAmount)) {
                                binding.price.text =
                                    "${discountAmount.toInt()} ${itemReceived[itemPosition]!!.price!!.currency}"
                            } else {
                                binding.price.text =
                                    "$discountAmount ${itemReceived[itemPosition]!!.price!!.currency}"
                            }
                            if (itemReceived[itemPosition]!!.price!!.amount == floor(itemReceived[itemPosition]!!.price!!.amount)) {
                                binding.oldprice.text =
                                    "${itemReceived[itemPosition]!!.price!!.amount.toInt()} ${itemReceived[itemPosition]!!.price!!.currency}"
                            } else {
                                binding.oldprice.text =
                                    "${itemReceived[itemPosition]!!.price!!.amount} ${itemReceived[itemPosition]!!.price!!.currency}"
                            }

                            binding.oldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                        } else {
                            val discountAmount =
                                itemReceived[itemPosition]!!.price!!.amount - itemReceived[itemPosition]!!.price!!.discountAmount!!
                            if (discountAmount == floor(discountAmount)) {
                                binding.price.text =
                                    "${discountAmount.toInt()} ${itemReceived[itemPosition]!!.price!!.currency}"
                            } else {
                                binding.price.text =
                                    "$discountAmount ${itemReceived[itemPosition]!!.price!!.currency}"
                            }
                            if (itemReceived[itemPosition]!!.price!!.amount == floor(itemReceived[itemPosition]!!.price!!.amount)) {
                                binding.oldprice.text =
                                    "${itemReceived[itemPosition]!!.price!!.amount.toInt()} ${itemReceived[itemPosition]!!.price!!.currency}"
                            } else {
                                binding.oldprice.text =
                                    "${itemReceived[itemPosition]!!.price!!.amount} ${itemReceived[itemPosition]!!.price!!.currency}"
                            }
                            binding.oldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                        }
                    } else {
                        if (itemReceived[itemPosition]!!.price!!.amount == floor(itemReceived!![itemPosition].price!!.amount)) {
                            binding.price.text =
                                "${itemReceived[itemPosition]!!.price!!.amount.toInt()} ${itemReceived[itemPosition]!!.price!!.currency}"
                        } else {
                            binding.price.text =
                                "${itemReceived[itemPosition]!!.price!!.amount} ${itemReceived[itemPosition]!!.price!!.currency}"
                        }
                    }
                }

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val outputFormatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val outputFormatter2 = DateTimeFormatter.ofPattern("HH:mm")
                val time =
                    LocalDateTime.parse(itemReceived[itemPosition]!!.dateto.toString(), formatter)
                val date =
                    LocalDateTime.parse(itemReceived[itemPosition]!!.dateto.toString(), formatter)
                val formattedTime = time.format(outputFormatter2)
                val formattedDate = date.format(outputFormatter3)
                binding.status.text =
                    " $formattedDate ${resources.getString(R.string.at)} $formattedTime"
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(item: SerializableEvent) =
            AboutEventFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("item", item)
                }
            }
    }
}