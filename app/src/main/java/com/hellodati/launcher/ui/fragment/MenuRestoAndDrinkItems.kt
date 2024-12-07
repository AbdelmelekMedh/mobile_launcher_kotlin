package com.hellodati.launcher.ui.fragment

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.hellodati.launcher.EateriesMobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.EateriesClient
import com.hellodati.launcher.databinding.FragmentMenuRestoAndDrinkItemsBinding
import com.hellodati.launcher.serializable_data.SerializableMenuAboutBooking
import com.hellodati.launcher.serializable_data.SerializableRestoDrinkItems
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val ARG_ITEM = "item"
private val ARG_LIST = "list"

class MenuRestoAndDrinkItems: Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentMenuRestoAndDrinkItemsBinding
    private var itemPosition: Int = 0
    private var restaurantPosition: Int = 0
    private var categoriesPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            val item = it.getSerializable(ARG_ITEM) as SerializableRestoDrinkItems
            itemPosition = item.itemPosition
            categoriesPosition = item.Categoryposition
            val list = it.getSerializable(ARG_LIST) as SerializableMenuAboutBooking
            restaurantPosition = list.itemPosition
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuRestoAndDrinkItemsBinding.inflate(inflater, container, false)

        try {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    val restaurantList = EateriesClient(requireView().context).getAllEateries()
                    val restaurant = restaurantList[restaurantPosition]
                    val dishRestaurantModel = restaurant.categories!![categoriesPosition].items!!
                    if (dishRestaurantModel.isEmpty()){
                        binding.emptyMenu.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    } else{
                        val dishRestaurantItem = dishRestaurantModel[itemPosition]
                        binding.chosenContentImage.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        binding.chosenContentLayout.visibility = View.VISIBLE
                        val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)

                        Glide.with(binding.root.context)
                            .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/eatery_item_pictures/${dishRestaurantItem.id}_${dishRestaurantItem.image}?height=200")
                            .into(binding.chosenContentImage)

                        when (LocalHelper.getLanguage(binding.root.context).toString()) {
                            "en" -> {

                                if (dishRestaurantItem!!.title.en.toString() != "null"){
                                    binding.chosenContentName.text = dishRestaurantItem!!.title.en.toString()
                                }else{
                                    binding.chosenContentName.text = dishRestaurantItem!!.title.default.toString()
                                }

                                if (dishRestaurantItem!!.description.en.toString() != "null"){
                                    binding.chosenContentDescription.text = dishRestaurantItem!!.description.en.toString()
                                }else{
                                    binding.chosenContentDescription.text = dishRestaurantItem!!.description.default.toString()
                                }

                            }

                            "ar" -> {
                                if (dishRestaurantItem!!.title.ar.toString() != "null"){
                                    binding.chosenContentName.text = dishRestaurantItem!!.title.ar.toString()
                                }else{
                                    binding.chosenContentName.text = dishRestaurantItem!!.title.default.toString()
                                }

                                if (dishRestaurantItem!!.description.en.toString() != "null"){
                                    binding.chosenContentDescription.text = dishRestaurantItem!!.description.ar.toString()
                                }else{
                                    binding.chosenContentDescription.text = dishRestaurantItem!!.description.default.toString()
                                }
                            }

                            "fr" -> {
                                if (dishRestaurantItem!!.title.fr.toString() != "null"){
                                    binding.chosenContentName.text = dishRestaurantItem!!.title.fr.toString()
                                }else{
                                    binding.chosenContentName.text = dishRestaurantItem!!.title.default.toString()
                                }

                                if (dishRestaurantItem!!.description.en.toString() != "null"){
                                    binding.chosenContentDescription.text = dishRestaurantItem!!.description.fr.toString()
                                }else{
                                    binding.chosenContentDescription.text = dishRestaurantItem!!.description.default.toString()
                                }
                            }

                            else -> {
                                binding.chosenContentName.text = dishRestaurantItem!!.title.default.toString()
                                binding.chosenContentDescription.text = dishRestaurantItem!!.description.default.toString()
                            }
                        }


                        //binding.chosenContentPrice.text = "${dishRestaurantModel!!.price!!.amount.toInt()} ${dishRestaurantModel!!.price!!.currency}"

                        if(dishRestaurantItem!!.price!!.hasDiscount == true){
                            if (dishRestaurantItem!!.price!!.discountType.toString() == "percentege"){
                                val discountAmount : Double = dishRestaurantItem!!.price!!.amount - ((dishRestaurantItem!!.price!!.discountAmount!! * dishRestaurantItem!!.price!!.amount) / 100.0)
                                if(discountAmount == floor(discountAmount)){
                                    binding.chosenContentPrice.text = "${discountAmount.toInt()} ${dishRestaurantItem!!.price!!.currency}"
                                }else{
                                    binding.chosenContentPrice.text = "$discountAmount ${dishRestaurantItem!!.price!!.currency}"
                                }
                                if(dishRestaurantItem!!.price!!.amount == floor(dishRestaurantItem!!.price!!.amount)){
                                    binding.chosenContentOldprice.text = "${dishRestaurantItem!!.price!!.amount.toInt()} ${dishRestaurantItem!!.price!!.currency}"
                                }else{
                                    binding.chosenContentOldprice.text = "${dishRestaurantItem!!.price!!.amount} ${dishRestaurantItem!!.price!!.currency}"
                                }
                                binding.chosenContentOldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                            }else{
                                val discountAmount = dishRestaurantItem!!.price!!.amount - dishRestaurantItem!!.price!!.discountAmount!!
                                if(discountAmount == floor(discountAmount)){
                                    binding.chosenContentPrice.text = "${discountAmount.toInt()} ${dishRestaurantItem!!.price!!.currency}"
                                }else{
                                    binding.chosenContentPrice.text = "$discountAmount ${dishRestaurantItem!!.price!!.currency}"
                                }
                                if(dishRestaurantItem!!.price!!.amount == floor(dishRestaurantItem!!.price!!.amount)){
                                    binding.chosenContentOldprice.text = "${dishRestaurantItem!!.price!!.amount.toInt()} ${dishRestaurantItem!!.price!!.currency}"
                                }else{
                                    binding.chosenContentOldprice.text = "${dishRestaurantItem!!.price!!.amount} ${dishRestaurantItem!!.price!!.currency}"
                                }
                                binding.chosenContentOldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                            }
                        }else {
                            if (dishRestaurantItem!!.price!!.amount == floor(dishRestaurantItem!!.price!!.amount)) {
                                binding.chosenContentPrice.text =
                                    "${dishRestaurantItem!!.price!!.amount.toInt()} ${dishRestaurantItem!!.price!!.currency}"
                            } else {
                                binding.chosenContentPrice.text =
                                    "${dishRestaurantItem!!.price!!.amount} ${dishRestaurantItem!!.price!!.currency}"
                            }
                        }

                        when (LocalHelper.getLanguage(binding.root.context).toString()) {
                            "en" -> {
                                if (dishRestaurantItem!!.title.en.toString() != "null"){
                                    binding.serviceTitle.text = dishRestaurantItem!!.title.en.toString()
                                }else{
                                    binding.serviceTitle.text = dishRestaurantItem!!.title.default.toString()
                                }
                            }

                            "ar" -> {
                                if (dishRestaurantItem!!.title.ar.toString() != "null"){
                                    binding.serviceTitle.text = dishRestaurantItem!!.title.ar.toString()
                                }else{
                                    binding.serviceTitle.text = dishRestaurantItem!!.title.default.toString()
                                }
                            }

                            "fr" -> {
                                if (dishRestaurantItem!!.title.fr.toString() != "null"){
                                    binding.serviceTitle.text = dishRestaurantItem!!.title.fr.toString()
                                }else{
                                    binding.serviceTitle.text = dishRestaurantItem!!.title.default.toString()
                                }
                            }

                            else -> {
                                binding.serviceTitle.text = dishRestaurantItem!!.title.default.toString()
                            }
                        }
                    }

                }
            }
        }catch (e:Exception){
            Log.e("device_gsf", e.message.toString())
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InRoomFoodCategoriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        @JvmStatic
        fun newInstance(item: SerializableRestoDrinkItems,list: SerializableMenuAboutBooking) =
            MenuRestaurantDrinkFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ITEM, item)
                    putSerializable(ARG_LIST, list)
                }
            }
    }
}