package com.hellodati.launcher.ui.fragment

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.hellodati.launcher.Meeting_mobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.IrdMenuClient
import com.hellodati.launcher.api.MeetingClient
import com.hellodati.launcher.databinding.FragmentMeetingBinding
import com.hellodati.launcher.type.IrdCategoryEnum
import com.hellodati.launcher.ui.adapters.InRoomFoodCategoriesAdapter
import com.hellodati.launcher.ui.adapters.MeetingAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MeetingFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentMeetingBinding

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

        binding = FragmentMeetingBinding.inflate(inflater, container, false)
        binding.serviceTitle.text = resources.getString(R.string.meeting_service)

        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            binding.msgTitle.text = getString(R.string.no_internet_connection)
            binding.emptyMenuText.text = ""
            binding.emptyMenuImage.visibility = View.GONE
            binding.emptyMenu.setBackgroundResource(R.drawable.no_connection_v2)
            binding.emptyMenu.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }else{
            try {
                GlobalScope.launch (Dispatchers.Main){
                    val meetingClient = MeetingClient(requireView().context)
                    val meetingList = meetingClient.getMeetings()

                    if (meetingList.isEmpty()){
                        binding.emptyMenu.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    }else{
                        binding.meetRecyclerView.adapter = MeetingAdapter(meetingList, binding)
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }catch (e:Exception){
                Log.e("device_gsf", e.message.toString())
            }

            binding.swipeContainer.setOnRefreshListener {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        try {
                            val meetingClient = MeetingClient(requireView().context)
                            val meetingList = meetingClient.getMeetings()

                            if (meetingList.isEmpty()){
                                binding.emptyMenu.visibility = View.VISIBLE
                                binding.swipeContainer.isRefreshing = false
                            }else{
                                binding.meetRecyclerView.adapter = MeetingAdapter(meetingList, binding)
                                binding.emptyMenu.visibility = View.GONE
                                binding.swipeContainer.isRefreshing = false
                            }
                        }catch (e:Exception){
                            Log.e("swipeRefresh", e.message.toString())
                        }
                    }
                }
            }
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    // new branch

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MeetingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}