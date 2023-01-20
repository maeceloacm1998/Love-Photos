package com.br.lovephotos

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.br.lovephotos.databinding.ActivityMainBinding
import com.br.photoscheme.teste.view.PhotoSchemeActivity
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(applicationContext)

        val sharedpreferences = getSharedPreferences("onborading", Context.MODE_PRIVATE);
        val onboarding = sharedpreferences.getBoolean("key", false)

        if(onboarding) {
            goToGallery()
        }

        binding.nextBtn.setOnClickListener {
            goToNextStep()
        }
    }

    private fun goToNextStep() {
        val intent = Intent(applicationContext, FirstStep::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun goToGallery() {
        val intent = Intent(applicationContext, PhotoSchemeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}