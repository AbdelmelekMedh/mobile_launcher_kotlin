package com.hellodati.launcher.ui.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hellodati.launcher.GsiAvailableQuestionsQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.GsiAvailableQuestionsClient
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.FragmentSurveyListBinding
import com.hellodati.launcher.ui.adapters.SurveyAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SurveyListFragment: Fragment() {

    private lateinit var binding: FragmentSurveyListBinding
    private lateinit var GsiAQ: List<GsiAvailableQuestionsQuery.GsiAvailableQuestion>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSurveyListBinding.inflate(inflater, container,false)

        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            binding.msgTitle.text = getString(R.string.no_internet_connection)
            binding.emptyMenuText.text = ""
            binding.emptyMenuImage.visibility = View.GONE
            binding.emptyMenu.setBackgroundResource(R.drawable.no_connection_v2)
            binding.emptyMenu.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }else{
            GlobalScope.launch (Dispatchers.Main){
                try {
                    val ResidenceId = ResidenceClient(binding.root.context).getResidenceId().toString()
                    val avilaboleQuestionsClient = GsiAvailableQuestionsClient(requireView().context)
                    GsiAQ = avilaboleQuestionsClient.getAvailableGsiQuestions(ResidenceId)!!
                    if (GsiAQ.isEmpty()){
                        binding.emptyMenu.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    }else{
                        binding.rvParent.adapter = SurveyAdapter(GsiAQ,binding)
                        binding.progressBar.visibility = View.GONE
                    }
                    binding.rvParent.adapter = SurveyAdapter(GsiAQ,binding)
                }catch (e:Exception){
                    Log.e("avilaboleQuestionsClient", GsiAQ.toString())
                }
            }
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }
}