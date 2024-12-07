package com.hellodati.launcher.ui.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hellodati.launcher.R
import com.hellodati.launcher.api.IrdMenuClient
import com.hellodati.launcher.database.AppDataBase
import com.hellodati.launcher.databinding.FragmentBottomSheetInRoomFoodBinding
import com.hellodati.launcher.model.Basket
import com.hellodati.launcher.serializable_data.SerializableInRoomFood
import com.hellodati.launcher.type.IrdCategoryEnum
import com.hellodati.launcher.ui.activity.MainActivity
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.floor


class BottomSheetInRoomFoodFragment : BottomSheetDialogFragment(){

    private var dishPosition: Int = 0
    private var dirlistPosition: Int = 0
    private var nbr: Int? = 1
    private var isFood: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable(ARG_POSITION) as SerializableInRoomFood
            dishPosition = item.InRoomFood
            dirlistPosition = item.positions
            isFood = item.isFood
        }
    }
    companion object {
        const val ARG_POSITION = "arg_position"

        fun newInstance(position: SerializableInRoomFood): BottomSheetInRoomFoodFragment {
            val fragment = BottomSheetInRoomFoodFragment()
            val args = Bundle()
            args.putSerializable(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setDimAmount(0.4f)
            setOnShowListener {
                val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                bottomSheet.setBackgroundResource(android.R.color.transparent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bindingBottomSheetInRoomFoodBinding = FragmentBottomSheetInRoomFoodBinding.inflate(inflater, container, false)
            bindingBottomSheetInRoomFoodBinding.description.movementMethod = ScrollingMovementMethod()
        try {
            GlobalScope.launch (Dispatchers.Main) {
                val irdMenuClient = IrdMenuClient(requireView().context)
                if (isFood){
                         val irdList = irdMenuClient.getAll(IrdCategoryEnum.food)!!
                        if (irdList.isNotEmpty()){
                            val dishRestaurantModel = irdList[dirlistPosition].items!![dishPosition]
                            bindingBottomSheetInRoomFoodBinding.progressBar.visibility = View.GONE
                            bindingBottomSheetInRoomFoodBinding.scrollBar.visibility =View.VISIBLE
                            val hotelLinksPreferences = bindingBottomSheetInRoomFoodBinding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
                            Glide.with(bindingBottomSheetInRoomFoodBinding.root.context)
                                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/irditem_pictures/${dishRestaurantModel!!.id}_${dishRestaurantModel!!.image}?height=300")
                                .into(bindingBottomSheetInRoomFoodBinding.chosenContentImage)

                            when (LocalHelper.getLanguage(bindingBottomSheetInRoomFoodBinding.root.context).toString()) {
                                "en" -> {
                                    bindingBottomSheetInRoomFoodBinding.generalDescriptionTitle.text = dishRestaurantModel!!.title.en.toString().trimEnd()
                                    bindingBottomSheetInRoomFoodBinding.description.text = dishRestaurantModel!!.description.en.toString().trimEnd()
                                }

                                "ar" -> {
                                    bindingBottomSheetInRoomFoodBinding.generalDescriptionTitle.text = dishRestaurantModel!!.title.ar.toString().trimEnd()
                                    bindingBottomSheetInRoomFoodBinding.description.text = dishRestaurantModel!!.description.ar.toString().trimEnd()
                                }

                                "fr" -> {
                                    bindingBottomSheetInRoomFoodBinding.generalDescriptionTitle.text = dishRestaurantModel!!.title.fr.toString().trimEnd()
                                    bindingBottomSheetInRoomFoodBinding.description.text = dishRestaurantModel!!.description.fr.toString().trimEnd()
                                }

                                else -> {
                                    bindingBottomSheetInRoomFoodBinding.generalDescriptionTitle.text = dishRestaurantModel!!.title.default.toString().trimEnd()
                                    bindingBottomSheetInRoomFoodBinding.description.text = dishRestaurantModel!!.description.default.toString().trimEnd()
                                }
                            }

                            if (bindingBottomSheetInRoomFoodBinding.generalDescriptionTitle.text.equals("null")){
                                bindingBottomSheetInRoomFoodBinding.generalDescriptionTitle.text = dishRestaurantModel!!.title.default.toString().trimEnd()
                            }

                            if (bindingBottomSheetInRoomFoodBinding.description.text.equals("null")){
                                bindingBottomSheetInRoomFoodBinding.description.text = dishRestaurantModel!!.description.default.toString().trimEnd()
                            }

                            if(dishRestaurantModel!!.price!!.hasDiscount == true){
                                if (dishRestaurantModel!!.price!!.discountType.toString() == "percentege"){
                                    val discountAmount : Double = dishRestaurantModel!!.price!!.amount - ((dishRestaurantModel!!.price!!.discountAmount!! * dishRestaurantModel!!.price!!.amount) / 100.0)
                                    if(discountAmount == floor(discountAmount)){
                                        bindingBottomSheetInRoomFoodBinding.chosenContentPrice.text = "${discountAmount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                                    }else{
                                        bindingBottomSheetInRoomFoodBinding.chosenContentPrice.text = "$discountAmount ${dishRestaurantModel!!.price!!.currency}"
                                    }
                                    if(dishRestaurantModel!!.price!!.amount == floor(dishRestaurantModel!!.price!!.amount)){
                                        bindingBottomSheetInRoomFoodBinding.chosenContentOldprice.text = "${dishRestaurantModel!!.price!!.amount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                                    }else{
                                        bindingBottomSheetInRoomFoodBinding.chosenContentOldprice.text = "${dishRestaurantModel!!.price!!.amount} ${dishRestaurantModel!!.price!!.currency}"
                                    }

                                    bindingBottomSheetInRoomFoodBinding.chosenContentOldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                                }else{
                                    val discountAmount = dishRestaurantModel!!.price!!.amount - dishRestaurantModel!!.price!!.discountAmount!!
                                    if(discountAmount == floor(discountAmount)){
                                        bindingBottomSheetInRoomFoodBinding.chosenContentPrice.text = "${discountAmount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                                    }else{
                                        bindingBottomSheetInRoomFoodBinding.chosenContentPrice.text = "$discountAmount ${dishRestaurantModel!!.price!!.currency}"
                                    }
                                    if(dishRestaurantModel!!.price!!.amount == floor(dishRestaurantModel!!.price!!.amount)){
                                        bindingBottomSheetInRoomFoodBinding.chosenContentOldprice.text = "${dishRestaurantModel!!.price!!.amount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                                    }else{
                                        bindingBottomSheetInRoomFoodBinding.chosenContentOldprice.text = "${dishRestaurantModel!!.price!!.amount} ${dishRestaurantModel!!.price!!.currency}"
                                    }
                                    bindingBottomSheetInRoomFoodBinding.chosenContentOldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                                }
                            }else {
                                if (dishRestaurantModel!!.price!!.amount == floor(dishRestaurantModel!!.price!!.amount)) {
                                    bindingBottomSheetInRoomFoodBinding.chosenContentPrice.text =
                                        "${dishRestaurantModel!!.price!!.amount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                                } else {
                                    bindingBottomSheetInRoomFoodBinding.chosenContentPrice.text =
                                        "${dishRestaurantModel!!.price!!.amount} ${dishRestaurantModel!!.price!!.currency}"
                                }
                            }

                            bindingBottomSheetInRoomFoodBinding.nbrPerson.text = Editable.Factory.getInstance().newEditable(nbr.toString())

                            bindingBottomSheetInRoomFoodBinding.increase.setOnClickListener {
                                nbr = bindingBottomSheetInRoomFoodBinding.nbrPerson.text.toString().toInt()
                                nbr = nbr!! + 1
                                bindingBottomSheetInRoomFoodBinding.nbrPerson.text = Editable.Factory.getInstance().newEditable(nbr.toString())
                            }

                            bindingBottomSheetInRoomFoodBinding.decrease.setOnClickListener {
                                if (nbr!! > 1) {
                                    nbr = bindingBottomSheetInRoomFoodBinding.nbrPerson.text.toString().toInt()
                                    nbr = nbr!! - 1
                                    bindingBottomSheetInRoomFoodBinding.nbrPerson.text =
                                        Editable.Factory.getInstance().newEditable(nbr.toString())
                                }
                            }

                            bindingBottomSheetInRoomFoodBinding.btnOrder.setOnClickListener {
                                try {

                                    val dataBase = AppDataBase.getInstance(bindingBottomSheetInRoomFoodBinding.root.context)
                                    val existingBasket = dataBase!!.basketDao().getBasketById(dishRestaurantModel!!.id)
                                    if (existingBasket == null) {
                                        var title:String
                                        when (LocalHelper.getLanguage(bindingBottomSheetInRoomFoodBinding.root.context).toString()) {
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
                                                        quantity = nbr!!,
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
                                                        quantity = nbr!!,
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
                                                    quantity = nbr!!,
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
                                    bindingBottomSheetInRoomFoodBinding.root.context,
                                    dishRestaurantModel!!.title.default +" " +bindingBottomSheetInRoomFoodBinding.root.resources.getString(R.string.order_added_success),
                                    Toast.LENGTH_LONG
                                ).show()
                                dismiss()
                            }

                            bindingBottomSheetInRoomFoodBinding.btnCancel.setOnClickListener {
                                dismiss()
                            }
                        }
                    }else{
                        val irdList = irdMenuClient.getAll(IrdCategoryEnum.drink)!!
                        if (irdList.isNotEmpty()){
                            val dishRestaurantModel = irdList[dirlistPosition].items!![dishPosition]
                            bindingBottomSheetInRoomFoodBinding.progressBar.visibility = View.GONE
                            bindingBottomSheetInRoomFoodBinding.scrollBar.visibility =View.VISIBLE
                            val hotelLinksPreferences = bindingBottomSheetInRoomFoodBinding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)

                            Glide.with(bindingBottomSheetInRoomFoodBinding.root.context)
                                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/irditem_pictures/${dishRestaurantModel!!.id}_${dishRestaurantModel!!.image}?height=300")
                                .into(bindingBottomSheetInRoomFoodBinding.chosenContentImage)

                            when (LocalHelper.getLanguage(bindingBottomSheetInRoomFoodBinding.root.context).toString()) {
                                "en" -> {
                                    bindingBottomSheetInRoomFoodBinding.generalDescriptionTitle.text = dishRestaurantModel!!.title.en.toString().trimEnd()
                                    bindingBottomSheetInRoomFoodBinding.description.text = dishRestaurantModel!!.description.en.toString().trimEnd()
                                }

                                "ar" -> {
                                    bindingBottomSheetInRoomFoodBinding.generalDescriptionTitle.text = dishRestaurantModel!!.title.ar.toString().trimEnd()
                                    bindingBottomSheetInRoomFoodBinding.description.text = dishRestaurantModel!!.description.ar.toString().trimEnd()
                                }

                                "fr" -> {
                                    bindingBottomSheetInRoomFoodBinding.generalDescriptionTitle.text = dishRestaurantModel!!.title.fr.toString().trimEnd()
                                    bindingBottomSheetInRoomFoodBinding.description.text = dishRestaurantModel!!.description.fr.toString().trimEnd()
                                }

                                else -> {
                                    bindingBottomSheetInRoomFoodBinding.generalDescriptionTitle.text = dishRestaurantModel!!.title.default.toString().trimEnd()
                                    bindingBottomSheetInRoomFoodBinding.description.text = dishRestaurantModel!!.description.default.toString().trimEnd()
                                }
                            }

                            if (bindingBottomSheetInRoomFoodBinding.generalDescriptionTitle.text.equals("null")){
                                bindingBottomSheetInRoomFoodBinding.generalDescriptionTitle.text = dishRestaurantModel!!.title.default.toString().trimEnd()
                            }

                            if (bindingBottomSheetInRoomFoodBinding.description.text.equals("null")){
                                bindingBottomSheetInRoomFoodBinding.description.text = dishRestaurantModel!!.description.default.toString().trimEnd()
                            }

                            if(dishRestaurantModel!!.price!!.hasDiscount == true){
                                if (dishRestaurantModel!!.price!!.discountType.toString() == "percentege"){
                                    val discountAmount : Double = dishRestaurantModel!!.price!!.amount - ((dishRestaurantModel!!.price!!.discountAmount!! * dishRestaurantModel!!.price!!.amount) / 100.0)
                                    if(discountAmount == floor(discountAmount)){
                                        bindingBottomSheetInRoomFoodBinding.chosenContentPrice.text = "${discountAmount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                                    }else{
                                        bindingBottomSheetInRoomFoodBinding.chosenContentPrice.text = "$discountAmount ${dishRestaurantModel!!.price!!.currency}"
                                    }
                                    if(dishRestaurantModel!!.price!!.amount == floor(dishRestaurantModel!!.price!!.amount)){
                                        bindingBottomSheetInRoomFoodBinding.chosenContentOldprice.text = "${dishRestaurantModel!!.price!!.amount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                                    }else{
                                        bindingBottomSheetInRoomFoodBinding.chosenContentOldprice.text = "${dishRestaurantModel!!.price!!.amount} ${dishRestaurantModel!!.price!!.currency}"
                                    }

                                    bindingBottomSheetInRoomFoodBinding.chosenContentOldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                                }else{
                                    val discountAmount = dishRestaurantModel!!.price!!.amount - dishRestaurantModel!!.price!!.discountAmount!!
                                    if(discountAmount == floor(discountAmount)){
                                        bindingBottomSheetInRoomFoodBinding.chosenContentPrice.text = "${discountAmount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                                    }else{
                                        bindingBottomSheetInRoomFoodBinding.chosenContentPrice.text = "$discountAmount ${dishRestaurantModel!!.price!!.currency}"
                                    }
                                    if(dishRestaurantModel!!.price!!.amount == floor(dishRestaurantModel!!.price!!.amount)){
                                        bindingBottomSheetInRoomFoodBinding.chosenContentOldprice.text = "${dishRestaurantModel!!.price!!.amount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                                    }else{
                                        bindingBottomSheetInRoomFoodBinding.chosenContentOldprice.text = "${dishRestaurantModel!!.price!!.amount} ${dishRestaurantModel!!.price!!.currency}"
                                    }
                                    bindingBottomSheetInRoomFoodBinding.chosenContentOldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                                }
                            }else {
                                if (dishRestaurantModel!!.price!!.amount == floor(dishRestaurantModel!!.price!!.amount)) {
                                    bindingBottomSheetInRoomFoodBinding.chosenContentPrice.text =
                                        "${dishRestaurantModel!!.price!!.amount.toInt()} ${dishRestaurantModel!!.price!!.currency}"
                                } else {
                                    bindingBottomSheetInRoomFoodBinding.chosenContentPrice.text =
                                        "${dishRestaurantModel!!.price!!.amount} ${dishRestaurantModel!!.price!!.currency}"
                                }
                            }

                            bindingBottomSheetInRoomFoodBinding.nbrPerson.text = Editable.Factory.getInstance().newEditable(nbr.toString())

                            bindingBottomSheetInRoomFoodBinding.increase.setOnClickListener(View.OnClickListener {
                                nbr = bindingBottomSheetInRoomFoodBinding.nbrPerson.text.toString().toInt()
                                nbr = nbr!! + 1
                                bindingBottomSheetInRoomFoodBinding.nbrPerson.text = Editable.Factory.getInstance().newEditable(nbr.toString())
                            })

                            bindingBottomSheetInRoomFoodBinding.decrease.setOnClickListener(View.OnClickListener {
                                if (nbr!! > 1) {
                                    nbr = bindingBottomSheetInRoomFoodBinding.nbrPerson.text.toString().toInt()
                                    nbr = nbr!! - 1
                                    bindingBottomSheetInRoomFoodBinding.nbrPerson.text =
                                        Editable.Factory.getInstance().newEditable(nbr.toString())
                                }
                            })

                            bindingBottomSheetInRoomFoodBinding.btnOrder.setOnClickListener {
                                try {

                                    val dataBase = AppDataBase.getInstance(bindingBottomSheetInRoomFoodBinding.root.context)
                                    val existingBasket = dataBase!!.basketDao().getBasketById(dishRestaurantModel!!.id)
                                    if (existingBasket == null) {
                                        var title:String
                                        when (LocalHelper.getLanguage(bindingBottomSheetInRoomFoodBinding.root.context).toString()) {
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
                                                        quantity = nbr!!,
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
                                                        quantity = nbr!!,
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
                                                    quantity = nbr!!,
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
                                    bindingBottomSheetInRoomFoodBinding.root.context,
                                    dishRestaurantModel!!.title.default +" " +bindingBottomSheetInRoomFoodBinding.root.resources.getString(R.string.order_added_success),
                                    Toast.LENGTH_LONG
                                ).show()
                                dismiss()
                            }

                            bindingBottomSheetInRoomFoodBinding.btnCancel.setOnClickListener {
                                dismiss()
                            }
                        }
                    }
            }
        }catch (e:Exception){
            Log.e("device_gsf", e.message.toString())
        }

        return bindingBottomSheetInRoomFoodBinding.root
    }
}