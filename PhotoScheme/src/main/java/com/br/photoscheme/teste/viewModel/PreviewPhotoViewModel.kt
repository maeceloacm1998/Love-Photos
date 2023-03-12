package com.br.photoscheme.teste.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.br.photoscheme.teste.models.FirebaseResponse
import com.br.photoscheme.teste.repository.PhotoSchemeRepository
import com.br.photoscheme.teste.state.PreviewPhotoState
import kotlinx.coroutines.*

class PreviewPhotoViewModel {
    private val repository = PhotoSchemeRepository()

    private var mPhotoUrl = MutableLiveData<PreviewPhotoState>()
    var photoUrl: LiveData<PreviewPhotoState> = mPhotoUrl

    fun fetchSpecificPhoto(photoId: String, child: String) {
        mPhotoUrl.value = PreviewPhotoState.Loading

        val url = repository.getSpecificPhoto(photoId, child, object : FirebaseResponse {
            override fun success(result: Any?) {
                mPhotoUrl.value = PreviewPhotoState.Success(result as String)
            }

            override fun error() {
                mPhotoUrl.value = PreviewPhotoState.Error
            }
        })
    }
}
