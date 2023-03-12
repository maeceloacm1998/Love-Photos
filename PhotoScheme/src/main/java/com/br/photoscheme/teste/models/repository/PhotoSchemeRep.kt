package com.br.photoscheme.teste.models.repository

import com.br.photoscheme.teste.models.FirebaseResponse

interface PhotoSchemeRep {
    fun getPhotoList(child: String, response: FirebaseResponse)
    fun getSpecificPhoto(photoId: String, child: String, firebaseResponse: FirebaseResponse)
    fun updatePhoto(downsizedImageBytes: ByteArray?, path: String, uuid: String, response: FirebaseResponse)
}