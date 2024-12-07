package com.hellodati.launcher.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hellodati.launcher.ui.activity.MainActivity
import com.hellodati.launcher.R
import com.hellodati.launcher.database.AppDataBase
import com.hellodati.launcher.databinding.FragmentBasketBinding
import com.hellodati.launcher.databinding.ItemBasketBinding
import com.hellodati.launcher.model.Basket
import java.lang.Exception
import java.util.Locale
import kotlin.math.floor

class BasketAdapter(
    private val dishRestaurantModel: MutableList<Basket>,
    val binding: FragmentBasketBinding
) :
    RecyclerView.Adapter<BasketAdapter.ViewHolder>() {
    private var nbr: Int? = 1
    private var total: Double = 0.0
    private var count: Double = 0.0
    private var totalItem: Double = 0.0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemBasketBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_basket, parent, false)
        /*  val animation = AnimationUtils.loadAnimation(parent.context, R.anim.item_animation)
          view.startAnimation(animation)*/
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var dataBase = AppDataBase.getInstance(binding.root.context)
        holder.binding.apply {
            title.text = dishRestaurantModel[position].title
            if(dishRestaurantModel[position].price == floor(dishRestaurantModel[position].price)){
                price.text = dishRestaurantModel[position].price.toInt().toString() + " " + dishRestaurantModel[position].currency
            }else{
                price.text = dishRestaurantModel[position].price.toString() + " " + dishRestaurantModel[position].currency
            }
            qtVar.text = dishRestaurantModel[position].quantity.toString()
            totalItem = binding.totalPrice.text.toString().toDouble()
            Log.e("totalItem", totalItem.toString())
            incQt.setOnClickListener {
                nbr = qtVar.text.toString().toInt()
                nbr = nbr!! + 1
                qtVar.text = nbr.toString()
                total += dishRestaurantModel[position].price
                count = total + totalItem
                val count =  String.format(Locale.US,"%.3f", count).toDouble()
                binding.totalPrice.text = "$count"


                try {
                    val db = AppDataBase.getInstance(binding.root.context)
                    val dao = db!!.basketDao()

                    val itemToUpdate = dao.getBasketById(dishRestaurantModel[position].idDb)

                    itemToUpdate!!.quantity = nbr as Int


                    dao.update(itemToUpdate)
                }catch (e:Exception){
                    Log.e("ddbase",e.message.toString())
                }

            }


            decQt.setOnClickListener {
                if (nbr!! > 1) {
                    nbr = qtVar.text.toString().toInt()
                    nbr = nbr!! - 1
                    qtVar.text = nbr.toString()
                    total -= dishRestaurantModel[position].price
                    count = total + totalItem
                    val count =  String.format(Locale.US,"%.3f", count).toDouble()
                    binding.totalPrice.text = "$count"


                    try {
                        val db = AppDataBase.getInstance(binding.root.context)
                        val dao = db!!.basketDao()

                        val itemToUpdate = dao.getBasketById(dishRestaurantModel[position].idDb.toString())

                        itemToUpdate!!.quantity = nbr as Int


                        dao.update(itemToUpdate)
                    }catch (e:Exception){
                        Log.e("ddbase",e.message.toString())
                    }

                }
            }

            deleteBtn.setOnClickListener {
                binding.totalPrice.text = "${binding.totalPrice.text.toString().toDouble() - dishRestaurantModel[position].price}"
                var dataBase = AppDataBase.getInstance(binding.root.context)!!.basketDao()
                    .delete(dishRestaurantModel[position])
                dishRestaurantModel.removeAt(position)
                notifyItemRemoved(position)
                notifyDataSetChanged()
                MainActivity.updateTextView("${dishRestaurantModel.size}")
                if (dishRestaurantModel.isEmpty()) {
                    binding.emptyBasket.visibility = View.VISIBLE
                    binding.ids.visibility = View.GONE
                    MainActivity.updateTextView("${dishRestaurantModel.size}")
                }

            }
        }

    }

    override fun getItemCount() = dishRestaurantModel.size

}