package com.hellodati.launcher.ui.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hellodati.launcher.Mobile_conversationQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.databinding.ItemContainerReceivedMessageBinding
import com.hellodati.launcher.databinding.ItemContainerSentMessageBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ChatAdapter(private var chatMessage: List<Mobile_conversationQuery.Chat>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var VIEW_TYPE_SENT = 1
    private var VIEW_TYPE_RECEIVED = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == VIEW_TYPE_SENT){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_container_sent_message, parent, false)
            SentMessageViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_container_received_message, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            (holder as SentMessageViewHolder).setData(chatMessage[position])
        } else {
            (holder as ReceivedMessageViewHolder).setData(chatMessage[position])
        }
    }

    override fun getItemCount(): Int {
        return chatMessage.size
    }

    override fun getItemViewType(position: Int): Int {
        if (chatMessage[position].guestMessage){
            return VIEW_TYPE_SENT
        }else{
            return VIEW_TYPE_RECEIVED
        }
    }
    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val bindingSent = ItemContainerSentMessageBinding.bind(itemView)
         fun setData(chatMessage: Mobile_conversationQuery.Chat) {
             bindingSent.textMeassage.setOnClickListener {
                 if (bindingSent.textDateTime.visibility == View.GONE){
                     bindingSent.textDateTime.visibility = View.VISIBLE
                 }else{
                     bindingSent.textDateTime.visibility = View.GONE
                 }
             }
             val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
             val outputFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
             val date = LocalDateTime.parse(chatMessage.sendingTime.toString(), formatter)
             val formattedDate2 = date.format(outputFormatter2)
             bindingSent.textMeassage.text = chatMessage.message
             bindingSent.textDateTime.text = formattedDate2
        }
    }

      class ReceivedMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

          private val bindingReceive = ItemContainerReceivedMessageBinding.bind(itemView)
          private fun getBitmapFromEncodeString(encodedImage: String?): Bitmap? {
              return if (encodedImage != null) {
                  val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
                  BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
              } else {
                  null
              }
          }
          fun setData (chatMessage: Mobile_conversationQuery.Chat){
              bindingReceive.textMeassage.setOnClickListener {
                  if (bindingReceive.textDateTime.visibility == View.GONE){
                      bindingReceive.textDateTime.visibility = View.VISIBLE
                  }else{
                      bindingReceive.textDateTime.visibility = View.GONE
                  }
              }
              val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
              val outputFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
              val date = LocalDateTime.parse(chatMessage.sendingTime.toString(), formatter)
              val formattedDate2 = date.format(outputFormatter2)
              bindingReceive.textMeassage.text = chatMessage.message
              bindingReceive.textDateTime.text = formattedDate2
              val receiverProfileImage = getBitmapFromEncodeString(chatMessage.sender!!.profilePicture)
              /*if(receiverProfileImage != null){
                  bindingReceive.imageProfile.setImageBitmap(receiverProfileImage)
              }*/
          }
    }
}