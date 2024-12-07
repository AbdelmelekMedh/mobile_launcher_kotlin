package com.hellodati.launcher.ui.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.Navigation
import com.apollographql.apollo3.api.Optional
import com.hellodati.launcher.R
import com.hellodati.launcher.WellBeing_mobileQuery
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.ReservationClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.DialogPasscodeBinding
import com.hellodati.launcher.databinding.FragmentBookingWellBeingBinding
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

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_ITEM = "item"

class BookingWellBeingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var itemReceived: WellBeing_mobileQuery.WellBeing_mobile
    private lateinit var binding : FragmentBookingWellBeingBinding
    private var nbr: Int? = 1
    lateinit var myDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        myDialog = Dialog(requireContext())
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBookingWellBeingBinding.inflate(inflater, container, false)

        binding.date.setOnClickListener {
            onClickDatePick(view)
        }

        binding.hour.setOnClickListener {
            onClickTimePick(view)
        }

        binding.nbrPerson.text = Editable.Factory.getInstance().newEditable(nbr.toString())

        binding.increase.setOnClickListener(View.OnClickListener {
            nbr = binding.nbrPerson.text.toString().toInt()
            nbr = nbr!! + 1
            binding.nbrPerson.text = Editable.Factory.getInstance().newEditable(nbr.toString())
        })
        binding.decrease.setOnClickListener(View.OnClickListener {
            if (nbr!! > 1) {
                nbr = binding.nbrPerson.text.toString().toInt()
                nbr = nbr!! - 1
                binding.nbrPerson.text = Editable.Factory.getInstance().newEditable(nbr.toString())
            }
        })

        binding.btnBook.setOnClickListener(View.OnClickListener {

            if (binding.dateTxt.text.toString() == "") {
                Toast.makeText(context, getString(R.string.date_required), Toast.LENGTH_LONG).show()
            } else {
                if (binding.hourTxt.text.toString() == "") {
                    Toast.makeText(context, getString(R.string.hour_required), Toast.LENGTH_LONG)
                        .show()
                } else {
                    showConfirmationRequestPopup()
                }
            }
        })

        binding.backBtn.setOnClickListener {
            binding.bookingForm.visibility = View.VISIBLE
            binding.beforeGoToMain.visibility = View.GONE
            binding.txtDate.text = ""
            binding.txtNbrPerson.text = ""
        }

        binding.backBtn.setOnClickListener {
            val navController = Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            )

            navController.navigate(R.id.wellBeingFragment)
        }

        return  binding.root
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
                    binding.dateTxt.text = sdf.format(myCalendar.time)
                } else {
                    // The selected date is in the past
                    var title:String
                    when (LocalHelper.getLanguage(binding.root.context).toString()) {
                        "en" -> {
                            title = itemReceived!!.title.en!!
                        }

                        "ar" -> {
                            title = itemReceived!!.title.ar!!
                        }

                        "fr" -> {
                            title = itemReceived!!.title.fr!!
                        }

                        else -> {
                            title = itemReceived!!.title.default!!
                        }
                    }

                    if (title.isNullOrEmpty()){
                        title = itemReceived!!.title.default!!
                    }
                    binding.dateTxt.text = ""
                    Toast.makeText(
                        context,
                        getString(R.string.no_booking_error_date1)+" "+title+" "+getString(R.string.no_booking_error_date2),
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
        val timePicker =
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                myCalendar[Calendar.HOUR_OF_DAY] = hourOfDay
                myCalendar[Calendar.MINUTE] = minute
                val myFormat = "HH:mm" //In which you need put here
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.hourTxt.text = sdf.format(myCalendar.time)
            }
        TimePickerDialog(
            context, R.style.DatePicker, timePicker,
            myCalendar[Calendar.HOUR_OF_DAY],
            myCalendar[Calendar.MINUTE], true
        ).show()
    }

    private fun showConfirmationRequestPopup() {
        val bindingDialog = DialogPasscodeBinding.inflate(layoutInflater)
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

            val date: String = binding.dateTxt.text.toString()
            val time: String = binding.hourTxt.text.toString()
            val nbrPerson: Int = binding.nbrPerson.text.toString().toInt()
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
                        val reservationClient = ReservationClient(binding.root.context)
                        val createReservationInput = CreateReservationInput(
                            date = "$date $time",
                            itemId = Optional.absent(),
                            numberOfPerson = nbrPerson.toDouble(),
                            reservationType = ReservationTypeEnum.wellbeingitems,
                            residenceId = ResidenceClient(binding.root.context).getResidenceId().toString()
                        )
                        reservationClient.createReservation(createReservationInput)
                        bindingDialog.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(
                            context,
                            getString(R.string.booking_sent_success),
                            Toast.LENGTH_LONG
                        ).show()
                        binding
                        binding.dateTxt.text = ""
                        binding.hourTxt.text = ""
                        binding.nbrPerson.text = Editable.Factory.getInstance().newEditable("1")
                        binding.comment.text = Editable.Factory.getInstance().newEditable("")
                        binding.txtDate.text = "$date $time"
                        binding.txtNbrPerson.text = "$nbr"
                        binding.bookingForm.visibility = View.GONE
                        binding.beforeGoToMain.visibility = View.VISIBLE

                        myDialog.dismiss()
                    }


                }
            } catch (e: Exception) {
                Log.e("device_gsf", e.message.toString())
            }
        }

        myDialog.show()
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConciergeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}