package com.br.photoscheme.teste.controller

import com.airbnb.epoxy.EpoxyController
import com.br.photoscheme.teste.holder.gridHolder
import com.br.photoscheme.teste.holder.photoHolder
import com.br.photoscheme.teste.models.PhotoItem

class PhotoSchemeController: EpoxyController() {
    private var photosList: List<PhotoItem> = mutableListOf()

    override fun buildModels() {
        if (photosList.isEmpty()) {
            photoHolder {
                id("photo")
                url("https://cdn.discordapp.com/attachments/759959328726974504/1064671226536132678/indice.jpg")
            }
        } else {
            photosList.forEach { photo ->
                handleImage(photo)
            }
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