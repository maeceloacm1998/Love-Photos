package com.br.photoscheme.teste.models.repository

import com.br.photoscheme.teste.models.FirebaseResponse

interface PhotoSchemeRep {
    fun getPhotoList(child: String, response: FirebaseResponse)
    suspend fun getSpecificPhoto(photoId: String, child: String): String
    suspend fun updatePhoto(downsizedImageBytes: ByteArray?, path: String, uuid: String)
}