package com.autobot.chromium.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.launcher.arclauncher.database.typeconverters.BitmapTypeConverter

@Database(entities = [TabData::class], version = 1, exportSchema = false)
@TypeConverters( BitmapTypeConverter::class)
abstract class ChromiumDataBase : RoomDatabase() {

    companion object {
        fun getInstance(context: Context) =
            Room.databaseBuilder(context, ChromiumDataBase::class.java, "Chromium_database")
                .fallbackToDestructiveMigration()
                .build()
    }

    abstract fun tabDao():TabDao

}