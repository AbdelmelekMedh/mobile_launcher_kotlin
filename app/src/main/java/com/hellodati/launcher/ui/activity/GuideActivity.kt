package com.hellodati.launcher.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.hellodati.launcher.R
import com.hellodati.launcher.ui.adapters.MyViewPagerAdapter
import com.hellodati.launcher.databinding.ActivityGuideBinding

class GuideActivity : AppCompatActivity() {
    private lateinit var binding:ActivityGuideBinding
    lateinit var dots: Array<TextView>
    lateinit var layouts: IntArray
    val views = ArrayList<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGuideBinding.inflate(layoutInflater)


        views.add(layoutInflater.inflate(R.layout.fragment_slide1, null))
        views.add(layoutInflater.inflate(R.layout.fragment_slide2, null))
        views.add(layoutInflater.inflate(R.layout.fragment_slide3, null))
        views.add(layoutInflater.inflate(R.layout.fragment_slide4, null))

        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)

        binding.wifiView.setOnClickListener {
            val wifiApp = binding.root.context.packageManager.getLaunchIntentForPackage("com.hellodati.wificonfig")
            if (wifiApp != null){
                binding.root.context.startActivity(wifiApp)
            }else{
                Toast.makeText(
                    binding.root.context,
                    binding.root.context.getString(R.string.error_datiphone),
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        addBottomDots(0)

        val pagerAdapter = MyViewPagerAdapter(views)
        binding.viewPager.adapter = pagerAdapter



        val viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                addBottomDots(position)

                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == views.size - 1) {
                    // last page. make button text to GOT IT
                    binding.btnnextTxtview.text = getString(R.string.start)
                    binding.btnSkip.visibility = View.GONE
                } else {
                    // still pages are left
                    binding.btnnextTxtview.text = getString(R.string.next)
                    binding.btnSkip.setVisibility(View.VISIBLE)
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        }

        binding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener)

        binding.btnNext.setOnClickListener(View.OnClickListener {
            val current: Int = getItem(+1)
            if (current < views.size) {
                binding.viewPager.currentItem = current
            } else {
                val intent = Intent(applicationContext, WelcomeActivity::class.java)
                startActivity(intent)
            }
        })

        binding.btnSkip.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, WelcomeActivity::class.java)
            startActivity(intent)
        })

        setContentView(binding.root)
    }

    private fun addBottomDots(currentPage: Int) {
        dots = Array<TextView>(views.size) { TextView(applicationContext) }
        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)
        binding.layoutDots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i].text = Html.fromHtml(".")
            dots[i].textSize = 70f
            dots[i].setTextColor(colorsInactive[currentPage])
            binding.layoutDots.addView(dots[i])
        }
        if (dots.isNotEmpty())
            dots[currentPage].setTextColor(colorsActive[currentPage])
    }
    private fun getItem(i: Int): Int {
        return binding.viewPager.currentItem + i
    }
}