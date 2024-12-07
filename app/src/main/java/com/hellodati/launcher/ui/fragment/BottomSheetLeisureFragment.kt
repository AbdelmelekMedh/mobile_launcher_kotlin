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
import com.hellodati.launcher.api.LeisureClient
import com.hellodati.launcher.api.ReservationClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.DialogPasscodeBinding
import com.hellodati.launcher.databinding.FragmentBottomSheetLeisureBinding
import com.hellodati.launcher.serializable_data.SerializableLeisure
import com.hellodati.launcher.type.CreateReservationInput
import com.hellodati.launcher.type.ReservationTypeEnum
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.floor

class BottomSheetLeisureFragment: BottomSheetDialogFragment() {

    private lateinit var bindingBottomSheetLeisureBinding: FragmentBottomSheetLeisureBinding
    private var nbr: Int? = 1
    private var itemPosition: Int =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable(ARG_POSITION) as SerializableLeisure
            itemPosition= item.leisurePosition
        }
    }

    companion object {
        const val ARG_POSITION = "arg_position"

        fun newInstance(position: SerializableLeisure): BottomSheetLeisureFragment {
            val fragment = BottomSheetLeisureFragment()
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
        bindingBottomSheetLeisureBinding = FragmentBottomSheetLeisureBinding.inflate(inflater, container, false)
        bindingBottomSheetLeisureBinding.description.movementMethod = ScrollingMovementMethod()
        GlobalScope.launch(Dispatchers.Main) {
            val leisureClient = LeisureClient(requireView().context)
            val leisureList = leisureClient.getLeisureMenu()
            if (leisureList.isNotEmpty()){
                bindingBottomSheetLeisureBinding.progressBar.visibility = View.GONE
                bindingBottomSheetLeisureBinding.scrollBar.visibility =View.VISIBLE
                bindingBottomSheetLeisureBinding.timeLayout.visibility = View.VISIBLE
                bindingBottomSheetLeisureBinding.btnBook.visibility = View.VISIBLE
                val hotelLinksPreferences = bindingBottomSheetLeisureBinding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)

                Glide.with(bindingBottomSheetLeisureBinding.root.context)
                    .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/leisure_pictures/${leisureList!![itemPosition].id}_${leisureList!![itemPosition].image}?height=300")
                    .apply { RequestOptions().fitCenter() }
                    .into(bindingBottomSheetLeisureBinding.image)

                when (LocalHelper.getLanguage(bindingBottomSheetLeisureBinding.root.context).toString()) {
                    "en" -> {
                        if (leisureList[itemPosition]!!.title.en.toString() != "null") {
                            bindingBottomSheetLeisureBinding.generalDescriptionTitle.text = leisureList[itemPosition]!!.title.en.toString().trimEnd()
                        } else {
                            bindingBottomSheetLeisureBinding.generalDescriptionTitle.text = leisureList[itemPosition]!!.title.default.toString().trimEnd()
                        }

                        if (leisureList[itemPosition]!!.title.en.toString() != "null") {
                            bindingBottomSheetLeisureBinding.description.text = leisureList[itemPosition]!!.description.en.toString().trimEnd()
                        } else {
                            bindingBottomSheetLeisureBinding.description.text = leisureList[itemPosition]!!.description.default.toString().trimEnd()
                        }

                    }

                    "ar" -> {
                        if (leisureList[itemPosition]!!.title.ar.toString() != "null") {
                            bindingBottomSheetLeisureBinding.generalDescriptionTitle.text = leisureList[itemPosition]!!.title.ar.toString().trimEnd()
                        } else {
                            bindingBottomSheetLeisureBinding.generalDescriptionTitle.text = leisureList[itemPosition]!!.title.default.toString().trimEnd()
                        }

                        if (leisureList[itemPosition]!!.title.ar.toString() != "null") {
                            bindingBottomSheetLeisureBinding.description.text = leisureList[itemPosition]!!.description.ar.toString().trimEnd()
                        } else {
                            bindingBottomSheetLeisureBinding.description.text = leisureList[itemPosition]!!.description.default.toString().trimEnd()
                        }
                    }

                    "fr" -> {
                        if (leisureList[itemPosition]!!.title.fr.toString() != "null") {
                            bindingBottomSheetLeisureBinding.generalDescriptionTitle.text = leisureList[itemPosition]!!.title.fr.toString().trimEnd()
                        } else {
                            bindingBottomSheetLeisureBinding.generalDescriptionTitle.text = leisureList[itemPosition]!!.title.default.toString().trimEnd()
                        }

                        if (leisureList[itemPosition]!!.title.fr.toString() != "null") {
                            bindingBottomSheetLeisureBinding.description.text = leisureList[itemPosition]!!.description.fr.toString().trimEnd()
                        } else {
                            bindingBottomSheetLeisureBinding.description.text = leisureList[itemPosition]!!.description.default.toString().trimEnd()
                        }
                    }

                    else -> {
                        bindingBottomSheetLeisureBinding.generalDescriptionTitle.text = leisureList[itemPosition]!!.title.default.toString().trimEnd()
                        bindingBottomSheetLeisureBinding.description.text = leisureList[itemPosition]!!.description.default.toString().trimEnd()
                    }
                }

                if (leisureList[itemPosition]!!.price!!.hasDiscount == true) {
                    if (leisureList[itemPosition]!!.price!!.discountType.toString() == "percentege") {
                        val discountAmount: Double =
                            leisureList[itemPosition]!!.price!!.amount - ((leisureList[itemPosition]!!.price!!.discountAmount!! * leisureList[itemPosition]!!.price!!.amount) / 100.0)
                        if (discountAmount == floor(discountAmount)) {
                            bindingBottomSheetLeisureBinding.price.text = "${discountAmount.toInt()} ${leisureList[itemPosition]!!.price!!.currency}"
                        } else {
                            bindingBottomSheetLeisureBinding.price.text = "$discountAmount ${leisureList[itemPosition]!!.price!!.currency}"
                        }
                        if (leisureList[itemPosition]!!.price!!.amount == floor(leisureList[itemPosition]!!.price!!.amount)) {
                            bindingBottomSheetLeisureBinding.oldprice.text = "${leisureList[itemPosition]!!.price!!.amount.toInt()} ${leisureList[itemPosition]!!.price!!.currency}"
                        } else {
                            bindingBottomSheetLeisureBinding.oldprice.text = "${leisureList[itemPosition]!!.price!!.amount} ${leisureList[itemPosition]!!.price!!.currency}"
                        }

                        bindingBottomSheetLeisureBinding.oldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        val discountAmount = leisureList[itemPosition]!!.price!!.amount - leisureList[itemPosition]!!.price!!.discountAmount!!
                        if (discountAmount == floor(discountAmount)) {
                            bindingBottomSheetLeisureBinding.price.text =
                                "${discountAmount.toInt()} ${leisureList[itemPosition]!!.price!!.currency}"
                        } else {
                            bindingBottomSheetLeisureBinding.price.text =
                                "$discountAmount ${leisureList[itemPosition]!!.price!!.currency}"
                        }
                        if (leisureList[itemPosition]!!.price!!.amount == floor(leisureList[itemPosition]!!.price!!.amount)) {
                            bindingBottomSheetLeisureBinding.oldprice.text =
                                "${leisureList[itemPosition]!!.price!!.amount.toInt()} ${leisureList[itemPosition]!!.price!!.currency}"
                        } else {
                            bindingBottomSheetLeisureBinding.oldprice.text =
                                "${leisureList[itemPosition]!!.price!!.amount} ${leisureList[itemPosition]!!.price!!.currency}"
                        }
                        bindingBottomSheetLeisureBinding.oldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    }
                } else {
                    if (leisureList[itemPosition]!!.price!!.amount == floor(leisureList[itemPosition]!!.price!!.amount)) {
                        bindingBottomSheetLeisureBinding.price.text = "${leisureList[itemPosition]!!.price!!.amount.toInt()} ${leisureList[itemPosition]!!.price!!.currency}"
                    } else {
                        bindingBottomSheetLeisureBinding.price.text = "${leisureList[itemPosition]!!.price!!.amount} ${leisureList[itemPosition]!!.price!!.currency}"
                    }
                }

                bindingBottomSheetLeisureBinding.status.text = "${resources.getString(R.string.openning_time_from)}: ${leisureList[itemPosition]!!.from} ${resources.getString(R.string.openning_time_to)}: ${leisureList[itemPosition]!!.to}"

                bindingBottomSheetLeisureBinding.date.setOnClickListener {
                    onClickDatePick(view)
                }

                bindingBottomSheetLeisureBinding.hour.setOnClickListener {
                    onClickTimePick(view)
                }

                bindingBottomSheetLeisureBinding.backBtn.setOnClickListener {
                    bindingBottomSheetLeisureBinding.scrollBar.visibility = View.VISIBLE
                    bindingBottomSheetLeisureBinding.timeLayout.visibility = View.VISIBLE
                    bindingBottomSheetLeisureBinding.btnBook.visibility = View.VISIBLE
                    bindingBottomSheetLeisureBinding.beforeGoToMain.visibility = View.GONE
                }

                bindingBottomSheetLeisureBinding.nbrPerson.text = Editable.Factory.getInstance().newEditable(nbr.toString())

                bindingBottomSheetLeisureBinding.increase.setOnClickListener(View.OnClickListener {
                    nbr = bindingBottomSheetLeisureBinding.nbrPerson.text.toString().toInt()
                    nbr = nbr!! + 1
                    bindingBottomSheetLeisureBinding.nbrPerson.text = Editable.Factory.getInstance().newEditable(nbr.toString())
                })
                bindingBottomSheetLeisureBinding.decrease.setOnClickListener(View.OnClickListener {
                    if (nbr!! > 1) {
                        nbr = bindingBottomSheetLeisureBinding.nbrPerson.text.toString().toInt()
                        nbr = nbr!! - 1
                        bindingBottomSheetLeisureBinding.nbrPerson.text = Editable.Factory.getInstance().newEditable(nbr.toString())
                    }
                })

                bindingBottomSheetLeisureBinding.btnBook.setOnClickListener {
                    if (bindingBottomSheetLeisureBinding.dateTxt.text.toString() == "") {
                        Toast.makeText(
                            bindingBottomSheetLeisureBinding.root.context,
                            R.string.date_required,
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        if (bindingBottomSheetLeisureBinding.hourTxt.text.toString() == "") {
                            Toast.makeText(
                                bindingBottomSheetLeisureBinding.root.context,
                                R.string.hour_required,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        } else {
                            showConfirmationRequestPopup()
                        }
                    }
                }
            }
        }
        return bindingBottomSheetLeisureBinding.root
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

    fun onClickDatePick(view: View?) {
        val datePicker =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "yyyy-MM-dd" //In which you need put here
                val sdf = SimpleDateFormat(myFormat, Locale.US)


                val selectedDate = myCalendar.time
                if (isTodayOrFutureDate(selectedDate)) {
                    // The selected date is today or a future date
                    bindingBottomSheetLeisureBinding.dateTxt.text = sdf.format(myCalendar.time)
                } else {
                    // The selected date is in the past
                    bindingBottomSheetLeisureBinding.dateTxt.text = ""
                    Toast.makeText(
                        context,
                        getString(R.string.no_booking_error_date),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        context?.let {
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

    fun onClickTimePick(view: View?) {
        GlobalScope.launch(Dispatchers.Main) {
            // Update UI here
            val leisureClient = LeisureClient(requireView().context)
            val leisureList = leisureClient.getLeisureMenu()
            val itemReceived = leisureList[itemPosition]
            if (itemReceived == null){

            }else{
                val fromTimeString = itemReceived!!.from.toString()
                val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
                val fromTime = LocalTime.parse(fromTimeString, timeFormatter)
                val toTimeString = itemReceived!!.to.toString()
                val toTime = LocalTime.parse(toTimeString, timeFormatter)
                val timePicker =
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        myCalendar[Calendar.HOUR_OF_DAY] = hourOfDay
                        myCalendar[Calendar.MINUTE] = minute
                        val myFormat = "HH:mm a" //In which you need put here
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
                        val formattedTime = timeFormatter.format(LocalTime.of(hourOfDay, minute))
                        val selectedTime = LocalTime.parse(formattedTime, timeFormatter)
                        /*     val fromTime = LocalTime.parse(itemReceived!!.from)
                         val toTime = LocalTime.parse(itemReceived!!.to)*/
                        if (selectedTime.isAfter(fromTime) && selectedTime.isBefore(toTime)
                        ) {
                            bindingBottomSheetLeisureBinding.hourTxt.text = sdf.format(myCalendar.time)
                        } else {

                            bindingBottomSheetLeisureBinding.hourTxt.text = ""
                            Toast.makeText(
                                context,
                                getString(R.string.no_booking_error_date1) + " " + itemReceived!!.title.default + " " + getString(
                                    R.string.no_booking_error_date2
                                ),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                TimePickerDialog(
                    context, R.style.DatePicker, timePicker,
                    myCalendar[Calendar.HOUR_OF_DAY],
                    myCalendar[Calendar.MINUTE], true
                ).show()
            }
        }
    }


    private fun showConfirmationRequestPopup() {
        val myDialog = Dialog(bindingBottomSheetLeisureBinding.root.context)
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bindingDialog = DialogPasscodeBinding.inflate(LayoutInflater.from(bindingBottomSheetLeisureBinding.root.context))
        myDialog.setContentView(bindingDialog.root)

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

            val date: String = bindingBottomSheetLeisureBinding.dateTxt.text.toString()
            val time: String = bindingBottomSheetLeisureBinding.hourTxt.text.toString()
            val nbrPerson: Int = bindingBottomSheetLeisureBinding.nbrPerson.text.toString().toInt()
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
                        val leisureClient = LeisureClient(requireView().context)
                        val leisureList = leisureClient.getLeisureMenu()
                        val itemReceived = leisureList[itemPosition]
                        val reservationClient = ReservationClient(bindingDialog.root.context)
                        val createReservationInput = CreateReservationInput(
                            date = "$date $time",
                            itemId = Optional.present(itemReceived!!.id),
                            numberOfPerson = nbrPerson.toDouble(),
                            reservationType = ReservationTypeEnum.leisure,
                            residenceId = ResidenceClient(bindingDialog.root.context).getResidenceId()
                                .toString()
                        )
                        reservationClient.createReservation(createReservationInput)
                        bindingDialog.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(
                            context,
                            getString(R.string.booking_sent_success),
                            Toast.LENGTH_LONG
                        ).show()
                        bindingBottomSheetLeisureBinding
                        bindingBottomSheetLeisureBinding.dateTxt.text = ""
                        bindingBottomSheetLeisureBinding.hourTxt.text = ""
                        bindingBottomSheetLeisureBinding.nbrPerson.text = Editable.Factory.getInstance().newEditable("1")
                        bindingBottomSheetLeisureBinding.comment.text = Editable.Factory.getInstance().newEditable("")
                        bindingBottomSheetLeisureBinding.txtDate.text = "$date $time"
                        bindingBottomSheetLeisureBinding.txtNbrPerson.text = "$nbr"
                        bindingBottomSheetLeisureBinding.scrollBar.visibility = View.GONE
                        bindingBottomSheetLeisureBinding.timeLayout.visibility = View.GONE
                        bindingBottomSheetLeisureBinding.btnBook.visibility = View.GONE
                        bindingBottomSheetLeisureBinding.beforeGoToMain.visibility = View.VISIBLE

                        myDialog.dismiss()
                    }


                }
            } catch (e: Exception) {
                Log.e("device_gsf", e.message.toString())
            }
        }

        myDialog.show()
    }
}
