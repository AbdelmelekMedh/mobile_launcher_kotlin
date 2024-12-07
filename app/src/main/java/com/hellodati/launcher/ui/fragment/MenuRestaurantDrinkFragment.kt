package com.hellodati.launcher.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellodati.launcher.EateriesMobileQuery
import com.hellodati.launcher.api.EateriesClient
import com.hellodati.launcher.ui.adapters.CategoryRestaurantDrinkAdapter
import com.hellodati.launcher.databinding.FragmentMenuRestaurantDrinkBinding
import com.hellodati.launcher.serializable_data.SerializableMenuAboutBooking
import com.hellodati.launcher.ui.adapters.RestaurantAndDrinkAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


private val ARG_LIST = "list"

/**
 * A simple [Fragment] subclass.
 * Use the [MenuRestaurantDrinkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuRestaurantDrinkFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentMenuRestaurantDrinkBinding
    private var itemPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable(ARG_LIST) as SerializableMenuAboutBooking
            itemPosition = item.itemPosition
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuRestaurantDrinkBinding.inflate(inflater, container, false)
        GlobalScope.launch (Dispatchers.Main) {
            val restaurantList = EateriesClient(requireView().context).getAllEateries()
            if (restaurantList.isEmpty()) {
                binding.rvParent.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.emptyMenu.visibility = View.VISIBLE
            } else {
                val itemReceived = restaurantList[itemPosition]
                binding.rvParent.adapter =
                    CategoryRestaurantDrinkAdapter(itemReceived!!.categories, binding, itemPosition)
                binding.progressBar.visibility = View.GONE
            }
        }

        binding.swipeContainer.setOnRefreshListener {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    try {
                        val restaurantList = EateriesClient(requireView().context).getAllEateries()
                        if (restaurantList.isEmpty()) {
                            binding.rvParent.visibility = View.GONE
                            binding.emptyMenu.visibility = View.VISIBLE
                            binding.swipeContainer.isRefreshing = false
                        } else {
                            val itemReceived = restaurantList[itemPosition]
                            binding.rvParent.adapter = CategoryRestaurantDrinkAdapter(itemReceived!!.categories, binding, itemPosition)
                            binding.swipeContainer.isRefreshing = false
                            binding.emptyMenu.visibility = View.GONE
                            binding.rvParent.visibility = View.VISIBLE
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
        fun newInstance(list: SerializableMenuAboutBooking) =
            MenuRestaurantDrinkFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LIST, list)
                }
            }
    }
}