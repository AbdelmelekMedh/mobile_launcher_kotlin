package com.hellodati.launcher.ui.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.hellodati.launcher.R
import com.hellodati.launcher.api.EateriesClient
import com.hellodati.launcher.api.RateServiceMobileClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.FragmentReviewsRestaurantDrinkBinding
import com.hellodati.launcher.serializable_data.SerializableMenuAboutBooking
import com.hellodati.launcher.type.ServiceEnum
import com.hellodati.launcher.ui.adapters.ReviewsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ReviewsRestaurantDrinkFragment : Fragment() {

    private lateinit var binding : FragmentReviewsRestaurantDrinkBinding
    private var itemPosition: Int = 0
    var myDialogLangs: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable("item") as SerializableMenuAboutBooking
            itemPosition = item.itemPosition
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentReviewsRestaurantDrinkBinding.inflate(inflater, container, false)
        binding.recyclerReviews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        try {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    val eateries = EateriesClient(requireView().context)
                    val restaurantList = eateries.getAllEateries()
                    val itemReceived = restaurantList[itemPosition]
                    val filteredList = eateries.getAllEateries().filter { eateries ->
                        eateries.id == itemReceived!!.id
                    }

                    val reviewItems = filteredList.flatMap { eateries ->
                        eateries.reviews!!
                    }

                    Log.e("aaaa 1",reviewItems.toString())
       /*             Log.e("aaaa 2",itemReceived!!.id)
                    Log.e("aaaa 3",filteredList.toString())
                    Log.e("aaaa 4",reviewItems.toString())*/
                    //binding.bar5stars.progress =
                    if (reviewItems.isEmpty()){
                        binding.progressBar.visibility = View.GONE
                        binding.emptyBasket.visibility = View.VISIBLE
                        binding.scrollReview.visibility = View.GONE
                        binding.linearRecyclerReview.visibility = View.GONE
                    }else{

                        binding.progressBar.visibility = View.GONE
                        binding.scrollReview.visibility = View.VISIBLE
                        binding.linearRecyclerReview.visibility = View.VISIBLE
                        val formattedNumber: String = String.format(Locale.US,"%.1f", filteredList[0].ratingAverage)
                        binding.avgRate.text = formattedNumber
                        binding.avgRateStars.rating = formattedNumber.toFloat()
                        binding.bar5stars.progress = filteredList[0].starsNumber!!.fiveStars.toInt()
                        binding.bar4stars.progress = filteredList[0].starsNumber!!.fourStars.toInt()
                        binding.bar3stars.progress = filteredList[0].starsNumber!!.threeStars.toInt()
                        binding.bar2stars.progress = filteredList[0].starsNumber!!.twoStars.toInt()
                        binding.bar1stars.progress = filteredList[0].starsNumber!!.oneStar.toInt()
                        binding.recyclerReviews.adapter = ReviewsAdapter(ServiceEnum.Eatery, reviewItems)
                    }


                }
            }
        }catch (e:Exception){
            Log.e("logReview", e.message.toString())
        }

        binding.swipeContainer.setOnRefreshListener {
            try {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        val eateries = EateriesClient(requireView().context)
                        val restaurantList = eateries.getAllEateries()
                        val itemReceived = restaurantList[itemPosition]
                        val filteredList = eateries.getAllEateries().filter { eateries ->
                            eateries.id == itemReceived!!.id
                        }

                        val reviewItems = filteredList.flatMap { eateries ->
                            eateries.reviews!!
                        }

                        Log.e("aaaa 1",reviewItems.toString())
                        if (reviewItems.isEmpty()){
                            binding.progressBar.visibility = View.GONE
                            binding.emptyBasket.visibility = View.VISIBLE
                            binding.scrollReview.visibility = View.GONE
                            binding.linearRecyclerReview.visibility = View.GONE
                        }else{

                            binding.progressBar.visibility = View.GONE
                            binding.scrollReview.visibility = View.VISIBLE
                            binding.linearRecyclerReview.visibility = View.VISIBLE
                            val formattedNumber: String = String.format(Locale.US,"%.1f", filteredList[0].ratingAverage)
                            binding.avgRate.text = formattedNumber
                            binding.avgRateStars.rating = formattedNumber.toFloat()
                            binding.bar5stars.progress = filteredList[0].starsNumber!!.fiveStars.toInt()
                            binding.bar4stars.progress = filteredList[0].starsNumber!!.fourStars.toInt()
                            binding.bar3stars.progress = filteredList[0].starsNumber!!.threeStars.toInt()
                            binding.bar2stars.progress = filteredList[0].starsNumber!!.twoStars.toInt()
                            binding.bar1stars.progress = filteredList[0].starsNumber!!.oneStar.toInt()
                            binding.recyclerReviews.adapter = ReviewsAdapter(ServiceEnum.Eatery, reviewItems)
                        }


                    }
                }
            }catch (e:Exception){
                Log.e("logReview", e.message.toString())
            }
            binding.swipeContainer.isRefreshing = false
        }

        binding.btnAvis.setOnClickListener{
            showReviewPopup()
        }

        binding.btnAvisNotEmpty.setOnClickListener{
            showReviewPopup()
        }
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

            try {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        val imm = listItem.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(listItem.windowToken, 0)
                        val eateries = EateriesClient(requireView().context)
                        val restaurantList = eateries.getAllEateries()
                        val itemReceived = restaurantList[itemPosition]
                        RateServiceMobileClient(binding.root.context).createRate(
                            edit_text_comment.text.toString(),itemReceived!!.id,ratingBarReview.rating.toDouble(),ResidenceClient(binding.root.context).getResidenceId().toString(),ServiceEnum.Eatery
                        )

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

    companion object {
        @JvmStatic
        fun newInstance(item: SerializableMenuAboutBooking) =
            ReviewsRestaurantDrinkFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("item", item)
                }
            }
    }
}