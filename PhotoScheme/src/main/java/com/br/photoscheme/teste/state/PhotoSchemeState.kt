package com.br.photoscheme.teste.state

import com.br.photoscheme.teste.models.PhotoItem

sealed class PhotoSchemeState {
    object Loading : PhotoSchemeState()
    object Error : PhotoSchemeState()
    class Success(val photolist: List<PhotoItem>) : PhotoSchemeState()
}