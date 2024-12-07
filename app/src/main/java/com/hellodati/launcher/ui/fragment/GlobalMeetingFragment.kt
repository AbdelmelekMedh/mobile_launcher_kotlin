package com.hellodati.launcher.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayout
import com.hellodati.launcher.Meeting_mobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.MeetingClient
import com.hellodati.launcher.databinding.FragmentGlobalMeetingBinding
import com.hellodati.launcher.serializable_data.SerializableMeeting
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_ITEM = "item"

class GlobalMeetingFragment : Fragment() {

    private lateinit var binding : FragmentGlobalMeetingBinding
    private var itemPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable(ARG_ITEM) as SerializableMeeting
            itemPosition = item.meetingPosition
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGlobalMeetingBinding.inflate(inflater, container, false)
        GlobalScope.launch (Dispatchers.Main){
            val meetingClient = MeetingClient(requireView().context)
            val meetingList = meetingClient.getMeetings()
            if (meetingList.isEmpty()){
                binding.progressBar.visibility = View.GONE
                binding.emptyMenu.visibility = View.VISIBLE
            } else{
                binding.progressBar.visibility = View.GONE
                val itemReceived = meetingList[itemPosition]
                setFragmentOfTab(BookingMeetingFragment.newInstance(SerializableMeeting(itemPosition)))

                when (LocalHelper.getLanguage(binding.root.context).toString()) {
                    "en" -> {
                        binding.serviceTitle.text = itemReceived!!.title.en.toString()
                    }

                    "ar" -> {
                        binding.serviceTitle.text = itemReceived!!.title.ar.toString()
                    }

                    "fr" -> {
                        binding.serviceTitle.text = itemReceived!!.title.fr.toString()
                    }

                    else -> {
                        binding.serviceTitle.text = itemReceived!!.title.default.toString()
                    }
                }

                if (binding.serviceTitle.text.equals("null")){
                    binding.serviceTitle.text = itemReceived!!.title.default.toString()
                }
                val bookingTabToRemove = binding.tabs.getTabAt(0)
                if (bookingTabToRemove != null) {
                    binding.tabs.removeTab(bookingTabToRemove)
                }
                val reviewTabToRemove = binding.tabs.getTabAt(2)
                if (reviewTabToRemove != null) {
                    binding.tabs.removeTab(reviewTabToRemove)
                }

                binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        when (tab?.position) {
                            0 -> {
                                setFragmentOfTab(BookingMeetingFragment())
                            }

                            1 -> {
                                val animation =
                                    ObjectAnimator.ofFloat(binding.tabContent, "translationY", 0f)
                                animation.duration = 1000
                                animation.addListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationStart(animation: Animator) {
                                        binding.progressBar.visibility = View.VISIBLE
                                    }

                                    override fun onAnimationEnd(animation: Animator) {
                                        binding.progressBar.visibility = View.GONE
                                    }
                                })
                                animation.start()

                                setFragmentOfTab(AboutMeetingFragment.newInstance(SerializableMeeting(itemPosition)))

                            }

                            2 -> {
                                setFragmentOfTab(AboutRestaurantFragment())
                            }

                            3 -> {
                                setFragmentOfTab(ReviewsRestaurantFragment())
                            }

                            else -> {
                                // Handle other tab selections
                            }

                        }
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        // Write code to handle tab reselect
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                        // Write code to handle tab reselect
                    }
                })
            }
            }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        return binding.root
    }

    fun setFragmentOfTab(fragment: Fragment?) {
        if (fragment != null) {
            val container = binding.tabContent
            container.removeAllViews()
            val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            ft.replace(binding.tabContent.id, fragment)
            ft.addToBackStack(null)
            ft.commit()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(item: SerializableMeeting) =
            GlobalMeetingFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ITEM, item)
                }
            }
    }
}