package com.br.photoscheme.teste.models

import com.br.photoscheme.teste.service.entity.PhotoListEntity
import com.br.photoscheme.teste.service.entity.ThumbListEntity

data class PhotoItem(
    val id: String,
    val url: String
)

fun PhotoItem.asThumbModel(): ThumbListEntity {
    val photoEntity = ThumbListEntity()
    photoEntity.mId = this.id
    photoEntity.url = this.url

    return photoEntity
}

fun PhotoItem.asPhotoModel(): PhotoListEntity {
    val photoEntity = PhotoListEntity()
    photoEntity.mId = this.id
    photoEntity.url = this.url

    return photoEntity
}