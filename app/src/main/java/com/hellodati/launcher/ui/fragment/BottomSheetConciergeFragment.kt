package com.hellodati.launcher.ui.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.Toast
import com.apollographql.apollo3.api.Optional
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ConciergeClient
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.ReservationClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.DialSendRequestBinding
import com.hellodati.launcher.databinding.DialogPasscodeBinding
import com.hellodati.launcher.serializable_data.SerializableConcierge
import com.hellodati.launcher.serializable_data.SerializableMeeting
import com.hellodati.launcher.type.CreateConciergeRequestInput
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BottomSheetConciergeFragment: BottomSheetDialogFragment() {

    private lateinit var bindingDial: DialSendRequestBinding
    private var emptyList: Boolean = false
    private var parentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable(ARG_POSITION) as SerializableConcierge
            parentPosition = item.conciergePosition
            emptyList = item.emptyList
        }
    }

    companion object {
        const val ARG_POSITION = "arg_position"

        fun newInstance(position: SerializableConcierge): BottomSheetConciergeFragment {
            val fragment = BottomSheetConciergeFragment()
            val args = Bundle()
            args.putSerializable(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setDimAmount(0.4f)
            setOnShowListener {
                val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                bottomSheet.setBackgroundResource(android.R.color.transparent)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingDial = DialSendRequestBinding.inflate(inflater, container, false)
        GlobalScope.launch (Dispatchers.Main) {
            val conciergeClient = ConciergeClient(bindingDial.root.context)
            val conciergeList = conciergeClient.getConciergeList()
            if (conciergeList.isNotEmpty()){
                bindingDial.progressBar.visibility = View.GONE
                bindingDial.scrollBar.visibility =View.VISIBLE
                val hotelLinksPreferences = context?.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
                Glide.with(bindingDial.root.context)
                    .load(hotelLinksPreferences?.getString("api_files_server", null) + "/picture/concierge_pictures/${conciergeList!![parentPosition].id}_${conciergeList!![parentPosition].image}?height=300")
                    .into(bindingDial.image)

                when (LocalHelper.getLanguage(bindingDial.root.context).toString()) {
                    "en" -> {
                        bindingDial.title.text = conciergeList[parentPosition].title.en
                        bindingDial.description.text = conciergeList[parentPosition].description.en
                    }

                    "ar" -> {
                        bindingDial.title.text = conciergeList[parentPosition].title.ar
                        bindingDial.description.text = conciergeList[parentPosition].description.ar
                    }

                    "fr" -> {
                        bindingDial.title.text = conciergeList[parentPosition].title.fr
                        bindingDial.description.text = conciergeList[parentPosition].description.fr
                    }

                    else -> {
                        bindingDial.title.text = conciergeList[parentPosition].title.default
                        bindingDial.description.text = conciergeList[parentPosition].description.default
                    }
                }

                if (bindingDial.title.text.isNullOrEmpty()) {
                    bindingDial.title.text = conciergeList!![parentPosition].title.default
                }
                if (bindingDial.description.text.isNullOrEmpty()) {
                    bindingDial.description.text = conciergeList[parentPosition].description.default
                }

                if (emptyList) {
                    bindingDial.txtMustSelect.visibility = View.GONE
                    bindingDial.itemCard.visibility = View.GONE
                    bindingDial.sendRequest.setOnClickListener {
                        showConfirmationRequestPopup(parentPosition)
                    }


                } else {
                    bindingDial.sendRequest.visibility = View.GONE
                    bindingDial.txtMustSelect.visibility = View.VISIBLE
                    bindingDial.itemCard.visibility = View.VISIBLE
                    for ((index) in conciergeList[parentPosition].listServices!!.default!!.withIndex()) {
                        val radioButton = RadioButton(bindingDial.root.context)
                        radioButton.id = index // Set a unique ID for each radio button
                        radioButton.text = conciergeList[parentPosition].listServices!!.default!![index]

                        // Add the radio button to the radio group
                        bindingDial.radioGroup.addView(radioButton)
                        radioButton.setOnClickListener {
                            try {
                                bindingDial.sendRequest.visibility = View.VISIBLE
                                bindingDial.sendRequest.setOnClickListener {
                                    showConfirmationRequestPopupItem(
                                        conciergeList[parentPosition].listServices!!.default!![index],
                                        parentPosition
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("err", e.message.toString())
                            }
                        }
                    }
                }
                try {

                } catch (e: Exception) {
                    Log.e("device_gsf", e.message.toString())
                }
            }
        }
        return bindingDial.root
    }

    private fun showConfirmationRequestPopup(parentPosition:Int) {
        val bindingDialog = DialogPasscodeBinding.inflate(LayoutInflater.from(bindingDial.root.context))
        val myDialog = Dialog(bindingDial.root.context)
        myDialog.setContentView(bindingDialog.root)
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingDialog.userId1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 1) {
                    bindingDialog.userId2.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        bindingDialog.userId2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 1) {
                    bindingDialog.userId3.requestFocus()
                }else if (s.toString().isEmpty()) {
                    bindingDialog.userId1.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        bindingDialog.userId3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 1) {
                    bindingDialog.userId4.requestFocus()
                }else if (s.toString().isEmpty()) {
                    bindingDialog.userId2.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        bindingDialog.userId4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    bindingDialog.userId3.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        bindingDialog.btnOk.setOnClickListener {
            val userId: String = bindingDialog.userId1.text.toString() + bindingDialog.userId2.text.toString() +bindingDialog.userId3.text.toString() + bindingDialog.userId4.text.toString()
            try {
                GlobalScope.launch {

                    withContext(Dispatchers.Main) {
                        val imm = bindingDialog.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(bindingDialog.root.windowToken, 0)
                        bindingDialog.progressBar.visibility = View.VISIBLE
                        val result = GuestClient(bindingDialog.root.context).checkPassword(
                            ResidenceClient(bindingDialog.root.context).getGuestId().toString(),
                            userId
                        )

                        if (result != true) {
                            bindingDialog.progressBar.visibility = View.GONE
                            bindingDialog.userId1.startAnimation(BasketFragment.shakeUserCodeError())
                            bindingDialog.userId1.requestFocus()
                            bindingDialog.userId1.text.clear()
                            bindingDialog.userId2.startAnimation(BasketFragment.shakeUserCodeError())
                            bindingDialog.userId2.text.clear()
                            bindingDialog.userId3.startAnimation(BasketFragment.shakeUserCodeError())
                            bindingDialog.userId3.text.clear()
                            bindingDialog.userId4.startAnimation(BasketFragment.shakeUserCodeError())
                            bindingDialog.userId4.text.clear()
                            return@withContext
                        }
                        val reservationClient = ReservationClient(bindingDial.root.context)
                        val conciergeClient = ConciergeClient(bindingDial.root.context)
                        val conciergeList = conciergeClient.getConciergeList()
                        val createConciergeRequestInput = CreateConciergeRequestInput(
                            residenceId = ResidenceClient(bindingDial.root.context).getResidenceId().toString(),
                            conciergeId = conciergeList[parentPosition].id,
                            subItem = Optional.present("")
                        )
                        reservationClient.createReservationConcierge(createConciergeRequestInput)
                        bindingDialog.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(
                            bindingDialog.root.context,
                            bindingDial.root.context.resources.getString(R.string.booking_sent_success),
                            Toast.LENGTH_LONG
                        ).show()


                        myDialog.dismiss()
                    }


                }
            } catch (e: Exception) {
                Log.e("device_gsf", e.message.toString())
            }
        }

        myDialog.show()

        val dialogView = myDialog.window?.decorView?.findViewById<View>(android.R.id.content)
        dialogView?.setOnTouchListener { _, event ->
            val dialogRect = Rect()
            dialogView.getGlobalVisibleRect(dialogRect)

            if (!dialogRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                myDialog.dismiss()
                true
            } else {
                false
            }
        }
    }

    private fun showConfirmationRequestPopupItem(subItem:String,parentPosition: Int) {
        val bindingDialog = DialogPasscodeBinding.inflate(LayoutInflater.from(bindingDial.root.context))
        val myDialog2 = Dialog(bindingDial.root.context)
        myDialog2.setContentView(bindingDialog.root)
        myDialog2.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingDialog.userId1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 1) {
                    bindingDialog.userId2.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        bindingDialog.userId2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 1) {
                    bindingDialog.userId3.requestFocus()
                }else if (s.toString().isEmpty()) {
                    bindingDialog.userId1.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        bindingDialog.userId3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 1) {
                    bindingDialog.userId4.requestFocus()
                }else if (s.toString().isEmpty()) {
                    bindingDialog.userId2.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        bindingDialog.userId4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    bindingDialog.userId3.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        bindingDialog.btnOk.setOnClickListener {
            val userId: String = bindingDialog.userId1.text.toString() + bindingDialog.userId2.text.toString() +bindingDialog.userId3.text.toString() + bindingDialog.userId4.text.toString()
            try {
                GlobalScope.launch {

                    withContext(Dispatchers.Main) {
                        val imm = bindingDialog.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(bindingDialog.root.windowToken, 0)
                        bindingDialog.progressBar.visibility = View.VISIBLE
                        val result = GuestClient(bindingDialog.root.context).checkPassword(
                            ResidenceClient(bindingDialog.root.context).getGuestId().toString(),
                            userId
                        )

                        if (result != true) {
                            bindingDialog.progressBar.visibility = View.GONE
                            bindingDialog.userId1.startAnimation(BasketFragment.shakeUserCodeError())
                            bindingDialog.userId1.requestFocus()
                            bindingDialog.userId1.text.clear()
                            bindingDialog.userId2.startAnimation(BasketFragment.shakeUserCodeError())
                            bindingDialog.userId2.text.clear()
                            bindingDialog.userId3.startAnimation(BasketFragment.shakeUserCodeError())
                            bindingDialog.userId3.text.clear()
                            bindingDialog.userId4.startAnimation(BasketFragment.shakeUserCodeError())
                            bindingDialog.userId4.text.clear()
                            return@withContext
                        }
                        val reservationClient = ReservationClient(bindingDial.root.context)
                        val conciergeClient = ConciergeClient(bindingDial.root.context)
                        val conciergeList = conciergeClient.getConciergeList()
                        val createConciergeRequestInput = CreateConciergeRequestInput(
                            residenceId = ResidenceClient(bindingDial.root.context).getResidenceId().toString(),
                            conciergeId = conciergeList[parentPosition].id,
                            subItem = Optional.present(subItem)
                        )
                        reservationClient.createReservationConcierge(createConciergeRequestInput)
                        Toast.makeText(
                            bindingDial.root.context,
                            bindingDial.root.context.resources.getString(R.string.booking_sent_success),
                            Toast.LENGTH_LONG
                        ).show()


                        myDialog2.dismiss()
                    }

                }
            } catch (e: Exception) {
                Log.e("device_gsf", e.message.toString())
            }
        }

        myDialog2.show()
    }
}