package com.hellodati.launcher.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hellodati.launcher.Device_byGsfIdQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.DeviceClient
import com.hellodati.launcher.ui.adapters.CallsOptionAdapter
import com.hellodati.launcher.databinding.FragmentCallsBinding
import com.hellodati.launcher.model.CallOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CallsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CallsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentCallsBinding
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
        binding = FragmentCallsBinding.inflate(inflater, container, false)
        val view = binding.root
        // Inflate the layout for this fragment
        var contents = listOf<CallOption>(
            CallOption(
                CallOption.CallOptionCode.ROOM,
                getString(R.string.option_call_room_title),
                getString(R.string.option_call_room_desc),
                getString(R.string.option_call_room_desc_long),
                R.drawable.ic_chambre,
               ContextCompat.getColor(requireContext(), R.color.dark_blue)
            ),
            CallOption(
                CallOption.CallOptionCode.EMERGENCY,
                getString(R.string.option_call_urgent_title),
                getString(R.string.option_call_urgent_desc),
                getString(R.string.option_call_urgent_desc_long),
                R.drawable.ic_urgence,
                ContextCompat.getColor(requireContext(), R.color.shady_blue)
            ),
            CallOption(
                CallOption.CallOptionCode.ROOM_SERVICE,
                getString(R.string.option_call_room_service_title),
                getString(R.string.option_call_room_service_desc),
                getString(R.string.option_call_room_service_desc_long),
                R.drawable.ic_service_chambre,
                ContextCompat.getColor(requireContext(), R.color.half_dark_blue)
            ),
            CallOption(
                CallOption.CallOptionCode.NATIONAL,
                getString(R.string.option_call_local_title),
                getString(R.string.option_call_local_desc),
                getString(R.string.option_call_local_desc_long),
                R.drawable.ic_local,
                ContextCompat.getColor(requireContext(), R.color.half_light_blue)
            ),
            CallOption(
                CallOption.CallOptionCode.INTERNATIONAL,
                getString(R.string.option_call_international_title),

                getString(R.string.option_call_international_desc),
                getString(R.string.option_call_international_desc_long),
                R.drawable.ic_international,
                ContextCompat.getColor(requireContext(), R.color.light_blue)
            )
        )

        GlobalScope.launch (Dispatchers.Main){
            try {
                val deviceClient = DeviceClient(binding.root.context.applicationContext)
                val device: Device_byGsfIdQuery.Device_byGsfId? = deviceClient.byGsfId()
                binding.phoneNum.text = device?.sim?.number
            } catch (e: Exception) {
                Log.d("deviceNumber", "deviceNumber")
            }
        }

            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = context?.let { CallsOptionAdapter(contents, it,binding) }
            binding.recyclerView.adapter = adapter


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CallsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CallsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        val myClass = CallsFragment()
        fun setFragmentOfTabStatic() = myClass.setFragmentOfTab(myClass)
    }

     fun setFragmentOfTab(fragment: Fragment?) {
        if (fragment != null) {
            val ft =
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            ft.replace(binding.detailed.id, fragment)
            ft.addToBackStack(null)
            ft.commit()
        }
    }


}