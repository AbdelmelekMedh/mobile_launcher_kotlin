package com.hellodati.launcher.ui.fragment

import MenuEventFragment
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayout
import com.hellodati.launcher.R
import com.hellodati.launcher.api.EventClient
import com.hellodati.launcher.databinding.FragmentEventBinding
import com.hellodati.launcher.serializable_data.SerializableEvent
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class EventFragment : Fragment() {

    private lateinit var binding: FragmentEventBinding
    private var itemReceived: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable("item") as SerializableEvent
            itemReceived = item.event
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventBinding.inflate(inflater, container, false)
        //binding.serviceTitle.text = resources.getString(R.string.event_service)
        GlobalScope.launch (Dispatchers.Main) {
            try {
                val eventClient = EventClient(binding.root.context)
                val eventList = eventClient.getAllEvent()
                if (eventList.isNotEmpty()){
                    when (LocalHelper.getLanguage(binding.root.context).toString()) {
                        "en" -> {

                            if (eventList[itemReceived]!!.title.en.toString() != "null") {
                                binding.chosenContentName.text = eventList[itemReceived]!!.title.en.toString()
                            } else {
                                binding.chosenContentName.text =
                                    eventList[itemReceived]!!.title.default.toString()
                            }
                        }

                        "ar" -> {
                            if (eventList[itemReceived]!!.title.ar.toString() != "null") {
                                binding.chosenContentName.text = eventList[itemReceived]!!.title.ar.toString()
                            } else {
                                binding.chosenContentName.text =
                                    eventList[itemReceived]!!.title.default.toString()
                            }
                        }

                        "fr" -> {
                            if (eventList[itemReceived]!!.title.fr.toString() != "null") {
                                binding.chosenContentName.text = eventList[itemReceived]!!.title.fr.toString()
                            } else {
                                binding.chosenContentName.text =
                                    eventList[itemReceived]!!.title.default.toString()
                            }
                        }

                        else -> {
                            binding.chosenContentName.text = eventList[itemReceived]!!.title.default.toString()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("device_gsf", e.message.toString())
            }
        }
        setFragmentOfTab(BookingEventFragment.newInstance(SerializableEvent(itemReceived!!)))
        binding.tabs.visibility = View.VISIBLE
        val menuTabToRemove = binding.tabs.getTabAt(0)
        if (menuTabToRemove != null) {
            binding.tabs.removeTab(menuTabToRemove)
        }
        val reviewTabToRemove = binding.tabs.getTabAt(2)
        if (reviewTabToRemove != null) {
            binding.tabs.removeTab(reviewTabToRemove)
        }

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        setFragmentOfTab(BookingEventFragment.newInstance(SerializableEvent(itemReceived!!)))
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

                        setFragmentOfTab(AboutEventFragment.newInstance(SerializableEvent(itemReceived!!)))

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



        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    fun setFragmentOfTab(fragment: Fragment?) {
        if (fragment != null) {
            val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            ft.replace(binding.tabContent.id, fragment)
            ft.addToBackStack(null)
            ft.commit()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(item: SerializableEvent) =
            EventFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("item", item)
                }
            }

        fun newInstanceWithoutParams() = EventFragment()

    }
}