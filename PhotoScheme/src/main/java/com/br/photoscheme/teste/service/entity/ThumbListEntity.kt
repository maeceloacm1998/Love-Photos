package com.br.photoscheme.teste.service.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.br.photoscheme.teste.models.PhotoItem

@Entity(tableName = "thumb_list_table")
class ThumbListEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "mId")
    var mId: String = ""

    @ColumnInfo(name = "url")
    var url: String = ""
}

fun ThumbListEntity.asDomainModel() =
    PhotoItem(
        id = this.mId,
        url = this.url
    )