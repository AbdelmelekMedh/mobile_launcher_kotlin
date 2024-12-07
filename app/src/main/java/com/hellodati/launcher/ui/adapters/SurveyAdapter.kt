package com.hellodati.launcher.ui.adapters

import android.R.attr.height
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hellodati.launcher.GsiAvailableQuestionsQuery
import com.hellodati.launcher.R
import com.hellodati.launcher.databinding.FragmentSurveyListBinding
import com.hellodati.launcher.databinding.ItemSurveyBinding
import com.hellodati.launcher.serializable_data.SerializableSurvey
import com.hellodati.launcher.ui.helper.LocalHelper
import java.lang.Integer.parseInt


class SurveyAdapter(
    private val surveyList: List<GsiAvailableQuestionsQuery.GsiAvailableQuestion>,
    val binding: FragmentSurveyListBinding,
) :
    RecyclerView.Adapter<SurveyAdapter.ViewHolder>() {

    private var availableQuestionList: MutableList<String> = mutableListOf()
    private var stringList = listOf(
        binding.root.context.getString(R.string.how_was_your_check_in),
        binding.root.context.getString(R.string.how_did_you_find_your_room),
        binding.root.context.getString(R.string.how_do_you_qualify_our_staff),
        binding.root.context.getString(R.string.how_was_your_culinary_experience_with_us),
        binding.root.context.getString(R.string.how_was_your_experience_at_our_hotel),
        binding.root.context.getString(R.string.how_did_you_find_our_technological_solution)
    )

    private var questionList: List<String> = listOf(
        "checkIn",
        "room",
        "guest_staff",
        "food",
        "hotel_review",
        "app_review"
    )

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemSurveyBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_survey, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.binding.apply {
                for (i in surveyList.indices){
                    if (surveyList[i].question == questionList[position]){
                        when (surveyList[i].question) {
                            "checkIn" -> {
                                cardSurvey.layoutParams.height =  250
                                cardSurvey.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                                Glide.with(binding.root.context)
                                    .load(R.drawable.p22)
                                    .into(cardColor)
                                when (LocalHelper.getLanguage(binding.root.context).toString()) {
                                    "en" -> {
                                        state.text = stringList[position]
                                    }

                                    "ar" -> {
                                        state.text = stringList[position]
                                    }

                                    "fr" -> {
                                        state.text = stringList[position]
                                    }

                                    else -> {
                                        state.text = stringList[position]
                                    }
                                }

                                if (state.text.isNullOrEmpty()) {
                                    state.text = stringList[position]
                                }

                            }
                            "room" -> {
                                cardSurvey.layoutParams.height = 250
                                cardSurvey.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                                Glide.with(binding.root.context)
                                    .load(R.drawable.p21)
                                    .into(cardColor)
                                when (LocalHelper.getLanguage(binding.root.context).toString()) {
                                    "en" -> {
                                        state.text = stringList[position]
                                    }

                                    "ar" -> {
                                        state.text = stringList[position]
                                    }

                                    "fr" -> {
                                        state.text = stringList[position]
                                    }

                                    else -> {
                                        state.text = stringList[position]
                                    }
                                }

                                if (state.text.isNullOrEmpty()) {
                                    state.text = stringList[position]
                                }
                            }
                            "guest_staff" -> {
                                cardSurvey.layoutParams.height = 250
                                cardSurvey.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                                Glide.with(binding.root.context)
                                    .load(R.drawable.p19)
                                    .into(cardColor)
                                when (LocalHelper.getLanguage(binding.root.context).toString()) {
                                    "en" -> {
                                        state.text = stringList[position]
                                    }

                                    "ar" -> {
                                        state.text = stringList[position]
                                    }

                                    "fr" -> {
                                        state.text = stringList[position]
                                    }

                                    else -> {
                                        state.text = stringList[position]
                                    }
                                }

                                if (state.text.isNullOrEmpty()) {
                                    state.text = stringList[position]
                                }


                            }
                            "food" -> {
                                cardSurvey.layoutParams.height = 250
                                cardSurvey.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                                Glide.with(binding.root.context)
                                    .load(R.drawable.p18)
                                    .into(cardColor)
                                when (LocalHelper.getLanguage(binding.root.context).toString()) {
                                    "en" -> {
                                        state.text = stringList[position]
                                    }

                                    "ar" -> {
                                        state.text = stringList[position]
                                    }

                                    "fr" -> {
                                        state.text = stringList[position]
                                    }

                                    else -> {
                                        state.text = stringList[position]
                                    }
                                }

                                if (state.text.isNullOrEmpty()) {
                                    state.text = stringList[position]
                                }

                            }
                            "hotel_review" -> {
                                cardSurvey.layoutParams.height = 250
                                cardSurvey.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                                Glide.with(binding.root.context)
                                    .load(R.drawable.p17)
                                    .into(cardColor)
                                when (LocalHelper.getLanguage(binding.root.context).toString()) {
                                    "en" -> {
                                        state.text = stringList[position]
                                    }

                                    "ar" -> {
                                        state.text = stringList[position]
                                    }

                                    "fr" -> {
                                        state.text = stringList[position]
                                    }

                                    else -> {
                                        state.text = stringList[position]
                                    }
                                }

                                if (state.text.isNullOrEmpty()) {
                                    state.text = stringList[position]
                                }


                            }
                            "app_review" -> {
                                cardSurvey.layoutParams.height = 250
                                cardSurvey.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                                Glide.with(binding.root.context)
                                    .load(R.drawable.p20)
                                    .into(cardColor)
                                when (LocalHelper.getLanguage(binding.root.context).toString()) {
                                    "en" -> {
                                        state.text = stringList[position]
                                    }

                                    "ar" -> {
                                        state.text = stringList[position]
                                    }

                                    "fr" -> {
                                        state.text = stringList[position]
                                    }

                                    else -> {
                                        state.text = stringList[position]
                                    }
                                }

                                if (state.text.isNullOrEmpty()) {
                                    state.text = stringList[position]
                                }
                            }
                        }
                    }
                }

                holder.itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putSerializable("item", SerializableSurvey(stringList[position]))

                    val navController = Navigation.findNavController(
                        (binding.root.context as Activity),
                        R.id.nav_host_fragment_content_main
                    )
                    navController.navigate(R.id.surveyFragment, bundle)
                }

            }
    }

    override fun getItemCount() = stringList.size
}