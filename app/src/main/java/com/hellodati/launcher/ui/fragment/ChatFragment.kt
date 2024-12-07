package com.hellodati.launcher.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.apollographql.apollo3.exception.ApolloException
import com.hellodati.launcher.Message_listenerSubscription
import com.hellodati.launcher.PhoneNotifSubscription
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ConversationCreateClient
import com.hellodati.launcher.api.GraphQLClient
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.MarkAllAsReadClient
import com.hellodati.launcher.api.MessageSendClient
import com.hellodati.launcher.api.MobileConversationClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.FragmentChatBinding
import com.hellodati.launcher.type.CreateConversationInput
import com.hellodati.launcher.type.CreateMessageInput
import com.hellodati.launcher.type.PhoneNotifTypeEnum
import com.hellodati.launcher.ui.adapters.ChatAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    var myDialogLangs: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            binding.progressBar.visibility = View.GONE
            binding.textAvailability.visibility = View.VISIBLE
            binding.layoutSend.setOnClickListener {
                Toast.makeText(binding.root.context, "no internet connection available ", Toast.LENGTH_SHORT).show()
                binding.inputMessage.text.clear()
            }
        } else {
            try {
                GlobalScope.launch (Dispatchers.Main){
                    fetchAndDisplayMessages()
                }
            } catch (e: ApolloException) {
                Log.e("chatDisplay", e.toString())
            }

            binding.layoutSend.setOnClickListener {
                GlobalScope.launch (Dispatchers.Main){
                    if (binding.inputMessage.text.trim().isNotEmpty() || binding.inputMessage.text.trim().isNotBlank()) {
                        binding.layoutSend.isClickable = false
                        val message = binding.inputMessage.text.toString()
                        binding.inputMessage.text.clear()
                        try {
                            val guestId = ResidenceClient(binding.root.context).getGuestId().toString()
                            val guest = GuestClient(binding.root.context).getGuestById(guestId)
                            if (guest?.hasUnresolvedConversation == false) {
                                binding.progressBar.visibility = View.GONE
                                binding.firstMessage.visibility = View.GONE
                                val createConversation = ConversationCreateClient(binding.root.context)
                                createConversation.createConversation(
                                    CreateConversationInput(
                                        guestId,
                                        message
                                    )
                                )
                            } else {
                                val sendMessage = MessageSendClient(binding.root.context)
                                val mobileConvId = MobileConversationClient(binding.root.context).getConversation(guestId)!!.id
                                if (mobileConvId != null) {
                                    MarkAllAsReadClient(binding.root.context).MarkAllAsRead(mobileConvId, true)
                                    val input = CreateMessageInput(
                                        mobileConvId,
                                        true,
                                        message,
                                        guestId
                                    )
                                    sendMessage.sendMessage(input)
                                }
                            }
                        } catch (e: ApolloException) {
                            Log.e("chatListener", e.toString())
                        }
                    }else {
                        Toast.makeText(binding.root.context, "Please enter some message! ", Toast.LENGTH_SHORT).show()
                    }
                    binding.layoutSend.isClickable = true
                }
            }

            GlobalScope.launch (Dispatchers.Main){
                try {
                    val graphQLClient = GraphQLClient(requireContext())
                    graphQLClient.client.subscription(Message_listenerSubscription())
                        .toFlow()
                        .collect{
                            fetchAndDisplayMessages()
                        }
                } catch (e: ApolloException) {
                    Log.e("chatListener", e.toString())
                }
            }

            GlobalScope.launch (Dispatchers.Main){
                try {
                    val graphQLClient = GraphQLClient(requireContext())
                    val gsfId = graphQLClient.getGsfId()

                    graphQLClient.client.subscription(PhoneNotifSubscription(gsfId = gsfId.toString()))
                        .toFlow()
                        .collect {
                            when (it.data?.phoneNotif?.type) {
                                PhoneNotifTypeEnum.chat_resolved -> showReviewPopup()
                                else -> {}
                            }
                        }

                } catch (e: ApolloException) {
                    Log.e("chatResolvedListener", e.toString())
                }
            }
        }

        binding.inputMessage.setOnClickListener {

        }

        binding.backBtn.setOnClickListener {
            val imm = binding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
            val navController = Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.homeFragment)
        }

        return binding.root
    }

    private suspend fun fetchAndDisplayMessages() {
        try {
            val guest = GuestClient(binding.root.context).getGuestById(ResidenceClient(binding.root.context).getGuestId().toString())
            if (guest != null) {
                if (guest.hasUnresolvedConversation == true) {
                    try {
                        val chatMessageList = MobileConversationClient(binding.root.context).getConversation(
                            ResidenceClient(binding.root.context).getGuestId().toString()
                        )!!.chats!!
                        if (chatMessageList.isEmpty()){
                            binding.textAvailability.visibility = View.VISIBLE
                        }else{
                            binding.firstMessage.visibility = View.GONE
                            binding.chatRecyclerView.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            binding.chatRecyclerView.adapter = ChatAdapter(chatMessageList)
                            val lastItemPosition = (binding.chatRecyclerView.adapter as ChatAdapter).itemCount - 1
                            if (lastItemPosition >= 0) {
                                binding.chatRecyclerView.scrollToPosition(lastItemPosition)
                            }
                        }
                    } catch (e: ApolloException) {
                        Log.e("chatMessageList", e.toString())
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.firstMessage.visibility = View.VISIBLE
                    binding.chatRecyclerView.visibility = View.GONE
                }
            }
        } catch (e: ApolloException) {
            Log.e("chatDisplay", e.toString())
        }
    }

    private fun showReviewPopup() {
        myDialogLangs = context?.let { Dialog(it) }
        myDialogLangs?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val listItem: View = LayoutInflater.from(context).inflate(R.layout.popup_reviews, null, false)
        myDialogLangs?.setContentView(listItem)
        val btn_cancel = listItem.findViewById<Button>(R.id.btn_cancel)
        btn_cancel.setOnClickListener {
            myDialogLangs?.dismiss()
            GlobalScope.launch (Dispatchers.Main){
                fetchAndDisplayMessages()
            }
        }
        val popup_comment = listItem.findViewById<TextView>(R.id.txtPop_comment)
        val edit_text_comment = listItem.findViewById<EditText>(R.id.editText_Comment)
        val btn_send = listItem.findViewById<Button>(R.id.btn_sendReview)
        val ratingBarReview = listItem.findViewById<RatingBar>(R.id.ratingBarReview)
        val relativeLayout = listItem.findViewById<RelativeLayout>(R.id.relative_review)
        popup_comment.text = getString(R.string.chat_feedback)
        relativeLayout.setOnTouchListener { v, _ ->
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            true
        }
        btn_send.setOnClickListener {
            try {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        /*val guestId = ResidenceClient(binding.root.context).getGuestId().toString()
                        val mobileConvId = MobileConversationClient(binding.root.context).getConversation(guestId)!!.id.toString()
                        RateServiceMobileClient(binding.root.context).createRate(
                            edit_text_comment.text.toString(),mobileConvId,ratingBarReview.rating.toDouble(),ResidenceClient(binding.root.context).getResidenceId().toString()
                        )*/

                        fetchAndDisplayMessages()
                        myDialogLangs!!.dismiss()
                        Toast.makeText(binding.root.context,
                            getString(R.string.chat_rating_conformation_toast), Toast.LENGTH_LONG).show()

                    }


                }
            } catch (e: Exception) {
                Log.e("device_gsf", e.message.toString())
            }

        }
        myDialogLangs?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val params: ViewGroup.LayoutParams = myDialogLangs?.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        myDialogLangs?.window?.attributes = params as WindowManager.LayoutParams
        myDialogLangs?.show()
    }

}