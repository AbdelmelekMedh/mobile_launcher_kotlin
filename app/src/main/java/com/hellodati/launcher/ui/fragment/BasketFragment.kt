package com.hellodati.launcher.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hellodati.launcher.R
import com.hellodati.launcher.ui.adapters.BasketAdapter
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.OrderClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.database.AppDataBase
import com.hellodati.launcher.databinding.ActivityMainBinding
import com.hellodati.launcher.databinding.FragmentBasketBinding
import com.hellodati.launcher.model.Basket
import com.hellodati.launcher.type.CreateOrderInput
import com.hellodati.launcher.type.OrderItemInput
import com.hellodati.launcher.ui.activity.MainActivity


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BasketFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BasketFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentBasketBinding
    private lateinit var bindingMainActivity: ActivityMainBinding
    private lateinit var listOfBasket: MutableList<Basket>
    private lateinit var listOfItems: MutableList<OrderItemInput>
    private var listOfOrderItems = arrayListOf<Pair<String, Int>>()
    private lateinit var basketAdapter: BasketAdapter

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

        binding = FragmentBasketBinding.inflate(inflater, container, false)
        bindingMainActivity = ActivityMainBinding.inflate(inflater, container, false)

        var dataBase = AppDataBase.getInstance(binding.root.context)

        listOfBasket = dataBase!!.basketDao().getAll() as MutableList<Basket>


        if (listOfBasket.isEmpty()) {
            binding.emptyBasket.visibility = View.VISIBLE
            binding.ids.visibility = View.GONE
        }else{
            binding.headerHolder.visibility = View.VISIBLE
        }
        MainActivity.updateTextView(listOfBasket.size.toString())
        basketAdapter = BasketAdapter(listOfBasket, binding)
        binding.recyclerBasket.adapter = basketAdapter

        binding.recyclerBasket.scheduleLayoutAnimation();


        binding.validerBtn.setOnClickListener {
            showPopupConfirmation()
        }

        binding.swipeRefresh.setOnRefreshListener(
            SwipeRefreshLayout.OnRefreshListener {
                val dataBase = AppDataBase.getInstance(binding.root.context)

                listOfBasket = dataBase!!.basketDao().getAll() as MutableList<Basket>


                if (listOfBasket.isEmpty()) {
                    binding.emptyBasket.visibility = View.VISIBLE
                    binding.ids.visibility = View.GONE
                }
                MainActivity.updateTextView(listOfBasket.size.toString())
                basketAdapter = BasketAdapter(listOfBasket, binding)
                binding.recyclerBasket.adapter = basketAdapter

                binding.recyclerBasket.scheduleLayoutAnimation();
                binding.swipeRefresh.isRefreshing = false
            })

        updatePrixTotal()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun updatePrixTotal() {

        var prixTotal = 0.0
        for (plat in listOfBasket) {
            prixTotal += plat.quantity * plat.price
        }
        if(prixTotal == floor(prixTotal)){
            binding.totalPrice.text = prixTotal.toInt().toString()
        }else{
            binding.totalPrice.text = prixTotal.toString()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BasketFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        fun shakeUserCodeError(): TranslateAnimation? {
            val shake = TranslateAnimation(0f, 10f, 0f, 0f)
            shake.duration = 1000
            shake.interpolator = CycleInterpolator(7f)
            return shake
        }
    }

    fun showPopupConfirmation() {

        if (binding.userId.text.isNullOrEmpty()) {
            binding.userId.startAnimation(shakeError())
        } else {
            try {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        binding.validerBtn.isClickable = false
                        val result = GuestClient(binding.root.context).checkPassword(
                            ResidenceClient(binding.root.context).getGuestId().toString(),
                            binding.userId.text.toString()
                        )
                        binding.userId.text.clear()
                        val imm = binding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                        if (result != true){
                            binding.userId.startAnimation(shakeError())
                        }else{
                            listOfItems = mutableListOf(
                                OrderItemInput("item1", 1.0)
                            )
                            listOfItems.clear()
                            for (itemBasket in AppDataBase.getInstance(binding.root.context)!!.basketDao().getAll()) {
                                listOfItems.add(OrderItemInput(itemBasket.idDb, itemBasket.quantity.toDouble()))
                            }
                            try {
                                GlobalScope.launch {

                                    withContext(Dispatchers.Main) {
                                        val orderPostClient = OrderClient(binding.root.context)
                                        val createOrderInput =
                                            ResidenceClient(binding.root.context).getResidenceId()?.let {
                                                CreateOrderInput(
                                                    items = listOfItems,
                                                    residenceId = it
                                                )
                                            }
                                        createOrderInput?.let { orderPostClient.createOrder(it) }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("device_gsf", e.message.toString())
                            }
                            val countDownTimer: CountDownTimer = object : CountDownTimer(50, 2000) {
                                override fun onTick(millisUntilFinished: Long) {

                                }
                                override fun onFinish() {
                                    binding.emptyBasket.visibility = View.VISIBLE
                                    binding.successBasket.visibility = View.VISIBLE
                                    binding.successBasket.startAnimation(
                                        AnimationUtils.loadAnimation(
                                            binding.root.context,
                                            R.anim.slide_up
                                        ))
                                }
                            }
                            countDownTimer.start()


                            val handler = Handler()
                            val runnable = Runnable {
                                binding.successBasket.visibility = View.GONE
                                binding.ids.visibility = View.GONE
                            }
                            handler.postDelayed(runnable, 10000)
                            val iterator = listOfBasket.iterator()
                            while (iterator.hasNext()) {
                                val itemBasket = iterator.next()
                                AppDataBase.getInstance(binding.root.context)!!.basketDao().delete(itemBasket)
                                iterator.remove()
                            }
                            binding.recyclerBasket.adapter!!.notifyDataSetChanged()
                            binding.emptyBasket.visibility = View.VISIBLE
                            MainActivity.updateTextView("0")
                            MainActivity.shakeBadge()
      /*                      for ((index, itemBasket) in listOfBasket.withIndex()) {
                                AppDataBase.getInstance(binding.root.context)!!.basketDao().delete(itemBasket)
                                listOfBasket.removeAt(index)
                                binding.recyclerBasket.adapter!!.notifyItemRemoved(index)
                                binding.recyclerBasket.adapter!!.notifyDataSetChanged()
                            }
                            MainActivity.updateTextView("0")
                            MainActivity.shakeBadge()*/
                        }
                        binding.validerBtn.isClickable = true
                    }
                }
            } catch (e: Exception) {
                Log.e("device_gsf", e.message.toString())
            }


        }

        binding.followBtn.setOnClickListener {
            val navController = Navigation.findNavController(
                (binding.root.context as Activity),
                R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.orderHistoryFragment)
        }
    }

    fun shakeError(): TranslateAnimation? {
        val shake = TranslateAnimation(0f, 10f, 0f, 0f)
        shake.duration = 1000
        shake.interpolator = CycleInterpolator(7f)
        return shake
    }
}