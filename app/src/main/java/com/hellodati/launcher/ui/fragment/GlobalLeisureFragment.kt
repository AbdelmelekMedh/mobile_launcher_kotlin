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
import com.hellodati.launcher.Leisures_mobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.LeisureClient
import com.hellodati.launcher.databinding.FragmentGlobalLeisureBinding
import com.hellodati.launcher.serializable_data.SerializableLeisure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private val ARG_ITEM = "item"


class GlobalLeisureFragment : Fragment() {

    private var itemPosition: Int =0
    private lateinit var binding : FragmentGlobalLeisureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable(ARG_ITEM) as SerializableLeisure
            itemPosition= item.leisurePosition
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGlobalLeisureBinding.inflate(inflater, container, false)
        GlobalScope.launch(Dispatchers.Main) {
            val leisureClient = LeisureClient(requireView().context)
            val leisureList = leisureClient.getLeisureMenu()
            val itemReceived = leisureList[itemPosition]
            if (itemReceived == null){
                binding.progressBar.visibility = View.GONE
                binding.emptyMenu.visibility = View.VISIBLE
            }else {
                binding.serviceTitle.text = itemReceived!!.title.default.toString()
                binding.progressBar.visibility = View.GONE
                setFragmentOfTab(BookingLeisureFragment.newInstance(SerializableLeisure(itemPosition)))

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
                                setFragmentOfTab(BookingLeisureFragment.newInstance(SerializableLeisure(itemPosition)))
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

                                setFragmentOfTab(AboutLeisureFragment.newInstance(SerializableLeisure(itemPosition)))

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
          Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            ).navigate(R.id.leisureFragment)
        }

        return  binding.root
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
        fun newInstance(item: SerializableLeisure) =
            GlobalLeisureFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ITEM, item)
                }
            }
    }
}