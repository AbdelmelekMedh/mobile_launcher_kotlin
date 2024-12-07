package com.hellodati.launcher.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.hellodati.launcher.R
import com.hellodati.launcher.ui.adapters.ContactsCallsAdapter
import com.hellodati.launcher.api.RoomPhones
import com.hellodati.launcher.databinding.FragmentCallsBinding
import com.hellodati.launcher.databinding.FragmentContactCallBinding
import com.hellodati.launcher.model.Contacts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"
private const val ARG_PARAM5 = "param5"

/**
 * A simple [Fragment] subclass.
 * Use the [ContactCallFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactCallFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var param3: Int? = null
    private var param4: Int? = null
    private var callItemClicked: Int? = null
    private lateinit var contents :List<Contacts>
    private lateinit var binding: FragmentContactCallBinding
    private lateinit var bindingCall: FragmentCallsBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            param3 = it.getInt(ARG_PARAM3)
            param4 = it.getInt(ARG_PARAM4)
            callItemClicked = it.getInt(ARG_PARAM5)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentContactCallBinding.inflate(inflater, container, false)
        bindingCall = FragmentCallsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            when (callItemClicked) {
                0 -> {
                    try {
                        GlobalScope.launch {

                            withContext(Dispatchers.Main) {

                                val roomPhones = RoomPhones(requireView().context)
                                contents = listOf<Contacts>(
                                    Contacts(
                                        1,
                                        getString(R.string.option_call_room_title),
                                        getString(R.string.option_call_room_desc),
                                        R.drawable.ic_chambre,
                                        ContextCompat.getColor(requireContext(), R.color.dark_blue)
                                    )
                                )

                                binding.cardTitle.text = arguments?.getString("param1").toString()
                                binding.cardSummery.text = requireArguments().getString("param2")
                                binding.header.setCardBackgroundColor(requireArguments().getInt("param3"))
                                binding.cardColor.setCardBackgroundColor(requireArguments().getInt("param3"))
                                if (roomPhones.getAllRooms().isNotEmpty()){
                                    binding.recyclerContactList.layoutManager = LinearLayoutManager(context)
                                    binding.recyclerContactList.adapter = ContactsCallsAdapter(contents,view.context,callItemClicked,roomPhones.getAllRooms())
                                }
                                //binding.cardIcon.setImageResource(requireArguments().getInt("param4"))
                            }
                        }
                    }catch (e:Exception){
                        Log.e("device_gsf", e.message.toString())
                    }
                }
                1 -> {
                    contents = listOf<Contacts>(
                        Contacts(
                            1,
                            getString(R.string.call_Police),
                            "197",
                            R.drawable.ic_chambre,
                            ContextCompat.getColor(requireContext(), R.color.dark_blue)
                        ),
                        Contacts(
                            2,
                            resources.getString(R.string.call_SAMU),
                            "190",
                            R.drawable.ic_chambre,
                            ContextCompat.getColor(requireContext(), R.color.dark_blue)
                        ),
                        Contacts(
                            3,
                            resources.getString(R.string.call_Fire_brigade),
                            "190",
                            R.drawable.ic_chambre,
                            ContextCompat.getColor(requireContext(), R.color.dark_blue)
                        ),
                        Contacts(
                            4,
                            resources.getString(R.string.call_aeroport),
                            "71754000",
                            R.drawable.ic_chambre,
                            ContextCompat.getColor(requireContext(), R.color.dark_blue)
                        )
                    )

                    binding.cardTitle.text = arguments?.getString("param1").toString()
                    binding.cardSummery.text = requireArguments().getString("param2")
                    binding.header.setCardBackgroundColor(requireArguments().getInt("param3"))
                    binding.cardColor.setCardBackgroundColor(requireArguments().getInt("param3"))
                    if (contents.isNotEmpty()){
                        binding.recyclerContactList.layoutManager = LinearLayoutManager(context)
                        binding.recyclerContactList.adapter = ContactsCallsAdapter(contents,view.context,callItemClicked,null)
                    }
                }

                else -> {
             /*       binding.recyclerContactList.layoutManager = LinearLayoutManager(context)
                    var adapter = ContactsCallsAdapter(
                        contents,
                        view.context,
                        callItemClicked,
                        roomPhones.getAllRooms()
                    )
                    binding.recyclerContactList.adapter = adapter



                    binding.cardTitle.text = arguments?.getString("param1").toString()
                    binding.cardSummery.text = requireArguments().getString("param2")
                    binding.header.setCardBackgroundColor(requireArguments().getInt("param3"))
                    binding.cardColor.setCardBackgroundColor(requireArguments().getInt("param3"))
                    binding.cardIcon.setImageResource(requireArguments().getInt("param4"))*/
                }
            }







            //binding.cardIcon.setImageResource(requireArguments().getInt("mIcon"))
        } catch (e: Exception) {
            Log.e("khalilos", e.message.toString())
        }


        val navController = Navigation.findNavController(view)
        binding.btnBack.setOnClickListener {
            try {
                navController.navigate(R.id.callsFragment)

            } catch (e: Exception) {

            }

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: Int, param4: Int,callClickedPosition: Int) =
            ContactCallFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putInt(ARG_PARAM3, param3)
                    putInt(ARG_PARAM4, param4)
                    putInt(ARG_PARAM5, callClickedPosition)
                }
            }
    }
}