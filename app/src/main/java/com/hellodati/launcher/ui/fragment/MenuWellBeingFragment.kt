package com.hellodati.launcher.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellodati.launcher.WellBeing_mobileQuery
import com.hellodati.launcher.api.IrdMenuClient
import com.hellodati.launcher.ui.adapters.CategoryWellBeingAdapter
import com.hellodati.launcher.api.WellBeingClient
import com.hellodati.launcher.databinding.FragmentMenuWellBeingBinding
import com.hellodati.launcher.databinding.FragmentWellBeingBinding
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

class MenuWellBeingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentMenuWellBeingBinding
    private lateinit var bindingWellBeing: FragmentWellBeingBinding
    private lateinit var wellBeingList :List<WellBeing_mobileQuery.WellBeing_mobile>

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

        binding =  FragmentMenuWellBeingBinding.inflate(inflater, container, false)
        bindingWellBeing =  FragmentWellBeingBinding.inflate(inflater, container, false)
        try {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    // Update UI here
                    val wellBeingClient = WellBeingClient(requireView().context)
                    wellBeingList = wellBeingClient.getWellBeingMenu()
                    Log.e("wellbeing",wellBeingList.toString())
                    if (wellBeingList.isEmpty()){
                        //binding.emptyMenu.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    }else{
                        binding.rvParent.adapter = CategoryWellBeingAdapter(wellBeingList, binding)
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
                        val wellBeingClient = WellBeingClient(requireView().context)
                        wellBeingList = wellBeingClient.getWellBeingMenu()
                        if (wellBeingList.isEmpty()){
                            //binding.emptyMenu.visibility = View.VISIBLE
                            binding.swipeContainer.isRefreshing = false
                        }else{
                            //binding.emptyMenu.visibility = View.GONE
                            binding.rvParent.adapter = CategoryWellBeingAdapter(wellBeingList, binding)
                            binding.swipeContainer.isRefreshing = false
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
            MenuWellBeingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}