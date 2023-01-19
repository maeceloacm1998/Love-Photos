package com.br.photoscheme.teste.repository

import com.br.photoscheme.teste.constants.PhotoSchemeConstants
import com.br.photoscheme.teste.models.PhotoItem
import com.br.photoscheme.teste.models.repository.PhotoSchemeRep
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.delay
import java.util.UUID

class PhotoSchemeRepository : PhotoSchemeRep {
    private val storage = Firebase.storage

    override suspend fun getPhotoList(): MutableList<StorageReference> {
        var list: MutableList<StorageReference> = mutableListOf()
        storage.reference.child(PhotoSchemeConstants.THUMB_PATH).listAll()
            .addOnSuccessListener { photos ->
                list = photos.items
            }
        delay(3000L)
        return list
    }

    override suspend fun parseStorageReferenceToLink(photos: MutableList<StorageReference>): MutableList<PhotoItem> {
        val list: MutableList<PhotoItem> = mutableListOf()
        photos.forEachIndexed { index, storageReference ->
            storageReference.downloadUrl.addOnSuccessListener {
                val photo = PhotoItem(index.toString(), it.toString())
                list.add(photo)
            }.addOnFailureListener {
                it.printStackTrace()
            }
        }
        delay(5000L)
        return list
    }

    override suspend fun updatePhoto(downsizedImageBytes: ByteArray?) {
        val uuid = UUID.nameUUIDFromBytes(downsizedImageBytes)
        val storageReference =
            FirebaseStorage.getInstance().getReference("/${PhotoSchemeConstants.THUMB_PATH}")
                .child(uuid.toString())
        storageReference.putBytes(downsizedImageBytes!!)
        delay(3000L)
    }
}