package com.hellodati.launcher.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hellodati.launcher.databinding.CsiLayoutBinding

class CsiLayoutActivity : AppCompatActivity()  {

    lateinit var binding: CsiLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CsiLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}