package com.hellodati.launcher.ui.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hellodati.launcher.databinding.FragmentRestaurantBinding
import com.hellodati.launcher.Ird_menuQuery
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


class RestaurantFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentRestaurantBinding
    private lateinit var irdList :List<Ird_menuQuery.Ird_menu>

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

        binding = FragmentRestaurantBinding.inflate(inflater, container, false)
        binding.serviceTitle.text = resources.getString(R.string.in_room_food)
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            binding.msgTitle.text = getString(R.string.no_internet_connection)
            binding.emptyMenuText.text = ""
            binding.emptyMenu.setBackgroundResource(R.drawable.no_connection_v2)
            binding.emptyMenu.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }else{
            try {
                GlobalScope.launch {

                    withContext(Dispatchers.Main) {
                        // Update UI here
                        val irdMenuClient = IrdMenuClient(requireView().context)
                        irdList = irdMenuClient.getAll(IrdCategoryEnum.food)!!
                        if (irdList.isEmpty()){
                            binding.emptyMenu.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                        }else{
                            binding.rvParent.adapter = InRoomFoodCategoriesAdapter(
                                irdList,
                                binding.root.context,
                                true
                            )
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
                            val irdMenuClient = IrdMenuClient(requireView().context)
                            irdList = irdMenuClient.getAll(IrdCategoryEnum.food)!!
                            if (irdList.isEmpty()){
                                binding.emptyMenu.visibility = View.VISIBLE
                                binding.swipeContainer.isRefreshing = false
                            }else{
                                binding.rvParent.adapter = InRoomFoodCategoriesAdapter(
                                    irdList,
                                    binding.root.context,
                                    true
                                )
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

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        return binding.root
    }
//test new branch



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
            RestaurantFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }
}