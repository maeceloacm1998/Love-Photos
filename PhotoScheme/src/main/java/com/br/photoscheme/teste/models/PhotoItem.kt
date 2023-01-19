package com.br.photoscheme.teste.models

import com.br.photoscheme.teste.service.entity.PhotoListEntity

data class PhotoItem(
    val id: String,
    val url: String
)

fun PhotoItem.asDomainModel(): PhotoListEntity {
    val photoEntity = PhotoListEntity()
    photoEntity.mId = this.id
    photoEntity.url = this.url

    return photoEntity
}