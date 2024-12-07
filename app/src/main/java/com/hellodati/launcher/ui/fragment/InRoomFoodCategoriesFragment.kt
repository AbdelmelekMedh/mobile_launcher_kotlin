package com.hellodati.launcher.ui.fragment

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.hellodati.launcher.R
import com.hellodati.launcher.api.IrdMenuClient
import com.hellodati.launcher.database.AppDataBase
import com.hellodati.launcher.databinding.FragmentMenuRestaurantBinding
import com.hellodati.launcher.model.Basket
import com.hellodati.launcher.serializable_data.SerializableInRoomFood
import com.hellodati.launcher.type.IrdCategoryEnum
import com.hellodati.launcher.ui.activity.MainActivity
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.math.floor


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_ITEM = "item"

class InRoomFoodCategoriesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentMenuRestaurantBinding
    private var dishPosition: Int = 0
    private var dirlistPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            val item = it.getSerializable(ARG_ITEM) as SerializableInRoomFood
            dishPosition = item.InRoomFood
            dirlistPosition = item.positions
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuRestaurantBinding.inflate(inflater, container, false)


        try {
            GlobalScope.launch {

                withContext(Dispatchers.Main) {
                    val irdMenuClient = IrdMenuClient(requireView().context)

                    val irdList = irdMenuClient.getAll(IrdCategoryEnum.food)!!


                    if (irdList.isEmpty()){
                        binding.emptyMenu.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        binding.serviceTitle.text = "Empty"
                    }else{
                        val dishRestaurantModel = irdList[dirlistPosition].items!![dishPosition]
                        binding.chosenContentImage.visibility = View.VISIBLE
                        binding.serviceTitle.text = dishRestaurantModel!!.title.default.toString()
                        binding.chosenContentLayout.visibility = View.VISIBLE

                        when (LocalHelper.getLanguage(binding.root.context).toString()) {
                            "en" -> {
                                binding.chosenContentName.text = dishRestaurantModel!!.title.en.toString()
                                binding.choosenContentSummery.text = dishRestaurantModel!!.description.en.toString()
                            }

                            "ar" -> {
                                binding.chosenContentName.text = dishRestaurantModel!!.title.ar.toString()
                                binding.choosenContentSummery.text = dishRestaurantModel!!.description.ar.toString()
                            }

                            "fr" -> {
                                binding.chosenContentName.text = dishRestaurantModel!!.title.fr.toString()
                                binding.choosenContentSummery.text = dishRestaurantModel!!.description.fr.toString()
                            }

                            else -> {
                                binding.chosenContentName.text = dishRestaurantModel!!.title.default.toString()
                                binding.choosenContentSummery.text = dishRestaurantModel!!.description.default.toString()
                            }
                        }

                        if (binding.chosenContentName.text.equals("null")){
                            binding.chosenContentName.text = dishRestaurantModel!!.title.default.toString()
                        }

                        if (binding.choosenContentSummery.text.equals("null")){
                            binding.choosenContentSummery.text = dishRestaurantModel!!.description.default.toString()
                        }

                        if(dishRestaurantModel!!.price!!.hasDiscount == true){
                            if (dishRestaurantModel!!.price!!.discountType.toString() == "percentege"){
                                val discountAmount : Double = dishRestaurantModel!!.price!!.amount - ((dishRestaurantModel!!.price!!.discountAmount!! * dishRestaurantModel!!.price!!.amount) / 100.0)
                                if(discountAmount == floor(discountAmount)){
                                    binding.chosenContentPrice.text = "${discountAmount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                                }else{
                                    binding.chosenContentPrice.text = "$discountAmount ${dishRestaurantModel!!.price!!.currency}"
                                }
                                if(dishRestaurantModel!!.price!!.amount == floor(dishRestaurantModel!!.price!!.amount)){
                                    binding.chosenContentOldprice.text = "${dishRestaurantModel!!.price!!.amount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                                }else{
                                    binding.chosenContentOldprice.text = "${dishRestaurantModel!!.price!!.amount} ${dishRestaurantModel!!.price!!.currency}"
                                }

                                binding.chosenContentOldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                            }else{
                                val discountAmount = dishRestaurantModel!!.price!!.amount - dishRestaurantModel!!.price!!.discountAmount!!
                                if(discountAmount == floor(discountAmount)){
                                    binding.chosenContentPrice.text = "${discountAmount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                                }else{
                                    binding.chosenContentPrice.text = "$discountAmount ${dishRestaurantModel!!.price!!.currency}"
                                }
                                if(dishRestaurantModel!!.price!!.amount == floor(dishRestaurantModel!!.price!!.amount)){
                                    binding.chosenContentOldprice.text = "${dishRestaurantModel!!.price!!.amount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                                }else{
                                    binding.chosenContentOldprice.text = "${dishRestaurantModel!!.price!!.amount} ${dishRestaurantModel!!.price!!.currency}"
                                }
                                binding.chosenContentOldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                            }
                        }else {
                            if (dishRestaurantModel!!.price!!.amount == floor(dishRestaurantModel!!.price!!.amount)) {
                                binding.chosenContentPrice.text =
                                    "${dishRestaurantModel!!.price!!.amount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                            } else {
                                binding.chosenContentPrice.text =
                                    "${dishRestaurantModel!!.price!!.amount} ${dishRestaurantModel!!.price!!.currency}"
                            }
                        }

                        val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)

                        Glide.with(binding.root.context)
                            .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/irditem_pictures/${dishRestaurantModel!!.id}_${dishRestaurantModel!!.image}?height=300")
                            .into(binding.chosenContentImage)

                        binding.btnOrder.setOnClickListener {
                            try {

                                val dataBase = AppDataBase.getInstance(binding.root.context)
                                val existingBasket = dataBase!!.basketDao().getBasketById(dishRestaurantModel!!.id)
                                if (existingBasket == null) {
                                    var title:String
                                    when (LocalHelper.getLanguage(binding.root.context).toString()) {
                                        "en" -> {
                                            title = dishRestaurantModel!!.title.en.toString()
                                        }

                                        "ar" -> {
                                            title = dishRestaurantModel!!.title.ar.toString()
                                        }

                                        "fr" -> {
                                            title = dishRestaurantModel!!.title.fr.toString()
                                        }

                                        else -> {
                                            title = dishRestaurantModel!!.title.default.toString()
                                        }
                                    }

                                    if (title == "null"){
                                        title = dishRestaurantModel!!.title.default.toString()
                                    }
                                    if(dishRestaurantModel!!.price!!.hasDiscount == true){
                                        if (dishRestaurantModel!!.price!!.discountType.toString() == "percentege"){
                                            val price  = dishRestaurantModel!!.price!!.amount - ((dishRestaurantModel!!.price!!.discountAmount!! * dishRestaurantModel!!.price!!.amount!!) / 100.0)
                                            val basket = dishRestaurantModel!!.price?.let { it1 ->
                                                Basket(
                                                    title = title,
                                                    currency = dishRestaurantModel!!.price!!.currency,
                                                    price = price,
                                                    quantity = 1,
                                                    date = LocalDate.now().toString(),
                                                    idDb = dishRestaurantModel!!.id
                                                )
                                            }
                                            val allBasket = basket?.let { it1 -> dataBase.basketDao().insert(it1) }
                                        }else{
                                            val price = dishRestaurantModel!!.price!!.amount - dishRestaurantModel!!.price!!.discountAmount!!
                                            val basket = dishRestaurantModel!!.price?.let { it1 ->
                                                Basket(
                                                    title = title,
                                                    currency = dishRestaurantModel!!.price!!.currency,
                                                    price = price,
                                                    quantity = 1,
                                                    date = LocalDate.now().toString(),
                                                    idDb = dishRestaurantModel!!.id
                                                )
                                            }
                                            val allBasket = basket?.let { it1 -> dataBase.basketDao().insert(it1) }
                                        }
                                    }else{
                                        val price = dishRestaurantModel!!.price!!.amount
                                        val basket = dishRestaurantModel!!.price?.let { it1 ->
                                            Basket(
                                                title = title,
                                                currency = dishRestaurantModel!!.price!!.currency,
                                                price = price,
                                                quantity = 1,
                                                date = LocalDate.now().toString(),
                                                idDb = dishRestaurantModel!!.id
                                            )
                                        }
                                        val allBasket = basket?.let { it1 -> dataBase.basketDao().insert(it1) }
                                    }



                                    MainActivity.updateTextView(dataBase.basketDao().getAll().size.toString())
                                    MainActivity.shakeBadge()
                                } else {
                                    existingBasket.quantity = existingBasket.quantity + 1

                                    dataBase.basketDao().update(existingBasket)
                                    MainActivity.shakeBadge()
                                }

                            } catch (e: Exception) {
                                Log.e("ddatabase", e.message.toString())
                            }

                            Toast.makeText(
                                binding.root.context,
                                dishRestaurantModel!!.title.default +" " +binding.root.resources.getString(R.string.order_added_success),
                                Toast.LENGTH_LONG
                            ).show()
                            requireActivity().onBackPressed()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                    binding.btnCancel.setOnClickListener {
                        requireActivity().onBackPressed()
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
        fun newInstance(item: SerializableInRoomFood) =
            RestaurantFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ITEM, item)
                }
            }
    }

}