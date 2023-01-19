package com.br.photoscheme.teste.service.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.br.photoscheme.teste.service.dao.PhotoListDAO
import com.br.photoscheme.teste.service.entity.PhotoListEntity

@Database(entities = [PhotoListEntity::class], version = 1)
abstract class PhotoListDB : RoomDatabase() {
    abstract fun photoListDAO(): PhotoListDAO

    companion object {
        private lateinit var INSTANCE: PhotoListDB
        private const val DATABASE_NAME = "photo_list_db"

        fun getDataBase(context: Context): PhotoListDB {
            if (!::INSTANCE.isInitialized) {
                synchronized(PhotoListDB::class.java) {
                    INSTANCE = Room.databaseBuilder(context, PhotoListDB::class.java, DATABASE_NAME)
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }

            return INSTANCE
        }
    }
}