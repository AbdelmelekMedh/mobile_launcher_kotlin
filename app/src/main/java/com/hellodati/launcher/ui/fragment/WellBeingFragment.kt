package com.hellodati.launcher.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayout
import com.hellodati.launcher.R
import com.hellodati.launcher.api.WellBeingClient
import com.hellodati.launcher.databinding.FragmentWellBeingBinding
import com.hellodati.launcher.ui.adapters.CategoryWellBeingAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class WellBeingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentWellBeingBinding

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
        binding = FragmentWellBeingBinding.inflate(inflater,container,false)

        setFragmentOfTab(MenuWellBeingFragment())
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
                    val wellBeingClient = WellBeingClient(requireView().context)
                    val wellBeingList = wellBeingClient.getWellBeingMenu()
                    Log.e("wellbeing",wellBeingList.toString())
                    if (wellBeingList.isEmpty()){
                        binding.emptyMenu.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    }else{
                        binding.emptyMenu.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                    }

                }
            }catch (e:Exception){
                Log.e("device_gsf", e.message.toString())
            }
        }

        binding.btnBack.setOnClickListener {
            val navController = Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            )

            navController.navigate(R.id.homeFragment)
        }

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        setFragmentOfTab(MenuWellBeingFragment())
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

                        setFragmentOfTab(BookingWellBeingFragment())

                    }

                    2 -> {
                        //setFragmentOfTab(AboutWellBeingFragment())
                    }

                    3 -> {
                       // setFragmentOfTab(ReviewsDrinkFragment())
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

/*        val bookingTabToRemove = binding.tabs.getTabAt(1)
        if (bookingTabToRemove != null) {
            binding.tabs.removeTab(bookingTabToRemove)
        }*/
        val reviewTabToRemove = binding.tabs.getTabAt(3)
        if (reviewTabToRemove != null) {
            binding.tabs.removeTab(reviewTabToRemove)
        }
        binding.tabs.removeTab(binding.tabs.getTabAt(2))

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
            WellBeingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}