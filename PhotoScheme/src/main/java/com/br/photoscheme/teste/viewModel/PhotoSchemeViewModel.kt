package com.br.photoscheme.teste.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.br.photoscheme.teste.constants.PhotoSchemeConstants
import com.br.photoscheme.teste.models.FirebaseResponse
import com.br.photoscheme.teste.models.PhotoItem
import com.br.photoscheme.teste.repository.PhotoSchemeRepository
import com.br.photoscheme.teste.service.dao.ThumbListDAO
import com.br.photoscheme.teste.service.entity.ThumbListEntity
import com.br.photoscheme.teste.service.entity.asDomainModel
import com.br.photoscheme.teste.state.PhotoSchemeState
import java.util.UUID

class PhotoSchemeViewModel(
    private val repository: PhotoSchemeRepository,
    private val dao: ThumbListDAO
) : ViewModel() {

    private var mPhotoList = MutableLiveData<PhotoSchemeState>()
    var photoList: LiveData<PhotoSchemeState> = mPhotoList

    fun fetchPhotos(path: String) {
        mPhotoList.value = PhotoSchemeState.Loading

        repository.getPhotoList(path, object : FirebaseResponse {
            override fun success(result: Any?) {
                val photos = result as List<PhotoItem>
                mPhotoList.value = PhotoSchemeState.Success(photos.sortedBy { it.id.toInt() })
            }

            override fun error() {
                TODO("Not yet implemented")
            }
        })
    }

    fun updatePhoto(scaleDividerThumb: ByteArray?, scaleDividerPhoto: ByteArray?) {
        mPhotoList.value = PhotoSchemeState.UpdatePhoto

        val uuid = UUID.nameUUIDFromBytes(scaleDividerThumb).toString()
        repository.updatePhoto(
            scaleDividerThumb,
            PhotoSchemeConstants.THUMB_PATH,
            uuid,
            object : FirebaseResponse {
                override fun success(result: Any?) {}

                override fun error() {}
            }
        )

        repository.updatePhoto(
            scaleDividerPhoto,
            PhotoSchemeConstants.PHOTO_PATH,
            uuid,
            object : FirebaseResponse {
                override fun success(result: Any?) {
                    fetchPhotos(PhotoSchemeConstants.THUMB_PATH)
                }

                override fun error() {

                }
            }
        )
    }

    fun createThumbList(thumbListDB: List<ThumbListEntity>) {
        dao.clearThumbList()
        dao.createThumbList(thumbListDB)
    }

    fun fetchThumbOfDB() {
        mPhotoList.value = PhotoSchemeState.Loading

        val newPhotoList = dao.getThumbList().map {
            it.asDomainModel()
        }
        mPhotoList.value = PhotoSchemeState.Success(newPhotoList)
    }
}