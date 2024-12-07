package com.hellodati.launcher.ui.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
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
import androidx.fragment.app.Fragment
import com.hellodati.launcher.R
import com.hellodati.launcher.databinding.FragmentReviewsRestaurantBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReviewsRestaurantFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReviewsRestaurantFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentReviewsRestaurantBinding
    var myDialogLangs: Dialog? = null

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
        // Inflate the layout for this fragment
        binding = FragmentReviewsRestaurantBinding.inflate(inflater, container, false)
        binding.btnAvis.setOnClickListener{
            showReviewPopup()
        }

        binding.btnAvisNotEmpty
        return binding.root
    }

    fun showReviewPopup() {
        myDialogLangs = context?.let { Dialog(it) }
        myDialogLangs?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val listItem: View = LayoutInflater.from(context).inflate(R.layout.popup_reviews, null, false)
        myDialogLangs?.setContentView(listItem)
        val btn_cancel = listItem.findViewById<Button>(R.id.btn_cancel)
        btn_cancel.setOnClickListener { myDialogLangs?.dismiss() }
        val edit_text_comment = listItem.findViewById<EditText>(R.id.editText_Comment)
        val btn_send = listItem.findViewById<Button>(R.id.btn_sendReview)
        val ratingBarReview = listItem.findViewById<RatingBar>(R.id.ratingBarReview)
        val relativeLayout = listItem.findViewById<RelativeLayout>(R.id.relative_review)
        relativeLayout.setOnTouchListener { v, _ ->
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            true
        }
        btn_send.setOnClickListener {

        }
        myDialogLangs?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val params: ViewGroup.LayoutParams = myDialogLangs?.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        myDialogLangs?.window?.attributes = params as WindowManager.LayoutParams
       myDialogLangs?.show()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReviewsRestaurantFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReviewsRestaurantFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}