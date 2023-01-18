package com.br.photoscheme.teste.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.br.photoscheme.databinding.ActivityPhotoSchemeBinding
import com.br.photoscheme.teste.controller.PhotoSchemeController
import com.br.photoscheme.teste.extensions.downsizedImageBytes
import com.br.photoscheme.teste.models.PhotoItem
import com.br.photoscheme.teste.models.contract.PhotoSchemeContract
import com.br.photoscheme.teste.viewModel.PhotoSchemeViewModel
import com.google.firebase.FirebaseApp

class PhotoSchemeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoSchemeBinding
    private val controller by lazy { PhotoSchemeController(contract) }
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
        viewModel.photoList.observe(this) { photoList ->
            val newPhotoList = photoList.toMutableList()
            newPhotoList.add(0, PhotoItem("create", ""))

            controller.setPhotosList(newPhotoList)
        }
    }

    var imagePickerActivityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            viewModel.updatePhoto(imageUri?.downsizedImageBytes(applicationContext))
        }
    }

    val contract = object : PhotoSchemeContract {
        override fun clickUpdatePhotoListener() {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerActivityResult.launch(intent);
        }

        override fun clickPhotoListener() {}
    }
}