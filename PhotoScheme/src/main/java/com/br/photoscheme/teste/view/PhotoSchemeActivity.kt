package com.br.photoscheme.teste.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.br.photoscheme.databinding.ActivityPhotoSchemeBinding
import com.br.photoscheme.teste.constants.PhotoSchemeConstants
import com.br.photoscheme.teste.controller.PhotoSchemeController
import com.br.photoscheme.teste.extensions.downsizedImageBytes
import com.br.photoscheme.teste.models.PhotoItem
import com.br.photoscheme.teste.models.asDomainModel
import com.br.photoscheme.teste.models.contract.PhotoSchemeContract
import com.br.photoscheme.teste.service.database.PhotoListDB
import com.br.photoscheme.teste.state.PhotoSchemeState
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
        PhotoListDB.getDataBase(applicationContext);

        controller()
        observers()
        handleFetchPhotoList()
    }

    private fun controller() {
        binding.photoSchemeRv.apply {
            setController(controller)
            layoutManager = GridLayoutManager(context, 3)
            requestModelBuild()
        }
    }

    private fun observers() {
        viewModel.photoList.observe(this) { photoSchemeState ->
            when (photoSchemeState) {
                is PhotoSchemeState.Success -> {
                    val newPhotoList = photoSchemeState.photolist.toMutableList()
                    newPhotoList.add(
                        0,
                        PhotoItem(PhotoSchemeConstants.ID_UPDATE_PHOTO_COMPONENT, "")
                    )

                    savePhotoListInDB(photoSchemeState.photolist)
                    controller.setPhotosList(newPhotoList)
                    visiblePhotoList()
                }
                is PhotoSchemeState.Loading -> {
                    visibleShimmer()
                }
                else -> {}
            }
        }
    }

    private fun savePhotoListInDB(photoList: List<PhotoItem>) {
        val dao = PhotoListDB.getDataBase(applicationContext).photoListDAO()
        val newPhotoListDB = photoList.map {
            it.asDomainModel()
        }
        dao.clearPhotoList()
        dao.createPhotoList(newPhotoListDB)
    }

    private fun handleFetchPhotoList() {
        val dao = PhotoListDB.getDataBase(applicationContext).photoListDAO()
        if (dao.getPhotoList().isNullOrEmpty()) {
            viewModel.fetchPhotos()
            return
        }
        viewModel.fetchPhotosOfDB(dao)
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
            intent.type = PhotoSchemeConstants.INTENT_TYPE_GALLERY_ACTIVITY
            imagePickerActivityResult.launch(intent);
        }

        override fun clickPhotoListener() {
        }
    }

    private fun visiblePhotoList() {
        binding.photoSchemeRv.visibility = View.VISIBLE
        binding.photoSchemeShimmerId.shimmer.visibility = View.GONE
    }

    private fun visibleShimmer() {
        binding.photoSchemeRv.visibility = View.GONE
        binding.photoSchemeShimmerId.shimmer.visibility = View.VISIBLE
    }
}