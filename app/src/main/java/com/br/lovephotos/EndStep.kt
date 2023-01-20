package com.br.lovephotos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.br.lovephotos.databinding.ActivityEndStepBinding
import com.br.photoscheme.teste.view.PhotoSchemeActivity
import com.google.firebase.FirebaseApp


class EndStep : AppCompatActivity() {
    private lateinit var binding: ActivityEndStepBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEndStepBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(applicationContext)
        val sharedpreferences = getSharedPreferences("onborading", Context.MODE_PRIVATE);


        binding.nextBtn.setOnClickListener {
            sharedpreferences.edit().putBoolean("key", true).apply()

            goToGallery()
        }
    }

    private fun goToGallery() {
        val intent = Intent(applicationContext, PhotoSchemeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}