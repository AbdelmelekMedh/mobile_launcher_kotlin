package com.hellodati.launcher.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ConciergeClient
import com.hellodati.launcher.databinding.FragmentConciergeServiceItemsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ConciergeServiceItemsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding :FragmentConciergeServiceItemsBinding

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

        binding = FragmentConciergeServiceItemsBinding.inflate(inflater, container, false)

        try {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    val conciergeClient = ConciergeClient(binding.root.context)

                    binding.rvParent.layoutManager = LinearLayoutManager(context,
                        LinearLayoutManager.VERTICAL,false)
                    if (conciergeClient.getConciergeList().isEmpty()){
                        binding.emptyMenu.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    }else{
                        binding.progressBar.visibility = View.GONE
                        Log.e("zz", conciergeClient.getConciergeList().toString())
                       // binding.rvParent.adapter = ConciergeItemsAdapter(itemReceived!!.listServices, binding)
                    }
                }
            }
        }catch (e:Exception){
            Log.e("device_gsf", e.message.toString())
        }


        binding.btnBack.setOnClickListener {
            val navController = Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            )

            navController.navigate(R.id.conciergeServicesFragment)
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BookingRestaurantFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}