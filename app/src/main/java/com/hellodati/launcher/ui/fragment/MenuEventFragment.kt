
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.apollographql.apollo3.api.Optional
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hellodati.launcher.Events_mobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.ClickClient
import com.hellodati.launcher.api.EventClient
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.ReservationClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.database.AppDataBase
import com.hellodati.launcher.databinding.DialogPasscodeBinding
import com.hellodati.launcher.databinding.FragmentBottomSheetEventBinding
import com.hellodati.launcher.databinding.FragmentBottomSheetInRoomFoodBinding
import com.hellodati.launcher.databinding.FragmentMenuEventBinding
import com.hellodati.launcher.model.Basket
import com.hellodati.launcher.serializable_data.SerializableEvent
import com.hellodati.launcher.type.CreateReservationInput
import com.hellodati.launcher.type.ReservationTypeEnum
import com.hellodati.launcher.type.ServiceEnum
import com.hellodati.launcher.ui.activity.MainActivity
import com.hellodati.launcher.ui.fragment.BasketFragment
import com.hellodati.launcher.ui.fragment.BottomSheetEventFragment
import com.hellodati.launcher.ui.fragment.RestaurantFragment
import com.hellodati.launcher.ui.helper.LocalHelper
import com.synnapps.carouselview.ImageClickListener
import com.synnapps.carouselview.ImageListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MenuEventFragment : Fragment(), ViewPager.OnPageChangeListener {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentMenuEventBinding
    private lateinit var eventList: List<Events_mobileQuery.Events_mobile>
    private lateinit var sampleImages: MutableList<String>
    private var isPageSelectedCalled = false
    private var previousPosition = 0
    private var currentPosition: Int = 0
    private var nbr: Int? = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        sampleImages = mutableListOf()
        eventList = emptyList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuEventBinding.inflate(inflater, container, false)
        binding.serviceTitle.text = resources.getString(R.string.event_service)
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            binding.msgTitle.text = getString(R.string.no_internet_connection)
            binding.emptyMenuText.text = ""
            binding.emptyMenuImage.visibility = View.GONE
            binding.emptyMenu.setBackgroundResource(R.drawable.no_connection_v2)
            binding.emptyMenu.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }else{
            binding.carouselView.pageCount = eventList.size
            binding.carouselView.setImageListener(imageListener)
            try {
                GlobalScope.launch(Dispatchers.Main) {
                    val eventClient = EventClient(binding.root.context)
                    eventList = eventClient.getAllEvent()
                    if (eventList.isNotEmpty()){
                        binding.progressBar.visibility = View.GONE
                        binding.carouselView.visibility = View.VISIBLE
                        binding.categoryFilter.visibility = View.VISIBLE
                        binding.btnBooking.visibility = View.VISIBLE
                        val hotelLinksPreferences = binding.root.context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)

                        for (event in eventList) {
                            val imageURL =
                                hotelLinksPreferences.getString("api_files_server", null) +
                                        "/picture/event_pictures/${event.id}_${event.picture}?height=300"
                            sampleImages.add(imageURL)
                        }

                        binding.carouselView.pageCount = eventList.size
                        binding.carouselView.setImageListener(imageListener)

                        binding.carouselView.setImageClickListener{ position ->
                            binding.carouselView.isClickable = false
                            binding.carouselView.pauseCarousel()
                            val bottomSheetFragment = BottomSheetEventFragment.newInstance(SerializableEvent(position))
                            bottomSheetFragment.show(
                                (binding.root.context as FragmentActivity).supportFragmentManager,
                                bottomSheetFragment.tag
                            )
                            GlobalScope.launch (Dispatchers.Main) {
                                val clickClient = ClickClient(binding.root.context)
                                clickClient.createClick(
                                    ResidenceClient(binding.root.context).getResidenceId().toString(),
                                    eventList!![position].id,
                                    ServiceEnum.Events
                                )
                                /*val bundle = Bundle()
                                bundle.putSerializable("item", SerializableEvent(position))
                                val navController = Navigation.findNavController(
                                    (binding.root.context as Activity),
                                    R.id.nav_host_fragment_content_main
                                )
                                navController.navigate(R.id.eventFragment, bundle)*/
                            }
                        }

                        binding.btnBooking.setOnClickListener {
                            binding.btnBooking.isClickable = false
                            binding.carouselView.pauseCarousel()
                            val bottomSheetFragment = BottomSheetEventFragment.newInstance(SerializableEvent(currentPosition))
                            bottomSheetFragment.show(
                                (binding.root.context as FragmentActivity).supportFragmentManager,
                                bottomSheetFragment.tag
                            )
                            GlobalScope.launch (Dispatchers.Main){
                                val clickClient = ClickClient(binding.root.context)
                                clickClient.createClick(
                                    ResidenceClient(binding.root.context).getResidenceId().toString(),
                                    eventList!![currentPosition].id,
                                    ServiceEnum.Events
                                )
                                /*val bundle = Bundle()
                                bundle.putSerializable("item", SerializableEvent(currentPosition))
                                val navController = Navigation.findNavController(
                                    (binding.root.context as Activity),
                                    R.id.nav_host_fragment_content_main
                                )
                                navController.navigate(R.id.eventFragment, bundle)*/
                            }
                        }
                    }else{
                        binding.progressBar.visibility = View.GONE
                        binding.emptyMenu.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.e("device_gsf", e.message.toString())
            }
        }

        binding.carouselView.addOnPageChangeListener(this)

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    private var imageListener: ImageListener = ImageListener { position, imageView ->
        Glide.with(requireContext())
            .load(sampleImages[position])
            .into(imageView)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        binding.eventName.text = eventList[position].title.default
        currentPosition = position
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val outputFormatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dayFrom = LocalDateTime.parse(eventList[position].datefrom.toString(), formatter)
        val dayTo = LocalDateTime.parse(eventList[position].dateto.toString(), formatter)


        //binding.txtOpeningTime.text= time.format(outputFormatter) +" "+day.format(outputFormatterDate)
        binding.txtPhone.text = eventList[position].phone!!.number.toString()
        binding.txtLocation.text = eventList[position].location
        if (eventList[position].singleDay){
            binding.endDate.visibility = View.GONE
            binding.txtOpeningDate.text = "${binding.root.context.resources.getString(R.string.reservation_date)}: ${dayFrom.format(outputFormatterDate)} "
            binding.txtOpeningTime.text = " ${binding.root.context.resources.getString(R.string.openning_time_from)}: ${dayFrom.format(outputFormatter)} - ${binding.root.context.resources.getString(R.string.openning_time_to)}: ${dayTo.format(outputFormatter)}"
        }else{
            binding.endDate.visibility = View.VISIBLE
            binding.txtOpeningTime.text = " ${binding.root.context.resources.getString(R.string.openning_time_from)}: ${dayFrom.format(outputFormatter)} - ${binding.root.context.resources.getString(R.string.openning_time_to)}: ${dayTo.format(outputFormatter)}"
            binding.txtOpeningDate.text = "${binding.root.context.resources.getString(R.string.reservation_date)} ${binding.root.context.resources.getString(R.string.openning_time_from)}: ${dayFrom.format(outputFormatterDate)}"
            binding.txtEndDate.text = "${binding.root.context.resources.getString(R.string.reservation_date)} ${binding.root.context.resources.getString(R.string.openning_time_to)}: ${dayTo.format(outputFormatterDate)}"
        }


        if(eventList[position].status!!.rawValue == "free"){
            binding.price.text = "free"
            binding.oldprice.visibility = View.GONE
        }else{
            if(eventList[position].price!!.hasDiscount == true){
                binding.oldprice.visibility = View.VISIBLE
                if (eventList[position].price!!.discountType.toString() == "percentege"){
                    val discountAmount : Double = eventList[position].price!!.amount - ((eventList[position].price!!.discountAmount!! * eventList[position].price!!.amount) / 100.0)
                    if(discountAmount == floor(discountAmount)){
                        binding.price.text = "${discountAmount.toInt()} ${eventList[position].price!!.currency}"
                    }else{
                        binding.price.text = "$discountAmount ${eventList[position].price!!.currency}"
                    }
                    if(eventList[position].price!!.amount == floor(eventList[position].price!!.amount)){
                        binding.oldprice.text = "${eventList[position].price!!.amount.toInt()} ${eventList[position].price!!.currency}"
                    }else{
                        binding.oldprice.text = "${eventList[position].price!!.amount} ${eventList[position].price!!.currency}"
                    }

                    binding.oldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }else{
                    val discountAmount = eventList[position].price!!.amount - eventList[position].price!!.discountAmount!!
                    if(discountAmount == floor(discountAmount)){
                        binding.price.text = "${discountAmount.toInt()} ${eventList[position].price!!.currency}"
                    }else{
                        binding.price.text = "$discountAmount ${eventList[position].price!!.currency}"
                    }
                    if(eventList[position].price!!.amount == floor(eventList[position].price!!.amount)){
                        binding.oldprice.text = "${eventList[position].price!!.amount.toInt()} ${eventList[position].price!!.currency}"
                    }else{
                        binding.oldprice.text = "${eventList[position].price!!.amount} ${eventList[position].price!!.currency}"
                    }
                    binding.oldprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
            }else {
                binding.oldprice.visibility = View.GONE
                if (eventList[position].price!!.amount == floor(eventList[position].price!!.amount)) {
                    binding.price.text = "${eventList[position].price!!.amount.toInt()} ${eventList[position].price!!.currency}"
                } else {
                    binding.price.text = "${eventList[position].price!!.amount} ${eventList[position].price!!.currency}"
                }
            }
        }
    }

    override fun onPageSelected(position: Int) {
        /*if (isPageSelectedCalled) {
            // onPageSelected is already being executed, return to avoid infinite loop
            return
        }

        currentPosition = position
        isPageSelectedCalled = true

        val lastVisiblePosition = binding.carouselView.currentItem
        val isFirstItem = lastVisiblePosition == 0
        val isLastItem = lastVisiblePosition == sampleImages.size - 1

        if (isFirstItem && isLastItem) {
            // Only one item present, stay on the current item
            isPageSelectedCalled = false
            return
        }

        if (isFirstItem) {
            binding.carouselView.setCurrentItem(sampleImages.size - 1, false)
        } else if (isLastItem) {
            binding.carouselView.setCurrentItem(0, false)
        }

        isPageSelectedCalled = false*/

    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RestaurantFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }
}