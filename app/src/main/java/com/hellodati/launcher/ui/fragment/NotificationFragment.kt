package com.hellodati.launcher.ui.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hellodati.launcher.R
import com.hellodati.launcher.api.PhoneNotifListener
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.FragmentNotificationBinding
import com.hellodati.launcher.model.RestaurantModel
import com.hellodati.launcher.ui.adapters.NotificationAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var binding : FragmentNotificationBinding

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        binding =  FragmentNotificationBinding.inflate(inflater, container, false)
        var contents = listOf<RestaurantModel>(
            RestaurantModel(
                1,
                "Restaurant",
                "",
                R.drawable.restaurant_service,
            ),
            RestaurantModel(
                2,
                "Drinks",
                "",
                R.drawable.boisson_service,
            ),
            RestaurantModel(
                3,
                "Leisure",
                "",
                R.drawable.loisir_service,
            ),
            RestaurantModel(
                4,
                "Well-being",
                "",
                R.drawable.well_being_service,
            ),
            RestaurantModel(
                5,
                "Events",
                "",
                R.drawable.evenement_service,
            )

        )
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            binding.msgTitle.text = getString(R.string.no_internet_connection)
            binding.emptyMenuText.text = ""
            binding.emptyNotification.setBackgroundResource(R.drawable.no_connection_v2)
            binding.emptyNotification.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }else{
            try {
                GlobalScope.launch {

                    withContext(Dispatchers.Main) {
                        val phoneNotifListener = PhoneNotifListener(binding.root.context)

                        if (phoneNotifListener.getGuestNotifications( ResidenceClient(binding.root.context).getResidenceId().toString()).isEmpty()){
                            binding.emptyNotification.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                        }else{
                            val dayNameMap = mapOf(
                                Calendar.SUNDAY to "Sun",
                                Calendar.MONDAY to "Mon",
                                Calendar.TUESDAY to "Tue",
                                Calendar.WEDNESDAY to "Wed",
                                Calendar.THURSDAY to "Thrs",
                                Calendar.FRIDAY to "Fri",
                                Calendar.SATURDAY to "Sat"
                            )
                            Log.e("notiflist", phoneNotifListener.getGuestNotifications(ResidenceClient(binding.root.context).getResidenceId().toString()).toString())
                            val notificationsByDayOfWeek = phoneNotifListener.getGuestNotifications(ResidenceClient(binding.root.context).getResidenceId().toString()).groupBy { notification ->
                                val calendar = Calendar.getInstance()
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                                val date: Date = dateFormat.parse(notification.createdAt.toString())
                                calendar.time = date
                                calendar.get(Calendar.DAY_OF_WEEK)

                            }.mapKeys { dayNameMap[it.key] }.toList()

                            binding.emptyNotification.visibility = View.GONE
                            binding.headerHolder.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.layoutManager = LinearLayoutManager(context)
                            binding.recyclerView.adapter = NotificationAdapter(notificationsByDayOfWeek,binding.root.context)
                        }
                    }
                }
            }catch (e:Exception){
                Log.e("device_gsf", e.message.toString())
            }
        }

        binding.swipeContainer.setOnRefreshListener {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    try {
                        val phoneNotifListener = PhoneNotifListener(binding.root.context)
                        if (phoneNotifListener.getGuestNotifications(
                                ResidenceClient(binding.root.context).getResidenceId()
                                    .toString()
                            ).isEmpty()
                        ) {
                            binding.emptyNotification.visibility = View.VISIBLE
                        } else {
                            val dayNameMap = mapOf(
                                Calendar.SUNDAY to "Sunday",
                                Calendar.MONDAY to "Monday",
                                Calendar.TUESDAY to "Tuesday",
                                Calendar.WEDNESDAY to "Wednesday",
                                Calendar.THURSDAY to "Thursday",
                                Calendar.FRIDAY to "Friday",
                                Calendar.SATURDAY to "Saturday"
                            )
                            val notificationsByDayOfWeek =
                                phoneNotifListener.getGuestNotifications(
                                    ResidenceClient(binding.root.context).getResidenceId()
                                        .toString()
                                ).groupBy { notification ->
                                    val calendar = Calendar.getInstance()
                                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                                    val date: Date =
                                        dateFormat.parse(notification.createdAt.toString())
                                    calendar.time = date
                                    calendar.get(Calendar.DAY_OF_WEEK)

                                }.mapKeys { dayNameMap[it.key] }.toList()

                            binding.recyclerView.layoutManager =
                                LinearLayoutManager(context)
                            binding.recyclerView.adapter = NotificationAdapter(
                                notificationsByDayOfWeek,
                                binding.root.context
                            )
                            binding.emptyNotification.visibility = View.GONE
                        }
                    }catch (e:Exception){
                        Log.e("swipeRefresh", e.message.toString())
                    }
                    binding.swipeContainer.isRefreshing = false
                }
            }
        }
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}