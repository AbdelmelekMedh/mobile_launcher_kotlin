package com.hellodati.launcher.ui.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hellodati.launcher.R
import com.hellodati.launcher.api.GetOrdersClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.FragmentOrderHistoryBinding
import com.hellodati.launcher.ui.adapters.HistoryOrderAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class OrderHistoryFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentOrderHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentOrderHistoryBinding.inflate(layoutInflater)
        /*        val flowerList = listOf(
                    DishRestaurantModel("Rose"),
                    DishRestaurantModel("Daisy"),
                    DishRestaurantModel("Lily"),
                    DishRestaurantModel("Narcissus"),
                    DishRestaurantModel("Lotus"),
                    DishRestaurantModel("Jasmine"),
                    DishRestaurantModel("Tulip")
                )

                val collectios = listOf(
                    CategoryRestaurantModel("All Flowers", flowerList),
                    CategoryRestaurantModel("Want to buy", flowerList.reversed()),
                    CategoryRestaurantModel("Popular Flowers", flowerList.shuffled()),
                )*/
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            binding.msgTitle.text = getString(R.string.no_internet_connection)
            binding.emptyMenuText.text = ""
            binding.emptyOrder.setBackgroundResource(R.drawable.no_connection_v2)
            binding.emptyOrder.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }else{
            try {
                GlobalScope.launch {

                    withContext(Dispatchers.Main) {
                        val ordersList = GetOrdersClient(binding.root.context).getAllOrders(
                            ResidenceClient(binding.root.context).getResidenceId().toString()
                        )
                        if (ordersList.isEmpty()) {
                            //binding.txtEmpty.visibility = View.VISIBLE
                            binding.emptyOrder.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                        } else {
                            // binding.txtEmpty.visibility = View.GONE
                            binding.emptyOrder.visibility = View.GONE
                            binding.rvParent.adapter = HistoryOrderAdapter(ordersList, binding)
                            binding.rvParent.scheduleLayoutAnimation();
                            binding.progressBar.visibility = View.GONE
                        }


                        /*val runnable: Runnable = Runnable {
                            run(){
                                if (ordersList.isEmpty()) {
                                    binding.txtEmpty.visibility = View.VISIBLE
                                    binding.emptyOrder.visibility = View.VISIBLE
                                    binding.progressBar.visibility = View.GONE
                                } else {
                                    binding.txtEmpty.visibility = View.GONE
                                    binding.emptyOrder.visibility = View.GONE
                                    binding.rvParent.adapter = HistoryOrderAdapter(ordersList, binding)
                                    binding.rvParent.scheduleLayoutAnimation();
                                    binding.progressBar.visibility = View.GONE
                                }
                            }
                        }

                        val handler = Handler()
                        handler.postDelayed(runnable,1000)*/


                    }
                }
            } catch (e: Exception) {
                Log.e("device_gsf", e.message.toString())
            }
        }

        binding.swipeContainer.setOnRefreshListener{
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
                binding.swipeContainer.isRefreshing = false
            }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryBookingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryBookingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}