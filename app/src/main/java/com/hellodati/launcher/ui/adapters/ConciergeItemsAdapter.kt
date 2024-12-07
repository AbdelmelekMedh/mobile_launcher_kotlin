package com.hellodati.launcher.ui.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.hellodati.launcher.ConciergeList_mobileQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.ReservationClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.DialSendRequestBinding
import com.hellodati.launcher.databinding.DialogPasscodeBinding
import com.hellodati.launcher.databinding.ItemChildConciergeBinding
import com.hellodati.launcher.type.CreateConciergeRequestInput
import com.hellodati.launcher.ui.fragment.BasketFragment
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConciergeItemsAdapter(
    private val conciergeList: ConciergeList_mobileQuery.ConciergeList_mobile,
    val binding: DialSendRequestBinding) :
    RecyclerView.Adapter<ConciergeItemsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemChildConciergeBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_child_concierge, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.radioButton.text = conciergeList.listServices!!.default!![position]

        holder.binding.radioButton.setOnClickListener {
            try {
                binding.sendRequest.visibility = View.VISIBLE
                binding.sendRequest.setOnClickListener {
                    showConfirmationRequestPopup(conciergeList.listServices!!.default!![position])
                }
            }catch (e: Exception){
                Log.e("err",e.message.toString())
            }
        }
    }

    override fun getItemCount() : Int{
        val lang = when (LocalHelper.getLanguage(binding.root.context).toString()) {
            "en" -> conciergeList.listServices?.en?.size ?: conciergeList.listServices?.default!!.size
            "ar" -> conciergeList.listServices?.ar?.size ?: conciergeList.listServices?.default!!.size
            "fr" -> conciergeList.listServices?.fr?.size ?: conciergeList.listServices?.default!!.size
            else ->  conciergeList.listServices?.default!!.size
        }

        return lang
    }


    private fun showConfirmationRequestPopup(subItem:String) {
        val bindingDialog = DialogPasscodeBinding.inflate(LayoutInflater.from(binding.root.context))
        val myDialog2 = Dialog(binding.root.context)
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
                        val reservationClient = ReservationClient(binding.root.context)

                        val createConciergeRequestInput = CreateConciergeRequestInput(
                            residenceId = ResidenceClient(binding.root.context).getResidenceId().toString(),
                            conciergeId = conciergeList.id,
                            subItem = Optional.present(subItem)
                        )
                        reservationClient.createReservationConcierge(createConciergeRequestInput)
                        Toast.makeText(
                            binding.root.context,
                            binding.root.context.resources.getString(R.string.booking_sent_success),
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