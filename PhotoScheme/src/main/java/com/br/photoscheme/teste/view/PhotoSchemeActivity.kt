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
import com.br.photoscheme.teste.models.asThumbModel
import com.br.photoscheme.teste.models.contract.PhotoSchemeContract
import com.br.photoscheme.teste.service.database.PhotoListDB
import com.br.photoscheme.teste.service.database.ThumbListDB
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
        PhotoListDB.getDataBase(applicationContext);

        controller()
        observers()
        handleFetchPhotoList()
        handleSwipeRefresh()
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

                    saveThumbListInDB(photoSchemeState.photolist)
                    controller.setPhotosList(newPhotoList)
                    visiblePhotoList()
                }
                is PhotoSchemeState.Loading -> {
                    visibleShimmer()
                }
                is PhotoSchemeState.UpdatePhoto -> {
                    visibleLoadingUpdatePhoto()
                }
                else -> {}
            }
        }
    }

    private fun saveThumbListInDB(photoList: List<PhotoItem>) {
        val dao = ThumbListDB.getDataBase(applicationContext).thumbListDAO()
        val newThumbListDB = photoList.map {
            it.asThumbModel()
        }
        dao.clearThumbList()
        dao.createThumbList(newThumbListDB)
    }

    private fun handleFetchPhotoList() {
        val dao = ThumbListDB.getDataBase(applicationContext).thumbListDAO()
        if (dao.getThumbList().isNullOrEmpty()) {
            viewModel.fetchPhotos(PhotoSchemeConstants.THUMB_PATH)
            return
        }
        viewModel.fetchThumbOfDB(dao)
    }

    private fun handleSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchPhotos(PhotoSchemeConstants.THUMB_PATH)
        }
    }

    var imagePickerActivityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            val downsizedThumb = imageUri?.downsizedImageBytes(applicationContext, null)
            val downsizedPhoto = imageUri?.downsizedImageBytes(applicationContext, 2)
            viewModel.updatePhoto(downsizedThumb, downsizedPhoto)
        }
    }

    val contract = object : PhotoSchemeContract {
        override fun clickUpdatePhotoListener() {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = PhotoSchemeConstants.INTENT_TYPE_GALLERY_ACTIVITY
            imagePickerActivityResult.launch(intent);
        }

        override fun clickPhotoListener(url: String) {
            startActivity(PreviewPhotoActivity.newInstance(applicationContext, url))
        }
    }

    private fun visiblePhotoList() {
        binding.photoSchemeRv.visibility = View.VISIBLE
        binding.photoSchemeShimmerId.shimmer.visibility = View.GONE
    }

    private fun visibleShimmer() {
        binding.swipeRefresh.isRefreshing = false
        binding.photoSchemeRv.visibility = View.GONE
        binding.layoutUpdatePhoto.loading.visibility = View.GONE
        binding.photoSchemeShimmerId.shimmer.visibility = View.VISIBLE
    }

    private fun visibleLoadingUpdatePhoto() {
        binding.swipeRefresh.isRefreshing = false
        binding.photoSchemeRv.visibility = View.GONE
        binding.photoSchemeShimmerId.shimmer.visibility = View.GONE
        binding.layoutUpdatePhoto.loading.visibility = View.VISIBLE
        binding.layoutUpdatePhoto.animationView.speed = 0.3F
    }
}