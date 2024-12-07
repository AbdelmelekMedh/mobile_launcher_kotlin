package ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hellodati.launcher.api.ConciergeClient
import com.hellodati.launcher.api.IrdMenuClient
import com.hellodati.launcher.databinding.FragmentConciergeServicesBinding
import com.hellodati.launcher.type.IrdCategoryEnum
import com.hellodati.launcher.ui.adapters.ConciergeAdapter
import com.hellodati.launcher.ui.adapters.InRoomFoodCategoriesAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class ConciergeServicesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentConciergeServicesBinding



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

        binding = FragmentConciergeServicesBinding.inflate(inflater, container, false)


        binding.progressBar.visibility = View.VISIBLE

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
                        binding.rvParent.adapter = ConciergeAdapter(conciergeClient.getConciergeList(), binding)
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
                        val conciergeClient = ConciergeClient(binding.root.context)
                        binding.rvParent.layoutManager = LinearLayoutManager(context,
                            LinearLayoutManager.VERTICAL,false)
                        if (conciergeClient.getConciergeList().isEmpty()){
                            binding.emptyMenu.visibility = View.VISIBLE
                            binding.swipeContainer.isRefreshing = false
                        }else{
                            binding.rvParent.adapter = ConciergeAdapter(conciergeClient.getConciergeList(), binding)
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
            ConciergeServicesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}