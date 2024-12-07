package com.hellodati.launcher.ui.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hellodati.launcher.R
import com.hellodati.launcher.RoomPhonesQuery
import com.hellodati.launcher.model.Contacts

class ContactsCallsAdapter(
    private var contactsArray: List<Contacts>,
    private var context: Context,
    private var callItemClicked: Int?,
    private var allRooms: List<RoomPhonesQuery.RoomPhone>?,

    ) : RecyclerView.Adapter<ContactsCallsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (allRooms.isNullOrEmpty()) {
            return contactsArray!!.size
        }
        return allRooms!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val phoneCall = Intent(Intent.ACTION_DIAL)
        //val phoneCall = context.packageManager.getLaunchIntentForPackage("com.simplemobiletools.dialer")
        when (callItemClicked) {
            0 -> {

                    holder.contactNumber!!.text = allRooms!![i].roomPhone
                    holder.contactNumber1!!.text = allRooms!![i].roomNumber.toInt().toString()
                    holder.contactColor?.setCardBackgroundColor(contactsArray[0].color!!)
                    holder.contactIcon?.setImageResource(contactsArray[0].mIcon!!)



                holder.itemView.setOnClickListener {

                    if (phoneCall != null){
                        //phoneCall.putExtra("number", contactsArray[i].simNumber.toString())
                        phoneCall.data = Uri.parse("tel:${allRooms!![i].roomPhone}")
                        context.startActivity(phoneCall)
                    }else{
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_datiphone),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            1 -> {
                holder.contactNumber!!.text = contactsArray[i].title
                holder.contactNumber1!!.text = contactsArray[i].simNumber
                holder.contactColor?.setCardBackgroundColor(contactsArray[i].color!!)
                holder.contactIcon?.setImageResource(contactsArray[i].mIcon!!)
                holder.itemView.setOnClickListener {

                    if (phoneCall != null){
                        phoneCall.data = Uri.parse("tel:${contactsArray[i].simNumber}")
                        //phoneCall.putExtra("tel", contactsArray[i].simNumber.toString())
                        context.startActivity(phoneCall)
                    }else{
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_datiphone),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }


    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contactColor: CardView? = itemView.findViewById(R.id.contact_color)
        var itemCard: CardView? = itemView.findViewById(R.id.item_card)
        var contactIcon: ImageView? = itemView.findViewById(R.id.contact_icon)
        var contactNumber: TextView? = itemView.findViewById(R.id.contact_number)
        var contactNumber1: TextView? = itemView.findViewById(R.id.contact_number1)

    }
}