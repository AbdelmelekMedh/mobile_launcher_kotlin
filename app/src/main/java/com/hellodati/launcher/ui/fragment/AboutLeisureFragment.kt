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
import com.hellodati.launcher.Leisures_mobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.LeisureClient
import com.hellodati.launcher.databinding.FragmentAboutLeisureBinding
import com.hellodati.launcher.serializable_data.SerializableLeisure
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.floor


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val ARG_ITEM = "item"


class AboutLeisureFragment : Fragment() {

    private var itemPosition: Int =0
    private lateinit var binding: FragmentAboutLeisureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable(ARG_ITEM) as SerializableLeisure
            itemPosition = item.leisurePosition
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutLeisureBinding.inflate(inflater, container, false)
        GlobalScope.launch(Dispatchers.Main) {
            val leisureClient = LeisureClient(requireView().context)
            val leisureList = leisureClient.getLeisureMenu()
            if (leisureList.isEmpty()){
                binding.noData.visibility = View.VISIBLE
            }else{
                val itemReceived = leisureList[itemPosition]
                binding.image.visibility = View.VISIBLE
                binding.perPerson.visibility = View.VISIBLE
                binding.openingTime.visibility = View.VISIBLE
                when (LocalHelper.getLanguage(binding.root.context).toString()) {
                    "en" -> {
                        if (itemReceived!!.title.en.toString() != "null") {
                            binding.generalDescriptionTitle.text = itemReceived!!.title.en.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.title.default.toString()
                        }

                        if (itemReceived!!.title.en.toString() != "null") {
                            binding.description.text = itemReceived!!.description.en.toString()
                        } else {
                            binding.description.text = itemReceived!!.description.default.toString()
                        }

                    }

                    "ar" -> {
                        if (itemReceived!!.title.ar.toString() != "null") {
                            binding.generalDescriptionTitle.text = itemReceived!!.title.ar.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.title.default.toString()
                        }

                        if (itemReceived!!.title.ar.toString() != "null") {
                            binding.description.text = itemReceived!!.description.ar.toString()
                        } else {
                            binding.description.text = itemReceived!!.description.default.toString()
                        }
                    }

                    "fr" -> {
                        if (itemReceived!!.title.fr.toString() != "null") {
                            binding.generalDescriptionTitle.text = itemReceived!!.title.fr.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.title.default.toString()
                        }

                        if (itemReceived!!.title.fr.toString() != "null") {
                            binding.description.text = itemReceived!!.description.fr.toString()
                        } else {
                            binding.description.text = itemReceived!!.description.default.toString()
                        }
                    }

                    else -> {
                        binding.generalDescriptionTitle.text = itemReceived!!.title.default.toString()
                        binding.description.text = itemReceived!!.description.default.toString()
                    }
                }
                val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
                Glide.with(binding.root.context)
                    .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/leisure_pictures/${itemReceived!!.id}_${itemReceived!!.image}?height=300")
                    .apply { RequestOptions().fitCenter() }
                    .into(binding.image)

                //binding.price.text = itemReceived!!.price!!.amount.toInt().toString() + " " + itemReceived!!.price!!.currency

                if (itemReceived!!.price!!.hasDiscount == true) {
                    if (itemReceived!!.price!!.discountType.toString() == "percentege") {
                        val discountAmount: Double =
                            itemReceived!!.price!!.amount - ((itemReceived!!.price!!.discountAmount!! * itemReceived!!.price!!.amount) / 100.0)
                        if (discountAmount == floor(discountAmount)) {
                            binding.price.text =
                                "${discountAmount.toInt()} ${itemReceived!!.price!!.currency}"
                        } else {
                            binding.price.text = "$discountAmount ${itemReceived!!.price!!.currency}"
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
                            binding.price.text = "$discountAmount ${itemReceived!!.price!!.currency}"
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

                binding.status.text =
                    "${resources.getString(R.string.openning_time_from)} ${itemReceived!!.from} ${
                        resources.getString(R.string.openning_time_to)
                    } ${itemReceived!!.to}"
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(item: SerializableLeisure) =
            AboutLeisureFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ITEM, item)
                }
            }
    }
}