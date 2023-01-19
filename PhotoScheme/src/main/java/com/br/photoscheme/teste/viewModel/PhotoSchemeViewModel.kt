package com.br.photoscheme.teste.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.br.photoscheme.teste.repository.PhotoSchemeRepository
import com.br.photoscheme.teste.service.dao.PhotoListDAO
import com.br.photoscheme.teste.service.entity.asDomainModel
import com.br.photoscheme.teste.state.PhotoSchemeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoSchemeViewModel : ViewModel() {
    private val repository = PhotoSchemeRepository()

    private var mPhotoList = MutableLiveData<PhotoSchemeState>()
    var photoList: LiveData<PhotoSchemeState> = mPhotoList

    fun fetchPhotos() {
        mPhotoList.value = PhotoSchemeState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            val photoList = async { repository.getPhotoList() }.await()

            photoList.let {
                val parseLinks = async { repository.parseStorageReferenceToLink(photoList) }.await()
                withContext(Dispatchers.Main) {
                    mPhotoList.value = PhotoSchemeState.Success(parseLinks)
                }
            }
        }
    }

    fun updatePhoto(downsizedImageBytes: ByteArray?) {
        CoroutineScope(Dispatchers.IO).launch {
            async { repository.updatePhoto(downsizedImageBytes) }.await()

            withContext(Dispatchers.Main) {
                fetchPhotos()
            }
        }
    }

    fun fetchPhotosOfDB(dao: PhotoListDAO) {
        mPhotoList.value = PhotoSchemeState.Loading

        val newPhotoList = dao.getPhotoList().map {
            it.asDomainModel()
        }
        mPhotoList.value = PhotoSchemeState.Success(newPhotoList)
    }
}