package com.br.photoscheme.teste.service.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.br.photoscheme.teste.service.entity.ThumbListEntity

@Dao
interface ThumbListDAO {
    @Query("SELECT * FROM thumb_list_table")
    fun getThumbList(): MutableList<ThumbListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createThumbList(timelineItem: List<ThumbListEntity>)

    @Query("DELETE FROM thumb_list_table")
    fun clearThumbList()
}