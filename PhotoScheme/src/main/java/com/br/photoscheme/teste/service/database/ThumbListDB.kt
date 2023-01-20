package com.br.photoscheme.teste.service.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.br.photoscheme.teste.service.dao.ThumbListDAO
import com.br.photoscheme.teste.service.entity.ThumbListEntity

@Database(entities = [ThumbListEntity::class], version = 1)
abstract class ThumbListDB : RoomDatabase() {
    abstract fun thumbListDAO(): ThumbListDAO

    companion object {
        private lateinit var INSTANCE: ThumbListDB
        private const val DATABASE_NAME = "thumb_list_db"

        fun getDataBase(context: Context): ThumbListDB {
            if (!::INSTANCE.isInitialized) {
                synchronized(ThumbListDB::class.java) {
                    INSTANCE = Room.databaseBuilder(context, ThumbListDB::class.java, DATABASE_NAME)
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }

            return INSTANCE
        }
    }
}