package com.hellodati.launcher.ui.fragment

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.hellodati.launcher.R
import com.hellodati.launcher.api.IrdMenuClient
import com.hellodati.launcher.databinding.FragmentMenuDrinkBinding
import com.hellodati.launcher.serializable_data.SerializableInRoomDrink
import com.hellodati.launcher.type.IrdCategoryEnum
import com.hellodati.launcher.ui.adapters.BeverageDrinkAdapter
import com.hellodati.launcher.ui.adapters.CategoryDrinkAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_ITEM = "item"

class MenuDrinkFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentMenuDrinkBinding
    private var categoryDrinkPosition: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            val item = it.getSerializable(ARG_ITEM) as SerializableInRoomDrink
            categoryDrinkPosition = item.Position
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuDrinkBinding.inflate(inflater, container, false)
        binding.progressBar.visibility = View.VISIBLE
        binding.serviceTitle.text = resources.getString(R.string.in_room_drink)

        try {
            GlobalScope.launch {

                withContext(Dispatchers.Main) {
                    val irdMenuClient = IrdMenuClient(requireView().context)
                    val irdList = irdMenuClient.getAll(IrdCategoryEnum.drink)
                    val categoryDrinkList = irdList!![categoryDrinkPosition].items!!
                        binding.progressBar.visibility = View.GONE
                        binding.chosenContentLayout.visibility = View.VISIBLE
                        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                            binding.btnCancel,
                            PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f),
                            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f)
                        )
                        scaleDown.duration = 300
                        binding.recyclerBeverage.visibility = View.VISIBLE
                        binding.btnOrder.visibility = View.GONE
                        binding.btnCancel.visibility = View.GONE
                        binding.recyclerBeverage.layoutManager = GridLayoutManager(binding.root.context,2)
                        binding.recyclerBeverage.adapter = BeverageDrinkAdapter(categoryDrinkList,binding)
                        binding.chosenContentLayout.visibility = View.VISIBLE
                        binding.menuRestaurantFrame.visibility = View.GONE
                }
            }
        }catch (e:Exception){
            Log.e("device_gsf", e.message.toString())
        }
        binding.btnCancel.setOnClickListener {

            binding.chosenContentImage.visibility = View.GONE
            binding.chosenContentName.visibility = View.GONE
            binding.chosenContentName.visibility = View.GONE
            binding.chosenContentPrice.visibility = View.GONE
            binding.chosenContentOldprice.visibility = View.GONE
            binding.choosenContentSummery.visibility = View.GONE
            binding.chosenContentDescription.visibility = View.GONE
            binding.recyclerBeverage.visibility = View.VISIBLE
            binding.chosenContentLayout.visibility = View.VISIBLE
            binding.btnOrder.visibility = View.GONE
            binding.btnCancel.visibility = View.GONE
            binding.menuRestaurantFrame.visibility = View.GONE

        }

        binding.swipeContainer.setOnRefreshListener {
            if (binding.recyclerBeverage.visibility == View.VISIBLE
                && binding.chosenContentLayout.visibility == View.VISIBLE){
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        try {
                            val irdMenuClient = IrdMenuClient(requireView().context)
                            val irdList = irdMenuClient.getAll(IrdCategoryEnum.drink)
                            val categoryDrinkList = irdList!![categoryDrinkPosition].items!!
                            binding.progressBar.visibility = View.GONE
                            binding.chosenContentLayout.visibility = View.VISIBLE
                            val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                                binding.btnCancel,
                                PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f),
                                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f)
                            )
                            scaleDown.duration = 300
                            binding.recyclerBeverage.visibility = View.VISIBLE
                            binding.btnOrder.visibility = View.GONE
                            binding.btnCancel.visibility = View.GONE
                            binding.recyclerBeverage.layoutManager = GridLayoutManager(binding.root.context,2)
                            binding.recyclerBeverage.adapter = BeverageDrinkAdapter(categoryDrinkList,binding)
                            binding.chosenContentLayout.visibility = View.VISIBLE
                            binding.menuRestaurantFrame.visibility = View.GONE
                            binding.swipeContainer.isRefreshing = false
                        }catch (e:Exception){
                            Log.e("swipeRefresh", e.message.toString())
                        }
                    }
                }
            }else{
                binding.swipeContainer.isRefreshing = false
            }
        }

        binding.btnBack.setOnClickListener {
            if (binding.recyclerBeverage.visibility == View.VISIBLE
                && binding.chosenContentLayout.visibility == View.VISIBLE) {
                requireActivity().onBackPressed()
            }else{
                binding.chosenContentImage.visibility = View.GONE
                binding.chosenContentName.visibility = View.GONE
                binding.chosenContentName.visibility = View.GONE
                binding.chosenContentPrice.visibility = View.GONE
                binding.chosenContentOldprice.visibility = View.GONE
                binding.choosenContentSummery.visibility = View.GONE
                binding.chosenContentDescription.visibility = View.GONE
                binding.recyclerBeverage.visibility = View.VISIBLE
                binding.chosenContentLayout.visibility = View.VISIBLE
                binding.btnOrder.visibility = View.GONE
                binding.btnCancel.visibility = View.GONE
                binding.menuRestaurantFrame.visibility = View.GONE
            }

        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuDrinkFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}