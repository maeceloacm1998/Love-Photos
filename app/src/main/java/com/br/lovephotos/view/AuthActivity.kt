package com.br.lovephotos.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.br.lovephotos.databinding.ActivityAuthBinding
import com.br.photoscheme.teste.view.PhotoSchemeActivity

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        showMessageError(false)

        binding.submit.setOnClickListener {
            handleSubmit()
        }

        binding.pin.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleSubmit()
            }
            false
        }

        binding.animationView.speed = 1.4f
    }

    private fun handleSubmit() {
        val pin = binding.pin.text.toString()

        if (pin == "2011") {
            goToGallery()
        } else {
            showMessageError(true)
        }
    }

    private fun showMessageError(error: Boolean) {
        if (error) {
            binding.error.visibility = View.VISIBLE
        } else {
            binding.error.visibility = View.GONE
        }
    }

    private fun goToGallery() {
        val intent = Intent(PhotoSchemeActivity.newInstance(this))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    companion object {
        fun newInstance(context: Context): Intent {
            return Intent(context, AuthActivity::class.java)
        }
    }
}