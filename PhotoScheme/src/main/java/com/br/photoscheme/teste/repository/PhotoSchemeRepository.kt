package com.br.photoscheme.teste.repository

import com.br.photoscheme.teste.models.FirebaseResponse
import com.br.photoscheme.teste.models.PhotoItem
import com.br.photoscheme.teste.models.repository.PhotoSchemeRep
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.delay

class PhotoSchemeRepository : PhotoSchemeRep {
    private val storage = Firebase.storage

    override fun getPhotoList(child: String, response: FirebaseResponse) {
        storage.reference.child(child).listAll()
            .addOnSuccessListener { photos ->
                parseStorageReferenceToLink(photos.items, response)
            }
            .addOnFailureListener {
                response.error()
            }
    }

    override fun getSpecificPhoto(photoId: String, child: String, firebaseResponse: FirebaseResponse) {
        storage.reference.child("${child}/${photoId}").downloadUrl.addOnSuccessListener { photo ->
            firebaseResponse.success(photo.toString())
        }.addOnFailureListener { firebaseResponse.error() }
    }

    private fun parseStorageReferenceToLink(photos: MutableList<StorageReference>, firebaseResponse: FirebaseResponse) {
        val list: MutableList<PhotoItem> = mutableListOf()

        photos.forEachIndexed { index, storageReference ->
            storageReference.downloadUrl.addOnSuccessListener {
                if(index < photos.size - 1) {
                    val photo = PhotoItem(index.toString(), it.toString())
                    list.add(photo)
                    return@addOnSuccessListener
                }
                firebaseResponse.success(list)
            }.addOnFailureListener {
                it.printStackTrace()
                firebaseResponse.error()
            }
        }
    }

    override fun updatePhoto(downsizedImageBytes: ByteArray?, path: String, uuid: String, response: FirebaseResponse) {
        val storageReference = FirebaseStorage.getInstance().getReference("/${path}").child(uuid)
        storageReference.putBytes(downsizedImageBytes!!).addOnSuccessListener {
            response.success(null)
        }.addOnFailureListener {
            response.error()
        }
    }
}