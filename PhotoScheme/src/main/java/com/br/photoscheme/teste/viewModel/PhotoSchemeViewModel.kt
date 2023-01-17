package com.br.photoscheme.teste.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.br.photoscheme.teste.models.PhotoItem
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.*

class PhotoSchemeViewModel : ViewModel() {
    private val storage = Firebase.storage

    private var mPhotoList = MutableLiveData<List<PhotoItem>>()
    var photoList: LiveData<List<PhotoItem>> = mPhotoList

    fun fetchPhotos() {
        val list: MutableList<PhotoItem> = mutableListOf()

        storage.reference.child("images").listAll().addOnSuccessListener { photos ->
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

    private fun downloadPhotoUrl(photo: StorageReference): String {
        var url = ""
        photo.downloadUrl.addOnSuccessListener {
            url = it.toString()
        }.addOnFailureListener {
            val x = ""
        }
        return url
    }

//    storage.reference.child("images").listAll().addOnSuccessListener {
//        it.items.map {
//            it.downloadUrl.addOnSuccessListener {
//                photosList.add(it.toString())
//                controller.setPhotosList(photosList)
//                controller.requestModelBuild()
//            }.addOnFailureListener {
//                // Handle any errors
//            }
//        }
//    }
}