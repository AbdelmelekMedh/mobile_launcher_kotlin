package com.hellodati.launcher.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hellodati.launcher.R
import com.hellodati.launcher.api.EateriesClient
import com.hellodati.launcher.databinding.FragmentAboutRestaurantDrinkBinding
import com.hellodati.launcher.serializable_data.SerializableMenuAboutBooking
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private val ARG_LIST = "list"


class AboutRestaurantDrinkFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentAboutRestaurantDrinkBinding
    private var itemPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable(ARG_LIST) as SerializableMenuAboutBooking
            itemPosition = item.itemPosition
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutRestaurantDrinkBinding.inflate(inflater, container, false)
        GlobalScope.launch (Dispatchers.Main) {
            val restaurantList = EateriesClient(requireView().context).getAllEateries()
            if (restaurantList.isEmpty()){
                binding.progressBar.visibility = View.GONE
            }else{
                val itemReceived = restaurantList[itemPosition]
                binding.image.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.generalDescriptionTitle.visibility = View.VISIBLE
                when (LocalHelper.getLanguage(binding.root.context).toString()) {
                    "en" -> {
                        if (itemReceived!!.title.en.toString() != "null") {
                            binding.generalDescriptionTitle.text = itemReceived!!.title.en.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.title.default.toString()
                        }

                        if (itemReceived!!.description.en.toString() != "null") {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.description.en.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.description.default.toString()
                        }
                    }

                    "ar" -> {
                        if (itemReceived!!.title.ar.toString() != "null") {
                            binding.generalDescriptionTitle.text = itemReceived!!.title.ar.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.title.default.toString()
                        }

                        if (itemReceived!!.description.en.toString() != "null") {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.description.ar.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.description.default.toString()
                        }
                    }

                    "fr" -> {
                        if (itemReceived!!.title.fr.toString() != "null") {
                            binding.generalDescriptionTitle.text = itemReceived!!.title.fr.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.title.default.toString()
                        }

                        if (itemReceived!!.description.fr.toString() != "null") {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.description.fr.toString()
                        } else {
                            binding.generalDescriptionTitle.text =
                                itemReceived!!.description.default.toString()
                        }
                    }

                    else -> {
                        binding.generalDescriptionTitle.text = itemReceived!!.title.default.toString()
                        binding.description.text = itemReceived!!.description.default.toString()
                    }
                }

                if (binding.generalDescriptionTitle.text.isNullOrEmpty()) {
                    binding.generalDescriptionTitle.text = itemReceived!!.title.default.toString()
                }

                if (binding.description.text.isNullOrEmpty()) {
                    binding.description.text = itemReceived!!.description.default.toString()
                }

                val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
                Glide.with(binding.root.context)
                    .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/eatery_pictures/${itemReceived!!.id}_${itemReceived!!.image}?height=300")
                    .into(binding.image)
            }
        }
        return binding.root
    }
    

    companion object {

        @JvmStatic
        fun newInstance(list: SerializableMenuAboutBooking) =
            AboutRestaurantDrinkFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LIST, list)
                }
            }
    }
}