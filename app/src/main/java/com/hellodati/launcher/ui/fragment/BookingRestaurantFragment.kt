package com.hellodati.launcher.ui.fragment

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hellodati.launcher.R
import com.hellodati.launcher.databinding.FragmentBookingRestaurantBinding
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
var myCalendar: Calendar = Calendar.getInstance(Locale.FRANCE)

/**
 * A simple [Fragment] subclass.
 * Use the [BookingRestaurantFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookingRestaurantFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentBookingRestaurantBinding
    private var nbr: Int? = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookingRestaurantBinding.inflate(inflater, container, false)


        binding.date.setOnClickListener {
            onClickDatePick(view)
        }

        binding.hour.setOnClickListener{
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
                    Toast.makeText(context, getString(R.string.hour_required), Toast.LENGTH_LONG).show()
                } else {

                    val date: String = binding.dateTxt.text.toString()
                    val time: String = binding.hourTxt.text.toString()
                    val nbrPerson: Int = binding.nbrPerson.text.toString().toInt()
                    val comment: String = binding.comment.text.toString()
                    Toast.makeText(context, getString(R.string.booking_sent_success), Toast.LENGTH_LONG).show()
                    binding
                    binding.dateTxt.text = ""
                    binding.hourTxt.text = ""
                    binding.nbrPerson.text = Editable.Factory.getInstance().newEditable("1")
                    binding.comment.text = Editable.Factory.getInstance().newEditable("")
                    binding.txtDate.text = "$date $time"
                    binding.txtNbrPerson.text = "$nbr"
                    binding.bookingForm.visibility = View.GONE
                    binding.beforeGoToMain.visibility = View.VISIBLE



                }
            }
        })

        binding.backBtn.setOnClickListener{
            binding.bookingForm.visibility = View.VISIBLE
            binding.beforeGoToMain.visibility = View.GONE
            binding.txtDate.text = ""
            binding.txtNbrPerson.text = ""
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun onClickDatePick(view: View?) {
        val datePicker =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "yyyy-MM-dd" //In which you need put here
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.dateTxt.text = sdf.format(myCalendar.time)
            }
        context?.let {
            DatePickerDialog(
                it,
                R.style.DatePicker,
                datePicker,
                myCalendar.get(
                    Calendar.YEAR
                ),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    fun onClickTimePick(view: View?) {
        val timePicker =
            OnTimeSetListener { view, hourOfDay, minute ->
                myCalendar[Calendar.HOUR_OF_DAY] = hourOfDay
                myCalendar[Calendar.MINUTE] = minute
                val myFormat = "HH:mm" //In which you need put here
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.hourTxt.setText(sdf.format(myCalendar.time))
            }
        TimePickerDialog(
            context, R.style.DatePicker, timePicker,
            myCalendar[Calendar.HOUR_OF_DAY],
            myCalendar[Calendar.MINUTE], true
        ).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BookingRestaurantFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BookingRestaurantFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}