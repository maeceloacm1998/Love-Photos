package com.br.photoscheme.teste.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.br.photoscheme.R
import com.br.photoscheme.databinding.ActivityPhotoSchemeBinding
import com.br.photoscheme.teste.controller.PhotoSchemeController
import com.br.photoscheme.teste.viewModel.PhotoSchemeViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class PhotoSchemeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoSchemeBinding
    private val controller by lazy { PhotoSchemeController() }
    private val viewModel by lazy { PhotoSchemeViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoSchemeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(applicationContext);

        controller()
        observers()
        viewModel.fetchPhotos()
    }

    private fun controller() {
        binding.photoSchemeRv.apply {
            setController(controller)
            layoutManager = GridLayoutManager(context, 3)
            requestModelBuild()
        }
    }
    private fun observers() {
        viewModel.photoList.observe(this) {
            controller.setPhotosList(it.toMutableList())
        }
    }
}