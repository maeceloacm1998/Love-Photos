package com.br.photoscheme.teste.models.repository

import com.br.photoscheme.teste.models.PhotoItem
import com.google.firebase.storage.StorageReference

interface PhotoSchemeRep {
    suspend fun getPhotoList(): MutableList<StorageReference>
    suspend fun parseStorageReferenceToLink(photos: MutableList<StorageReference>): MutableList<PhotoItem>
    suspend fun updatePhoto(downsizedImageBytes: ByteArray?)
}