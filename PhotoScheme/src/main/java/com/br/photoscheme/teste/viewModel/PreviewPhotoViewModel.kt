package com.br.photoscheme.teste.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.br.photoscheme.teste.repository.PhotoSchemeRepository
import com.br.photoscheme.teste.state.PreviewPhotoState
import kotlinx.coroutines.*

class PreviewPhotoViewModel {
    private val repository = PhotoSchemeRepository()

    private var mPhotoUrl = MutableLiveData<PreviewPhotoState>()
    var photoUrl: LiveData<PreviewPhotoState> = mPhotoUrl

    fun fetchSpecificPhoto(photoId: String, child: String) {
        mPhotoUrl.value = PreviewPhotoState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            val url = async { repository.getSpecificPhoto(photoId, child) }.await()

            withContext(Dispatchers.Main) {
                if(url.isBlank()) {
                    mPhotoUrl.value = PreviewPhotoState.Error
                    return@withContext
                }

                mPhotoUrl.value = PreviewPhotoState.Success(url)
            }
        }
    }
}
