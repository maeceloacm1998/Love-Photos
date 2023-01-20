package com.br.photoscheme.teste.state

sealed class PreviewPhotoState {
    object Loading : PreviewPhotoState()
    object Error : PreviewPhotoState()
    class Success(val url: String) : PreviewPhotoState()
}