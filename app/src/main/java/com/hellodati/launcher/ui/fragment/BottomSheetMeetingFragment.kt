package com.hellodati.launcher.ui.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
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
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.MeetingClient
import com.hellodati.launcher.api.ReservationClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.DialogPasscodeBinding
import com.hellodati.launcher.databinding.FragmentBottomSheetMeetingBinding
import com.hellodati.launcher.serializable_data.SerializableMeeting
import com.hellodati.launcher.type.CreateReservationInput
import com.hellodati.launcher.type.ReservationTypeEnum
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.floor

class BottomSheetMeetingFragment: BottomSheetDialogFragment() {

    private var itemPosition: Int = 0
    private var nbr: Int? = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable(ARG_POSITION) as SerializableMeeting
            itemPosition = item.meetingPosition
        }
    }

    companion object {
        const val ARG_POSITION = "arg_position"

        fun newInstance(position: SerializableMeeting): BottomSheetMeetingFragment {
            val fragment = BottomSheetMeetingFragment()
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
        val bindingBottomSheetMeetingBinding = FragmentBottomSheetMeetingBinding.inflate(inflater, container, false)
        bindingBottomSheetMeetingBinding.description.movementMethod = ScrollingMovementMethod()
        GlobalScope.launch (Dispatchers.Main){
            val meetingClient = MeetingClient(requireView().context)
            val meetingList = meetingClient.getMeetings()
            if (meetingList.isNotEmpty()){
                bindingBottomSheetMeetingBinding.progressBar.visibility = View.GONE
                bindingBottomSheetMeetingBinding.scrollBar.visibility =View.VISIBLE
                val hotelLinksPreferences = bindingBottomSheetMeetingBinding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)

                Glide.with(bindingBottomSheetMeetingBinding.root.context)
                    .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/meeting_pictures/${meetingList!![itemPosition].id}_${meetingList!![itemPosition].picture}?height=300")
                    .apply { RequestOptions().fitCenter() }
                    .into(bindingBottomSheetMeetingBinding.image)

                when (LocalHelper.getLanguage(bindingBottomSheetMeetingBinding.root.context).toString()) {
                    "en" -> {
                        if (meetingList!![itemPosition].title.en.toString() != "null") {
                            bindingBottomSheetMeetingBinding.generalDescriptionTitle.text =
                                meetingList!![itemPosition].title.en.toString().trimEnd()
                        } else {
                            bindingBottomSheetMeetingBinding.generalDescriptionTitle.text =
                                meetingList!![itemPosition].title.default.toString().trimEnd()
                        }
                        if (meetingList!![itemPosition].description.en.toString() != "null") {
                            bindingBottomSheetMeetingBinding.description.text = meetingList!![itemPosition].description.en.toString().trimEnd()
                        } else {
                            bindingBottomSheetMeetingBinding.description.text = meetingList!![itemPosition].description.default.toString().trimEnd()
                        }
                    }

                    "ar" -> {
                        if (meetingList!![itemPosition].title.ar.toString() != "null") {
                            bindingBottomSheetMeetingBinding.generalDescriptionTitle.text =
                                meetingList!![itemPosition].title.ar.toString().trimEnd()
                        } else {
                            bindingBottomSheetMeetingBinding.generalDescriptionTitle.text =
                                meetingList!![itemPosition].title.default.toString().trimEnd()
                        }
                        if (meetingList!![itemPosition].description.ar.toString() != "null") {
                            bindingBottomSheetMeetingBinding.description.text = meetingList!![itemPosition].description.ar.toString().trimEnd()
                        } else {
                            bindingBottomSheetMeetingBinding.description.text = meetingList!![itemPosition].description.default.toString().trimEnd()
                        }
                    }

                    "fr" -> {
                        if (meetingList!![itemPosition].title.fr.toString() != "null") {
                            bindingBottomSheetMeetingBinding.generalDescriptionTitle.text =
                                meetingList!![itemPosition].title.fr.toString().trimEnd()
                        } else {
                            bindingBottomSheetMeetingBinding.generalDescriptionTitle.text =
                                meetingList!![itemPosition].title.default.toString().trimEnd()
                        }
                        if (meetingList!![itemPosition].description.fr.toString() != "null") {
                            bindingBottomSheetMeetingBinding.description.text = meetingList!![itemPosition].description.fr.toString().trimEnd()
                        } else {
                            bindingBottomSheetMeetingBinding.description.text = meetingList!![itemPosition].description.default.toString().trimEnd()
                        }
                    }

                    else -> {
                        bindingBottomSheetMeetingBinding.generalDescriptionTitle.text =
                            meetingList!![itemPosition].title.default.toString().trimEnd()
                        bindingBottomSheetMeetingBinding.description.text = meetingList!![itemPosition].description.default.toString().trimEnd()
                    }
                }

                if (meetingList!![itemPosition].price!!.hasDiscount == true) {
                    if (meetingList!![itemPosition].price!!.discountType.toString() == "percentege") {
                        val discountAmount: Double =
                            meetingList!![itemPosition].price!!.amount - ((meetingList!![itemPosition].price!!.discountAmount!! * meetingList!![itemPosition].price!!.amount) / 100.0)
                        if (discountAmount == floor(discountAmount)) {
                            bindingBottomSheetMeetingBinding.price.text =
                                "${discountAmount.toInt()} ${meetingList!![itemPosition].price!!.currency}"
                        } else {
                            bindingBottomSheetMeetingBinding.price.text =
                                "$discountAmount ${meetingList!![itemPosition].price!!.currency}"
                        }
                        if (meetingList!![itemPosition].price!!.amount == floor(meetingList!![itemPosition].price!!.amount)) {
                            bindingBottomSheetMeetingBinding.oldprice.text =
                                "${meetingList!![itemPosition].price!!.amount.toInt()} ${meetingList!![itemPosition].price!!.currency}"
                        } else {
                            bindingBottomSheetMeetingBinding.oldprice.text =
                                "${meetingList!![itemPosition].price!!.amount} ${meetingList!![itemPosition].price!!.currency}"
                        }

                        bindingBottomSheetMeetingBinding.oldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        val discountAmount =
                            meetingList!![itemPosition].price!!.amount - meetingList!![itemPosition].price!!.discountAmount!!
                        if (discountAmount == floor(discountAmount)) {
                            bindingBottomSheetMeetingBinding.price.text =
                                "${discountAmount.toInt()} ${meetingList!![itemPosition].price!!.currency}"
                        } else {
                            bindingBottomSheetMeetingBinding.price.text =
                                "$discountAmount ${meetingList!![itemPosition].price!!.currency}"
                        }
                        if (meetingList!![itemPosition].price!!.amount == floor(meetingList!![itemPosition].price!!.amount)) {
                            bindingBottomSheetMeetingBinding.oldprice.text =
                                "${meetingList!![itemPosition].price!!.amount.toInt()} ${meetingList!![itemPosition].price!!.currency}"
                        } else {
                            bindingBottomSheetMeetingBinding.oldprice.text =
                                "${meetingList!![itemPosition].price!!.amount} ${meetingList!![itemPosition].price!!.currency}"
                        }
                        bindingBottomSheetMeetingBinding.oldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    }
                } else {
                    if (meetingList!![itemPosition].price!!.amount == floor(meetingList!![itemPosition].price!!.amount)) {
                        bindingBottomSheetMeetingBinding.price.text =
                            "${meetingList!![itemPosition].price!!.amount.toInt()} ${meetingList!![itemPosition].price!!.currency}"
                    } else {
                        bindingBottomSheetMeetingBinding.price.text =
                            "${meetingList!![itemPosition].price!!.amount} ${meetingList!![itemPosition].price!!.currency}"
                    }
                }

                bindingBottomSheetMeetingBinding.date.setOnClickListener {
                    val datePicker = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        myCalendar.set(Calendar.YEAR, year)
                        myCalendar.set(Calendar.MONTH, monthOfYear)
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        val myFormat = "yyyy-MM-dd" //In which you need put here
                        val sdf = SimpleDateFormat(myFormat, Locale.US)


                        val selectedDate = myCalendar.time
                        if (isTodayOrFutureDate(selectedDate)) {
                            // The selected date is today or a future date
                            bindingBottomSheetMeetingBinding.dateTxt.text = sdf.format(myCalendar.time)
                        } else {
                            GlobalScope.launch (Dispatchers.Main) {
                                val meetingClient = MeetingClient(bindingBottomSheetMeetingBinding.root.context)
                                val meetingList = meetingClient.getMeetings()
                                val itemReceived = meetingList[itemPosition]
                                bindingBottomSheetMeetingBinding.dateTxt.text = ""
                                Toast.makeText(
                                    bindingBottomSheetMeetingBinding.root.context,
                                    ""+ R.string.no_booking_error_date1 + " " + itemReceived!!.title + " " + R.string.no_booking_error_date2
                                    ,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    bindingBottomSheetMeetingBinding.root.context?.let {
                        val datePickerDialog = DatePickerDialog(
                            it,
                            R.style.DatePicker,
                            datePicker,
                            myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)
                        )

                        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

                        datePickerDialog.show()
                    }
                }

                bindingBottomSheetMeetingBinding.hour.setOnClickListener {
                    val timePicker = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        myCalendar[Calendar.HOUR_OF_DAY] = hourOfDay
                        myCalendar[Calendar.MINUTE] = minute
                        val myFormat = "HH:mm" //In which you need put here
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        bindingBottomSheetMeetingBinding.hourTxt.text = sdf.format(myCalendar.time)
                    }
                    TimePickerDialog(
                        bindingBottomSheetMeetingBinding.root.context, R.style.DatePicker, timePicker,
                        myCalendar[Calendar.HOUR_OF_DAY],
                        myCalendar[Calendar.MINUTE], true
                    ).show()
                }

                bindingBottomSheetMeetingBinding.nbrPerson.text = Editable.Factory.getInstance().newEditable(nbr.toString())
                GlobalScope.launch (Dispatchers.Main) {
                    val meetingClient = MeetingClient(bindingBottomSheetMeetingBinding.root.context)
                    val meetingList = meetingClient.getMeetings()
                    val itemReceived = meetingList[itemPosition]
                    bindingBottomSheetMeetingBinding.nbrPerson.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                            // Do nothing
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            // Do nothing
                        }

                        override fun afterTextChanged(s: Editable?) {
                            val newNbr = s.toString().toIntOrNull()

                            if (newNbr != null && newNbr > itemReceived!!.capacity.toInt()) {
                                Toast.makeText(
                                    bindingBottomSheetMeetingBinding.root.context,
                                    R.string.number_of_guests,
                                    Toast.LENGTH_LONG
                                ).show()
                                bindingBottomSheetMeetingBinding.nbrPerson.text = Editable.Factory.getInstance()
                                    .newEditable(itemReceived!!.capacity.toInt().toString())
                            }
                        }
                    })

                    bindingBottomSheetMeetingBinding.increase.setOnClickListener(View.OnClickListener {
                        nbr = bindingBottomSheetMeetingBinding.nbrPerson.text.toString().toInt()
                        if (nbr!! + 1 <= itemReceived!!.capacity.toInt()) {
                            nbr = nbr!! + 1
                            bindingBottomSheetMeetingBinding.nbrPerson.text =
                                Editable.Factory.getInstance().newEditable(nbr.toString())
                        } else {
                            Toast.makeText(
                                bindingBottomSheetMeetingBinding.root.context,
                                R.string.number_of_guests,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    })
                }
                bindingBottomSheetMeetingBinding.decrease.setOnClickListener(View.OnClickListener {
                    if (nbr!! > 1) {
                        nbr = bindingBottomSheetMeetingBinding.nbrPerson.text.toString().toInt()
                        nbr = nbr!! - 1
                        bindingBottomSheetMeetingBinding.nbrPerson.text = Editable.Factory.getInstance().newEditable(nbr.toString())
                    }
                })

                bindingBottomSheetMeetingBinding.btnBook.setOnClickListener(View.OnClickListener {

                    if (bindingBottomSheetMeetingBinding.dateTxt.text.toString() == "") {
                        Toast.makeText(bindingBottomSheetMeetingBinding.root.context, R.string.date_required, Toast.LENGTH_LONG).show()
                    } else {
                        if (bindingBottomSheetMeetingBinding.hourTxt.text.toString() == "") {
                            Toast.makeText(bindingBottomSheetMeetingBinding.root.context, R.string.hour_required, Toast.LENGTH_LONG)
                                .show()
                        } else {
                            val myDialog2 = Dialog(bindingBottomSheetMeetingBinding.root.context)
                            myDialog2.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            val bindingDialog = DialogPasscodeBinding.inflate(LayoutInflater.from(bindingBottomSheetMeetingBinding.root.context))
                            myDialog2.setContentView(bindingDialog.root)

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

                                val date: String = bindingBottomSheetMeetingBinding.dateTxt.text.toString()
                                val time: String = bindingBottomSheetMeetingBinding.hourTxt.text.toString()
                                val nbrPerson: Int = bindingBottomSheetMeetingBinding.nbrPerson.text.toString().toInt()
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
                                            val meetingClient = MeetingClient(bindingBottomSheetMeetingBinding.root.context)
                                            val meetingList = meetingClient.getMeetings()
                                            val itemReceived = meetingList[itemPosition]
                                            val reservationClient = ReservationClient(bindingBottomSheetMeetingBinding.root.context)
                                            val createReservationInput = CreateReservationInput(
                                                date = "$date $time",
                                                itemId = Optional.present(itemReceived!!.id),
                                                numberOfPerson = nbrPerson.toDouble(),
                                                reservationType = ReservationTypeEnum.meeting,
                                                residenceId = ResidenceClient(bindingBottomSheetMeetingBinding.root.context).getResidenceId().toString()
                                            )
                                            reservationClient.createReservation(createReservationInput)
                                            bindingDialog.progressBar.visibility = View.INVISIBLE
                                            Toast.makeText(
                                                bindingBottomSheetMeetingBinding.root.context,
                                                R.string.booking_sent_success,
                                                Toast.LENGTH_LONG
                                            ).show()
                                            bindingBottomSheetMeetingBinding
                                            bindingBottomSheetMeetingBinding.dateTxt.text = ""
                                            bindingBottomSheetMeetingBinding.hourTxt.text = ""
                                            bindingBottomSheetMeetingBinding.nbrPerson.text = Editable.Factory.getInstance().newEditable("1")
                                            bindingBottomSheetMeetingBinding.comment.text = Editable.Factory.getInstance().newEditable("")
                                            bindingBottomSheetMeetingBinding.txtDate.text = "$date $time"
                                            bindingBottomSheetMeetingBinding.txtNbrPerson.text = "$nbr"
                                            bindingBottomSheetMeetingBinding.scrollBar.visibility = View.GONE
                                            bindingBottomSheetMeetingBinding.beforeGoToMain.visibility = View.VISIBLE

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
                })

                bindingBottomSheetMeetingBinding.backBtn.setOnClickListener {
                    bindingBottomSheetMeetingBinding.scrollBar.visibility = View.VISIBLE
                    bindingBottomSheetMeetingBinding.beforeGoToMain.visibility = View.GONE
                    bindingBottomSheetMeetingBinding.txtDate.text = ""
                    bindingBottomSheetMeetingBinding.txtNbrPerson.text = ""
                }
            }
        }

        return bindingBottomSheetMeetingBinding.root
    }

    fun isTodayOrFutureDate(date: Date): Boolean {
        val today = Date()
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = today
        cal2.time = date
        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)) ||
                date.after(today)
    }
}