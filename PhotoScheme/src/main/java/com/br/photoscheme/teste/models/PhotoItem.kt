package com.br.photoscheme.teste.models

import com.br.photoscheme.teste.service.entity.PhotoListEntity
import com.br.photoscheme.teste.service.entity.ThumbListEntity

data class PhotoItem(
    val id: String,
    val url: String
)

fun List<PhotoItem>.asThumbListModel(): List<ThumbListEntity> {
    return this.map {
        it.asThumbModel()
    }
}

fun PhotoItem.asThumbModel(): ThumbListEntity {
    return ThumbListEntity().also {
        it.mId = id
        it.url = url
    }
}

fun PhotoItem.asPhotoModel(): PhotoListEntity {
    return PhotoListEntity().also {
        it.mId = id
        it.url = url
    }
}