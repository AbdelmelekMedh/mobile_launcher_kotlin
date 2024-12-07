package com.hellodati.launcher.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hellodati.launcher.R
import com.hellodati.launcher.api.GlobalSettingsClient
import com.hellodati.launcher.ui.adapters.ServicesAdapter
import com.hellodati.launcher.databinding.FragmentHome2Binding
import com.hellodati.launcher.databinding.ItemServiceBinding
import com.hellodati.launcher.model.Services
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHome2Binding
    private lateinit var bindingItem: ItemServiceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHome2Binding.inflate(inflater, container, false)
        bindingItem = ItemServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)
        loadServices(navController)
    }

    private suspend fun fetchGlobalSettingsImages(globalSettingsClient: GlobalSettingsClient) {
        withContext(Dispatchers.Main) {
            val settings = globalSettingsClient.getSettings()

            val hotelLinksPreferences = context?.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
            Glide.with(binding.root.context)
                .load("${hotelLinksPreferences?.getString("api_files_server", null)}/picture/globalSettings_pictures/${settings?.id}_${settings?.cover}?height=800")
                .into(binding.hotelImage)

            Glide.with(binding.root.context)
                .load("${hotelLinksPreferences?.getString("api_files_server", null)}/picture/globalSettings_pictures/${settings?.id}_${settings?.defaultLogo}?height=300")
                .into(binding.hotelLogo)

            binding.hotelStars.rating = settings?.rating?.toFloat() ?: 0f

            val language = LocalHelper.getLanguage(binding.root.context).toString()
            val title = when (language) {
                "en" -> settings?.name?.en
                "ar" -> settings?.name?.ar
                "fr" -> settings?.name?.fr
                else -> null
            }
            binding.hotelTitle.text = title.takeUnless { it.isNullOrEmpty() } ?: settings?.name?.default
        }
    }

    private fun loadServices(navController: NavController) {
        val contents = listOf(
            Services(1, resources.getString(R.string.in_room_food), "", R.drawable.restaurant_service),
            Services(2, resources.getString(R.string.in_room_drink), "", R.drawable.boisson_service),
            Services(3, resources.getString(R.string.restaurant_and_bar), "", R.drawable.service_restaurants_bars),
            Services(4, resources.getString(R.string.well_being), "", R.drawable.well_being_service),
            Services(5, resources.getString(R.string.event_service), "", R.drawable.evenement_service),
            Services(6, resources.getString(R.string.leisure_service), "", R.drawable.loisir_service),
            Services(7, resources.getString(R.string.concierge_service), "", R.drawable.concierge_service),
            Services(8, resources.getString(R.string.meeting_service), "", R.drawable.meeting_service),
            Services(9, getString(R.string.survey), "", R.drawable.survey),
            Services(10, resources.getString(R.string.contact_us_service), "", R.drawable.contact_us_service),
            //Services(10, resources.getString(R.string.contact_us_service), "", R.drawable.loisir3),
            // Add more services here
        )

        try {
            GlobalScope.launch {
                val globalSettingsClient = GlobalSettingsClient(binding.root.context)
                fetchGlobalSettingsImages(globalSettingsClient)
            }
        } catch (e: Exception) {
            Log.e("errorSettings", e.message.toString())
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = ServicesAdapter(contents, requireContext(), bindingItem, navController)
        binding.recyclerView.adapter = adapter

        binding.swipeContainer.setOnRefreshListener {
            binding.recyclerView.adapter = adapter
            binding.swipeContainer.isRefreshing = false
        }
    }
}
