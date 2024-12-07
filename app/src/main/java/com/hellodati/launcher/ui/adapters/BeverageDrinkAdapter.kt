package com.hellodati.launcher.ui.adapters

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellodati.launcher.Ird_menuQuery
import com.hellodati.launcher.ui.activity.MainActivity
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ClickClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.database.AppDataBase
import com.hellodati.launcher.databinding.FragmentDrinksBinding
import com.hellodati.launcher.databinding.FragmentMenuDrinkBinding
import com.hellodati.launcher.databinding.ItemDrinkBeverageBinding
import com.hellodati.launcher.model.Basket
import com.hellodati.launcher.type.ServiceEnum
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.math.floor
import kotlin.math.log

class BeverageDrinkAdapter(
    private val beverageDrinkModel: List<Ird_menuQuery.Item>?,
    val binding: FragmentMenuDrinkBinding,
) :
    RecyclerView.Adapter<BeverageDrinkAdapter.ViewHolder>() {
    private var lastPosition = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemDrinkBeverageBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_drink_beverage, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setAnimation(holder.itemView, position)
        val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
        holder.binding.apply {
            when (LocalHelper.getLanguage(binding.root.context).toString()) {
                "en" -> {
                    if (beverageDrinkModel!![position].title.en.toString() != "null"){
                        contentTitle.text = beverageDrinkModel!![position].title.en.toString()
                    }else{
                        contentTitle.text = beverageDrinkModel!![position].title.default.toString()
                    }

                    if (beverageDrinkModel!![position].description.en.toString() != "null"){
                        contentDescription.text = beverageDrinkModel!![position].description.en.toString()
                    }else{
                        contentDescription.text = beverageDrinkModel!![position].description.default.toString()
                    }
                }

                "ar" -> {
                    if (beverageDrinkModel!![position].title.ar.toString() != "null"){
                        contentTitle.text = beverageDrinkModel!![position].title.ar.toString()
                    }else{
                        contentTitle.text = beverageDrinkModel!![position].title.default.toString()
                    }

                    if (beverageDrinkModel!![position].description.ar.toString() != "null"){
                        contentDescription.text = beverageDrinkModel!![position].description.ar.toString()
                    }else{
                        contentDescription.text = beverageDrinkModel!![position].description.default.toString()
                    }
                }

                "fr" -> {
                    if (beverageDrinkModel!![position].title.fr.toString() != "null"){
                        contentTitle.text = beverageDrinkModel!![position].title.fr.toString()
                    }else{
                        contentTitle.text = beverageDrinkModel!![position].title.default.toString()
                    }

                    if (beverageDrinkModel!![position].description.fr.toString() != "null"){
                        contentDescription.text = beverageDrinkModel!![position].description.fr.toString()
                    }else{
                        contentDescription.text = beverageDrinkModel!![position].description.default.toString()
                    }
                }

                else -> {
                    contentTitle.text = beverageDrinkModel!![position].title.default.toString()
                    contentDescription.text = beverageDrinkModel!![position].description.default.toString()
                }
            }
            //contentPrice.text = "${beverageDrinkModel!![position].price!!.amount} ${beverageDrinkModel!![position].price!!.currency}"

            if(beverageDrinkModel!![position].price!!.hasDiscount == true){
                if (beverageDrinkModel!![position].price!!.discountType.toString() == "percentege"){
                    val discountAmount : Double = beverageDrinkModel!![position].price!!.amount - ((beverageDrinkModel!![position].price!!.discountAmount!! * beverageDrinkModel!![position].price!!.amount) / 100.0)
                    if(discountAmount == floor(discountAmount)){
                        contentPrice.text = "${discountAmount.toInt()} ${beverageDrinkModel!![position].price!!.currency}"
                    }else{
                        contentPrice.text = "$discountAmount ${beverageDrinkModel!![position].price!!.currency}"
                    }
                    if(beverageDrinkModel!![position].price!!.amount == floor(beverageDrinkModel!![position].price!!.amount)){
                        oldContentPrice.text = "${beverageDrinkModel!![position].price!!.amount.toInt()} ${beverageDrinkModel!![position].price!!.currency}"
                    }else{
                        oldContentPrice.text = "${beverageDrinkModel!![position].price!!.amount} ${beverageDrinkModel!![position].price!!.currency}"
                    }
                    oldContentPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }else{
                    val discountAmount = beverageDrinkModel!![position].price!!.amount - beverageDrinkModel!![position].price!!.discountAmount!!
                    if(discountAmount == floor(discountAmount)){
                        contentPrice.text = "${discountAmount.toInt()} ${beverageDrinkModel!![position].price!!.currency}"
                    }else{
                        contentPrice.text = "$discountAmount ${beverageDrinkModel!![position].price!!.currency}"
                    }
                    if(beverageDrinkModel!![position].price!!.amount == floor(beverageDrinkModel!![position].price!!.amount)){
                        oldContentPrice.text = "${beverageDrinkModel!![position].price!!.amount.toInt()} ${beverageDrinkModel!![position].price!!.currency}"
                    }else{
                        oldContentPrice.text = "${beverageDrinkModel!![position].price!!.amount} ${beverageDrinkModel!![position].price!!.currency}"
                    }
                    oldContentPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
            }else{
                if(beverageDrinkModel!![position].price!!.amount == floor(beverageDrinkModel!![position].price!!.amount)){
                    contentPrice.text = "${beverageDrinkModel!![position].price!!.amount.toInt()} ${beverageDrinkModel!![position].price!!.currency}"
                }else{
                    contentPrice.text = "${beverageDrinkModel!![position].price!!.amount} ${beverageDrinkModel!![position].price!!.currency}"
                }
            }

            Glide.with(binding.root.context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/irditem_pictures/${beverageDrinkModel!![position].id}_${beverageDrinkModel!![position].image}?height=300")
                .into(contentImage)

            binding.chosenContentImage.visibility = View.GONE
            binding.chosenContentName.visibility = View.GONE
            binding.chosenContentName.visibility = View.GONE
            binding.chosenContentPrice.visibility = View.GONE
            binding.choosenContentSummery.visibility = View.GONE
            binding.chosenContentDescription.visibility = View.GONE
            /*            Glide.with(binding.root.context)
                            .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/irditem_pictures/${beverageDrinkModel!![position].id}_${beverageDrinkModel!![position].image}?height=300")
                            .into(binding.chosenContentImage)*/
        }

        holder.itemView.setOnClickListener {
            binding.chosenContentImage.visibility = View.VISIBLE
            binding.chosenContentName.visibility = View.VISIBLE
            binding.chosenContentName.visibility = View.VISIBLE
            binding.chosenContentPrice.visibility = View.VISIBLE
            binding.chosenContentOldprice.visibility = View.VISIBLE
            binding.choosenContentSummery.visibility = View.VISIBLE
            binding.chosenContentDescription.visibility = View.VISIBLE
            binding.chosenContentLayout.visibility = View.VISIBLE
            binding.recyclerBeverage.visibility = View.GONE
            binding.btnOrder.visibility = View.VISIBLE
            binding.btnCancel.visibility = View.VISIBLE

            GlobalScope.launch {

                withContext(Dispatchers.Main) {
                    val clickClient = ClickClient(binding.root.context)
                    clickClient.createClick(
                        ResidenceClient(binding.root.context).getResidenceId().toString(),beverageDrinkModel!![position].id,
                        ServiceEnum.IrdDrinksItem)
                }
            }
            when (LocalHelper.getLanguage(binding.root.context).toString()) {
                "en" -> {
                    if (beverageDrinkModel!![position].title.en.toString() != "null"){
                        binding.chosenContentName.text = beverageDrinkModel!![position].title.en.toString()
                    }else{
                        binding.chosenContentName.text = beverageDrinkModel!![position].title.default.toString()
                    }

                    if (beverageDrinkModel!![position].description.en.toString() != "null"){
                        binding.choosenContentSummery.text = beverageDrinkModel!![position].description.en.toString()
                    }else{
                        binding.choosenContentSummery.text = beverageDrinkModel!![position].description.default.toString()
                    }
                }

                "ar" -> {
                    if (beverageDrinkModel!![position].title.ar.toString() != "null"){
                        binding.chosenContentName.text = beverageDrinkModel!![position].title.ar.toString()
                    }else{
                        binding.chosenContentName.text = beverageDrinkModel!![position].title.default.toString()
                    }

                    if (beverageDrinkModel!![position].description.ar.toString() != "null"){
                        binding.choosenContentSummery.text = beverageDrinkModel!![position].description.ar.toString()
                    }else{
                        binding.choosenContentSummery.text = beverageDrinkModel!![position].description.default.toString()
                    }
                }

                "fr" -> {
                    if (beverageDrinkModel!![position].title.fr.toString() != "null"){
                        binding.chosenContentName.text = beverageDrinkModel!![position].title.fr.toString()
                    }else{
                        binding.chosenContentName.text = beverageDrinkModel!![position].title.default.toString()
                    }

                    if (beverageDrinkModel!![position].description.ar.toString() != "null"){
                        binding.choosenContentSummery.text = beverageDrinkModel!![position].description.fr.toString()
                    }else{
                        binding.choosenContentSummery.text = beverageDrinkModel!![position].description.default.toString()
                    }
                }

                else -> {
                    binding.chosenContentName.text = beverageDrinkModel!![position].title.default.toString()
                    binding.choosenContentSummery.text = beverageDrinkModel!![position].description.default.toString()
                }
            }


            //binding.chosenContentPrice.text = "${beverageDrinkModel!![position].price!!.amount} ${beverageDrinkModel!![position].price!!.currency}"

            if(beverageDrinkModel!![position].price!!.hasDiscount == true){
                if (beverageDrinkModel!![position].price!!.discountType.toString() == "percentege"){
                    val discountAmount : Double = beverageDrinkModel!![position].price!!.amount - ((beverageDrinkModel!![position].price!!.discountAmount!! * beverageDrinkModel!![position].price!!.amount) / 100.0)
                    if(discountAmount == floor(discountAmount)){
                        binding.chosenContentPrice.text = "${discountAmount.toInt()} ${beverageDrinkModel!![position].price!!.currency}"
                    }else{
                        binding.chosenContentPrice.text = "$discountAmount ${beverageDrinkModel!![position].price!!.currency}"
                    }
                    if(beverageDrinkModel!![position].price!!.amount == floor(beverageDrinkModel!![position].price!!.amount)){
                        binding.chosenContentOldprice.text = "${beverageDrinkModel!![position].price!!.amount.toInt()} ${beverageDrinkModel!![position].price!!.currency}"
                    }else{
                        binding.chosenContentOldprice.text = "${beverageDrinkModel!![position].price!!.amount} ${beverageDrinkModel!![position].price!!.currency}"
                    }
                    binding.chosenContentOldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }else{
                    val discountAmount = beverageDrinkModel!![position].price!!.amount - beverageDrinkModel!![position].price!!.discountAmount!!
                    if(discountAmount == floor(discountAmount)){
                        binding.chosenContentPrice.text = "${discountAmount.toInt()} ${beverageDrinkModel!![position].price!!.currency}"
                    }else{
                        binding.chosenContentPrice.text = "$discountAmount ${beverageDrinkModel!![position].price!!.currency}"
                    }
                    if(beverageDrinkModel!![position].price!!.amount == floor(beverageDrinkModel!![position].price!!.amount)){
                        binding.chosenContentOldprice.text = "${beverageDrinkModel!![position].price!!.amount.toInt()} ${beverageDrinkModel!![position].price!!.currency}"
                    }else{
                        binding.chosenContentOldprice.text = "${beverageDrinkModel!![position].price!!.amount} ${beverageDrinkModel!![position].price!!.currency}"
                    }
                    binding.chosenContentOldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
            }else{
                if(beverageDrinkModel!![position].price!!.amount == floor(beverageDrinkModel!![position].price!!.amount)){
                    binding.chosenContentPrice.text = "${beverageDrinkModel!![position].price!!.amount.toInt()} ${beverageDrinkModel!![position].price!!.currency}"
                }else{
                    binding.chosenContentPrice.text = "${beverageDrinkModel!![position].price!!.amount} ${beverageDrinkModel!![position].price!!.currency}"
                }
            }
            Log.d("bb", beverageDrinkModel!![position].price!!.discountType.toString())

            Glide.with(binding.root.context)
                .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/irditem_pictures/${beverageDrinkModel!![position].id}_${beverageDrinkModel!![position].image}?height=300")
                .into(binding.chosenContentImage)


            binding.btnOrder.setOnClickListener {
                try {

                    val dataBase = AppDataBase.getInstance(binding.root.context)
                    val existingBasket =
                        dataBase!!.basketDao().getBasketById(beverageDrinkModel!![position].id)
                    if (existingBasket == null) {
                        val title:String
                        when (LocalHelper.getLanguage(binding.root.context).toString()) {
                            "en" -> {
                                if (beverageDrinkModel!![position].title.en.toString() != "null"){
                                    title = beverageDrinkModel!![position].title.en.toString()
                                }else{
                                    title = beverageDrinkModel!![position].title.default.toString()
                                }
                            }

                            "ar" -> {
                                if (beverageDrinkModel!![position].title.ar.toString() != "null"){
                                    title = beverageDrinkModel!![position].title.ar.toString()
                                }else{
                                    title = beverageDrinkModel!![position].title.default.toString()
                                }
                            }

                            "fr" -> {
                                if (beverageDrinkModel!![position].title.fr.toString() != "null"){
                                    title = beverageDrinkModel!![position].title.fr.toString()
                                }else{
                                    title = beverageDrinkModel!![position].title.default.toString()
                                }
                            }

                            else -> {
                                title = beverageDrinkModel!![position].title.default.toString()
                            }
                        }
                        if(beverageDrinkModel!![position].price!!.hasDiscount == true){
                            if (beverageDrinkModel!![position].price!!.discountType.toString() == "percentege"){
                                val price  = beverageDrinkModel!![position].price!!.amount - ((beverageDrinkModel!![position].price!!.discountAmount!! * beverageDrinkModel!![position].price!!.amount!!) / 100.0)
                                val basket = beverageDrinkModel!![position].price?.let { it1 ->
                                    Basket(
                                        title = title,
                                        currency = beverageDrinkModel!![position]!!.price!!.currency,
                                        price = price,
                                        quantity = 1,
                                        date = LocalDate.now().toString(),
                                        idDb = beverageDrinkModel!![position].id
                                    )
                                }
                                val allBasket = basket?.let { it1 -> dataBase.basketDao().insert(it1) }
                            }else{
                                val price = beverageDrinkModel!![position].price!!.amount - beverageDrinkModel!![position].price!!.discountAmount!!
                                val basket = beverageDrinkModel!![position].price?.let { it1 ->
                                    Basket(
                                        title = title,
                                        currency = beverageDrinkModel!![position]!!.price!!.currency,
                                        price = price,
                                        quantity = 1,
                                        date = LocalDate.now().toString(),
                                        idDb = beverageDrinkModel!![position].id
                                    )
                                }
                                val allBasket = basket?.let { it1 -> dataBase.basketDao().insert(it1) }
                            }
                        }else{
                            val price = beverageDrinkModel!![position].price!!.amount
                            val basket = beverageDrinkModel!![position].price?.let { it1 ->
                                Basket(
                                    title = title,
                                    currency = beverageDrinkModel!![position]!!.price!!.currency,
                                    price = price,
                                    quantity = 1,
                                    date = LocalDate.now().toString(),
                                    idDb = beverageDrinkModel!![position].id
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
                    beverageDrinkModel!![position].title.default + " " + binding.root.resources.getString(
                        R.string.order_added_success
                    ),
                    Toast.LENGTH_LONG
                ).show()
                binding.chosenContentImage.visibility = View.GONE
                binding.chosenContentName.visibility = View.GONE
                binding.chosenContentName.visibility = View.GONE
                binding.chosenContentPrice.visibility = View.GONE
                binding.chosenContentOldprice.visibility = View.GONE
                binding.choosenContentSummery.visibility = View.GONE
                binding.chosenContentDescription.visibility = View.GONE
                binding.recyclerBeverage.visibility = View.VISIBLE
                binding.chosenContentLayout.visibility = View.VISIBLE
                binding.btnOrder.visibility = View.GONE
                binding.btnCancel.visibility = View.GONE
                binding.menuRestaurantFrame.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = beverageDrinkModel!!.size

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation =
                AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.item_animation)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }
}