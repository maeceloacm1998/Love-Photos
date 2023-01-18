package com.br.photoscheme.teste.controller

import com.airbnb.epoxy.EpoxyController
import com.br.photoscheme.teste.holder.gridHolder
import com.br.photoscheme.teste.holder.photoHolder
import com.br.photoscheme.teste.holder.updateNewPhotoHolder
import com.br.photoscheme.teste.models.PhotoItem
import com.br.photoscheme.teste.models.contract.PhotoSchemeContract

class PhotoSchemeController(private val contract: PhotoSchemeContract) : EpoxyController() {
    private var photosList: List<PhotoItem> = mutableListOf()

    override fun buildModels() {
        photosList.forEachIndexed { index, photoItem ->
            if (isFirstItem(index)) {
                handleUpdatePhoto()
            } else {
                handleImage(photoItem)
            }
        }
    }

    private fun isFirstItem(position: Int): Boolean {
        return position == 0
    }

    private fun handleUpdatePhoto() {
        updateNewPhotoHolder {
            id("update_photo")
            clickUpdatePhotoListener(contract::clickUpdatePhotoListener)
        }
    }

    private fun handleImage(photo: PhotoItem) {
        photoHolder {
            id("photo${photo.id}")
            url(photo.url)
        }
    }

    fun setPhotosList(photosList: MutableList<PhotoItem>) {
        this.photosList = photosList
        requestModelBuild()
    }
}