package com.hellodati.launcher.ui.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.hellodati.launcher.ui.fragment.ContactCallFragment
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.FragmentCallsBinding
import com.hellodati.launcher.model.CallOption
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CallsOptionAdapter(private var callsArray: List<CallOption>, var context: Context, var binding: FragmentCallsBinding) :
    RecyclerView.Adapter<CallsOptionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_call_option, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return callsArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        holder.cardColor.setColorFilter(callsArray[i].mColor)
        holder.cardIcon.setImageResource(callsArray[i].mIcon)
        holder.cardTitle.text = callsArray[i].mTitle
        holder.cardSummery.text = callsArray[i].mSummery
        holder.cardInformation.setOnClickListener(View.OnClickListener { v ->
            val myDialogLangs = Dialog(v.context)
            myDialogLangs.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val listItem: View =
                LayoutInflater.from(v.context).inflate(R.layout.popup_info, null, false)
            myDialogLangs.setContentView(listItem)
            val btn_ok = listItem.findViewById<Button>(R.id.btn_ok)
            val icon = listItem.findViewById<ImageView>(R.id.icon_call)
            val txt_title = listItem.findViewById<TextView>(R.id.title_call)
            val txt_description = listItem.findViewById<TextView>(R.id.description_call)
            icon.setImageResource(callsArray[i].mIcon)
            txt_title.text = (callsArray[i].mTitle)
            txt_description.text = (callsArray[i].mDescription)
            btn_ok.setOnClickListener { myDialogLangs.dismiss() }
            myDialogLangs.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            myDialogLangs.show()
        })

        holder.itemView.setOnClickListener {
            GlobalScope.launch {
                try {
                    ResidenceClient(context.applicationContext).byGsfId()
                }catch (e: Exception){
                    Log.e("callClient", e.message.toString())
                }
            }
            val phoneCall = Intent(Intent.ACTION_DIAL)

            when (i) {
                0 -> {
                    try {
                        showContacts(i)
                    } catch (e: Exception) {
                        Log.e("kamatcho", e.message.toString())
                    }

                }

                1 -> {
                    showContacts(i)
                }

                2 -> {
                    if (phoneCall != null){
                        phoneCall.data = Uri.parse("tel:71900555")
                        //phoneCall.putExtra("number", "71900555")
                        context.startActivity(phoneCall)
                    }else{
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_datiphone),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                3-> {
                    if (phoneCall != null){
                        context.startActivity(phoneCall)
                    }else{
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_datiphone),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                4 -> {
                    if (phoneCall != null){
                        context.startActivity(phoneCall)
                    }else{
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_datiphone),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                else -> {
                  //  Toast.makeText(context, "clicked $i", Toast.LENGTH_SHORT).show()
                }
            }


        }


    }



    private fun showContacts(i: Int) {
        try {
            binding.detailed.removeAllViews()
            binding.listOptions.visibility = View.GONE
            binding.detailed.visibility = View.VISIBLE
            val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            val call : ContactCallFragment = ContactCallFragment.newInstance(
                callsArray[i].mTitle.toString(),
                callsArray[i].mSummery.toString(),
                callsArray[i].mIcon,
                callsArray[i].mColor,
                i
            )
            ft.replace(binding.detailed.id, call)
            ft.addToBackStack(null)
            ft.commit()
        } catch (e: Exception) {

        }

        // UI_CallFragment.setFragmentOfTab(ContactListFragment())
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardColor: ImageView = itemView.findViewById(R.id.card_color)
        var cardTitle: TextView = itemView.findViewById(R.id.card_title)
        var cardSummery: TextView = itemView.findViewById(R.id.card_summery)
        var cardIcon: ImageView = itemView.findViewById(R.id.card_icon)
        var cardInformation: ImageView = itemView.findViewById(R.id.card_information)
    }
}