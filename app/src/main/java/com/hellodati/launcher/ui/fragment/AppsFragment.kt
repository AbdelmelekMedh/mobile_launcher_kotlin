package com.hellodati.launcher.ui.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.hellodati.launcher.ui.adapters.AppsFragmentAdapter
import com.hellodati.launcher.databinding.FragmentAppsBinding
import com.hellodati.launcher.model.Apps
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AppsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentAppsBinding

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
        binding = FragmentAppsBinding.inflate(inflater, container, false)
        val view = binding.root

        // Inflate the layout for this fragment
        binding.recyclerApps.layoutManager = GridLayoutManager(context,4)
        val adapter = getInstalledApps().let { context?.let { it1 ->
            AppsFragmentAdapter(
                it,
                it1
            )
        } }
        binding.recyclerApps.adapter = adapter

        return view
        //return inflater.inflate(R.layout.fragment_apps, container, false)
    }

    fun GetAppsList(): ArrayList<Apps>? {
        var allowedApps: List<String?>? = null

        val pm: PackageManager = requireContext().packageManager

        //new add
        val appListSpec: ArrayList<Apps> = ArrayList<Apps>()
        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)
        val allApps = pm.queryIntentActivities(i, 0)
        for (ri in allApps) {
   /*         val app = Apps()
            app.label = ri.loadLabel(pm).toString()
            app.packageName = ri.activityInfo.packageName
            app.icon = ri.activityInfo.loadIcon(pm)
            if (!app.packageName.equals(requireContext().packageName)) {
                    if (allowedApps != null) {
                        if (allowedApps.contains(app.packageName)) {
                            appListSpec.add(app)
                        }
                    }else {
                    Log.e("**************", "Installed package :" + app.packageName)
                    appListSpec.add(app)
                }
            }*/
        }
        return appListSpec
    }

    private fun getInstalledApps(): List<Apps> {
        val packageManager = requireContext().packageManager
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val appList = mutableListOf<Apps>()
        
        val listPackageName = listOf(
            "com.hellodati.wificonfig",
            "com.hellodati.torch"
        )
        for (app in apps) {
            val packageName = app.packageName
            if (packageName in listPackageName) {
                val appName = packageManager.getApplicationLabel(app).toString()
                val appIcon = packageManager.getApplicationIcon(app)
                appList.add(Apps(appName, packageName, appIcon))
            }
        }
        return appList
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AppsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}