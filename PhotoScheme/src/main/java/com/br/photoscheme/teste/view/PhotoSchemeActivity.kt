package com.br.photoscheme.teste.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.br.photoscheme.databinding.ActivityPhotoSchemeBinding
import com.br.photoscheme.teste.constants.PhotoSchemeConstants
import com.br.photoscheme.teste.controller.PhotoSchemeController
import com.br.photoscheme.teste.extensions.GridLayoutDecoration
import com.br.photoscheme.teste.extensions.downsizedImageBytes
import com.br.photoscheme.teste.models.PhotoItem
import com.br.photoscheme.teste.models.asThumbModel
import com.br.photoscheme.teste.models.contract.PhotoSchemeContract
import com.br.photoscheme.teste.repository.PhotoSchemeRepository
import com.br.photoscheme.teste.service.database.PhotoListDB
import com.br.photoscheme.teste.service.database.ThumbListDB
import com.br.photoscheme.teste.state.PhotoSchemeState
import com.br.photoscheme.teste.viewModel.PhotoSchemeViewModel

class PhotoSchemeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoSchemeBinding
    private val controller by lazy { PhotoSchemeController(photoSchemeContract) }
    private val viewModel by lazy {
        PhotoSchemeViewModel(
            PhotoSchemeRepository(), ThumbListDB.getDataBase(applicationContext).thumbListDAO()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoSchemeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PhotoListDB.getDataBase(applicationContext);

        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        ActivityCompat.requestPermissions(
            this, listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray(), 122
        )

        photoSchemeController()
        observers()
        handleFetchPhotoList()
        handleSwipeRefresh()
    }

    private fun photoSchemeController() {
        binding.photoSchemeRv.apply {
            setController(controller)
            layoutManager = GridLayoutManager(context, 4)
            val itemDecoration = GridLayoutDecoration(
                applicationContext, com.airbnb.viewmodeladapter.R.dimen.abc_control_padding_material
            )
            addItemDecoration(itemDecoration)
            requestModelBuild()
        }
    }

    private fun observers() {
        viewModel.photoList.observe(this) { photoSchemeState ->
            when (photoSchemeState) {
                is PhotoSchemeState.Success -> {
                    val newPhotoList = photoSchemeState.photolist.toMutableList()
                    includeAddPhotoButton(newPhotoList)
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

    private fun includeAddPhotoButton(photoList: MutableList<PhotoItem>): MutableList<PhotoItem> {
        photoList.add(0, PhotoItem(PhotoSchemeConstants.ID_UPDATE_PHOTO_COMPONENT, ""))
        return photoList
    }

    private fun saveThumbListInDB(photoList: List<PhotoItem>) {
        val newThumbListDB = photoList.map { it.asThumbModel() }
        viewModel.createThumbList(newThumbListDB)
    }

    private fun handleFetchPhotoList() {
        val dao = ThumbListDB.getDataBase(applicationContext).thumbListDAO()

        if (dao.getThumbList().isEmpty()) {
            viewModel.fetchPhotos(PhotoSchemeConstants.THUMB_PATH)
            return
        }
        viewModel.fetchThumbOfDB()
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

    private val photoSchemeContract = object : PhotoSchemeContract {
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

    companion object {
        fun newInstance(context: Context): Intent {
            return Intent(context, PhotoSchemeActivity::class.java)
        }
    }
}