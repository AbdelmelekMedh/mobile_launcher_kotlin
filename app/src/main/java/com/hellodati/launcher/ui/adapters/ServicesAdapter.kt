package com.hellodati.launcher.ui.adapters

import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ClickClient
import com.hellodati.launcher.api.GraphQLClient
import com.hellodati.launcher.api.OrderClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.api.RoomPhones
import com.hellodati.launcher.databinding.CsiLayoutBinding
import com.hellodati.launcher.databinding.DialogDeviceShakeBinding
import com.hellodati.launcher.databinding.ItemServiceBinding
import com.hellodati.launcher.model.Services
import com.hellodati.launcher.type.CreateOrderInput
import com.hellodati.launcher.type.ServiceEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ServicesAdapter(
    private var servicesArray: List<Services>,
    var context: Context,
    var binding: ItemServiceBinding,
    var navController: NavController
) :
    RecyclerView.Adapter<ServicesAdapter.ViewHolder>() {

    lateinit var dialog: Dialog
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)

        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return servicesArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {

        when (i) {
            0 -> {
                holder.title.setTextColor(Color.parseColor("#739147"))
                holder.arrowView.setColorFilter(Color.parseColor("#739147"))
            }

            else -> {
                holder.title.setTextColor(Color.parseColor("#ffffff"))
                holder.arrowView.setColorFilter(Color.parseColor("#ffffff"))
            }
        }

        holder.title.text = servicesArray[i].title
        holder.imageService.setImageResource(servicesArray[i].image!!)



        holder.itemView.setOnClickListener {

            when (i) {
                0 -> {
                    try {
                            GlobalScope.launch {

                                withContext(Dispatchers.Main) {
                                    val clickClient = ClickClient(binding.root.context)
                                    clickClient.createClick(ResidenceClient(binding.root.context).getResidenceId().toString(),"",ServiceEnum.IrdEat)
                                }
                            }
                        navController.navigate(R.id.restaurentFragment)
                    } catch (e: Exception) {

                    }

                }

                1 -> {

                    try {
                        GlobalScope.launch {

                            withContext(Dispatchers.Main) {
                                val clickClient = ClickClient(binding.root.context)
                                clickClient.createClick(ResidenceClient(binding.root.context).getResidenceId().toString(),"",ServiceEnum.IrdDrinks)
                            }
                        }
                        navController.navigate(R.id.drinksFragment)
                    } catch (e: Exception) {

                    }


                }

                2 -> {
                    try {
                        //navController.navigate(R.id.menuRestaurantAndDrinkFragment)
                        GlobalScope.launch {

                            withContext(Dispatchers.Main) {
                                val clickClient = ClickClient(binding.root.context)
                                clickClient.createClick(ResidenceClient(binding.root.context).getResidenceId().toString(),"",ServiceEnum.Eatery)
                            }
                        }
                        navController.navigate(R.id.restaurantAndDrinkFragment)
                    } catch (e: Exception) {

                    }
                }

                3 -> {
                    try {
                        GlobalScope.launch {

                            withContext(Dispatchers.Main) {
                                val clickClient = ClickClient(binding.root.context)
                                clickClient.createClick(ResidenceClient(binding.root.context).getResidenceId().toString(),"",ServiceEnum.Wellbeing)
                            }
                        }
                        navController.navigate(R.id.wellBeingFragment)
                    } catch (e: Exception) {

                    }
                }

                4 -> {
                    try {
                        GlobalScope.launch {

                            withContext(Dispatchers.Main) {
                                val clickClient = ClickClient(binding.root.context)
                                clickClient.createClick(ResidenceClient(binding.root.context).getResidenceId().toString(),"",ServiceEnum.Events)
                            }
                        }
                        navController.navigate(R.id.menuEventFragment)
                    } catch (e: Exception) {

                    }
                }

                5 -> {
                    try {
                        GlobalScope.launch {

                            withContext(Dispatchers.Main) {
                                val clickClient = ClickClient(binding.root.context)
                                clickClient.createClick(ResidenceClient(binding.root.context).getResidenceId().toString(),"",ServiceEnum.Leisure)
                            }
                        }
                        navController.navigate(R.id.leisureFragment)
                    } catch (e: Exception) {

                    }
                }

                6 -> {
                    try {
                        GlobalScope.launch {

                            withContext(Dispatchers.Main) {
                                val clickClient = ClickClient(binding.root.context)
                                clickClient.createClick(ResidenceClient(binding.root.context).getResidenceId().toString(),"",ServiceEnum.Concierge)
                            }
                        }
                        navController.navigate(R.id.conciergeFragment)
                    } catch (e: Exception) {

                    }
                }

                7 -> {
                    try {
                        GlobalScope.launch {

                            withContext(Dispatchers.Main) {
                                val clickClient = ClickClient(binding.root.context)
                                clickClient.createClick(ResidenceClient(binding.root.context).getResidenceId().toString(),"",ServiceEnum.Meetings)
                            }
                        }
                        navController.navigate(R.id.meetingFragment)
                    } catch (e: Exception) {

                    }
                }
                8 -> {
                    try {
                        GlobalScope.launch {

                            /*withContext(Dispatchers.Main) {
                                val clickClient = ClickClient(binding.root.context)
                                clickClient.createClick(ResidenceClient(binding.root.context).getResidenceId().toString(),"",ServiceEnum.Leisure)
                            }*/
                        }
                        navController.navigate(R.id.surveyListFragment)
                    } catch (e: Exception) {

                    }
                }

                9 -> {
                    try {

                        navController.navigate(R.id.contactUsFragment)
                    } catch (e: Exception) {

                    }
                }



                else -> {
                    Toast.makeText(context, "clicked $i", Toast.LENGTH_SHORT).show()
                }
            }


        }


    }

    private fun showPopup2() {
        dialog.setContentView(R.layout.dialog_device_shake)

        dialog.show()
    }

    private fun showPopup() {
        val binding2: CsiLayoutBinding = CsiLayoutBinding.inflate(LayoutInflater.from(context))


        val dialog = Dialog(context)
        dialog.setContentView(binding2.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        dialog.show()
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageService: ImageView = itemView.findViewById(R.id.image_view)
        var arrowView: ImageView = itemView.findViewById(R.id.arrow_view)
        var title: TextView = itemView.findViewById(R.id.title)
        var txtDescHotel: TextView = itemView.findViewById(R.id.txt_desc_hotel)

    }
}