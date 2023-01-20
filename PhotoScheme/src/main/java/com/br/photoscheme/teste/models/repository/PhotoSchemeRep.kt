package com.br.photoscheme.teste.models.repository

import com.br.photoscheme.teste.models.PhotoItem
import com.google.firebase.storage.StorageReference

interface PhotoSchemeRep {
    suspend fun getPhotoList(child: String): MutableList<StorageReference>
    suspend fun getSpecificPhoto(photoId: String, child: String): String
    suspend fun parseStorageReferenceToLink(photos: MutableList<StorageReference>): MutableList<PhotoItem>
    suspend fun updatePhoto(downsizedImageBytes: ByteArray?, path: String, uuid: String)
}