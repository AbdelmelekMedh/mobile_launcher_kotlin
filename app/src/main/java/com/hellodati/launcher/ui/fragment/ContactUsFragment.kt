package com.hellodati.launcher.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.hellodati.launcher.R
import com.hellodati.launcher.api.GlobalSettingsClient
import com.hellodati.launcher.databinding.FragmentContactUsBinding
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ContactUsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentContactUsBinding
    private lateinit var phoneNumber: String
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var facebookLink: String
    private lateinit var twitterLink: String
    private lateinit var youtubeLink: String
    private lateinit var website: String
    private lateinit var tripadvisor: String

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

        binding = FragmentContactUsBinding.inflate(inflater, container, false)
        binding.serviceTitle.text = requireContext().getString(R.string.contact_us_service)

        binding.btnContactUs.visibility = View.GONE
        binding.btnContactUs.setOnClickListener {
            val myDialog = Dialog(binding.root.context)
            myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val listItem = LayoutInflater.from(binding.root.context)
                .inflate(R.layout.popup_contact_us, null, false)
            myDialog.setContentView(listItem)

            val mailSender = listItem.findViewById<EditText>(R.id.edit_email)
            val mail = listItem.findViewById<EditText>(R.id.editText_Email)
            val subject = listItem.findViewById<EditText>(R.id.subject)

            val btn_send = listItem.findViewById<Button>(R.id.btn_sendContact)
            val btn_cancel = listItem.findViewById<ImageView>(R.id.btn_cancelContact)
            btn_cancel.setOnClickListener {
                myDialog.dismiss()
            }

            btn_send.setOnClickListener {
                //send mail to the hotel by using API
                myDialog.dismiss()
            }

            myDialog.setCancelable(false)
            myDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            myDialog.show()
        }
        binding.btnBack.setOnClickListener {
            val navController = Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            )

            navController.navigate(R.id.homeFragment)
        }


        try {
            GlobalScope.launch {

                withContext(Dispatchers.Main) {
                    val globalSettingsClient = GlobalSettingsClient(binding.root.context)
                    Log.e("ggs",globalSettingsClient.getSettings().toString())
                    try {
                        phoneNumber = globalSettingsClient.getSettings()?.phone?.number.toString()
                        longitude = globalSettingsClient.getSettings()?.longitude.toString()
                        latitude = globalSettingsClient.getSettings()?.latitude.toString()
                        facebookLink = globalSettingsClient.getSettings()?.links?.facebook.toString()
                        twitterLink = globalSettingsClient.getSettings()?.links?.twitter.toString()
                        youtubeLink = globalSettingsClient.getSettings()?.links?.youtube.toString()
                        website = globalSettingsClient.getSettings()?.links?.website.toString()
                        tripadvisor = globalSettingsClient.getSettings()?.tripAdvisor.toString()
                    }catch (e:Exception){}


                    try {
                        val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)

                        Glide.with(binding.root.context)
                            .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/globalSettings_pictures/${globalSettingsClient.getSettings()?.id}_${globalSettingsClient.getSettings()?.defaultLogo}?height=300")
                            .into(binding.hotelLogo)

                        binding.hotelStars.rating = globalSettingsClient.getSettings()?.rating?.toFloat()!!


                        val title = when (LocalHelper.getLanguage(binding.root.context).toString()) {
                            "en" -> globalSettingsClient.getSettings()?.name?.en
                            "ar" -> globalSettingsClient.getSettings()?.name?.ar
                            "fr" -> globalSettingsClient.getSettings()?.name?.fr
                            else -> null
                        }
                        binding.hotelName.text = title.takeUnless { it.isNullOrEmpty() } ?: globalSettingsClient.getSettings()?.name?.default

                    }catch (e:Exception){

                    }

                    val phone = globalSettingsClient.getSettings()?.phone?.number
                    val fax = globalSettingsClient.getSettings()?.fax?.number
                    val linkWebSite = globalSettingsClient.getSettings()?.links?.website

                    val text = when {
                        phone != null && fax != null -> "$phone - $fax"
                        phone != null -> phone
                        fax != null -> fax
                        else -> ""
                    }

                    binding.hotelTel.text = text

                    binding.hotelAddress.text = globalSettingsClient.getSettings()?.address

                    binding.hotelMail.text = globalSettingsClient.getSettings()?.email
                    binding.hotelWebsite.text = "${linkWebSite ?: ""}"

                    if (tripadvisor !=  "null"){
                        binding.hotelTripadvisor.text = tripadvisor
                    }


                    binding.hotelCallLayout.setOnClickListener {
                        val phoneCall = Intent(Intent.ACTION_DIAL)
                        if (phoneCall != null) {
                            phoneCall.data = Uri.parse("tel:$phoneNumber")

                            requireContext().startActivity(phoneCall)
                        } else {
                            Toast.makeText(
                                context,
                                requireContext().getString(R.string.error_datiphone),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    binding.mapLayout.setOnClickListener {
                        val uri =
                            Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude")
                        requireContext().startActivity(Intent(Intent.ACTION_VIEW, uri))
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_up,
                            R.anim.slide_out_up
                        )
                    }

                    binding.redirectWebsite.setOnClickListener {
                        val uri =
                            Uri.parse("https://$website")
                        requireContext().startActivity(Intent(Intent.ACTION_VIEW, uri))
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_up,
                            R.anim.slide_out_up
                        )
                    }

                    binding.redirectTripadvisor.setOnClickListener {
                        val uri =
                            Uri.parse("https://$tripadvisor")
                        requireContext().startActivity(Intent(Intent.ACTION_VIEW, uri))
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_up,
                            R.anim.slide_out_up
                        )
                    }

                    binding.hotelFacebook.setOnClickListener {
                        val uri = Uri.parse("https://$facebookLink")
                        requireContext().startActivity(Intent(Intent.ACTION_VIEW, uri))
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_up,
                            R.anim.slide_out_up
                        )
                    }


                    binding.hotelTwitter.setOnClickListener {
                        val uri = Uri.parse("https://$twitterLink")
                        requireContext().startActivity(Intent(Intent.ACTION_VIEW, uri))
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_up,
                            R.anim.slide_out_up
                        )
                    }

                    binding.hotelYoutube.setOnClickListener {
                        val uri = Uri.parse("https://$youtubeLink")
                        requireContext().startActivity(Intent(Intent.ACTION_VIEW, uri))
                        requireActivity().overridePendingTransition(
                            R.anim.slide_in_up,
                            R.anim.slide_out_up
                        )
                    }


                }
            }
        } catch (e: Exception) {
            Log.e("errorSettings", e.message.toString())
        }




        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactUsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}