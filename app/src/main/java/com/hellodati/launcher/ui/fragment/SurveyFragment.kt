package com.hellodati.launcher.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.hellodati.launcher.R
import com.hellodati.launcher.api.GsiSubmitReviewClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.databinding.ActivityMainBinding
import com.hellodati.launcher.databinding.FragmentSurveyBinding
import com.hellodati.launcher.serializable_data.SerializableSurvey
import com.hellodati.launcher.type.GsiQuestionsEnum
import com.hellodati.launcher.type.GsiRateEnum
import com.hellodati.launcher.type.SubmitReviewInput
import com.hellodati.launcher.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private val ARG_ITEM = "item"
class SurveyFragment: Fragment() {

    private lateinit var binding: FragmentSurveyBinding
    private lateinit var bindingMain : ActivityMainBinding
    private var itemPosition: String = ""
    private var answerList: MutableList<String> = mutableListOf()
    private lateinit var questionSelected: GsiQuestionsEnum
    private lateinit var rate: GsiRateEnum
    private var selectedItem : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val item = it.getSerializable(ARG_ITEM) as SerializableSurvey
            itemPosition = item.itemPosition
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSurveyBinding.inflate(inflater, container, false)

        binding.mainQuestion.text = itemPosition
        if (itemPosition == binding.root.context.getString(R.string.how_was_your_check_in)){
            questionSelected = GsiQuestionsEnum.checkin
            binding.questionImage.setImageDrawable(resources.getDrawable(R.drawable.gsi_check_in))
            binding.question1.text = getString(R.string.it_s_been_slow)
            binding.question2.text = getString(R.string.unfriendly_staff)
            binding.question3.text = getString(R.string.hostile_welcome)
            binding.question4.text = getString(R.string.other)
            binding.question5.text = getString(R.string.it_was_quick_and_efficient)
            binding.question6.text = getString(R.string.warm_welcome_from_the_staff)
            binding.question7.text = getString(R.string.immediate_sense_of_welcome)
            binding.question8.text = getString(R.string.other)

        }else if (itemPosition == binding.root.context.getString(R.string.how_did_you_find_your_room)){
            questionSelected = GsiQuestionsEnum.room
            binding.questionImage.setImageDrawable(resources.getDrawable(R.drawable.gsi_room))
            binding.question1.text = getString(R.string.cleanliness_issues)
            binding.question2.text = getString(R.string.unpleasant_odors)
            binding.question3.text = getString(R.string.equipment_failures)
            binding.question4.text = getString(R.string.other)
            binding.question5.text = getString(R.string.spotlessly_clean_and_fresh)
            binding.question6.text = getString(R.string.equipment_in_excellent_condition)
            binding.question7.text = getString(R.string.comfortable_bed)
            binding.question8.text = getString(R.string.other)
        }else if (itemPosition == binding.root.context.getString(R.string.how_do_you_qualify_our_staff)){
            questionSelected = GsiQuestionsEnum.guest_staff
            binding.questionImage.setImageDrawable(resources.getDrawable(R.drawable.gsi_guest_staff))
            binding.question1.text = getString(R.string.lack_of_friendliness)
            binding.question2.text = getString(R.string.communication_problems)
            binding.question3.text = getString(R.string.obvious_incompetence)
            binding.question4.text = getString(R.string.other)
            binding.question5.text = getString(R.string.constant_smiles)
            binding.question6.text = getString(R.string.demonstrated_competence)
            binding.question7.text = getString(R.string.sense_of_exemplary_service)
            binding.question8.text = getString(R.string.other)
        }else if (itemPosition == binding.root.context.getString(R.string.how_was_your_culinary_experience_with_us)){
            questionSelected = GsiQuestionsEnum.food
            binding.questionImage.setImageDrawable(resources.getDrawable(R.drawable.gsi_culinary_experience))
            binding.question0Btn.visibility = View.VISIBLE
            binding.question9Btn.visibility = View.VISIBLE
            binding.question0.text = getString(R.string.lack_of_choice)
            binding.question1.text = getString(R.string.bad_quality)
            binding.question2.text = getString(R.string.bad_taste)
            binding.question3.text = getString(R.string.hygiene_problems)
            binding.question4.text = getString(R.string.other)
            binding.question9.text = getString(R.string.wide_choice_of_dishes)
            binding.question5.text = getString(R.string.high_quality_of_meals)
            binding.question6.text = getString(R.string.exquisite_flavors)
            binding.question7.text = getString(R.string.pleasant_atmosphere_in_the_restaurant)
            binding.question8.text = getString(R.string.other)
        }else if (itemPosition == binding.root.context.getString(R.string.how_was_your_experience_at_our_hotel)){
            questionSelected = GsiQuestionsEnum.hotel_review
            binding.questionImage.setImageDrawable(resources.getDrawable(R.drawable.gsi_hotel_review))
            binding.question0Btn.visibility = View.VISIBLE
            binding.question9Btn.visibility = View.VISIBLE
            binding.question0.text = getString(R.string.hygiene_problems_at_the_hotel)
            binding.question1.text = getString(R.string.lack_of_local_attraction)
            binding.question2.text = getString(R.string.lack_of_friendliness_of_staff)
            binding.question3.text = getString(R.string.limitation_of_activities)
            binding.question4.text = getString(R.string.other)
            binding.question9.text = getString(R.string.hotel_maintained_to_perfection)
            binding.question5.text = getString(R.string.strategic_location)
            binding.question6.text = getString(R.string.helpful_and_available_team)
            binding.question7.text = getString(R.string.wide_range_of_activities)
            binding.question8.text = getString(R.string.other)
        }else if (itemPosition == binding.root.context.getString(R.string.how_did_you_find_our_technological_solution)){
            questionSelected = GsiQuestionsEnum.app_review
            binding.questionImage.setImageDrawable(resources.getDrawable(R.drawable.gsi_app_review))
            binding.question1.text = getString(R.string.complexity_of_use)
            binding.question2.text = getString(R.string.insufficient_internet_package)
            binding.question3.text = getString(R.string.i_prefer_to_use_my_own_phone)
            binding.question4.text = getString(R.string.other)
            binding.question5.text = getString(R.string.very_useful_technologies)
            binding.question6.text = getString(R.string.user_friendly_interface)
            binding.question7.text = getString(R.string.impressed_by_innovation)
            binding.question8.text = getString(R.string.other)
        }
        binding.likeImage.setOnClickListener {
            answerList.clear()
            rate = GsiRateEnum.top
            binding.conformationLayout.visibility = View.VISIBLE
            binding.ratingQuestions.visibility = View.VISIBLE
            binding.topRatingQuestion.visibility = View.VISIBLE
            binding.flopRatingQuestion.visibility = View.INVISIBLE
            binding.dislikeImage.setImageDrawable(resources.getDrawable(R.drawable.dislike))
            binding.likeImage.setImageDrawable(resources.getDrawable(R.drawable.pushed_like))
            binding.question0Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question0Btn.isSelected = false
            binding.question1Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question1Btn.isSelected = false
            binding.question2Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question2Btn.isSelected = false
            binding.question3Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question3Btn.isSelected = false
            binding.question4Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question4Btn.isSelected = false
            binding.question5Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question5Btn.isSelected = false
            binding.question6Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question6Btn.isSelected = false
            binding.question7Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question7Btn.isSelected = false
            binding.question8Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question8Btn.isSelected = false
            binding.question9Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question9Btn.isSelected = false
        }

        binding.dislikeImage.setOnClickListener {
            answerList.clear()
            rate = GsiRateEnum.flop
            binding.conformationLayout.visibility = View.VISIBLE
            binding.ratingQuestions.visibility = View.VISIBLE
            binding.topRatingQuestion.visibility = View.INVISIBLE
            binding.flopRatingQuestion.visibility = View.VISIBLE
            binding.dislikeImage.setImageDrawable(resources.getDrawable(R.drawable.pushed_dislike))
            binding.likeImage.setImageDrawable(resources.getDrawable(R.drawable.like))
            binding.question0Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question0Btn.isSelected = false
            binding.question1Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question1Btn.isSelected = false
            binding.question2Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question2Btn.isSelected = false
            binding.question3Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question3Btn.isSelected = false
            binding.question4Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question4Btn.isSelected = false
            binding.question5Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question5Btn.isSelected = false
            binding.question6Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question6Btn.isSelected = false
            binding.question7Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question7Btn.isSelected = false
            binding.question8Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question8Btn.isSelected = false
            binding.question9Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            binding.question9Btn.isSelected = false
        }

        binding.question0Btn.setOnClickListener {
            if (!binding.question0Btn.isSelected){
                answerList.add(binding.question0.text.toString())
                binding.question0Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.newboy))
                binding.question0Btn.isSelected = true
            }else{
                answerList.remove(binding.question0.text.toString())
                binding.question0Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.question0Btn.isSelected = false
            }
        }

        binding.question1Btn.setOnClickListener {
            if (!binding.question1Btn.isSelected){
                answerList.add(binding.question1.text.toString())
                binding.question1Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.newboy))
                binding.question1Btn.isSelected = true
            }else{
                answerList.remove(binding.question1.text.toString())
                binding.question1Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.question1Btn.isSelected = false
            }
        }

        binding.question2Btn.setOnClickListener {
            if (!binding.question2Btn.isSelected){
                answerList.add(binding.question2.text.toString())
                binding.question2Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.newboy))
                binding.question2Btn.isSelected = true
            }else{
                answerList.remove(binding.question2.text.toString())
                binding.question2Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.question2Btn.isSelected = false
            }
        }

        binding.question3Btn.setOnClickListener {
            if (!binding.question3Btn.isSelected){
                answerList.add(binding.question3.text.toString())
                binding.question3Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.newboy))
                binding.question3Btn.isSelected = true
            }else{
                answerList.remove(binding.question3.text.toString())
                binding.question3Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.question3Btn.isSelected = false
            }
        }

        binding.question4Btn.setOnClickListener {
            if (!binding.question4Btn.isSelected){
                answerList.add(binding.question4.text.toString())
                binding.question4Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.newboy))
                binding.question4Btn.isSelected = true
            }else{
                answerList.remove(binding.question4.text.toString())
                binding.question4Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.question4Btn.isSelected = false
            }
        }

        binding.question5Btn.setOnClickListener {
            if (!binding.question5Btn.isSelected){
                binding.question5Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.newboy))
                answerList.add(binding.question5.text.toString())
                binding.question5Btn.isSelected = true

            }else{
                binding.question5Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                answerList.remove(binding.question5.text.toString())
                binding.question5Btn.isSelected = false


            }
        }

        binding.question6Btn.setOnClickListener {
            if (!binding.question6Btn.isSelected){
                answerList.add(binding.question6.text.toString())
                binding.question6Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.newboy))
                binding.question6Btn.isSelected = true
            }else{
                answerList.remove(binding.question6.text.toString())
                binding.question6Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.question6Btn.isSelected = false
            }
        }

        binding.question7Btn.setOnClickListener {
            if (!binding.question7Btn.isSelected){
                answerList.add(binding.question7.text.toString())
                binding.question7Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.newboy))
                binding.question7Btn.isSelected = true
            }else{
                answerList.remove(binding.question7.text.toString())
                binding.question7Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.question7Btn.isSelected = false
            }
        }

        binding.question8Btn.setOnClickListener {
            if (!binding.question8Btn.isSelected){
                answerList.add(binding.question8.text.toString())
                binding.question8Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.newboy))
                binding.question8Btn.isSelected = true
            }else{
                answerList.remove(binding.question8.text.toString())
                binding.question8Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.question8Btn.isSelected = false
            }
        }

        binding.question9Btn.setOnClickListener {
            if (!binding.question9Btn.isSelected){
                answerList.add(binding.question9.text.toString())
                binding.question9Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.newboy))
                binding.question9Btn.isSelected = true
            }else{
                answerList.remove(binding.question9.text.toString())
                binding.question9Btn.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.question9Btn.isSelected = false
            }
        }

        binding.conformationBtn.setOnClickListener {
            if (answerList.isEmpty()){
                Toast.makeText(
                    binding.root.context, getString(R.string.chose_an_answer_to_submit_your_review),
                    Toast.LENGTH_LONG
                ).show()
            }else{
                try {
                    GlobalScope.launch (Dispatchers.Main) {
                        binding.conformationBtn.isClickable = false
                        val input = SubmitReviewInput(questionSelected, rate, ResidenceClient(binding.root.context).getResidenceId().toString(), answerList)
                        val gsiSubmitReviewClient = GsiSubmitReviewClient(binding.root.context)
                        gsiSubmitReviewClient.createGsiReview(input)
                        requireActivity().onBackPressed()
                    }
                }catch (e:Exception){
                    Log.e("device_gsf", e.message.toString())
                }
            }
        }

        binding.btnSkip.setOnClickListener {
            requireActivity().onBackPressed()
            bindingMain.imgChat.visibility = View.VISIBLE
            bindingMain.imgProfile.visibility = View.VISIBLE
            bindingMain.appBarMain.linearLayout6.visibility = View.VISIBLE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingMain = (requireActivity() as MainActivity).binding
        bindingMain.imgChat.visibility = View.GONE
        bindingMain.imgProfile.visibility = View.GONE
        bindingMain.appBarMain.linearLayout6.visibility = View.GONE
    }

    companion object {
        @JvmStatic
        fun newInstance(item: SerializableSurvey) =
            MenuRestaurantDrinkFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ITEM, item)
                }
            }
    }
}