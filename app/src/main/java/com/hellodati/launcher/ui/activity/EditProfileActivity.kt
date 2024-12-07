package com.hellodati.launcher.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hellodati.launcher.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener{
           finish()
        }

        binding.birthDate.text = ""
        binding.birthDate.text = ""

/*        binding.btnValider.setOnClickListener{
            try {
                GlobalScope.launch {

                    withContext(Dispatchers.Main) {
                        val guestClient = GuestClient(binding.root.context)
                        var updateGuestInput = UpdateGuestInput(
                            birthdate =
                        )
                        guestClient.updateProfile()
                    }
                }
            } catch (e: Exception) {
                Log.e("device_gsf", e.message.toString())
            }
        }*/
    }
}