package com.hellodati.launcher.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayout
import com.hellodati.launcher.R
import com.hellodati.launcher.api.EateriesClient
import com.hellodati.launcher.databinding.FragmentMenuBookingAboutRestaurantDrinkBinding
import com.hellodati.launcher.serializable_data.SerializableMenuAboutBooking
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_ITEM = "item"

class MenuBookingAboutRestaurantDrinkFragment: Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentMenuBookingAboutRestaurantDrinkBinding
    private var itemPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            val item = it.getSerializable(ARG_ITEM) as SerializableMenuAboutBooking
            itemPosition = item.itemPosition
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuBookingAboutRestaurantDrinkBinding.inflate(inflater, container, false)

        try {
            GlobalScope.launch {

                withContext(Dispatchers.Main) {
                    val restaurantList = EateriesClient(requireView().context).getAllEateries()

                    if (restaurantList.isEmpty()){
                        binding.progressBar.visibility = View.GONE
                        binding.emptyMenu.visibility = View.VISIBLE
                    }else{
                        val itemReceived = restaurantList[itemPosition]
                        binding.progressBar.visibility = View.GONE
                        binding.tabs.visibility = View.VISIBLE
                        when (LocalHelper.getLanguage(binding.root.context).toString()) {
                            "en" -> {
                                if (itemReceived.title.en.toString() != "null") {
                                    binding.serviceTitle.text = itemReceived.title.en.toString()
                                } else {
                                    binding.serviceTitle.text = itemReceived.title.default.toString()
                                }
                            }

                            "ar" -> {
                                if (itemReceived.title.en.toString() != "null") {
                                    binding.serviceTitle.text = itemReceived.title.ar.toString()
                                } else {
                                    binding.serviceTitle.text = itemReceived.title.default.toString()
                                }
                            }

                            "fr" -> {
                                if (itemReceived.title.en.toString() != "null") {
                                    binding.serviceTitle.text = itemReceived.title.fr.toString()
                                } else {
                                    binding.serviceTitle.text = itemReceived.title.default.toString()
                                }
                            }

                            else -> {
                                binding.serviceTitle.text = itemReceived.title.default.toString()
                            }
                        }
                    }
                }

                val menuRestaurantDrinkFragment = MenuRestaurantDrinkFragment.newInstance(SerializableMenuAboutBooking(itemPosition))
                val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                binding.tabContent.removeAllViews()
                fragmentManager.beginTransaction()
                    .replace(binding.tabContent.id, menuRestaurantDrinkFragment)
                    .addToBackStack(null)
                    .commit()


                binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        when (tab?.position) {
                            0 -> {
                                val menuRestaurantDrinkFragment = MenuRestaurantDrinkFragment.newInstance(SerializableMenuAboutBooking(itemPosition))
                                val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                                binding.tabContent.removeAllViews()
                                fragmentManager.beginTransaction()
                                    .replace(binding.tabContent.id, menuRestaurantDrinkFragment)
                                    .addToBackStack(null)
                                    .commit()
                            }
                            1 -> {
                                val animation = ObjectAnimator.ofFloat(binding.tabContent, "translationY", 0f)
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

                                val bookingRestaurantDrinkFragment = BookingRestaurantDrinkFragment.newInstance(SerializableMenuAboutBooking(itemPosition))
                                val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                                binding.tabContent.removeAllViews()
                                fragmentManager.beginTransaction()
                                    .replace(binding.tabContent.id, bookingRestaurantDrinkFragment)
                                    .addToBackStack(null)
                                    .commit()

                            }
                            2 -> {
                                val aboutRestaurantDrinkFragment = AboutRestaurantDrinkFragment.newInstance(SerializableMenuAboutBooking(itemPosition))
                                val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                                binding.tabContent.removeAllViews()
                                fragmentManager.beginTransaction()
                                    .replace(binding.tabContent.id, aboutRestaurantDrinkFragment)
                                    .addToBackStack(null)
                                    .commit()
                            }

                            3 ->{
                                val reviewsRestaurantDrinkFragment = ReviewsRestaurantDrinkFragment.newInstance(SerializableMenuAboutBooking(itemPosition))
                                val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                                binding.tabContent.removeAllViews()
                                fragmentManager.beginTransaction()
                                    .replace(binding.tabContent.id, reviewsRestaurantDrinkFragment)
                                    .addToBackStack(null)
                                    .commit()
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

             /*   val reviewTabToRemove = binding.tabs.getTabAt(3)
                if (reviewTabToRemove != null) {
                    binding.tabs.removeTab(reviewTabToRemove)
                }*/
                binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        val tabView = tab?.customView
                        tabView?.setOnClickListener {
                            Toast.makeText(context, "clicked ${binding.tabs.selectedTabPosition}", Toast.LENGTH_SHORT).show()
                            Log.e("ttab","clicked")
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                        val tabView = tab?.customView
                        tabView?.setOnClickListener(null)
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        // Do nothing
                    }
                })
            }
        } catch (e:Exception){
            Log.e("device_gsf", e.message.toString())
        }


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
        fun newInstance(param1: String, param2: String) =
            InRoomFoodCategoriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(item: SerializableMenuAboutBooking) =
            RestaurantAndDrinkFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ITEM, item)
                }
            }
    }

}