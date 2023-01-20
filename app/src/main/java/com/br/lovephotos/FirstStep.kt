package com.br.lovephotos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.br.lovephotos.databinding.ActivityFirstStepBinding
import com.br.lovephotos.databinding.ActivityMainBinding
import com.br.photoscheme.teste.view.PhotoSchemeActivity
import com.google.firebase.FirebaseApp

class FirstStep : AppCompatActivity() {
    private lateinit var binding: ActivityFirstStepBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstStepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.oneBtn.setOnClickListener {
            it.setBackgroundDrawable(ContextCompat.getDrawable(applicationContext ,R.drawable.bg_error))
        }

        binding.twoBtn.setOnClickListener {
            it.setBackgroundDrawable(ContextCompat.getDrawable(applicationContext ,R.drawable.bg_error))
        }

        binding.nextBtn.setOnClickListener {
            goToNextStep()
        }
    }

    private fun goToNextStep() {
        val intent = Intent(applicationContext, EndStep::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}