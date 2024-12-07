package com.hellodati.launcher.ui.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hellodati.launcher.R
import com.hellodati.launcher.ui.adapters.HistoryBookingAdapter
import com.hellodati.launcher.api.GetReservationsClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.FragmentHistoryBookingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryBookingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryBookingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentHistoryBookingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBookingBinding.inflate(inflater, container, false)

 /*       val flowerList = listOf(
            DishRestaurantModel("Rose"),
            DishRestaurantModel("Daisy"),
            DishRestaurantModel("Lily"),
            DishRestaurantModel("Narcissus"),
            DishRestaurantModel("Lotus"),
            DishRestaurantModel("Jasmine"),
            DishRestaurantModel("Tulip")
        )

        val collectios = listOf(
            CategoryRestaurantModel("All Flowers",flowerList),
            CategoryRestaurantModel("Want to buy",flowerList.reversed()),
            CategoryRestaurantModel("Popular Flowers",flowerList.shuffled()),
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
                        val reservationList = GetReservationsClient(binding.root.context).getAllReservations(ResidenceClient(binding.root.context).getResidenceId().toString())
                        if (reservationList.isEmpty()) {
                            // binding.txtEmpty.visibility = View.VISIBLE
                            binding.emptyOrder.visibility = View.VISIBLE
                            // binding.txtEmpty.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                        } else {
                            //  binding.txtEmpty.visibility = View.GONE
                            binding.emptyOrder.visibility = View.GONE
                            binding.progressBar.visibility = View.GONE
                            //  binding.txtEmpty.visibility = View.GONE
                            binding.recyclerHistory.adapter = HistoryBookingAdapter(reservationList,binding)
                            binding.recyclerHistory.scheduleLayoutAnimation();
                        }
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
                            val reservationList = GetReservationsClient(binding.root.context).getAllReservations(ResidenceClient(binding.root.context).getResidenceId().toString())
                            binding.recyclerHistory.adapter = HistoryBookingAdapter(reservationList,binding)
                            binding.recyclerHistory.scheduleLayoutAnimation();
                        }
                    }
                } catch (e: Exception) {
                    Log.e("device_gsf", e.message.toString())
                }
                binding.swipeContainer.isRefreshing = false
            }

        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
        return binding.root
    }

    fun onBackPressed(callback: () -> Unit) {
        val newCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                callback()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, newCallback)
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