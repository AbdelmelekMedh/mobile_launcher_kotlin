package com.hellodati.launcher.ui.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hellodati.launcher.GetMobileOrdersQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.CancelOrderClient
import com.hellodati.launcher.api.GetOrdersClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.FragmentOrderHistoryBinding
import com.hellodati.launcher.databinding.ItemHistoryHeaderBinding
import com.hellodati.launcher.type.OrderItemStatusEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.floor

class HistoryOrderAdapter(
    private val collections: List<GetMobileOrdersQuery.GetMobileOrder>,
    var binding: FragmentOrderHistoryBinding
) :
    RecyclerView.Adapter<HistoryOrderAdapter.CollectionsViewHolder>() {

    lateinit var context: Context

    class CollectionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemHistoryHeaderBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history_header, parent, false)
        context = parent.context
        return CollectionsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionsViewHolder, position: Int) {
        holder.binding.apply {

            val collection = collections[position]
            title.text = binding.root.resources.getString(R.string.reservation_number) +" "+collection.orderNumber

/*            if (collection.rejectedSum.toString().toDouble() == null){
                rejectSumResult = 0.0
            }else{
                rejectSumResult = collection.rejectedSum.toString().toDouble()
            }*/

            if (collection.status == OrderItemStatusEnum.waiting){
                deleteBtn.visibility = View.VISIBLE
            }else{
                deleteBtn.visibility = View.GONE
            }

            deleteBtn.setOnClickListener {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        val cancelOrderClient = CancelOrderClient(binding.root.context)
                        cancelOrderClient.cancelOrder(
                            collection.id)
                        try {
                            GlobalScope.launch {
                                withContext(Dispatchers.Main) {
                                    val ordersList = GetOrdersClient(binding.root.context).getAllOrders(
                                        ResidenceClient(binding.root.context).getResidenceId().toString()
                                    )
                                    Log.e("ordertt", ordersList.toString())
                                    binding.rvParent.adapter = HistoryOrderAdapter(ordersList, binding)
                                    binding.rvParent.scheduleLayoutAnimation()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("device_gsf", e.message.toString())
                        }
                    }
                }
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val outputFormatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val time = LocalDateTime.parse(collection.createdAt.toString(), formatter)
            val day = LocalDateTime.parse(collection.createdAt.toString(), formatter)
            date.text= time.format(outputFormatter) +" "+day.format(outputFormatterDate)
            if(collection.total!!.amount == floor(collection.total!!.amount)){
                totPrice.text = "${collection.total!!.amount.toInt()} ${collection.total!!.currency}"
            }else{
                val price = collection.total!!.amount
                val priceStr =  String.format(Locale.US,"%.3f", price).toDouble()
                totPrice.text = "$priceStr ${collection.total!!.currency}"
            }
            val dishRestaurantAdapter = ItemHistoryOrderAdapter(collection.orderItems,binding)
            rvSubItem.adapter = dishRestaurantAdapter
            rvSubItem.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            holder.itemView.setOnClickListener {
                if (collection.orderItems!!.isNotEmpty()){
                    rvSubItem.visibility = if (rvSubItem.isShown) View.GONE else View.VISIBLE
                    arrowDown.visibility = if (rvSubItem.isShown) View.GONE else View.VISIBLE
                    arrowUp.visibility = if (!rvSubItem.isShown) View.GONE else View.VISIBLE

                }

            }
        }
    }

    override fun getItemCount() = collections.size
}