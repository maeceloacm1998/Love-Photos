package com.br.photoscheme.teste.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.br.photoscheme.teste.constants.PhotoSchemeConstants
import com.br.photoscheme.teste.repository.PhotoSchemeRepository
import com.br.photoscheme.teste.service.dao.PhotoListDAO
import com.br.photoscheme.teste.service.dao.ThumbListDAO
import com.br.photoscheme.teste.service.entity.asDomainModel
import com.br.photoscheme.teste.state.PhotoSchemeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class PhotoSchemeViewModel : ViewModel() {
    private val repository = PhotoSchemeRepository()

    private var mPhotoList = MutableLiveData<PhotoSchemeState>()
    var photoList: LiveData<PhotoSchemeState> = mPhotoList

    fun fetchPhotos(path: String) {
        mPhotoList.value = PhotoSchemeState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            val photoList = async { repository.getPhotoList(path) }.await()

            photoList.let {
                val parseLinks = async { repository.parseStorageReferenceToLink(photoList) }.await()
                withContext(Dispatchers.Main) {
                    mPhotoList.value = PhotoSchemeState.Success(parseLinks)
                }
            }
        }
    }

    fun updatePhoto(scaleDividerThumb: ByteArray?, scaleDividerPhoto: ByteArray?) {
        CoroutineScope(Dispatchers.IO).launch {
            val uuid = UUID.nameUUIDFromBytes(scaleDividerThumb).toString()
            async {
                repository.updatePhoto(
                    scaleDividerThumb,
                    PhotoSchemeConstants.THUMB_PATH,
                    uuid
                )
            }.await()
            async {
                repository.updatePhoto(
                    scaleDividerPhoto,
                    PhotoSchemeConstants.PHOTO_PATH,
                    uuid
                )
            }.await()

            withContext(Dispatchers.Main) {
                fetchPhotos(PhotoSchemeConstants.THUMB_PATH)
            }
        }
    }

    fun fetchThumbOfDB(dao: ThumbListDAO) {
        mPhotoList.value = PhotoSchemeState.Loading

        val newPhotoList = dao.getThumbList().map {
            it.asDomainModel()
        }
        mPhotoList.value = PhotoSchemeState.Success(newPhotoList)
    }
}