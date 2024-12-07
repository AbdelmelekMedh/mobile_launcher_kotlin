package com.hellodati.launcher.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellodati.launcher.Leisures_mobileQuery
import com.hellodati.launcher.api.IrdMenuClient
import com.hellodati.launcher.api.LeisureClient
import com.hellodati.launcher.databinding.FragmentLeisureBinding
import com.hellodati.launcher.databinding.FragmentMenuLeisureBinding
import com.hellodati.launcher.type.IrdCategoryEnum
import com.hellodati.launcher.ui.adapters.InRoomFoodCategoriesAdapter
import com.hellodati.launcher.ui.adapters.LeisureAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MenuLeisureFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentMenuLeisureBinding
    private lateinit var bindingLeisureFragment: FragmentLeisureBinding
    private lateinit var leisureList :List<Leisures_mobileQuery.Leisures_mobile>

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
        binding = FragmentMenuLeisureBinding.inflate(inflater, container, false)
        bindingLeisureFragment = FragmentLeisureBinding.inflate(inflater, container, false)

        try {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    // Update UI here
                    val leisureClient = LeisureClient(requireView().context)
                    leisureList = leisureClient.getLeisureMenu()
                    Log.e("leisureList",leisureList.toString())
                    if (leisureList.isEmpty()){
                        binding.emptyMenu.visibility = View.VISIBLE
                        binding.swipeContainer.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                    }else{
                        binding.recyclerLeisureActivites.adapter = LeisureAdapter(leisureList, binding)
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
                        val leisureClient = LeisureClient(requireView().context)
                        leisureList = leisureClient.getLeisureMenu()
                        Log.e("leisureList",leisureList.toString())
                        if (leisureList.isEmpty()){
                            binding.emptyMenu.visibility = View.VISIBLE
                            binding.swipeContainer.isRefreshing = false
                        }else{
                            binding.recyclerLeisureActivites.adapter = LeisureAdapter(leisureList, binding)
                            binding.swipeContainer.isRefreshing = false
                            binding.emptyMenu.visibility = View.GONE
                        }
                    }catch (e:Exception){
                        Log.e("swipeRefresh", e.message.toString())
                    }
                }
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuLeisureFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}