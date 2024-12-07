package com.hellodati.launcher.ui.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import com.apollographql.apollo3.api.Optional
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hellodati.launcher.R
import com.hellodati.launcher.api.EventClient
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.ReservationClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.DialogPasscodeBinding
import com.hellodati.launcher.databinding.FragmentBottomSheetEventBinding
import com.hellodati.launcher.serializable_data.SerializableEvent
import com.hellodati.launcher.serializable_data.SerializableMeeting
import com.hellodati.launcher.type.CreateReservationInput
import com.hellodati.launcher.type.ReservationTypeEnum
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor

class BottomSheetEventFragment: BottomSheetDialogFragment() {

    private var parentPosition: Int = 0
    private var nbr: Int? = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable(ARG_POSITION) as SerializableEvent
            parentPosition = item.event
        }
    }

    companion object {
        const val ARG_POSITION = "arg_position"

        fun newInstance(position: SerializableEvent): BottomSheetEventFragment {
            val fragment = BottomSheetEventFragment()
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
        val bindingBottomSheetEventBinding = FragmentBottomSheetEventBinding.inflate(inflater, container, false)
        bindingBottomSheetEventBinding.description.movementMethod = ScrollingMovementMethod()
        GlobalScope.launch(Dispatchers.Main) {
            val eventClient = EventClient(bindingBottomSheetEventBinding.root.context)
            val eventList = eventClient.getAllEvent()
            if (eventList.isNotEmpty()){
                bindingBottomSheetEventBinding.progressBar.visibility = View.GONE
                bindingBottomSheetEventBinding.scrollBar.visibility =View.VISIBLE

                val hotelLinksPreferences = bindingBottomSheetEventBinding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
                Glide.with(bindingBottomSheetEventBinding.root.context)
                    .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/event_pictures/${eventList[parentPosition]!!.id}_${eventList[parentPosition]!!.picture}?height=300")
                    .apply { RequestOptions().fitCenter() }
                    .into(bindingBottomSheetEventBinding.image)

                when (LocalHelper.getLanguage(bindingBottomSheetEventBinding.root.context).toString()) {
                    "en" -> {
                        if (eventList[parentPosition]!!.title.en.toString() != "null") {
                            bindingBottomSheetEventBinding.generalDescriptionTitle.text =
                                eventList[parentPosition]!!.title.en.toString().trimEnd()
                        } else {
                            bindingBottomSheetEventBinding.generalDescriptionTitle.text =
                                eventList[parentPosition]!!.title.default.toString().trimEnd()
                        }

                        if (eventList[parentPosition]!!.title.en.toString() != "null") {
                            bindingBottomSheetEventBinding.description.text =
                                eventList[parentPosition]!!.description.en.toString().trimEnd()
                        } else {
                            bindingBottomSheetEventBinding.description.text =
                                eventList[parentPosition]!!.description.default.toString().trimEnd()
                        }

                    }

                    "ar" -> {
                        if (eventList[parentPosition]!!.title.ar.toString() != "null") {
                            bindingBottomSheetEventBinding.generalDescriptionTitle.text =
                                eventList[parentPosition]!!.title.ar.toString().trimEnd()
                        } else {
                            bindingBottomSheetEventBinding.generalDescriptionTitle.text =
                                eventList[parentPosition]!!.title.default.toString().trimEnd()
                        }

                        if (eventList[parentPosition]!!.title.ar.toString() != "null") {
                            bindingBottomSheetEventBinding.description.text =
                                eventList[parentPosition]!!.description.ar.toString().trimEnd()
                        } else {
                            bindingBottomSheetEventBinding.description.text =
                                eventList[parentPosition]!!.description.default.toString().trimEnd()
                        }
                    }

                    "fr" -> {
                        if (eventList[parentPosition]!!.title.fr.toString() != "null") {
                            bindingBottomSheetEventBinding.generalDescriptionTitle.text =
                                eventList[parentPosition]!!.title.fr.toString().trimEnd()
                        } else {
                            bindingBottomSheetEventBinding.generalDescriptionTitle.text =
                                eventList[parentPosition]!!.title.default.toString().trimEnd()
                        }

                        if (eventList[parentPosition]!!.title.fr.toString() != "null") {
                            bindingBottomSheetEventBinding.description.text =
                                eventList[parentPosition]!!.description.fr.toString().trimEnd()
                        } else {
                            bindingBottomSheetEventBinding.description.text =
                                eventList[parentPosition]!!.description.default.toString().trimEnd()
                        }
                    }

                    else -> {
                        bindingBottomSheetEventBinding.generalDescriptionTitle.text =
                            eventList[parentPosition]!!.title.default.toString().trimEnd()
                        bindingBottomSheetEventBinding.description.text =
                            eventList[parentPosition]!!.description.default.toString().trimEnd()
                    }
                }

                if (eventList[parentPosition]!!.status!!.rawValue == "free") {
                    bindingBottomSheetEventBinding.price.text = "free"
                } else {
                    if (eventList[parentPosition]!!.price!!.hasDiscount == true) {
                        if (eventList[parentPosition]!!.price!!.discountType.toString() == "percentege") {
                            val discountAmount: Double =
                                eventList[parentPosition]!!.price!!.amount - ((eventList[parentPosition]!!.price!!.discountAmount!! * eventList[parentPosition]!!.price!!.amount) / 100.0)
                            if (discountAmount == floor(discountAmount)) {
                                bindingBottomSheetEventBinding.price.text =
                                    "${discountAmount.toInt()} ${eventList[parentPosition]!!.price!!.currency}"
                            } else {
                                bindingBottomSheetEventBinding.price.text =
                                    "$discountAmount ${eventList[parentPosition]!!.price!!.currency}"
                            }
                            if (eventList[parentPosition]!!.price!!.amount == floor(eventList[parentPosition]!!.price!!.amount)) {
                                bindingBottomSheetEventBinding.oldprice.text =
                                    "${eventList[parentPosition]!!.price!!.amount.toInt()} ${eventList[parentPosition]!!.price!!.currency}"
                            } else {
                                bindingBottomSheetEventBinding.oldprice.text =
                                    "${eventList[parentPosition]!!.price!!.amount} ${eventList[parentPosition]!!.price!!.currency}"
                            }

                            bindingBottomSheetEventBinding.oldprice.paintFlags =
                                Paint.STRIKE_THRU_TEXT_FLAG
                        } else {
                            val discountAmount =
                                eventList[parentPosition]!!.price!!.amount - eventList[parentPosition]!!.price!!.discountAmount!!
                            if (discountAmount == floor(discountAmount)) {
                                bindingBottomSheetEventBinding.price.text =
                                    "${discountAmount.toInt()} ${eventList[parentPosition]!!.price!!.currency}"
                            } else {
                                bindingBottomSheetEventBinding.price.text =
                                    "$discountAmount ${eventList[parentPosition]!!.price!!.currency}"
                            }
                            if (eventList[parentPosition]!!.price!!.amount == floor(eventList[parentPosition]!!.price!!.amount)) {
                                bindingBottomSheetEventBinding.oldprice.text =
                                    "${eventList[parentPosition]!!.price!!.amount.toInt()} ${eventList[parentPosition]!!.price!!.currency}"
                            } else {
                                bindingBottomSheetEventBinding.oldprice.text =
                                    "${eventList[parentPosition]!!.price!!.amount} ${eventList[parentPosition]!!.price!!.currency}"
                            }
                            bindingBottomSheetEventBinding.oldprice.paintFlags =
                                Paint.STRIKE_THRU_TEXT_FLAG
                        }
                    } else {
                        if (eventList[parentPosition]!!.price!!.amount == floor(eventList[parentPosition]!!.price!!.amount)) {
                            bindingBottomSheetEventBinding.price.text =
                                "${eventList[parentPosition]!!.price!!.amount.toInt()} ${eventList[parentPosition]!!.price!!.currency}"
                        } else {
                            bindingBottomSheetEventBinding.price.text =
                                "${eventList[parentPosition]!!.price!!.amount} ${eventList[parentPosition]!!.price!!.currency}"
                        }
                    }
                }

                bindingBottomSheetEventBinding.backBtn.setOnClickListener {
                    bindingBottomSheetEventBinding.scrollBar.visibility = View.VISIBLE
                    bindingBottomSheetEventBinding.beforeGoToMain.visibility = View.GONE
                }

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val outputFormatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val outputFormatter2 = DateTimeFormatter.ofPattern("HH:mm")
                val timeFrom = LocalDateTime.parse(eventList[parentPosition]!!.datefrom.toString(), formatter)
                val dateFrom = LocalDateTime.parse(eventList[parentPosition]!!.datefrom.toString(), formatter)
                val formattedTimeFrom = timeFrom.format(outputFormatter2)
                val formattedDateFrom = dateFrom.format(outputFormatter3)
                val timeTo = LocalDateTime.parse(eventList[parentPosition]!!.dateto.toString(), formatter)
                val dateTo = LocalDateTime.parse(eventList[parentPosition]!!.dateto.toString(), formatter)
                val formattedTimeTo = timeTo.format(outputFormatter2)
                val formattedDateTo = dateTo.format(outputFormatter3)
                if (eventList[parentPosition]!!.singleDay){
                    bindingBottomSheetEventBinding.status.text = " $formattedDateFrom ${resources.getString(R.string.openning_time_from)} $formattedTimeFrom ${resources.getString(R.string.openning_time_to)} $formattedTimeTo"
                }else{
                    bindingBottomSheetEventBinding.openingDateOverview.visibility = View.VISIBLE
                    bindingBottomSheetEventBinding.statusDate.text = " ${resources.getString(R.string.openning_time_from)} $formattedDateFrom ${resources.getString(R.string.openning_time_to)} $formattedDateTo"
                    bindingBottomSheetEventBinding.status.text = " ${resources.getString(R.string.openning_time_from)} $formattedTimeFrom ${resources.getString(R.string.openning_time_to)} $formattedTimeTo"
                }

                bindingBottomSheetEventBinding.nbrPerson.text =
                    Editable.Factory.getInstance().newEditable(nbr.toString())

                bindingBottomSheetEventBinding.increase.setOnClickListener(View.OnClickListener {
                    nbr = bindingBottomSheetEventBinding.nbrPerson.text.toString().toInt()
                    nbr = nbr!! + 1
                    bindingBottomSheetEventBinding.nbrPerson.text =
                        Editable.Factory.getInstance().newEditable(nbr.toString())
                })
                bindingBottomSheetEventBinding.decrease.setOnClickListener(View.OnClickListener {
                    if (nbr!! > 1) {
                        nbr = bindingBottomSheetEventBinding.nbrPerson.text.toString().toInt()
                        nbr = nbr!! - 1
                        bindingBottomSheetEventBinding.nbrPerson.text =
                            Editable.Factory.getInstance().newEditable(nbr.toString())
                    }
                })


                bindingBottomSheetEventBinding.btnBook.setOnClickListener {
                    val myDialog2 = Dialog(bindingBottomSheetEventBinding.root.context)
                    myDialog2.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    val bindingDialog = DialogPasscodeBinding.inflate(layoutInflater)
                    myDialog2.setContentView(bindingDialog.root)

                    bindingDialog.userId1.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            if (s.toString().length == 1) {
                                bindingDialog.userId2.requestFocus()
                            }
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }
                    })

                    bindingDialog.userId2.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            if (s.toString().length == 1) {
                                bindingDialog.userId3.requestFocus()
                            } else if (s.toString().isEmpty()) {
                                bindingDialog.userId1.requestFocus()
                            }
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }
                    })

                    bindingDialog.userId3.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            if (s.toString().length == 1) {
                                bindingDialog.userId4.requestFocus()
                            } else if (s.toString().isEmpty()) {
                                bindingDialog.userId2.requestFocus()
                            }
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }
                    })

                    bindingDialog.userId4.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            if (s.toString().isEmpty()) {
                                bindingDialog.userId3.requestFocus()
                            }
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }
                    })

                    val time =
                        LocalDateTime.parse(eventList[parentPosition]!!.dateto.toString(), formatter)
                    val date =
                        LocalDateTime.parse(eventList[parentPosition]!!.datefrom.toString(), formatter)
                    val formattedTime = time.format(outputFormatter2)
                    val formattedDate = date.format(outputFormatter3)

                    bindingDialog.btnOk.setOnClickListener {

                        val nbrPerson: Int =
                            bindingBottomSheetEventBinding.nbrPerson.text.toString().toInt()
                        val userId: String =
                            bindingDialog.userId1.text.toString() + bindingDialog.userId2.text.toString() + bindingDialog.userId3.text.toString() + bindingDialog.userId4.text.toString()
                        try {
                            GlobalScope.launch {

                                withContext(Dispatchers.Main) {
                                    val imm =
                                        bindingDialog.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    imm.hideSoftInputFromWindow(bindingDialog.root.windowToken, 0)
                                    bindingDialog.progressBar.visibility = View.VISIBLE
                                    val result = GuestClient(bindingDialog.root.context).checkPassword(
                                        ResidenceClient(bindingDialog.root.context).getGuestId()
                                            .toString(),
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
                                    val reservationClient = ReservationClient(bindingBottomSheetEventBinding.root.context)
                                    val createReservationInput = CreateReservationInput(
                                        date = "$formattedTime $formattedDate",
                                        itemId = Optional.present(eventList[parentPosition]!!.id),
                                        numberOfPerson = nbrPerson.toDouble(),
                                        reservationType = ReservationTypeEnum.events,
                                        residenceId = ResidenceClient(bindingBottomSheetEventBinding.root.context).getResidenceId()
                                            .toString()
                                    )
                                    reservationClient.createReservation(createReservationInput)
                                    bindingDialog.progressBar.visibility = View.INVISIBLE
                                    Toast.makeText(
                                        context,
                                        getString(R.string.booking_sent_success),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    bindingBottomSheetEventBinding
                                    bindingBottomSheetEventBinding.nbrPerson.text =
                                        Editable.Factory.getInstance().newEditable("1")
                                    bindingBottomSheetEventBinding.txtDate.text = "$date $time"
                                    bindingBottomSheetEventBinding.txtNbrPerson.text = "$nbr"
                                    bindingBottomSheetEventBinding.scrollBar.visibility = View.GONE
                                    bindingBottomSheetEventBinding.beforeGoToMain.visibility =
                                        View.VISIBLE

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
        }
        return bindingBottomSheetEventBinding.root
    }
}