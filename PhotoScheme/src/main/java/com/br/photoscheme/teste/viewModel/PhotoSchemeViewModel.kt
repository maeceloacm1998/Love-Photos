package com.br.photoscheme.teste.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.br.photoscheme.teste.models.PhotoItem
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.*

class PhotoSchemeViewModel : ViewModel() {
    private val storage = Firebase.storage

    private var mPhotoList = MutableLiveData<List<PhotoItem>>()
    var photoList: LiveData<List<PhotoItem>> = mPhotoList

    fun fetchPhotos() {
        val list: MutableList<PhotoItem> = mutableListOf()

        storage.reference.child("thumb").listAll().addOnSuccessListener { photos ->
            photos.items.forEachIndexed { index, storageReference ->
                storageReference.downloadUrl.addOnSuccessListener {
                    val photo = PhotoItem(index.toString(), it.toString())
                    list.add(photo)

                    if (index == photos.items.size - 1) {
                        mPhotoList.value = list
                    }
                }.addOnFailureListener {
                    val l = ""
                }
            }
        }
    }

    fun updatePhoto(downsizedImageBytes: ByteArray?) {
        val uuid = UUID.nameUUIDFromBytes(downsizedImageBytes)
        val storageReference = FirebaseStorage.getInstance().getReference("/thumb").child(uuid.toString())
        storageReference.putBytes(downsizedImageBytes!!)
    }

    private fun downloadPhotoUrl(photo: StorageReference): String {
        var url = ""
        photo.downloadUrl.addOnSuccessListener {
            url = it.toString()
        }.addOnFailureListener {
            val x = ""
        }
        return url
    }
}