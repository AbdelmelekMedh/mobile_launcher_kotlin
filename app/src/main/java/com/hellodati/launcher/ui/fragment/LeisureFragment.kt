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
import com.hellodati.launcher.api.LeisureClient
import com.hellodati.launcher.databinding.FragmentLeisureBinding
import com.hellodati.launcher.ui.adapters.LeisureAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class LeisureFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLeisureBinding

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
        binding = FragmentLeisureBinding.inflate(inflater, container, false)

        binding.serviceTitle.text = resources.getString(R.string.leisure_service)
        binding.tabs.visibility = View.GONE
        setFragmentOfTab(MenuLeisureFragment())
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            binding.msgTitle.text = getString(R.string.no_internet_connection)
            binding.emptyMenuText.text = ""
            binding.emptyMenuImage.visibility = View.GONE
            binding.emptyMenu.setBackgroundResource(R.drawable.no_connection_v2)
            binding.emptyMenu.visibility = View.VISIBLE
        }else{
            try {
                GlobalScope.launch (Dispatchers.Main) {
                    val leisureClient = LeisureClient(requireView().context)
                    val leisureList = leisureClient.getLeisureMenu()
                    if (leisureList.isEmpty()){
                        binding.emptyMenu.visibility = View.VISIBLE
                    }else{
                        binding.emptyMenu.visibility = View.GONE
                    }

                }
            }catch (e:Exception){
                Log.e("device_gsf", e.message.toString())
            }
        }

        binding.btnBack.setOnClickListener {
            Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            ).navigate(R.id.homeFragment)
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
            LeisureFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}