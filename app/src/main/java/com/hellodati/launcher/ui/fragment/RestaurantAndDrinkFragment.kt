package com.hellodati.launcher.ui.fragment

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.hellodati.launcher.ui.adapters.RestaurantAndDrinkAdapter
import com.hellodati.launcher.api.EateriesClient
import com.hellodati.launcher.databinding.FragmentRestaurantAndDrinkBinding
import com.hellodati.launcher.model.RestaurantModel
import com.google.android.material.tabs.TabLayout
import com.hellodati.launcher.R
import com.hellodati.launcher.api.IrdMenuClient
import com.hellodati.launcher.type.IrdCategoryEnum
import com.hellodati.launcher.ui.adapters.InRoomFoodCategoriesAdapter
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
 * Use the [RestaurantAndDrinkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RestaurantAndDrinkFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentRestaurantAndDrinkBinding

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
        binding = FragmentRestaurantAndDrinkBinding.inflate(inflater, container, false)


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
            binding.emptyMenuImage.visibility = View.GONE
            binding.emptyMenu.setBackgroundResource(R.drawable.no_connection_v2)
            binding.emptyMenu.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }else{
            try {
                GlobalScope.launch {

                    withContext(Dispatchers.Main) {
                        val eateries = EateriesClient(requireView().context).getAllEateries()
                        if (eateries.isEmpty()){
                            binding.emptyMenu.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                        }else{
                            binding.rvParent.adapter = RestaurantAndDrinkAdapter(eateries, binding)
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }catch (e:Exception){
                Log.e("device_gsf", e.message.toString())
            }

            binding.swipeContainer.setOnRefreshListener {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        try {
                            val eateries = EateriesClient(requireView().context).getAllEateries()
                            if (eateries.isEmpty()){
                                binding.emptyMenu.visibility = View.VISIBLE
                                binding.swipeContainer.isRefreshing = false
                            }else{
                                binding.rvParent.adapter = RestaurantAndDrinkAdapter(eateries, binding)
                                binding.swipeContainer.isRefreshing = false
                                binding.emptyMenu.visibility = View.GONE
                            }
                        }catch (e:Exception){
                            Log.e("swipeRefresh", e.message.toString())
                        }
                    }
                }
            }
        }
        try {
            GlobalScope.launch {

                withContext(Dispatchers.Main) {
                    val eateries = EateriesClient(requireView().context).getAllEateries()
                    if (eateries.isEmpty()){
                        binding.emptyMenu.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    }else{
                        binding.rvParent.adapter = RestaurantAndDrinkAdapter(eateries, binding)
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }catch (e:Exception){
            Log.e("device_gsf", e.message.toString())
        }

        binding.swipeContainer.setOnRefreshListener {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    try {
                        val eateries = EateriesClient(requireView().context).getAllEateries()
                        if (eateries.isEmpty()){
                            binding.emptyMenu.visibility = View.VISIBLE
                            binding.swipeContainer.isRefreshing = false
                        }else{
                            binding.rvParent.adapter = RestaurantAndDrinkAdapter(eateries, binding)
                            binding.swipeContainer.isRefreshing = false
                            binding.emptyMenu.visibility = View.GONE
                        }
                    }catch (e:Exception){
                        Log.e("swipeRefresh", e.message.toString())
                    }
                }
            }
        }

        binding.serviceTitle.text = resources.getString(R.string.restaurant_and_bar)
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                 /*   0 -> {
                        setFragmentOfTab(MenuRestaurantFragment())
                    }*/

           /*         1 -> {
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

                        setFragmentOfTab(BookingRestaurantDrinkFragment())

                    }*/

                 /*   2 -> {
                        setFragmentOfTab(AboutRestaurantDrinkFragment())
                    }*/

                    3 -> {
                        Log.e("review","test")
                        setFragmentOfTab(ReviewsRestaurantDrinkFragment())
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
        val reviewTabToRemove = binding.tabs.getTabAt(3)
        if (reviewTabToRemove != null) {
            binding.tabs.removeTab(reviewTabToRemove)
        }
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RestaurantAndDrinkFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RestaurantAndDrinkFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}