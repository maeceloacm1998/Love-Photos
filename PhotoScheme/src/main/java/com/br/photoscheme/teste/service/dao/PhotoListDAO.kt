package com.br.photoscheme.teste.service.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.br.photoscheme.teste.service.entity.PhotoListEntity

@Dao
interface PhotoListDAO {
    @Query("SELECT * FROM photo_list_table")
    fun getPhotoList(): MutableList<PhotoListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createPhotoList(timelineItem: List<PhotoListEntity>)

    @Query("DELETE FROM photo_list_table")
    fun clearPhotoList()
}